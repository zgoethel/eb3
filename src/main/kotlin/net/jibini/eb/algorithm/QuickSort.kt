package net.jibini.eb.algorithm

import net.jibini.eb.algorithm.QuickSort.Frame

import java.util.LinkedList
import java.util.concurrent.LinkedBlockingDeque

/**
 * An unstable iterative quick-sort implementation; sorts on array-lists are
 * in-place, but linked-lists are copied to an array for constant-time random
 * access (adds O(n) space complexity and is copied back upon completion).
 *
 * By nature, this algorithm is best suited for lists with constant-time access
 * due to heavy usage of random index accesses (e.g. array-lists), but has been
 * adapted to have additional space complexity to better support linked-lists.
 * 
 * Each iteration's partition is one or more elements wide, meaning that
 * elements with a comparation of zero against the pivot will not be sorted any
 * further by any child iterations; put another way, each partition created is
 * between one extremity of the previous partition until the closest element
 * equal to the pivot, thus no sub-partition will contain an element with a
 * comparison of zero against the pivot.
 *
 * For a stable sort, try merge sort, stable quick-sort, or the system's sort.
 *
 * This implementation has also been modified to fall back to another sort
 * implementation in a worst-case scenario (perfectly sorted or sorted in
 * reverse); the fallback sort algorithm will be used if a given stack-depth
 * is reached (the recommended fallback depth is 64).
 *
 * @see PseudoRecursive
 * @see PseudoRecursiveSort
 *
 * @see StableQuickSort
 * @see MergeSort
 * @see SystemSort
 *
 * @author Zach Goethel
 */
