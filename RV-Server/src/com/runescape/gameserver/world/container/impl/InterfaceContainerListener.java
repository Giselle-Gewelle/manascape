package com.runescape.gameserver.world.container.impl;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.ContainerListener;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A ContainerListener which updates a client-side interface to match the
 * server-side copy of the container.
 * @author Graham Edgecombe
 *
 */
public class InterfaceContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * The interface id.
	 */
	private int interfaceId;
	
	/**
	 * Creates the container listener.
	 * @param player The player.
	 * @param interfaceId The interface id.
	 */
	public InterfaceContainerListener(Player player, int interfaceId) {
		this.player = player;
		this.interfaceId = interfaceId;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		Item item = container.get(slot);
		player.getPacketSender().sendUpdateItem(interfaceId, slot, item);
	}

	@Override
	public void itemsChanged(Container container) {
		player.getPacketSender().sendUpdateItems(interfaceId, container.toArray());
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getPacketSender().sendUpdateItems(interfaceId, slots, container.toArray());
	}

}
