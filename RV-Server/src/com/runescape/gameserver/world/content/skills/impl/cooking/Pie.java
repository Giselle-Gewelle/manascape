package com.runescape.gameserver.world.content.skills.impl.cooking;

public class Pie {
	private int ingredOne, ingredTwo, itemCreated;
	
	public Pie(int ingredOne, int ingredTwo, int itemCreated) {
		this.ingredOne = ingredOne;
		this.ingredTwo = ingredTwo;
		this.itemCreated = itemCreated;
	}
	
	public Pie(int ingredOne, int itemCreated) {
		this.ingredOne = ingredOne;
		this.ingredTwo = 2315;
		this.itemCreated = itemCreated;
	}

	public int getIngredOne() {
		return ingredOne;
	}

	public int getIngredTwo() {
		return ingredTwo;
	}
	
	public int getItemCreated() {
		return itemCreated;
	}
}
