import java.util.ArrayList;
import java.util.Random;


public class Driver {

	public static void main(String[] args) {

		int LENGTH = 10;
	    Random randomGenerator = new Random();
	    int[] arrayMerge = new int[LENGTH];
	    int[] arrayRadix = new int[LENGTH];
	    
	    for (int i = 0; i < LENGTH; i++){
	      int randomInt = randomGenerator.nextInt(1000000);
	      arrayMerge[i] = randomInt;
	      arrayRadix[i] = randomInt;
	    }
	    
	    try {
	    	
			long RadixTime = RadixSort.start(arrayRadix);
			long MergeTime = MergeSort.start(arrayMerge);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

	   /*
	    boolean flag = false;
	    for(int i = 0; i < LENGTH; i++){
	    	if(arrayMerge[i] != arrayRadix[i]){
	    		flag = true;
	    	}
	    }
	    if(flag){
    		System.out.println("Arrays Do Not Match.");
	    }else{
    		System.out.println("Arrays Match.");
	    }
	  */
	}

}
