package com.runescape.gameserver.world.content.skills.guides;

public final class AttackGuide extends Guide {
	
	private String[] basicMetalTypes = new String[] {
		"Bronze", "Iron", "Steel", "Black", "White", "Mithril", "Adamant", "Rune"
	};
	private int[] basicLevels = new int[] {
		1, 1, 5, 10, 10, 20, 30, 40
	};
	private int[][] basicItems = new int[][] {
		// Bronze
		{ 1205, 1351, 1422, 3095, 1277, 1291, 1321, 1237, 1337, 1375, 1307, 3190 },
		// Iron
		{ 1203, 1349, 1420, 3096, 1279, 1293, 1323, 1239, 1335, 1363, 1309, 3192 },
		// Steel
		{ 1207, 1353, 1424, 3097, 1281, 1295, 1325, 1241, 1339, 1365, 1311, 3194 },
		// Black
		{ 1217, 1361, 1426, 3098, 1283, 1297, 1327, 4580, 1341, 1367, 1313, 3196 },
		// White
		{ 6591, 6601, 6587, 6605, 6607, 6611, 6613, 6589, 6609, 6599 },
		// Mithril
		{ 1209, 1355, 1428, 3099, 1285, 1299, 1329, 1243, 1343, 1369, 1315, 3198 },
		// Adamant
		{ 1211, 1357, 1430, 3100, 1287, 1301, 1331, 1245, 1345, 1371, 1317, 3200 },
		// Rune
		{ 1213, 1359, 1432, 3101, 1289, 1303, 1333, 1247, 1347, 1373, 1319, 3202 }
	};
	private String[] basicTypes = new String[] {
		"Dagger", "Axe", "Mace", "Claws", "Sword", "Longsword", "Scimitar", "Spear", "Warhammer", "Battleaxe", "Two-Handed Sword", "Halberd"
	};

	@Override
	public void setContent(int tabId) {
		String metalType = null;
		if(tabId < 8 && tabId != 4) {
			metalType = basicMetalTypes[tabId];
			levels = new int[] { basicLevels[tabId] };
			itemIds = basicItems[tabId];
			names = basicTypes;
			for(int i = 0; i < names.length; i++) {
				names[i] = metalType + " " + names[i];
			}
		} else if(tabId == 4) {
			// White doesn't have an axe or spear... We'll just have to fix that now won't we!
			// TODO Add white axe and spear into the cache.
			metalType = basicMetalTypes[tabId];
			levels = new int[] { 
				basicLevels[tabId] 
			};
			itemIds = basicItems[tabId];
			names = new String[] {
				"Dagger", "Mace", "Claws", "Sword", "Longsword", "Scimitar", "Warhammer", "Battleaxe", "Two-Handed Sword", "Halberd"
			};
			for(int i = 0; i < names.length; i++) {
				names[i] = metalType + " " + names[i];
			}
		} else if(tabId == 8) {
			metalType = "Dragon";
			levels = new int[] { 
				60 
			};
			itemIds = new int[] { 
				1215, 6739, 1434, 1305, 4587, 1249, 1377, 7158, 3204
			};
			names = new String[] {
				"Dagger", "Axe", "Mace", "Longsword", "Scimitar", "Spear", "Battleaxe", "Two-Handed Sword", "Halberd"
			};
			for(int i = 0; i < names.length; i++) {
				names[i] = metalType + " " + names[i];
			}
		} else if(tabId == 9) {
			levels = new int[] { 
				70 
			};
			itemIds = new int[] {
				4710, 4718, 4726, 4747, 4755
			};
			names = new String[] {
				"Ahrim's Staff (With 70 Magic)", 
				"Dharok's Greataxe (With 70 Strength)",
				"Guthan's Spear",
				"Torag's Hammers (With 70 Strength)",
				"Verac's Flail"
			};
		} else if(tabId == 10) {
			levels = new int[] { 
				50, 50, 60, 60, 60, 60, 60, 70
			};
			itemIds = new int[] {
				4158, 4675, 6523, 6528, 6525, 6526, 6527, 4151
			};
			names = new String[] {
				"Leaf-Bladed Spear (With 55 Slayer)", 
				"Ancient Staff (With 50 Magic)",
				"TokTz-Xil-Ak (Obsidian Sword)",
				"TzHaar-Ket-Om (Obsidian Maul) (With 60 Strength)",
				"TokTz-Xil-Ek (Obsidian Knife)",
				"TokTz-Mej-Tal (Obsidian Staff)",
				"TokTz-Ket-Em (Obsidian Mace)",
				"Abyssal Whip"
			};
		} else if(tabId == 11) {
			levels = new int[] { 
				99 
			};
			itemIds = new int[] { 
				667 
			};
			names = new String[] {
				"Skill Mastery"
			};
		}
	}
	
}
