package net.jibini.eb.teststand.impl

import net.jibini.eb.data.FieldFormat
import net.jibini.eb.impl.Classpath

import java.lang.IllegalArgumentException

/**
 * String formatter implementation for string fields.
 *
 * @author Zach Goethel
 */
@Classpath
class RemoveDecimalFormat : FieldFormat
{
    override fun format(value: Any?) = value?.toString()

    override fun formatString(value: Any?) = (if ((format(value) ?: "-").matches(Regex("[0-9]+\\.[0.9]+")))
        (format(value) ?: "-").replace(Regex("\\.[0-9]"), "")
    else
        format(value) ?: "-").toUpperCase()

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

    override val searchWidget = "search/string"
}