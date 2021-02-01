package net.jibini.eb.data

import net.jibini.eb.data.impl.AbstractCachedDataSourceImpl

/**
 * Any database or collection which can be queried for the entirety of
 * its values, or any values added or modified after a given point in
 * time. The data source implementation is responsible for tracking the
 * last point in time a sync was performed.
 *
 * For an implementation which maintains a cached copy and only
 * incrementally updates values, extend [AbstractCachedDataSourceImpl].
 *
 * @author Zach Goethel
 */
interface DataSource
{
    /**
     * Provides access to the full contents of the data source. This may
     * be used to gather an entire dataset to be used for filtering and
     * display of filtered results.
     *
     * For an implementation which maintains a cached copy and only
     * incrementally updates values, extend [AbstractCachedDataSourceImpl].
     *
     * @return All documents stored in this data source, keyed by their
     *     primary keys.
     */
    fun retrieveFor(descriptor: DocumentDescriptor): Map<String, Document>

    /**
     * Checks for any documents in this data source which have changed
     * since the last incremental update. Calling this incremental
     * update should update the tracking indices, thus calling this
     * method twice in quick succession should always return an empty
     * set the second time.
     *
     * For external data sources, query against a line tracker, such as
     * the SQL sequential modification counter.
     *
     * @return All documents added or modified after the last retrieval.
     *     The time of last retrieval is tracked by the data source.
     */
    fun retrieveIncrementalFor(descriptor: DocumentDescriptor): Collection<Document>

    /**
     * Retrieves a single document which has the provided type and the
     * provided primary key. If no document exists, a blank document
     * will null-pointer values will be created.
     *
     * @returns Retrieved or created document with the given type and
     *     matching primary key.
     */
    fun retrieveSingle(descriptor: DocumentDescriptor, primaryKey: String): Document
}