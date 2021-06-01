package net.jibini.eb.teststand.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.data.impl.StringFormat
import net.jibini.eb.impl.Classpath

/**
 * String formatter implementation for upper-case string fields.
 *
 * @author Zach Goethel
 */
@Classpath
class ToUpperStringFormat : FieldFormat by StringFormat()
{
    override fun format(value: Any?) = value?.toString()?.toUpperCase();

    override fun formatString(value: Any?) = format(value) ?: "-"
}