package net.jibini.eb.epicor

/**
 * Epicor extension configuration for integration with the Epicor API v1
 * backend. Allows configuration of the connection to the Epicor backend
 * and how data is loaded for search indices and aggregation.
 *
 * @author Zach Goethel
 */
class EpicorConfig
{
    /**
     * Server path leading to the Epicor backend server. Must be API v1.
     * Must be HTTPS.
     *
     * Non-conformance will result in errors.
     */
    val baseServerPath = "https://[epicor_path]/epicorerp/api/v1"

    /**
     * Credential username for sync with Epicor.
     */
    val username = "manager"

    /**
     * Credential password for sync with Epicor.
     */
    val password = "manager"

    /**
     * Incremental cache sync interval (in seconds). Repositories will
     * be updated incrementally from Epicor with this interval between
     * syncs.
     */
    val refreshInterval = 60

    /**
     * Working directory schema folder which contains Epicor document
     * schema definitions.
     *
     * Repositories will be created and synchronized for documents
     * defined here.
     */
    val schemaDirectory = "epicor_schema"

    /**
     * Whether the Epicor extension should be loaded and enabled at runtime.
     */
    val enabled = false
}