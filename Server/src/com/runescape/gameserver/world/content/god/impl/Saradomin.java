package com.runescape.gameserver.world.content.god.impl;

import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.god.God;
import com.runescape.gameserver.world.entity.mob.npc.impl.TempleKnight;

public final class Saradomin extends God {

	@Override
	public GodFaction getFaction() {
		return GodFaction.SARADOMIN;
	}

	@Override
	public Class<?>[] getFighters() {
		return new Class<?>[] {
			TempleKnight.class
		};
	}

}
