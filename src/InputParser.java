/*To read input file for assignment. Will also parse and validate data, and throw appropriate errors
 *Author: Jacky Tang
 */
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class InputParser {
	
	private ArrayList<String> machines;
	private ArrayList<String> tasks;
	
	private String path;
	
	private ArrayList<ArrayList<String>> forcedPairs = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> forbiddenPairs = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> tooNearInvalid = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> tooNearPenalties = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> machinePenalties = new ArrayList<ArrayList<String>>();
	
	// Temporary main method to run program
	public static void main(String[] args) {
		
		InputParser parser = new InputParser("src/test.txt");
		try {
			parser.parseData();
		} catch (IOException ioe) {
			System.out.println("Error while parsing input file");
		}
	}
	
	private void printList(ArrayList<ArrayList<String>> list) {
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	// Constructor
	public InputParser (String filename) {
		this.path = filename;
		this.machines = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8"));
		this.tasks = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H"));
	}
	
	// Read through file and parse data from each line of text
	public void parseData() throws IOException {
		// File reading objects
		FileReader fr = new FileReader(path);
		BufferedReader reader = new BufferedReader(fr);
		// Read each line and check for label unless file ends
		String aLine;
	    while ((aLine = reader.readLine()) != null) {
	    	checkLabel(reader, aLine);
	    }
	}
	
	// Check for appropriate label and parse out values under each label
	private void checkLabel(BufferedReader reader, String aLine) {
		// Make lowercase for consistency in case checks
		String currentLine = aLine.toLowerCase();
		// For each label read values based on structure of text
		switch (currentLine) {
			case "forced partial assignment:":
				readValues(reader,aLine,forcedPairs,"tuples","m,t");
				break;
			case "forbidden machine:":
				readValues(reader,aLine,forbiddenPairs,"tuples","m,t");
				break;
			case "too-near tasks:":
				readValues(reader,aLine,tooNearInvalid,"tuples","t,t");
				break;
			case "too-near penalities":
				readValues(reader,aLine,tooNearPenalties,"tuples","t,t");
				break;
			case "machine penalties:":
				readValues(reader,aLine,machinePenalties,"grid","none");
				break;
		}
	}
	
	// Reads values in given section until it reaches a blank line or end of file
	private void readValues(BufferedReader reader, String aLine, 
			ArrayList<ArrayList<String>> list, String parseType, String tupleType) {
		try {
			// Keep reading next line unless it is blank or end of file
			while ((aLine = reader.readLine()) != null && !(aLine.replaceAll(" ", "").isEmpty())) {
				if (parseType == "tuples") {
					list.add(parseTuples(aLine,tupleType));
				} else { 
					list.add(parseGrid(aLine));
				}
			}
		} catch (IOException e) {
			System.out.println("Error while parsing input file");
		}
	}
	
	// Extracts values from tuples [eg.(1,2)] into integer arraylist
	private ArrayList<String> parseTuples(String aLine, String type) {
		aLine.replaceAll(" ", "");
		aLine = aLine.replaceAll("[()]", "");
		String[] values = aLine.split(",");		
		
		ArrayList<String> tuple = new ArrayList<String>();
		switch (type) {
			case "m,t":
				if (machines.contains(values[0]) && tasks.contains(values[1])) {
					for (int i=0; i<values.length; i++) {
						tuple.add(values[i]);
					}
				} else {
					System.out.println("Invalid machine/task");
				}
				break;
			case "t,t":
				if (tasks.contains(values[0]) && tasks.contains(values[1])) {
					for (int i=0; i<values.length; i++) {
						tuple.add(values[i]);
					}
				} else {
					System.out.println("Invalid machine/task");
				}
				break;
			default: break;
		}
		
		return tuple;
	}
	
	// Extracts values from grid separated by spaces into 2d arraylist
	private ArrayList<String> parseGrid(String aLine) {
		String[] values = aLine.split(" ");
		
		ArrayList<String> row = new ArrayList<String>();
		for (int i=0; i<values.length; i++) {
			values[i].replaceAll(" ", "");
			row.add(values[i]);
		}
		
		return row;
	}
	
	// Add to arraylist if able to parse number string
	private void validateNum(String numString, ArrayList<Integer> list) {
		try {
			int num = Integer.parseInt(numString);
			list.add(num);
		} catch (NumberFormatException nfe) {
			System.out.println("Error while parsing input file");
		}
	}
	  
}
