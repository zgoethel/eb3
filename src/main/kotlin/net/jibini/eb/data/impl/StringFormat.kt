package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat

class StringFormat : FieldFormat
{
    override fun invoke(any: Any?) = any?.toString();
}