package net.jibini.eb.algorithm

import java.util.concurrent.LinkedBlockingDeque

//TODO Document
class PseudoRecursive<F>(
	firstFrame: F? = null
)
{
	val stack = LinkedBlockingDeque<F>()
	
	var allowThreads = false
	
	init
	{
		if (firstFrame != null)
			stack.push(firstFrame)
	}
	
	fun perform(step: Step<F>)
	{
		while (stack.isNotEmpty())
		{
			val frame = stack.pop()
			
			step.step(frame, stack)
		}
	}
	
	@FunctionalInterface
	interface Step<F>
	{
		fun step(frame: F, stack: LinkedBlockingDeque<F>)
	}
}