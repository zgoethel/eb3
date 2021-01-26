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
    override fun invoke(any: Any?) = when (any)
    {
        null -> null

        // Attempt to parse; null if invalid
        else -> any.toString().toFloatOrNull()
    }
}