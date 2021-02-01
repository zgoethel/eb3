package net.jibini.eb.data.impl

import net.jibini.eb.data.DataSource
import net.jibini.eb.data.Document
import net.jibini.eb.data.DocumentDescriptor

/**
 * An abstract data source which maintains an in-memory cache of all
 * values which exist in the source and change over time.
 *
 * All existing documents are cached in memory. [Incremental
 * updates][retrieveIncrementalFor] will find remote changes and update
 * the local caches.
 *
 * @author Zach Goethel
 */
abstract class AbstractCachedDataSourceImpl : DataSource
{
    override fun retrieveFor(descriptor: DocumentDescriptor): Map<String, Document>
    {
        // Rely on the implementation's incremental sync
        retrieveIncrementalFor(descriptor)

        return DocumentRepositoryCachesImpl.get(descriptor)
    }

    override fun retrieveIncrementalFor(descriptor: DocumentDescriptor): Collection<Document>
    {
        val incremental = performIncrementalFor(descriptor)
        incremental.forEach() { DocumentRepositoryCachesImpl.put(it) }

        return incremental
    }

    /**
     * Internal incremental update for implementations of this abstract
     * class. Performs the actual update, wrapped by the parent's
     * [retrieveIncrementalFor] in order to properly cache values.
     *
     * @param descriptor Document descriptor of the documents to load.
     * @return All documents added or modified after the last retrieval.
     *     The time of last retrieval is tracked by the data source.
     */
    abstract fun performIncrementalFor(descriptor: DocumentDescriptor): Collection<Document>

    // Resorts to the cache, which also incrementally updates
    // the cache
    override fun retrieveSingle(descriptor: DocumentDescriptor, primaryKey: String)
        = retrieveFor(descriptor)[primaryKey]!!
}