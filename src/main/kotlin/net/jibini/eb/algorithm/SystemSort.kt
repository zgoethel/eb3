package net.jibini.eb.algorithm

class SystemSort<E>(
	comparator: Comparator<E>
) : Sort<E>(comparator)
{
	override val stable = true
	
	override fun sort(elements: List<E>)
	{
		if (elements !is MutableList)
			throw IllegalStateException("Cannot use system sort on an immutable list")
		
		elements.sortWith(comparator)
	}
}