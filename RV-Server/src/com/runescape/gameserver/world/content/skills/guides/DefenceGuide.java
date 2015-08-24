package com.runescape.gameserver.world.content.skills.guides;

public final class DefenceGuide extends Guide {
	
	private String[] basicMetalTypes = new String[] {
		"Bronze", "Iron", "Steel", null, null, "Mithril", "Adamant", "Rune"
	};
	private int[] basicLevels = new int[] {
		1, 1, 5, 10, -1, 20, 30, 40
	};
	private int[][] basicItems = new int[][] {
		// Bronze
		{ 1173, 1189, 1139, 1155, 1103, 1117, 1087, 1075, 4119 },
		// Iron
		{ 1175, 1191, 1137, 1153, 1101, 1115, 1081, 1067, 4121 },
		// Steel
		{ 1177, 1193, 1141, 1157, 1105, 1119, 1083, 1069, 4123 },
		// Black
		null,
		// White/Temple Knight
		null,
		// Mithril
		{ 1181, 1197, 1143, 1159, 1109, 1121, 1085, 1071, 4127 },
		// Adamant
		{ 1183, 1199, 1145, 1161, 1111, 1123, 1091, 1073, 4129 },
		// Rune
		{ 1185, 1201, 1147, 1163, 1113, 1127, 1093, 1079, 4131 },
	};
	private String[] basicTypes = new String[] {
		"Square Shield", "Kiteshield", "Medium Helmet", "Full Helmet", "Chainbody", "Platebody", "Plateskirt", "Platelegs", "Boots"
	};

	@Override
	public void setContent(int tabId) {
		String metalType = null;
		if(tabId < 8 && tabId != 3 && tabId != 4) {
			metalType = basicMetalTypes[tabId];
			levels = new int[] { basicLevels[tabId] };
			itemIds = basicItems[tabId];
			names = basicTypes;
			for(int i = 0; i < names.length; i++) {
				names[i] = metalType + " " + names[i];
			}
		} else if(tabId == 3) {
			levels = new int[] {
				// Old
				10, 10, 10, 10, 10, 10, 10, 10, 10,
				// New
				20, 20, 20, 
				60, 60, 60
			};
			itemIds = new int[] {
				// Old
				1179, 1195, 1151, 1165, 1107, 1125, 1089, 1077, 4125, 
				// New
				7987, 7985, 7986,
				7984, 7982, 7983
			};
			names = new String[] {
				// Old
				"Square Shield", "Kiteshield", "Medium Helmet", "Full Helmet", "Chainbody", "Platebody", "Plateskirt", "Platelegs", "Boots", 
				// New
				"Black Knight Helm", "Black Knight Platebody", "Black Knight Platelegs",
				"Commander Helm", "Commander Platebody", "Commander Platelegs"
			};
		} else if(tabId == 4) {
			levels = new int[] {
				// White
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 
				// Temple Knight
				20, 20, 20, 
				30, 30, 30, 
				40, 40, 40, 
				50, 50, 50, 
				60, 60, 60
			};
			itemIds = new int[] { 
				// White
				6631, 6633, 6621, 6623, 6615, 6617, 6627, 6625, 6629, 6619, 
				// Temple Knight
				5574, 5575, 5576, 
				7956, 7957, 7958, 
				7959, 7960, 7961, 
				7962, 7963, 7964, 
				7965, 7966, 7967
			};
			names = new String[] {
				// White
				"Square Shield", "Kiteshield", "Medium Helmet", "Full Helmet", "Chainbody", "Platebody", "Plateskirt", "Platelegs", "Gloves", "Boots", 
				// Temple Knight
				"Initiate Helm (With 10 Prayer)", "Initiate Platemail (With 10 Prayer)", "Initiate Platelegs (With 10 Prayer)", 
				"Proselyte Helm (With 20 Prayer)", "Proselyte Platemail (With 20 Prayer)", "Proselyte Platelegs (With 20 Prayer)", 
				"Acolyte Helm (With 30 Prayer)", "Acolyte Platemail (With 30 Prayer)", "Acolyte Platelegs (With 30 Prayer)", 
				"Partisan Helm (With 40 Prayer)", "Partisan Platemail (With 40 Prayer)", "Partisan Platelegs (With 40 Prayer)", 
				"Commander Helm (With 50 Prayer)", "Commander Platemail (With 50 Prayer)", "Commander Platelegs (With 50 Prayer)"
			};
		} else if(tabId == 8) {
			levels = new int[] {
				60
			};
			itemIds = new int[] {
				1187, 1149, 3140, 4585, 4087
			};
			names = new String[] {
				"Dragon Square Shield", "Dragon Medium Helmet", "Dragon Chainbody", "Dragon Plateskirt", "Dragon Platelegs"
			};
		} else if(tabId == 9) {
			levels = new int[] {
				70
			};
			itemIds = new int[] {
				4708, 4712, 4714, 4716, 4720, 4722, 4724, 4728, 4730, 4732, 4736, 4738, 4745, 4749, 4751, 4753, 4757, 4759
			};
			names = new String[] {
				"Ahrim's Hood (With 70 Magic)", "Ahrim's Robe Top (With 70 Magic)", "Ahrim's Robeskirt (With 70 Magic)", 
				"Dharok's Helm", "Dharok's Platebody", "Dharok's Platelegs",
				"Guthan's Helm", "Guthan's Platebody", "Guthan's Chainskirt", 
				"Karil's Coif (With 70 Ranged)", "Karil's Leather Top (With 70 Ranged)", "Karil's Leather Skirt (With 70 Ranged)", 
				"Torag's Helm", "Torag's Platebody", "Torag's Platelegs", 
				"Verac's Helm", "Verac's Brassard", "Verac's Plateskirt"
			};
		} else if(tabId == 10) {
			//mage
		} else if(tabId == 11) {
			//range
		} else if(tabId == 12) {
			//tk
		} else if(tabId == 13) {
			//milestone
		}
	}
	
}
