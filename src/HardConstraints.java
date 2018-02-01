import java.util.ArrayList;

public class HardConstraints {
	public ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
	
	// Currently only prints console error message
	public ArrayList<ArrayList<String>> TooNear(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> tooNear) {
		if (forcedPairs.isEmpty()) {
			return returnList;
		}
		else {
			for (int i = 0; i < forcedPairs.size(); i++) {
				//Get pairing in list of forced pairs
				ArrayList<String> pairing = forcedPairs.get(i);
				int machine = Integer.parseInt(pairing.get(0));
				String task = pairing.get(1);
				//For each pairing in tooNear list, check if forced pair task is the same
				for (int j = 0; j < tooNear.size(); j++) {
					if (task.equals(tooNear.get(j).get(0)) || task.equals(tooNear.get(j).get(1))) {
						//If tasks are equal search forced pair list for new pairing with machine i+1
						for (int k = 0; k < forcedPairs.size(); k++) {
							ArrayList<String> pairingPlus = forcedPairs.get(k);
							int machinePlus = Integer.parseInt(pairingPlus.get(0));
							String taskPlus = pairingPlus.get(1);
							// If machine of the two pairings are i and i+1
							if (machinePlus == machine+1 || (machine == 8 && machinePlus == 1)) {
								// If task of new machine i+1 is in the too near pair print error
								if (taskPlus.equals(tooNear.get(j).get(0)) || taskPlus.equals(tooNear.get(j).get(1))) {
									System.out.println("Invalid forced pairs + near pairs");
								}
							}
						}
						ArrayList<String> ip = new ArrayList<String>();
						if (task.equals(tooNear.get(j).get(0))) {
							if ((machine + 1) == 9) {
								ip.add("1");
							}
							else {
								ip.add(Integer.toString(machine + 1));
							}
							ip.add(tooNear.get(j).get(1));
							returnList.add(ip);
						}
						else if (task.equals(tooNear.get(j).get(1))) {
							if ((machine - 1) == 0) {
								ip.add("8");
							}
							else {
								ip.add(Integer.toString(machine - 1));
							}
							ip.add(tooNear.get(j).get(0));
							returnList.add(ip);
						}
					}
				}
			}
		}
		return returnList;
	}
	
	public ArrayList<ArrayList<String>> eliminatePairs(ArrayList<ArrayList<String>> grid, ArrayList<ArrayList<String>> pairList) {
		for (int i = 0; i < pairList.size(); i++) {
			for (int j = 0; j < grid.size(); j++) {
				if (grid.get(j).get(0).equals(pairList.get(i).get(0)) && grid.get(j).get(1).equals(pairList.get(i).get(1))) {
					ArrayList<String> neg1 = new ArrayList<String>();
					neg1.add("-1");
					neg1.add("-1");
					grid.set(j, neg1);
				}
			}
		}
		return grid;
	}
}