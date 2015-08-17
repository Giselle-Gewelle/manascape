package com.runescape.gameserver.world.content.quest;

import java.awt.Color;

import com.runescape.gameserver.world.entity.mob.player.Player;

public abstract class Quest {

	public final static int 
		STAGE_NOT_STARTED = 0, 
		STAGE_COMPLETED = 1000;
	
	protected Player player;
	protected int stage;
	protected int id;
	protected String name;
	protected String questLog;
	
	public Quest(Player player, int id, String name) {
		this.player = player;
		stage = 0;
		this.id = id;
		this.name = name;
		questLog = "";
	}
	
	public abstract void updateQuestLog();
	
	public void setComplete() {
		stage = STAGE_COMPLETED;
	}
	
	public void incrementStage() {
		setStage(stage + 1);
	}
	public void setStage(int stage) {
		if(stage > 0 && stage < STAGE_COMPLETED) {
			player.getPacketSender().sendTextColor(id, Color.YELLOW);
		} else if(stage == STAGE_COMPLETED) {
			player.getPacketSender().sendTextColor(id, Color.GREEN);
		} else {
			player.getPacketSender().sendTextColor(id, Color.RED);
		}
		
		this.stage = stage;
	}
	public int getStage() {
		return stage;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void log(String line) {
		if(!questLog.isEmpty()) {
			questLog += "\\n";
		}
		questLog += line;
	}
	public String getQuestLog() {
		return questLog;
	}
	public void resetQuestLog() {
		questLog = "";
	}
	
}
