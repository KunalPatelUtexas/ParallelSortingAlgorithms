import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrickSort {
	
	static AtomicBoolean cmp = new AtomicBoolean();
	
	
	//"Parallel bubble sort"
	
	//Starts the sorter and clock for parallel implementation
		public static long startParallel(int[] list) throws Throwable {
			long startTime = System.nanoTime();
			int size = list.length;
			
			//old implementation
			parallelBrickSort(list, size);
			
			//new implementation
			parallelBrickMergeSort(list, 0, size);
			
			long endTime = System.nanoTime();
			return (endTime - startTime);
		}
		
		//Starts the sorter and clock for serial implementation
		public static long startSerial(int[] list) throws Throwable {
			long startTime = System.nanoTime();
			int size = list.length;
			
			//old implementation
			//brickSort(list, size);
			
			//new implementation
			brickMergeSort(list, 0, size);
			
			long endTime = System.nanoTime();
			return (endTime - startTime);
		}
	
	/*Compare and Swap -
	 * 		Checks to see if the values at indices i and j are consistent
	 * 		with the direction in which list is sorted. 
	 * 		Also returns a boolean representing whether or not the swap 
	 * 		occurred after the comparison.
	 * 
	 * Inputs: 	int list[] 		- array of integers
	 * 			int i 			- index 1
	 * 			int j			- index 2
	 * 			int direction	- ascending or descending order of sorting 
	 * 
	 * Outputs: boolean 		- true if swap occurred, false otherwise
	 * 
	 */
	public static boolean compareAndSwap(int[] list, int i, int j){
		if(list[i] > list[j]){
			int temp = list [j];
			list[j] = list[i];
			list[i] = temp;
			return true;
		}
		return false;
	}
	
	/* Parallel Compare and Swap -
	 * 		Checks to see if the values at indices i and j are consistent
	 * 		with the direction in which list is sorted. 
	 * 		Performs a get-and-set operation on the static variable cmp
	 * 		which represents whether that iteration is complete.
	 * 
	 * Inputs: 	int list[] 		- array of integers
	 * 			int i 			- index 1
	 * 			int j			- index 2
	 * 			int direction	- ascending or descending order of sorting 
	 * 
	 */
	public static void parallelCompareAndSwap(int[] list, int i, int j){
		if(list[i] > list[j]){
			int temp = list [j];
			list[j] = list[i];
			list[i] = temp;
			cmp.getAndSet(false);
		}
	}
	
	/* Brick Sort -
	  * 	ALternates between sorting even and odd elements in list
	  * 	|  x   y  |  z   a  |  b   c  |  d   e  | 
	  * 	  x  |  y   z  |  a   b  |  c   d  |  e
	  * 						...
	  * 						etc
	  *    
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 
	  */
	public static void brickSort(int[] list, int size){
		
		//array is unsorted initially
		boolean complete = false;
		 
		//each iteration of the loop is two phases in the brick sort
	    while (!complete){
	    	//set complete to true
	    	//If no swaps are made, then complete remains true at the end of the
	    	//iteration, indicating that the sort is complete.
	        complete = true;
	        
	        //use two separate loops for even and odd elements to avoid collision
	        int i = 0;
	        
	        //even loop
		    for (; i <= size - 2; i += 2){
		        if (compareAndSwap(list, i, i+1)){
		            complete = false;
		        }
		    }
		    
		    //odd loop
		    for (i = 1; i <= size - 2; i += 2){
		    	if (compareAndSwap(list, i, i+1)){
		            complete = false;
		        }
	        }
		}     
	}
	
	/* Parallel Brick Sort -
	  * 	ALternates between sorting even and odd elements in list
	  * 	|  x   y  |  z   a  |  b   c  |  d   e  | 
	  * 	  x  |  y   z  |  a   b  |  c   d  |  e
	  * 						...
	  * 						etc
	  * 
	  * 	The sorting in each phase is done in parallel using
	  * 	compare-and-swap.
	  *    
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 
	  */
	public static void parallelBrickSort(int[] list, int size){
		
		//array is unsorted initially
		cmp.set(false);
		 
		//each iteration of the loop is two phases in the brick sort
	    while (!cmp.get()){
	    	//set complete to true
	    	//If no swaps are made, then complete remains true at the end of the
	    	//iteration, indicating that the sort is complete.
	        cmp.set(true);
	        
	        //use two separate loops for even and odd elements to avoid collision
	        int i = 0;
	        
	        //parallelize CAS operations
	        ForkJoinPool pool = new ForkJoinPool();
	        
	        //even loop
		    for (; i <= size - 2; i += 2){
		    	pool.submit(new BrickSorter(list, i, i + 1));
		    }
		    
		    //odd loop
		    for (i = 1; i <= size - 2; i += 2){
		    	pool.submit(new BrickSorter(list, i, i + 1));
	        }
		}     
	}
	
	////////////////////////////////////////
	//IMPROVED IMPLEMENTATION: BRICK MERGE//
	////////////////////////////////////////
	
	/*
	 * Serial Implementation
	 */
	public static void brickMerge(int list[], int start, int len, int dist){
		
		int div = dist*2;
		
		if(div < len){
			//even phase
			brickMerge(list, start, len, div);
			//odd phase
			brickMerge(list, start + dist, len, div);
			for(int i = start + dist; i + dist < start + len; i += div){
				compareAndSwap(list, i, i + dist);
			}
		}
		else{
			compareAndSwap(list, start, start + dist);
		}
	}

	public static void brickMergeSort(int[] list, int start, int len){
		//recursion base case
		if(len == 1){
			return;
		}
		else{
			//split and sort halves
			int div = len/2;
			brickMergeSort(list, start, div);
			brickMergeSort(list, start + div, div);
			//merge halves
			brickMerge(list, start, len, 1);
		}
	}
	
	/*
	 * Parallel Implementation
	 */
	public static void parallelBrickMerge(int list[], int start, int len, int dist){
		
		int div = dist*2;
		
		if(div < len){
			//even phase
			Thread even = new Thread(new BrickMergeSorter(list, start, len, div));
			//odd phase
			Thread odd = new Thread(new BrickMergeSorter(list, start + dist, len, div));
			
			even.start();
			odd.start();
			
			try {
				even.join();
				odd.join();
			} catch (InterruptedException ie) {}
			
			
			for(int i = start + dist; i + dist < start + len; i += div){
				compareAndSwap(list, i, i + dist);
			}
		}
		else{
			compareAndSwap(list, start, start + dist);
		}
	}

	public static void parallelBrickMergeSort(int[] list, int start, int len){
		//recursion base case
		if(len == 1){
			return;
		}
		else{
			//split and sort halves
			int div = len/2;
			parallelBrickMergeSort(list, start, div);
			parallelBrickMergeSort(list, start + div, div);
			//merge halves
			parallelBrickMerge(list, start, len, 1);
		}
	}
	
	
	
	
	/* Brick Sorter -
	  * 	Brick Sorter is a static inner class that is used by
	  * 	each thread in merge to concurrently run their compare-
	  * 	and-swap operations on the array "list".
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int i			- index 1
	  * 		int j			- index 2
	  * 
	  */
	public static class  BrickSorter implements Runnable {
		private int[] list;
		private int i;
		private int j;
		
		public BrickSorter(int[] list, int i, int j) {
			this.list = list;
			this.i = i;
			this.j = j;
		}
		
		public void run() {
			try {
				BrickSort.parallelCompareAndSwap(list, i, j);
		    } catch (Exception e) {
				e.printStackTrace(System.out);
		    }
		}
	}
	
	/* Brick Merge Sorter -
	  * 	Brick Merge Sorter is a static inner class that is used by
	  * 	each thread in merge to concurrently run their compare-
	  * 	and-swap operations on the array "list".
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int start		- index 1
	  * 		int len			- index 2
	  * 		int dist		- 
	  * 
	  */
	public static class  BrickMergeSorter implements Runnable {
		private int[] list;
		private int start;
		private int len;
		private int dist;
		
		public BrickMergeSorter(int[] list, int start, int len, int dist) {
			this.list = list;
			this.start = start;
			this.len = len;
			this.dist = dist;
		}
		
		public void run() {
			try {
				BrickSort.brickMerge(list, start, len, dist);
		    } catch (Exception e) {
				e.printStackTrace(System.out);
		    }
		}
	}
	
	//tester
    public static void main(String args[]){
	     int list[] = {23, 35, 88, 43, 16, 9, 12, 55};
	     int size = list.length;
	     
	     parallelBrickMergeSort(list, 0, size);
	     
	     System.out.println(Arrays.toString(list));
	 }
}
