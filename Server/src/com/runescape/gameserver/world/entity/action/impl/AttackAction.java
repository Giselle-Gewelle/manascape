package com.runescape.gameserver.world.entity.action.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.content.combat.Combat;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;
import com.runescape.gameserver.world.entity.mob.npc.NPC;

public class AttackAction extends Action {

	private Mob victim;

	private int waitTime = 0;

	public AttackAction(Mob attacker, Mob victim) {
		super(attacker, 100);
		this.victim = victim;
		
		if(attacker instanceof NPC) {
			NPC npc = (NPC) attacker;
			if(npc.hasAggro()) {
				attacker.setAggressorState(false);
				this.stop();
			}
		}
	}

	public AttackAction(Mob attacker, Mob victim, int waitTime) {
		super(attacker, 300);
		this.victim = victim;
		this.waitTime = waitTime;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.FOLLOW;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	private Location lastVictimLoc = null;
	
	@Override
	public void execute() {
		if(waitTime <= 0) {
			final Mob attacker = getMob();

			if(Combat.canAttack(attacker, victim)) {
				attacker.setAggressorState(true);
				
				if(victim instanceof NPC) {
					((NPC) victim).setHasAggro(true);
				}
				
				int distance = 1;
				
				if(attacker.getAttackType() == AttackType.MAGIC || attacker.getAttackType() == AttackType.RANGE) {
					distance = 5;
				}
				
				@SuppressWarnings("unused")
				boolean shouldMove = false;
				if(lastVictimLoc == null) {
					shouldMove = true;
				} else if(!lastVictimLoc.equals(victim.getLocation())) {
					//shouldMove = true;
				}
				if(!attacker.getLocation().isWithinInteractionDistance(victim.getLocation(), distance)) {
					lastVictimLoc = new Location(victim.getLocation().getX(), victim.getLocation().getY(), victim.getLocation().getHeight());
					Location close = attacker.getLocation().getClosestLocation(victim.getLocation());
					attacker.getWalkingQueue().walkTo(close.getX(), close.getY());
					return;
				} else if(attacker.getLocation().equals(victim.getLocation())) {
					Location close = attacker.getLocation().getClosestLocation(victim.getLocation());
					attacker.getWalkingQueue().walkTo(close.getX(), close.getY());
					return;
				}
				
				int speed = 5;
				if(attacker.getEquippedWeapon() != null) {
					speed = (600 * attacker.getEquippedWeapon().getSpeed());
					if(speed == -600) {
						speed = 600;
					}
					if(this.getDelay() != speed) {
						this.setDelay(speed);
					}
				}
				
				if(!attacker.isInCombat()) {
					attacker.resetInteractingEntity();
					//attacker.setCurrentSpell(null);
					//attacker.setAttackType(AttackType.CRUSH);
				}
				
				CooldownFlags cooldownFlag = CooldownFlags.MELEE_SWING;
				if(attacker.getAttackType() == AttackType.MAGIC) {
					cooldownFlag = CooldownFlags.SPELL_CAST;
				} else if(attacker.getAttackType() == AttackType.RANGE) {
					cooldownFlag = CooldownFlags.RANGED_SHOT;
				}
				
				if(!attacker.getMobCooldowns().get(cooldownFlag)) {
					Location victimLoc = victim.getLocation();
					if(attacker.getFaceLocation() != victimLoc) {
						attacker.face(victimLoc);
					}
					attacker.setAggressorState(true);
					Combat.doAttack(attacker, victim, attacker.getAttackType(), attacker.getCurrentSpell());
					attacker.getMobCooldowns().flag(cooldownFlag, speed - 600);
					/*
					 * Find out why this clears at the end of the attack
					 * action...
					 */
					attacker.getMobCooldowns().flag(cooldownFlag, 16000);
				} else {
					System.out.println("Flagged!");
				}
			} else {
				//System.out.println("Combat action stopped!");
				attacker.resetInteractingEntity();
				attacker.resetFace();
				attacker.setInCombat(false);
				if(attacker instanceof NPC) {
					((NPC) attacker).setAggroAction(null);
				}
				attacker.setAggressorState(false);
				this.stop();
			}
		} else {
			waitTime--;
		}
	}

}
