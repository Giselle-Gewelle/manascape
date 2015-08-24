package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.action.impl.AttackAction;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class PlayerOptionPacket extends IncomingPacket {

	public PlayerOptionPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		switch(packet.getOpcode()) {
		case 245:
			/*
			 * Option 1.
			 */
			option1(player, packet);
			break;
		case 233:
			/*
			 * Option 2.
			 */
			option2(player,  packet);
			break;
		case 194:
			/*
			 * Option 3.
			 */
			option3(player, packet);
			break;
		}
	}

	private void option1(final Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = (Player) World.getInstance().getPlayers().get(id);
		if(victim != null && player.getLocation().isWithinInteractionDistance(victim.getLocation())) {
			player.getActionQueue().addAction(new AttackAction(player, victim));
		}
	}
	
	private void option2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
	}
	
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
	}
		
}

