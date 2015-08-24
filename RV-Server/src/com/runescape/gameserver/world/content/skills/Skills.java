package com.runescape.gameserver.world.content.skills;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.world.Graphic;
import com.runescape.gameserver.world.content.skills.impl.Prayer.Prayers;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Skills {

	public enum Skill {

		ATTACK(0, 18789), 
		DEFENCE(1, 18795), 
		STRENGTH(2, 18792), 
		HITPOINTS(3, 18790), 
		RANGED(4, 18798), 
		PRAYER(5, 18801), 
		MAGIC(6, 18804), 
		COOKING(7, 18800), 
		WOODCUTTING(8, 18806), 
		FLETCHING(9, 18805), 
		FISHING(10, 18797), 
		FIREMAKING(11, 18803), 
		CRAFTING(12, 18802), 
		SMITHING(13, 18794), 
		MINING(14, 18791), 
		HERBLORE(15, 18796), 
		AGILITY(16, 18793), 
		THIEVING(17, 18799), 
		SLAYER(18, 18808), 
		FARMING(19, 18809), 
		RUNECRAFTING(20, 18807);

		private int id;
		private int tooltipId;
		
		private Skill(int id, int tooltipId) {
			this.id = id;
			this.tooltipId = tooltipId;
		}

		public int getId() {
			return id;
		}
		
		public int getTooltipId() {
			return tooltipId;
		}

		public static int length() {
			return values().length;
		}

		public boolean isCombatSkill() {
			return getId() <= 6 || getId() == 23;
		}

		public static Skill[] getCombatSkills() {
			return new Skill[] { ATTACK, DEFENCE, STRENGTH, RANGED, PRAYER, MAGIC };
		}

		public static Skill forID(int id) {
			if(id < values().length) {
				return values()[id];
			} else {
				return null;
			}
		}

		public static Map<Integer, Integer> getXPForLevelTable() {
			return xpForLevelTable;
		}

		private static Map<Integer, Integer> xpForLevelTable = new HashMap<Integer, Integer>(99);

		public static void setXPForLevelTable() {
			for(int i = 1; i < 110; i++) {
				int xp = getXPForLevel(i);
				xpForLevelTable.put(i, xp);
			}
		}

		public static int getXPForLevel(int level) {
			int points = 0;
			int output = 0;
			for(int lvl = 1; lvl <= level; lvl++) {
				points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
				if(lvl >= level) {
					return output;
				}
				output = (int) Math.floor(points / 4);
			}
			return 0;
		}

	}

	public static final double MAXIMUM_EXP = 200000000;
	public static final int HEALTH_REGEN_RATE = 100;

	private Mob mob;

	private int combatLevel;

	private final Map<Skill, Integer> realLevels = new HashMap<>(Skill.length());
	private final Map<Skill, Integer> levels = new HashMap<>(Skill.length());
	private final Map<Skill, Double> exps = new HashMap<>(Skill.length());
	
	private long lastFireLight;
	private int lastHealthRegen;

	public Skills(Mob mob) {
		this.mob = mob;
		for(Skill s : Skill.values()) {
			if(!s.equals(Skill.HITPOINTS)) {
				realLevels.put(s, 1);
				levels.put(s, 1);
				exps.put(s, (double) 0);
			} else {
				realLevels.put(s, 10);
				levels.put(s, 10);
				exps.put(s, (double) 1184);
			}
		}
		combatLevel = 3;
		
		lastFireLight = -1;
		lastHealthRegen = HEALTH_REGEN_RATE;
	}
	
	public void handleHealthRegen() {
		if(lastHealthRegen > 0) {
			lastHealthRegen--;
			if(mob.getPrayer().isPrayerActive(Prayers.RAPID_HEAL)) {
				lastHealthRegen--;
			}
			return;
		}
		
		raiseLevel(Skill.HITPOINTS, 1);
		lastHealthRegen = HEALTH_REGEN_RATE;
	}
	
	public void updateTooltip(Skill s) {
		int tipId = s.getTooltipId();
		if(tipId == -1) {
			return;
		}
		
		int level = getLevel(s);
		int realLevel = getRealLevel(s);
		int exp = (int) getExperience(s);
		int nextLevelExp = -1;
		int remainder = -1;
		if(realLevel < 99) {
			nextLevelExp = Skill.getXPForLevel(realLevel + 1);
			remainder = nextLevelExp - exp;
		}
		
		String tip = "";
		DecimalFormat formatter = new DecimalFormat("#,###");
		
		String skillName = s.toString().toLowerCase();
		skillName = Character.toUpperCase(skillName.charAt(0)) + skillName.substring(1);
		
		tip = skillName + ": " + level + "/" + realLevel;
		tip += "\\n";
		tip += "Experience: " + formatter.format(exp);
		if(realLevel < 99) {
			tip += "\\n";
			tip += "Next Level: " + formatter.format(nextLevelExp);
			tip += "\\n";
			tip += "Remainder: " + formatter.format(remainder);
		}
		
		((Player) mob).getPacketSender().sendString(tipId, tip);
	}

	public void updateTooltips() {
		if(!(mob instanceof Player)) {
			return;
		}
		
		for(Skill s : Skill.values()) {
			updateTooltip(s);
		}
	}

	public int getRealLevel(int i) {
		Skill s = Skill.forID(i);
		return realLevels.get(s);
	}

	public int getRealLevel(Skill s) {
		return realLevels.get(s);
	}

	public int getLevel(int i) {
		Skill s = Skill.forID(i);
		return levels.get(s);
	}

	public int getLevel(Skill s) {
		return levels.get(s);
	}
	
	public long getLastFireLight() {
		return lastFireLight;
	}
	
	public void setLastFireLight(long l) {
		this.lastFireLight = l;
	}

	public int getTotalLevel() {
		int total = 0;
		for(Integer i : realLevels.values())
			total += i.intValue();
		return total;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setSkill(Skill skill, int level, int realLevel, double exp) {
		levels.put(skill, level);
		realLevels.put(skill, realLevel);
		exps.put(skill, exp);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}
	
	public void setLevels(Skill skill, int level) {
		levels.put(skill, level);
		realLevels.put(skill, level);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void setLevel(Skill skill, int level) {
		levels.put(skill, level);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void raiseLevel(Skill skill, int raiseAmount) {
		int oldValue = levels.get(skill);
		levels.put(skill, oldValue + raiseAmount);
		if(levels.get(skill) > realLevels.get(skill))
			levels.put(skill, realLevels.get(skill));
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void setExperience(Skill skill, double exp) {
		int oldLvl = getLevelForExperience(skill);
		exps.put(skill, exp);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
		int newLvl = getLevelForExperience(skill);
		if(oldLvl != newLvl) {
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	public void incrementLevel(Skill skill) {
		int increment = levels.get(skill) + 1;
		levels.put(skill, increment);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void decrementLevel(Skill skill) {
		int amount = levels.get(skill) - 1;
		levels.put(skill, amount);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void detractLevel(Skill skill, int decrement) {
		int oldVal = levels.get(skill);
		int newVal = oldVal - decrement;
		if(newVal < 0)
			newVal = 0;
		levels.put(skill, newVal);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public void normalizeLevel(Skill skill) {
		levels.put(skill, realLevels.get(skill));
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
	}

	public int getLevelForExperience(Skill skill) {
		int xp = exps.get(skill).intValue();
		if(xp >= Skill.getXPForLevelTable().get(99))
			return 99;
		for(int level = 1; level <= 99; level++) {
			int xp_needed = Skill.getXPForLevelTable().get(level);
			if(xp_needed == xp)
				return level;
			if(xp_needed > xp)
				return level - 1;
		}
		return 1;
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for(int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if(lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public double getExperience(Skill skill) {
		return exps.get(skill);
	}

	public void levelUp(Skill skill) {
		Interface levelInterface = Interface.forSkillId(skill.getId());
		int interfaceId = levelInterface.getInterfaceId();
		int string1 = levelInterface.getString1();
		int string2 = levelInterface.getString2();
		string1 = (string1 == 0 ? (interfaceId + 1) : string1);
		string2 = (string2 == 0 ? (interfaceId + 2) : string2);
		String skillName = skill.toString().toLowerCase();
		String a = "a";
		char c = skillName.charAt(0);
		if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
			a = "an";
		}

		if(mob instanceof Player) {
			Player player = (Player) mob;
			
			player.getPacketSender().sendString(string1, "<col=" + Constants.COLOUR_BLUE + ">Congratulations, you just advanced " + a + " " + skillName + " level!</col>");
			player.getPacketSender().sendString(string2, "Your " + skillName + " level is now " + realLevels.get(skill) + ".");
			if(skill.getId() == 19) {
				player.getPacketSender().sendInterfaceModel(13933, 250, 952);
			}
			player.getPacketSender().sendChatBoxInterface(interfaceId);
			player.getPacketSender().sendMessage("Congratulations, you've just advanced " + a + " " + skillName + " level!");
		}
	}

	public boolean addSkillXP(double amount, Skill skill) {

		final double xp = exps.get(skill);

		if(amount + xp < 0 || xp > 200000000) {
			return false;
		}
		final int oldLevel = getLevelForXP(skill);
		/*
		 * Increase our experience
		 */
		exps.put(skill, xp + amount);
		/*
		 * If our new xp gives us a greater level then the old one we level up
		 */
		int newLevel = getLevelForXP(skill);
		if(oldLevel < newLevel && newLevel < 100) {
			realLevels.put(skill, newLevel);
			/*
			 * Restores the skill to its real level as long as it's not prayer
			 * or summoning otherwise we just add 1 point
			 */
			if(levels.get(skill) < realLevels.get(skill)) {
				if(skill != Skill.HITPOINTS && skill != Skill.PRAYER)
					levels.put(skill, realLevels.get(skill));
				else
					levels.put(skill, levels.get(skill) + (newLevel - oldLevel));
			}
			levelUp(skill);
			mob.playGraphics(Graphic.LEVEL_UP);
			if(skill.isCombatSkill()) {
				final int combat = findCombatLevel();
				setCombatLevel(combat);
				mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
		}
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendSkill(skill);
			updateTooltip(skill);
		}
		return true;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
		mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public int findCombatLevel() {

		final int attack = getRealLevel(0);
		final int defence = getRealLevel(1);
		final int strength = getRealLevel(2);
		final int hitpoints = getRealLevel(3);
		final int prayer = getRealLevel(5);
		final int ranged = getRealLevel(4);
		final int magic = getRealLevel(6);

		double combatLevel = (defence + hitpoints + Math.floor(prayer / 2)) * 0.25;

		double warrior = (attack + strength) * 0.325;

		double ranger = ranged * 0.4875;

		double mage = magic * 0.4875;
		int realLevel = (int) (combatLevel + Math.max(warrior, Math.max(ranger, mage)));

		return realLevel;
	}

	private enum Interface {

		ATTACK(0, 6247), DEFENCE(1, 6253), STRENGTH(2, 6206), HITPOINTS(3, 6216), RANGED(4, 4443, 5453, 6114), PRAYER(5, 6242), MAGIC(6, 6211), COOKING(7, 6226), WOODCUTTING(8, 4272), FLETCHING(9,
				6231), FISHING(10, 6258), FIREMAKING(11, 4282), CRAFTING(12, 6263), SMITHING(13, 6221), MINING(14, 4416, 4417, 4438), HERBLORE(15, 6237), AGILITY(16, 4277), THIEVING(17, 4261, 4263,
				4264), SLAYER(18, 12122), FARMING(19, 13929), RUNECRAFTING(20, 4267);

		private static Map<Integer, Interface> levelInterfaces = new HashMap<Integer, Interface>();

		public static Interface forSkillId(int skill) {
			return levelInterfaces.get(skill);
		}

		static {
			for(Interface levelInterface : Interface.values()) {
				levelInterfaces.put(levelInterface.skillId, levelInterface);
			}
		}

		private int skillId;
		private int interfaceId;
		private int string1;
		private int string2;

		private Interface(int skillId, int interfaceId) {
			this(skillId, interfaceId, 0, 0);
		}

		private Interface(int skillId, int interfaceId, int string1, int string2) {
			this.skillId = skillId;
			this.interfaceId = interfaceId;
			this.string1 = string1;
			this.string2 = string2;
		}

		public int getInterfaceId() {
			return interfaceId;
		}

		public int getString1() {
			return string1;
		}

		public int getString2() {
			return string2;
		}

	}

	public int getLevelForXP(Skill skill) {
		int xp = exps.get(skill).intValue();
		if(xp >= Skill.getXPForLevelTable().get(99))
			return 99;
		for(int level = 1; level <= 99; level++) {
			int xp_needed = Skill.getXPForLevelTable().get(level);
			if(xp_needed == xp)
				return level;
			if(xp_needed > xp)
				return level - 1;
		}
		return 1;
	}

	public void setRealLevel(Skill skill, Integer newLevel, boolean refreshSkill) {
		if(newLevel > 99)
			newLevel = 99;
		if(levels.get(skill) == realLevels.get(skill)) {
			levels.put(skill, newLevel);
		}
		realLevels.put(skill, newLevel);
		exps.put(skill, (double) Skill.getXPForLevelTable().get(newLevel));
		if(refreshSkill) {
			final int combat = findCombatLevel();
			setCombatLevel(combat);
			if(mob instanceof Player) {
				((Player) mob).getPacketSender().sendSkill(skill);
				updateTooltip(skill);
			}
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}

	}

}
