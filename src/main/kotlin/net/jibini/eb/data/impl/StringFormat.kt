package net.jibini.eb.data.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

import java.lang.IllegalArgumentException

/**
 * String formatter implementation for string fields.
 *
 * @author Zach Goethel
 */
@Classpath
class StringFormat : FieldFormat
{
    override fun format(value: Any?) = value?.toString()

    override fun formatString(value: Any?) = format(value) ?: "-"

    override fun filter(value: Any?, fieldName: String, args: MutableMap<String, Array<String>>): Boolean
    {
        val method = (args["_${fieldName}_method"] ?: arrayOf(""))[0]
        val regex = (args["_${fieldName}_regex"] ?: arrayOf(""))[0]

        if (regex.isEmpty()) return true

        return when (method)
        {
            "Contains" -> formatString(value).toLowerCase().contains(Regex(regex.toLowerCase()))
            "Matches" ->formatString(value).toLowerCase().matches(Regex(regex.toLowerCase()))
            ">=" -> formatString(value) >= regex
            "<=" -> formatString(value) <= regex

            else -> throw IllegalArgumentException("Unrecognized filter method '$method'")
        }
    }
}