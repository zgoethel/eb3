package net.jibini.eb.teststand;

import net.jibini.eb.EasyButtonConfig;
import net.jibini.eb.data.DataSource;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.teststand.impl.TestStandClientSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
     * This plugin's data source, which in this case points to either the
     * discovered contents of a scan directory or a server-side cache of node's
     * reported documents.
     */
    public DataSource source;

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
            source = new TestStandClientSource();
            // Schedule the loader update at the specified minute interval
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            final ScheduledFuture<?> future = executor.scheduleAtFixedRate(this::loaderTask, 0L, config.getIntervalMinutes(), TimeUnit.MINUTES);

            new Thread(() ->
            {
                while (true)
                    try
                    {
                        future.get();
                    } catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
            }).start();
        }
    }

    /**
     * This task is called at a scheduled rate to update the local caches.
     */
    private void loaderTask()
    {
        source.retrieveIncrementalFor(DocumentDescriptor.forName("TEST_STAND_SHEET"));
        log.info("Scheduled loader update is complete");
    }
}
