package com.runescape.gameserver.world.entity.mob.npc.impl;

import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Man extends NPC {

	public Man(CacheNPCDefinition definition) {
		super(definition);
	}

	@Override
	public boolean handleSecondClick(Player player) {
		//player.setPlayerDialogue(new Dialogue(player, this, new String[] {"EAT A FUCKING COCK."}, ChatEmotion.ANGER_4));
		return true;
	}
}
