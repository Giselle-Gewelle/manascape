package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.container.Equipment.EquipmentType;
import com.runescape.gameserver.world.definitions.EquipmentDefinition;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;
import com.runescape.util.LevelRequirement;

public class WieldPacket extends IncomingPacket {

	public WieldPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) throws Throwable {
		int interfaceId = packet.getLEShort() & 0xFFFF;
		int id = packet.getLEShort() & 0xFFFF;
		int slot = packet.getShortA() & 0xFFFF;
		
		EquipmentDefinition def = EquipmentDefinition.forItemId(id);
		
		if (def != null) {
			for (int i = 0; i < def.getLevelReq().size(); i++) {
				LevelRequirement r = def.getLevelReq().get(i);
				if (!r.playerHasRequirement(player)) {
					player.getPacketSender().sendMessage(r.getUnmetEquipmentRequirementMessage());
					return;
				}
			}
		}
		
		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Item item = player.getInventory().get(slot);
				if(item != null && item.getId() == id) {
					EquipmentType type = Equipment.getType(item);
					Item oldEquip = null;
					boolean stackable = false;
					if(player.getEquipment().isSlotUsed(type.getSlot()) && !stackable) {
						oldEquip = player.getEquipment().get(type.getSlot());
						player.getEquipment().set(type.getSlot(), null);
					}
					player.getInventory().set(slot, null);
					if(oldEquip != null) {
						player.getInventory().add(oldEquip);
					}
					if(!stackable) {
						player.getEquipment().set(type.getSlot(), item);
					} else {
						player.getEquipment().add(item);
					}
				}
			}
			break;
		}
	}

}
