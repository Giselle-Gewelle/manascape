package com.runescape.gameserver.world.content.god.battles.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.god.God;
import com.runescape.gameserver.world.content.god.battles.Battlefield;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.object.GameObject;

public class LumbridgeBattlefield extends Battlefield {

	@Override
	public God[] getGods() {
		return new God[] { 
			World.getInstance().getGod(GodFaction.SARADOMIN),
			World.getInstance().getGod(GodFaction.ZAMORAK)
		};
	}

	@Override
	public Location[] getLocations() {
		return new Location[] {
			new Location(3165, 3248),
			new Location(3162, 3223)
		};
	}

	@Override
	public int getMaxFighters() {
		return 20;
	}

	@Override
	public Location getCenter() {
		return new Location(3164, 3235);
	}

	@Override
	public Location getMin() {
		return new Location(3158, 3222);
	}

	@Override
	public Location getMax() {
		return new Location(3170, 3251);
	}

	@Override
	public GameObject[] getObjects() {
		return new GameObject[] {
			/* Sara */
			new GameObject(GameObjectDefinition.forId(585), new Location(3164, 3243), 10, 0, false), //statue
			new GameObject(GameObjectDefinition.forId(4902), new Location(3170, 3345), 10, 0, false), //standard
			new GameObject(GameObjectDefinition.forId(13175), new Location(3161, 3243), 10, 0, false), //icon
			new GameObject(GameObjectDefinition.forId(13179), new Location(3166, 3249), 10, 0, false), //altar
			/* Zammy */
			new GameObject(GameObjectDefinition.forId(587), new Location(3164, 3223), 10, 2, false), //statue
			new GameObject(GameObjectDefinition.forId(13176), new Location(3161, 3222), 10, 2, false), //icon
			new GameObject(GameObjectDefinition.forId(4903), new Location(3160, 3223), 10, 2, false) //standard
		};
	}

}
