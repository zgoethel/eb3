package net.jibini.eb.teststand.impl;

import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.data.impl.AbstractCachedDataSourceImpl;
import net.jibini.eb.data.impl.DocumentSubmissionImpl;
import net.jibini.eb.impl.EasyButtonContextImpl;
import net.jibini.eb.teststand.TestStand;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    // Required to submit documents to server
    private final DocumentSubmissionImpl submit = EasyButtonContextImpl.getBean(DocumentSubmissionImpl.class);

    /**
     * Whether this source has loaded at least once. When false, existing pre-
     * loaded documents will be read from the store file.
     */
    private boolean initialLoad = false;

    /**
     * Contains data stored on disk to persist entries through restarts.
     */
    private StoreFile storeFile;

    /**
     * A hash-map of all document file names which are loaded and their last-
     * modified dates. Files will be compared to this time to determine if file
     * changes must be loaded.
     */
    private final Map<String, Long> loaded = new HashMap<>();

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
            storeFile = new StoreFile(DocumentDescriptor.forName("TEST_STAND_SHEET"), new File(testStand.config.getDocumentStore()));

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

            for (File file : files)
            {
                try
                {
                    long lastModified = file.lastModified();
                    if (loaded.getOrDefault(file.getName(), -1L) >= lastModified) continue;

                    Document book = loadDocument(file, descriptor);

                    if (book != null)
                    {
                        book.getInternal().put("last_modified", lastModified);
                        loaded.put(file.getName(), lastModified);

                        created++;

                        books.add(book);
                        storeFile.write(book);

                        submit.sendDocument(
                            String.format("%s/document/%s", testStand.config.getServerAddress(), descriptor.getName()),
                            book);
                    } else
                        unknown++;
                } catch (Exception ex)
                {
                    log.error("Failed to load a workbook", ex);
                }
            }

            log.info("Found {} new workbook(s) in the scan directory", created);
            log.warn("Found {} workbook(s) with no applicable parsing definitions", unknown);
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
     * Loads already-indexed workbooks from store file into memory.
     */
    private void initialLoad(List<Document> books, DocumentDescriptor descriptor)
    {
        Collection<Document> store = storeFile.loadAll();

        for (Document document : store)
        {
            try
            {
                books.add(document);

                loaded.put(
                    Objects.requireNonNull(document.get("file_name")).toString(),
                    (Long)document.get("last_modified")
                );

                submit.sendDocument(
                    String.format("%s/document/%s", testStand.config.getServerAddress(), descriptor.getName()),
                    document);
            } catch (Exception ex)
            {
                log.error("Failed to load a store file line", ex);
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
