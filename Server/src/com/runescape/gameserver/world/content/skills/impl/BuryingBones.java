package com.runescape.gameserver.world.content.skills.impl;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.miscellaneous.Misc;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class BuryingBones {
	
	private static enum BoneData {

		BONES(526, 4.5),
		WOLF_BONES(2859, 4.5),
		BURNT_BONES(528, 4.5),
		BAT_BONES(530, 5.3),
		MONKEY_BONES(3179, 5),
		BIG_BONES(532, 15),
		BABY_DRAGON_BONES(534, 30),
		WYVERN_BONES(6812, 50),
		DRAGON_BONES(536, 72),
		ZOGRE_BONES(4812, 82),
		FAYGR_BONES(4830, 84),
		RAURG_BONES(4832, 96),
		DAGANNOTH_BONES(6729, 125),
		OURG_BONES(4834, 140);

		private final Item bone;
		private final double expGained;

		private BoneData(final int bone, final double expGained) {
			this.bone = new Item(bone);
			this.expGained = expGained;
		}

		private Item getBone() {
			return bone;
		}

		private double getXP() {
			return expGained;
		}

		@SuppressWarnings("unused")
		private final String getMessage() {
			return Misc.optimizeText(toString().toLowerCase().replaceAll("_", " "));
		}

		private static BoneData getID(final int ID) {
			for (BoneData b : values()) {
				if (b.getBone().getId() == ID) {
					return b;
				}
			}
			return null;
		}

	}
	
	static Animation BURY = Animation.create(827);
	public static boolean bury(Player player, int id, int slot) {
		BoneData bone = BoneData.getID(id);
		if (player.getInventory().contains(bone.getBone().getId())) {
			if (System.currentTimeMillis() - player.buryDelay > 1500) {
				player.buryDelay = System.currentTimeMillis();
				player.getPacketSender().sendMessage("You dig a hole in the ground.");
				player.getInventory().remove(slot, bone.getBone());
				player.playAnimation(BURY);
				player.getSkills().addSkillXP(bone.getXP(), Skill.PRAYER);
				player.getPacketSender().sendMessage("You bury the bones.");
				return true;
			}
		}
		return false;
	}

}
