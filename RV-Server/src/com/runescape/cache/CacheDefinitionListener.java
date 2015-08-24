package com.runescape.cache;

import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;

/**
 * An object definition listener, which is notified when object definitions
 * have been parsed.
 * @author Graham Edgecombe
 *
 */
public interface CacheDefinitionListener {
	
	/**
	 * Called when an object definition is parsed.
	 * @param def The definition that was parsed.
	 */
	public void objectDefinitionParsed(GameObjectDefinition def);

	public void npcDefinitionParsed(CacheNPCDefinition def);

}
