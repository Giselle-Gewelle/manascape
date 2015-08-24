package com.runescape.gameserver.world.entity.object;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.impl.*;

/**
 * Represents a single game object.
 * @author Graham Edgecombe
 *
 */
public class GameObject {

	/**
	 * The location.
	 */
	private Location location;
	
	/**
	 * The definition.
	 */
	private GameObjectDefinition definition;
	
	/**
	 * The type.
	 */
	private int type;
	
	/**
	 * The rotation.
	 */
	private int rotation;
	
	private boolean loadedInLandscape;
	
	private int health;
	
	/**
	 * Creates the game object.
	 * @param definition The definition.
	 * @param location The location.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GameObject(GameObjectDefinition definition, Location location, int type, int rotation, boolean loadedInLandscape) {
		this(definition, location, type, rotation, loadedInLandscape, -1);
	}
	
	public GameObject(GameObjectDefinition definition, Location location, int type, int rotation, boolean loadedInLandscape, int health) {
		this.definition = definition;
		this.location = location;
		this.type = type;
		this.rotation = rotation;
		this.loadedInLandscape = loadedInLandscape;
		this.health = health;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	/**
	 * Gets the location.
	 * @return The location.
	 */
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * Gets the definition.
	 * @return The definition.
	 */
	public GameObjectDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the type.
	 * @return The type.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Gets the rotation.
	 * @return The rotation.
	 */
	public int getRotation() {
		return rotation;
	}
	
	public boolean loadedInLandscape() {
		return loadedInLandscape;
	}
	
	public void setLoadedInLandscape(boolean loadedInLandscape) {
		this.loadedInLandscape = loadedInLandscape;
	}
	
	public boolean handleFirstClick(Player player) {
		return false;
	}
	
	public boolean handleSecondClick(Player player) {
		return false;
	}
	
	public boolean handleThirdClick(Player player) {
		return false;
	}

	public boolean handleItemOnUs(Player player, Item item) {
		return false;
	}
	
	public static GameObject getObjectInstance(GameObject o) {
		GameObjectDefinition def = o.getDefinition();
		Location loc = o.getLocation();
		int type = o.getType();
		int rotation = o.getRotation();
		int objectId = o.getDefinition().getId();
		String objectName = o.getDefinition().getName();
		if (objectName == null) {
			System.out.println("null object name for id: " + objectId);
			return null;
		}
		System.out.println("object name " + objectName);
		System.out.println("object loc " + loc.toString());
		System.out.println("object type " + type);
		System.out.println("object rotation " + rotation);
		System.out.println("object id " + objectId);
		
		if (objectName.contains("Altar"))
			return new Altar(def, loc, type, rotation, true);
		
		switch(objectId) {
		case 1530:
			return new Doors(def, loc, type, rotation, true);
		case 1902:
			return new Range(def, loc, type, rotation, true);
		case 11666:
			return new Furnace(def, loc, type, rotation, true);
		}
		return o;
	}
}
