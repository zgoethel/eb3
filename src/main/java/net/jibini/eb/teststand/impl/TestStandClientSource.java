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

    private boolean initialLoad = false;

    private final Set<String> loaded = new HashSet<>();

    @NotNull
    @Override
    public Collection<Document> performIncrementalFor(@NotNull DocumentDescriptor descriptor)
    {
        List<Document> books = new ArrayList<>();

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
            log.info("Found {} workbooks in recursive scan", files.size());
            int created = 0;

            BufferedWriter writer = new BufferedWriter(new FileWriter(testStand.config.getDocumentStore(), true));

            for (File file : files)
            {
                if (loaded.contains(file.getName())) continue;
                loaded.add(file.getName());
                created++;

                Document book = loadDocument(file, descriptor);
                books.add(book);
                writer.write(new JSONObject(book.getInternal()).toString());
                writer.write("\n");
            }

            log.info("Found {} new workbooks in the scan directory", created);

            writer.flush();
            writer.close();
        } catch (IOException ex)
        {
            log.error("Failed to load test stand workbooks from scan", ex);
        }

        return books;
    }

    @NotNull
    private Document loadDocument(File file, DocumentDescriptor descriptor) throws IOException
    {
        Document document = new Document(descriptor);
        Workbook book = WorkbookFactory.create(file);

        document.getInternal().put("file_name", file.getName());

        book.close();

        return document;
    }

    /**
     * Loads already-indexed workbooks from file into memory.
     */
    private void initialLoad(List<Document> books, DocumentDescriptor descriptor)
    {
        try
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
                        if (line.length() == 0) continue;

                        JSONObject entry = new JSONObject(line);

                        Document document = new Document(descriptor);
                        document.getInternal().putAll(entry.toMap());
                        System.out.println(entry.toMap());
                        books.add(document);

                        loaded.add(Objects.requireNonNull(document.get("file_name")).toString());
                    }

                    reader.close();
                } catch (IOException ex)
                {
                    log.error("Failed to load already-scanned test sheets", ex);
                }
            } else
            {
                if (store.getParentFile() != null)
                {
                    if (!store.getParentFile().mkdirs())
                        log.error("Failed to create store parent directories");
                }

                try
                {
                    store.createNewFile();
                } catch (IOException ex)
                {
                    log.error("Failed to create store file", ex);
                }
            }
        } catch (Throwable t)
        {
            t.printStackTrace();
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
