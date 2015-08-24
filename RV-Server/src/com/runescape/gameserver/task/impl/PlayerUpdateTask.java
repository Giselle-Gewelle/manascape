package com.runescape.gameserver.task.impl;

import java.util.Iterator;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.io.packet.PacketBuilder;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.ChatMessage;
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
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task which creates and sends the player update block.
 * @author Graham Edgecombe
 *
 */
public class PlayerUpdateTask implements Task {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates an update task.
	 * @param player The player.
	 */
	public PlayerUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		if(player.isMapRegionChanging()) {
			player.getPacketSender().sendMapRegion();
		}
		PacketBuilder updateBlock = new PacketBuilder();
		PacketBuilder packet = new PacketBuilder(90, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();
		updateThisPlayerMovement(packet);
		updatePlayer(updateBlock, player, false, true);
		packet.putBits(8, player.getLocalPlayers().size());
		for(Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext();) {
			Player otherPlayer = it$.next();
			if(World.getInstance().getPlayers().contains(otherPlayer) && !otherPlayer.isTeleporting() && otherPlayer.getLocation().isWithinDistance(player.getLocation())) {
				updatePlayerMovement(packet, otherPlayer);
				if(otherPlayer.getUpdateFlags().isUpdateRequired()) {
					updatePlayer(updateBlock, otherPlayer, false, false);
				}
			} else {
				it$.remove();
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}
		for(Player otherPlayer : World.getInstance().getRegionManager().getLocalPlayers(player)) {
			if(player.getLocalPlayers().size() >= 255) {
				break;
			}
			if(otherPlayer == player || player.getLocalPlayers().contains(otherPlayer)) {
				continue;
			}
			player.getLocalPlayers().add(otherPlayer);
			addNewPlayer(packet, otherPlayer);
			updatePlayer(updateBlock, otherPlayer, true, false);
		}
		if(!updateBlock.isEmpty()) {
			packet.putBits(11, 2047);
			packet.finishBitAccess();
			packet.put(updateBlock.toPacket().getPayload());
		} else {
			packet.finishBitAccess();
		}
		player.write(packet.toPacket());
	}

	/**
	 * Updates a non-this player's movement.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	public void updatePlayerMovement(PacketBuilder packet, Player otherPlayer) {
		if(otherPlayer.getSprites().getPrimarySprite() == -1) {
			if(otherPlayer.getUpdateFlags().isUpdateRequired()) {
				packet.putBits(1, 1);
				packet.putBits(2, 0);
			} else {
				packet.putBits(1, 0);
			}
		} else if(otherPlayer.getSprites().getSecondarySprite() == -1) {
			packet.putBits(1, 1);
			packet.putBits(2, 1);
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		} else {
			packet.putBits(1, 1);
			packet.putBits(2, 2);
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());
			packet.putBits(3, otherPlayer.getSprites().getSecondarySprite());
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Adds a new player.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	public void addNewPlayer(PacketBuilder packet, Player otherPlayer) {
		int yPos = otherPlayer.getLocation().getY() - player.getLocation().getY();
		int xPos = otherPlayer.getLocation().getX() - player.getLocation().getX();
		packet.putBits(11, otherPlayer.getIndex());
		packet.putBits(5, xPos);
		packet.putBits(1, 1);
		packet.putBits(1, 1);	
		packet.putBits(5, yPos);
	}
	
    private static void appendHit2Update(final Player p, final PacketBuilder updateBlock) {
    	updateBlock.putByteA((byte) p.getDamage().getHitDamage2());
    	updateBlock.putByteS((byte) p.getDamage().getHitType2());
    	updateBlock.putByteC((byte) p.getSkills().getLevel(3));
    	updateBlock.put((byte) p.getSkills().getLevelForExperience(Skill.HITPOINTS));
    }

    private static void appendHitUpdate(final Player p, final PacketBuilder updateBlock) {
    	updateBlock.putByteS((byte) p.getDamage().getHitDamage1());
    	updateBlock.putByteC((byte) p.getDamage().getHitType1());
    	updateBlock.putByteS((byte) p.getSkills().getLevel(3));
    	updateBlock.put((byte) p.getSkills().getLevelForExperience(Skill.HITPOINTS));
    }

	/**
	 * Updates a player.
	 * @param packet The packet.
	 * @param otherPlayer The other player.
	 * @param forceAppearance The force appearance flag.
	 * @param noChat Indicates chat should not be relayed to this player.
	 */
	public void updatePlayer(PacketBuilder packet, Player otherPlayer, boolean forceAppearance, boolean noChat) {
		if(!otherPlayer.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}
		synchronized(otherPlayer) {
			if(otherPlayer.hasCachedUpdateBlock() && otherPlayer != player && !forceAppearance && !noChat) {
				packet.put(otherPlayer.getCachedUpdateBlock().getPayload().flip());
				return;
			}
			PacketBuilder block = new PacketBuilder();
			int mask = 0;
			final UpdateFlags flags = otherPlayer.getUpdateFlags();
			
			if(flags.get(UpdateFlag.ANIMATION)) {
				mask |= 0x8;
			}
			if(flags.get(UpdateFlag.FORCED_CHAT)) {
				mask |= 0x10;
			}
			// Async Walking/Animation
			if(flags.get(UpdateFlag.FACE_ENTITY)) {
				mask |= 0x1;
			}
			if(flags.get(UpdateFlag.FACE_COORDINATE)) {
				mask |= 0x2;
			}			
			if(flags.get(UpdateFlag.GRAPHICS)) {
				mask |= 0x200;
			}
			if(flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				mask |= 0x4;
			}
			if(flags.get(UpdateFlag.HIT_2)) {
				mask |= 0x400;
			}			
			if(flags.get(UpdateFlag.CHAT) && !noChat) {
				mask |= 0x40;
			}
			if(flags.get(UpdateFlag.HIT)) {
				mask |= 0x80;
			}			
			if(mask >= 0xFF) {
				mask |= 0x20;
				block.put((byte) (mask & 0xFF));
				block.put((byte) (mask >> 8));
			} else {
				block.put((byte) (mask));
			}			
			if(flags.get(UpdateFlag.ANIMATION)) {
				appendAnimationUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.FORCED_CHAT)) {
				block.putRS2String(player.getForcedChat());
			}
			if(flags.get(UpdateFlag.FACE_ENTITY)) {
				Mob entity = otherPlayer.getInteractingEntity();
				block.putShortA(entity == null ? -1 : entity.getClientIndex());
			}
			if(flags.get(UpdateFlag.FACE_COORDINATE)) {
				Location loc = otherPlayer.getFaceLocation();
				if(loc == null) {
					block.putShort(0);
					block.putShort(0);
				} else {
					block.putShort(loc.getX() * 2 + 1);
					block.putShort(loc.getY() * 2 + 1);
				}
			}			
			if(flags.get(UpdateFlag.GRAPHICS)) {
				appendGraphicsUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				appendPlayerAppearanceUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.HIT_2)) {
				appendHit2Update(otherPlayer, block);
			}
			if(flags.get(UpdateFlag.CHAT) && !noChat) {
				appendChatUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.HIT)) {
				appendHitUpdate(otherPlayer, block);
			}			
			/*
			 * Convert the block builder to a packet.
			 */
			Packet blockPacket = block.toPacket();
			
