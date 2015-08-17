package com.runescape.gameserver.task.impl;

import java.util.Random;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.npc.NPCSpawn;

/**
 * A task which performs pre-update tasks for an NPC.
 * @author Graham Edgecombe
 *
 */
public class NPCTickTask implements Task {
	
	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;
	private final Random random = new Random();
	
	/**
	 * Creates the tick task.
	 * @param npc The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed set the last known region.
		 */
		if(npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}
		
		if(npc.getActionQueue().size() < 1) {
			NPCSpawn npcSpawn = npc.getNPCSpawn();
			if(npcSpawn.canWalk()) {
				if(npc.getInteractingEntity() != null) {
					// follow, or follow if attacking
				} else {
					if(!npc.getMobCooldowns().get(CooldownFlags.WALKING) && !npc.isDead() && !npc.isDestroyed()) {
						/*
						 * Make sure that players are in the regions nearby, otherwise we shouldn't bother moving the NPC.
						 * Hoping this will save some memory!
						 */
						if(World.getInstance().getRegionManager().getPlayersInSurroundingRegions(npc).size() > 0) {
							/*
							 * Random walking!
							 */
							if(random.nextInt(4) == 1) {
								int x = npc.getLocation().getX();
								int y = npc.getLocation().getY();
								int moveX = x + (random.nextInt(3) - random.nextInt(3));
								int moveY = y + (random.nextInt(3) - random.nextInt(3));
								
								// TODO check NPC size and make it clip accordingly
								
								Location min = npcSpawn.getMinimumLocation();
								Location max = npcSpawn.getMaximumLocation();
								
								/*
								 * Make sure the NPC isn't going out of its bounds, if it has bounds.
								 */
								if(min != null) {
									int minX = min.getX();
									int minY = min.getY();
									
									if(moveX < minX) {
										moveX = minX;
									}
									if(moveY < minY) {
										moveY = minY;
									}
								}
								if(max != null) {
									int maxX = max.getX();
									int maxY = max.getY();
									
									if(moveX > maxX) {
										moveX = maxX;
									}
									if(moveY > maxY) {
										moveY = maxY;
									}
								}
								
								npc.getWalkingQueue().walkTo(moveX, moveY, 5, false);
							}
						}
					}
				}
			}
		}

		npc.getWalkingQueue().processNextMovement();
		npc.tick();
		npc.getSkills().handleHealthRegen();
	}

}
