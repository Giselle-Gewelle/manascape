package com.runescape.gameserver;

/**
 * Holds global server constants.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Constants {

	public static final int NAME_LEN = 20;
	
	public static final String COLOUR_BLUE = "000099";
	
	/**
	 * If yellchat is enabled or not.
	 */
	public static boolean yellChatEnabled = true;

	/**
	 * The directory for the engine scripts.
	 */
	public static final String SCRIPTS_DIRECTORY = "./data/scripts/";

	/**
	 * Difference in X coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * Difference in Y coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

	/**
	 * Default sidebar interfaces array.
	 */
	public static final int SIDEBAR_INTERFACES[][] = new int[][] { new int[] { 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 0 },
			new int[] { 3917, 19001, 3213, 1644, 5608, 1151, 5065, 5715, 2449, 904, 147, 6299, 2423 }, };

	public static final int EXPERIENCE_RATE = 1;

	public static int[] packetSizes = new int[256];

	static {
		for(int i = 0; i < packetSizes.length; i++) {
			packetSizes[i] = 0;
		}
		packetSizes[1] = 12;
		packetSizes[3] = 6;
		packetSizes[4] = 6;
		packetSizes[6] = 0;
		packetSizes[8] = 2;
		packetSizes[13] = 2;
		packetSizes[19] = 4;
		packetSizes[22] = 2;
		packetSizes[24] = 6;
		packetSizes[28] = -1;
		packetSizes[31] = 4;
		packetSizes[36] = 8;
		packetSizes[40] = 0;
		packetSizes[42] = 2;
		packetSizes[45] = 2;
		packetSizes[49] = -1;
		packetSizes[50] = 6;
		packetSizes[54] = 6;
		packetSizes[55] = 6;
		packetSizes[56] = -1;
		packetSizes[57] = 8;
		packetSizes[67] = 2;
		packetSizes[71] = 6;
		packetSizes[75] = 4;
		packetSizes[77] = 6;
		packetSizes[78] = 4;
		packetSizes[79] = 2;
		packetSizes[80] = 2;
		packetSizes[83] = 8;
		packetSizes[91] = 6;
		packetSizes[95] = 4;
		packetSizes[100] = 6;
		packetSizes[104] = 4;
		packetSizes[110] = 0;
		packetSizes[112] = 2;
		packetSizes[116] = 2;
		packetSizes[119] = 1;
		packetSizes[120] = 8;
		packetSizes[123] = 7;
		packetSizes[126] = 1;
		packetSizes[136] = 6;
		packetSizes[140] = 4;
		packetSizes[141] = 8;
		packetSizes[143] = 8;
		packetSizes[152] = 12;
		packetSizes[157] = 4;
		packetSizes[158] = 6;
		packetSizes[160] = 8;
		packetSizes[161] = 6;
		packetSizes[163] = 13;
		packetSizes[165] = 1;
		packetSizes[168] = 0;
		packetSizes[171] = -1;
		packetSizes[173] = 3;
		packetSizes[176] = 3;
		packetSizes[177] = 6;
		packetSizes[181] = 6;
		packetSizes[184] = 10;
		packetSizes[187] = 1;
		packetSizes[194] = 2;
		packetSizes[197] = 4;
		packetSizes[202] = 0;
		packetSizes[203] = 6;
		packetSizes[206] = 8;
		packetSizes[210] = 8;
		packetSizes[211] = 12;
		packetSizes[213] = -1;
		packetSizes[217] = 8;
		packetSizes[222] = 3;
		packetSizes[226] = 2;
		packetSizes[227] = 9;
		packetSizes[228] = 6;
		packetSizes[230] = 6;
		packetSizes[231] = 6;
		packetSizes[233] = 2;
		packetSizes[241] = 6;
		packetSizes[244] = -1;
		packetSizes[245] = 2;
		packetSizes[247] = -1;
		packetSizes[248] = 0;
	}
	/**
	 * The player cap.
	 */
	public static final int MAX_PLAYERS = 2000;

	/**
	 * The NPC cap.
	 */
	public static final int MAX_NPCS = 32000;

	/**
	 * An array of valid characters in a long username.
	 */
	public static final char VALID_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"', '[', ']', '|', '?', '/', '`' };

	/**
	 * Packed text translate table.
	 */
	public static final char XLATE_TABLE[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']' };

	/**
	 * The maximum amount of items in a stack.
	 */
	public static final int MAX_ITEMS = Integer.MAX_VALUE;

	public static final int EXP_MULTIPLIER = 5;

}
