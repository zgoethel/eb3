package net.jibini.eb.data

/**
 * A collection of fields and field values which represent a discrete object which can be viewed as a document or
 * database entry.  For example, a part in a part database will contain fields and field values for the part name,
 * model number, vendors, and so on.
 *
 * @author Zach Goethel
 */
class Document(
    /**
     * Document schema defining the structure of this document.
     */
    val descriptor: DocumentDescriptor,
) : Map<String, Any?>
{
    /**
     * Internal class; simple implementation for an entry.
     */
    private class Entry(
        override val key: String,
        override val value: Any?
    ) : Map.Entry<String, Any?>

    /**
     * Decorated map which stores unformatted internal values.
     */
    val internal: MutableMap<String, Any?> = HashMap()

    init
    {
        for (field in descriptor.fields)
            internal[field.key] = null
    }

    // Formats the value using the field's format
    override fun get(key: String) = descriptor.fields[key]
        ?.format
        ?.invoke(internal[key])

    // Reprocesses the internal map to use the overridden getter
    override val entries: Set<Map.Entry<String, Any?>>
        get() = internal.entries
            .map { entry -> Entry(entry.key, this[entry.key]) }
            .toSet()

    override val keys: Set<String>
        get() = internal.keys
    override val size: Int
        get() = internal.size

    // Reprocesses the internal map to use the overridden getter
    override val values: Collection<Any?>
        get() = internal.entries
            .map { entry -> this[entry.key] }

    override fun containsKey(key: String) = internal.containsKey(key)

    override fun containsValue(value: Any?) = internal.containsValue(value)

    override fun isEmpty() = internal.isEmpty()
}