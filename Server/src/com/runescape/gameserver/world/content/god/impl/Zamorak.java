package com.runescape.gameserver.world.content.god.impl;

import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.god.God;
import com.runescape.gameserver.world.entity.mob.npc.impl.BlackKnight;

public final class Zamorak extends God {

	@Override
	public GodFaction getFaction() {
		return GodFaction.ZAMORAK;
	}

	@Override
	public Class<?>[] getFighters() {
		return new Class<?>[] {
			BlackKnight.class
		};
	}

}
