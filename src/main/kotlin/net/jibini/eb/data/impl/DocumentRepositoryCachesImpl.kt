package net.jibini.eb.data.impl

import net.jibini.eb.data.Document
import net.jibini.eb.data.DocumentDescriptor

import java.util.concurrent.ConcurrentHashMap

/**
 * A collection of repositories associated with each [document
 * descriptor][DocumentDescriptor] which has been created.
 *
 * Each repository is a hashed map linking a document's name to its
 * associated values. This allows efficient joining of document data
 * when assembling reports. An document or report assembly service can
 * easily look up a related document by its name in a hash table.
 *
 * Each type of document should have exactly one active repository.
 * Each document should have a unique value in its primary key, and no
 * two documents should share a common primary key. Two documents of
 * two different types may have the same primary key.
 *
 * Access to repository maps and the mapped documents within are
 * synchronized and thread-safe.
 *
 * @author Zach Goethel
 */
object DocumentRepositoryCachesImpl
{
    /**
     * A thread-safe map of maps, first mapping a document descriptor
     * name to its mapped documents, then each document's name to its
     * document instance.
     */
    private val collections = ConcurrentHashMap<String, ConcurrentHashMap<String, Document>>()

    /**
     * Gets or creates the repository map for the given [document
     * descriptor][DocumentDescriptor] name.
     *
     * @param name The relevant document descriptor's name. The document
     *     repository will be hashed and retrievable against this name.
     * @returns The retrieved or created mapped document repository.
     */
    @JvmStatic
    fun get(name: String): MutableMap<String, Document> = collections
        .getOrPut(name) { ConcurrentHashMap() }

    /**
     * Gets or creates the repository map for the given [document
     * descriptor][DocumentDescriptor].
     *
     * @param descriptor The relevant document descriptor. The document
     *     repository will be hashed and retrievable against this
     *     descriptor's name.
     * @returns The retrieved or created mapped document repository.
     */
    @JvmStatic
    fun get(descriptor: DocumentDescriptor) = get(descriptor.name)

    /**
     * Places the given document in the correct repository. This is a
     * shorthand convenience method for first retrieving the document's
     * descriptor's repository, then adding the document instance to
     * that repository.
     *
     * If a document with the provided document's primary key already
     * exists, it will be replaced.
     *
     * @param document Document to place in the correct repository.
     */
    @JvmStatic
    fun put(document: Document)
    {
        get(document.descriptor.name)[document[document.descriptor.primaryKey]!!.toString()] = document
    }

    /**
     * Retrieves the correct document of the given type with the given
     * name. This is a shorthand convenience method for first retrieving
     * the document's descriptor's repository, then retrieving the
     * document instance in that repository.
     *
     * If no document with the given name exists, a new one will be
     * created with no data.
     *
     * @param descriptor Relevant document descriptor of the document to
     *     retrieve.
     * @param primaryKey The requested document's primary key.
     * @return The document instance with the given type and primary
     *     key, or a created empty document if none is found.
     */
    @JvmStatic
    fun get(descriptor: DocumentDescriptor, primaryKey: String) = get(descriptor.name)
        .getOrPut(primaryKey) { Document(descriptor) }

    /**
     * Retrieves the correct document of the given type with the given
     * name. This is a shorthand convenience method for first retrieving
     * the document's descriptor's repository, then retrieving the
     * document instance in that repository.
     *
     * If no document with the given name exists, a new one will be
     * created with no data.
     *
     * @param descriptorName Name of the relevant document descriptor of
     *     the document to retrieve.
     * @param primaryKey The requested document's primary key.
     * @return The document instance with the given type and primary
     *     key, or a null-pointer if none is found.
     */
    @JvmStatic
    fun get(descriptorName: String, primaryKey: String) = get(descriptorName)
        .getOrPut(primaryKey) { Document(DocumentDescriptor.forName(descriptorName)) }
}