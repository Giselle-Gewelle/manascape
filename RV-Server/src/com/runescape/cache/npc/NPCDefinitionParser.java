package com.runescape.cache.npc;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.runescape.cache.Archive;
import com.runescape.cache.Cache;
import com.runescape.cache.CacheDefinitionListener;
import com.runescape.cache.index.impl.StandardIndex;
import com.runescape.cache.util.ByteBufferUtils;
import com.runescape.gameserver.world.definitions.CacheNPCDefinition;

/**
 * Parses NPC definitions from the cache.
 * @author Aizen Sousuke
 */
public class NPCDefinitionParser {
	
	private Cache cache;
	private StandardIndex[] indices;
	private CacheDefinitionListener listener;
	
	public NPCDefinitionParser(Cache cache, StandardIndex[] indices, CacheDefinitionListener listener) {
		this.cache = cache;
		this.indices = indices;
		this.listener = listener;
	}
	
	public void parse() throws IOException {
		ByteBuffer buf = new Archive(cache.getFile(0, 2)).getFileAsByteBuffer("npc.dat");
		
		for(StandardIndex index : indices) {
			int id = index.getIdentifier();
			int offset = index.getFile(); // bad naming, should be getOffset()
			buf.position(offset);
			
			String name = "null";
			String desc = "null";
			int combatLevel = 0;
			
			do {
				int configCode = buf.get() & 0xFF;
				if(configCode == 0) {
					break;
				}
				if(configCode == 1) {
					int someCounter = buf.get() & 0xFF;
					for(int i = 0; i < someCounter; i++) {
						buf.getShort();
					}
				} else if(configCode == 2) {
					name = ByteBufferUtils.getString(buf);
				} else if(configCode == 3) {
					desc = ByteBufferUtils.getString(buf);
				} else if(configCode == 12) {
					buf.get();
				} else if(configCode == 13) {
					buf.getShort();
				} else if(configCode == 14)
		            buf.getShort();
		        else if(configCode == 17) {
	                buf.getShort();
	                buf.getShort();
	                buf.getShort();
	                buf.getShort();
	            } else if(configCode >= 30 && configCode < 40) {
	            	ByteBufferUtils.getString(buf);
	            } else if(configCode == 40) {
	                int k = buf.get() & 0xFF;
	                for(int k1 = 0; k1 < k; k1++) {
	                    buf.getShort();
	                    buf.getShort();
	                }

	            } else if(configCode == 60) {
	                int l = buf.get() & 0xFF;
	                for(int l1 = 0; l1 < l; l1++)
	                    buf.getShort();

	            } else if(configCode == 90)
	                buf.getShort();
	            else if(configCode == 91)
	                buf.getShort();
	            else if(configCode == 92)
	                buf.getShort();
	            else if(configCode == 93) {
	                //bool
	            } else if(configCode == 95)
	                combatLevel = buf.getShort();
	            else if(configCode == 97)
	                buf.getShort();
	            else if(configCode == 98)
	                buf.getShort();
	            else if(configCode == 99) {
	                //bool
	            } else if(configCode == 100)
	                buf.get();
	            else if(configCode == 101)
	                buf.get()/* * 5*/;
	            else if(configCode == 102)
	                buf.getShort();
	            else if(configCode == 103)
	                buf.getShort();
	            else if(configCode == 106) {
	                buf.getShort();
	                buf.getShort();
	                int i1 = buf.get() & 0xFF;
	                for(int i2 = 0; i2 <= i1; i2++) {
	                    buf.getShort();
	                }

	            } else if(configCode == 107) {
	                //bool
	            }
				//System.out.println("id = " + id + ", name = " + name + ", desc = " + desc);
			} while(true);
			
			listener.npcDefinitionParsed(new CacheNPCDefinition(id, name, desc, combatLevel));
		}
	}
	
}
