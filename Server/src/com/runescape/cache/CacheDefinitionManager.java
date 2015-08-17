package com.runescape.cache;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.runescape.cache.index.impl.MapIndex;
import com.runescape.cache.index.impl.StandardIndex;
import com.runescape.cache.map.LandscapeListener;
import com.runescape.cache.map.LandscapeParser;
import com.runescape.cache.npc.NPCDefinitionParser;
import com.runescape.cache.obj.ObjectDefinitionParser;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.object.GameObject;

/**
 * Manages all of the in-game objects.
 * @author Graham Edgecombe
 *
 */
public class CacheDefinitionManager implements LandscapeListener, CacheDefinitionListener {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(CacheDefinitionManager.class.getName());
	
	/**
	 * The number of definitions loaded.
	 */
	private int definitionCount = 0;
	
	/**
	 * The count of objects loaded.
	 */
	private int objectCount = 0;
	
	private int npcCount = 0;
	
	/**
	 * Loads the objects in the map.
	 * @throws IOException if an I/O error occurs.
	 * @throws InvalidCacheException if the cache is invalid.
	 */
	public void load() throws IOException, InvalidCacheException {
		Cache cache = new Cache(new File("./data/cache/"));
		try {
			logger.info("Loading object definitions...");
			StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
			new ObjectDefinitionParser(cache, defIndices, this).parse();
			logger.info("Loaded " + definitionCount + " object definitions.");
			
			logger.info("Loading NPC definitions...");
			StandardIndex[] npcIndices = cache.getIndexTable().getNPCDefinitionIndices();
			new NPCDefinitionParser(cache, npcIndices, this).parse();
			logger.info("Loaded " + npcCount + " cache NPC definitions.");
			
			logger.info("Loading map...");
			MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
			for(MapIndex index : mapIndices) {
				new LandscapeParser(cache, index.getIdentifier(), this).parse();
			}
			logger.info("Loaded " + objectCount + " objects.");
		} finally {
			cache.close();
		}
	}

	@Override
	public void objectParsed(GameObject obj) {
		objectCount++;
		World.getInstance().getRegionManager().getRegionByLocation(obj.getLocation()).getGameObjects().add(obj);
	}

	@Override
	public void objectDefinitionParsed(GameObjectDefinition def) {
		definitionCount++;
		GameObjectDefinition.addDefinition(def);
	}

	@Override
	public void npcDefinitionParsed(CacheNPCDefinition def) {
		npcCount++;
		CacheNPCDefinition.addDefinition(def);
	}

}
