package net.jibini.eb.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random; 

import org.junit.Assert;
import org.junit.Test;

public class TestSorts
{
	private Random random = new Random();
	
	private int numElements = 254095;
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
		sort.sort(elements);
		
		double last = -1.0;
		
		Assert.assertEquals("Mismatch in list size; elements added or lost", numElements, elements.size());
		
		for (double e : elements)
		{
			Assert.assertTrue("Sort failed; elements are out of order", e >= last);
			
			last = e;
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
		Sort<Integer> sort = new QuickSort<>(comparator, 16, new MergeSort<>(comparator));
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canHybridQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator, 16, new MergeSort<>(comparator));
		
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
