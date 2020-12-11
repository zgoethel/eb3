package net.jibini.eb.algorithm

import kotlin.math.pow

import java.util.LinkedList

//TODO Additional optimizations
//TODO Document
class HeapSort<E>(
	comparator: Comparator<E>
): Sort<E>(comparator)
{
	override val stable = false
	
	override fun sort(elements: List<E>)
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use heapsort on an immutable list")
		
		var list: MutableList<E> = elements
		
		if (elements is LinkedList)
		{
			list = ArrayList(elements.size)
			list.addAll(elements)
		}
			
		val heap = HeapWrapper(list)
		
		heap.heapify(comparator)
		
		for (i in 0 until list.size)
		{
			val nextSorted = list[0]
			
			list[0] = list[list.lastIndex - heap.ignoreLast]
			list[list.lastIndex - heap.ignoreLast++] = nextSorted
			
			if (i < list.size - 2)
				heap.siftDown(0, 0, comparator)
		}
		
		if (elements is LinkedList)
		{
			val listIterator = elements.listIterator()
			var i = 0
			
			while (listIterator.hasNext())
			{
				listIterator.next()
				listIterator.set(list[i++])
			}
		}
	}
}

class HeapWrapper<E>(
	val internal: MutableList<E>
)
{
	var ignoreLast = 0
	
	fun get(row: Int, index: Int): E?
	{
		val i = rowToIndex(row) + index
		
		return if (i < internal.size - ignoreLast)
			internal[i]
		else
			null
	}
	
	fun set(row: Int, index: Int, value: E)
	{
		internal[rowToIndex(row) + index] = value
	}
	
	fun childIndexInRow(index: Int, location: Int) = index * 2 + location
	
	fun indexToRow(index: Int) = Integer.numberOfTrailingZeros(Integer.highestOneBit(index + 1))
	
	fun rowToIndex(row: Int) = (1 shl row) - 1
	
	fun indexToElement(index: Int) = Pair(indexToRow(index), index - rowToIndex(indexToRow(index)))
	
	val deepestRow: Int
		get() = indexToRow(internal.size - ignoreLast - 1)
	
	val lastElement: Pair<Int, Int>
		get() = indexToElement(internal.size - ignoreLast - 1)
	
	val lastElementWithChildren: Pair<Int, Int>
		get()
		{
			var (row, index) = lastElement
			
			row--
			index /= 2
			
			return Pair(row, index)
		}
	
	fun siftDown(row: Int, index: Int, comparator: Comparator<E>)
	{
		var sifted = false
				
		val self = this.get(row, index)!!
		
		var siftRow = row
		var siftIndex = index
		
		val (lastParentRow, lastParentIndex) = lastElementWithChildren
		val deepest = deepestRow
		
		while (!sifted && siftRow < deepest && (siftRow < lastParentRow || siftIndex <= lastParentIndex))
		{
			siftRow++
			
			val childLeftIndex = childIndexInRow(siftIndex, 0)
			
			val childLeft = this.get(siftRow, childLeftIndex)!!
			val childRight = this.get(siftRow, childLeftIndex + 1)
			
			val childComparison =
				if (childRight == null)
					0
				else
					comparator.compare(childLeft, childRight)
			
			val leastChild =
				if (childComparison >= 0)
					childLeft
				else
					childRight
			
			val comparisonToChild = comparator.compare(self, leastChild)
			
			if (comparisonToChild < 0)
			{
				this.set(siftRow - 1, siftIndex, leastChild!!)
				
				if (childComparison >= 0)
				{
					this.set(siftRow, childLeftIndex, self)
					siftIndex = childLeftIndex
				} else
				{
					this.set(siftRow, childLeftIndex + 1, self)
					siftIndex = childLeftIndex + 1
				}
			} else
				sifted = true
		}
	}
	
	fun heapify(comparator: Comparator<E>)
	{
		var (r, i) = lastElementWithChildren
		
		for (row in r downTo 0)
		{
			for (index in i downTo 0)
				siftDown(row, index, comparator)
			
			i = (1 shl (row - 1)) - 1
		}
	}
}