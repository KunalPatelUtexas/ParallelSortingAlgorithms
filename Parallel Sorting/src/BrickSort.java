import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrickSort {
	
	static AtomicBoolean cmp = new AtomicBoolean();
	
	
	//"Parallel bubble sort"
	
	//Starts the sorter and clock for parallel implementation
		public static long startParallel(int[] list) throws Throwable {
			long startTime = System.nanoTime();
			int size = list.length;
			parallelBrickSort(list, size);
			long endTime = System.nanoTime();
			return (endTime - startTime);
		}
		
		//Starts the sorter and clock for serial implementation
		public static long startSerial(int[] list) throws Throwable {
			long startTime = System.nanoTime();
			int size = list.length;
			brickSort(list, size);
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
	
	/* Bitonic Merge Sorter -
	  * 	Bitonic Merge Sorter is a static inner class that is used by
	  * 	each thread in merge to concurrently run their compare-
	  * 	and-swap operations on the array "list".
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 		int start		- the starting position within the array
	  * 		int direction	- ascending or descending order of sorting
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
	
	//tester
    public static void main(String args[]){
	     int list[] = {23, 35, 88, 43, 16, 9, 12, 55};
	     int size = list.length;
	     
	     parallelBrickSort(list, size);
	     
	     System.out.println(Arrays.toString(list));
	 }
}
