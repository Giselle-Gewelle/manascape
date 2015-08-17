package com.runescape.util;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.definitions.ItemBonuses;
import com.runescape.gameserver.world.definitions.ItemDefinition;

import java.io.*;
import java.util.*;

public class TesterClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ItemBonuses.loadItemBonuses();
		
		
	}
	
	static Item item;
	static BufferedWriter write;
	public static void dump() throws Exception {
		write = new BufferedWriter(new FileWriter("./dump.txt"));
		List<Integer> numbers = new LinkedList<>();
		for (int i = 0; i < 7955; i++) {
			item = new Item(i);
			if (item == null)
				continue;
			ItemDefinition def = item.getDefinition();
			if (def == null)
				continue;
			if (item.getId() == def.getNotedId())
				continue;
			if (def.getName().contains("dagger")) {
				numbers.add(item.getId());
				write.write("        {");
				write.newLine();
				write.write("                \"id\": " + item.getId() + ",");
				write.newLine();
				write.write("                \"name\": " + "\"" + def.getName() + "\",");
				write.newLine();
				write.write("                \"predefined\":" + "\"DAGGER\"");
				write.newLine();
				write.write("        },");
				write.newLine();
			}
		}
		write.close();
	}
	
}
