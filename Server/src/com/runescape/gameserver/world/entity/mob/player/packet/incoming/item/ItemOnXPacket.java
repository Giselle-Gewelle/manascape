package com.runescape.gameserver.world.entity.mob.player.packet.incoming.item;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.skills.impl.Firemaking;
import com.runescape.gameserver.world.content.skills.impl.Fletching;
import com.runescape.gameserver.world.content.skills.impl.Firemaking.Log;
import com.runescape.gameserver.world.content.skills.impl.Fletching.KnifeAction;
import com.runescape.gameserver.world.content.skills.impl.cooking.Pie;
import com.runescape.gameserver.world.content.skills.impl.cooking.PieMaking;
import com.runescape.gameserver.world.content.skills.impl.cooking.PieMaking.Pies;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;
import com.runescape.gameserver.world.entity.object.GameObject;

public class ItemOnXPacket extends IncomingPacket {

	private static final int ITEM_ON_INVENTORY_ITEM = 1;
	private static final int ITEM_ON_NPC = 57;
	private static final int ITEM_ON_PLAYER = 143;
	private static final int ITEM_ON_OBJECT = 152;
	private static final int ITEM_ON_GROUND_ITEM = 211;	
	
	public ItemOnXPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) throws Throwable {
		switch(packet.getOpcode()) {
			case ITEM_ON_INVENTORY_ITEM: 
				handItemOnInventoryItem(player, packet);
				break;			
			case ITEM_ON_NPC:
				handleItemOnNpc(player, packet);
				break;
			case ITEM_ON_PLAYER:
				handleItemOnPlayer(player, packet);
				break;
			case ITEM_ON_OBJECT:
				handleItemOnObject(player, packet);
				break;
			case ITEM_ON_GROUND_ITEM:
				handleItemOnGroundItem(player, packet);
				break;
		}
	}

	private void handleItemOnGroundItem(Player player, Packet packet) {
		int slotId = packet.getLEShortA();
		int itemId = packet.getShortA();
		int yCoord = packet.getLEShortA();
		int xCoord = packet.getLEShortA();
		int interfaceId = packet.getLEShort();
		int groundItemId = packet.getLEShort();
		System.out.println("Item Id: " + itemId + " Interface Id: " + interfaceId + 
				" X: " + xCoord +  " Y: " + yCoord + " Slot Id: " + slotId + " Ground Item: " + groundItemId );		
	}

	private void handleItemOnObject(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int objectId = packet.getLEShort();
		int interfaceId = packet.getLEShort();
		int itemId = packet.getLEShort();
		int yCoord = packet.getLEShort();
		int slotId = packet.getShort();
		int xCoord = packet.getLEShortA();
		Location objectLocation = Location.create(xCoord, yCoord);
		GameObject o = World.getInstance().getRegionManager().getObjectAtLocation(objectLocation);
		
		if (o == null) {
			System.out.println("null object");
			return;
		}
		player.sendOMessage("Object Id: " + o.getDefinition().getId() + " X: " + o.getLocation().getX() + " Y: " + o.getLocation().getY() + " Interface Id: " + interfaceId + " Slot Id: " + slotId + " Item Id: " + itemId);
		
		player.face(objectLocation);
		if (o.handleItemOnUs(player, new Item(itemId)))
			return;
	}

	private void handleItemOnPlayer(Player player, Packet packet) {
		int interfaceId = packet.getLEShort();
		int playerIndex = packet.getLEShortA();
		int itemId = packet.getShort();
		int slotId = packet.getShortA();
		System.out.println("Item Id: " +itemId + " Slot Id: " + slotId + " Player Index: " + playerIndex + " Interface Id: " + interfaceId);
		
	}

	private void handleItemOnNpc(Player player, Packet packet) {
		int slotId = packet.getShort();
		int itemId = packet.getLEShort();
		int interfaceId = packet.getLEShortA();
		int npcIndex = packet.getShort();
		System.out.println("Item Id: " + itemId + " Slot Id: " + slotId + " Npc Index: " + npcIndex + " InterfaceId: " + interfaceId);
		
	}

	private void handItemOnInventoryItem(Player player, Packet packet) {
		int usedWithItemId = packet.getShort();
		int usedSlotId = packet.getLEShort();
		int usedItemId = packet.getLEShort();
		@SuppressWarnings("unused")
		int interfaceId2 = packet.getLEShortA();
		int usedWithSlotId = packet.getShortA();
		@SuppressWarnings("unused")
		int interfaceId = packet.getShortA();
		//System.out.println("Used With Item Id: " + usedWithItemId + " Used With Slot: " + usedWithSlotId + " Used Item Id: " + usedItemId + " Interface Id 2: " + interfaceId2 + " Slot Id: " + usedSlotId + " Interface Id: " + interfaceId);		

		/*
		 * Make sure the items are valid and in their correct slots.
		 */
		if(usedSlotId < 0 || usedSlotId > 27 || usedWithSlotId < 0 || usedWithSlotId > 27) {
			return;
		}
		//int slot1 = player.getInventory().getSlotById(usedWithItemId);
		//int slot2 = player.getInventory().getSlotById(usedItemId);
		Item item1 = player.getInventory().get(usedSlotId);
		Item item2 = player.getInventory().get(usedWithSlotId);
		if(item1.getId() != usedItemId) {
			return;
		}
		if(item2.getId() != usedWithItemId) {
			return;
		}
		/*if(slot1 == -1 || slot2 == -1) {
			return;
		}
		if(slot1 != usedWithSlotId && slot1 != usedSlotId) {
			return;
		}
		if(slot2 != usedWithSlotId && slot2 != usedSlotId) {
			return;
		}
		if(slot1 == slot2) {
			return;
		}*/
		
		Pie pie = Pies.forId(usedItemId, usedWithItemId);
		
		if (pie != null) {
			PieMaking p = new PieMaking(player, pie);
			p.execute();
			return;
		}
		
		
		int tinderboxId = Firemaking.TINDERBOX.getId();
		if(usedWithItemId == tinderboxId || usedItemId == tinderboxId) {
			int checkId = usedWithItemId;
			if(checkId == tinderboxId) {
				checkId = usedItemId;
			}
			Log log = Log.forId(checkId);
			if(log != null) {
				player.getActionQueue().addAction(new Firemaking(player, log, usedItemId, usedWithItemId, usedSlotId, usedWithSlotId));
			}
		}
		
		int knifeId = Fletching.KNIFE.getId();
		if(usedWithItemId == knifeId || usedItemId == knifeId) {
			int checkId = usedWithItemId;
			if(checkId == knifeId) {
				checkId = usedItemId;
			}
			KnifeAction knifeAction = KnifeAction.forLogId(checkId);
			if(knifeAction != null) {
				new Fletching(player).handleKnifeAction(knifeAction);
			}
		}
	}

}
