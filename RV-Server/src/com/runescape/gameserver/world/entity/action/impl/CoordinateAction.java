package com.runescape.gameserver.world.entity.action.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;

public class CoordinateAction extends Action {

	private Action action;
	private int distance;
	private Location location;

	public CoordinateAction(Mob entity, Location location, int distance, Action action) {
		super(entity, 600);
		this.action = action;
		this.distance = distance;
		this.location = location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.FOLLOW;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public void execute() {
		Mob entity = getMob();
		if(entity == null || entity.isDestroyed() || entity.isDead()) {
			this.stop();
		}
		
		CooldownFlags cooldownFlag = CooldownFlags.WALKING;
		if(!entity.getMobCooldowns().get(cooldownFlag)) {
			if(entity.getLocation().isWithinInteractionDistance(location, distance)) {
				entity.getWalkingQueue().reset();
				entity.getActionQueue().addAction(action);
				this.stop();
			}
		}
	}
	
}
