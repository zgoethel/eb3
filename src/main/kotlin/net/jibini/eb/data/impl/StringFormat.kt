package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat

/**
 * String formatter implementation for string fields.
 *
 * @author Zach Goethel
 */
class StringFormat : FieldFormat
{
    override fun invoke(any: Any?) = any?.toString();
}