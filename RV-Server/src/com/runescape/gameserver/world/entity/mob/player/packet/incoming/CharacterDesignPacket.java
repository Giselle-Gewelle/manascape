package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class CharacterDesignPacket extends IncomingPacket {

	public CharacterDesignPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) { // Still Lies
		final int gender = packet.get();
		final int head = packet.get();
		final int beard = packet.get();
		final int chest = packet.get();
		final int arms = packet.get();
		final int hands = packet.get();
		final int legs = packet.get();
		final int feet = packet.get();
		final int hairColour = packet.get();
		final int torsoColour = packet.get();
		final int legColour = packet.get();
		final int feetColour = packet.get();
		final int skinColour = packet.get();

		int look[] = new int[13];

		look[0] = gender;

		look[6] = head;
		look[7] = chest;
		look[8] = arms;
		look[9] = hands;
		look[10] = legs;
		look[11] = feet;
		look[12] = beard;

		look[1] = hairColour;
		look[2] = torsoColour;
		look[3] = legColour;
		look[4] = feetColour;
		look[5] = skinColour;

        player.getAppearance().setLook(look);
        player.getInterfaceState().interfaceClosed();
        player.getUpdateFlags().set(UpdateFlag.APPEARANCE, true);
		
	}

}
