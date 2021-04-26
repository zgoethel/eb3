package net.jibini.eb.teststand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import net.jibini.eb.EasyButtonConfig;
import net.jibini.eb.data.DocumentDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * Test-stand extension main class for configuration and initialization.
 *
 * @author Zach Goethel
 */
@Component
public class TestStand
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The test-stand extension's configuration file, which is loaded upon
     * startup. If no configuration existed prior to startup, a default
     * configuration will be created and returned.
     *
     * @see TestStandConfig
     */
    public TestStandConfig config;

    /**
     * Test-stand extension initialization. Loads the test-stand configuration.
     *
     * Initializes the test-stand document schemas and prepares to accept new
     * document submissions or updates from floor clients.
     */
    @PostConstruct
    public void init()
    {
        log.info("Loading test-stand extension configuration settings");
        config = EasyButtonConfig.loadOrDefault(new TestStandConfig());

        // Load the vendor plugin schemas from the assets
        DocumentDescriptor.loadAll(new File(config.getSchemaDirectory()));

        if (config.isClient())
        {
            // Schedule the loader update at the specified minute interval
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this::loaderTask, 0L, config.getIntervalMinutes(), TimeUnit.MINUTES);
        }
    }

    /**
     * This task is called at a scheduled rate to update the local caches.
     */
    private void loaderTask()
    {
        try
        {
            log.info("Scanning '{}' for new test workbooks", config.getScanDirectory());
            File scanDirectory = new File(config.getScanDirectory());

            List<File> files = scanDirectory(scanDirectory);
            log.info("Found {} workbooks in recursive scan", files.size());
        } catch (IOException ex)
        {
            log.error("Failed to load test stand workbooks from scan", ex);
        }

        log.info("Scheduled loader update is complete");
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
