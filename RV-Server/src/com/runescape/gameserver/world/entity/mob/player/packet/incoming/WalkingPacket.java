package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class WalkingPacket extends IncomingPacket {

	public WalkingPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		int size = packet.getLength();
		if(packet.getOpcode() == 213) {
		    size -= 14;
		}

		//player.getWalkingQueue().reset();
		player.getActionQueue().clearNonWalkableActions();
		player.resetInteractingEntity();
		player.getPacketSender().sendCloseInterfaces();

		final int steps = (size - 5) / 2;
		//if(steps <= 0) {
		//	System.out.println("no steps!");
		//	return;
		//}
		final int[][] path = new int[steps][2];

		final int firstX = packet.getLEShortA();
		///final boolean runSteps = packet.getByte() == 1;
		packet.getByte(); //^^^
		final int firstY = packet.getLEShortA();
		//System.out.println("x = " + firstX + ", y = " + firstY);
		
		//player.getWalkingQueue().setRunningQueue(runSteps);
		//player.getWalkingQueue().addStep(firstX, firstY);	
		player.getWalkingQueue().walkTo(firstX, firstY);
		for(int i = 0; i < steps; i++) {
		    path[i][0] = packet.getByte();
		    path[i][1] = packet.getByteS();
		}		
		for(int i = 0; i < steps; i++) {
		    path[i][0] += firstX;
		    path[i][1] += firstY;
		    //player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		//player.getWalkingQueue().finish();
		//System.out.println("" + steps);
		if(steps <= 0) {
			return;
		}
		player.getWalkingQueue().walkTo(path[steps - 1][0], path[steps - 1][1]);
	}
	
	public void handle2(Player player, Packet packet) {
		int size = packet.getLength();
		if(packet.getOpcode() == 213) {
		    size -= 14;
		}

		player.getWalkingQueue().reset();
		player.getActionQueue().clearNonWalkableActions();
		player.resetInteractingEntity();

		final int steps = (size - 5) / 2;
		final int[][] path = new int[steps][2];

		final int firstX = packet.getLEShortA();
		final boolean runSteps = packet.getByte() == 1;
		final int firstY = packet.getLEShortA();
		
		player.getWalkingQueue().setRunningQueue(runSteps);
		player.getWalkingQueue().addStep(firstX, firstY );		
		for (int i = 0; i < steps; i++) {
		    path[i][0] = packet.getByte();
		    path[i][1] = packet.getByteS();
		}		
		for (int i = 0; i < steps; i++) {
		    path[i][0] += firstX;
		    path[i][1] += firstY;
		    player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		player.getWalkingQueue().finish();
	}

}
