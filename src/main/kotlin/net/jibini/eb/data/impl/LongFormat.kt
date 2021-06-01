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
}