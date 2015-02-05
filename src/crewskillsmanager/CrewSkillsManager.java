package crewskillsmanager;

import java.util.ArrayList;
import java.util.List;

import crewskillsmanager.resourcemanager.InventoryManager;

public class CrewSkillsManager {
	private InventoryManager inventoryMgr;
	private List<String> missionsInProgress;
	private int changesSinceSave = 0;
	private final int CHANGESTOSAVE = 3;
	
	public CrewSkillsManager() { 
		inventoryMgr = new InventoryManager();
		String[] inProgress = inventoryMgr.getMissionsInProgress();
		missionsInProgress = new ArrayList<String>();
		for(int i=0; i<inProgress.length; i++){
			startMission(inProgress[i]);
		}
	
	}
	
	/**
	 * adds a crewskill to the in-progress list, given a valid id
	 * @param missionId
	 * @return
	 */
	public boolean startMission(String missionId){
		if(isValidMissionId(missionId)){
			missionsInProgress.add(missionId);
			madeChange();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * adds a crewskill to in-progress list based on all of the information about the crewskill.
	 * this method invokes genCrewSkillId with the parameters, and then calls by startMission(missionId)
	 * 
	 * @param op
	 * @param skill
	 * @param missionType
	 * @param missionYield
	 * @param cost
	 * @return
	 */
	public boolean startMission(char op, char skill, char missionType, byte missionYield, short cost){
		// generate mission id
		String missionId = genMissionId(op, skill, missionType, missionYield, cost);
		// add to list using mission id
		return startMission(missionId);
	}
	
	
	/**
	 * completes the given crew skill, and signifies that the return was nothing (it failed)
	 * @param missionId
	 * @return
	 */
	public boolean finishMission(String missionId){
		if(missionsInProgress.remove(missionId)){
			inventoryMgr.failedMission(missionId);
			
			madeChange();
			
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * completes the given crew skill, and notifies the resource manager of the single yield
	 * @param missionId
	 * @param yieldId
	 * @param yieldAmt
	 * @return
	 */
	public boolean finishMission(String missionId, String yieldId, int yieldAmt){
		if(missionsInProgress.remove(missionId)){
			inventoryMgr.addInventory(yieldId, yieldAmt);
			inventoryMgr.addCredits(missionId);
			inventoryMgr.addMission(missionId);
			madeChange();
			
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * completes the given crew skill, and notifies the resource manager of the multiple yields
	 * @param missionId
	 * @param yieldId
	 * @param yieldAmt
	 * @return
	 */
	public boolean finishMission(String missionId, String[] yieldId, int[] yieldAmt){
		// if the amount of unique yields == the amount of unique amounts AND
		// if the mission is currently in the list of in-progress missions
		
		if(yieldId.length==yieldAmt.length && missionsInProgress.remove(missionId)){
			inventoryMgr.addCredits(missionId);
			inventoryMgr.addMission(missionId);
			for(int i=0; i<yieldId.length; i++){
				inventoryMgr.addInventory(yieldId[i], yieldAmt[i]);
			}
			madeChange();
			
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * signifies "cancelling" a crewskill, i.e. the crewskill didn't finish, and no resources were consumed
	 * @param missionId
	 * @return
	 */
	public boolean removeMission(String missionId){
		return missionsInProgress.remove(missionId);
	}
	
	/**
	 * generates a crewskill mission id based on the parameters
	 * @param op
	 * @param skill
	 * @param missionType
	 * @param missionYield
	 * @param cost
	 * @return
	 */
	public String genMissionId(char op, char skill, char missionType, byte missionYield, short cost){
		StringBuilder sb = new StringBuilder();
		sb.append(op);
		sb.append(skill);
		sb.append(missionType);
		sb.append(missionYield);
		sb.append(cost);
		return sb.toString();
	}
	
	/**
	 * returns true if given mission id is valid
	 * @param missionId
	 * @return
	 */
	public boolean isValidMissionId(String missionId){
		String regex = "([cg])([abslditu])([a-z])([1-4])\\d*";
		return missionId.matches(regex);
	}
	
	
	/**
	 * returns an array representation of the missionsInProgress list, or an empty array if there are
	 * no missions in progress
	 * @return
	 */
	public String[] missionListToArray(){
		if(missionsInProgress.size() > 0){
			// generate array of ids in progress
			String[] missionIds = new String[missionsInProgress.size()];
			for(int i=0; i<missionsInProgress.size(); i++){
				missionIds[i] = missionsInProgress.get(i);
			}
			return missionIds;
		}else{
			return new String[]{};
		}
		
		
	}
	/**
	 * closes the resource manager/does final sync
	 */
	public void close(){
		// if we have missions in progress
		if(missionsInProgress.size() > 0){
			inventoryMgr.close(missionListToArray());
		}else{
			inventoryMgr.close();
		}
	}
	
	
	/**
	 * Increases the number of changes since save, and saves if we've reached threshold
	 */
	private void madeChange(){
		// if the number of changes we've made is higher than our save threshold
		changesSinceSave++;
		if(changesSinceSave == CHANGESTOSAVE){ 
			inventoryMgr.syncConfig();
			changesSinceSave = 0;
		}
		
	}
}
