package net.jibini.eb.algorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSorts
{
	private class SortTestRun
	{
		String name;
		String type;
		
		int numElements;
		int comparisons;
		
		double time;
		
		SortTestRun(String name, String type, int numElements, int comparisons, double time)
		{
			this.name = name;
			this.type = type;
			
			this.numElements = numElements;
			this.comparisons = comparisons;
			
			this.time = time;
		}
	}
	
	private static final Logger log = LoggerFactory.getLogger(TestSorts.class);
	
	static List<SortTestRun> runs = new LinkedList<>();
	
	Random random = new Random();
	
	int numElements = 240095;
	int numIterations = 32;
	
	AtomicInteger counter = new AtomicInteger(0);
	
	private void fillTestValues(List<Integer> elements, int numElements)
	{
		if (elements.isEmpty())
		{
			for (int i = 0; i < numElements; i++)
				elements.add(i);
		} else
		{
			ListIterator<Integer> listIterator = elements.listIterator();
			
			int i = 0;
			
			while (listIterator.hasNext())
			{
				listIterator.next();
				listIterator.set(i++);
			}
		}
	}
	
	private double sortAndAssert(List<Integer> elements, Sort<Integer> sort, int numElements)
	{
		// Count the number of instances of each number before
		Map<Integer, Integer> numberCounts = new HashMap<>();
		for (int i : elements)
			numberCounts.put(i, numberCounts.getOrDefault(i, 0) + 1);
		
		counter.set(0);
		
		long start = System.currentTimeMillis();
		sort.sort(elements);
		long end = System.currentTimeMillis();
		
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
		
		return (double)(end - start) / 1000.0;
	}
	
	private Comparator<Integer> comparator = (a, b) ->
	{
		counter.incrementAndGet();
		
		return a - b;
	};
	
	@Test
	public void benchSystemSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new SystemSort<>(comparator);
		
		templateTestSort(list, sort, "Timsort");
	}
	
	@Test
	public void benchSystemSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new SystemSort<>(comparator);
		
		templateTestSort(list, sort, "Timsort");
	}

	public void templateTestSort(List<Integer> list, Sort<Integer> sort, int numElements,
			String postFix)
	{
//		log.info("================================");
//		log.info(String.format("TESTING SORT '%s' ON LIST '%s'", sort.getClass().getName(), 
//				list.getClass().getSimpleName()));
//		log.info("================================");
		
		int comparisonAccum = 0;
		double timeAccum = 0;

		fillTestValues(list, numElements);
		
		for (int i = 0; i < numIterations; i++)
		{
			Collections.shuffle(list);
			
			double time = sortAndAssert(list, sort, numElements);
			
			comparisonAccum += counter.get();
			timeAccum += time;
			
//			log.info(String.format("Run %d/%d: %d comparisons, %f seconds", i + 1, 
//					numIterations, counter.get(), time));
		}
		
		comparisonAccum /= numIterations;
		timeAccum /= numIterations;
		
		String type = "ARRAY";
		if (list instanceof LinkedList)
			type = "LINKED";
		
		String name = sort.getClass().getSimpleName();
		if (!postFix.equals(""))
			name += " (" + postFix + ")";
			
		runs.add(new SortTestRun(name, type, numElements,
				comparisonAccum, timeAccum));
		
//		log.info("================================");
//		log.info("");
	}
	
	public void templateTestSort(List<Integer> list, Sort<Integer> sort,
			String postFix)
	{
		templateTestSort(list, sort, numElements, postFix);
	}
	
	@Test
	public void canMergeSortSmallArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, 2048, "Small");
	}
	
	@Test
	public void canMergeSortSmallLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, 2048, "Small");
	}
	
	@Test
	public void canThreadedMergeSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canThreadedMergeSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new MergeSort<>(comparator);
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new QuickSort<>(comparator, 128, new MergeSort<>(comparator));
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator, 128, new MergeSort<>(comparator));
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canHybridQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new QuickSort<>(comparator, 32, new HeapSort<>(comparator));
		
		templateTestSort(list, sort, "Hybrid");
	}
	
	@Test
	public void canHybridQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new QuickSort<>(comparator, 32, new HeapSort<>(comparator));
		
		templateTestSort(list, sort, "Hybrid");
	}
	
	@Test
	public void canStableQuickSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new StableQuickSort<>(comparator);
		
		templateTestSort(list, sort, "*");
	}
	
	@Test
	public void canStableQuickSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new StableQuickSort<>(comparator);
		
		templateTestSort(list, sort, "*");
	}
	
	@Test
	public void canHeapSortArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canHeapSortLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort, "");
	}
	
	@Test
	public void canHeapSortSmallArray()
	{
		List<Integer> list = new ArrayList<>(numElements);
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort, 2048, "Small");
	}
	
	@Test
	public void canHeapSortSmallLinked()
	{
		List<Integer> list = new LinkedList<>();
		Sort<Integer> sort = new HeapSort<>(comparator);
		
		templateTestSort(list, sort, 2048, "Small");
	}
	
	@AfterClass
	public static void printResults()
	{
		new MergeSort<SortTestRun>((a, b) ->
		{
			if (a.time < b.time)
				return -1;
			else if (a.time > b.time)
				return 1;
			else
				return 0;
		}).sort(runs);
		
		new MergeSort<SortTestRun>((a, b) ->
		{
			if (a.name.contains("(Small)"))
				return 0;
			else
			{
				if (a.type.equals("LINKED") && b.type.equals("ARRAY"))
					return 1;
				else if (a.type.equals("ARRAY") && b.type.equals("LINKED"))
					return -1;
				else
					return 0;
			}
		}).sort(runs);
		
		log.info("SORT NAME            | TYPE   | AVERAGE SORT TIME (ASCENDING)        | AVERAGE COMPARISONS           ");
		log.info("=====================|========|======================================|===============================");
		
		int l = -1;
		String p = "";
		
		for (SortTestRun run : runs)
		{
			// Print a divider if this number of elements is an order of magnitude
			// greater than the previous run
			if (run.numElements / l >= 10 || (p.equals("ARRAY") && run.type.equals("LINKED"))
					&& !run.name.contains("(Small)"))
				log.info("---------------------|--------|--------------------------------------|-------------------------------");
			
			l = run.numElements;
			p = run.type;
			
			String message = "%-20s | %-6s | %7s ELEMENTS IN %f SECONDS | %9s (%f * nlog(n))";
			
			double nlogn = (double)run.numElements * (Math.log((double)run.numElements) / Math.log(2));
			double coefficient = (double)run.comparisons / nlogn;
			
			log.info(String.format(message, run.name, run.type, "" + run.numElements,
					run.time, "" + run.comparisons, coefficient));
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("sort-analysis.csv"));
		
		Comparator<Integer> comparator = Comparator.comparingInt(a -> a);
		
		Sort<?>[] sorts = 
		{
			new SystemSort<Integer>(comparator),
			
			new MergeSort<Integer>(comparator),
			new QuickSort<Integer>(comparator, 4096, new MergeSort<>(comparator)),
			
			new QuickSort<Integer>(comparator, 16, new HeapSort<>(comparator)),
			
//			new HeapSort<Integer>(comparator),
//			new StableQuickSort<Integer>(comparator)
		};
		
		for (Sort<?> sort : sorts)
			writer.write("," + sort.getClass().getSimpleName());
		writer.write("\n");
		
		for (int i = 1; i <= 2000001; i += 50000)
		{
			ArrayList<Integer> numbers = new ArrayList<>(i);
			for (int j = 0; j < i; j++)
				numbers.add(j);
			
			System.out.print(String.format("%-9s: ", i + ""));
			writer.write("" + i);
			
			for (Sort<?> sort : sorts)
			{
				double time = 0.0;
				int numRuns = 8;
				
				for (int j = 0; j < numRuns; j++)
				{
					Collections.shuffle(numbers);
					
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException ex)
					{ }
	
					long start = System.currentTimeMillis();
					((Sort<Integer>)sort).sort(numbers);
					long end = System.currentTimeMillis();
					
					time += (double)(end - start) / 1000;

					System.out.print("||");
					
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException ex)
					{ }
				}
				
				writer.write("," + time / numRuns);
			}
			
			System.out.println();
			writer.write("\n");
		}
		
		writer.flush();
		writer.close();
	}
}
