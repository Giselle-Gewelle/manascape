package com.runescape.gameserver.world.entity.mob.player.packet;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.core.session.IoSession;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.incoming.*;
import com.runescape.gameserver.world.entity.mob.player.packet.incoming.item.GroundItemOptionPacket;
import com.runescape.gameserver.world.entity.mob.player.packet.incoming.item.ItemOnXPacket;
import com.runescape.gameserver.world.entity.mob.player.packet.incoming.item.ItemOptionPacket;
import com.runescape.gameserver.world.entity.mob.player.packet.incoming.item.SwitchItemPacket;

public class PacketReceiver {
	
	private static final Logger logger = Logger.getLogger(PacketReceiver.class.getName());
	
	private Player player;
	private IncomingPacket[] handlers = new IncomingPacket[256];
	
	public PacketReceiver(Player player) {
		this.player = player;
		IncomingPacket defaultHandler = new UnhandledPacket(player);
		for(int i = 0; i < handlers.length; i++) {
			if(handlers[i] == null) {
				handlers[i] = defaultHandler;
			}
		}
		
		IncomingPacket quietHandler = new QuietPacket(player);
		int[] quiet = new int[] {
			19, 40, 110, 140, 187, 202, 244, 248
		};
		for(int q : quiet) {
			handlers[q] = quietHandler;
		}
		
		IncomingPacket walkingHandler = new WalkingPacket(player);
		int[] walk = new int[] {
			28, 213, 247
		};
		for(int w : walk) {
			handlers[w] = walkingHandler;
		}
		
		handlers[56] = new CommandPacket(player);
		
		handlers[123] = new SwitchItemPacket(player);
		
		IncomingPacket itemOptionPacket = new ItemOptionPacket(player);
		int[] itemOption = new int[] {
			3, 4, 71, 91, 158, 177, 203, 231
		};
		for(int i : itemOption) {
			handlers[i] = itemOptionPacket;
		}
		
		handlers[24] = new WieldPacket(player);
		
		IncomingPacket buttonPacket = new ButtonPacket(player);
		handlers[79] = buttonPacket;
		handlers[226] = buttonPacket;
		
		handlers[49] = new ChatPacket(player);
		
		handlers[75] = new EnterAmountPacket(player);
		
		IncomingPacket friendHandler = new FriendsPacket(player);
		int[] friend = new int[] {
			120, 141, 160, 217
		};
		for(int f : friend) {
			handlers[f] = friendHandler;
		}
		
		IncomingPacket objectOptionHandler = new ObjectOptionPacket(player);
		int[] objectOption = new int[] {
			181, 241
		};
		for(int o : objectOption) {
			handlers[o] = objectOptionHandler;
		}
		
		handlers[163] = new CharacterDesignPacket(player);
		
		handlers[245] = new PlayerOptionPacket(player);
		
		IncomingPacket npcOptionPacket = new NPCOptionPacket(player);
		int[] npcOption = new int[] {
			13, 67, 104, 112
		};
		for(int n : npcOption) {
			handlers[n] = npcOptionPacket;
		}
		
		IncomingPacket itemOnPacket = new ItemOnXPacket(player);
		int[] itemOn = new int[] {
			1, 57, 143, 152, 211
		};
		for(int o : itemOn) {
			handlers[o] = itemOnPacket;
		}
		
		handlers[54] = new GroundItemOptionPacket(player);
	}

	public void handle(IoSession session, Packet packet) throws Throwable {
		try {
			handlers[packet.getOpcode()].handle(packet);
		} catch(Exception ex) {
			logger.log(Level.SEVERE, "Exception handling packet.", ex);
			session.close(false);
		}
	}
	
	public Player getPlayer() {
		return player;
	}

}
