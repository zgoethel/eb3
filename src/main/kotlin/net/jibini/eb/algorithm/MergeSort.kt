package net.jibini.eb.algorithm

import java.util.LinkedList

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

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
		
		val aux = arrayOfNulls<Any>(elements.size)
		var main: MutableList<E> = elements
		
		if (elements is LinkedList)
		{
			main = ArrayList<E>(elements.size)
			main.addAll(elements)
		}
		
		if (elements.size < 4096)
		{
			var blockSize = 1
			
			while (true)
			{
				val numBlocks = (elements.size + (blockSize - 1)) / blockSize
				
				for (block in 0 until numBlocks step 2)
					merge(main, aux, block * blockSize, blockSize)
				
				if (blockSize >= (elements.size + 1) / 2)
					break
				blockSize *= 2
			}
		} else
		{
			val numThreads = 4
			val jobs = ArrayList<Job>(numThreads)
			
			val perThread = (elements.size + (numThreads - 1)) / numThreads
			
			for (i in 0 until numThreads)
				jobs += GlobalScope.launch {
					var blockSize = 1
					
					while (true)
					{
						val numBlocks = (perThread + (blockSize - 1)) / blockSize
						
						for (block in 0 until numBlocks step 2)
						{
							merge(main, aux, i * perThread + block * blockSize, blockSize,
								hardStop = i * perThread + perThread)
						}
						
						if (blockSize >= (perThread + 1) / 2)
							break
						blockSize *= 2
					}
				}
			
			runBlocking {
				for (job in jobs)
					job.join()
			}
			
			// For this block, see single-threaded branch above
			var blockSize = perThread
			
			while (true)
			{
				val numBlocks = (elements.size + (blockSize - 1)) / blockSize
				
				for (block in 0 until numBlocks step 2)
					merge(main, aux, block * blockSize, blockSize)
				
				if (blockSize >= (elements.size + 1) / 2)
					break
				blockSize *= 2
			}
		}
		
		if (elements is LinkedList)
		{
			elements.clear()
			elements.addAll(main)
		}
	}
	
	private fun merge(elements: MutableList<E>, aux: Array<Any?>, start: Int, blockSize: Int,
			hardStop: Int = elements.size)
	{
		var l = start
		var r = start + blockSize
		
		val end = minOf(hardStop, elements.size, start + blockSize * 2)
		
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