package com.runescape.gameserver.event.impl;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;

/**
 * This event handles the expiry of a cooldown.
 * @author Brett Russell
 *
 */
public class CooldownEvent extends Event {
	
	private Mob entity;
	
	private CooldownFlags cooldown;

	/**
	 * Creates a cooldown event for a single CooldownFlag.
	 * @param entity The entity for whom we are expiring a cooldown.
	 * @param duration The length of the cooldown.
	 */
	public CooldownEvent(Mob entity, CooldownFlags cooldown, int duration) {
		super(duration);
		this.entity = entity;
		this.cooldown = cooldown;
	}

	@Override
	public void execute() {
		entity.getMobCooldowns().set(cooldown, false);
		this.stop();
	}

}
