package com.runescape.gameserver.world.content.god;

import java.util.Random;

import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;

public abstract class God {

	private Random random;
	
	public God() {
		random = new Random();
	}
	
	public abstract GodFaction getFaction();
	
	public abstract Class<?>[] getFighters();
	
	public final NPC getRandomFighter() {
		Class<?>[] fighters = getFighters();
		
		int randomIndex = random.nextInt(fighters.length);
		
		return createNewFighter(fighters[randomIndex]);
	}
	
	public final NPC createNewFighter(Class<?> fighterClass) {
		try {
			NPC npc = (NPC) fighterClass.newInstance();
			
			return npc;
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
