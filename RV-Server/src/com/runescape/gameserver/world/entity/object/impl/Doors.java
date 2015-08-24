package com.runescape.gameserver.world.entity.object.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;

public class Doors extends GameObject {

	public Doors(GameObjectDefinition definition, Location location, int type,
			int rotation, boolean loadedInLandscape) {
		super(definition, location, type, rotation, loadedInLandscape);
	}
	
	@Override
	public boolean handleFirstClick(Player p) {
		return true;
	}
	
	@Override
	public boolean handleSecondClick(Player p) {
		return true;
	}

}
