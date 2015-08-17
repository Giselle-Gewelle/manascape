package com.runescape.gameserver.world.entity.mob.npc.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.runescape.gameserver.world.GroundItem;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.player.Player;

public final class DropTable {

	public static final int MAX_DROPS = 15;

	private Map<Integer, Drop> constantDrops;
	private Map<Integer, Drop> chanceDrops;
	private HashMap<Integer, Drop> possibleDrops;
	private Random random;
	
	public DropTable() {
		constantDrops = new HashMap<Integer, Drop>();
		chanceDrops = new HashMap<Integer, Drop>();
		random = new Random();
	}
	
	public void dropRandomDrops(Player player, Location location) {
		Collection<Drop> drops = getRandomDrops();
		if(drops.size() > 0) {
			String name = (player == null ? null : player.getName());
			for(Drop drop : drops) {
				World.getInstance().createGroundItem(new GroundItem(name, drop.getItem(), location), player);
			}
		}
	}
	
	public Collection<Drop> getRandomDrops() {
		Collection<Drop> dropList = new LinkedList<Drop>();
		
		if(constantDrops.size() > 0) {
			for(int i = 0; i < constantDrops.size(); i++) {
				dropList.add(constantDrops.get(i));
			}
		}
		
		if(chanceDrops.size() > 0) {
			possibleDrops = new HashMap<Integer, Drop>(chanceDrops);
			int dropCount = random.nextInt(MAX_DROPS + 1);
			for(int i = 0; i < dropCount; i++) {
				Drop drop = getRandomDrop();
				if(drop != null) {
					dropList.add(drop);
				}
			}
		}
		
		return dropList;
	}
	
	public Drop getRandomDrop() {
		List<Integer> keys = new ArrayList<Integer>(possibleDrops.keySet());
		List<Drop> list = new ArrayList<Drop>(possibleDrops.values());
		
		int dropId = random.nextInt(list.size());
		Drop drop = list.get(dropId);
		
		int chance = drop.getRarity().getChance();
		int randChance = random.nextInt(chance + 1);
		if(randChance == chance) {
			possibleDrops.remove(keys.get(dropId));
			return drop;
		}
		
		return null;
	}
	
	public void add(Drop drop) {
		DropRarity rarity = drop.getRarity();
		if(rarity == DropRarity.ALWAYS) {
			constantDrops.put(constantDrops.size(), drop);
		} else {
			chanceDrops.put(chanceDrops.size(), drop);
		}
	}
	
	public void add(int itemId, int amount, DropRarity rarity) {
		if(rarity == DropRarity.ALWAYS) {
			constantDrops.put(constantDrops.size(), new Drop(itemId, amount, rarity));
		} else {
			chanceDrops.put(chanceDrops.size(), new Drop(itemId, amount, rarity));
		}
	}
	
	public void add(Item item, DropRarity rarity) {
		if(rarity == DropRarity.ALWAYS) {
			constantDrops.put(constantDrops.size(), new Drop(item, rarity));
		} else {
			chanceDrops.put(chanceDrops.size(), new Drop(item, rarity));
		}
	}
	
}
