import java.util.Arrays;
import java.util.Random;

public class  BitonicSort {
	
	private final static int UP = 1;
	private final static int DOWN = 0;
	
	//for bitonic sort, the array size n must be a power of two
	
	/*Compare and Swap -
	 * 		Checks to see if the values at indices i and j are consistent
	 * 		with the direction in which list is sorted.
	 * 
	 * Inputs: 
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
	
	 /* mergeSort -
	  * 
	  * Inputs:
	  * 
	  * */
	 public static void mergeSort(int list[], int size, int start, int direction){
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
	         mergeSort(list, div, start, direction);
	         //right
	         mergeSort(list, div, start + div, direction);
	     }
	 }
	
	 /* This funcion first produces a bitonic sequence by
	    recursively sorting its two halves in opposite sorting
	    orders, and then  calls bitonicMerge to make them in
	    the same order */
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
	         //bitonicSort(list, div, start, UP);
	         Thread left = new Thread(new Sorter(list, div, start, UP));
	
	         //sort the right half in a descending direction
	         //bitonicSort(list, div, start + div, DOWN);
	         Thread right = new Thread(new Sorter(list, div, start + div, DOWN));
	         
	         left.start();
	         right.start();
			
			try {
				left.join();
				right.join();
			}
			catch (InterruptedException ie) {}
			
			 //list now contains:
			 // [ [SORTED ASCENDING], [SORTED DESCENDING] ]
			 
			 //recursively sort the list using a mergesort
			 mergeSort(list, size, start, direction);
		 }
	 }
	 
	 public static class  Sorter implements Runnable {
			private int[] list;
			private int size;
			private int start;
			private int direction;
			
			public Sorter(int[] list, int size, int start, int direction) {
				this.list = list;
				this.size = size;
			}
			
			public void run() {
				try {
					BitonicSort.bitonicSort(list, size, start, direction);
			    } catch (Exception e) {
					e.printStackTrace(System.out);
			    }
			}
		}
	
	
	 // tester
	 public static void main(String args[]){
	     int list[] = {23, 35, 88, 43, 16, 9, 12, 55};
	     int size = list.length;
	     int start = 0;
	     int direction = UP;
	     
	     bitonicSort(list, size, start, direction);
	     
	     System.out.println(Arrays.toString(list));
	 }
	

}