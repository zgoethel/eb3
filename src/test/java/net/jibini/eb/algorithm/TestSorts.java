package net.jibini.eb.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random; 

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSorts
{
	private Random random = new Random();
	
	private int numElements = 254095;
	private int numIterations = 16;
	
	private void fillTestValues(List<Integer> elements)
	{
		if (elements instanceof LinkedList || elements.size() == 0)
		{
			if (elements instanceof LinkedList)
				elements.clear();
			
			for (int i = 0; i < numElements; i++)
				elements.add(random.nextInt(2048));
		} else
		{
			for (int i = 0; i < numElements; i++)
				elements.set(i, random.nextInt(2048));
		}
	}
	
	private void sortAndAssert(List<Integer> elements, Sort<Integer> sort)
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
	
	private Comparator<Integer> comparator = (a, b) ->
	{
		return a - b;
	};
	
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
	
	public void templateTestSort(List<Integer> list, Sort<Integer> sort)
	{
		for (int i = 0; i < numIterations; i++)
		{
			fillTestValues(list);
			sortAndAssert(list, sort);
		}
	}
	
	@Test
	public void canMergeSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canMergeSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new QuickSort<>(comparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator);
		
		templateTestSort(list, sort);
	}
}
