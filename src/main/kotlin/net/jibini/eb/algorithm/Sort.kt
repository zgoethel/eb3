package net.jibini.eb.algorithm

/**
 * Any algorithm which can sort elements by mutually comparing them.
 *
 * @see PseudoRecursiveSort
 *
 * @see QuickSort
 * @see MergeSort
 *
 * @author Zach Goethel
 */
abstract class Sort<E>(
	var comparator: Comparator<E>
)
{
	/**
	 * Whether the algorithm is stable (the order of equivalent elements is
	 * always preserved).
	 */
	abstract val stable: Boolean
	
	/**
	 * Sorts the elements in the given list.
	 *
	 * @param elements List containing elements to sort; the contents of the
	 *		list may be modified, but the list's size should not change
	 */
	abstract fun sort(elements: List<E>)
}