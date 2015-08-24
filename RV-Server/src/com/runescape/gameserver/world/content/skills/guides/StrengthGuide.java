package com.runescape.gameserver.world.content.skills.guides;

public final class StrengthGuide extends Guide {

	@Override
	public void setContent(int tabId) {
		if(tabId == 0) {
			levels = new int[] {
				5, 5, 10, 15, 20, 30, 50, 60, 70, 70
			};
			names = new String[] {
				"Black Halberd (With 10 Attack)",
				"White Halberd (With 10 Attack)",
				"Mithril Halberd (With 20 Attack)",
				"Adamant Halberd (With 30 Attack)",
				"Rune Halberd (With 40 Attack)",
				"Dragon Halberd (With 60 Attack)",
				"Granite Maul (With 50 Attack)",
				"TzHaar-Ket-Om (Obsidian Maul) (With 60 Attack)",
				"Dharok's Greataxe (With 70 Attack)",
				"Torag's Hammers (With 70 Attack)"
			};
			itemIds = new int[] {
				3196, 6599, 3198, 3200, 3202, 3204, 4153, 6528, 4718, 4747
			};
		} else if(tabId == 1) {
			levels = new int[] {
				50
			};
			names = new String[] {
				"Granite Shield (With 50 Defence)"
			};
			itemIds = new int[] {
				3122
			};
		} else if(tabId == 2) {
			levels = new int[] {
				90, 99
			};
			names = new String[] {
				"Using two-handed swords with one hand",
				"Skill Mastery"
			};
			itemIds = new int[] {
				7158, 7671
			};
		}
	}
	
}
