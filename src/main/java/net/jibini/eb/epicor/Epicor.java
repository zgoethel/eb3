package net.jibini.eb.epicor;

import net.jibini.eb.EasyButtonConfig;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.epicor.impl.EpicorSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;

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

        //TODO CLASSPATH LOADING
        EpicorSource source = new EpicorSource();

        DocumentDescriptor
            .loadAll(new File(config.getSchemaDirectory()))
            .forEach(source::retrieveFor);
    }
}
