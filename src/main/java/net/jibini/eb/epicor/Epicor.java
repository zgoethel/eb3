package net.jibini.eb.epicor;

import net.jibini.eb.EasyButtonConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
     */
    @PostConstruct
    public void init()
    {
        log.info("Loading Epicor extension configuration settings");
        config = EasyButtonConfig.loadOrDefault(new EpicorConfig());
    }
}
