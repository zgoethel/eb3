package net.jibini.eb.teststand

import java.util.UUID

/**
 * Configuration file for settings related to the test-stand datasheet
 * extension. This is configuration is for a customer-specific
 * extension.
 *
 * This is configuration for the test-stand workbook server, and each individual
 * client on the factory floor must be configured to match the server.
 *
 * @author Zach Goethel
 */
class TestStandConfig
{
    /**
     * Working directory schema folder which contains test-stand document
     * schema definitions.
     *
     * Repositories will be created and synchronized for documents
     * defined here.
     */
    val schemaDirectory = "test_stand_schema"

    /**
     * Files in this directory define how to parse test-stand worksheets of
     * different categories and versions.
     */
    val definitionDirectory = "test_stand_definitions"

    /**
     * Floor clients will need to be configured with this secret key in order to
     * submit new and updated workbooks to the test-stand server.
     *
     * Do not post this secret publicly. The default secret key is random.
     */
    val client_secret = UUID.randomUUID()
        .toString()
        .replace("-", "")
}