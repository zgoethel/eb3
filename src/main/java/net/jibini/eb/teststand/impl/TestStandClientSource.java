package net.jibini.eb.teststand.impl;

import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.data.impl.AbstractCachedDataSourceImpl;
import net.jibini.eb.impl.EasyButtonContextImpl;
import net.jibini.eb.teststand.TestStand;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.jetbrains.annotations.NotNull;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * The test-stand datasheet parsing implementation. This data source loads new
 * and existing datasheets incrementally from a specified scanning directory.
 *
 * @author Zach Goethel
 */
public class TestStandClientSource extends AbstractCachedDataSourceImpl
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Required to access test-stand configuration
    private final TestStand testStand = EasyButtonContextImpl.getBean(TestStand.class);

    /**
     * Whether this source has loaded at least once. When false, existing pre-
     * loaded documents will be read from the store file.
     */
    private boolean initialLoad = false;

    /**
     * A hash-map of all document file names which are loaded and their SHA
     * hashes. Files will be compared to this hash to determine if changes must
     * be loaded.
     */
    private final Map<String, String> loaded = new HashMap<>();

    /**
     * Load in the workbook definitions for parsing workbooks.
     */
    private final TestStandDefinitionImpl def = new TestStandDefinitionImpl(testStand.config.getDefinitionDirectory());

    @NotNull
    @Override
    public Collection<Document> performIncrementalFor(@NotNull DocumentDescriptor descriptor)
    {
        List<Document> books = new ArrayList<>();

        // Load from store file if not yet loaded
        if (!initialLoad)
        {
            initialLoad(books, descriptor);
            log.info("Loaded {} existing pre-scanned documents", books.size());
            initialLoad = true;
        }

        try
        {
            log.info("Scanning '{}' for new test workbooks", testStand.config.getScanDirectory());
            File scanDirectory = new File(testStand.config.getScanDirectory());

            List<File> files = scanDirectory(scanDirectory);
            log.info("Found {} workbook(s) in recursive scan", files.size());
            int created = 0, unknown = 0;

            BufferedWriter writer = new BufferedWriter(new FileWriter(testStand.config.getDocumentStore(), true));

            for (File file : files)
            {
                try
                {
                    String hash = calculateSHA(file);
                    if (loaded.getOrDefault(file.getName(), "none").equals(hash)) continue;

                    Document book = loadDocument(file, descriptor);

                    if (book != null)
                    {
                        book.getInternal().put("hash_sha", hash);
                        loaded.put(file.getName(), hash);

                        created++;

                        books.add(book);
                        writer.write(new JSONObject(book.getInternal()).toString());
                        writer.write("\n");
                    } else
                        unknown++;
                } catch (Exception ex)
                {
                    log.error("Failed to load a workbook", ex);
                }
            }

            log.info("Found {} new workbook(s) in the scan directory", created);
            log.warn("Found {} workbook(s) with no applicable parsing definitions", unknown);

            writer.flush();
            writer.close();
        } catch (IOException ex)
        {
            log.error("Failed to load test stand workbooks from scan", ex);
        }

        return books;
    }

    /**
     * Loads a specified workbook as a document object. The parser will attempt
     * to find an appropriate test-stand format definition for the workbook.
     *
     * @param file File pointing to the workbook.
     * @param descriptor Descriptor with which to create a document.
     * @return Created document with parsed workbook values; if no parsing
     *      definition can be found for the workbook, the document may be empty
     *      except for the filename.
     * @throws IOException If a read error occurs while parsing.
     */
    private Document loadDocument(File file, DocumentDescriptor descriptor) throws IOException
    {
        Document document = new Document(descriptor);
        Workbook book = WorkbookFactory.create(file, "", true);

        document.getInternal().put("file_name", file.getName());
        boolean success = def.fill(document, book);

        book.close();

        return success ? document : null;
    }

    /**
     * @param file File whose contents to use to calculate a SHA-1 checksum.
     * @return A SHA-1 checksum of the provided file which may be used to
     *      determine if the file has changed from a point A to a point B.
     */
    private String calculateSHA(File file)
    {
        try
        {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");

            try (InputStream input = new FileInputStream(file))
            {
                byte[] buffer = new byte[(int)file.length()];
                int len = input.read(buffer);

                while (len != -1)
                {
                    sha.update(buffer, 0, len);
                    len = input.read(buffer);
                }

                byte[] d = sha.digest();
                sha.reset();

                return new HexBinaryAdapter().marshal(d);
            }
        } catch (IOException | NoSuchAlgorithmException ex)
        {
            log.error("Failed to calculate file SHA representation", ex);

            return "";
        }
    }

    /**
     * Loads already-indexed workbooks from store file into memory.
     */
    private void initialLoad(List<Document> books, DocumentDescriptor descriptor)
    {
        File store = new File(testStand.config.getDocumentStore());

        if (store.exists())
        {
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(store));
                String line;

                while ((line = reader.readLine()) != null)
                {
                    try
                    {
                        if (line.length() == 0) continue;

                        JSONObject entry = new JSONObject(line);

                        Document document = new Document(descriptor);
                        document.getInternal().putAll(entry.toMap());
                        books.add(document);

                        loaded.put(
                            Objects.requireNonNull(document.get("file_name")).toString(),
                            Objects.requireNonNull(document.get("hash_sha")).toString()
                        );
                    } catch (Exception ex)
                    {
                        log.error("Failed to load a store file line", ex);
                    }
                }

                reader.close();
            } catch (IOException ex)
            {
                log.error("Failed to load already-scanned test sheets", ex);
            }
        } else
        {
            if (store.getParentFile() != null)
                store.getParentFile().mkdirs();

            try
            {
                store.createNewFile();
            } catch (IOException ex)
            {
                log.error("Failed to create store file", ex);
            }
        }
    }

    /**
     * Recursively scans the provided directory for Excel workbooks.
     *
     * @param directory Base directory to scan for workbooks.
     * @return A list of all workbooks discovered in all subdirectories.
     * @throws IOException If a scan or read error occurs.
     */
    private List<File> scanDirectory(File directory) throws IOException
    {
        if (!directory.isDirectory())
            throw new IllegalStateException("Provided scanning directory must be a directory");

        List<File> files = new ArrayList<>();
        scanDirectory(directory, files);

        return files;
    }

    /**
     * Recursively scans the provided directory for Excel workbooks (recursive
     * implementation method).
     *
     * @param directory Base directory to scan for workbooks.
     * @param output Reference to a list of all workbooks discovered so far, to
     *      which newly discovered workbooks will be added.
     */
    private void scanDirectory(File directory, List<File> output)
    {
        for (File f : Objects.requireNonNull(directory.listFiles()))
        {
            if (f.isDirectory())
                scanDirectory(f, output);
            else if (f.getName().endsWith(".xlsx"))
                output.add(f);
        }
    }
}
