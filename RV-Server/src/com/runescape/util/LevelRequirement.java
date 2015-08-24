package com.runescape.util;

import com.runescape.gameserver.world.content.miscellaneous.Misc;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class LevelRequirement {
	private int levelReq;
	private Skill skill;
	
	public LevelRequirement(int levelReq, Skill skill) {
		this.levelReq = levelReq;
		this.skill = skill;
	}
	
	public boolean playerHasRequirement(Player player) {
		return player.getSkills().getRealLevel(skill) >= levelReq;
	}
	
	public String getUnmetEquipmentRequirementMessage() {
		return "You need a " + Misc.optimizeText(this.skill.toString().toLowerCase()) + " level of " + this.levelReq + " to wield this item.";
	}
	
	public String getUnmetRequirementMessage() {
		return "You need a " + Misc.optimizeText(this.skill.toString().toLowerCase()) + " level of " + this.levelReq + " to do this.";
	}
	
	public String getUnmetSpellRequirementMessage() {
		return "You need a Magic level of " + this.levelReq + " to use this spell.";
	}

	public int getLevelReq() {
		return levelReq;
	}

	public Skill getSkill() {
		return skill;
	}
}
