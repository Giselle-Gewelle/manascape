package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import java.util.logging.Logger;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class UnhandledPacket extends IncomingPacket {

	private static final Logger logger = Logger.getLogger(UnhandledPacket.class.getName());

	public UnhandledPacket(Player player) {
		super(player);
	}
	
	@Override
	public void handle(Packet packet) {
		logger.info("Packet : [opcode=" + packet.getOpcode() + " length=" + packet.getLength() + " payload=" + packet.getPayload() + "]");
	}

}
