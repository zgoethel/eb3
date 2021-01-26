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
    override fun invoke(any: Any?) = when (any)
    {
        true, "true" -> true
        null -> null

        else -> false
    }
}