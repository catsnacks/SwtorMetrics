package crewskillsmanager.resourcemanager;

import java.util.HashMap;

import crewskillsmanager.resourcemanager.filemanager.ConfigManager;

public class InventoryManager {
	private ConfigManager configMgr = new ConfigManager();
	// following 3 hashmaps take care of managing the incoming inventory.
	// it can then be sync'd with the config file by calling syncConfig()
	private HashMap<String, Integer> inventoryElement;
	private HashMap<String, Integer> creditsElement;
	private HashMap<String, Integer> missionsElement;
	
	// constants
	private final String INVENTORY = "INVENTORY";
	private final String MISSIONS = "MISSIONS";
	private final String CREDITS = "CREDITS";
	private final String TOTALSPENT = "totalspent";
	
	public InventoryManager(){
		inventoryElement = new HashMap<String, Integer>();
		creditsElement  = new HashMap<String, Integer>();
		missionsElement = new HashMap<String, Integer>();
	}
	
	public String[] getMissionsInProgress(){
		return configMgr.getMissionsInProgress();
	}
	
	/**
	 * adds a given amount of a given item to the working inventory
	 * @param yieldId
	 * @param yieldAmt
	 */
	public void addInventory(String yieldId, int yieldAmt){
		if(inventoryElement.containsKey(yieldId)){
			inventoryElement.put(yieldId, inventoryElement.get(yieldId)+yieldAmt);
		}else{
			inventoryElement.put(yieldId, yieldAmt);
		}
	}
	
	/**
	 * adds a completed mission to the MISSIONS element
	 * @param yieldId
	 * @param yieldAmt
	 */
	public void addMission(String missionId){
		String missionConfigId = missionId.substring(0, 4);
		if(missionsElement.containsKey(missionConfigId)){
			missionsElement.put(missionConfigId, missionsElement.get(missionConfigId)+1);
		}else{
			missionsElement.put(missionConfigId, 1);
		}
	}
	
	/**
	 * If the missions was a failure, we still want to record that
	 * @param missionId
	 */
	public void failedMission(String missionId){
		String missionConfigId = missionId.substring(0, 4);
		if(missionsElement.containsKey(missionConfigId)){
			missionsElement.put(missionConfigId, missionsElement.get(missionConfigId)+1);
		}else{
			missionsElement.put(missionConfigId, 1);
		}
		
		
		if(missionsElement.containsKey("f-"+missionConfigId)){
			missionsElement.put("f-"+missionConfigId, missionsElement.get("f-"+missionConfigId)+1);
		}else{
			missionsElement.put("f-"+missionConfigId, 1);
		}
	}
	
	/**
	 * adds amount of credits spent on specific crewskill to config
	 * @param missionId
	 */
	public void addCredits(String missionId){
		int cost = Integer.parseInt(missionId.substring(4));
		String missionConfigId = missionId.substring(0, 4);
		
		if(creditsElement.containsKey(missionConfigId)){
			creditsElement.put(missionConfigId, creditsElement.get(missionConfigId)+cost);
		}else{
			creditsElement.put(missionConfigId, cost);
		}
		
		
		if(creditsElement.containsKey(TOTALSPENT)){
			creditsElement.put(TOTALSPENT, creditsElement.get(TOTALSPENT)+cost);
		}else{
			creditsElement.put(TOTALSPENT, cost);
		}
	}
	
	
	/**
	 * Syncs the current data with config file, and clears current information
	 */
	public void syncConfig(){
		configMgr.updateElement(MISSIONS, missionsElement);
		missionsElement.clear();
		configMgr.updateElement(CREDITS, creditsElement);
		creditsElement.clear();
		configMgr.updateElement(INVENTORY, inventoryElement);
		inventoryElement.clear();
	}
	
	/**
	 * closes the resource manager down, saves all in memory to config
	 * @param skillIds the ids of the crew skills currently in progress
	 */
	public void close(String[] skillIds){
		syncConfig();
		configMgr.setCrewSkillsInProgress(skillIds);
		configMgr.write();
	}
	
	public void close(){
		syncConfig();
		configMgr.setCrewSkillsInProgress();
		configMgr.write();
	}
	
}
