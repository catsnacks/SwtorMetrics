package crewskillsmanager.resourcemanager.filemanager;

import java.util.Map;

import configlite.ConfigLite;

public class ConfigManager {
	private final String INVENTORY = "INVENTORY";
	private final String MISSIONS = "MISSIONS";
	private final String CREDITS = "CREDITS";
	private final String MISSIONSINPROGRESS = "MISSIONS IN PROGRESS";
	
	private final String TOTALSPENT = "totalspent";
	private final String SKILLSPENT = "-spent";
	
	private ConfigLite config;
	
	public ConfigManager(){
		config = new ConfigLite("swtor_metrics.cfg");
		config.setWriteOnce(true);
		checkConfig();
	}
	
	/**
	 * Updates all of the current settings of given element by added the passed values
	 * to the previous values
	 * 
	 * the hashmap passed MUST be cleared after passing to this method
	 * 
	 * @param element
	 * @param settings
	 */
	public void updateElement(String element, java.util.HashMap<String, Integer> settings){
		if(!config.hasElement(element))
			config.addElement(element);
		
		config.enterElement(element);
		for (Map.Entry<String, Integer> entry : settings.entrySet()){
			String attr = entry.getKey();
			int value = entry.getValue();
			if(config.hasSetting(element, attr))
				value += config.getInt(attr);
			
			config.setSetting(attr, value);
		}
		config.saveToFile();
	}
	
	/**
	 * Sets the crew skills in progress, in the config, to the given array of crew skill ids.
	 * This is meant to be used with the resourcemanager's close()
	 * @param skillIds
	 */
	public void setCrewSkillsInProgress(String[] skillIds){
		config.clearSettings(MISSIONSINPROGRESS);
		config.enterElement(MISSIONSINPROGRESS);
		for(int i=0; i<skillIds.length; i++){
			config.addSetting("mission"+i, skillIds[i]);
		}
	}
	
	/**
	 * Sets the crew skills in progress to none, meant to be used with resourcemanager's clse()
	 */
	public void setCrewSkillsInProgress(){
		config.clearSettings(MISSIONSINPROGRESS);
	}
	
	public String[] getMissionsInProgress(){
		String[] attrs = config.getSettingAttributes(MISSIONSINPROGRESS);
		String[] missionIds = new String[attrs.length];
		
		config.enterElement(MISSIONSINPROGRESS);
		for(int i=0;i<attrs.length;i++){
			missionIds[i] = config.getString(attrs[i]);
		}
		
		return missionIds;
	}
	
	/**
	 * Forces a reread of the config into memory
	 * WARNING: THIS WILL DELETE ALL CONFIG ENTRIES SINCE LAST WRITE
	 */
	public void read(){
		config = new ConfigLite("swtor_metrics.cfg");
		config.setWriteOnce(true);
		checkConfig();
	}
	
	/**
	 * writes config file changes to disk
	 */
	public void write(){
		config.saveToFile();
	}
	/**
	 * checks that the necessary elements are in config,
	 * and creates them if they aren't 
	 */
	private void checkConfig(){
		boolean changesMade=false;
		if(!config.hasElement(INVENTORY)){
			config.addElement(INVENTORY);
			changesMade=true;
		}
		if(!config.hasElement(MISSIONS)){
			config.addElement(MISSIONS);
			changesMade=true;
		}
		if(!config.hasElement(CREDITS)){
			config.addElement(CREDITS);
			changesMade=true;
		}
		if(!config.hasElement(MISSIONSINPROGRESS)){
			config.addElement(MISSIONSINPROGRESS);
			changesMade=true;
		}
		
		if(changesMade)
			config.saveToFile();
	}
	
	
}
