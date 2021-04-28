package net.jibini.eb.data

import net.jibini.eb.epicor.impl.EpicorSource
import net.jibini.eb.impl.ClasspathAnnotationImpl

import org.json.JSONObject

import java.io.File
import java.lang.IllegalStateException

/**
 * Contains a collection of fields which make up a certain type of
 * document, such as the document's name and its data values. All
 * documents stored in a single relational database table should share
 * this common schema.
 *
 * This descriptor provides a list of fields and how they should be
 * formatted. The description of a document's schema allows querying
 * of data sources for the fields necessary to create new documents.
 * For example, see the [EpicorSource] implementation and its info
 * about how descriptors are used to query OData APIs.
 *
 * These descriptors should be stored in a directory called "schemas"
 * in the root working directory. Descriptors are loaded from this
 * directory as JSON files describing document types and fields.
 * Upon loading these descriptors, repositories are created to store
 * caches of documents. This allows quicker access to data to join
 * together assembled documents (accessing memory vs. accessing an
 * API). This is a replacement to the usage of Business Activity
 * Queries in EasyButton 2.
 *
 * @author Zach Goethel
 */
class DocumentDescriptor(
    /**
     * This document schema's unique name; all documents of a given type
     * should share the same name.
     *
     * Document caches will be kept against this name.
     */
    val name: String,

    /**
     * A plain-English representation of this document name for UI.
     */
    val title: String,

    /**
     * This document's primary key. Each instance of a document with
     * this schema will be hashed against the string value of the value
     * associated with this key.
     *
     * For example, set to "PartNum" for parts. Part documents are then
     * indexed against their part numbers.
     */
    val primaryKey: String,

    /**
     * The system revision ID, which is Epicor's representation of the
     * SQL sequential exchange counter. For a table called "Part," this
     * should be "Part_SysRevID."
     */
    val trackIndex: String
)
{
    /**
     * A map of fields for this document, hashed against their name. A
     * [Document] created with this descriptor must contain values for
     * all fields listed here.
     *
     * Each field defines its name, where it's from, and how it should
     * be formatted. Certain fields will define indices which allow
     * searching of documents by the defined field. For example, a
     * string may define a substring index or an alphabetized binary
     * search index.
     */
    val fields: Map<String, Field> = HashMap()

    /**
     * Adds the provided field to the document schema. This will change
     * the document definition of all documents created with this
     * descriptor. Behavior of existing documents at the time this is
     * changed is undefined. Changing the document descriptor while
     * documents are actively using it may result in unexpected behavior.
     *
     * This is primarily used to load fields from JSON descriptor files
     * and add the fields to the schema. This is performed at startup
     * before any documents exist.
     *
     * @param field Field to add to this document's schema. It should
     *      define the field, its name, indices built on this field, and
     *      how to present it to the end user.
     */
    fun add(field: Field)
    {
        (fields as MutableMap)[field.name] = field
    }

    companion object
    {
        /**
         * Map containing all document descriptors which have been
         * loaded, hashed against their names.
         */
        private val loaded = mutableMapOf<String, DocumentDescriptor>()

        /**
         * Retrieves the descriptor with the given name, assuming one
         * has been loaded.
         *
         * @throws NullPointerException If no descriptor with the given
         *     name has been loaded.
         * @return The loaded document descriptor with the given name.
         */
        @JvmStatic
        fun forName(name: String) = loaded[name]!!

        /**
         * Loads the given JSON document descriptor. The provided file
         * should be a JSON file containing the schema name, source, and
         * fields defined for the schema. Each field should define a
         * name, format, source, and search index if applicable.
         *
         * Documents may be sourced from a backend server, such as
         * Epicor, or assembled from other sources which are cached in
         * memory.
         *
         * @param file JSON file containing the schema definition data.
         *     Must be a JSON file.
         * @return Loaded document descriptor.
         */
        @JvmStatic
        fun load(file: File): DocumentDescriptor
        {
            if (!file.isFile || !file.name.toLowerCase().endsWith(".json"))
                throw IllegalStateException("Provided file is not a valid file")

            val json = JSONObject(file.readText())
            val descriptor = DocumentDescriptor(
                json.getString("name"),
                json.getString("title"),
                json.getString("primaryKey"),
                json.getString("trackIndex")
            )

            json.getJSONArray("fields")
                .forEach {
                    if (it !is JSONObject)
                        throw IllegalStateException("Malformed fields list in document descriptor")

                    descriptor.add(Field(
                        it.getString("name"),
                        ClasspathAnnotationImpl.findAndCreate(it.getString("format"))
                    ))
                }

            loaded[descriptor.name] = descriptor

            return descriptor
        }

        /**
         * Loads all JSON document descriptors in the provided
         * directory. Does not recurse. This is useful for loading all
         * descriptors in a configuration folder.
         *
         * The provided folder should contain JSON files defining schema
         * names, sources, and fields defined for each schema. Each
         * field should define a name, format, source, and search index
         * if applicable.
         *
         * Documents may be sourced from a backend server, such as
         * Epicor, or assembled from other sources which are cached in
         * memory.
         *
         * @param directory Directory containing document descriptors.
         *     All files ending in ".json" will be loaded using [load].
         * @return Collection of loaded document descriptors.
         */
        @JvmStatic
        fun loadAll(directory: File): Collection<DocumentDescriptor>
        {
            if (!directory.isDirectory)
                throw IllegalStateException("Provided file is not a directory")

            return directory
                .listFiles { _, name -> name.toLowerCase().endsWith(".json") }!!
                .map { load(it) }
        }
    }
}