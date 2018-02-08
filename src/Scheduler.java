import java.io.IOException;
import java.io.*;
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
	public void mainMethod(String[] args) {
//		// Get input arguments from console
//		try {
//			inputFileName = args[0];
//			outputFileName = args[1];
//		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("Error while determining input and output files");
//			System.exit(0);
//		}
//		
		// Create start conditions
		Scheduler s = new Scheduler();
		
		// Parse data into lists
		inputFileName = args[0];
		outputFileName = args[1];

		InputParser parser = new InputParser(inputFileName, s);
		try {
			parser.parseData();
		} catch (IOException ioe) {
			System.out.println("Error while parsing input file");
		}
		
		// Complete hard constraints
		HardConstraints hc = new HardConstraints();
		s.setForcedPairs(hc.forcedPartialAssignment(s.getForcedPairs()));
		s.setForbiddenPairs(hc.forbiddenMachine(s.getForcedPairs(), s.getForbiddenPairs()));
		ArrayList<ArrayList<String>> invalidPairs = hc.tooNear(s.getForcedPairs(), s.getTooNearInvalid());
		hc.addForbidden(s.getForbiddenPairs(), invalidPairs);
//		hc.eliminatePairs(s.getMachinePenalties(), s.getMachines(), s.getTasks(), s.getForbiddenPairs());
		
		// Create matches in the finished array
		for (ArrayList<String> forced : s.getForcedPairs()) {
			// Check if forced pair violates too-near conditions
			String machine = forced.get(0);
			String task = forced.get(1);
			boolean isValid = hc.isValidPair(s.getForbiddenPairs(),machine,task);
			// Make match if valid
			if (isValid) {
				int mach = s.getMachines().indexOf(machine);
				s.getFinished()[mach] = task;
				// Add new too-near invalids created by new match
				ArrayList<ArrayList<String>> newPair = new ArrayList<ArrayList<String>>();
				ArrayList<String> pair = new ArrayList<String>(Arrays.asList(machine,task));
				newPair.add(pair);
				hc.addForbidden(s.getForbiddenPairs(),hc.tooNear(s.getForcedPairs(),newPair));
			}
		}
		
		// Complete soft constraints
		SoftConstraints sc = new SoftConstraints();
		s.setFinished(sc.setPenalties(s, hc, s.getFinished()));
		
		// Generate output file
		String[] solution = s.getFinished();
		int quality = sc.getTotalPenalties();
		System.out.println("Solution: ");
		s.printArray(solution);
		System.out.println("Quality: " + quality);
		if (solution.length > 0) { 
			Output o = new Output(outputFileName, solution, quality);
			o.print();
		} else { 
			Output o = new Output(outputFileName); 
			o.print(); 
		}
	}
	
	public void printArray(String[] array) {
		for (int i=0; i<array.length; i++) {
			System.out.print(array[i] + ", ");
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