package com.runescape.gameserver.world.content.combat.magic.spells;

import java.util.LinkedList;
import java.util.List;

import com.runescape.gameserver.world.Item;

public class RuneRequirement {
	
	public RuneRequirement(int idOne, int amountOne, int idTwo, int amountTwo) {
		runesRequired.add(new Item(idOne, amountOne));
		runesRequired.add(new Item(idTwo, amountTwo));
	}
	
	public RuneRequirement(int idOne, int amountOne, int idTwo, int amountTwo, int idThree, int amountThree) {
		runesRequired.add(new Item(idOne, amountOne));
		runesRequired.add(new Item(idTwo, amountTwo));
		runesRequired.add(new Item(idThree, amountThree));
	}
	
	public RuneRequirement(int idOne, int amountOne, int idTwo, int amountTwo,
			int idThree, int amountThree, int idFour, int amountFour) {
		runesRequired.add(new Item(idOne, amountOne));
		runesRequired.add(new Item(idTwo, amountTwo));
		runesRequired.add(new Item(idThree, amountThree));
		runesRequired.add(new Item(idFour, amountFour));
	}
	
	private final List<Item> runesRequired = new LinkedList<>();
	
	public List<Item> getRunesRequired() {
		return runesRequired;
	}

}
