package com.runescape.cache.map;

import com.runescape.gameserver.world.region.Tile;

/**
 * A map listener is notified when a tile is parsed by a
 * <code>MapParser</code>.
 * @author Graham Edgecombe
 *
 */
public interface MapListener {
	
	/**
	 * Handles action when a tile is parsed.
	 * @param tile The tile.
	 */
	public void tileParsed(Tile tile);

}
