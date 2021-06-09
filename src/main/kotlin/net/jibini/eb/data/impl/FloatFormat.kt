package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

/**
 * Boolean formatter implementation for true/false fields.
 *
 * @author Zach Goethel
 */
@Classpath
class FloatFormat : FieldFormat
{
    override fun format(value: Any?) = when (value)
    {
        null -> null

        // Attempt to parse; null if invalid
        else -> value.toString().toFloatOrNull()
    }

    override fun formatString(value: Any?) = format(value)?.toString() ?: "-"

    override fun filter(value: Any?, fieldName: String, args: MutableMap<String, Array<String>>): Boolean
    {
        val min = (args["_${fieldName}_min"] ?: arrayOf(""))[0].toFloatOrNull()
        val max = (args["_${fieldName}_max"] ?: arrayOf(""))[0].toFloatOrNull()

        return (min == null || (format(value) as Float) >= min)
            && (max == null || (format(value) as Float) <= max)
    }

    override val searchWidget = "search/float"
}