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

	private static String inputFileName;
	private static String outputFileName;

	
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
	public void setFinished(String[] matches) { finishedPairs = matches; }
	
	public void setForcedPairs(ArrayList<ArrayList<String>> list) { forcedPairs = list; }
	public void setForbiddenPairs(ArrayList<ArrayList<String>> list) { forbiddenPairs = list; }
	public void setTooNearInvalid(ArrayList<ArrayList<String>> list) { tooNearInvalid = list; }
	public void setTooNearPenalties(ArrayList<ArrayList<String>> list) { tooNearPenalties = list; }
	public void setMachinePenalties(ArrayList<ArrayList<Integer>> list) { machinePenalties = list; }

	//Command line args assumes input filename is first argument and output is second any other is not used
	public static void main(String[] args) {
		// Run test cases
//				TestIterator test = new TestIterator();
//				test.runTests();
				
		// Get input arguments from console
		try {
			inputFileName = args[0];
			outputFileName = args[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error while determining input and output files");
			System.exit(0);
		}
		
		// Create start conditions
		Scheduler scheduler = new Scheduler();
		for (int i=0; i<8; i++) {
			scheduler.closedPairs.addAll(new ArrayList<String>());
		}
		
		// Parse data into lists
		InputParser parser = new InputParser(inputFileName, scheduler);
		try {
			parser.parseData();
		} catch (IOException ioe) {
			System.out.println("Error while parsing input file");
		}
		
		// Complete hard constraints
		HardConstraints hc = new HardConstraints();
		scheduler.setForcedPairs(hc.forcedPartialAssignment(scheduler.getForcedPairs()));
		scheduler.setForbiddenPairs(hc.forbiddenMachine(scheduler.getForcedPairs(), scheduler.getForbiddenPairs()));
		ArrayList<ArrayList<String>> invalidPairs = hc.tooNear(scheduler.getForcedPairs(), scheduler.getTooNearInvalid());
		hc.addForbidden(scheduler.getForbiddenPairs(), invalidPairs);
		
		// Create simple array of matches
		for (ArrayList<String> forced : scheduler.getForcedPairs()) {
			int mach = Integer.parseInt(forced.get(0));
			scheduler.getFinished()[mach] = forced.get(1);
		}
		
		// Complete soft constraints
		SoftConstraints sc = new SoftConstraints();
		scheduler.setFinished(sc.setPenalties(scheduler, hc));
		
		// Generate output file
		String[] solution = scheduler.getFinished();
		int quality = sc.getTotalPenalties();
		if (solution.length > 0) { 
			Output o = new Output(outputFileName, solution, quality); 
		} else { 
			Output o = new Output(outputFileName); 
			o.print(); 
		}
	}
	
	public void printLists() {
		System.out.println("Forced");
		printList(forcedPairs);
		System.out.println("Forbidden");
		printList(forbiddenPairs);
		System.out.println("TooNearInvalid");
		printList(tooNearInvalid);
		System.out.println("TooNearPenalties");
		printList(tooNearPenalties);
		System.out.println("PenaltyGrid");
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