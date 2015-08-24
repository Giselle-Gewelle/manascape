package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class EnterAmountPacket extends IncomingPacket {

	public EnterAmountPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		int amount = packet.getInt();
		if(player.getInterfaceState().isEnterAmountInterfaceOpen()) {
			player.getInterfaceState().closeEnterAmountInterface(amount);
		}
		if (player.getInterfaceState().getNumberInputListener() != null) {
			player.getInterfaceState().getNumberInputListener().execute(amount);
		}
	}

}
