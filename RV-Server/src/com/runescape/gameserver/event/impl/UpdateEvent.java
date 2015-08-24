package com.runescape.gameserver.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.task.ConsecutiveTask;
import com.runescape.gameserver.task.ParallelTask;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.task.impl.NPCResetTask;
import com.runescape.gameserver.task.impl.NPCTickTask;
import com.runescape.gameserver.task.impl.NPCUpdateTask;
import com.runescape.gameserver.task.impl.PlayerResetTask;
import com.runescape.gameserver.task.impl.PlayerTickTask;
import com.runescape.gameserver.task.impl.PlayerUpdateTask;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * An event which starts player update tasks.
 * @author Graham Edgecombe
 *
 */
public class UpdateEvent extends Event {

	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;
	
	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public UpdateEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		List<Task> tickTasks = new ArrayList<Task>();
		List<Task> updateTasks = new ArrayList<Task>();
		List<Task> resetTasks = new ArrayList<Task>();
		
		for(NPC npc : World.getInstance().getNPCs()) {
			tickTasks.add(new NPCTickTask(npc));
			resetTasks.add(new NPCResetTask(npc));
		}
		
		Iterator<Player> it$ = World.getInstance().getPlayers().iterator();
		while(it$.hasNext()) {
			Player player = it$.next();
			//if(!player.getSession().isConnected()) {
			if(player.isDestroyed()) {
				it$.remove();
			} else {
				tickTasks.add(new PlayerTickTask(player));
				updateTasks.add(new ConsecutiveTask(new PlayerUpdateTask(player), new NPCUpdateTask(player)));
				resetTasks.add(new PlayerResetTask(player));
			}
		}
		
		// ticks can no longer be parallel due to region code
		Task tickTask = new ConsecutiveTask(tickTasks.toArray(new Task[0]));
		Task updateTask = new ParallelTask(updateTasks.toArray(new Task[0]));
		Task resetTask = new ParallelTask(resetTasks.toArray(new Task[0]));
		
		World.getInstance().submit(new ConsecutiveTask(tickTask, updateTask, resetTask));
	}

}
