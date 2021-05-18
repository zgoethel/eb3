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
}