package com.runescape.gameserver.world.content.skills.impl;

import java.util.HashMap;
import java.util.Map;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Prayer {

	private Mob mob;

	public Prayer(Mob mob) {
		this.mob = mob;
		for(Prayers p : Prayers.values())
			activePrayers.put(p, false);
	}

	private Map<Prayers, Boolean> activePrayers = new HashMap<>(Prayers.values().length);
	private long stopPrayerDelay;
	private double prayerPoint;
	private int headIcon = -1;

	public boolean handlePrayerDrain() {
		if(mob.isDead())
			return false;

		if(noPrayersOn())
			return false;

		double toRemove = 0.0D;
		for(final Prayers p : Prayers.values())
			if(isPrayerActive(p))
				toRemove += p.getDrainRate() / 10D;

		if(toRemove > 0.0D)
			toRemove /= 1 + 0.035; // TODO: Add prayer bonus, from equipment.

		prayerPoint -= toRemove;

		if(prayerPoint <= 0.0D) {
			prayerPoint = 1.0D + prayerPoint;
			reduceLevel();
		}
		return true;
	}

	public void reduceLevel() {
		mob.getSkills().decrementLevel(Skill.PRAYER);
		if(mob.getSkills().getLevel(Skill.PRAYER) <= 0) {
			if(mob instanceof Player) {
				((Player) mob).getPacketSender().sendMessage("You have run out of prayer points!");
			}
			resetPrayers();
		}
	}

	public void resetPrayers() {
		for(Prayers p : Prayers.values())
			deactivatePrayer(p);
		if(this.getHeadIcon() != -1)
			activateHeadIcon(-1);
	}

	public boolean noPrayersOn() {
		for(final Prayers p : Prayers.values())
			if(isPrayerActive(p))
				return false;
		return true;
	}

	public boolean handleActivate(Prayers prayer) {
		if(prayer == null || mob == null)
			return false;

		if(!isPrayerActive(prayer) && mob.getSkills().getLevel(Skill.PRAYER) <= 0) {
			disableGlow(prayer);
			return false;
		}

		if(mob.getSkills().getLevel(Skill.PRAYER) > 0) {
			if(hasLevelReq(prayer)) {
				switch(prayer) {
				default:
					break;
				case THICK_SKIN:
				case ROCK_SKIN:
				case STEEL_SKIN:
					checkPrayers(prayer, new int[] { 0 });
					break;

				case BURST_OF_STRENGTH:
				case SUPERHUMAN_STRENGTH:
				case ULTIMATE_STRENGTH:
					checkPrayers(prayer, new int[] { 1, 3, 4, 6 });
					break;

				case CLARITY_OF_THOUGHT:
				case IMPROVED_REFLEXES:
				case INCREDIBLE_REFLEXES:
					checkPrayers(prayer, new int[] { 2, 3, 4, 6 });
					break;

				case PROTECT_ITEM:
					break;

				case PROTECT_FROM_MAGIC:
				case PROTECT_FROM_MELEE:
				case PROTECT_FROM_RANGE:
					if(System.currentTimeMillis() - stopPrayerDelay < 5000L) {
						if(mob instanceof Player) {
							((Player) mob).getPacketSender().sendMessage("You have been injured and can't use this prayer!");
						}
						disableGlow(prayer);
						return false;
					}
					checkPrayers(prayer, new int[] { 5, 8 });
					break;

				case RETRIBUTION:
				case REDEMPTION:
				case SMITE:
					checkPrayers(prayer, new int[] { 5, 8, 9 });
					break;

				}

				int headIcon = prayer.getHeadIcon();

				if(!isPrayerActive(prayer)) {
					activePrayers.put(prayer, true);
					if(headIcon >= 0)
						activateHeadIcon(headIcon);
				} else {
					deactivatePrayer(prayer);
				}
			} else {
				if(mob instanceof Player) {
					((Player) mob).getPacketSender().sendString(357, "You need a <col=" + Constants.COLOUR_BLUE + ">Prayer level of " + prayer.getLevelReq() + "</col> to use <col=" + Constants.COLOUR_BLUE + ">" + prayer.getName() + "</col>.");
					((Player) mob).getPacketSender().sendChatBoxInterface(356);
					disableGlow(prayer);
				}
				// TODO Prayer fail sound effect
			}
		} else {
			if(mob instanceof Player) {
				disableGlow(prayer);
				((Player) mob).getPacketSender().sendMessage("You have run out of prayer points!");
			}
		}
		return true;
	}

	public boolean hasLevelReq(Prayers p) {
		return mob.getSkills().getRealLevel(Skill.PRAYER) >= p.getLevelReq();
	}

	public void activatePrayer(Prayers p) {
		activePrayers.put(p, true);
	}

	public void deactivatePrayer(Prayers p) {
		disableGlow(p);
		activePrayers.put(p, false);
		if(p.hasHeadIcon())
			activateHeadIcon(-1);
	}

	public void disableGlow(Prayers p) {
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendConfig(p.getGlowFrame(), 0);
		}
	}

	public void checkPrayers(Prayers prayer, int[] badTypes) {
		if(!isPrayerActive(prayer)) {
			for(final Prayers p : Prayers.values()) {
				for(int i = 0; i < badTypes.length; i++) {
					if(p.getPrayerType() == badTypes[i]) {
						if(isPrayerActive(p) && p.getButtonId() != prayer.getButtonId()) {
							deactivatePrayer(p);
						}
					}
				}
			}
		}
	}

	public int getHeadIcon() {
		return headIcon;
	}

	void activateHeadIcon(int newHeadIcon) {
		if(this.headIcon != newHeadIcon)
			this.headIcon = newHeadIcon;
		mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public boolean isPrayerActive(Prayers p) {
		return activePrayers.get(p);
	}

	public boolean hasMeleeProtection() {
		return isPrayerActive(Prayers.PROTECT_FROM_MELEE);
	}

	public boolean hasMagicProtection() {
		return isPrayerActive(Prayers.PROTECT_FROM_MAGIC);
	}

	public boolean hasRangedProtection() {
		return isPrayerActive(Prayers.PROTECT_FROM_RANGE);
	}

	public boolean hasProtectItem() {
		return isPrayerActive(Prayers.PROTECT_ITEM);
	}

	Skill prayer = Skill.PRAYER;

	Animation prayerAnim = Animation.create(645);

	public boolean usePrayerAltar() {
		int realLevel = mob.getSkills().getRealLevel(prayer);
		int currentLevel = mob.getSkills().getLevel(prayer);
		if(currentLevel == realLevel) {
			if(mob instanceof Player) {
				((Player) mob).getPacketSender().sendMessage("Your prayer points are already full charged.");
			}
			return false;
		}
		int increase = realLevel - currentLevel;
		mob.getSkills().setLevel(prayer, increase + currentLevel);
		mob.playAnimation(prayerAnim);
		if(mob instanceof Player) {
			((Player) mob).getPacketSender().sendMessage("Your prayer points have been renewed.");
		}
		return true;
	}

	public enum Prayers {

		THICK_SKIN("Thick Skin", 5609, 1, 83, .5, -1, 0), 
		BURST_OF_STRENGTH("Burst of Strength", 5610, 4, 84, .5, -1, 1), 
		CLARITY_OF_THOUGHT("Clarity of Thought", 5611, 7, 85, .5, -1, 2),
		
		ROCK_SKIN("Rock Skin", 5612, 10, 86, 1, -1, 0), 
		SUPERHUMAN_STRENGTH("Superhuman Strength", 5613, 13, 87, 1, -1, 1), 
		IMPROVED_REFLEXES("Improved Reflexes", 5614, 16, 88, 1, -1, 2),

		RAPID_RESTORE("Rapid Restore", 5615, 19, 89, 0.15, -1, 7), 
		RAPID_HEAL("Rapid Heal", 5616, 22, 90, 0.3, -1, 7), 
		PROTECT_ITEM("Protect Item", 5617, 25, 91, 0.3, -1, 7),

		STEEL_SKIN("Steel Skin", 5618, 28, 92, 2, -1, 0), 
		ULTIMATE_STRENGTH("Ultimate Strength", 5619, 31, 93, 2, -1, 1), 
		INCREDIBLE_REFLEXES("Incredible Reflexes", 5620, 34, 94, 2, -1, 2),

		PROTECT_FROM_MAGIC("Protect from Magic", 5621, 37, 95, 2, 2, 5), 
		PROTECT_FROM_RANGE("Protect from Range", 5622, 40, 96, 2, 1, 5), 
		PROTECT_FROM_MELEE("Protect from Melee", 5623, 43, 97, 2, 0, 5),

		RETRIBUTION("Retribution", 683, 46, 98, .5, 3, 8), 
		REDEMPTION("Redemption", 684, 49, 99, 1, 5, 8), 
		SMITE("Smite", 685, 52, 100, 2, 4, 8);

		private String name;
		private int buttonId, levelReq, glowFrame, headIcon, prayerType;
		private double drainRate;

		private Prayers(String name, int buttonId, int req, int glowFrame, double drainRate, int headIcon, int prayerType) {
			this.name = name;
			this.buttonId = buttonId;
			this.levelReq = req;
			this.glowFrame = glowFrame;
			this.drainRate = drainRate;
			this.headIcon = headIcon;
			this.prayerType = prayerType;
		}
		
		public String getName() {
			return name;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getGlowFrame() {
			return glowFrame;
		}

		public int getHeadIcon() {
			return headIcon;
		}

		public int getPrayerType() {
			return prayerType;
		}

		public double getDrainRate() {
			return drainRate;
		}

		public boolean hasHeadIcon() {
			return this.getHeadIcon() > 0;
		}

		public static Prayers forButtonId(int button) {
			for(Prayers p : values())
				if(p.getButtonId() == button)
					return p;
			return null;
		}
		
	}
}
