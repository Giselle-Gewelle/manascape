package com.runescape.gameserver.world.content.quest;

import java.util.HashMap;
import java.util.Map;

import com.runescape.gameserver.world.content.quest.impl.*;
import com.runescape.gameserver.world.entity.mob.player.Player;

public final class QuestHandler {

	private Player player;
	private Map<Integer, Quest> quests;
	
	public QuestHandler(Player player) {
		this.player = player;
		this.quests = new HashMap<Integer, Quest>();
		
		Quest quest = new DivineIntervention(player);
		quests.put(quest.getId(), quest);
	}
	
	public void openQuestLog(Quest quest) {
		quest.resetQuestLog();
		quest.updateQuestLog();
		player.getPacketSender().sendString(19054, quest.getName());
		player.getPacketSender().sendString(19056, quest.getQuestLog());
		player.getPacketSender().sendInterface(19044);
	}
	
	public Quest forId(int buttonId) {
		if(!quests.containsKey(buttonId)) {
			return null;
		}
		
		return quests.get(buttonId);
	}
	
}
