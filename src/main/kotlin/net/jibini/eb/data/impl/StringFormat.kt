package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

/**
 * String formatter implementation for string fields.
 *
 * @author Zach Goethel
 */
@Classpath
class StringFormat : FieldFormat
{
    override fun invoke(any: Any?) = any?.toString();
}