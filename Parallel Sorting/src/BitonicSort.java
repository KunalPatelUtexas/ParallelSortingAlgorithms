import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class  BitonicSort {
	
	private final static int UP = 1;
	private final static int DOWN = 0;
	
	//For bitonic sort, the array size n must be a power of two.
	
	//Starts the sorter and clock for parallel implementation
	public static long startParallel(int[] list) throws Throwable {
		long startTime = System.nanoTime();
		int size = list.length;
	    int start = 0;
	    int direction = UP;
		parallelBitonicSort(list, size, start, direction);
		long endTime = System.nanoTime();
		return (endTime - startTime);
	}
	
	//Starts the sorter and clock for serial implementation
	public static long startSerial(int[] list) throws Throwable {
		long startTime = System.nanoTime();
		int size = list.length;
	    int start = 0;
	    int direction = UP;
		bitonicSort(list, size, start, direction);
		long endTime = System.nanoTime();
		return (endTime - startTime);
	}
	
	
	
	/*Compare and Swap -
	 * 		Checks to see if the values at indices i and j are consistent
	 * 		with the direction in which list is sorted.
	 * 
	 * Inputs: 	int list[] 		- array of integers
	 * 			int i 			- index 1
	 * 			int j			- index 2
	 * 			int direction	- ascending or descending order of sorting 
	 * 
	 * */
	 public static void compareAndSwap(int list[], int i, int j, int direction)
	 {
	     if ( (list[i] > list[j] && direction == UP) || (list[i] < list[j] && direction == DOWN)) {
	         // If comparison succeeds, swap the values of list[i] and list[j]
	         int temp = list[i];
	         list[i] = list[j];
	         list[j] = temp;
	     }
	 }
	
	 /* Merge Sort -
	  * 	Divides a list then uses compare and swap 
	  * 	to sort the halves recursively.
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 		int start		- the starting position within the array
	  * 		int direction	- ascending or descending order of sorting
	  * 
	  * */
	 public static void bitonicMerge(int list[], int size, int start, int direction){
		 if(size == 1){
			 //single element lists are already sorted
			 //recursion base case
			 return;
		 }
		 else{
			 //divide list 
			 int div = size/2;
	         for (int i = start; i < start + div; i++){
	        	 //split and CAS into two sorted lists 
	             compareAndSwap(list, i, i + div , direction);
	         }
	         //take the two sorted lists and sort left and right halves
	         //left
	         bitonicMerge(list, div, start, direction);

	         //right
	         bitonicMerge(list, div, start + div, direction);

	     }
	 }
	 
	 /* Merge Sort -
	  * 	Divides a list then uses compare and swap 
	  * 	to sort the halves recursively.
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 		int start		- the starting position within the array
	  * 		int direction	- ascending or descending order of sorting
	  * 
	  * */
	 public static void parallelBitonicMerge(int list[], int size, int start, int direction){
		 if(size == 1){
			 //single element lists are already sorted
			 //recursion base case
			 return;
		 }
		 else{
			 //divide list 
			 int div = size/2;
			 ForkJoinPool pool = new ForkJoinPool();
	         for (int i = start; i < start + div; i++){
	        	 //split and CAS into two sorted lists 
	        	 pool.submit(new BitonicMergeSorter(list, i, i + div, direction));
	             //compareAndSwap(list, i, i + div , direction);
	         }
	         //take the two sorted lists and sort left and right halves
	         //left
	         bitonicMerge(list, div, start, direction);

	         //right
	         bitonicMerge(list, div, start + div, direction);

	     }
	 }
	 
	 /* Bitonic Sort -
	  * 	Divides a list into two halves, then sorts the left half in 
	  * 	ascending order and the right half in descending order.
	  * 	The two halves are then merged into a single sorted list.
	  * 
	  * 	This is a serial implementation of Bitonic Sort.
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 		int start		- the starting position within the array
	  * 		int direction	- ascending or descending order of sorting
	  * 
	  */
	 public static void bitonicSort(int list[], int size, int start, int direction){
		 if(size == 1){
			 //single element lists are already sorted
			 //recursion base case
			 return;
		 }
		 else{
			 //divide the list into two halves
	         int div = size/2;
	
	         //sort the left half in an ascending direction
	         bitonicSort(list, div, start, UP);
	         
	         //sort the right half in a descending direction
	         bitonicSort(list, div, start + div, DOWN);
			
			 //list now contains:
			 // [ [SORTED ASCENDING], [SORTED DESCENDING] ]
			 
			 //recursively sort the list using a mergesort
	         bitonicMerge(list, size, start, direction);
		 }
	 }
	
	 /* Parallel Bitonic Sort -
	  * 	Divides a list into two halves, then sorts the left half in 
	  * 	ascending order and the right half in descending order.
	  * 	The two halves are then merged into a single sorted list.
	  * 
	  * 	This is a parallel implementation of Bitonic Sort.
	  * 	
	  * Inputs:	int list[] 		- array of integers
	  * 		int size		- the size of array "list"
	  * 		int start		- the starting position within the array
	  * 		int direction	- ascending or descending order of sorting
	  * 
	  */
	 public static void parallelBitonicSort(int list[], int size, int start, int direction){
		 if(size == 1){
			 //single element lists are already sorted
			 //recursion base case
			 return;
		 }
		 else{
			 //divide the list into two halves
	         int div = size/2;
	
	         //sort the left half in an ascending direction
	         bitonicSort(list, div, start, UP);
	         //Thread left = new Thread(new BitonicSorter(list, div, start, UP));
	
	         //sort the right half in a descending direction
	         bitonicSort(list, div, start + div, DOWN);
	         //Thread right = new Thread(new BitonicSorter(list, div, start + div, DOWN));
	         
			 //list now contains:
			 // [ [SORTED ASCENDING], [SORTED DESCENDING] ]
			 
			 //recursively sort the list using a mergesort
			parallelBitonicMerge(list, size, start, direction);
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
	 public static class  BitonicMergeSorter implements Runnable {
			private int[] list;
			private int i;
			private int j;
			private int direction;
			
			public BitonicMergeSorter(int[] list, int i, int j, int direction) {
				this.list = list;
				this.i = i;
				this.j = j;
				this.direction = direction;
			}
			
			public void run() {
				try {
					BitonicSort.compareAndSwap(list, i, j, direction);
			    } catch (Exception e) {
					e.printStackTrace(System.out);
			    }
			}
		}
	
	
	 // tester
	 /*
	 public static void main(String args[]){
	     int list[] = {23, 35, 88, 43, 16, 9, 12, 55};
	     int size = list.length;
	     int start = 0;
	     int direction = UP;
	     
	     parallelBitonicSort(list, size, start, direction);
	     
	     System.out.println(Arrays.toString(list));
	 }
	 */
	

}