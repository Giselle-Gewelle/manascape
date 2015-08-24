package com.runescape.gameserver.world.entity.mob.player.packet.incoming.item;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class GroundItemOptionPacket extends IncomingPacket {

	public GroundItemOptionPacket(Player player) {
		super(player);
	}

	@SuppressWarnings("unused")
	@Override
	public void handle(Packet packet) throws Throwable {
		int itemId = packet.getShortA();
		int y = packet.getLEShort();
		int x = packet.getShort();
		
		
	}

}
