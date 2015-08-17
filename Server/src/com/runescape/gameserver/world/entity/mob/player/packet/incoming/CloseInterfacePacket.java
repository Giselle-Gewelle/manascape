package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class CloseInterfacePacket extends IncomingPacket {

	public CloseInterfacePacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		player.getInterfaceState().interfaceClosed();
	}

}
