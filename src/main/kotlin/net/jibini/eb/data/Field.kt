package net.jibini.eb.data

/**
 * Defines a field in a document's schema.  This descriptions states the field's
 * name (key) and the type of value held in that field.
 *
 * This field description also facilitates indices to search by a certain field.
 *
 * @author Zach Goethel
 */
class Field(
    /**
     * The name (key) of the field.
     */
    val name: String,

    /**
     * A formatter which can create the proper JSON-ified output of any value
     * this field may hold.
     */
    val format: FieldFormat
)
{
    /**
     * Provides a hash such that a set of fields can be stored and retrieved
     * against their names.
     */
    override fun hashCode(): Int
    {
        // Resort to name's hashing algorithm
        return name.hashCode()
    }

    /**
     * Allows a set of fields to be stored and retrieved against their names.
     *
     * @return If the field provided has the same name as this one.
     */
    override fun equals(other: Any?): Boolean
    {
        if (other == null || other !is Field)
            return false
        return other.name == name
    }
}