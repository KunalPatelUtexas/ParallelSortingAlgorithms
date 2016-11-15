import java.util.*;
import java.util.concurrent.*;


class RadixSort{
	
	static CyclicBarrier wait, finished, sync;
	static int[] a, b;
	static int numThreads;
    static int[]localMax ;
    static int allCount[][];

	public static long start(int[] a) {
		long startTime = System.nanoTime();
		new RadixSort().sort(a);
		long endTime = System.nanoTime();
		return (endTime - startTime);     
	 }

	 void sort(int[] a){
		try{
			numThreads = Runtime.getRuntime().availableProcessors();
			wait = new CyclicBarrier(numThreads+1);
			finished = new CyclicBarrier(numThreads+1);
			sync = new CyclicBarrier(numThreads);
			localMax = new int[numThreads];
			allCount = new int[numThreads][];
			this.a = a;
			b = new int[a.length];

			for (int i = 0; i< numThreads; i++){
				 (new Thread(new Radix(i))).start();
			}
			wait.await();	
			finished.await();
			
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	 }


	 class Radix implements Runnable{
		 int index = 0;
		 int[] localCount;
		 int myMax;
		 int left, right;
		 int bit1, bit2;

		 Radix(int i) { 
		   index = i;
		 }
		   
		 void findLocalMax(){
			 int num = (a.length/numThreads);
			 left = num*index;
			 myMax = a[left];
			 right = left + num;
			 if (index == (numThreads - 1)){
				 right = a.length;
			 }

			 for (int i = left+1; i < right; i++) {
				 if (a[i] > myMax){
					 myMax = a[i];
				 }
			 }
			 localMax[index] = myMax;
		}

	    void paraRadix(int[] a, int[] b) {
	    	try {
	    		int numBit = 2;
	    		int n = a.length;
		         
		        while (myMax >= (1<<numBit)){
		        	 numBit++;
		        }
		        bit1 = numBit/2;
		        bit2 = numBit - bit1;

		        paraRadixSort(a, b, bit1, 0);
		        sync.await();
		        paraRadixSort(b, a, bit2, bit1);
		         
	        } catch (Exception e) {
				e.printStackTrace(System.out);
	        }
	    }

		void paraRadixSort(int[] a, int[] b, int maskLen, int shift){
			try {
				int mask = (1<<maskLen) - 1;
		        localCount = new int[mask + 1];
		        int sum = 0;

		        for (int i = left; i < right; i++) {
		        	localCount[(a[i]>> shift) & mask]++;
		        }
		        allCount[index] = localCount;

			    sync.await();
				 
			    localCount = new int[localCount.length];

		        for (int val = 0; val < mask+1; val++) {
		        	for (int i = 0; i < index; i++) {
		        		sum += allCount[i][val];
					}
					localCount [val] = sum;
					for (int i = index; i < numThreads; i++) {
						sum += allCount[i][val];
					}
			    }

			    for (int i = left; i < right; i++) {
		        	b[localCount[(a[i]>>shift) & mask]++] = a[i];
		        }
		    } catch (Exception e) {
				e.printStackTrace(System.out);
		    }
        }


	       public void run() {
	    	   try {
	    		   wait.await();
	    		   findLocalMax();   
	    		   sync.await();
			   	   // all threads should have same max
	    		   for (int i = 0; i < numThreads; i++){	
	    			   if (myMax < localMax[i]){
	    				   myMax = localMax[i];
	    			   }	
	    		   }
	    		   paraRadix(a,b);
                       
	    		   // wait on main and all other thread
	    		   finished.await();
			   } catch (Exception e) {
					e.printStackTrace(System.out);
			   }
	       }
     }
}