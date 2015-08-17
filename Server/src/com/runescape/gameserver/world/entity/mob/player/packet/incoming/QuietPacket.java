package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

/**
 * A packet handler which takes no action i.e. it ignores the packet.
 * @author Graham Edgecombe
 *
 */
public class QuietPacket extends IncomingPacket {

	public QuietPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		if (packet.getOpcode() == 202) {
			//player.getActionSender().sendLogout();
		}
	}

}
