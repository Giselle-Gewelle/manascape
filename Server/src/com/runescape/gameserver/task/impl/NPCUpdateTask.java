package com.runescape.gameserver.task.impl;

import java.util.Iterator;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.io.packet.PacketBuilder;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Equipment.EquipmentType;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.WeaponDefinition.Predefined;
import com.runescape.gameserver.world.entity.mob.Appearance;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.UpdateFlags;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task which creates and sends the NPC update block.
 * @author Graham Edgecombe
 *
 */
public class NPCUpdateTask implements Task {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates an npc update task.
	 * @param player The player.
	 */
	public NPCUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		PacketBuilder updateBlock = new PacketBuilder();
		PacketBuilder packet = new PacketBuilder(71, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();
		int npcCount = player.getLocalNPCs().size();
		packet.putBits(8, npcCount);
		for(Iterator<NPC> it$ = player.getLocalNPCs().iterator(); it$.hasNext();) {
			NPC npc = it$.next();
			if(World.getInstance().getNPCs().contains(npc) && npc.isVisible() && !npc.isTeleporting() && npc.getLocation().isWithinDistance(player.getLocation())) {
				updateNPCMovement(packet, npc);
				//if(npc.getUpdateFlags().isUpdateRequired()) {
				//if(npc.getUpdateFlags().isUpdateRequired() || !player.getEntityCooldowns().get(CooldownFlags.NPC_SPAWNS)) {
					updateNPC(updateBlock, npc);
				//}
			} else {
				it$.remove();
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}
		for(NPC npc : World.getInstance().getRegionManager().getLocalNpcs(player)) {
			if(player.getLocalNPCs().size() >= 255) {
				break;
			}
			if(player.getLocalNPCs().contains(npc)) {
				continue;
			}
			player.getLocalNPCs().add(npc);
			addNewNPC(packet, npc);
			//if(npc.getUpdateFlags().isUpdateRequired()) {
			//if(npc.getUpdateFlags().isUpdateRequired() || !player.getEntityCooldowns().get(CooldownFlags.NPC_SPAWNS)) {
				updateNPC(updateBlock, npc);
			//}
		}
		if(!updateBlock.isEmpty()) {
			packet.putBits(14, 16383);
			packet.finishBitAccess();
			packet.put(updateBlock.toPacket().getPayload());
		} else {
			packet.finishBitAccess();
		}
		player.write(packet.toPacket());
	}

	/**
	 * Adds a new NPC.
	 * @param packet The main packet.
	 * @param npc The npc to add.
	 */
	private void addNewNPC(PacketBuilder packet, NPC npc) {
		packet.putBits(14, npc.getIndex());
		int yPos = npc.getLocation().getY() - player.getLocation().getY();
		int xPos = npc.getLocation().getX() - player.getLocation().getX();
		//packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		packet.putBits(1, 1);
		
		packet.putBits(5, yPos);
		packet.putBits(5, xPos);
		packet.putBits(1, 0);
		if(npc.getDefinition() == null) {
			packet.putBits(13, npc.getId());
		} else {
			packet.putBits(13, npc.getDefinition().getId());
		}

	}

	/**
	 * Update an NPC's movement.
	 * @param packet The main packet.
	 * @param npc The npc.
	 */
	private void updateNPCMovement(PacketBuilder packet, NPC npc) {		
		/*
		 * Check if the NPC is running.
		 */
		if(npc.getSprites().getSecondarySprite() == -1) {
			/*
			 * They are not, so check if they are walking.
			 */
			if(npc.getSprites().getPrimarySprite() == -1) {
				/*
				 * They are not walking, check if the NPC needs an update.
				 */
				//if(npc.getUpdateFlags().isUpdateRequired()) {
					/*
					 * Indicate an update is required.
					 */
					packet.putBits(1, 1);
					
					/*
					 * Indicate we didn't move.
					 */
					packet.putBits(2, 0);
				//} else {
					/*
					 * Indicate no update or movement is required.
					 */
				//	packet.putBits(1, 0);
				//}
			} else {
				/*
				 * They are walking, so indicate an update is required.
				 */
				packet.putBits(1, 1);
				
				/*
				 * Indicate the NPC is walking 1 tile.
				 */
				packet.putBits(2, 1);
				
				/*
				 * And write the direction.
				 */
				packet.putBits(3, npc.getSprites().getPrimarySprite());
				
				/*
				 * And write the update flag.
				 */
				//packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				packet.putBits(1, 1);
			}
		} else {
			/*
			 * They are running, so indicate an update is required.
			 */
			packet.putBits(1, 1);

			/*
			 * Indicate the NPC is running 2 tiles.
			 */
			packet.putBits(2, 2);
			
			/*
			 * And write the directions.
			 */
			packet.putBits(3, npc.getSprites().getPrimarySprite());
			packet.putBits(3, npc.getSprites().getSecondarySprite());
			
			/*
			 * And write the update flag.
			 */
			//packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			packet.putBits(1, 1);
		}
	}
	
	/**
	 * Update an NPC.
	 * @param packet The update block.
	 * @param npc The npc.
	 */
	private void updateNPC(PacketBuilder packet, NPC npc) {
		/*
		 * Calculate the mask.
		 */
		int mask = 0;
		final UpdateFlags flags = npc.getUpdateFlags();
	//	if(flags.get(UpdateFlag.TRANSFORM)) {
			mask |= 0x1;
	//	}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			mask |= 0x40;
		}
		if(flags.get(UpdateFlag.HIT)) {
			mask |= 0x80;
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			mask |= 0x4;
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			mask |= 0x400;
		}
		if(flags.get(UpdateFlag.FACE_COORDINATE)) {
			mask |= 0x8;
		}
		if(flags.get(UpdateFlag.ANIMATION)) {
			mask |= 0x2;
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			mask |= 0x10;
		}	
		//if(npc.shouldUpdateAppearance()) {
		//	mask |= 0x200;
		//}	
		/*
		 * And write the mask.
		 */
		if(mask >= 0xFF) {
			mask |= 0x20;
			packet.put((byte) (mask & 0xFF));
			packet.put((byte) (mask >> 8));
		} else {
			packet.put((byte) (mask));
		}
		
		//if(flags.get(UpdateFlag.TRANSFORM)) {
		//	packet.putShortA(npc.getTransformInto());
		//}
		appendNpcAppearanceUpdate(packet, npc);
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			Mob entity = npc.getInteractingEntity();
			packet.putLEShort(entity == null ? -1 : entity.getClientIndex());
		}
		if(flags.get(UpdateFlag.HIT)) {
			packet.putByteA(npc.getDamage().getHitDamage1());
			packet.putByteA(npc.getDamage().getHitType1());
			//packet.put((byte) npc.getHealth());
			//packet.putByteS((byte) npc.getMaxHealth());
			packet.put((byte) npc.getSkills().getLevel(Skill.HITPOINTS));
			packet.putByteS((byte) npc.getSkills().getRealLevel(Skill.HITPOINTS));
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			packet.putShort(npc.getCurrentGraphic().getId());
			packet.putLEInt(npc.getCurrentGraphic().getDelay());
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			packet.putRS2String(npc.getForcedChat());
		}
		if(flags.get(UpdateFlag.FACE_COORDINATE)) {
			Location loc = npc.getFaceLocation();
			if(loc == null) {
				packet.putLEShortA(0);
				packet.putLEShort(0);
			} else {
				packet.putLEShortA(loc.getX() * 2 + 1);
				packet.putLEShort(loc.getY() * 2 + 1);
			}
		}
		if(flags.get(UpdateFlag.ANIMATION)) {
			packet.putShort(npc.getCurrentAnimation().getId());
			packet.putByteS((byte) npc.getCurrentAnimation().getDelay());
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			packet.putByteS((byte) npc.getDamage().getHitDamage2());
			packet.putByteS((byte) npc.getDamage().getHitType2());
			packet.put((byte) npc.getHealth());
			packet.putByteC(npc.getMaxHealth());
		}
		//if(npc.shouldUpdateAppearance()) {
		//}
			
