package com.runescape.gameserver.world.definitions;

/**
 * Handles NPC definitions fetched from the cache.
 * @author Aizen Sousuke
 */
public class CacheNPCDefinition {

	public static final int MAX_DEFINITIONS = 15000;
	
	private static CacheNPCDefinition[] definitions = new CacheNPCDefinition[MAX_DEFINITIONS];

	public static void addDefinition(CacheNPCDefinition def) {
		definitions[def.getId()] = def;
	}
	
	public static CacheNPCDefinition forId(int id) {
		return definitions[id];
	}
	
	private int id;
	private String name;
	private String desc;
	private int combatLevel;
	
	public CacheNPCDefinition(int id, String name, String desc, int combatLevel) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.combatLevel = combatLevel;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public int getCombatLevel() {
		return combatLevel;
	}
	
}
