package com.runescape.gameserver.world.content.combat;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.content.combat.magic.SpellHandler;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.WeaponDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.Damage.Hit;
import com.runescape.gameserver.world.entity.Damage.HitType;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * Handles the combat system.
 * 
 * @author Brett
 * @author Aizen Sousuke
 */
public class Combat {

	/*
	 * public static enum AttackType { MELEE, RANGED, MAGIC, }
	 */

	public static enum CombatStyle {
		ACCURATE(0, new Skill[] { Skill.ATTACK }, new double[] { 4 }), AGGRESSIVE(1, new Skill[] { Skill.STRENGTH }, new double[] { 4 }), DEFENSIVE(2, new Skill[] { Skill.DEFENCE },
				new double[] { 4 }), CONTROLLED(3, new Skill[] { Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE }, new double[] { 1.33, 1.33, 1.33 }), MAGIC(4, new Skill[] { Skill.MAGIC },
				new double[] { 2 }), DEFENSIVE_MAGIC(5, new Skill[] { Skill.MAGIC, Skill.DEFENCE }, new double[] { 1.33, 1 }), ACCURATE_RANGE(6, new Skill[] { Skill.RANGED }, new double[] { 4 }), AGGRESSIVE_RANGE(
				7, new Skill[] { Skill.RANGED }, new double[] { 4 }), DEFENSIVE_RANGE(8, new Skill[] { Skill.RANGED, Skill.DEFENCE }, new double[] { 2, 2 });

		private static Map<Integer, CombatStyle> combatStyles = new HashMap<Integer, CombatStyle>();

		public static CombatStyle forId(int combatStyle) {
			return combatStyles.get(combatStyle);
		}

		static {
			for(CombatStyle combatStyle : CombatStyle.values()) {
				combatStyles.put(combatStyle.id, combatStyle);
			}
		}

		private int id;
		private Skill[] skills;
		private double[] experiences;

		private CombatStyle(int id, Skill[] skills, double[] experiences) {
			this.id = id;
			this.skills = skills;
			this.experiences = experiences;
		}

		public int getId() {
			return id;
		}

		public Skill[] getSkills() {
			return skills;
		}

		public Skill getSkill(int index) {
			return skills[index];
		}

		public double[] getExperiences() {
			return experiences;
		}

		public double getExperience(int index) {
			return experiences[index];
		}
	}

	public static enum AttackType {
		STAB(0), SLASH(1), CRUSH(2), MAGIC(3), RANGE(4);

		private static Map<Integer, AttackType> attackTypes = new HashMap<Integer, AttackType>();

		public static AttackType forId(int attackType) {
			return attackTypes.get(attackType);
		}

		static {
			for(AttackType attackType : AttackType.values()) {
				attackTypes.put(attackType.id, attackType);
			}
		}

		private int id;

		private AttackType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public boolean isMelee() {
			return this == AttackType.CRUSH || this == AttackType.SLASH || this == AttackType.STAB;
		}
	}

	public static class CombatSession {
		private int damage = 0;

		// private long timestamp = 0;

		public CombatSession() {

		}

		public int getDamage() {
			return this.damage;
		}
	}

	/**
	 * Represents an instance of combat, where Entity is an assailant and
	 * Integer is the sum of their damage done. This is mapped to every victim
	 * in combat, and used to determine drops.
	 * 
	 * @author Brett Russell
	 */
	public static class CollectiveCombatSession {
		private long stamp;
		private Map<Mob, CombatSession> damageMap;
		private Set<Mob> names = damageMap.keySet();
		private boolean isActive;
		@SuppressWarnings("unused")
		private Mob victim;

		public CollectiveCombatSession(Mob victim) {
			java.util.Date date = new java.util.Date();
			this.stamp = date.getTime();
			this.isActive = true;
			this.victim = victim;
		}

		/**
		 * Gets the timestamp for this object (when the session began).
		 * 
		 * @return The timestamp.
		 */
		public long getStamp() {
			return stamp;
		}

