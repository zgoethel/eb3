package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

/**
 * Boolean formatter implementation for true/false fields.
 *
 * @author Zach Goethel
 */
@Classpath
class BooleanFormat : FieldFormat
{
    override fun format(value: Any?) = when (value)
    {
        true, "T", "true" -> true
        null -> null

        else -> false
    }

    override fun formatString(value: Any?) = format(value)?.toString()?.capitalize() ?: "-"
}