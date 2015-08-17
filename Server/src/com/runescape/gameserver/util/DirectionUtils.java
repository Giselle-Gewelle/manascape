package com.runescape.gameserver.util;

/**
 * A utility class for direction-related methods.
 * @author Graham Edgecombe
 *
 */
public class DirectionUtils {

	public static final int NORTH_WEST = 0;
	public static final int NORTH = 1;
	public static final int NORTH_EAST = 2;
	public static final int WEST = 3;
	public static final int EAST = 4;
	public static final int SOUTH_WEST = 5;
	public static final int SOUTH = 6;
	public static final int SOUTH_EAST = 7;
	
	/**
	 * Finds a direction.
	 * @param dx X difference.
	 * @param dy Y difference.
	 * @return The direction.
	 */
	public static int direction(int dx, int dy) {
		if(dx < 0) {
			if(dy < 0) {
				return 5;
			} else if(dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if(dx > 0) {
			if(dy < 0) {
				return 7;
			} else if(dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if(dy < 0) {
				return 6;
			} else if(dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

}