		/**
		 * Gets the entity with the highest damage count this session.
		 * 
		 * @return The entity with the highest damage count.
		 */
		public Mob getTopDamage() {
			Mob top = null;
			int damageDone = 0;
			int currentHighest = 0;

			Iterator<Mob> itr = names.iterator();

			while(itr.hasNext()) {
				Mob currentEntity = itr.next();
				damageDone = damageMap.get(currentEntity).getDamage();
				if(damageDone > currentHighest) {
					currentHighest = damageDone;
					top = currentEntity;
				}
			}
			return top;
		}

		/**
		 * Returns the Map of this session's participants. If you would want it,
		 * that is...
		 * 
		 * @return A Map of the participants and their damage done.
		 */
		public Map<Mob, CombatSession> getDamageCharts() {
			return damageMap;
		}

		/**
		 * Adds a participant to this session.
		 * 
		 * @param participant
		 *            The participant to add.
		 */
		public void addParticipant(Mob participant) {
			// TODO CombatSession
			damageMap.put(participant, null);
		}

		/**
		 * Remove a participant.
		 * 
		 * @param participant
		 *            The participant to remove.
		 */
		public void removeParticipant(Mob participant) {
			damageMap.remove(participant);
		}

		/**
		 * Sets this sessions active state.
		 * 
		 * @param state
		 *            A <code>boolean</code> value representing the state.
		 */
		public void setState(boolean b) {
			this.isActive = b;
		}

		/**
		 * Determine the active state of this session.
		 * 
		 * @return The active state as a <code>boolean</code> value.
		 */
		public boolean getIsActive() {
			return this.isActive;
		}
	}

	/**
	 * Checks if an entity can attack another. Shamelessly stolen from another
	 * of Graham's projects.
	 * 
	 * @param source
	 *            The source entity.
	 * @param victim
	 *            The target entity.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public static boolean canAttack(Mob source, Mob victim) {
		if(source == null || victim == null) {
			return false;
		}

		if(source.isDestroyed() || victim.isDestroyed()) {
			return false;
		}
		if(victim.isDead() || source.isDead()) {
			return false;
		}
		if((source instanceof Player) && (victim instanceof Player)) {
			// attackable zones, etc
			return false;
		}
		//if(!source.getLocation().isWithinInteractionDistance(victim.getLocation(), 5)) {
		//	return false;
		//}
		return true;
	}

	/**
	 * Inflicts damage on the recipient.
	 * 
	 * @param recipient
	 *            The entity taking the damage.
	 * @param damage
	 *            The damage to be done.
	 */
	public static void inflictDamage(Mob recipient, Mob aggressor, Hit damage) {
		if((recipient instanceof Player) && (aggressor != null)) {
			Player p = (Player) recipient;
			p.inflictDamage(damage, aggressor);
			int anim = 424;
			Item weapon = ((Player) recipient).getEquipment().get(Equipment.SLOT_WEAPON);
			int weaponId = -1;
			if(weapon != null) {
				weaponId = weapon.getId();
			}
			if(weaponId > -1) {
				WeaponDefinition weaponDef = WeaponDefinition.forId(weaponId);
				if(weaponDef != null) {
					anim = weaponDef.getAnims()[WeaponDefinition.DEFEND];
				}
			}
			p.playAnimation(Animation.create(anim, 2));
		} else if((recipient instanceof NPC) && (aggressor != null)) {
			NPC n = (NPC) recipient;
			n.inflictDamage(damage, aggressor);
			n.playAnimation(Animation.create(n.getDefendAnim(), 2));
		}
		if(aggressor instanceof Player) {
			Player player = (Player) aggressor;
			CombatStyle combatStyle = player.getCombatStyle();
			Skill[] skills = combatStyle.getSkills();
			double[] exp = combatStyle.getExperiences();
			for(int i = 0; i < skills.length; i++) {
				player.getSkills().addSkillXP(damage.getDamage() * exp[i], skills[i]);
			}
			((Player) aggressor).getSkills().addSkillXP(damage.getDamage() * 1.33, Skill.HITPOINTS);
			// ((Player) aggressor).getSkills().addExperience(Skills.ATTACK
			// /*getAttackStyle*/, damage.getDamage() * 4);
		}
	}

	static SecureRandom random = new SecureRandom();

