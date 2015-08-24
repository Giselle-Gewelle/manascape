package com.runescape.gameserver.event.impl;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.region.Region;

public class DeathEvent extends Event {
	
	private Mob entity;
	private Mob killer;
	
	private int cycle = 0;
	private int respawn = 0;

	public DeathEvent(Mob entity) {
		super(0);
		this.entity = entity;
	}
	
	public DeathEvent(Mob entity, Mob killer) {
		super(0);
		this.entity = entity;
		this.killer = killer;
	}

	@Override
	public void execute() {
		if(cycle == 0) {
			// KILL!
			
			entity.setDead(true);
			
			if(entity.isPlayer()) {
				Player player = (Player) entity;
				player.getPacketSender().sendMessage("Oh dear, you are dead!");
			}
			entity.getActionQueue().clearAllActions();
			entity.getWalkingQueue().reset();
			entity.resetInteractingEntity();
			entity.playAnimation(Animation.HUMAN_DEATH);
			entity.setVisible(false);
			setDelay(3000);
			cycle++;
		} else if(cycle == 1) {
			// Make the body disappear and stuff.
			
			if(entity instanceof NPC) {
				NPC npc = (NPC) entity;
				npc.kill(killer);
			}
			cycle++;
		} else {
			// REVIVE! :D
			
			if(entity.isPlayer()) {
				this.stop();
				entity.setTeleportTarget(Mob.DEFAULT_LOCATION);
				Player player = (Player) entity;
				player.getSkills().setLevel(Skill.HITPOINTS, player.getSkills().getLevelForExperience(Skill.HITPOINTS));
				entity.setDead(false);
				entity.playAnimation(Animation.RESET);
			} else if(entity instanceof NPC) {
				// TODO NPC combat definitions, get the NPC's respawn timer from them.
				if(respawn >= 1) {
					this.stop();
					NPC npc = (NPC) entity;
					if(npc.getNPCSpawn() != null) {
						npc.setTeleportTarget(npc.getNPCSpawn().getSpawnLocation());
						Region region = World.getInstance().getRegionManager().getRegionByLocation(npc.getNPCSpawn().getSpawnLocation());
						entity.setDead(false);
						entity.playAnimation(Animation.RESET);
						entity.setVisible(true);
						entity.addToRegion(region);
						
						Location walkTo = npc.getDefaultWalkTo();
						if(walkTo != null) {
							npc.getWalkingQueue().walkTo(walkTo.getX(), walkTo.getY());
						}
					} else {
						World.getInstance().unregister(npc);
					}
				} else {
					respawn++;
				}
			} else {
				this.stop();
			}
		}
	}

}