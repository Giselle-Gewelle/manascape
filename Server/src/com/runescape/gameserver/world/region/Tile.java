package com.runescape.gameserver.world.region;

import java.util.ArrayList;
import java.util.List;

import com.runescape.gameserver.world.GroundItem;

public class Tile {

	/**
	 * A list of ground items on this tile.
	 */
	private List<GroundItem> groundItems = new ArrayList<GroundItem>();

	/**
	 * Gets the ground items on this tile.
	 * 
	 * @return the groundItems
	 */
	public List<GroundItem> getGroundItems() {
		return groundItems;
	}
	
}
