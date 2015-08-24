package com.runescape.gameserver.world.entity.object.impl;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.content.skills.impl.cooking.Cooking.CookingItem;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;

public class Fire extends GameObject {

	public Fire(GameObjectDefinition definition, Location location, int type,
			int rotation, boolean loadedInLandscape) {
		super(definition, location, type, rotation, loadedInLandscape);
	}
	
	@Override
	public boolean handleItemOnUs(Player p, Item item) {
		CookingItem cook = CookingItem.forItem(item);
		p.getCooking().sendCookingInterface(cook, this);
		return true;
	}

}
