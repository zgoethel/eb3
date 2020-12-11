package net.jibini.eb.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random; 

import org.junit.Assert;
import org.junit.Test;

public class TestSorts
{
	private Random random = new Random();
	
	private int numElements = 60095;
	private int numIterations = 16;
	
	private void fillTestValues(List<Integer> elements, int numElements)
	{
		if (elements.isEmpty())
		{
			for (int i = 0; i < numElements; i++)
				elements.add(random.nextInt(Integer.MAX_VALUE));
		} else
		{
			ListIterator<Integer> listIterator = elements.listIterator();
			
			while (listIterator.hasNext())
			{
				listIterator.next();
				listIterator.set(random.nextInt(Integer.MAX_VALUE));
			}
		}
	}
	
	private void sortAndAssert(List<Integer> elements, Sort<Integer> sort, int numElements)
	{
		// Count the number of instances of each number before
		Map<Integer, Integer> numberCounts = new HashMap<>();
		for (int i : elements)
			numberCounts.put(i, numberCounts.getOrDefault(i, 0) + 1);
		
		sort.sort(elements);
		
		// Count the number of instances of each number after
		for (int i : elements)
			numberCounts.put(i, numberCounts.getOrDefault(i, 0) - 1);
		// Ensure the before and after counts are equal
		for (int i : numberCounts.values())
			Assert.assertEquals("The count of output values are not equal to the input", 0, i);
		
		int last = -1;
		
		// Ensure the overall size of the array is equal
		Assert.assertEquals("Mismatch in list size; elements added or lost", numElements, elements.size());
		
		for (int i : elements)
		{
			// Ensure each element is greater than (or equal to) the element
			// before it
			Assert.assertTrue("Sort failed; elements are out of order", i >= last);
			
			last = i;
		}
	}
	
	private Comparator<Integer> comparator = (a, b) -> a - b;
	
	@Test
	public void benchSystemSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new SystemSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void benchSystemSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new SystemSort<>(comparator);
		
		templateTestSort(list, sort);
	}

	public void templateTestSort(List<Integer> list, Sort<Integer> sort, int numElements)
	{
		for (int i = 0; i < numIterations; i++)
		{
			fillTestValues(list, numElements);
			sortAndAssert(list, sort, numElements);
		}
	}
	
	public void templateTestSort(List<Integer> list, Sort<Integer> sort)
	{
		templateTestSort(list, sort, numElements);
	}
	
	@Test
	public void canMergeSortSmallArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, 2048);
	}
	
	@Test
	public void canMergeSortSmallLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, 2048);
	}
	
	@Test
	public void canThreadedMergeSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canThreadedMergeSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new QuickSort<>(comparator, 64, new MergeSort<>(comparator));
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator, 64, new MergeSort<>(comparator));
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canHybridQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new QuickSort<>(comparator, 32, new HeapSort<>(comparator));
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canHybridQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator, 32, new HeapSort<>(comparator));
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canStableQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new StableQuickSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canStableQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new StableQuickSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canHeapSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canHeapSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort);
	}
}
