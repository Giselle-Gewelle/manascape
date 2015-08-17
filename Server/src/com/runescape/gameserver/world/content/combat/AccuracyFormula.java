package com.runescape.gameserver.world.content.combat;

import java.security.SecureRandom;

import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.Combat.CombatStyle;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.content.skills.impl.Prayer.Prayers;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class AccuracyFormula {
	
	private static final SecureRandom r = new SecureRandom();
	
	public static boolean hit(Mob e, Player victim, AttackType attackType, double multiplier) {
		if (e == null || victim == null || attackType == null)
			return false;
		
		final double attack = Math.floor(getAttack(e, attackType) * multiplier);
		final double defence = Math.floor(getDefence(victim, attackType));
		
		double accuracy = 0;
		
		if (attack < defence)
			accuracy = ((attack-1) / (2 * defence));
		else
			accuracy = ((1 - (defence+1) / (2 * attack)));
		
		if (e.isPlayer()) {
			Player player = (Player)e;
			player.sendOMessage("Change of hitting: " + Math.round(accuracy * 100D) + "%");
		}
		return r.nextDouble() < accuracy;
	}
	
	private static int getBestAttack(Player player, AttackType attackType)  {
			if (attackType.isMelee()) {
					if (player.getPlayerBonuses().getAttackBonuses().getAttackStab() > player.getPlayerBonuses().getAttackBonuses().getAttackSlash()
					&& player.getPlayerBonuses().getAttackBonuses().getAttackStab() > player.getPlayerBonuses().getAttackBonuses().getAttackCrush())
						return player.getPlayerBonuses().getAttackBonuses().getAttackStab();
					
					if (player.getPlayerBonuses().getAttackBonuses().getAttackSlash() > player.getPlayerBonuses().getAttackBonuses().getAttackStab()
							&& player.getPlayerBonuses().getAttackBonuses().getAttackSlash() > player.getPlayerBonuses().getAttackBonuses().getAttackCrush())
						return player.getPlayerBonuses().getAttackBonuses().getAttackSlash();
					else
						return player.getPlayerBonuses().getAttackBonuses().getAttackCrush() > player.getPlayerBonuses().getAttackBonuses().getAttackSlash()
								&& player.getPlayerBonuses().getAttackBonuses().getAttackCrush() > player.getPlayerBonuses().getAttackBonuses().getAttackStab() ?
										player.getPlayerBonuses().getAttackBonuses().getAttackCrush() : player.getPlayerBonuses().getAttackBonuses().getAttackStab();
			}
			
			if (attackType == AttackType.MAGIC)
				return player.getPlayerBonuses().getAttackBonuses().getAttackMagic();
			
			if (attackType == AttackType.RANGE)
				return player.getPlayerBonuses().getAttackBonuses().getAttackRange();
		
		return player.getPlayerBonuses().getAttackBonuses().getAttackStab();
	}
	
	private static int getBestDefence(Player player, AttackType attackType) {
		
		if (attackType.isMelee()) {
			if (player.getPlayerBonuses().getDefenceBonuses().getDefenceStab() > player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash()
					&& player.getPlayerBonuses().getDefenceBonuses().getDefenceStab() > player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush()) {
				return player.getPlayerBonuses().getDefenceBonuses().getDefenceStab();
			}
			if (player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() > player.getPlayerBonuses().getDefenceBonuses().getDefenceStab()
					&& player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() > player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush()) {
				return player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash();
			} else {
				return player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush() > player.getPlayerBonuses().getDefenceBonuses().getDefenceStab()
						&& player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush() > player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash() ?
								player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush() : player.getPlayerBonuses().getDefenceBonuses().getDefenceStab();
			}
		}
		
		if (attackType == AttackType.MAGIC)
			return player.getPlayerBonuses().getDefenceBonuses().getDefenceMagic();
		
		if (attackType == AttackType.RANGE)
			return player.getPlayerBonuses().getDefenceBonuses().getDefenceRange();
		
		return player.getPlayerBonuses().getDefenceBonuses().getDefenceStab();
	}
	
	private static double getAttack(Mob e, AttackType attackType) {
		if (e.isPlayer()) {
			Player player = (Player)e;
			final Skill level = attackType == AttackType.RANGE ? Skill.RANGED
					: attackType.isMelee() ? Skill.ATTACK
							: attackType == AttackType.MAGIC ? Skill.MAGIC : null;
			
			if (level == null)
				return -1.0;
			
			double atkLevel = (Math.floor(player.getSkills().getLevel(level)
					* getPrayerAttack(player, attackType)) + 8);
			
			atkLevel = (atkLevel * (getBestAttack(player, attackType) + 64)) / 10.0D;
			
			return atkLevel
					+ (player.getCombatStyle() == CombatStyle.ACCURATE ? 3
							: player.getCombatStyle() == CombatStyle.DEFENSIVE ? 1 : 0);
		}
		return 0;
	}
	
	private static double getDefence(Player player, AttackType attackType) {
		double defLevel = Math.floor((player.getSkills().getLevel(Skill.DEFENCE)
				* getPrayerDefence(player)) + 8);
		if (attackType == AttackType.MAGIC) {
			final double EM = Math.floor(player.getSkills().getLevel(Skill.MAGIC) * .7);
			final double d = Math.floor(defLevel * .3);
			defLevel = d + EM;
		}
		
		defLevel = (defLevel * (getBestDefence(player, attackType) + 64)) / 10.0D;
		
		return defLevel + (player.getCombatStyle() == CombatStyle.CONTROLLED ? 3 : player.getCombatStyle() == CombatStyle.DEFENSIVE ? 1 : 0);
		
	}
	
	private static double getPrayerAttack(Player player, AttackType attackType) {
		if (attackType.isMelee()) {
			if (player.getPrayer().isPrayerActive(Prayers.CLARITY_OF_THOUGHT)) {
				return 1.05;
			} else if (player.getPrayer().isPrayerActive(Prayers.IMPROVED_REFLEXES)) {		
				return 1.10;
			} else if (player.getPrayer().isPrayerActive(Prayers.INCREDIBLE_REFLEXES)) {
				return 1.15;
			}
		}
		return 1;
	}
	
	private static double getPrayerDefence(Player player) {
		if (player.getPrayer().isPrayerActive(Prayers.THICK_SKIN))
			return 1.05;
		else if (player.getPrayer().isPrayerActive(Prayers.ROCK_SKIN))
			return 1.10;
		else if (player.getPrayer().isPrayerActive(Prayers.STEEL_SKIN))
			return 1.15;
		return 1;
	}

}
