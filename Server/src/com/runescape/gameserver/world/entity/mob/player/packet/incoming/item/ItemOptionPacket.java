package com.runescape.gameserver.world.entity.mob.player.packet.incoming.item;

import java.util.logging.Logger;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.GroundItem;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Bank;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.content.skills.impl.BuryingBones;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;
import com.runescape.gameserver.world.region.Tile;

public class ItemOptionPacket extends IncomingPacket {

	private static final Logger logger = Logger.getLogger(ItemOptionPacket.class.getName());
	
	private static final int OPTION_1 = 3;
	private static final int OPTION_2 = 177;
	private static final int OPTION_3 = 91;
	private static final int OPTION_4 = 231;
	private static final int OPTION_5 = 158;
	private static final int DROP_ITEM = 4;
	private static final int TAKE_GROUND_ITEM = 71;
	private static final int CLICK_ITEM = 203;
	
	public ItemOptionPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) throws Throwable {
		switch(packet.getOpcode()) {
			case OPTION_1:
				handleItemOption1(player, packet);
				player.sendOMessage("option 1");
				break;
			case OPTION_2:
				handleItemOption2(player, packet);
				player.sendOMessage("option 2");
				break;
			case OPTION_3:
				handleItemOption3(player, packet);
				player.sendOMessage("option 3");
				break;
			case OPTION_4:
				handleItemOption4(player, packet);
				player.sendOMessage("option 4");
				break;
			case OPTION_5:
				handleItemOption5(player, packet);
				player.sendOMessage("option 5");
				break;
			case TAKE_GROUND_ITEM:
				handleTakeGroundItem(player, packet);
				break;
			case DROP_ITEM:
				handleDropItem(player, packet);
				break;
			case CLICK_ITEM:
				handleClickItem(player, packet);
				break;
		}
	}
	
	private void handleClickItem(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceId = packet.getShortA();
		int slot = packet.getLEShort();
		int itemId = packet.getLEShort();
		
		if (player.getEating().eat(itemId, slot))
				return;
		
		if (BuryingBones.bury(player, itemId, slot))
			return;
	}

	private void handleItemOption1(Player player, Packet packet) {
		int id = packet.getShortA();
		int interfaceId = packet.getShort();
		int slot = packet.getShort();
		switch(interfaceId) {
		case Equipment.INTERFACE:
			if(slot >= 0 && slot < Equipment.SIZE) { 
				if(!Container.transfer(player.getEquipment(), player.getInventory(), slot, id)) {
					// indicate it failed
				}
			}
			break;
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 1);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 1);
			}
			break;
		}
	}
	
	private void handleItemOption2(Player player, Packet packet) {
		int slot = packet.getShortA() & 0xFFFF;
		int id = packet.getLEShort() & 0xFFFF;
		int interfaceId = packet.getLEShort() & 0xFFFF;
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 5);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 5);
			}
			break;
		}
	}
	
	private void handleItemOption3(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		int slot = packet.getLEShortA() & 0xFFFF;
		int interfaceId = packet.getShort() & 0xFFFF;
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 10);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 10);
			}
			break;
		}
	}
	
	private void handleItemOption4(Player player, Packet packet) {
		int interfaceId = packet.getLEShortA() & 0xFFFF;
		int slot = packet.getLEShort() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, player.getInventory().getCount(id));
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, player.getBank().getCount(id));
			}
			break;
		}
	}
	
	private void handleItemOption5(Player player, Packet packet) {
		int slot = packet.getLEShortA() & 0xFFFF;
		int id = packet.getLEShortA() & 0xFFFF;
		int interfaceId = packet.getLEShort() & 0xFFFF;
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < Bank.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case 1743:
			player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			break;
		}
	}

	private void handleTakeGroundItem(final Player player, Packet packet) throws Throwable {
		final int itemId = packet.getLEShortA();
		int xCoord = packet.getLEShortA();
		int yCoord = packet.getShortA() & 0xFFFF;
		//System.out.println("Item: " + itemId + " X: " + xCoord + " Y: " + yCoord + " Z: " + player.getLocation().getZ());
		
		if(player.isDead()) {
			return;
		}
		
		final Location location = Location.create(xCoord, yCoord, player.getLocation().getHeight());
		
		Action action = new Action(player, 0) {
			@Override
			public void execute() {
				Tile tile = player.getRegion().getTile(location);
				for(GroundItem g : tile.getGroundItems()) {
					if(g.getItem().getId() == itemId && g.isOwnedBy(player.getName())) {
						if(player.getInventory().add(g.getItem())) {
							World.getInstance().unregister(g);
						} else {
							player.getPacketSender().sendMessage("There is not enough space in your inventory.");
						}
						break;
					}
				}
				this.stop();
			}

			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}

			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.WALKABLE;
			}
		};
		
		player.getActionQueue().addAction(new CoordinateAction(player, location, 0, action));
	}
	
	private void handleDropItem(Player player, Packet packet) throws Throwable {
		int slotId = packet.getLEShort();
		int itemId = packet.getLEShortA();
		int interfaceId = packet.getLEShortA();
		//System.out.println("Item: " + itemId + " Slot Id: " + slotId + " Interface Id: " + interfaceId);
		
		if(player.isDead()) {
			return;
		}
		
		switch(interfaceId) {
			case Inventory.INTERFACE:
				/*
				 * Make sure the slot is valid.
				 */
				if(slotId < 0 || slotId > Inventory.SIZE) {
					return;
				}
				/*
				 * Make sure the item is valid and is in its correct slot.
				 */
				Item item = player.getInventory().get(slotId);
				if(item == null || item.getId() != itemId) {
					return;
				}
				player.getInventory().remove(slotId, item);
				World.getInstance().createGroundItem(new GroundItem(player.getName(), item, player.getLocation()), player);
				break;
			default:
				logger.info("Unhandled item drop interface: " + interfaceId);
				break;
		}
	}
	
}
