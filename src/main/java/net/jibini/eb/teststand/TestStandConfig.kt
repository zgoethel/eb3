package net.jibini.eb.teststand

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
     * Whether this node is a client or server.
     */
    val isClient = false

    /**
     * If this node is a client, it requires an address for the server.
     */
    val serverAddress = "http://localhost:8080"

    /**
     * This directory will be recursively scanned for test stand datasheets.
     */
    val scanDirectory = "test_stand"

    /**
     * The scan directory will be scanned on startup, then it will be scanned
     * with delays of this minute value in between.
     */
    val intervalMinutes = 120

    /**
     * Already-indexed documents will be stored in this file.
     */
    val documentStore = "test_stand/store.json"
}