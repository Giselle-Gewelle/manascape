package com.runescape.gameserver.world.content.minigames;

import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class PestControl {
	
	private static int receivedXp(Player player, Skill skills) {
		int divisor = 6;
		if (skills == Skill.PRAYER) {
			divisor = 12;
		}
		return (int) (Math.pow(player.getSkills().getLevel(skills), 2) / divisor);
	}
	
	public static void sendPestControlRewardsInterface(Player player) {
		player.getPacketSender().sendString(18767, "Attack - " + receivedXp(player, Skill.ATTACK) + " xp");		
		player.getPacketSender().sendString(18768, "Strength - " + receivedXp(player, Skill.STRENGTH) + " xp");
		player.getPacketSender().sendString(18769, "Defence - " + receivedXp(player, Skill.DEFENCE) + " xp");
		player.getPacketSender().sendString(18770, "Ranged - " + receivedXp(player, Skill.RANGED) + " xp");
		player.getPacketSender().sendString(18771, "Magic - " + receivedXp(player, Skill.MAGIC) + " xp");
		player.getPacketSender().sendString(18772, "Hitpoints - " + receivedXp(player, Skill.HITPOINTS) + " xp");
		player.getPacketSender().sendString(18773, "Prayer - " + receivedXp(player, Skill.PRAYER) + " xp");
		player.getPacketSender().sendInterface(18691);
	}

}
