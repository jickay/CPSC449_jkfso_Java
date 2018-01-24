import java.io.FileNotFoundException;
import java.io.PrintWriter;

/* This class is for printing the output solution to a file.
 * To use to print a solution type:
 * 		Output op = new Output(testSol, testQual);
		op.print();
	To print that there is no solution:
		Output op = new Output();
		op.print();
 */

public class Output {
	public String[] solution;
	public int quality;
	
	public Output(String[] sol, int qual) {
		solution = sol;
		quality = qual;
	}
	
	public Output() {
		solution = new String[0];
		quality = 0;
	}
	
/* This method takes two arguments, a String array that should contain 
 * the eight tasks in order for each machine (first task in list is assigned
 * to the machine 1, second task to machine 2, etc), and an int representing
 * the quality of the solution.
 * 
 * It writes this information to a text file named Output
 */
	public void print() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("Output.txt");
		}
		catch (FileNotFoundException fnfe) {
			System.out.println("fnf");
		}
		if (solution.length == 0) {
			writer.print("No valid solution possible!");
		}
		else {
			writer.print("\"Solution\"");
			for (int i=0; i<solution.length;i++) {
				writer.print(solution[i]);
				writer.print(" ");
			}
			writer.print("; Quality:\" ");
			writer.print(quality);
		}
		writer.close();
	}
}
