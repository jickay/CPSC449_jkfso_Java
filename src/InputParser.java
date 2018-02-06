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
			//System.exit(0);
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
	    // Check if grid is empty
	    if (scheduler.getMachinePenalties().size() != 8) {
	    	System.out.println("machine penalty error");
	    	// System.exit(0);
	    }
	    // Print parsed lists
	    scheduler.printLists();
	}
	
	// Check for appropriate label and parse out values under each label
	private void checkLabel(BufferedReader reader, String aLine) {
		// Make lowercase for consistency in case checks
		String currentLine = aLine.toLowerCase();
		currentLine = currentLine.trim();
		// For each label read values based on structure of text
		ArrayList<String> labels = new ArrayList<String>(Arrays.asList(
					"forced partial assignment:",
					"forbidden machine:",
					"too-near tasks:",
					"machine penalties:",
					"too-near penalities"
				));
		switch (currentLine) {
			case "forced partial assignment:":
				scheduler.setForcedPairs(readTupleValues(reader,labels,1,aLine,scheduler.getForcedPairs(),"tuples","m,t"));
				break;
			case "forbidden machine:":
				scheduler.setForbiddenPairs(readTupleValues(reader,labels,2,aLine,scheduler.getForbiddenPairs(),"tuples","m,t"));
				break;
			case "too-near tasks:":
				scheduler.setTooNearInvalid(readTupleValues(reader,labels,3,aLine,scheduler.getTooNearInvalid(),"tuples","t,t"));
				break;
			case "too-near penalities":
				scheduler.setTooNearPenalties(readTupleValues(reader,labels,5,aLine,scheduler.getTooNearPenalties(),"tuples","t,t,q"));
				break;
			case "machine penalties:":
				scheduler.setMachinePenalties(readGridValues(reader,labels,4,aLine,scheduler.getMachinePenalties(),"grid","none"));
				break;
		}
	}
	
	// Reads values in given section until it reaches a blank line or end of file
	private ArrayList<ArrayList<String>> readTupleValues(BufferedReader reader, 
			ArrayList<String> labels, int nextLabelIndex,
			String aLine, ArrayList<ArrayList<String>> list, String parseType, String tupleType) {
		try {
			// Mark reader to return if next label is found
			reader.mark(1000);
			// Keep reading next line unless it is blank or end of file
			if (nextLabelIndex == 5) {
				while ((aLine = reader.readLine()) != null) {
					if (!aLine.isEmpty()) {
						aLine = aLine.trim();
						list.add(parseTuples(aLine,tupleType));
					}
				}
			} else if (nextLabelIndex < 4) {
				while ((aLine = reader.readLine()) != null && !aLine.matches(labels.get(nextLabelIndex))) {
					if (!aLine.isEmpty()) {
						aLine = aLine.trim();
						list.add(parseTuples(aLine,tupleType));
					}
				}
				reader.reset();
			}
		} catch (IOException e) {
			System.out.println("Error while parsing input file");
			//System.exit(0);
		}
		return list;
	}
	
	private ArrayList<ArrayList<Integer>> readGridValues(BufferedReader reader, 
			ArrayList<String> labels, int nextLabelIndex, String aLine, 
			ArrayList<ArrayList<Integer>> list, String parseType, String tupleType) {
		try {
			// Mark reader to return if next label is found
			reader.mark(1000);
			// Keep reading next line unless it is blank or end of file
			ArrayList<String> gridString = new ArrayList<String>();
			while ((aLine = reader.readLine()) != null && !aLine.matches(labels.get(nextLabelIndex))) {
				// Add all non-empty lines into grid
				if (!(aLine.replaceAll(" ", "").isEmpty())) {
					gridString.add(aLine);
				}
			}
			reader.reset();
			// Parse only if 8 rows/machines available
			if (gridString.size() == 8) {
				// Iterate over each line in grid
				for (String gridLine : gridString) {
					list.add(parseGrid(gridLine));
				}
			} else {
				System.out.println("machine penalty error");
				//System.exit(0);
			}
		} catch (IOException e) {
			System.out.println("Error while parsing input file");
			//System.exit(0);
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
					//System.exit(0);
				}
				break;
			case "t,t":
				if (scheduler.getTasks().contains(values[0]) && scheduler.getTasks().contains(values[1])) {
					for (int i=0; i<values.length; i++) {
						if (i == 2) { validateNum(values[i]); }
						tuple.add(values[i]);
					}
				} else {
					System.out.println("Invalid machine/task");
					//System.exit(0);
				}
				break;
			default: break;
		}
		
		return tuple;
	}
	
	// Extracts values from grid separated by spaces into 2d arraylist
	private ArrayList<Integer> parseGrid(String aLine) {
		ArrayList<Integer> row = new ArrayList<Integer>();
		aLine = aLine.trim();
		String[] values = aLine.split(" ");
		if (values.length == 8) {
			for (int i=0; i<values.length; i++) {
				values[i].replaceAll(" ", "");
				row.add(validateNum(values[i]));
			}
		} else {
			System.out.println("machine penalty error");
			//System.exit(0);
		}
		
		return row;
	}
	
	// Add to arraylist if able to parse number string
	private int validateNum(String numString) {
		int num = -1;
		try {
			num = Integer.parseInt(numString);
			// Check if natural number
			if (num < 0) {
				System.out.println("Error while parsing input file");
				//System.exit(0);
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Error while parsing input file");
			//System.exit(0);
		}
		return num;
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
//			//System.exit(0);
//		}
//		return intList;
//	}
	  
}
 