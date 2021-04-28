package net.jibini.eb.teststand.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

/**
 * String formatter implementation for upper-case string fields.
 *
 * @author Zach Goethel
 */
@Classpath
class ToUpperStringFormat : FieldFormat
{
    override fun invoke(any: Any?) = any?.toString()?.toUpperCase();
}