		npc.setUpdateAppearance(false);
	}
	private void appendNpcAppearanceUpdate(PacketBuilder packet, NPC npc) {
		PacketBuilder packetBuilder = new PacketBuilder();
		packetBuilder.put((byte) (npc.isDynamic() ? 0 : 1));
		if(!npc.isDynamic()) {
			Packet propsPacket = packetBuilder.toPacket();
			
			byte[] buffer = new byte[propsPacket.getLength()];
			propsPacket.getReverse(buffer, 0, propsPacket.getLength());

			packet.put((byte) propsPacket.getLength());
			packet.put(buffer);
			return;
		}
		
		Appearance app = npc.getAppearance();
		Container eq = npc.getEquipment();
		//Container eq = new Container(Container.Type.STANDARD, Equipment.SIZE);
		
		packetBuilder.put((byte) app.getGender()); // gender
		packetBuilder.put((byte) (npc.isClickable() ? 1 : 0));
		packetBuilder.put((byte) -1); // skull icon
		packetBuilder.put((byte) -1); // Prayer Icon
		
		for(int i = 0; i < 4; i++) {
			if(eq.isSlotUsed(i)) {
				packetBuilder.putShort((short) 0x200 + eq.get(i).getId());
			} else {
				packetBuilder.put((byte) 0);
			}
		}
		if(eq.isSlotUsed(Equipment.SLOT_CHEST)) {
			packetBuilder.putShort((short) 0x200 + eq.get(Equipment.SLOT_CHEST).getId());
		} else {
			packetBuilder.putShort((short) 0x100 + app.getChest()); // chest
		}
		if(eq.isSlotUsed(Equipment.SLOT_SHIELD)){
			packetBuilder.putShort((short) 0x200 + eq.get(Equipment.SLOT_SHIELD).getId());
		} else {
			packetBuilder.put((byte) 0);
		}
		Item chest = eq.get(Equipment.SLOT_CHEST);
		if(chest != null) {
			if(!Equipment.is(EquipmentType.PLATEBODY, chest)) {
				packetBuilder.putShort((short) 0x100 + app.getArms());
			} else {
				packetBuilder.putShort((short) 0x200 + chest.getId());
			}
		} else {
			packetBuilder.putShort((short) 0x100 + app.getArms());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOTTOMS)) {
			packetBuilder.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOTTOMS).getId());
		} else {
			packetBuilder.putShort((short) 0x100 + app.getLegs());
		}
		Item helm = eq.get(Equipment.SLOT_HELM);
		if(helm != null) {
			if(!Equipment.is(EquipmentType.FULL_HELM, helm) && !Equipment.is(EquipmentType.FULL_MASK, helm)) {
				packetBuilder.putShort((short) 0x100 + app.getHead());
			} else {
				packetBuilder.put((byte) 0);
			}
		} else {
			packetBuilder.putShort((short) 0x100 + app.getHead());
		}
		if(eq.isSlotUsed(Equipment.SLOT_GLOVES)) {
			packetBuilder.putShort((short) 0x200 + eq.get(Equipment.SLOT_GLOVES).getId());
		} else {
			packetBuilder.putShort((short) 0x100 + app.getHands());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOOTS)) {
			packetBuilder.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOOTS).getId());
		} else {
			packetBuilder.putShort((short) 0x100 + app.getFeet());
		}
		boolean fullHelm = false;
		if(helm != null) {
			if(Equipment.is(EquipmentType.FULL_HELM, helm) || Equipment.is(EquipmentType.FULL_MASK, helm)) {
				fullHelm = true;
			}
		}
		if(fullHelm || app.getGender() == 1) {
			packetBuilder.put((byte) 0);
		} else {
			packetBuilder.putShort((short) 0x100 + app.getBeard());
		}
		
		packetBuilder.put((byte) app.getHairColour()); // hairc
		packetBuilder.put((byte) app.getTorsoColour()); // torsoc
		packetBuilder.put((byte) app.getLegColour()); // legc
		packetBuilder.put((byte) app.getFeetColour()); // feetc
		packetBuilder.put((byte) app.getSkinColour()); // skinc
		
		int standAnim = -1;
		int walkAnim = -1;
		int runAnim = -1;
		if(npc.getEquippedWeapon() != null) {
			int[] anims = npc.getEquippedWeapon().getAnims();
			if(npc.getSkills().getRealLevel(Skill.STRENGTH) >= 90 && npc.getEquippedWeapon().getType() == Predefined.TWOHANDEDSWORD) {
				anims = new int[] { -1, -1, -1 };
			}
			if(anims[0] != -1) {
				standAnim = anims[0];
			}
			if(anims[1] != -1) {
				walkAnim = anims[1];
			}
			if(anims[2] != -1) {
				runAnim = anims[2];
			}
		}
		
		packetBuilder.putShort((short) standAnim == -1 ? 0x328 : standAnim); // stand
		packetBuilder.putShort((short) walkAnim == -1 ? 0x337 : walkAnim); // stand turn
		packetBuilder.putShort((short) walkAnim == -1 ? 0x333 : walkAnim); // walk
		packetBuilder.putShort((short) walkAnim == -1 ? 0x334 : walkAnim); // turn 180
		packetBuilder.putShort((short) walkAnim == -1 ? 0x335 : walkAnim); // turn 90 cw
		packetBuilder.putShort((short) walkAnim == -1 ? 0x336 : walkAnim); // turn 90 ccw
		packetBuilder.putShort((short) runAnim == -1 ? 0x338 : runAnim); // run
		
		packetBuilder.putRS2String(npc.getName());
		packetBuilder.putRS2String(npc.getDescription());
		packetBuilder.put((byte) npc.getSkills().getCombatLevel());
		
		String[] actions = npc.getActions();
		packetBuilder.put((byte) (actions == null ? 0 : actions.length));
		if(actions != null) {
			for(int i = 0; i < actions.length; i++) {
				String action = "n/a";
				if(actions[i] != null) {
					action = actions[i];
				}
				
				packetBuilder.putRS2String(action);
			}
		}
		
		Packet propsPacket = packetBuilder.toPacket();
		
		byte[] buffer = new byte[propsPacket.getLength()];
		propsPacket.getReverse(buffer, 0, propsPacket.getLength());
		packet.put((byte) propsPacket.getLength());
		packet.put(buffer);
	}

}
