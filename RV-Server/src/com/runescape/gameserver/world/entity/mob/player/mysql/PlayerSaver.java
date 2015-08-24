package com.runescape.gameserver.world.entity.mob.player.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.runescape.gameserver.mysql.MySqlHandler;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.content.skills.Skills;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.mob.Appearance;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.Settings;

public class PlayerSaver {
	
	private Connection con;

	public PlayerSaver(MySqlHandler mysql) {
		this.con = mysql.getConnection();
	}
	
	public void savePlayerEquipment(Player player) {
		Container equipment = player.getEquipment();
		String insertStr = "(?, ?, ?, ?)";
		String fullInsert = "";
		for(int i = 0; i < Equipment.SIZE; i++) {
			if(i != 0) {
				fullInsert += ",";
			}
			fullInsert += insertStr;
		}
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"REPLACE INTO user_equipment " + 
				"(username, slot, itemId, amount) " + 
				"VALUES " + 
				fullInsert + ";"
			);
			int index = 1;
			for(int i = 0; i < Equipment.SIZE; i++) {
				Item item = equipment.get(i);
				stmt.setString(index++, player.getName());
				stmt.setInt(index++, i);
				if(item != null) {
					stmt.setInt(index++, item.getId());
					stmt.setInt(index++, item.getCount());
				} else {
					stmt.setInt(index++, 65535);
					stmt.setInt(index++, 0);
				}
			}
			
			stmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void savePlayerInventory(Player player) {
		Container inventory = player.getInventory();
		String insertStr = "(?, ?, ?, ?)";
		String fullInsert = "";
		for(int i = 0; i < Inventory.SIZE; i++) {
			if(i != 0) {
				fullInsert += ",";
			}
			fullInsert += insertStr;
		}
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"REPLACE INTO user_inventory " + 
				"(username, slot, itemId, amount) " + 
				"VALUES " + 
				fullInsert + ";"
			);
			int index = 1;
			for(int i = 0; i < Inventory.SIZE; i++) {
				Item item = inventory.get(i);
				stmt.setString(index++, player.getName());
				stmt.setInt(index++, i);
				if(item != null) {
					stmt.setInt(index++, item.getId());
					stmt.setInt(index++, item.getCount());
				} else {
					stmt.setInt(index++, 65535);
					stmt.setInt(index++, 0);
				}
			}
			
			stmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void savePlayerSkills(Player player) {
		Skills skills = player.getSkills();
		String insertStr = "(?, ?, ?, ?, ?)";
		String fullInsert = "";
		for(int i = 0; i < Skill.values().length; i++) {
			if(i != 0) {
				fullInsert += ",";
			}
			fullInsert += insertStr;
		}
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"REPLACE INTO user_skills " + 
				"(username, skill, curLevel, level, exp) " + 
				"VALUES " + 
				fullInsert + ";"
			);
			int index = 1;
			for(Skill skill : Skill.values()) {
				stmt.setString(index++, player.getName());
				stmt.setString(index++, skill.name());
				stmt.setInt(index++, skills.getLevel(skill));
				stmt.setInt(index++, skills.getRealLevel(skill));
				stmt.setDouble(index++, skills.getExperience(skill));
			}
			
			stmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void savePlayerSettings(Player player) {
		String query = 
		"REPLACE INTO user_settings " + 
		"(username, attackStyle, withdrawNotes, swapping, autoRetaliate, running, brightness, mouseButtons, chatEffects) " + 
		"VALUES " + 
		"(?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		int index = 1;
		Settings settings = player.getSettings();
		try {
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(index++, player.getName());
			stmt.setInt(index++, settings.getInt("attackStyle"));
			stmt.setInt(index++, settings.getBool("withdrawNotes") ? 1 : 0);
			stmt.setInt(index++, settings.getBool("swapping") ? 1 : 0);
			stmt.setInt(index++, settings.getBool("autoRetaliate") ? 1 : 0);
			stmt.setInt(index++, settings.getBool("running") ? 1 : 0);
			stmt.setInt(index++, settings.getInt("brightness"));
			stmt.setInt(index++, settings.getInt("mouseButtons"));
			stmt.setInt(index++, settings.getInt("chatEffects"));
			
			stmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void savePlayer(Player player) {
		try {
			String appearanceStr = "";
			Appearance appearance = player.getAppearance();
			int[] values = appearance.getLook();
			for(int i = 0; i < values.length; i++) {
				if(i != 0) {
					appearanceStr += ",";
				} else {
					System.out.println("gender = " + values[i]);
				}
				appearanceStr += values[i];
			}
			
			String query = "REPLACE INTO user_details " + 
				"(username, password, rights, member, x, y, height, appearance, runEnergy)" + 
				"VALUES " + 
				"(?, ?, ?, ?, ?, ?, ?, ?, ?);";
			
			int index = 1;
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(index++, player.getName());
			stmt.setString(index++, player.getPassword());
			stmt.setInt(index++, player.getRights().toInteger());
			stmt.setInt(index++, player.isMembers() ? 1 : 0);
			stmt.setInt(index++, player.getLocation().getX());
			stmt.setInt(index++, player.getLocation().getY());
			stmt.setInt(index++, player.getLocation().getHeight());
			stmt.setString(index++, appearanceStr);
			stmt.setDouble(index++, player.getRunEnergy());
			
			stmt.execute();
			
			savePlayerSettings(player);
			savePlayerSkills(player);
			savePlayerInventory(player);
			savePlayerEquipment(player);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
