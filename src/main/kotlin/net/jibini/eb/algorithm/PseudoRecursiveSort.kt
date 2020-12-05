package net.jibini.eb.algorithm

//TODO Document
abstract class PseudoRecursiveSort<E, F>(
	comparator: Comparator<E>,
) : Sort<E>(comparator),
	PseudoRecursive.Step<F>
{
	override fun sort(elements: List<E>)
	{
		val firstFrame = this.generateFirstFrame(elements)
		
		val pseudo = PseudoRecursive(firstFrame)
		pseudo.perform(this)
	}
	
	abstract fun generateFirstFrame(elements: List<E>): F
}