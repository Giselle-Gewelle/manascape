package com.runescape.gameserver.world.entity.mob.npc.impl;

import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Hans extends NPC {

	public Hans(CacheNPCDefinition def) {
		super(def);
	}
	
	@Override
	public boolean handleSecondClick(Player player) {
		//player.setPlayerDialogue(new Dialogue(player, this, new String[] {"Hi, I'm Hans!"}, ChatEmotion.HAPPY));
		return true;
	}

}
