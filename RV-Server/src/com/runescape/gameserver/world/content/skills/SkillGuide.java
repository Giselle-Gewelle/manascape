package com.runescape.gameserver.world.content.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.content.skills.guides.*;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.PacketSender;

public final class SkillGuide {

	public static final int 
		STRING_TITLE = 8716, 
		STRING_MEMBERS_ONLY = 8849, 
		INTERFACE_ID = 8714,
		INTERFACE_ITEMS = 8847;

	public static final int[] tabTextIds = new int[] { 8846, 8823, 8824, 8827, 8837, 8840, 8843, 8859, 8862, 8865, 15303, 15306, 15309 };
	public static final int[] tabIds = new int[] { 8800, 8844, 8813, 8825, 8828, 8838, 8841, 8850, 8860, 8863, 15294, 15304, 15307 };
	
	private static HashMap<Integer, Integer> tabList = new HashMap<Integer, Integer>();
	
	static {
		int index = 0;
		for(int tabId : tabTextIds) {
			tabList.put(tabId, index++);
		}
	}
	
	public static int getTabIndex(int tabId) {
		if(!tabList.containsKey(tabId)) {
			return -1;
		}
		return tabList.get(tabId);
	}

	private Player player;
	private PacketSender packetSender;
	private SkillInfo skill;
	private List<Item> items;

	public SkillGuide(Player player, SkillInfo skill) {
		this.player = player;
		this.packetSender = player.getPacketSender();
		this.skill = skill;
		items = new ArrayList<Item>();
		show();
	}
	
	private void sendMenuLine(int level, String text, int itemId, int lineId) {
		packetSender.sendString(8760 + lineId, text);
		packetSender.sendString(8720 + lineId, String.valueOf(level));
		items.add(new Item(itemId, 1));
	}
	
	public void openTab(int id) {
		clearPage();

		Guide guide = skill.getGuide();
		String[] colours = null;
		
		guide.setContent(id);
		int[] levels = guide.getLevels();
		int[] itemIds = guide.getItemIds();
		String[] names = guide.getNames();
		
		colours = new String[levels.length];
		for(int c = 0; c < colours.length; c++) {
			if(player.getSkills().getRealLevel(Skill.valueOf(skill.name())) >= levels[c]) {
				colours[c] = Constants.COLOUR_BLUE;
			} else {
				colours[c] = "800000";
			}
		}
		
		for(int i = 0; i < names.length; i++) {
			int level = levels[0];
			String colour = colours[0];
			if(levels.length > 1) {
				level = levels[i];
				colour = colours[i];
			}
			sendMenuLine(level, "<col=" + colour + ">" + names[i] + "</col>", itemIds[i], i);
		}
		
		Item[] array = new Item[items.size()];
		items.toArray(array);
		packetSender.sendUpdateItems(INTERFACE_ITEMS, array);
		
		items.clear();
	}

	public void show() {
		String skillName = skill.toString().toLowerCase();
		skillName = Character.toUpperCase(skillName.charAt(0)) + skillName.substring(1);

		packetSender.sendString(STRING_TITLE, skillName + " Guide");
		packetSender.sendString(STRING_MEMBERS_ONLY, "");

		int index = 0;
		String[] options = SkillInfo.valueOf(skill.name()).getOptions();
		for(String option : options) {
			packetSender.sendInterfaceConfig(tabIds[index], false);
			packetSender.sendString(tabTextIds[index], option);
			index++;
		}
		
		if(index < tabIds.length) {
			for(int i = index; i < tabIds.length; i++) {
				packetSender.sendInterfaceConfig(tabIds[i], true);
				packetSender.sendString(tabTextIds[i], "");
			}
		}
		
		openTab(0);
		
		packetSender.sendInterface(INTERFACE_ID);
	}
	
	public void clearPage() {
		for(int i = 0; i < 40; i++) {
			packetSender.sendString(8760 + i, "");
			packetSender.sendString(8720 + i, "");
		}
	}

	public enum SkillInfo {
		ATTACK(
			8654, 
			new String[] { 
				"Bronze", "Iron", "Steel", "Black", "White", "Mithril", "Adamant", "Rune", "Dragon", "Barrows", "Special", "Milestones" 
			},
			new AttackGuide()
		),
		STRENGTH(
			8657,
			new String[] {
				"Weaponry", "Armour", "Milestones"
			},
			new StrengthGuide()
		), 
		DEFENCE(
			8660,
			new String[] {
				"Bronze", "Iron", "Steel", "Black", "White", "Mithril", "Adamant", "Rune", "Dragon", "Barrows", "Magic", "Ranged", "Milestones" 
			},
			new DefenceGuide()
		),
		RANGED(
			8663,
			new String[] {
				"Bows", "Thrown", "Armour", "Milestones"
			}, 
			null
		);
		
		private static HashMap<Integer, SkillInfo> menus = new HashMap<Integer, SkillInfo>();
		
		static {
			for(SkillInfo menu : SkillInfo.values()) {
				menus.put(menu.getButtonId(), menu);
			}
		}
		
		public static SkillInfo forButtonId(int buttonId) {
			if(!menus.containsKey(buttonId)) {
				return null;
			}
			
			return menus.get(buttonId);
		}

		private int buttonId;
		private String[] options;
		private Guide guide;

		private SkillInfo(int buttonId, String[] options, Guide guide) {
			this.buttonId = buttonId;
			this.options = options;
			this.guide = guide;
		}
		
		public int getButtonId() {
			return buttonId;
		}

		public String[] getOptions() {
			return options;
		}
		
		public Guide getGuide() {
			return guide;
		}
	}

}
