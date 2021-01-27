package net.jibini.eb.data

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A collection of fields and field values which represent a discrete
 * object which can be viewed as a document or database entry. For
 * example, a part in a part database will contain fields and field
 * values for the part name, model number, vendors, and so on.
 *
 * The document is initialized to all null-pointers upon creation.
 * Each field value should be added, which can be achieved by adding
 * to the [internal map][internal].
 *
 * Field values retrieved from this document will be formatted using
 * the formatter defined in the document descriptor field configuration.
 * If a value is improperly formatted, it will likely cause an
 * exception in the delegated formatter. Failed formatter results will
 * result in a value of null.
 *
 * The document's [descriptor] must be provided upon document creation
 * in order to properly format field values for retrieval. Attempting
 * to access a field value not defined in the provided descriptor will
 * result in a value of null.
 *
 * A document instance is a decorated hash-map, thus an instance's
 * entries may be iterated over like a normal map. Entries will all be
 * formatted according to the descriptor format configuration.
 *
 * @author Zach Goethel
 */
class Document(
    /**
     * Document schema defining the structure of this document. Field
     * values retrieved from this document will be formatted using the
     * formatter defined in the document descriptor field configuration.
     *
     * The document's descriptor must be provided upon document
     * creation in order to properly format field values for retrieval.
     * Attempting to access a field value not defined in the provided
     * descriptor will result in a value of null.
     */
    val descriptor: DocumentDescriptor,
) : Map<String, Any?>
{
    // Testing out this pattern
    private companion object Log : Logger by LoggerFactory.getLogger(Document::class.java)

    /**
     * Internal class; simple implementation for an entry. Used for
     * decoration and iteration.
     */
    private class Entry(
        override val key: String,
        override val value: Any?
    ) : Map.Entry<String, Any?>

    /**
     * Decorated map which stores unformatted internal values. Values
     * written to and read from this map may be incorrectly formatted.
     *
     * The document is initialized to all null-pointers upon creation.
     * Each field value should be added, which can be achieved by adding
     * to this internal map.
     *
     * Writing to this map is required. Reading is discouraged. Instead,
     * use the decorated [get] to correctly format values.
     */
    val internal: MutableMap<String, Any?> = HashMap()

    init
    {
        // Initialize values to null-pointers; iteration over the
        // document will then include unset document fields
        for (field in descriptor.fields)
            internal[field.key] = null
    }

    // Formats the value using the field's format
    override fun get(key: String) = try
    {
        descriptor.fields[key]
            ?.format
            ?.invoke(internal[key])
    } catch (ex: Exception)
    {
        error("Failed to format field '$key' with exception", ex)

        null
    }

    // Reprocesses the internal map to use the overridden getter
    override val entries: Set<Map.Entry<String, Any?>>
        get() = internal.entries
            .map { entry -> Entry(entry.key, this[entry.key]) }
            .toSet()

    // Reprocesses the internal map to use the overridden getter
    override val values: Collection<Any?>
        get() = internal.entries.map { entry -> this[entry.key] }

    // Directly delegate values to internal instance
    override val keys: Set<String>
        get() = internal.keys
    override val size: Int
        get() = internal.size

    // Directly delegate functions to internal instance
    override fun containsKey(key: String) = internal.containsKey(key)
    override fun containsValue(value: Any?) = internal.containsValue(value)
    override fun isEmpty() = internal.isEmpty()
}