import java.io.FileNotFoundException;
import java.io.PrintWriter;

/* This class is for printing the output solution to a file.
 * To use to print a solution type:
 * 		Output op = new Output(outputfilename, testSol, testQual);
		op.print();
	To print that there is no solution:
		Output op = new Output(outputfilename);
		op.print();
 */

public class Output {
	private String[] solution;
	private int quality;
	private String filename;
	private String zero = "partial assignment error";
	private String one = "invalid machine task";
	private String two = "machine penalty error";
	private String three = "invalid task";
	private String four = "invalid penalty";
	private String five = "Error while parsing input file";
	
	public Output(String outputFilename, String[] sol, int qual) {
		solution = sol;
		quality = qual;
		filename = outputFilename;
	}
	
	public Output(String outputFilename) {
		solution = new String[0];
		quality = 0;
		filename = outputFilename;
	}
	
	public void printError(int choice) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException fnfe) {
			System.out.println("fnf");
		}
		if (choice == 0) {
			writer.print(zero);
		}
		else if (choice == 1) {
			writer.print(one);
		}
		else if (choice == 2) {
			writer.print(two);
		}
		else if (choice == 3) {
			writer.print(three);
		}
		else if (choice == 4) {
			writer.print(four);
		}
		else if (choice == 5) {
			writer.print(five);
		}
		writer.close();
	}
	
/* Creates a new text document using the String filename as the title. If there is
 * a solution specified when the instance is created it will print it in the same
 * format as given on the website. If no solution is given it prints that there is
 * no solution
 */
	public void print() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException fnfe) {
			System.out.println("fnf");
		}
		if (solution.length == 0) {
			writer.print("No valid solution possible!");
		}
		else {
			writer.print("\"Solution\" ");
			for (int i=0; i<solution.length; i++) {
				writer.print(solution[i]);
				writer.print(" ");
			}
			writer.print("; Quality:\" ");
			writer.print(quality);
		}
		writer.close();
	}
}
