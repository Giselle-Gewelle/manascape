package com.runescape.gameserver.world.entity.mob.npc.drops;

public enum DropRarity {

	ALWAYS(0),
	COMMON(10),
	UNCOMMON(30),
	RARE(100),
	VERY_RARE(250);
	
	private int chance;
	
	private DropRarity(int chance) {
		this.chance = chance;
	}
	
	public int getChance() {
		return chance;
	}
	
}
