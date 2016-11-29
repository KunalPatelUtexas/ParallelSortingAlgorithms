import java.util.*;

/*
 * Psudocode:
 * 	ParallelSort(List)
 * 		if List > 1
 * 			Split List into Left and Right
 * 			In Parallel:
 * 				ParallelSort(Left)
 * 				ParallelSort(Right)
 * 			Merge Left and Right into List
 * 
 */
public class  MergeSort {
	private static final Random RAND = new Random(50);
	private static final int cores = Runtime.getRuntime().availableProcessors();

	public static long start(int[] a) throws Throwable {
		long startTime = System.nanoTime();
		parallelMergeSort(a, cores);
		long endTime = System.nanoTime();
		return (endTime - startTime);
	}
	
	
	public static void parallelMergeSort(int[] a, int numThreads) {
		try{
			if (numThreads <= 1) {
				mergeSort(a);
			} else if (a.length >= 2) {			
				splitMerge(a, numThreads);
			}
	   } catch (Exception e) {
			e.printStackTrace(System.out);
	   }
	}

	/*
	 * Split a[] in half and create a thread for reach half
	 */
	private static void splitMerge(int[] a, int numThreads) {
		try {
			int[] left  = Arrays.copyOfRange(a, 0, a.length / 2);
			int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);
			Thread lThread = new Thread(new Sorter(left,  numThreads / 2));
			Thread rThread = new Thread(new Sorter(right, numThreads / 2));
			lThread.start();
			rThread.start();
			
			try {
				lThread.join();
				rThread.join();
			} catch (InterruptedException ie) {}
			
			merge(left, right, a);
	   } catch (Exception e) {
			e.printStackTrace(System.out);
	   }
		
	}
	
	/*
	 * Helper method to sort each side of the arrays
	 */
	public static void mergeSort(int[] a) {
		try {
		
			if (a.length >= 2) {
				int[] left  = Arrays.copyOfRange(a, 0, a.length / 2);
				int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);
				
				mergeSort(left);
				mergeSort(right);
				
				merge(left, right, a);
			}
	   } catch (Exception e) {
			e.printStackTrace(System.out);
	   }
	}

	/*
	 * Merge left[] and right[] into a[] in O(n) time
	 */
	public static void merge(int[] left, int[] right, int[] a) {
		try {
			int left_index = 0;
			int right_index = 0;
			for (int i = 0; i < a.length; i++) {
				/*
				 * if right_index != valid, left index == valid, and left < right, populate from left side
				 * else, populate from right array
				 */
				if (right_index >= right.length || (left_index < left.length && left[left_index] < right[right_index])) {
					a[i] = left[left_index];
					left_index++;
				} else {
					a[i] = right[right_index];
					right_index++;
				}
			}
	   } catch (Exception e) {
			e.printStackTrace(System.out);
	   }
	}
	
	/*
	 * Helper class that runs Sorter thread
	 */
	public static class  Sorter implements Runnable {
		private int[] a;
		private int numThreads;
		
		public Sorter(int[] a, int numThreads) {
			this.a = a;
			this.numThreads = numThreads;
		}
		
		public void run() {
			try {
				MergeSort.parallelMergeSort(a, numThreads);
		    } catch (Exception e) {
				e.printStackTrace(System.out);
		    }
		}
	}

}