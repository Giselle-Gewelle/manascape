package com.runescape.gameserver.world.definitions;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.util.LevelRequirement;

/**
 * A class to define all equipment, excluding weapons.
 * @author Desmin
 *
 */
public class EquipmentDefinition {
	
	private static final Logger logger = Logger.getLogger(ItemDefinition.class.getName());
	
	public static void init() throws IOException, ParseException {
		
		logger.info("Loading equipment definitions.....");
		
		final JSONParser parser = new JSONParser();
		final JSONArray json = (JSONArray) parser.parse(new FileReader("data/equipment.json"));
		
		int count = 0;
		
		for (Object o : json) {
			
			JSONObject definition = (JSONObject) o;
			List<LevelRequirement> levelRequirements = new LinkedList<>();
			int equipmentId = ((Long)definition.get("id")).intValue();
			JSONObject levelReqObj = (JSONObject)definition.get("levelreq");
			int level = ((Long)levelReqObj.get("level")).intValue();
			String skill = (String)levelReqObj.get("skill");
			LevelRequirement levelReq = new LevelRequirement(level, Skill.valueOf(skill.toUpperCase()));
			levelRequirements.add(levelReq);
			if (definition.containsKey("levelreq")) {
				JSONObject levelReqObj2 = (JSONObject)definition.get("levelreq");
				int level2 = ((Long)levelReqObj2.get("level")).intValue();
				String skill2 = (String)levelReqObj2.get("skill");
				LevelRequirement levelReq2 = new LevelRequirement(level2, Skill.valueOf(skill2.toUpperCase()));
				levelRequirements.add(levelReq2);
			}
			
			equipment.put(equipmentId, new EquipmentDefinition(equipmentId, levelRequirements, ItemBonuses.getBonusesForId(equipmentId)));
			count++;
		}
		logger.info("Successfully loaded " + count + " equipment definitions.");
	}

	private int equipmentId;
	private List<LevelRequirement> levelReqs;
	private ItemBonuses equipmentBonuses;
	
	public EquipmentDefinition(int equipmentId, List<LevelRequirement> levelReqs, ItemBonuses itemBonuses) {
		this.equipmentId = equipmentId;
		this.levelReqs = levelReqs;
		this.equipmentBonuses = itemBonuses;
	}
	
	public int getEquipmentId() {
		return equipmentId;
	}
	
	public List<LevelRequirement> getLevelReq() {
		return levelReqs;
	}
	
	public ItemBonuses getEquipmentBonuses() {
		return equipmentBonuses;
	}
	
	private static Map<Integer, EquipmentDefinition> equipment = new HashMap<>();
	
	public static EquipmentDefinition forItemId(int id) {
		return equipment.get(id);
	}
}
