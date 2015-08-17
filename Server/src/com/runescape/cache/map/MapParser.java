package com.runescape.cache.map;

import com.runescape.cache.Cache;

/**
 * A class which parses map files in the game cache.
 * @author Graham Edgecombe
 *
 */
public class MapParser {
	
	/**
	 * The cache.
	 */
	@SuppressWarnings("unused")
	private Cache cache;
	
	/**
	 * The area id.
	 */
	@SuppressWarnings("unused")
	private int area;
	
	/**
	 * The map listener.
	 */
	@SuppressWarnings("unused")
	private MapListener listener;
	
	/**
	 * Creates the map parser.
	 * @param cache The cache.
	 * @param area The area id.
	 * @param listener The listener.
	 */
	public MapParser(Cache cache, int area, MapListener listener) {
		this.cache = cache;
		this.area = area;
		this.listener = listener;
	}
	
	/**
	 * Parses the map file.
	 */
	public void parse() {
		
	}

}