			/*
			 * Now it is over, cache the block if we can.
			 */
			if(otherPlayer != player && !forceAppearance && !noChat) {
				otherPlayer.setCachedUpdateBlock(blockPacket);
			}
		
			/*
			 * And finally append the block at the end.
			 */
			packet.put(blockPacket.getPayload());
		}
	}
	
	/**
	 * Appends an animation update.
	 * @param block The update block.
	 * @param otherPlayer The player.
	 */
	private void appendAnimationUpdate(PacketBuilder block, Player otherPlayer) {
		block.putShort(otherPlayer.getCurrentAnimation().getId());
		block.putByteS((byte) otherPlayer.getCurrentAnimation().getDelay());
	}

	/**
	 * Appends a graphics update.
	 * @param block The update block.
	 * @param otherPlayer The player.
	 */
	private void appendGraphicsUpdate(PacketBuilder block, Player otherPlayer) {
		block.putShortA(otherPlayer.getCurrentGraphic().getId());
		//block.putInt2(otherPlayer.getCurrentGraphic().getDelay() + otherPlayer.getCurrentGraphic().getHeight() << 16);
		block.putInt1(otherPlayer.getCurrentGraphic().getDelay());
	}

	/**
	 * Appends a chat text update.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	private void appendChatUpdate(PacketBuilder packet, Player otherPlayer) {
		ChatMessage cm = otherPlayer.getCurrentChatMessage();
		byte[] bytes = cm.getText();
		packet.putShort(((cm.getColour() & 0xFF) << 8) + (cm.getEffects() & 0xFF));
		packet.putByteC((byte) otherPlayer.getRights().toInteger());
		packet.putByteA(bytes.length);
		for (byte aByte : bytes) {
			packet.putByteA(aByte);
		}
	}
	
	/**
	 * Appends an appearance update.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	private void appendPlayerAppearanceUpdate(PacketBuilder packet, Player otherPlayer) {
		Appearance app = otherPlayer.getAppearance();
		Container eq = otherPlayer.getEquipment();
		
		PacketBuilder playerProps = new PacketBuilder();
		playerProps.put((byte) app.getGender()); // gender
		playerProps.put((byte) -1); // skull icon
		playerProps.put((byte) player.getPrayer().getHeadIcon()); // Prayer Icon
		
		for(int i = 0; i < 4; i++) {
			if(eq.isSlotUsed(i)) {
				playerProps.putShort((short) 0x200 + eq.get(i).getId());
			} else {
				playerProps.put((byte) 0);
			}
		}
		if(eq.isSlotUsed(Equipment.SLOT_CHEST)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_CHEST).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getChest()); // chest
		}
		if(eq.isSlotUsed(Equipment.SLOT_SHIELD)){
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_SHIELD).getId());
		} else {
			playerProps.put((byte) 0);
		}
		Item chest = eq.get(Equipment.SLOT_CHEST);
		if(chest != null) {
			if(!Equipment.is(EquipmentType.PLATEBODY, chest)) {
				playerProps.putShort((short) 0x100 + app.getArms());
			} else {
				playerProps.putShort((short) 0x200 + chest.getId());
			}
		} else {
			playerProps.putShort((short) 0x100 + app.getArms());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOTTOMS)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOTTOMS).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getLegs());
		}
		Item helm = eq.get(Equipment.SLOT_HELM);
		if(helm != null) {
			if(!Equipment.is(EquipmentType.FULL_HELM, helm) && !Equipment.is(EquipmentType.FULL_MASK, helm)) {
				playerProps.putShort((short) 0x100 + app.getHead());
			} else {
				playerProps.put((byte) 0);
			}
		} else {
			playerProps.putShort((short) 0x100 + app.getHead());
		}
		if(eq.isSlotUsed(Equipment.SLOT_GLOVES)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_GLOVES).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getHands());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOOTS)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOOTS).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getFeet());
		}
		boolean fullHelm = false;
		if(helm != null) {
			if(Equipment.is(EquipmentType.FULL_HELM, helm) || Equipment.is(EquipmentType.FULL_MASK, helm)) {
				fullHelm = true;
			}
		}
		if(fullHelm || app.getGender() == 1) {
			playerProps.put((byte) 0);
		} else {
			playerProps.putShort((short) 0x100 + app.getBeard());
		}
		
		playerProps.put((byte) app.getHairColour()); // hairc
		playerProps.put((byte) app.getTorsoColour()); // torsoc
		playerProps.put((byte) app.getLegColour()); // legc
		playerProps.put((byte) app.getFeetColour()); // feetc
		playerProps.put((byte) app.getSkinColour()); // skinc
		
		int standAnim = -1;
		int walkAnim = -1;
		int runAnim = -1;
		if(otherPlayer.getEquippedWeapon() != null) {
			int[] anims = otherPlayer.getEquippedWeapon().getAnims();
			if(otherPlayer.getSkills().getRealLevel(Skill.STRENGTH) >= 90 && otherPlayer.getEquippedWeapon().getType() == Predefined.TWOHANDEDSWORD) {
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
		
		playerProps.putShort((short) standAnim == -1 ? 0x328 : standAnim); // stand
		playerProps.putShort((short) walkAnim == -1 ? 0x337 : walkAnim); // stand turn
		playerProps.putShort((short) walkAnim == -1 ? 0x333 : walkAnim); // walk
		playerProps.putShort((short) walkAnim == -1 ? 0x334 : walkAnim); // turn 180
		playerProps.putShort((short) walkAnim == -1 ? 0x335 : walkAnim); // turn 90 cw
		playerProps.putShort((short) walkAnim == -1 ? 0x336 : walkAnim); // turn 90 ccw
		playerProps.putShort((short) runAnim == -1 ? 0x338 : runAnim); // run
		
		//playerProps.putLong(otherPlayer.getNameAsLong()); // player name
		playerProps.putRS2String(otherPlayer.getName());
		playerProps.put((byte) otherPlayer.getSkills().getCombatLevel()); // combat level
		playerProps.putShort(0); // (skill-level instead of combat-level) otherPlayer.getSkills().getTotalLevel()); // total level
		
		Packet propsPacket = playerProps.toPacket();
		
		byte[] buffer = new byte[propsPacket.getLength()];
		propsPacket.getReverse(buffer, 0, propsPacket.getLength());

		packet.put((byte) propsPacket.getLength());
		packet.put(buffer);
	}

	/**
	 * Updates this player's movement.
	 * @param packet The packet.
	 */
	private void updateThisPlayerMovement(PacketBuilder packet) {
		if(player.isTeleporting() || player.isMapRegionChanging()) {			
			packet.putBits(1, 1);
			packet.putBits(2, 3);
			packet.putBits(1, player.isTeleporting() ? 1 : 0);
			packet.putBits(2, player.getLocation().getHeight());
			packet.putBits(7, player.getLocation().getLocalY(player.getLastKnownRegion()));
			packet.putBits(7, player.getLocation().getLocalX(player.getLastKnownRegion()));
			packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		} else {
			if(player.getSprites().getPrimarySprite() == -1) {
				if(player.getUpdateFlags().isUpdateRequired()) {
					packet.putBits(1, 1);
					packet.putBits(2, 0);
				} else {
					packet.putBits(1, 0);
				}
			} else {
				if(player.getSprites().getSecondarySprite() == -1) {
					packet.putBits(1, 1);
					packet.putBits(2, 1);
					packet.putBits(3, player.getSprites().getPrimarySprite());
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				} else {
					packet.putBits(1, 1);
					packet.putBits(2, 2);
					packet.putBits(3, player.getSprites().getPrimarySprite());
					packet.putBits(3, player.getSprites().getSecondarySprite());
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				}
			}
		}
	}

}
