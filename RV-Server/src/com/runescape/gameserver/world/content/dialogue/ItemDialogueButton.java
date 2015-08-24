package com.runescape.gameserver.world.content.dialogue;

public enum ItemDialogueButton {

	FIRST_CLICK(
		new ItemButton(8874, 0), new ItemButton(8878, 1),
		new ItemButton(8889, 0), new ItemButton(8893, 1), new ItemButton(8897, 2),
		new ItemButton(8909, 0), new ItemButton(8913, 1), new ItemButton(8917, 2), new ItemButton(8921, 3),
		new ItemButton(8949, 0), new ItemButton(8953, 1), new ItemButton(8957, 2), new ItemButton(8961, 3), new ItemButton(8965, 4)
	),
	SECOND_CLICK(
		new ItemButton(8873, 0), new ItemButton(8877, 1),
		new ItemButton(8888, 0), new ItemButton(8892, 1), new ItemButton(8896, 2),
		new ItemButton(8908, 0), new ItemButton(8912, 1), new ItemButton(8916, 2), new ItemButton(8920, 3),
		new ItemButton(8948, 0), new ItemButton(8952, 1), new ItemButton(8956, 2), new ItemButton(8960, 3), new ItemButton(8964, 4)
	),
	THRID_CLICK(
		new ItemButton(8872, 0), new ItemButton(8876, 1),
		new ItemButton(8887, 0), new ItemButton(8891, 1), new ItemButton(8895, 2),
		new ItemButton(8907, 0), new ItemButton(8911, 1), new ItemButton(8915, 2), new ItemButton(8919, 3),
		new ItemButton(8947, 0), new ItemButton(8951, 1), new ItemButton(8955, 2), new ItemButton(8959, 3), new ItemButton(8964, 4)
	),
	FOURTH_CLICK(
		new ItemButton(8871, 0), new ItemButton(8875, 1),
		new ItemButton(8886, 0), new ItemButton(8890, 1), new ItemButton(8894, 2),
		new ItemButton(8906, 0), new ItemButton(8910, 1), new ItemButton(8914, 2), new ItemButton(8918, 3),
		new ItemButton(8946, 0), new ItemButton(8950, 1), new ItemButton(8954, 2), new ItemButton(8958, 3), new ItemButton(8963, 4)
	);
	
	private ItemButton[] buttons = null;
	
	ItemDialogueButton(ItemButton...buttons) {
		this.buttons = buttons;
	}
	
	public int getIndex() {
		switch(this) {
		case FIRST_CLICK:
			return 0;
		case SECOND_CLICK:
			return 1;
		case THRID_CLICK:
			return 2;
		case FOURTH_CLICK:
			return 3;
		}
		return -1;
	}
	
	public ItemButton[] getButtons() {
		return buttons;
	}
	
	public static ItemDialogueButton forButtonId(int button) {
		for (ItemDialogueButton e : values())
			for (ItemButton  b : e.getButtons())
				if (b.getButtonId() == button)
					return e;
		return null;
	}
	
	public static ItemButton ItemButtonForId(int button) {
		for (ItemDialogueButton e : values())
			for (ItemButton  b : e.getButtons())
				if (b.getButtonId() == button)
					return b;
		return null;
	}
	
}