//TODO Optimize for all-unique inputs
class QuickSort<E>(
	/**
	 * Element comparator by which to mutually compare elements.
	 */
	comparator: Comparator<E>,
	
	/**
 	 * The stack depth at which the quick-sort will fall back to another sort
	 * (for improved worst-case performance, see hybrid sorts/introsort).
	 */
	val fallbackDepth: Int = 64,
	/**
 	 * An instance of the fallback hybrid sort method.
	 */
	val fallbackSort: Sort<E> = MergeSort<E>(comparator)
) : PseudoRecursiveSort<E, Frame<E>>(comparator)
{
	// This implementation of quick-sort is not stable; using additional space
	// and/or time complexity, this algorithm could be made to be stable
	override val stable = false
	
	override fun generateFirstFrame(elements: List<E>): Frame<E>
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use quick-sort on an immutable list")
		
		return if (elements is LinkedList)
		{
			// Array copy adds O(n) space complexity, but massively improves
			// runtime of quick-sort on linked lists
			val copied = ArrayList<E>()
			copied.addAll(elements)
			// Set the first stub frame to include a linked-list reference
			Frame(copied, linked = elements)
		} else
			Frame(elements)
	}
	
	override fun step(frame: Frame<E>, stack: LinkedBlockingDeque<Frame<E>>)
	{
		if (frame is PivotFrame)
		{
			if (frame.depthCount == 0)
			{
				fallbackSort.sort(frame.elements.subList(frame.left, frame.right + 1))
				
				return
			}
			
			// Track left- and right-most indices which are in the partition
			// (e.g. have a comparison to the pivot of zero)
			var partRight = frame.left
			var partLeft = frame.left
			
			val pivot = frame.elements[partRight]
			
			for (i in frame.left + 1 .. frame.right)
			{
				// Grab element and compare to pivot
				val element = frame.elements[i]
				val comparison = comparator.compare(element, pivot)
				
				// Partition starts on the left; only move the partition if
				// this element belongs before or inside of it
				if (comparison <= 0)
				{
					// Maintain element directly after partition
					frame.elements[i] = frame.elements[++partRight]
					// Leap-frog the partition and put element
					frame.elements[partRight] = frame.elements[partLeft]
					frame.elements[partLeft] = element
					
					// The left edge of the partition only moves if the element
					// added is not part of the partition
					if (comparison < 0)
						partLeft++
				}
			}
			
			// Only spawn more frames if the current partition is at least two
			// elements wide; only spawn each sub-partition if each individual
			// partition will have more than one element
			if (frame.right > frame.left + 1)
			{
				if (partLeft > frame.left + 1)
					stack.push(PivotFrame(frame.elements, frame.depthCount - 1, frame.left, partLeft - 1))
				if (partRight < frame.right - 1)
					stack.push(PivotFrame(frame.elements, frame.depthCount - 1, partRight + 1, frame.right))
			}
		} else if (frame is CopyBackFrame)
		{
			// Reached final frame, copy back array to linked-list
			frame.linked!!.clear()
			frame.linked.addAll(frame.elements)
		} else
		{
			// This is a linked-list; create an array copy
			if (frame.linked != null)
				stack.push(CopyBackFrame(frame.elements, frame.linked))
			// Push first pivot frame on the array-list or copied array
			stack.push(PivotFrame(frame.elements, fallbackDepth))
		}
	}
	
	/**
	 * A stub quick-sort execution frame; if a frame is an instance of this
	 * type by none of the specific frame types, it is assumed this is the first
	 * frame of execution and additional steps to set up the stack must be
	 * taken (e.g. pushing the copy-back frame and first pivot frame).
	 *
	 * @see PivotFrame
	 * @see CopyBackFrame
	 */
	open class Frame<E>(
		val elements: MutableList<E>,
		// Maintain a reference to the original linked-list to copy back later
		val linked: LinkedList<E>? = null
	)
	
	/**
	 * This is a single unit of the quick-sort algorithm, representing a single
	 * partition who can spawn two sub-partitions.
	 *
	 * During each execution of a pivot frame, elements less than the pivot
	 * value will be moved to an index prior to the partition index, and values
	 * greater than the pivot will remain on the right side of the partition.
	 *
	 * @see Frame
	 * @see CopyBackFrame
	 */
	class PivotFrame<E>(
		elements: MutableList<E>,
		
		val depthCount: Int,
		
		val left: Int = 0,
		val right: Int = elements.lastIndex,
		
		linked: LinkedList<E>? = null
	) : Frame<E>(elements, linked)
	
	/**
	 * Upon completion of a sort, this frame may exist as the final task on the
	 * stack; if the original sort data was held in a linked-list, the array
	 * copy must be copied back to the original linked-list.
	 *
	 * @see Frame
	 * @see PivotFrame
	 */
	class CopyBackFrame<E>(
		elements: MutableList<E>,
		
		linked: LinkedList<E>
	) : Frame<E>(elements, linked)
}

/**
 * Wraps the quick-sort algorithm in such a manner which considers the initial
 * index of elements as a sorting rule.
 *
 * In its current implementation, multiple auxilary arrays may be built.
 *
 * Due to the extra used space and time taken to copy arrays, this implementation
 * is not recommended (it is included as an obligatory utility).
 * 
 * @see QuickSort
 * @see MergeSort
 * @see SystemSort
 *
 * @author Zach Goethel
 */
//TODO Additional optimizations
//TODO Document
class StableQuickSort<E>(
	comparator: Comparator<E>
) : Sort<E>(comparator)
{
	override val stable = true
	
	override fun sort(elements: List<E>)
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use stable quick-sort on an immutable list")
		
		val indexedCopy = ArrayList<IndexedElement<E>>(elements.size)
		for ((i, element) in elements.withIndex())
			indexedCopy += IndexedElement(i, element)
		
		val quickSort = QuickSort<IndexedElement<E>>(
		{
			a, b ->
			
			val comparison = comparator.compare(a.element, b.element)
			
			when (comparison)
			{
				0 -> a.index - b.index
				else -> comparison
			}
		})
		
		quickSort.sort(indexedCopy)
		
		if (elements is LinkedList)
		{
			elements.clear()
			for (indexed in indexedCopy)
				elements += indexed.element
		} else
		{
			for ((i, indexed) in indexedCopy.withIndex())
				elements[i] = indexed.element
		}
	}
	
	class IndexedElement<E>(
		val index: Int,
		val element: E
	)
}