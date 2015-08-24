package com.runescape.gameserver.world.entity.mob.player.packet;

import java.awt.Color;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.io.packet.PacketBuilder;
import com.runescape.gameserver.io.packet.Packet.Type;
import com.runescape.gameserver.world.GroundItem;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.Palette;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.Palette.PaletteTile;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;
import com.runescape.gameserver.world.region.Region;

public class PacketSender {

	private Player player;

	public PacketSender(Player player) {
		this.player = player;
	}

	public void send216(int arg0, int arg1) {
		PacketBuilder packet = new PacketBuilder(216);
		packet.putLEShortA(arg0);
		packet.putLEShortA(arg1);
		player.write(packet.toPacket());
	}
	
	public void sendConstructMapRegion(Palette palette) {
		player.setLastKnownRegion(player.getLocation());
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShortA(player.getLocation().getRegionX() + 6);
		bldr.startBitAccess();
		for(int z = 0; z < 4; z++) {
			for(int x = 0; x < 13; x++) {
				for(int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					bldr.putBits(1, tile != null ? 1 : 0);
					if(tile != null) {
						bldr.putBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
					}
				}
			}
		}
		bldr.finishBitAccess();
		bldr.putShortA(player.getLocation().getRegionY() + 6);
		player.write(bldr.toPacket());
	}

	public void sendMapRegion() {
		player.setLastKnownRegion(player.getLocation());
		player.write(new PacketBuilder(222).putShort(player.getLocation().getRegionY() + 6).putLEShortA(player.getLocation().getRegionX() + 6).toPacket());
		sendObjectsInArea();
		sendGroundItemsInArea();
	}

	public void sendDetails() {
		player.write(new PacketBuilder(126).put((byte) (player.isMembers() ? 1 : 0)).putLEShort(player.getIndex()).toPacket());
		player.write(new PacketBuilder(148).toPacket());
	}

	public void sendSkills() {
		for(Skill s : Skill.values()) {
			sendSkill(s);
		}
	}

	public void sendSkill(Skill skill) {
		PacketBuilder bldr = new PacketBuilder(49);
		bldr.putByteC((byte) skill.getId());
		bldr.put((byte) player.getSkills().getLevel(skill));
		bldr.putInt((int) player.getSkills().getExperience(skill));
		player.write(bldr.toPacket());
	}

	public void sendSidebarIconFlash(int sideBar) {
		player.getSession().write(new PacketBuilder(238).put((byte) sideBar).toPacket());
	}

	public void sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for(int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
	}

	public void sendSidebarInterface(int icon, int interfaceId) {
		player.write(new PacketBuilder(10).putByteS((byte) icon).putShortA(interfaceId).toPacket());
	}

	public void sendInterface(int interfaceId) {
		PacketBuilder packet = new PacketBuilder(159);
		packet.putLEShortA(interfaceId);
		player.write(packet.toPacket());
	}

	public void sendChatBoxInterface(int interfaceId) {
		PacketBuilder packet = new PacketBuilder(109);
		packet.putShort(interfaceId);
		player.write(packet.toPacket());
	}

