package com.runescape.gameserver.world.content.dialogue;

import com.runescape.gameserver.world.definitions.ItemDefinition;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.PacketSender;

public final class Dialogue {
	
	public enum DialogueType {
		PLAYER_CHAT,
		NPC_CHAT,
		OPTIONS,
		ITEM_OPTIONS,
		STATEMENT
	}
	
	private static final int[] 
		PLAYER_INTERFACES = new int[] {
			968, 973, 979, 986
		},
		NPC_INTERFACES = new int[] {
			4882, 4887, 4893, 4900
		},
		OPTION_INTERFACES = new int[] {
			2459, 2469, 2480, 2492
		},
		ITEM_INTERFACES = new int[] {
			8866, 8880, 8899, 8938
		},
		STATEMENT_INTERFACES = new int[] {
			356, 359, 363, 368
		};
	
	public static final Dialogue NOT_ENOUGH_SPACE = new Dialogue(
		new String[] {
			"You don't have enough space in your inventory."
		},
		DialogueType.STATEMENT
	);
	
	private DialogueType type;
	private Player player;
	private NPC npc;
	private String[] lines;
	private ChatAnimation anim;
	private int interfaceId;
	private int[] items;
	
	public Dialogue(String[] options, DialogueType type) {
		this.npc = null;
		this.lines = options;
		this.anim = null;
		
		if(type == DialogueType.OPTIONS) {
			interfaceId = OPTION_INTERFACES[lines.length - 2];
		} else if(type == DialogueType.STATEMENT) {
			interfaceId = STATEMENT_INTERFACES[lines.length - 1];
		}
		
		this.type = type;
	}
	
	public Dialogue(int[] items) {
		this.items = items;
		
		interfaceId = ITEM_INTERFACES[items.length - 2];
		
		type = DialogueType.ITEM_OPTIONS;
	}
	
	public Dialogue(NPC npc, String[] lines, ChatAnimation anim) {
		this.npc = npc;
		this.lines = lines;
		this.anim = anim;
		
		interfaceId = NPC_INTERFACES[lines.length - 1];
		
		type = DialogueType.NPC_CHAT;
	}
	
	public Dialogue(String[] lines, ChatAnimation anim) {
		this.npc = null;
		this.lines = lines;
		this.anim = anim;
		
		interfaceId = PLAYER_INTERFACES[lines.length - 1];
		
		type = DialogueType.PLAYER_CHAT;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int getInterfaceId() {
		return interfaceId;
	}
	
	public void show() {
		PacketSender packetSender = player.getPacketSender();
		
		switch(type) {
			default:
				break;
				
			case STATEMENT:
				for(int i = 0; i < lines.length; i++) {
					packetSender.sendString(interfaceId + 1 + i, lines[i]);
				}
				break;
			case ITEM_OPTIONS:
				for(int i = 0; i < items.length; i++) {
					packetSender.sendInterfaceModel(interfaceId + 3 + i, 150, items[i]);
					packetSender.sendString(interfaceId + items.length + 6 + (i * 4), ItemDefinition.forId(items[i]).getName());
				}
				break;
				
			case OPTIONS:
				for(int i = 0; i < lines.length; i++) {
					packetSender.sendString(interfaceId + 2 + i, lines[i]);
				}
				break;
				
			case NPC_CHAT:
				npc.getMobCooldowns().flag(CooldownFlags.WALKING, 1000);
				npc.face(player.getLocation());
				
				if(npc.isDynamic()) {
					packetSender.sendString(interfaceId + 2, npc.getName());
				} else if(npc.getDefinition() != null) {
					packetSender.sendString(interfaceId + 2, npc.getDefinition().getName());
				}
				for(int i = 0; i < lines.length; i++) {
					packetSender.sendString(interfaceId + 3 + i, lines[i]);
				}
				packetSender.sendInterfaceAnimation(interfaceId + 1, anim.getId());
				packetSender.sendNpcHead(interfaceId + 1, npc.getIndex());
				break;
				
			case PLAYER_CHAT:
				packetSender.sendString(interfaceId + 2, player.getName());
				for(int i = 0; i < lines.length; i++) {
					packetSender.sendString(interfaceId + 3 + i, lines[i]);
				}
				packetSender.sendInterfaceAnimation(interfaceId + 1, anim.getId());
				packetSender.sendPlayerHead(interfaceId + 1);
				break;
		}
		
		packetSender.sendChatBoxInterface(interfaceId);
	}
	
}
