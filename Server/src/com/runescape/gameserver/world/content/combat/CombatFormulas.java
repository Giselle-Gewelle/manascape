package com.runescape.gameserver.world.content.combat;

import com.runescape.gameserver.world.content.combat.Combat.CombatStyle;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.content.skills.impl.Prayer.Prayers;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class CombatFormulas {

	public static int maxMeleeHit(Mob entity) {
		return MeleeMaxHit.calculate(entity);
	}

	public static int npcMeleeAttack(Player player) {
		int attackLevel = player.getSkills().getLevel(Skill.ATTACK);
		final int realAttackLevel = player.getSkills().getRealLevel(Skill.ATTACK);

		if(player.getPrayer().isPrayerActive(Prayers.BURST_OF_STRENGTH))
			attackLevel = (int) (attackLevel + realAttackLevel * 0.05);
		else if(player.getPrayer().isPrayerActive(Prayers.SUPERHUMAN_STRENGTH))
			attackLevel = (int) (attackLevel + realAttackLevel * 0.10);
		else if(player.getPrayer().isPrayerActive(Prayers.ULTIMATE_STRENGTH))
			attackLevel = (int) (attackLevel + realAttackLevel * 0.15);

		int attackBonus = bestMeleeAttack(player);

		return (int) (attackLevel + attackLevel * 0.15D + (attackBonus + attackBonus * 0.5D));
	}

	public static int mageDefence(Player player) {
		double defenceLevel = player.getSkills().getLevel(1) * 0.3;
		defenceLevel += player.getSkills().getLevel(Skill.MAGIC) * .7;
		int defBonus = player.getPlayerBonuses().getDefenceBonuses().getDefenceMagic();
		if(player.getPrayer().isPrayerActive(Prayers.CLARITY_OF_THOUGHT)) {
			defenceLevel += 1.05;
		} else if(player.getPrayer().isPrayerActive(Prayers.IMPROVED_REFLEXES)) {
			defenceLevel += 1.10;
		} else if(player.getPrayer().isPrayerActive(Prayers.INCREDIBLE_REFLEXES)) {
			defenceLevel += 1.15;
		}
		return (int) ((defenceLevel + (defBonus * 2)));
	}

	public static int meleeDef(Player player) {
		int defenceLevel = player.getSkills().getLevel(1);
		final int i = bestMeleeDefence(player);
		if(player.getPrayer().isPrayerActive(Prayers.THICK_SKIN))
			defenceLevel += 1.05;
		else if(player.getPrayer().isPrayerActive(Prayers.ROCK_SKIN))
			defenceLevel += 1.10;
		else if(player.getPrayer().isPrayerActive(Prayers.STEEL_SKIN))
			defenceLevel += 1.15;
		double mod = 1.0D;
		if(player.getInteractingEntity() == null) {
			mod = 0.75D;
		}
		return (int) ((defenceLevel + defenceLevel * 0.05D + (i + i * 0.05D)) * mod);
	}

	public static int npcMagicAttack(Player player) {
		int magicLevel = player.getSkills().getLevel(6);
		return((magicLevel + (player.getPlayerBonuses().getAttackBonuses().getAttackMagic() * 2)) + 45);
	}

	public static int bestMeleeAttack(Player player) {
		if(player.getPlayerBonuses().getAttackBonuses().getAttackStab() > player.getPlayerBonuses().getAttackBonuses().getAttackSlash()
				&& player.getPlayerBonuses().getAttackBonuses().getAttackStab() > player.getPlayerBonuses().getAttackBonuses().getAttackCrush())
			return player.getPlayerBonuses().getAttackBonuses().getAttackStab();

		if(player.getPlayerBonuses().getAttackBonuses().getAttackSlash() > player.getPlayerBonuses().getAttackBonuses().getAttackStab()
				&& player.getPlayerBonuses().getAttackBonuses().getAttackSlash() > player.getPlayerBonuses().getAttackBonuses().getAttackCrush())
			return player.getPlayerBonuses().getAttackBonuses().getAttackSlash();
		else
			return player.getPlayerBonuses().getAttackBonuses().getAttackCrush() > player.getPlayerBonuses().getAttackBonuses().getAttackSlash()
					&& player.getPlayerBonuses().getAttackBonuses().getAttackCrush() > player.getPlayerBonuses().getAttackBonuses().getAttackStab() ? player.getPlayerBonuses().getAttackBonuses()
					.getAttackCrush() : player.getPlayerBonuses().getAttackBonuses().getAttackStab();
	}

	public static int bestMeleeDefence(Player player) {
		if(player.getPlayerBonuses().getDefenceBonuses().getDefenceStab() > player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash()
				&& player.getPlayerBonuses().getDefenceBonuses().getDefenceStab() > player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush()) {
			return player.getPlayerBonuses().getDefenceBonuses().getDefenceStab();
		}
		if(player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() > player.getPlayerBonuses().getDefenceBonuses().getDefenceStab()
				&& player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() > player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush()) {
			return player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash();
		} else {
			return player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush() > player.getPlayerBonuses().getDefenceBonuses().getDefenceStab()
					&& player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush() > player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() ? player.getPlayerBonuses()
					.getDefenceBonuses().getDefenceCrush() : player.getPlayerBonuses().getDefenceBonuses().getDefenceStab();
		}
	}

}

class MeleeMaxHit {

	public static int calculate(Mob entity) {
		
		int bonus = 0;
		if(entity.getEquippedWeapon() != null && entity.getEquippedWeapon().getBonuses() != null) {
			bonus = entity.getEquippedWeapon().getBonuses().getOtherBonuses().getStrength();
		}

		int strength = 1;
		if(entity instanceof Player) {
			strength = entity.getSkills().getLevel(Skill.STRENGTH);
		} else if(entity instanceof NPC) {
			strength = entity.getSkills().getCombatLevel();
		}

		double effectiveStrength = Math.floor((strength * getPrayerBonus(entity)) + 8);

		if(entity instanceof Player) {
			effectiveStrength += getStyleBonus((Player) entity);
		} else {
			effectiveStrength += 1;
		}

		double max = Math.floor(5 + effectiveStrength * (bonus + 64) / 64);

		return (int) ((max) / 10D);
	}

	public static int getStyleBonus(Player player) {
		if(player.getCombatStyle() == CombatStyle.AGGRESSIVE) {
			return 3;
		} else if(player.getCombatStyle() == CombatStyle.ACCURATE) {
			return 1;
		}
		return 0;
	}

	public static double getPrayerBonus(Mob entity) {
		if(entity instanceof NPC) {
			return 1;
		}
		
		Player player = (Player) entity;
		if(player.getPrayer().isPrayerActive(Prayers.BURST_OF_STRENGTH)) {
			return 1.05;
		} else if(player.getPrayer().isPrayerActive(Prayers.SUPERHUMAN_STRENGTH)) {
			return 1.10;
		} else if(player.getPrayer().isPrayerActive(Prayers.ULTIMATE_STRENGTH)) {
			return 1.15;
		}
		return 1;
	}

}
