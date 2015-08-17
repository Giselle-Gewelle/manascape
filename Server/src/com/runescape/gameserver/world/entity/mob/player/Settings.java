package com.runescape.gameserver.world.entity.mob.player;

import java.util.HashMap;
import java.util.Map;

public class Settings {

	private Map<String, Integer> integers;
	private Map<String, Boolean> booleans;
	
	
	public static final int SIZE = 100;
	
	public static final int ATTACK_STYLE = 0;
	public static final int WITHDRAW_NOTES = 1;
	public static final int SWAPPING = 2;
	public static final int AUTO_RETALIATE = 3;
	public static final int RUNNING = 4;
	public static final int RUN_ENERGY = 5;
	public static final int BRIGHTNESS = 6;
	public static final int MOUSE_BUTTONS = 7;
	public static final int CHAT_EFFECTS = 8;
	public static final int SPLIT_CHAT = 9;
	public static final int ACCEPT_AID = 10;
	public static final int WEIGHT = 11;
	
	private Player player;
	private double[] settings = new double[SIZE];
	
	public Settings(Player player) {
		settings[RUN_ENERGY] = 100;
		settings[SWAPPING] = 1;
		settings[BRIGHTNESS] = 1; /* normal brightness */
		settings[MOUSE_BUTTONS] = 2; /* two mouse buttons, 1 for one mouse button */
		settings[CHAT_EFFECTS] = 1; /* chat effects ON */
		settings[AUTO_RETALIATE] = 1; /* auto retaliate ON */
		
		this.player = player;
		
		integers = new HashMap<String, Integer>();
		booleans = new HashMap<String, Boolean>();
		setInt("attackStyle", 0);
		setBool("withdrawNotes", false);
		setBool("swapping", true);
		setBool("autoRetaliate", true);
		setBool("running", false);
		setInt("brightness", 1);
		setInt("mouseButtons", 2);
		setInt("chatEffects", 1);
	}
	
	public int getInt(String name) {
		return integers.get(name);
	}
	
	public boolean getBool(String name) {
		return booleans.get(name);
	}
	
	public void setInt(String name, int value) {
		if(integers.containsKey(name)) {
			integers.remove(name);
		}
		integers.put(name, value);
	}
	
	public void setBool(String name, boolean value) {
		if(booleans.containsKey(name)) {
			booleans.remove(name);
		}
		booleans.put(name, value);
		
		if(name.equals("running")) {
			if(player.isActive()) {
				player.getPacketSender().sendRunStatus(value);
			}
		}
	}
	
	/*public double[] getSettings() {
		return settings;
	}
	
	public double get(int settingId) {
		return settings[settingId];
	}
	
	public void set(int settingId, double settingValue) {
		settings[settingId] = settingValue;
	}
	
	public boolean getBool(int settingId) {
		return (settings[settingId] == 1);
	}
	
	public void setBool(int settingId, boolean settingValue) {
		settings[settingId] = (settingValue ? 1 : 0);
	}
	
	public int getInt(int settingId) {
		return (int) settings[settingId];
	}*/

}
