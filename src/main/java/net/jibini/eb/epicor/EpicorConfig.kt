package net.jibini.eb.epicor

/**
 * Epicor extension configuration for integration with the Epicor API v1
 * backend.
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
}