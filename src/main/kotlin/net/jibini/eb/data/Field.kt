package net.jibini.eb.data

/**
 * Defines a field in a document's schema. This descriptions states the
 * field's name (key) and the type of value held in that field. The
 * formatter object will be loaded from the classpath as configured in
 * the schema files.
 *
 * This field description also facilitates indices to search by a
 * certain field. Indices are also configured in the schema definition
 * files.
 *
 * @author Zach Goethel
 */
class Field(
    /**
     * The name (key) of the field for backend and DB use.
     */
    val name: String,

    /**
     * Plain-English name of this field for UI.
     */
    val title: String,

    /**
     * A formatter which can create the proper output (format and type)
     * of any value this field may hold.
     */
    val format: FieldFormat
)
{
    /**
     * Provides a hash such that a set of fields can be stored and
     * retrieved against their names.
     */
    override fun hashCode(): Int
    {
        // Resort to name's hashing algorithm
        return name.hashCode()
    }

    /**
     * Allows a set of fields to be stored and retrieved against their
     * names.
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