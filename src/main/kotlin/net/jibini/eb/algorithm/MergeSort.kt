package net.jibini.eb.algorithm

import java.util.LinkedList

//TODO Additional optimizations
//TODO Document
class MergeSort<E>(
	comparator: Comparator<E>
) : Sort<E>(comparator)
{
	override val stable = true
	
	override fun sort(elements: List<E>)
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use merge sort on an immutable list")
		
		val auxCopy = arrayOfNulls<Any>(elements.size)
		var mainCopy: MutableList<E> = elements
		
		if (elements is LinkedList)
		{
			mainCopy = ArrayList<E>(elements.size)
			mainCopy.addAll(elements)
		}
		
		var blockSize = 1
		
		while (true)
		{
			val numBlocks = (elements.size + (blockSize - 1)) / blockSize
			
			for (block in 0 until numBlocks step 2)
				merge(mainCopy, auxCopy, block * blockSize, blockSize)
			
			if (blockSize >= (elements.size + 1) / 2)
				break
			blockSize *= 2
		}
		
		if (elements is LinkedList)
		{
			elements.clear()
			elements.addAll(mainCopy)
		}
	}
	
	private fun merge(elements: MutableList<E>, aux: Array<Any?>, start: Int, blockSize: Int)
	{
		var l = start
		var r = start + blockSize
		
		val end = minOf(elements.size, start + blockSize * 2)
		
		for (i in start until end)
		{
			when
			{
				l >= start + blockSize -> aux[i] = elements[r++] as Any
				
				r >= end -> aux[i] = elements[l++] as Any
				
				else ->
				{
					val comparison = comparator.compare(elements[l], elements[r])
					
					if (comparison <= 0)
						aux[i] = elements[l++] as Any
					else
						aux[i] = elements[r++] as Any
				}
			}
		}
		
		for (i in start until end)
			@Suppress("UNCHECKED_CAST")
			elements[i] = aux[i]!! as E
	}
}