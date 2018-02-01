import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Scheduler {
	
	private ArrayList<String> machines = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8"));
	private ArrayList<String> tasks = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H"));
	
	private ArrayList<ArrayList<String>> forcedPairs = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> forbiddenPairs = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> tooNearInvalid = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> tooNearPenalties = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<Integer>> machinePenalties = new ArrayList<ArrayList<Integer>>();
	
	private ArrayList<String> closedPairs = new ArrayList<String>();
	private String[] finishedPairs = new String[8];
	
	// Getter methods
	public ArrayList<String> getMachines() { return machines; }
	public ArrayList<String> getTasks() { return tasks;	}
	public ArrayList<String> getClosed() { return closedPairs; }
	public String[] getFinished() { return finishedPairs; }

	public ArrayList<ArrayList<String>> getForcedPairs() { ArrayList<ArrayList<String>> copy = forcedPairs; return copy; }
	public ArrayList<ArrayList<String>> getForbiddenPairs() { ArrayList<ArrayList<String>> copy = forbiddenPairs; return copy; }
	public ArrayList<ArrayList<String>> getTooNearInvalid() { ArrayList<ArrayList<String>> copy = tooNearInvalid; return copy; }
	public ArrayList<ArrayList<String>> getTooNearPenalties() { ArrayList<ArrayList<String>> copy = tooNearPenalties; return copy; }
	public ArrayList<ArrayList<Integer>> getMachinePenalties() { ArrayList<ArrayList<Integer>> copy = machinePenalties; return copy; }
	
	// Setter methods
	public void setForcedPairs(ArrayList<ArrayList<String>> list) { forcedPairs = list; }
	public void setForbiddenPairs(ArrayList<ArrayList<String>> list) { forbiddenPairs = list; }
	public void setTooNearInvalid(ArrayList<ArrayList<String>> list) { tooNearInvalid = list; }
	public void setTooNearPenalties(ArrayList<ArrayList<String>> list) { tooNearPenalties = list; }
	public void setMachinePenalties(ArrayList<ArrayList<Integer>> list) { machinePenalties = list; }


	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		
		// Parse data into lists
		InputParser parser = new InputParser("src/test.txt", scheduler);
		try {
			parser.parseData();
		} catch (IOException ioe) {
			System.out.println("Error while parsing input file");
		}
		
	}
	
	public void printLists() {
		printList(forcedPairs);
		printList(forbiddenPairs);
		printList(tooNearInvalid);
		printList(tooNearPenalties);
		printListInts(machinePenalties);
	}
	
	private void printList(ArrayList<ArrayList<String>> list) {
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	private void printListInts(ArrayList<ArrayList<Integer>> list) {
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		}
	}

}