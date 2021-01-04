package net.jibini.eb.data

/**
 * Any database or collection which can be queried for the entirety
 * of its values, or any values added or modified after a given point.
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
     */
    fun retrieveIncrementalFor(descriptor: DocumentDescriptor): Collection<Document>
}