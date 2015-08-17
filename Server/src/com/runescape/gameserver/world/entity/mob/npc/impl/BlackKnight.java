package com.runescape.gameserver.world.entity.mob.npc.impl;

import java.util.Random;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.content.skills.Skills;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.WeaponDefinition;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.npc.drops.Drop;
import com.runescape.gameserver.world.entity.mob.npc.drops.DropRarity;
import com.runescape.gameserver.world.entity.mob.npc.drops.DropTable;

public final class BlackKnight extends NPC {

	private Random random = new Random();

	public BlackKnight() {
		super();
		super.setDynamic(true);
		super.setAction(1, "Attack");
		super.setName("Black Knight");
		super.setDescription("An evil knight follower of Zamorak.");
	}
	
	@Override
	public void setDropTable() {
		DropTable table = this.getDropTable();
		
		table.add(Drop.BONES);
		table.add(6619, 1, DropRarity.COMMON);
		table.add(6629, 1, DropRarity.COMMON);
		table.add(6609, 1, DropRarity.UNCOMMON);
		table.add(6623, 1, DropRarity.UNCOMMON);
		table.add(6617, 1, DropRarity.RARE);
		table.add(6625, 1, DropRarity.RARE);
	}
	
	@Override
	public void setSkills() {
		Skills skills = getSkills();
		
		skills.setCombatLevel(33);
		skills.setLevels(Skill.HITPOINTS, 42);
	}
	
	@Override
	public void setEquipment() {
		Container equipment = getEquipment();
		
		int[][] equips = new int[][] {
			// Black longsword
			{ Equipment.SLOT_WEAPON, 1297 },
			// Black Platebody
			{ Equipment.SLOT_CHEST, 1125 },
			// Black Full Helm
			{ Equipment.SLOT_HELM, 1165 },
			// Black Platelegs
			{ Equipment.SLOT_BOTTOMS, 1077 }
		};
		
		for(int[] equip : equips) {
			equipment.set(equip[0], new Item(equip[1], 1));
		}
		
		setEquippedWeapon(WeaponDefinition.forId(1297));
	}
	
	@Override
	public int getAttackAnim() {
		return (random.nextInt(2) == 1 ? 412 : 451);
	}
	
	@Override
	public int getDefendAnim() {
		return 410;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		this.aggroTo(TempleKnight.class);
	}

}
