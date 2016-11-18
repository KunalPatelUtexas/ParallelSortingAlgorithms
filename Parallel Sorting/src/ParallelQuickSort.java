import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelQuickSort {
	
	private int[] array;
	
	
	public ParallelQuickSort(int[] a) {
		this.array = a;
	}
	
	public class QuickSortRunnable implements Runnable {		
		private int[] array;
		ExecutorService pool;
		Vector<Future<Integer>> futures;
		private int split;
		private int initial_start;
		private int initial_end;
		
		public QuickSortRunnable(ExecutorService apool, Vector<Future<Integer>> somefutures, int[] a, int s, int e, int numThreads) {
			this.array = a;
			this.pool = apool;
			this.futures = somefutures;
			this.split = a.length/numThreads;
			this.initial_end = e;
			this.initial_start = s;
		}
		
		private void sort(final int a[], int start, int end) {
			int piv = this.array[0];
			int left = start;
			int right = end;
		    boolean goRight = true;
			
			while (left != right) {
				if (goRight) {
					if (array[right] < piv) {
						array[left] = array[right];           
						left++;           
						goRight = false;         
					} else {           
						right--;         
					}       
				} else if (goRight == false) {         
					if (array[left] > piv) {
						array[right] = array[left];
					    right--;
					    goRight = true;
					} else {
						left++;
					}
				}
			}
			array[left] = piv;
			
			if((left - start) > 1) {
				if ((left - start) > split) {
					Integer result = new Integer(0);
					futures.add(pool.submit(new QuickSortRunnable(pool, futures, array, start, left-1, split), result));
				} else {
					sort(array, start, left);
				}
			}
			
			if((end - left) > 1) {
				if((end - left) > split) {
					Integer result = new Integer(0);
					futures.add(pool.submit(new QuickSortRunnable(pool, futures, array, left+1, end, split), result));
				} else {
					sort(array, left+1, end);
				}
			}
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sort(this.array, this.initial_start, this.initial_end);
		}		
	}
	
	public long start() {
		
		//int cores = Runtime.getRuntime().availableProcessors();
		int cores = 10;
		final ExecutorService pool = Executors.newFixedThreadPool(cores);
		
		Vector<Future<Integer>> futures = new Vector<Future<Integer>>();
		QuickSortRunnable runnable = new QuickSortRunnable(pool, futures, this.array, 0, this.array.length-1, cores);
		
		// get start time in nano seconds
		long time = System.nanoTime();
		
		Integer result = new Integer(0);
		futures.add(pool.submit(runnable, result));
		
		while(!futures.isEmpty()) {
			Future<Integer> future = futures.remove(0);
			try {
				if(future != null) {
					future.get();
				}
			} catch(InterruptedException exception) {
				//System.out.println("Interrupted Exception Thrown");
			} catch(ExecutionException exception) {
				//System.out.println("Execution Exception Thrown");
			}
		}
		// return time it took to run in nano seconds
		time = System.nanoTime() - time;
		pool.shutdown();
		return time;
	}
	/*
	public static void main(String [] args) {
		int LENGTH = 200000;
		Random rand = new Random(42);
		int[] a = new int[LENGTH];
		for (int i=0; i<LENGTH; i++) {
			a[i] = rand.nextInt(LENGTH);
		}
		// How to Execute - below
		ParallelQuickSort pqs = new ParallelQuickSort(a);
		long timeNano = pqs.start();
		// How to Excute - above
		System.out.println("time = " + timeNano);
	}
	*/
}
