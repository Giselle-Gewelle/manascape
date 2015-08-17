package com.runescape.gameserver.world.entity.mob.player.packet;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;

public abstract class IncomingPacket {
	
	protected Player player;
	
	public IncomingPacket(Player player) {
		this.player = player;
	}
	
	public abstract void handle(Packet packet) throws Throwable;

}
