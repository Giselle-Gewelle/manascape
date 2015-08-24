package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.entity.action.impl.AttackAction;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class NPCOptionPacket extends IncomingPacket {

	private final int FIRST_CLICK = 67;
	private final int SECOND_CLICK = 112;
	private final int THIRD_CLICK = 13;
	private final int MAGIC_ON_NPC = 104;
	
	public NPCOptionPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		int npcId = 0;
		int opcode = packet.getOpcode();
		int spellId = 0;
		switch(opcode) {
		case FIRST_CLICK:
			npcId = packet.getShortA() & 0xFFFF;
			player.sendOMessage("Click first npc option.");
			break;
		case SECOND_CLICK:
			npcId = packet.getLEShort();
			player.sendOMessage("Click second npc option.");
			break;
		case THIRD_CLICK:
			npcId = packet.getLEShortA();
			player.sendOMessage("Click third npc option.");
			break;
		case MAGIC_ON_NPC:
			spellId = packet.getShortA();
			npcId = packet.getLEShort();
			player.sendOMessage("Spell Id: " + spellId + "; NPC index: " + npcId);
			break;
		}
		
		if(npcId < 0 || npcId >= Constants.MAX_NPCS) {
			return;
		}
		
		NPC n = (NPC)World.getInstance().getNPCs().get(npcId);
		
		if(n.getDefinition() == null) {
			player.getPacketSender().sendMessage("NPC Id: " + n.getId());
		} else {
			player.getPacketSender().sendMessage("NPC Id: " + n.getDefinition().getId());
		}
		
		player.face(n.getLocation());
		
		switch(opcode) {
		case FIRST_CLICK:
			if (n.handleFirstClick(player))
				return;
			
			if(n != null/* && player.getLocation().isWithinInteractionDistance(npc.getLocation())*/) {
				//player.getActionQueue().addAction(new AttackAction(player, victim));
				int distance = 1;
				player.getActionQueue().addAction(new CoordinateAction(player, n.getLocation(), distance, new AttackAction(player, n)));
			}
			break;
		case SECOND_CLICK:
			if (n.handleSecondClick(player))
				return;
			break;
		case THIRD_CLICK:
			if (n.handleThirdClick(player))
				return;
			break;
		case MAGIC_ON_NPC:
			Spells spell = Spells.forId(spellId);
			if (spell != null) {
				if (Spells.isNonCombatSpell(spell)) {
					
				} else {
					player.setCurrentSpell(spell);
					player.setAttackType(AttackType.MAGIC);
					player.getActionQueue().addAction(new AttackAction(player, n));
				}
			} else
				System.out.println("null spell" + spellId);
			break;
		
		}
	}
}
