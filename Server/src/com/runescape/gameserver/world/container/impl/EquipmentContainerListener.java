package com.runescape.gameserver.world.container.impl;

import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.ContainerListener;
import com.runescape.gameserver.world.definitions.ItemBonuses;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A ContainerListener which flags for an appearance update when the player
 * equips or removes an item.
 * @author Graham Edgecombe
 *
 */
public class EquipmentContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates the container listener.
	 * @param player The player.
	 */
	public EquipmentContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		updateBonuses(container);
	}

	@Override
	public void itemsChanged(Container container) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		updateBonuses(container);
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		updateBonuses(container);
	}
	
	void updateBonuses(Container con) {
		ItemBonuses.updateBonuses(player, con);
	}

}
