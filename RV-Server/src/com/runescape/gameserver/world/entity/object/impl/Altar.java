package com.runescape.gameserver.world.entity.object.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;

public class Altar extends GameObject {

	public Altar(GameObjectDefinition definition, Location location, int type,
			int rotation, boolean loadedInLandscape) {
		super(definition, location, type, rotation, loadedInLandscape);
	}
	
	@Override
	public boolean handleFirstClick(Player player) {
		return player.getPrayer().usePrayerAltar();
	}

}
