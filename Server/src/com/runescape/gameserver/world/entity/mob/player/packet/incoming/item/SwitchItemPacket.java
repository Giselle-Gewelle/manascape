package com.runescape.gameserver.world.entity.mob.player.packet.incoming.item;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.container.Bank;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class SwitchItemPacket extends IncomingPacket {

	public SwitchItemPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) {
		int toSlot = packet.getLEShortA();
		packet.getByteA();
		int interfaceId = packet.getShortA();
		int fromSlot = packet.getLEShort();
				
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
		case Inventory.INTERFACE:
			if(fromSlot >= 0 && fromSlot < Inventory.SIZE && toSlot >= 0 && toSlot < Inventory.SIZE && toSlot != fromSlot) {
				player.getInventory().swap(fromSlot, toSlot);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(fromSlot >= 0 && fromSlot < Bank.SIZE && toSlot >= 0 && toSlot < Bank.SIZE && toSlot != fromSlot) {
				if(player.getSettings().getBool("swapping")) {
					player.getBank().swap(fromSlot, toSlot);
				} else {
					player.getBank().insert(fromSlot, toSlot);
				}
			}
			break;
		}
	}

}
