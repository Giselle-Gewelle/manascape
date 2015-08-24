package com.runescape.gameserver.world.entity.mob.player.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.runescape.gameserver.mysql.MySqlHandler;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.content.skills.Skills;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.mob.player.LoginResult;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.PlayerDetails;
import com.runescape.gameserver.world.entity.mob.player.Settings;
import com.runescape.gameserver.world.entity.mob.player.Player.Rights;

public class PlayerLoader {
	
	private Connection con;
	
	public PlayerLoader(MySqlHandler mysql) {
		this.con = mysql.getConnection();
	}
	
	public void loadPlayerEquipment(Player player) {
		Container equipment = player.getEquipment();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"SELECT * FROM user_equipment WHERE username = ? LIMIT " + Equipment.SIZE + ";"
			);
			stmt.setString(1, player.getName());
			stmt.execute();
			ResultSet results = stmt.getResultSet();
			while(results.next()) {
				int slot = results.getInt("slot");
				int itemId = results.getInt("itemId");
				int count = results.getInt("amount");
				if(itemId != 65535) {
					equipment.set(slot, new Item(itemId, count));
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerInventory(Player player) {
		Container inventory = player.getInventory();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"SELECT * FROM user_inventory WHERE username = ? LIMIT " + Inventory.SIZE + ";"
			);
			stmt.setString(1, player.getName());
			stmt.execute();
			ResultSet results = stmt.getResultSet();
			while(results.next()) {
				int slot = results.getInt("slot");
				int itemId = results.getInt("itemId");
				int count = results.getInt("amount");
				if(itemId != 65535) {
					inventory.set(slot, new Item(itemId, count));
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerSkills(Player player) {
		Skills skills = player.getSkills();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"SELECT * FROM user_skills WHERE username = ? LIMIT " + Skill.values().length + ";"
			);
			stmt.setString(1, player.getName());
			stmt.execute();
			ResultSet results = stmt.getResultSet();
			while(results.next()) {
				Skill skill = Skill.valueOf(results.getString("skill"));
				skills.setLevel(skill, results.getInt("curLevel"));
				skills.setRealLevel(skill, results.getInt("level"), false);
				skills.setExperience(skill, results.getDouble("exp"));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerSettings(Player player) {
		Settings settings = player.getSettings();
		
		int attackStyle = 0;
		boolean notes = false;
		boolean swapping = true;
		boolean autoRet = true;
		boolean run = true;
		int bright = 1;
		int mouse = 2;
		int chat = 1;
		
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM user_settings WHERE username = ? LIMIT 1;");
			stmt.setString(1, player.getName());
			
			boolean success = stmt.execute();
			if(success) {
				ResultSet result = stmt.getResultSet();
				if(result.next()) {
					attackStyle = result.getInt("attackStyle");
					notes = result.getInt("withdrawNotes") == 1;
					swapping = result.getInt("swapping") == 1;
					autoRet = result.getInt("autoRetaliate") == 1;
					run = result.getInt("running") == 1;
					bright = result.getInt("brightness");
					mouse = result.getInt("mouseButtons");
					chat = result.getInt("chatEffects");
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		settings.setInt("attackStyle", attackStyle);
		settings.setBool("withdrawNotes", notes);
		settings.setBool("swapping", swapping);
		settings.setBool("autoRetaliate", autoRet);
		settings.setBool("running", run);
		settings.setInt("brightness", bright);
		settings.setInt("mouseButtons", mouse);
		settings.setInt("chatEffects", chat);
	}
	
	public LoginResult checkLogin(PlayerDetails playerDetails) {
		int code = 2;
		Player player = null;
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"SELECT * FROM user_details WHERE username = ? LIMIT 1;"
			);
			stmt.setString(1, playerDetails.getName());
			
			player = new Player(playerDetails);
			boolean success = stmt.execute();
			if(success) {
				ResultSet result = stmt.getResultSet();
				if(result.next()) {
					String dbPass = result.getString("password");
					if(playerDetails.getPassword().equals(dbPass)) {
						player.setRights(Rights.getRights(result.getInt("rights")));
						player.setMembers(result.getInt("member") == 1);
						player.setLocation(new Location(result.getInt("x"), result.getInt("y"), result.getInt("height")));
						
						String[] appearanceStr = result.getString("appearance").split(",");
						int[] appearance = new int[13];
						for(int i = 0; i < appearance.length; i++) {
							appearance[i] = Integer.parseInt(appearanceStr[i]);
						}
						player.getAppearance().setLook(appearance);
						
						player.setRunEnergy(result.getDouble("runEnergy"), false);
						
						loadPlayerSettings(player);
						loadPlayerSkills(player);
						loadPlayerInventory(player);
						loadPlayerEquipment(player);
					} else {
						code = 3;
					}
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			code = 11;
		}

		return new LoginResult(code, player);
	}

}
