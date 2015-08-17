package com.runescape.gameserver.world.content.combat.magic;

import com.runescape.gameserver.world.Graphic;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.content.combat.AccuracyFormula;
import com.runescape.gameserver.world.content.combat.Combat;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.content.miscellaneous.Misc;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.Damage.Hit;
import com.runescape.gameserver.world.entity.Damage.HitType;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.util.LevelRequirement;

public class SpellHandler {

	private static boolean playerHasSpellRequirements(Player player, Spells s) {
		/*
		 * if (player.isOwner()) return true;
		 */

		LevelRequirement level = new LevelRequirement(s.getLevel(), Skill.MAGIC);
		if(!level.playerHasRequirement(player)) {
			player.getPacketSender().sendMessage(level.getUnmetSpellRequirementMessage());
			player.getWalkingQueue().reset();
			player.setInCombat(false);
			return false;
		}

		if(player.getCurrentSpellBook() != s.getBook()) {
			player.getPacketSender().sendMessage("You cannot use this spell while using this spell book.");
			return false;
		}

		if(!playerHasRequiredRunes(player, s))
			return false;

		return true;
	}

	private static boolean playerHasRequiredRunes(Player player, Spells s) {
		boolean hasRunes = true;

		for(Item rune : s.getRuneReq().getRunesRequired()) {
			if(!hasRunes)
				continue;
			if(hasValidRuneStaff(player, rune.getId())) {
				continue;
			}
			if(!player.getInventory().containsItem(rune.getId(), rune.getCount())) {
				hasRunes = false;
				player.getPacketSender().sendMessage("You don't have the required runes to cast this spell.");
				player.setInCombat(false);
				player.getWalkingQueue().reset();
			}
		}

		// TODO: "Item" requirements for spells like the Iban Blast.
		return hasRunes;
	}

	private static boolean hasValidRuneStaff(Player player, int rune) {
		Item item = player.getEquipment().get(Equipment.SLOT_WEAPON);
		int staff = 0;
		if(item == null)
			return false;
		else
			staff = item.getId();
		switch(rune) {
		case 554:
			if(staff == 1387) {
				return true;
			}

			break;
		case 555:
			if(staff == 1383) {
				return true;
			}
			break;
		case 556:
			if(staff == 1381) {
				return true;
			}
			break;
		case 557:
			if(staff == 1385) {
				return true;
			}
			break;
		}
		return false;
	}

	private static void deleteRunes(Player player, Spells s) {
		for(Item i : s.getRuneReq().getRunesRequired()) {
			if(hasValidRuneStaff(player, i.getId()))
				continue;
			player.getInventory().remove(i);
		}
	}

	public static boolean startSpell(Mob e, final Spells s) {
		if(e.isPlayer() && !playerHasSpellRequirements((Player) e, s)) {
			return false;
		}

		e.setCurrentSpell(s);

		e.playAnimation(Animation.create(s.getAnimation()));
		e.playGraphics(Graphic.create(s.getStartGfx(), 100));

		if(e.isPlayer()) {
			deleteRunes((Player) e, s);
			if(s.getMaximumHit() <= 0)
				((Player) e).getSkills().addSkillXP(s.getXPGain(), Skill.MAGIC);
		}

		if(s.getMaximumHit() > 0 && e.getInteractingEntity() != null) {
			final int delay = Combat.getDelayForDistance(Misc.distanceBetween(e, e.getInteractingEntity()));
			magicCombatHit(e, s, delay);
		}
		e.setCurrentSpell(null);
		return true;
	}

	private static void magicCombatHit(final Mob caster, final Spells s, int delay) {
		final Mob victim = caster.getInteractingEntity();
		final boolean hasHit = victim.isPlayer() ? AccuracyFormula.hit(caster, (Player) victim, AttackType.MAGIC, 1) : Misc.random(caster.meleeAtk()) > Misc.random(victim.mageDef());

		if(s.getAnimation() > 0) {
			caster.playAnimation(Animation.create(s.getAnimation()));
		}
		if(s.getStartGfx() > 0) {
			caster.playGraphics(Graphic.create(s.getStartGfx(), 100));
		}

		delay = ProjectileHandler.sendMagicProjectile(caster, victim, s);

		int damage = Misc.random(s.getMaximumHit());

		Hit h;

		if(hasHit) {
			h = new Hit(damage, HitType.NORMAL_DAMAGE);
			if(s.getEndGfx() > 0)
				victim.playGraphics(Graphic.create(s.getEndGfx(), delay, 0));
		} else {
			h = new Hit(0, HitType.NO_DAMAGE);
			victim.playGraphics(Graphic.create(85, delay, 0));
		}

		if(victim.isPlayer()) {
			Player victimP = (Player) victim;
			victimP.inflictDamage(h, caster);
		} else {
			NPC victimN = (NPC) victim;
			victimN.inflictDamage(h, caster);
		}
		caster.setInCombat(false);
		caster.setInteractingEntity(null);
		caster.setCurrentSpell(null);
		caster.resetFace();
	}

}
