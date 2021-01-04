package net.jibini.eb.data

/**
 * Contains a collection of fields which make up a certain type
 * of document, such as the document's name and its data values.
 *
 * @author Zach Goethel
 */
class DocumentDescriptor(
    /**
     * This document schema's name; all documents of a given
     * type should have the same name.
     */
    val name: String
)
{
    /**
     * A map of fields for this document, hashed against their name.
     */
    val fields: Map<String, Field> = HashMap()

    /**
     * @param field Field to add to this document's schema.
     */
    fun add(field: Field)
    {
        (fields as MutableMap)[field.name] = field
    }
}