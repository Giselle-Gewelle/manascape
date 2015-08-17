package com.runescape.gameserver.world.content.god.battles;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.god.God;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.npc.NPCSpawn;
import com.runescape.gameserver.world.entity.object.GameObject;

public abstract class Battlefield {

	public abstract God[] getGods();
	
	public abstract Location[] getLocations();
	
	public abstract int getMaxFighters();
	
	public abstract Location getCenter();
	public abstract Location getMin();
	public abstract Location getMax();
	
	public abstract GameObject[] getObjects();
	
	public final void spawnInitialObjects() {
		GameObject[] objects = getObjects();
		
		for(int i = 0; i < objects.length; i++) {
			World.getInstance().register(objects[i]);
		}
	}
	
	public final void spawnInitialFighters() {
		God[] gods = getGods();
		
		for(int i = 0; i < gods.length; i++) {
			God god = gods[i];
			int maxFighters = getMaxFighters();
			Location spawnLocation = getLocations()[i];
			Location min = getMin();
			Location max = getMax();
			Location center = getCenter();
			
			for(int n = 0; n < maxFighters; n++) {
				NPC npc = god.getRandomFighter();
				npc.getWalkingQueue().setRunningToggled(true);
				npc.setLocation(spawnLocation);
				npc.setNPCSpawn(new NPCSpawn(npc, true, 1, spawnLocation, min, max));
				World.getInstance().register(npc);
				npc.getWalkingQueue().walkTo(center.getX(), center.getY());
				npc.setDefaultWalkTo(center);
			}
		}
	}

}
