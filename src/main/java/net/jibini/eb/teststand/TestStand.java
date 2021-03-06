package net.jibini.eb.teststand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import net.jibini.eb.EasyButtonConfig;
import net.jibini.eb.data.DocumentDescriptor;

import java.io.File;

import javax.annotation.PostConstruct;

@Component
public class TestStand
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The test-stand extension's configuration file, which is loaded upon startup. If no
     * configuration existed prior to startup, a default configuration will be
     * created and returned.
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

        DocumentDescriptor.loadAll(new File(config.getSchemaDirectory()));
    }
}
