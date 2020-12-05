package net.jibini.eb.algorithm

import net.jibini.eb.algorithm.MergeSort.Frame

import java.util.concurrent.LinkedBlockingDeque

//TODO Address performance issues (array copies?)
//TODO Document
class MergeSort<E>(
	comparator: Comparator<E>
) : PseudoRecursiveSort<E, Frame<E>>(comparator)
{
	override val stable = true
	
	override fun generateFirstFrame(elements: List<E>): Frame<E>
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use merge sort on an immutable list")
		
		return Frame(elements)
	}
	
	override fun step(frame: Frame<E>, stack: LinkedBlockingDeque<Frame<E>>)
	{
		if (frame is MergeFrame)
			merge(frame)
		else
			slice(frame, stack)
	}
	
	private fun slice(frame: Frame<E>, stack: LinkedBlockingDeque<Frame<E>>)
	{
		val left = ArrayList<E>(frame.elements.size / 2)
		val right = ArrayList<E>(frame.elements.size - left.size)
		
		for ((i, element) in frame.elements.withIndex())
		{
			if (i < frame.elements.size / 2)
				left += element
			else
				right += element
		}
		
		// Push merge frame for after both slice frames are popped
		stack.push(MergeFrame(frame.elements, left, right))
		
		if (frame.elements.size >= 2)
		{
			stack.push(Frame(left))
			stack.push(Frame(right))
		}
	}
	
	private fun merge(frame: MergeFrame<E>)
	{
		var l = 0
		var r = 0
		
		var size = frame.elements.size
		
		frame.elements.clear()
		
		for (i in 0 until size)
		{
			when
			{
				l == frame.left.size -> frame.elements += frame.right[r++]
				
				r == frame.right.size -> frame.elements += frame.left[l++]
				
				else ->
				{
					val comparison = comparator.compare(frame.left[l], frame.right[r])
					
					when
					{
						comparison > 0 -> frame.elements += frame.right[r++]
						
						else -> frame.elements += frame.left[l++]
					}
				}
			}
		}
	}
	
	open class Frame<E>( 
		val elements: MutableList<E>
	)
	
	class MergeFrame<E>(
		elements: MutableList<E>,
		
		val left: List<E>,
		val right: List<E>
	) : Frame<E>(elements)
}