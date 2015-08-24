package com.runescape.gameserver.world.content.dialogue;

public class ItemButton {

	private int itemIndex;
	private int button;
	
	public ItemButton(int button, int itemIndex) {
		this.button = button;
		this.itemIndex = itemIndex;
	}
	
	public int getItemIndex() {
		return itemIndex;
	}
	
	public int getButtonId() {
		return button;
	}

}
