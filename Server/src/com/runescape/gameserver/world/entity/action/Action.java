package com.runescape.gameserver.world.entity.action;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.world.entity.mob.Mob;

/**
 * An <code>Event</code> used for handling game actions.
 * @author blakeman8192
 * @author Graham Edgecombe
 * @author Aizen Sousuke
 */
public abstract class Action extends Event {
	
	/**
	 * A queue policy determines when the clients should queue up actions.
	 * @author Graham Edgecombe
	 *
	 */
	public enum StackPolicy {
		
		/**
		 * This indicates actions will always be queued.
		 */
		ALWAYS,
		
		/**
		 * This indicates actions will never be queued.
		 */
		NEVER,
		
	}
	
	/**
	 * A queue policy determines whether the action can occur while walking.
	 * @author Graham Edgecombe
	 * @author Brett Russell
	 *
	 */
	public enum WalkablePolicy {
		
		/**
		 * This indicates actions may occur while walking.
		 */
		WALKABLE,
		
		/**
		 * This indicates actions cannot occur while walking.
		 */
		NON_WALKABLE,
		
		/**
		 * This indicates actions can continue while following.
		 */
		FOLLOW,
		
	}
	
	/**
	 * A cancel policy determines when the action should destruct.
	 * @author Graham Edgecombe
	 *
	 */
	public enum CancelPolicy {
		
		/**
		 * This indicates actions will cancelled on any interaction.
		 */
		ALWAYS,
		
		/**
		 * This indicates actions will be cancelled only when walking.
		 */
		ONLY_ON_WALK
		
	}
	
	/**
	 * An animation policy determines if the action should reset animations when cancelled.
	 * @author Graham Edgecombe
	 *
	 */
	public enum AnimationPolicy {
		
		/**
		 * This indicates the action will reset your animation.
		 */
		RESET_ALL,
		
		/**
		 * This indicates the action will not reset your animation.
		 */
		RESET_NONE
		
	}

	/**
	 * The <code>Entity</code> associated with this ActionEvent.
	 */
	private Mob entity;

	/**
	 * Creates a new ActionEvent.
	 * @param entity The entity.
	 * @param delay The initial delay.
	 */
	public Action(Mob entity, long delay) {
		super(delay);
		this.entity = entity;
	}

	/**
	 * Gets the entity.
	 * @return The entity.
	 */
	public Mob getMob() {
		return entity;
	}
	
	/**
	 * Gets the queue policy of this action.
	 * @return The queue policy of this action.
	 */
	public abstract StackPolicy getStackPolicy();
	
	/**
	 * Gets the WalkablePolicy of this action.
	 * @return The walkable policy of this action.
	 */
	public abstract WalkablePolicy getWalkablePolicy();
	
	/**
	 * Gets the AnimationPolicy of this action.
	 * @return The animation policy of this action.
	 */
	public abstract AnimationPolicy getAnimationPolicy();
	
	@Override
	public void stop() {
		super.stop();
		entity.getActionQueue().processNextAction();
	}

}
