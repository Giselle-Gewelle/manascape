package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.content.skills.impl.Mining;
import com.runescape.gameserver.world.content.skills.impl.Woodcutting;
import com.runescape.gameserver.world.content.skills.impl.Mining.Rock;
import com.runescape.gameserver.world.content.skills.impl.Woodcutting.Tree;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;
import com.runescape.gameserver.world.entity.object.GameObject;
import com.runescape.gameserver.world.region.Region;

public class ObjectOptionPacket extends IncomingPacket {

	private static final int OPTION_1 = 181;
	private static final int OPTION_2 = 241;

	public ObjectOptionPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		switch(packet.getOpcode()) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		}
	}

	private void handleOption1(Player player, Packet packet) {
		int x = packet.getShortA() & 0xFFFF;
		int y = packet.getLEShort() & 0xFFFF;
		int id = packet.getLEShort() & 0xFFFF;

		Region region = player.getRegion();
		Location loc = Location.create(x, y, player.getLocation().getHeight());
		GameObject object = region.getGameObject(loc, id);
		if(object == null) {
			return;
		}

		player.getPacketSender().sendMessage("Object Id: " + id);

		if(object.handleFirstClick(player)) {
			player.getPacketSender().sendMessage("Handling first click....");
			return;
		}
		// woodcutting
		Tree tree = Tree.forId(id);
		if(tree != null/*
						 * &&
						 * player.getLocation().isWithinInteractionDistance(loc)
						 */) {
			//player.getActionQueue().addAction(new WoodcuttingAction(player, loc, tree));
			player.getActionQueue().addAction(new CoordinateAction(player, loc, 3, new Woodcutting(player, tree, object)));
		}
		// mining
		Rock rock = Rock.forId(id);
		if(rock != null/*
						 * &&
						 * player.getLocation().isWithinInteractionDistance(loc)
						 */) {
			//player.getActionQueue().addAction(new MiningAction(player, loc, rock));
			player.getActionQueue().addAction(new CoordinateAction(player, loc, 1, new Mining(player, rock, object)));
		}
	}

	private void handleOption2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		int x = packet.getShort() & 0xFFFF;
		int y = packet.getShortA() & 0xFFFF;
		Location loc = Location.create(x, y, player.getLocation().getHeight());
		Region region = player.getRegion();
		GameObject object = region.getGameObject(loc, id);
		if(object == null) {
			return;
		}

		player.getPacketSender().sendMessage("Second click Object Id: " + object.getType());
		if(player.getLocation().isWithinInteractionDistance(object.getLocation())) {
			if(object.handleSecondClick(player)) {
				//player.getPacketSender().sendMessage("Handling second click....");
				return;
			}
		}

	}

}
