/*To read input file for assignment. Will also parse and validate data, and throw appropriate errors
 *Author: Jacky Tang
 */
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class InputParser {
	
	private String path;
	private Scheduler scheduler;
	
		
	private void printList(ArrayList<ArrayList<String>> list) {
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
			System.exit(0);
		}
	}
	
	// Constructor
	public InputParser (String filename, Scheduler scheduler) {
		this.path = filename;
		this.scheduler = scheduler;
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
	    scheduler.printLists();
	}
	
	// Check for appropriate label and parse out values under each label
	private void checkLabel(BufferedReader reader, String aLine) {
		// Make lowercase for consistency in case checks
		String currentLine = aLine.toLowerCase();
		// For each label read values based on structure of text
		switch (currentLine) {
			case "forced partial assignment:":
				scheduler.setForcedPairs(readTupleValues(reader,aLine,scheduler.getForcedPairs(),"tuples","m,t"));
				break;
			case "forbidden machine:":
				scheduler.setForbiddenPairs(readTupleValues(reader,aLine,scheduler.getForbiddenPairs(),"tuples","m,t"));
				break;
			case "too-near tasks:":
				scheduler.setTooNearInvalid(readTupleValues(reader,aLine,scheduler.getTooNearInvalid(),"tuples","t,t"));
				break;
			case "too-near penalities":
				scheduler.setTooNearPenalties(readTupleValues(reader,aLine,scheduler.getTooNearPenalties(),"tuples","t,t"));
				break;
			case "machine penalties:":
				scheduler.setMachinePenalties(readGridValues(reader,aLine,scheduler.getMachinePenalties(),"grid","none"));
				break;
		}
	}
	
	// Reads values in given section until it reaches a blank line or end of file
	private ArrayList<ArrayList<String>> readTupleValues(BufferedReader reader, String aLine, 
			ArrayList<ArrayList<String>> list, String parseType, String tupleType) {
		try {
			// Keep reading next line unless it is blank or end of file
			while ((aLine = reader.readLine()) != null && !(aLine.replaceAll(" ", "").isEmpty())) {
				list.add(parseTuples(aLine,tupleType));
			}
		} catch (IOException e) {
			System.out.println("Error while parsing input file");
			System.exit(0);
		}
		return list;
	}
	
	private ArrayList<ArrayList<Integer>> readGridValues(BufferedReader reader, String aLine, 
			ArrayList<ArrayList<Integer>> list, String parseType, String tupleType) {
		try {
			// Keep reading next line unless it is blank or end of file
			while ((aLine = reader.readLine()) != null && !(aLine.replaceAll(" ", "").isEmpty())) {
				list.add(parseGrid(aLine));
			}
		} catch (IOException e) {
			System.out.println("Error while parsing input file");
			System.exit(0);
		}
		return list;
	}
	
	// Extracts values from tuples [eg.(1,2)] into integer arraylist
	private ArrayList<String> parseTuples(String aLine, String type) {
		aLine.replaceAll(" ", "");
		aLine = aLine.replaceAll("[()]", "");
		String[] values = aLine.split(",");		
		
		ArrayList<String> tuple = new ArrayList<String>();
		switch (type) {
			case "m,t":
				if (scheduler.getMachines().contains(values[0]) && scheduler.getTasks().contains(values[1])) {
					for (int i=0; i<values.length; i++) {
						tuple.add(values[i]);
					}
				} else {
					System.out.println("Invalid machine/task");
					System.exit(0);
				}
				break;
			case "t,t":
				if (scheduler.getTasks().contains(values[0]) && scheduler.getTasks().contains(values[1])) {
					for (int i=0; i<values.length; i++) {
						tuple.add(values[i]);
					}
				} else {
					System.out.println("Invalid machine/task");
					System.exit(0);
				}
				break;
			default: break;
		}
		
		return tuple;
	}
	
	// Extracts values from grid separated by spaces into 2d arraylist
	private ArrayList<Integer> parseGrid(String aLine) {
		String[] values = aLine.split(" ");
		
		ArrayList<Integer> row = new ArrayList<Integer>();
		for (int i=0; i<values.length; i++) {
			values[i].replaceAll(" ", "");
			validateNum(values[i],row);
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
			System.exit(0);
		}
	}
	
//	// Add to arraylist if able to parse number string
//	private ArrayList<ArrayList<Integer>> stringListToInt(ArrayList<ArrayList<String>> list) {
//		ArrayList<ArrayList<Integer>> intList = new ArrayList<ArrayList<Integer>>();
//		try {
//			for (int i=0; i<list.size(); i++) {
//				ArrayList<String> row = list.get(i);
//				intList.add(new ArrayList<Integer>());
//				for (int j=0; j<row.size(); j++) {
//					int num = Integer.parseInt(row.get(j));
//					intList.get(i).set(j,num);
//				}
//			}
//		} catch (NumberFormatException nfe) {
//			System.out.println("Error while parsing input file");
//			System.exit(0);
//		}
//		return intList;
//	}
	  
}
 