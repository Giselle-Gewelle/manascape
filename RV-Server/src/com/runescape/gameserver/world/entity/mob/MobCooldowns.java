package com.runescape.gameserver.world.entity.mob;

import java.util.BitSet;

import com.runescape.gameserver.event.impl.CooldownEvent;
import com.runescape.gameserver.world.World;

public class MobCooldowns {
	
	public enum CooldownFlags {
		MELEE_SWING,
		RANGED_SHOT,
		SPELL_CAST,
		SPECIAL_ATTACK,
		COMBAT,
		WALKING,
		FIREMAKING,
		NPC_SPAWNS
	}
	
	private BitSet cooldowns = new BitSet();
	private Mob mob;
	
	public MobCooldowns(Mob mob) {
		this.mob = mob;
	}
	
	public boolean areCooldownsPending() {
		return !cooldowns.isEmpty();
	}
	
	public void flag(CooldownFlags cooldown, int duration) {
		cooldowns.set(cooldown.ordinal(), true);
		World.getInstance().submit(new CooldownEvent(mob, cooldown, duration));
	}
	
	public void set(CooldownFlags cooldown, boolean value) {
		cooldowns.set(cooldown.ordinal(), value);
	}
	
	public boolean get(CooldownFlags cooldownFlags) {
		return cooldowns.get(cooldownFlags.ordinal());
	}
	
	public void reset() {
		cooldowns.clear();
	}

}