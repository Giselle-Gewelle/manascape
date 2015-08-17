package com.runescape.gameserver.world.content.dialogue;

import java.util.ArrayList;
import java.util.List;

public enum DialogueButton {

	ZERO(367, 373, 972, 978, 985, 993, 2461, 2471, 2482, 2494, 4886, 4892, 4899, 4907), 
	ONE(2462, 2472, 2483, 2495),
	TWO(2473, 2484, 2496), 
	THREE(2485, 2497), 
	FOUR(2498);
	
	private List<Integer> buttonIds = new ArrayList<>();
	
	private DialogueButton(int...buttons) {
		for(int i : buttons) {
			buttonIds.add(i);
		}
	}
	
	public static DialogueButton forId(int id) {
		for(DialogueButton button : values()) {
			if(button.getIdList().contains(id)) {
				return button;
			}
		}
		
		return null;
	}
	
	private List<Integer> getIdList() {
		return buttonIds;
	}
}
