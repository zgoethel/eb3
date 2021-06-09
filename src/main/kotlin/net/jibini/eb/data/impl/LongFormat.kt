package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

/**
 * 64-bit integer formatter implementation for integer fields.
 *
 * @author Zach Goethel
 */
@Classpath
class LongFormat : FieldFormat
{
    override fun format(value: Any?) = when (value)
    {
        null -> null

        // Attempt to parse; null if invalid
        else -> value.toString().toLongOrNull()
    }

    override fun formatString(value: Any?) = format(value)?.toString() ?: "-"

    override fun filter(value: Any?, fieldName: String, args: MutableMap<String, Array<String>>): Boolean
    {
        val min = (args["_${fieldName}_min"] ?: arrayOf(""))[0].toLongOrNull()
        val max = (args["_${fieldName}_max"] ?: arrayOf(""))[0].toLongOrNull()

        return (min == null || (format(value) as Long) >= min)
            && (max == null || (format(value) as Long) <= max)
    }

    override val searchWidget = "search/long"
}