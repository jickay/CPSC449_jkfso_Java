import java.util.ArrayList;

public class HardConstraints {
	public ArrayList<ArrayList<String>> returnList;
	
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
					}
				}
			}
		}
		return returnList;
	}
}
