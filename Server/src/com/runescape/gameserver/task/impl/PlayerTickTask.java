package com.runescape.gameserver.task.impl;

import java.util.Queue;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.ChatMessage;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class PlayerTickTask implements Task {
	
	private Player player;
	
	public PlayerTickTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		Queue<ChatMessage> messages = player.getChatMessageQueue();
		if(messages.size() > 0) {
			player.getUpdateFlags().flag(UpdateFlag.CHAT);
			ChatMessage message = player.getChatMessageQueue().poll();
			player.setCurrentChatMessage(message);
		} else {
			player.setCurrentChatMessage(null);
		}
		
		player.getWalkingQueue().processNextMovement();
		player.getPrayer().handlePrayerDrain();
		player.getSkills().handleHealthRegen();
	}

}
