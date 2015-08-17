package com.runescape.gameserver.world.content.dialogue;

import com.runescape.gameserver.world.entity.mob.player.Player;

public abstract class ChatInterfaceAction {

	public abstract void execute(Player player, int id, int itemIndex);

}
