package com.runescape.gameserver.world.content.skills.guides;

public abstract class Guide {

	protected int[] levels = null;
	protected int[] itemIds = null;
	protected String[] names = null;
	
	public abstract void setContent(int tabId);
	
	public int[] getLevels() {
		return levels;
	}
	
	public int[] getItemIds() {
		return itemIds;
	}
	
	public String[] getNames() {
		return names;
	}
	
}
