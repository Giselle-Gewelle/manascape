package com.runescape.gameserver.world.content.combat.magic;

import com.runescape.gameserver.pathfinder.Distance;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class ProjectileHandler {
	
	private static enum Projectile {
		ARROW(42, 3, 43, 30, 15),
		DARK_BOW_SECOND(30, 3, 43, 31, 25),
		KNIFE(32, 3, 45, 35, 5),
		DART(38, 2, 45, 35, 5),
		SPELL(50, 6, 45, 29, 15),
		MOVING_BARRAGE(0, 6, 50, 50, 0),
		JAD_MAGIC(50, 25, 100, 26, 15),
		JAD_RANGE(50, 50, 60, 26, 15),
		CANNON(30, 8, 37, 37, 15);
		public int delay, slowness, startHeight, endHeight, curve;

		Projectile(int delay, int slowness, int startHeight, int endHeight,
				int curve) {
			this.delay = delay;
			this.slowness = slowness;
			this.startHeight = startHeight;
			this.endHeight = endHeight;
			this.curve = curve;
		}
	}

	public static int sendMagicProjectile(Mob caster, Mob victim, Spells spell) {
		
		final Projectile p = Projectile.SPELL;
		
		int distance = Distance.calculateDistance(caster, victim);
		int speed = p.delay + p.slowness + distance * 5;
		double delay = p.delay + p.slowness + distance * 5d;
		delay = Math.ceil((delay * 12d) / 600d);
		if (distance > 1)
			delay += 1;
		for (Player player : World.getInstance().getRegionManager()
				.getLocalPlayers(caster)) {
			if (player != null && player.getLocation().isWithinViewingDistance
					(caster.getLocation())) {
				player.getPacketSender().sendProjectile(caster.getLocation(), victim.getLocation(), spell.getProjectile(), p.delay, speed, p.startHeight, p.endHeight, victim.getLockIndex(), p.curve);
			}
		}
		return (int)delay;
	}
}