	public static Hit calculateHit(Mob attacker, Mob victim, AttackType attack) {
		int verdict = 0;
		HitType hit = HitType.NORMAL_DAMAGE;
		
		verdict = random.nextInt(CombatFormulas.maxMeleeHit(attacker) + 1);
		if(verdict >= victim.getSkills().getLevel(Skill.HITPOINTS)) {
			verdict = victim.getSkills().getLevel(Skill.HITPOINTS);
		}
		
		if(verdict == 0) {
			hit = HitType.NO_DAMAGE;
		}
		
		Hit finalHit = new Hit(verdict, hit);
		return finalHit;
	}
	
	public static Hit calculatePlayerHit(Mob source, Mob victim, AttackType attack) {
		int verdict = 0;
		HitType hit = HitType.NORMAL_DAMAGE;
		if(victim instanceof Player) {
			Player v = (Player) victim;
			// calculations here
			verdict = random.nextInt(CombatFormulas.maxMeleeHit(v));

			if(verdict >= v.getSkills().getLevel(Skill.HITPOINTS)) {
				verdict = v.getSkills().getLevel(Skill.HITPOINTS);
			}
		} else if(victim instanceof NPC) {
			NPC v = (NPC) victim;
			verdict = random.nextInt(CombatFormulas.maxMeleeHit((Player) source));
			if(verdict >= v.getHealth()) {
				verdict = v.getHealth();
			}
		}
		if(verdict == 0) {
			hit = HitType.NO_DAMAGE;
		}
		Hit thisAttack = new Hit(verdict, hit);
		return thisAttack;
	}

	public static Hit calculateNPCHit(Mob source, Mob victim, AttackType attack) {
		int verdict = 0;
		HitType hit = HitType.NORMAL_DAMAGE;
		if(victim instanceof Player) {
			Player v = (Player) victim;
			// calculations here
			verdict = 1;
			if(verdict >= v.getSkills().getLevel(Skill.HITPOINTS)) {
				verdict = v.getSkills().getLevel(Skill.HITPOINTS);
			}
		} else if(victim instanceof NPC) {
			NPC v = (NPC) victim;
			verdict = 1;
			if(verdict >= v.getHealth()) {
				verdict = v.getHealth();
			}
		}
		if(verdict == 0) {
			hit = HitType.NO_DAMAGE;
		}
		Hit thisAttack = new Hit(verdict, hit);
		return thisAttack;
	}

	public static void initiateCombat(Mob source, Mob victim) {

	}

	@SuppressWarnings("unused")
	private static void handleMagicAttack(Mob source, Mob victim) {
		Spells spell = source.getCurrentSpell();
	}

	public static void doAttack(Mob source, Mob victim, AttackType attackType, Spells spell) {
		if(!canAttack(source, victim))
			return;
		source.setInteractingEntity(victim);

		if((attackType == AttackType.MAGIC) && spell != null) {
			SpellHandler.startSpell(source, spell);
		} else if(attackType.isMelee()) {
			int anim = 422;
			if(source instanceof Player) {
				Item weapon = ((Player) source).getEquipment().get(Equipment.SLOT_WEAPON);
				int weaponId = -1;
				if(weapon != null) {
					weaponId = weapon.getId();
				}
				if(weaponId > -1) {
					WeaponDefinition weaponDef = WeaponDefinition.forId(weaponId);
					if(weaponDef != null) {
						anim = weaponDef.getAttackAnims()[WeaponDefinition.ATTACK_1];
					}
				}
				source.playAnimation(Animation.create(anim, 0));
				//inflictDamage(victim, source, calculatePlayerHit(source, victim, attackType));
			} else if(source instanceof NPC) {
				source.playAnimation(Animation.create(((NPC) source).getAttackAnim(), 0));
				//inflictDamage(victim, source, calculateNPCHit(source, victim, attackType));
			}
			
			inflictDamage(victim, source, calculateHit(source, victim, attackType));
			source.setInCombat(true);
			victim.setInCombat(true);
		}
	}

	public static int getDelayForDistance(int distance) {
		switch(distance) {
		case 1: // '\001'
			return 2;

		case 2: // '\002'
			return 3;

		case 3: // '\003'
			return 3;

		case 4: // '\004'
			return 3;

		case 5: // '\005'
			return 3;

		case 6: // '\006'
			return 3;

		case 7: // '\007'
			return 4;

		case 8: // '\b'
			return 4;
		}
		return 4;
	}
}
