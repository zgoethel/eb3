package net.jibini.eb.data

/**
 * Any database or collection which can be queried for the entirety of
 * its values, or any values added or modified after a given point in
 * time. The data source implementation is responsible for tracking the
 * last point in time a sync was performed.
 *
 * @author Zach Goethel
 */
interface DataSource
{
    /**
     * @return All documents stored in this data source.
     */
    fun retrieveFor(descriptor: DocumentDescriptor): Collection<Document>

    /**
     * @return All documents added or modified after the last retrieval.
     *     The time of last retrieval is tracked by the data source.
     */
    fun retrieveIncrementalFor(descriptor: DocumentDescriptor): Collection<Document>
}