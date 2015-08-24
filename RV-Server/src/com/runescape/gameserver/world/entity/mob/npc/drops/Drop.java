package com.runescape.gameserver.world.entity.mob.npc.drops;

import com.runescape.gameserver.world.Item;

public final class Drop {

	public static final Drop BONES = new Drop(526, 1, DropRarity.ALWAYS);
	
	private Item item;
	private DropRarity rarity;
	
	public Drop(int itemId, int amount, DropRarity rarity) {
		this.item = new Item(itemId, amount);
		this.rarity = rarity;
	}
	
	public Drop(Item item, DropRarity rarity) {
		this.item = item;
		this.rarity = rarity;
	}
	
	public Item getItem() {
		return item;
	}
	
	public DropRarity getRarity() {
		return rarity;
	}
	
}