	public void sendInterfaceInventory(int interfaceId, int inventoryInterfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(128).putShortA(interfaceId).putLEShortA(inventoryInterfaceId).toPacket());
	}

	public void sendDialogueInterface(int interfaceId) {
		player.getSession().write(new PacketBuilder(158).putLEShort(interfaceId).toPacket());
	}

	public void sendPlayerHead(int interfaceId) {
		PacketBuilder packet = new PacketBuilder(255);
		packet.putLEShortA(interfaceId);
		player.write(packet.toPacket());
	}

	public void sendNpcHead(int interfaceId, int npcId) {
		PacketBuilder packet = new PacketBuilder(162);
		packet.putShortA(npcId);
		//packet.putShortA(npc.getIndex());
		packet.putLEShort(interfaceId);
		player.write(packet.toPacket());
	}

	public void sendInterfaceAnimation(int interfaceId, int animationId) {
		PacketBuilder packet = new PacketBuilder(2);
		packet.putLEShortA(interfaceId);
		packet.putShortA(animationId);
		player.write(packet.toPacket());
	}

	/*
	 * Wildy Icon top left = 196 Wildy Icon Correct place = 197 Duel Arena Icon
	 * = 201
	 */
	public void sendWalkableInterface(int interfaceId) {
		player.getSession().write(new PacketBuilder(50).putShort(interfaceId).toPacket());
	}

	public void sendFullScreenInterface(int interfaceId, int interfaceId2) {
		player.getSession().write(new PacketBuilder(253).putLEShort(interfaceId).putShortA(interfaceId2).toPacket());
	}

	public void sendCloseInterfaces() {
		player.write(new PacketBuilder(29).toPacket());
	}

	public void sendInterfaceConfig(int id, boolean value) {
		PacketBuilder bldr = new PacketBuilder(82);
		bldr.put((byte) (value ? 1 : 0));
		bldr.putShort(id);
		player.getSession().write(bldr.toPacket());
	}

	public void sendMessage(String message) {
		player.write(new PacketBuilder(63, Type.VARIABLE).putRS2String(message).toPacket());
	}

	public void sendLogout() {
		player.write(new PacketBuilder(5).toPacket()); // TODO IoFuture
	}

	public void sendUpdateItems(int interfaceId, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(206, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for(Item item : items) {
			if(item != null) {
				bldr.putLEShortA(item.getId() + 1);
				int count = item.getCount();
				if(count > 254) {
					bldr.putByteC((byte) 255);
					bldr.putLEInt(count);
				} else {
					bldr.putByteC((byte) count);
				}
			} else {
				bldr.putLEShortA(0);
				bldr.putByteC((byte) 0);
			}
		}
		player.write(bldr.toPacket());
	}

	public void sendUpdateItem(int interfaceId, int slot, Item item) {
		PacketBuilder bldr = new PacketBuilder(134, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId).putSmart(slot);
		if(item != null) {
			bldr.putShort(item.getId() + 1);
			int count = item.getCount();
			if(count > 254) {
				bldr.put((byte) 255);
				bldr.putInt(count);
			} else {
				bldr.put((byte) count);
			}
		} else {
			bldr.putShort(0);
			bldr.put((byte) 0);
		}
		player.write(bldr.toPacket());
	}

	public void sendUpdateItems(int interfaceId, int[] slots, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(134, Type.VARIABLE_SHORT).putShort(interfaceId);
		for(int slot : slots) {
			Item item = items[slot];
			bldr.putSmart(slot);
			if(item != null) {
				bldr.putShort(item.getId() + 1);
				int count = item.getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.put((byte) count);
				}
			} else {
				bldr.putShort(0);
				bldr.put((byte) 0);
			}
		}
		player.write(bldr.toPacket());
	}

	public void sendEnterAmountInterface() {
		player.write(new PacketBuilder(58).toPacket());
	}

	public void sendInteractionOption(String option, int slot, boolean top) {
		PacketBuilder bldr = new PacketBuilder(157, Type.VARIABLE);
		bldr.putByteC((byte) slot);
		bldr.putRS2String(option);
		bldr.put(top ? (byte) 0 : (byte) 1);
		player.write(bldr.toPacket());
	}

	public void sendString(int id, String string) {
		PacketBuilder bldr = new PacketBuilder(232, Type.VARIABLE_SHORT);
		bldr.putLEShortA(id);
		bldr.putRS2String(string);
		player.write(bldr.toPacket());
	}

	public void sendInterfaceModel(int id, int zoom, int model) {
		PacketBuilder bldr = new PacketBuilder(21);
		bldr.putShort(zoom).putLEShort(model).putLEShortA(id);
		player.write(bldr.toPacket());
	}

	public void sendTextColor(int childId, Color colour) {
		int r = (colour.getRed() >> 3) & 0x1F;
		int g = (colour.getGreen() >> 3) & 0x1F;
		int b = (colour.getBlue() >> 3) & 0x1F;
		PacketBuilder packet = new PacketBuilder(218);
		packet.putShort(childId);
		packet.putShortA((r << 10) | (g << 5) | b);
		player.write(packet.toPacket());
	}

	public void sendFriendServer(int status) {
		PacketBuilder packet = new PacketBuilder(251);
		packet.put((byte) status);
		player.write(packet.toPacket());
	}

	public void sendFriendStatus(long name, int worldId) {
		PacketBuilder packet = new PacketBuilder(78);
		if(worldId != 0) {
			worldId += 9;
		}
		packet.putLong(name);
		packet.put((byte) (worldId));
		player.write(packet.toPacket());
	}

	public void sendPrivateMessage(long name, int rights, byte[] message, int messageSize) {
		PacketBuilder packet = new PacketBuilder(135);
		packet.putLong(name);
		// packet.putLEInt(player.getPrivateMessage().getLastMessageIndex());
		packet.put((byte) rights);
		packet.put(message, 0, messageSize);
		player.write(packet.toPacket());
	}
	
	public void sendRunStatus(boolean running) {
		player.getSession().write(new PacketBuilder(124).put((byte) (running ? 1 : 0)).toPacket());
	}

	public void sendRunEnergy() {
		player.getSession().write(new PacketBuilder(125).put((byte) player.getRunEnergy()).toPacket());
	}

	public void sendSystemUpdate(int time) {
		player.getSession().write(new PacketBuilder(190).putLEShort(time).toPacket());
	}

	public void sendMiniMapState(int state) {
		player.getSession().write(new PacketBuilder(156).put((byte) state).toPacket());
	}

	public void sendMultiWayIcon(int state) {
		player.write(new PacketBuilder(233).put((byte) state).toPacket());
	}

	public void sendWelcomeScreen() {
		PacketBuilder packet = new PacketBuilder(76);
		packet.putLEShort(0);
		packet.putLEShortA(0);
		packet.putShort(0);
		packet.putShort(0);
		packet.putLEShort(1337); // Days Ago
		packet.putShortA(2); // Messages
		packet.putShortA(0);
		packet.putShort(0);
		packet.putLEInt(0);
		packet.putLEShort(0);
		packet.putByteS((byte) 0);
		sendFullScreenInterface(5993, 15244);
		player.write(packet.toPacket());
	}

	public void sendGroundItem(Location location, int itemId, int offset, int itemAmount) {
		PacketBuilder packet = new PacketBuilder(107);
		sendSetCurrentPlacement(location);
		packet.putShort(itemId);
		packet.putByteC(offset);
		packet.putShortA(itemAmount);
		player.write(packet.toPacket());
	}

	public void sendGroundItem(GroundItem groundItem) {
		sendGroundItem(groundItem.getLocation(), groundItem.getItem().getId(), 0, groundItem.getItem().getCount());
	}

	public void sendRemoveGroundItem(Location location, int itemId, int offset) {
		PacketBuilder packet = new PacketBuilder(208);
		sendSetCurrentPlacement(location);
		packet.putShortA(itemId);
		packet.putByteA((byte) offset);
		player.write(packet.toPacket());
	}

	public void sendRemoveGroundItem(GroundItem groundItem) {
		sendRemoveGroundItem(groundItem.getLocation(), groundItem.getItem().getId(), 0);
	}

	public void sendSetCurrentLocalPlacement(Location location) {
		PacketBuilder packet = new PacketBuilder(75);
		packet.putByteC(location.getLocalX());
		packet.putByteA(location.getLocalY());
		player.write(packet.toPacket());
	}

	public void sendSetCurrentPlacement(Location location) {
		PacketBuilder packet = new PacketBuilder(75);
		packet.putByteC(location.getX() - (player.getLastKnownRegion().getRegionX() * 8));
		packet.putByteA(location.getY() - (player.getLastKnownRegion().getRegionY() * 8));
		player.write(packet.toPacket());
	}

	public void sendSetCurrentEntityPlacement(Location location) {
		PacketBuilder packet = new PacketBuilder(183);
		packet.put((byte) (location.getX() - (player.getLastKnownRegion().getRegionX())));
		packet.putByteA(location.getY() - (player.getLastKnownRegion().getRegionY()));
		player.write(packet.toPacket());
	}

	public void sendConfig(int id, int value) {
		if(value < 255) {
			sendConfig1(id, value);
		} else {
			sendConfig2(id, value);
		}
	}

	public void sendConfig1(int id, int value) {
		System.out.println("config 1 value = " + value);
		PacketBuilder bldr = new PacketBuilder(182);
		bldr.putShortA(id);
		bldr.putByteS((byte) value);
		player.getSession().write(bldr.toPacket());
	}

	public void sendConfig2(int settingId, int settingState) {
		PacketBuilder packet = new PacketBuilder(115);
		packet.putInt2(settingState);
		packet.putLEShort(settingId);
		player.write(packet.toPacket());
	}

	public void sendSynchronizeConfigs() {
		player.write(new PacketBuilder(113).toPacket());
	}

	public void sendHintIconNPC(int index) {
		PacketBuilder packet = new PacketBuilder(199);
		packet.put((byte) 1);
		packet.putShort(index);
		packet.putShort(0);
		packet.put((byte) 0);
		player.write(packet.toPacket());
	}

	public void sendHintIconLocation(int type, int xCoord, int yCoord, byte zCoord) {
		PacketBuilder packet = new PacketBuilder(199);
		packet.put((byte) type);
		packet.putShort(xCoord);
		packet.putShort(yCoord);
		packet.put(zCoord);
		player.write(packet.toPacket());
	}

	public void sendHintIconPlayer(int index) {
		PacketBuilder packet = new PacketBuilder(199);
		packet.put((byte) 10);
		packet.putShort(index);
		packet.putShort(0);
		packet.put((byte) 0);
		player.write(packet.toPacket());
	}

	public void sendObjectAnimation(int objectX, int objectY, int animationId, int objectType, int orientation) {
		for(Player players : player.getRegion().getPlayers()) {
			PacketBuilder packet = new PacketBuilder(142);
			sendSetCurrentPlacement(Location.create(objectX, objectY, players.getLocation().getHeight()));
			packet.putShort(animationId);
			packet.putByteA((byte) ((objectType << 2) + (orientation & 3)));
			packet.put((byte) 0);
			players.write(packet.toPacket());
		}
	}

	public void sendAddObject(Location location, int objectId, int objectType, int orientation) {
		PacketBuilder packet = new PacketBuilder(152);
		sendSetCurrentPlacement(location);
		packet.putByteC((byte) (objectType << 2) + (orientation & 3));
		packet.putLEShortA(objectId);
		packet.putByteA((byte) 0);
		player.write(packet.toPacket());
	}

	public void sendAddGlobalObject(Location location, int objectId, int objectType, int orientation) {
		for(Player players : player.getRegion().getPlayers()) {
			PacketBuilder packet = new PacketBuilder(152);
			sendSetCurrentPlacement(location);
			packet.putByteC((byte) (objectType << 2) + (orientation & 3));
			packet.putLEShortA(objectId);
			packet.putByteA((byte) 0);
			players.write(packet.toPacket());
		}
	}

	public void sendRemoveObject(Location location, int objectType, int orientation) {
		PacketBuilder packet = new PacketBuilder(88);
		sendSetCurrentPlacement(location);
		packet.putByteS((byte) 0);
		packet.putByteS((byte) ((objectType << 2) + (orientation & 3)));
		player.write(packet.toPacket());
	}

	public void sendProjectile(Location start, Location finish, int id, int delay, int speed, int startHeight, int endHeight, int lockon, int slope) {
		int offsetX = (start.getX() - finish.getX()) * -1;
		int offsetY = (start.getY() - finish.getY()) * -1;
		sendSetCurrentPlacement(start);

		PacketBuilder packet = new PacketBuilder(181);
		packet.put((byte) 0);// Angle
		packet.put((byte) offsetY);
		packet.put((byte) offsetX);
		packet.putShort(lockon);
		packet.putShort(id);
		packet.put((byte) startHeight);
		packet.put((byte) endHeight);
		packet.putShort(delay);
		packet.putShort(speed);
		packet.put((byte) slope);
		packet.put((byte) 64);// Radius
		player.write(packet.toPacket());
	}

	public void sendObjectsInArea() {
		Region[] regions = World.getInstance().getRegionManager().getSurroundingRegions(player.getLocation());
		for(Region r : regions) {
			for(GameObject obj : r.getGameObjects()) {
				if(!obj.loadedInLandscape()) {
					sendAddObject(obj.getLocation(), obj.getDefinition().getId(), obj.getType(), obj.getRotation());
				}
			}
		}
	}

	public void sendGroundItemsInArea() {
		// TODO check distance!!
		for(Region r : World.getInstance().getRegionManager().getSurroundingRegions(player.getLocation())) {
			for(GroundItem item : r.getGroundItems()) {
				if(item.isOwnedBy(player.getName())) {
					sendGroundItem(item);
				}
			}
		}
	}

	public void sendSound(int soundId, int volume, int delay) {
		PacketBuilder packet = new PacketBuilder(26);
		packet.putShort(soundId);
		packet.put((byte) volume);
		packet.putShort(delay);
		player.write(packet.toPacket());
	}

	public void sendSound(int soundId) {
		sendSound(soundId, 000, 000);
	}

}
