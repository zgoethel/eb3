package net.jibini.eb.data

import net.jibini.eb.impl.Classpath

/**
 * Processes a field value into its proper format and/or type. Its
 * input is an improperly formatted or null value, and its output is a
 * properly formatted field value as the correct type.
 *
 * Implementing classes must be annotated with [Classpath].
 *
 * @author Zach Goethel
 */
interface FieldFormat
{
    /**
     * @param value Incorrectly formatted or null input value.
     * @return Properly formatted and typed object with the same data, or null
     *      if formatting was impossible.
     */
    fun format(value: Any?): Any?

    /**
     * @param value Incorrectly formatted or null input value.
     * @return Properly formatted string with the same data, or blank if
     *      formatting was impossible.
     */
    fun formatString(value: Any?): String

    /**
     * Filters a provided value (pass/fail) depending on the arguments provided.
     *
     * @param value Document to analyze and pass/fail based on contents.
     * @param args Query arguments for field inputs (encoded).
     * @return Whether this document passes the filter and should be displayed.
     */
    fun filter(value: Any?, fieldName: String, args: MutableMap<String, Array<String>>): Boolean

    /**
     * Path of the JSP file to use as a template for search inputs for fields
     * of this type.
     */
    val searchWidget: String
}