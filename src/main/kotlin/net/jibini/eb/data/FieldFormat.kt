package net.jibini.eb.data

/**
 * Processes a field value into its proper format and/or type.
 *
 * @author Zach Goethel
 */
@FunctionalInterface
interface FieldFormat
{
    /**
     * @param any An improperly formatted or null value.
     *
     * @return The properly formatted field value as the correct type.
     */
    fun format(any: Any?): Any?
}