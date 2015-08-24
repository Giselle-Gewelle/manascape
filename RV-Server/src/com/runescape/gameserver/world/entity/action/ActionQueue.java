package com.runescape.gameserver.world.entity.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.runescape.gameserver.world.Graphic;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.mob.Mob;

/**
 * Stores a queue of pending actions.
 * @author blakeman8192
 * @author Graham Edgecombe
 *
 */
public class ActionQueue {
	
	/**
	 * The maximum number of actions allowed to be queued at once, deliberately
	 * set to the size of the player's inventory.
	 */
	public static final int MAXIMUM_SIZE = Inventory.SIZE;
	
	/**
	 * A queue of <code>Action</code> objects.
	 */
	private Queue<Action> queuedActions = new LinkedList<Action>();
	
	/**
	 * The current action.
	 */
	private Action currentAction = null;
	
	/**
	 * The entity.
	 */
	private Mob entity;
	
	public ActionQueue(Mob entity) {
		this.entity = entity;
	}
	
	public int size() {
		return queuedActions.size();
	}
	
	/**
	 * Cancels all queued action events.
	 */
	public void cancelQueuedActions() {
		for(Action actionEvent : queuedActions) {
			actionEvent.stop();
		}
		queuedActions.clear();
		if(currentAction != null)
			currentAction.stop();
		currentAction = null;
	}
	
	/**
	 * Adds an <code>Action</code> to the queue.
	 * @param action The action.
	 */
	public void addAction(Action action) {
		if(queuedActions.size() >= MAXIMUM_SIZE) {
			return;
		}
		//int queueSize = queuedActions.size() + (currentAction == null ? 0 : 1);
		switch(action.getStackPolicy()) {
			case ALWAYS:
				break;
			case NEVER:
				clearAllActions();
				break;
		}
		switch(action.getAnimationPolicy()) {
		    case RESET_NONE:
		    	break;
		    case RESET_ALL:
				entity.playAnimation(Animation.create(-1));
				entity.playGraphics(Graphic.create(-1));
				break;			    	
	    }
		if(queuedActions.size() > 0) {
			Iterator<Action> it = queuedActions.iterator();
			while(it.hasNext()) {
			    Action queuedAction = it.next();
			    switch(queuedAction.getStackPolicy()) {
				    case ALWAYS:
				    	break;
				    case NEVER:
				    	queuedAction.stop();
				    	it.remove();
				    	break;
			    }
			}
		}
		
		queuedActions.add(action);
		processNextAction();
	}
	
	/**
	 * Clears the action queue and current action.
	 */
	public void clearAllActions() {
		boolean resetAnimations = false;
		if(queuedActions.size() > 0) {
			for(Action action : queuedActions) {
				switch(action.getAnimationPolicy()) {
				case RESET_NONE:
					break;
				case RESET_ALL:
					resetAnimations = true;
					break;
				}
				action.stop();
			}
			queuedActions = new LinkedList<Action>();
		}
		if(resetAnimations) {
			entity.playAnimation(Animation.create(-1));
			entity.playGraphics(Graphic.create(-1));
		}
	}
	
	/**
	 * Purges actions in the queue with a <code>WalkablePolicy</code> of <code>NON_WALKABLE</code>.
	 */
	public void clearNonWalkableActions() {
		if(currentAction != null) {
			switch(currentAction.getWalkablePolicy()) {
			case WALKABLE:
				break;
			case NON_WALKABLE:
				currentAction.stop();
				currentAction = null;
				break;
			case FOLLOW:
				currentAction.stop();
				currentAction = null;
				break;
			}
		}
		for(Action actionEvent : queuedActions) {
			switch(actionEvent.getWalkablePolicy()) {
			case WALKABLE:
				break;
			case NON_WALKABLE:
				actionEvent.stop();
				queuedActions.remove(actionEvent);
				break;
			case FOLLOW:
				actionEvent.stop();
				queuedActions.remove(actionEvent);
				break;
			}
		}
	}

	/**
	 * Processes this next action.
	 */
	public void processNextAction() {
		if(currentAction != null) {
			if(currentAction.isRunning()) {
				return;
			} else {
				currentAction = null;
			}
		}
		if(queuedActions.size() > 0) {
			currentAction = queuedActions.poll();
			World.getInstance().submit(currentAction);
		}
	}

}
