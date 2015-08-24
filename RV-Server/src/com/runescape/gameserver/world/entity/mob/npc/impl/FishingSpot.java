package com.runescape.gameserver.world.entity.mob.npc.impl;

import java.util.Random;

import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.content.skills.impl.Fishing;
import com.runescape.gameserver.world.content.skills.impl.Fishing.SpotType;
import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class FishingSpot extends NPC {

	private int spotId;
	private Random random;
	
	public FishingSpot(CacheNPCDefinition def) {
		super(def);
		spotId = def.getId();
	}
	
	private int getRandomHealth() {
		if(random == null) {
			random = new Random();
		}
		
		int health = random.nextInt(50);
		if(health < 5) {
			health = 5;
		}
		
		return health;
	}
	
	@Override
	public void resetHealth() {
		getSkills().setLevels(Skill.HITPOINTS, getRandomHealth());
	}
	
	@Override
	public void setSkills() {
		getSkills().setLevels(Skill.HITPOINTS, getRandomHealth());
	}
	
	@Override
	public void tick() {
		validateLife();
	}
	
	@Override
	public boolean handleSecondClick(Player player) {
		SpotType type = SpotType.getSpot(spotId, 2);
		if(type != null) {
			player.getActionQueue().addAction(new CoordinateAction(
				player, this.getLocation(), 1, new Fishing(player, this, type)
			));
		}
		
		return true;
	}
	
	@Override
	public boolean handleThirdClick(Player player) {
		SpotType type = SpotType.getSpot(spotId, 2);
		if(type != null) {
			
		}
		
		return true;
	}

}
