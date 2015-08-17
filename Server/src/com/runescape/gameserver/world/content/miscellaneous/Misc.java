package com.runescape.gameserver.world.content.miscellaneous;

import com.runescape.gameserver.world.entity.mob.Mob;

public class Misc {

	public static String optimizeText(String text) {
		final char buf[] = text.toCharArray();
		boolean endMarker = true;
		for(int i = 0; i < buf.length; i++) {
			final char c = buf[i];
			if(endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
			if(c == '.' || c == '!' || c == '?') {
				endMarker = true;
			}
		}
		return new String(buf, 0, buf.length);
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for(int i = 0; i < chars.length; i++) {
			if(!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if(Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	/**
	 * A method that uppercases the first letter for a string.
	 * 
	 * @param str
	 *            The String
	 * @return The String with the first letter uppercased.
	 */
	public static String ucFirst(String str) {
		str = str.toLowerCase();
		if(str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}

	public static int distanceBetween(Mob a1, Mob a2) {
		final int x = (int) Math.pow(a1.getLocation().getX() - a2.getLocation().getX(), 2D);
		final int y = (int) Math.pow(a1.getLocation().getY() - a2.getLocation().getY(), 2D);
		return (int) Math.floor(Math.sqrt(x + y));
	}

	public static int random(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

}
