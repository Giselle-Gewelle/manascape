if(npcId == 3852) {
	npcDef.name = "Commander Adriea";
	npcDef.description = new String("A commander under the service of Saradomin himself.").getBytes();
	npcDef.combatLevel = 126;
	
	npcDef.idleAnim = 808;
	npcDef.walkAnim = 819;
	npcDef.turn180Anim = 820;
	npcDef.turn90CWAnim = 821;
	npcDef.turn90CCWAnim = 822;
	npcDef.degreesToTurn = 32;
	
	npcDef.actions = new String[5];
	npcDef.actions[0] = "Talk-to";
	
	npcDef.models = new int[11];
	// head
	npcDef.models[0] = 414;
	npcDef.models[10] = 390;
	
	// body
	npcDef.models[1] = 3383;
	npcDef.models[2] = 344;
	
	// gloves
	npcDef.models[3] = 356;
	
	// weapon
	npcDef.models[4] = 546;
	
	// legs
	npcDef.models[5] = 432;
	
	// boots
	npcDef.models[6] = 5031;
	
	// shield
	npcDef.models[7] = 517;
	
	// cape
	npcDef.models[8] = 481;
	
	// chest symbol
	npcDef.models[9] = 3330;
	
	
	npcDef.chatModels = new int[2];
	npcDef.chatModels[0] = 113;
	npcDef.chatModels[1] = 131;
	

	npcDef.recolorOriginal = new int[14];
	npcDef.recolorNew = new int[14];
	
	npcDef.recolorOriginal[0] = 24;
	npcDef.recolorNew[0] = 20;
	npcDef.recolorOriginal[1] = 61;
	npcDef.recolorNew[1] = 99;
	npcDef.recolorOriginal[2] = 41;
	npcDef.recolorNew[2] = 82;
	npcDef.recolorOriginal[3] = 11187;
	npcDef.recolorNew[3] = 20;
	npcDef.recolorOriginal[4] = 57;
	npcDef.recolorNew[4] = 20;
	npcDef.recolorOriginal[5] = 5400;
	npcDef.recolorNew[5] = 61;
	npcDef.recolorOriginal[6] = 5648;
	npcDef.recolorNew[6] = 99;
	npcDef.recolorOriginal[7] = 10004;
	npcDef.recolorNew[7] = 82;
	
	// shield
	npcDef.recolorOriginal[8] = 7054;
	npcDef.recolorNew[8] = 82;
	
	// cape
	npcDef.recolorOriginal[9] = 926;
	npcDef.recolorNew[9] = 20;
	npcDef.recolorOriginal[10] = 7700;
	npcDef.recolorNew[10] = 43943;
	npcDef.recolorOriginal[11] = 11200;
	npcDef.recolorNew[11] = 99;
	npcDef.recolorOriginal[12] = 6032;
	npcDef.recolorNew[12] = 20;
	
	// hair
	npcDef.recolorOriginal[13] = 6798;
	npcDef.recolorNew[13] = 6852;
}