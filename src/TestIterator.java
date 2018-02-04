import java.util.Scanner;

/**
 * Test Iterator: Alternate main() method to run "usual" program repeatedly on separate inputs
 * Generates filepath i/o names
 * Prompts user for filepath and range of tests to run
 * BEFORE RUNNING: replace typical Scheduler main method with temporary name and uncomment class body
 */
public class TestIterator{
	
	public static void main(String[] args){

		Scanner keyboard = new Scanner(System.in);
		
		// Set file folder to run
//		System.out.println("**STEP 1**: Determine filepath:");
//		System.out.print("Enter the filepath to the tests' folder (no fwd slash at end): ");
//		String filePath = keyboard.nextLine();
//		filePath = filePath + "/test";
//		System.out.println("");
		
		String filePath = "TestFiles/test";
		
		// Set range of tests to run through (0 - 45)
//		System.out.println("**STEP 2**: Enter the values of the tests (0-45):");
//		System.out.print("First test to run: ");
//		int firstTest = keyboard.nextInt();
//		System.out.print("Last test to run: ");
//		int lastTest = keyboard.nextInt();		
//		System.out.println("");	
		
		int firstTest = 0;
		int lastTest = 45;
		
		System.out.println("RUNNING TESTS #" + firstTest + " THROUGH #" + lastTest);
		System.out.println("||||||||||||||||||||||||||||");
		
		for(int i=firstTest; i<=lastTest; i++){
			
			String arg1 = filePath + Integer.toString(i) + ".txt";			//input argument
			String arg2 = filePath + Integer.toString(i) + "_OUTPUT.txt";	//output argument
					
			String[] testArgs = new String[2];
			testArgs[0] = arg1;
			testArgs[1] = arg2;
			
			System.out.println("||||||||||||||||||||||||||||");
			System.out.println("RUNNING TEST #" + i + " ---> (" + arg1 + ")");
			
			Scheduler s = new Scheduler();
			try {
				s.mainMethod(testArgs);
			} catch (IndexOutOfBoundsException iobe) {
				System.out.println("TEST CAUSES ERROR");
			}
			
			System.out.print("Hit Enter to continue");
			keyboard.nextLine();
		}
	}	
	
}