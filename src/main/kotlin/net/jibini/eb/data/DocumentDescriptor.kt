package net.jibini.eb.data

import net.jibini.eb.data.impl.StringFormat
import org.json.JSONObject

import java.io.File
import java.lang.IllegalStateException

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

    companion object
    {
        /**
         * Loads the given JSON document descriptor.
         *
         * @return Loaded document descriptor.
         */
        @JvmStatic
        fun load(file: File): DocumentDescriptor
        {
            if (!file.isFile || !file.name.toLowerCase().endsWith(".json"))
                throw IllegalStateException("Provided file is not a valid file")

            val json = JSONObject(file.readText())
            val descriptor = DocumentDescriptor(json.getString("name"))

            json.getJSONArray("fields")
                .forEach {
                    if (it !is JSONObject)
                        throw IllegalStateException("Malformed fields list in document descriptor")

                    //TODO CLASSPATH SCAN FOR FORMATS
                    val format = StringFormat()

                    descriptor.add(Field(it.getString("name"), format))
                }

            return descriptor
        }

        /**
         * Loads all JSON document descriptors in the provided directory.  Does
         * not recurse.
         *
         * @param directory Directory containing document descriptors.
         *
         * @return Collection of loaded document descriptors.
         */
        @JvmStatic
        fun loadAll(directory: File): Collection<DocumentDescriptor>
        {
            if (!directory.isDirectory)
                throw IllegalStateException("Provided file is not a directory")

            val children = directory.listFiles { _, name -> name.toLowerCase().endsWith(".json") }!!

            return children.map { load(it) }
        }
    }
}