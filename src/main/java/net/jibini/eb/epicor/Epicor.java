package net.jibini.eb.epicor;

import net.jibini.eb.EasyButtonConfig;
import net.jibini.eb.data.DataSource;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.epicor.impl.EpicorSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Epicor extension main class for configuration and initialization.
 *
 * @author Zach Goethel
 */
@Component
public class Epicor
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The Epicor configuration file, which is loaded upon startup. If no
     * configuration existed prior to startup, a default configuration will be
     * created and returned.
     *
     * @see EasyButtonConfig
     */
    public EpicorConfig config;

    /**
     * This plugin's data source, which in this case points to Epicor.
     */
    public DataSource source;

    /**
     * This plugin's loaded document descriptors. Documents described here will
     * be incrementally loaded on a scheduled update interval.
     */
    public Collection<DocumentDescriptor> descriptors;

    /**
     * Epicor initialization. Loads the Epicor configuration.
     *
     * Initializes the Epicor document schemas and creates the initial caches of
     * document data. Schedules the interval incremental updates of caches.
     */
    @PostConstruct
    public void init()
    {
        log.info("Loading Epicor extension configuration settings");
        config = EasyButtonConfig.loadOrDefault(new EpicorConfig());

        // Load the vendor plugin schemas from the assets
        descriptors = DocumentDescriptor.loadAll(new File(config.getSchemaDirectory()));

        //TODO CLASSPATH LOADING
        source = new EpicorSource();
        // Schedule the loader update at the specified minute interval
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::loaderTask, 0L, config.getRefreshInterval(), TimeUnit.SECONDS);
    }

    /**
     * This task is called at a scheduled rate to update the local caches.
     */
    private void loaderTask()
    {
        descriptors.forEach(source::retrieveIncrementalFor);
        log.info("Scheduled loader update is complete");
    }
}
