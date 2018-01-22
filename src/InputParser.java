/*To read input file for assignment. Will also parse and validate data, and throw appropriate errors
 *Author: Jacky Tang
 */
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class InputParser {
	
	private String path;
//	private Charset ENCODING = StandardCharsets.UTF_8;
	
	private ArrayList<ArrayList<Integer>> forcedPairs = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> forbiddenPairs = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> tooNearInvalid = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> tooNearPenalties = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> machinePenalties = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) {
		InputParser parser = new InputParser("src/test.txt");
		try {
			parser.parseData();
		} catch (IOException ioe) {
			System.out.println("Error reading file");
		}
	}
	
	public InputParser (String filename) {
		this.path = filename;
	}
	
	public void parseData() throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader reader = new BufferedReader(fr);
		
		String aLine;
	    while ((aLine = reader.readLine()) != null) {
	    	checkLabel(reader, aLine);
	    }
	}
	
	private void checkLabel(BufferedReader reader, String aLine) {
		String currentLine = aLine.toLowerCase();
		switch (currentLine) {
			case "forced partial assignment:":
				readValues(reader,aLine,forcedPairs,"tuples");
				break;
			case "forbidden machine:":
				readValues(reader,aLine,forbiddenPairs,"tuples");
				break;
			case "too-near tasks:":
				readValues(reader,aLine,tooNearInvalid,"tuples");
				break;
			case "too-near penalities":
				readValues(reader,aLine,tooNearPenalties,"tuples");
				break;
			case "machine penalties:":
				readValues(reader,aLine,machinePenalties,"grid");
				break;
		}
	}
	
	private void readValues(BufferedReader reader, String aLine, ArrayList<ArrayList<Integer>> list, String parseType) {
		try {
			while ((aLine = reader.readLine()) != null && !(aLine.replaceAll(" ", "").isEmpty())) {
				if (parseType == "tuples") { list.add(parseTuples(aLine)); }
				else { list.add(parseGrid(aLine)); }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ArrayList<Integer> parseTuples(String aLine) {
		aLine.replaceAll(" ", "");
		aLine = aLine.replaceAll("[()]", "");
		String[] nums = aLine.split(",");
		
		ArrayList<Integer> tuple = new ArrayList<Integer>();
		for (int i=0; i<nums.length; i++) {
			tuple.add(Integer.parseInt(nums[i]));
		}
		
		return tuple;
	}
	
	private ArrayList<Integer> parseGrid(String aLine) {
		String[] nums = aLine.split(" ");
		
		ArrayList<Integer> row = new ArrayList<Integer>();
		for (int i=0; i<nums.length; i++) {
			nums[i].replaceAll(" ", "");
			row.add(Integer.parseInt(nums[i]));
		}
		
		return row;
	}
	  
}
