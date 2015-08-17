package com.runescape.gameserver.world.definitions;

import java.util.*;
import java.io.*;

import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class ItemBonuses {
	
	static Map<Integer, ItemBonuses> itemBonusesMap = new HashMap<>();
	
	public static ItemBonuses getBonusesForId(int id) {
		return itemBonusesMap.get(id);
	}
	static String bonusesPath = "data/itemBonuses.txt";
	static BufferedReader read;
	public static boolean loadItemBonuses() {
		int[] bonuses = new int[13];
		String[] b;
		String line;
		int id;
		try {
			read = new BufferedReader(new FileReader(bonusesPath));
			while ((line = read.readLine()) != null) {
					id = Integer.valueOf(line);
					line = read.readLine();
					b = line.split(",");
					for (int i = 0; i < b.length; i++) {
						bonuses[i] = Integer.valueOf(b[i].trim());
					}
					ItemBonuses bonus = new ItemBonuses(bonuses[0], bonuses[1], bonuses[2], bonuses[3],
							bonuses[4], bonuses[5], bonuses[6], bonuses[7], bonuses[8], bonuses[9],
							bonuses[10], bonuses[11]);
					itemBonusesMap.put(id, bonus);
			}
			read.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ItemBonuses(int attackStab, int attackSlash, int attackCrush,
				int attackMagic, int attackRange, int defenceStab, int defenceSlash, int defenceCrush,
				int defenceMagic, int defenceRange, int strength, int prayer) {
		this.attackBonuses = new AttackBonuses(attackStab, attackSlash, attackCrush,
				attackMagic, attackRange);
		this.defenceBonuses = new DefenceBonuses(defenceStab, defenceSlash, defenceCrush,
				defenceMagic, defenceRange);
		this.otherBonuses = new OtherBonuses(strength, prayer);
	}

	private OtherBonuses otherBonuses;
	
	private AttackBonuses attackBonuses;
	
	private DefenceBonuses defenceBonuses;
	
	public void reset() {
		this.otherBonuses.reset();
		this.attackBonuses.reset();
		this.defenceBonuses.reset();
	}
	
	public AttackBonuses getAttackBonuses() {
		return attackBonuses;
	}
	
	public DefenceBonuses getDefenceBonuses() {
		return defenceBonuses;
	}
	
	public OtherBonuses getOtherBonuses() {
		return otherBonuses;
	}
	
	public class OtherBonuses {
		private int strength = 0, prayer = 0;
		
		public OtherBonuses(int strength, int prayer) {
			this.strength = strength;
			this.prayer = prayer;
		}
		
		public void updateBonuses(int strength, int prayer) {
			this.strength += strength;
			this.prayer += prayer;
		}
		
		public void reset() {
			this.strength = 0;
			this.prayer = 0;
		}

		public int getStrength() {
			return strength;
		}

		public int getPrayer() {
			return prayer;
		}
		
	}
	
	public class AttackBonuses {
		private int attackStab = 0, attackSlash = 0, attackCrush = 0, attackMagic = 0, attackRange = 0;
		
		public AttackBonuses(int attackStab, int attackSlash, int attackCrush,
				int attackMagic, int attackRange) {
			this.attackStab = attackStab;
			this.attackSlash = attackSlash;
			this.attackCrush = attackCrush;
			this.attackMagic = attackMagic;
			this.attackRange = attackRange;
		}
		
		public void updateBonuses(int attackStab, int attackSlash, int attackCrush,
				int attackMagic, int attackRange) {
			this.attackStab += attackStab;
			this.attackSlash += attackSlash;
			this.attackCrush += attackCrush;
			this.attackMagic += attackMagic;
			this.attackRange += attackRange;
		}
		
		public void reset() {
			this.attackStab = 0;
			this.attackSlash = 0;
			this.attackCrush = 0;
			this.attackMagic = 0;
			this.attackRange = 0;
		}

		public int getAttackStab() {
			return attackStab;
		}

		public int getAttackSlash() {
			return attackSlash;
		}

		public int getAttackCrush() {
			return attackCrush;
		}

		public int getAttackMagic() {
			return attackMagic;
		}

		public int getAttackRange() {
			return attackRange;
		}
		
	}
	
	public class DefenceBonuses {
		private int defenceStab = 0, defenceSlash = 0, defenceCrush = 0, defenceMagic = 0, defenceRange = 0;

		public DefenceBonuses(int defenceStab, int defenceSlash, int defenceCrush,
				int defenceMagic, int defenceRange) {
			this.defenceStab = defenceStab;
			this.defenceSlash = defenceSlash;
			this.defenceCrush = defenceCrush;
			this.defenceMagic = defenceMagic;
			this.defenceRange = defenceRange;
		}
		
		public void updateBonuses(int defenceStab, int defenceSlash, int defenceCrush,
				int defenceMagic, int defenceRange) {
			this.defenceStab += defenceStab;
			this.defenceSlash += defenceSlash;
			this.defenceCrush += defenceCrush;
			this.defenceMagic += defenceMagic;
			this.defenceRange += defenceRange;
		}
		
		public void reset() {
			this.defenceStab = 0;
			this.defenceSlash = 0;
			this.defenceCrush = 0;
			this.defenceMagic = 0;
			this.defenceRange = 0;
		}

		public int getDefenceStab() {
			return defenceStab;
		}

		public int getDefenceSlash() {
			return defenceSlash;
		}

		public int getDefenceCrush() {
			return defenceCrush;
		}

		public int getDefenceMagic() {
			return defenceMagic;
		}

		public int getDefenceRange() {
			return defenceRange;
		}
		
	}
	
	public enum BonusString {
		A_STAB("Stab", 18957), A_SLASH("Slash", 18958), A_CRUSH("Crush", 18959), A_MAGIC("Magic", 18960), A_RANGE("Range", 18961),
		D_STAB("Stab", 18962), D_SLASH("Slash", 18963), D_CRUSH("Crush", 18964), D_MAGIC("Magic", 18965), D_RANGE("Range", 18966),
		STRENGTH("Strength", 18967), PRAYER("Prayer", 18968);
		
		private int stringId;
		
		private String string;
		
		BonusString(String s, int stringId) {
			this.string = s;
			this.stringId = stringId;
		}
		
		public int getStringId() {
			return stringId;
		}
		
		public String getString() {
			return string + ": ";
		}
		
		public static void resetBonuses(Player player) {
			for (BonusString s : values()) {
				player.getPacketSender().sendString(s.getStringId(), s.getString() + 0);
			}
		}
	}
	
	static void sendBonuses(Player player) {
		sendBonus(player, player.getPlayerBonuses().getAttackBonuses().getAttackCrush(), BonusString.A_CRUSH);
		sendBonus(player, player.getPlayerBonuses().getAttackBonuses().getAttackStab(), BonusString.A_STAB);
		sendBonus(player, player.getPlayerBonuses().getAttackBonuses().getAttackSlash(), BonusString.A_SLASH);
		sendBonus(player, player.getPlayerBonuses().getAttackBonuses().getAttackRange(), BonusString.A_RANGE);
		sendBonus(player, player.getPlayerBonuses().getAttackBonuses().getAttackMagic(), BonusString.A_MAGIC);
		
		sendBonus(player, player.getPlayerBonuses().getDefenceBonuses().getDefenceCrush(), BonusString.D_CRUSH);
		sendBonus(player, player.getPlayerBonuses().getDefenceBonuses().getDefenceStab(), BonusString.D_STAB);
		sendBonus(player, player.getPlayerBonuses().getDefenceBonuses().getDefenceSlash(), BonusString.D_SLASH);
		sendBonus(player, player.getPlayerBonuses().getDefenceBonuses().getDefenceRange(), BonusString.D_RANGE);
		sendBonus(player, player.getPlayerBonuses().getDefenceBonuses().getDefenceMagic(), BonusString.D_MAGIC);
		
		sendBonus(player, player.getPlayerBonuses().getOtherBonuses().getStrength(), BonusString.STRENGTH);
		sendBonus(player, player.getPlayerBonuses().getOtherBonuses().getPrayer(), BonusString.PRAYER);
	}
	
	static void sendBonus(Player player, int bonus, BonusString id) {
		boolean neg = String.valueOf(bonus).contains("-");
		player.getPacketSender().sendString(id.getStringId(), id.getString() + (neg ? "" : "+") + bonus);
	}
	
	static void addToBonuses(Player player, int item) {
		ItemBonuses b;
		try {
			b = EquipmentDefinition.forItemId(item).getEquipmentBonuses();
		} catch(Exception e) {
			b = ItemBonuses.getBonusesForId(item);
		}
		if (b == null)
			return;
		player.getPlayerBonuses().getDefenceBonuses().updateBonuses(b.getDefenceBonuses().getDefenceStab(), b.getDefenceBonuses().getDefenceStab()
				, b.getDefenceBonuses().getDefenceCrush(), b.getDefenceBonuses().getDefenceMagic(), b.getDefenceBonuses().getDefenceRange());
		player.getPlayerBonuses().getAttackBonuses().updateBonuses(b.getAttackBonuses().getAttackStab(), b.getAttackBonuses().getAttackStab()
				, b.getAttackBonuses().getAttackCrush(), b.getAttackBonuses().getAttackMagic(), b.getAttackBonuses().getAttackRange());
		player.getPlayerBonuses().getOtherBonuses().updateBonuses(b.getOtherBonuses().getStrength(), b.getOtherBonuses().getPrayer());
	}
	
	static int[] slots = {Equipment.SLOT_AMULET, Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CAPE,
			Equipment.SLOT_CHEST, Equipment.SLOT_GLOVES, Equipment.SLOT_HELM, Equipment.SLOT_RING, Equipment.SLOT_SHIELD, Equipment.SLOT_WEAPON};
	
	public static void updateBonuses(Player player, Container con) {
		player.getPlayerBonuses().reset();
		for (int i : slots) {
			if (con.isSlotUsed(i))
				addToBonuses(player, con.get(i).getId());
		}
		sendBonuses(player);
	}

}
