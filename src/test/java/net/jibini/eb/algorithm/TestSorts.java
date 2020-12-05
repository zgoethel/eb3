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
	
	private void fillTestValues(List<Double> elements)
	{
		if (elements instanceof LinkedList || elements.size() == 0)
		{
			if (elements instanceof LinkedList)
				elements.clear();
			
			for (int i = 0; i < numElements; i++)
				elements.add((double)random.nextInt(2048) / 1024);
		} else
		{
			for (int i = 0; i < numElements; i++)
				elements.set(i, (double)random.nextInt(2048) / 1024);
		}
	}
	
	private void sortAndAssert(List<Double> elements, Sort<Double> sort)
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
	
	private Comparator<Double> doubleComparator = (e0, e1) ->
	{
		if (e0 < e1)
			return -1;
		else if (e0 > e1)
			return 1;
		else
			return 0;
	};
	
	private void systemSortTemplate(List<Double> list)
	{
		for (int i = 0; i < numIterations; i++)
		{
			fillTestValues(list);
			list.sort(doubleComparator);
			
			double last = -1.0;
			
			Assert.assertEquals("Mismatch in list size; elements added or lost", numElements, list.size());
			
			for (double e : list)
			{
				Assert.assertTrue("Sort failed; elements are out of order", e >= last);
				
				last = e;
			}
		}
	}
	
	@Test
	public void benchSystemSortArray()
	{
		List<Double> list = new ArrayList<>(numElements);
		
		systemSortTemplate(list);
	}
	
	@Test
	public void benchSystemSortLinked()
	{
		List<Double> list = new LinkedList<>();
		
		systemSortTemplate(list);
	}
	
	public void templateTestSort(List<Double> list, Sort<Double> sort)
	{
		for (int i = 0; i < numIterations; i++)
		{
			fillTestValues(list);
			sortAndAssert(list, sort);
		}
	}
	
	@Test
	public void canQuickSortArray()
	{
		List<Double> list = new ArrayList<Double>(numElements);
		Sort<Double> sort = new QuickSort<Double>(doubleComparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canQuickSortLinked()
	{
		List<Double> list = new LinkedList<Double>();
		Sort<Double> sort = new QuickSort<Double>(doubleComparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canMergeSortArray()
	{
		List<Double> list = new ArrayList<Double>(numElements);
		Sort<Double> sort = new MergeSort<Double>(doubleComparator);
		
		templateTestSort(list, sort);
	}
	
	@Test
	public void canMergeSortLinked()
	{
		List<Double> list = new LinkedList<Double>();
		Sort<Double> sort = new MergeSort<Double>(doubleComparator);
		
		templateTestSort(list, sort);
	}
}
