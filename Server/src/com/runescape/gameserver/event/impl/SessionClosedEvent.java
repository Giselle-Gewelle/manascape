package com.runescape.gameserver.event.impl;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class SessionClosedEvent extends Event {

	private Player player;
	
	public SessionClosedEvent(Player player) {
		super(0);
		this.player = player;
	}

	private boolean canDestroy() {
		if(player.getMobCooldowns().get(CooldownFlags.COMBAT)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void execute() {
		if(canDestroy()) {
			World.getInstance().unregister(player);
			this.stop();
		} else {
			this.setDelay(600);
		}
	}

}
