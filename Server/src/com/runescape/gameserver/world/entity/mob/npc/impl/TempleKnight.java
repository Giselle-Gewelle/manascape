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

public final class TempleKnight extends NPC {
	
	private Random random = new Random();
	
	public TempleKnight() {
		super();
		super.setDynamic(true);
		super.setAction(1, "Attack");
	}
	
	@Override
	public void setDropTable() {
		DropTable table = this.getDropTable();
		
		table.add(Drop.BONES);
		table.add(1059, 1, DropRarity.COMMON);
		table.add(1061, 1, DropRarity.COMMON);
		table.add(1297, 1, DropRarity.UNCOMMON);
		table.add(7987, 1, DropRarity.UNCOMMON);
		table.add(7985, 1, DropRarity.RARE);
		table.add(7986, 1, DropRarity.RARE);
	}
	
	@Override
	public void setSkills() {
		Skills skills = getSkills();
		
		skills.setCombatLevel(36);
		skills.setLevels(Skill.HITPOINTS, 52);
	}
	
	@Override
	public void setEquipment() {
		Container equipment = getEquipment();
		
		int[][][] sets = new int[][][] {
			{
				{ Equipment.SLOT_HELM, 5574 },
				{ Equipment.SLOT_CHEST, 5575 },
				{ Equipment.SLOT_BOTTOMS, 5576 }
			},
			{
				{ Equipment.SLOT_HELM, 7956 },
				{ Equipment.SLOT_CHEST, 7957 },
				{ Equipment.SLOT_BOTTOMS, 7958 }
			},
			{
				{ Equipment.SLOT_HELM, 7959 },
				{ Equipment.SLOT_CHEST, 7960 },
				{ Equipment.SLOT_BOTTOMS, 7961 }
			},
			{
				{ Equipment.SLOT_HELM, 7962 },
				{ Equipment.SLOT_CHEST, 7963 },
				{ Equipment.SLOT_BOTTOMS, 7964 }
			}
		};
		int[] shields = { 6631, 6633, -1 };
		
		int[] weapons = { 6601, 6587, 6605, 6607, 6611, 6613, 6589, 6609, 6599 };
		
		Random random = new Random();
		int randSet = random.nextInt(sets.length);
		int[][] equips = sets[randSet];
		int randWep = random.nextInt(weapons.length);
		int wepId = weapons[randWep];
		int randShield = random.nextInt(shields.length);
		int shield = shields[randShield];
		//wepId = 4151;
		for(int[] equip : equips) {
			equipment.set(equip[0], new Item(equip[1], 1));
		}
		equipment.set(Equipment.SLOT_WEAPON, new Item(wepId, 1));
		equipment.set(Equipment.SLOT_GLOVES, new Item(6629, 1));
		equipment.set(Equipment.SLOT_BOOTS, new Item(6619, 1));
		if(shield != -1) {
			equipment.set(Equipment.SLOT_SHIELD, new Item(shield, 1));
		}
		
		setEquippedWeapon(WeaponDefinition.forId(wepId));
		
		String[] types = {
			"Initiate", "Proselyte", "Acolyte", "Partisan"
		};
		String type = types[randSet];
		super.setName("Temple Knight " + type);
		super.setDescription("A Temple Knight in the service of Saradomin.");
	}
	
	@Override
	public int getAttackAnim() {
		WeaponDefinition wep = this.getEquippedWeapon();
		if(wep == null) {
			return 422;
		}
		
		int[] anims = wep.getAttackAnims();
		int randAnim = random.nextInt(anims.length);
		int animId = anims[randAnim];
		
		return animId;
	}
	
	@Override
	public int getDefendAnim() {
		if(getEquipment().isSlotUsed(Equipment.SLOT_SHIELD)) {
			return 403;
		}
		
		return 410;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		this.aggroTo(BlackKnight.class);
	}

}
