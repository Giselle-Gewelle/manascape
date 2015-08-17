package com.runescape.gameserver.world.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.runescape.gameserver.world.GroundItem;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;
import com.runescape.gameserver.world.region.Tile;

/**
 * Represents a single region.
 * @author Graham Edgecombe
 *
 */
public class Region {

	/**
	 * The region coordinates.
	 */
	private RegionCoordinates coordinate;
	
	/**
	 * A list of players in this region.
	 */
	private List<Player> players = new LinkedList<Player>();
	
	/**
	 * A list of NPCs in this region.
	 */
	private List<NPC> npcs = new LinkedList<NPC>();
	
	/**
	 * A list of objects in this region.
	 */
	private List<GameObject> objects = new LinkedList<GameObject>();
	
	/**
	 * A map of tiles in this region.
	 */
	private Map<Location, Tile> tiles = new HashMap<Location, Tile>();

	/**
	 * A list of ground items in this region.
	 */
	private List<GroundItem> groundItems = new ArrayList<GroundItem>();
	
	/**
	 * Creates a region.
	 * @param coordinate The coordinate.
	 */
	public Region(RegionCoordinates coordinate) {
		this.coordinate = coordinate;
	}
	
	/**
	 * Gets the region coordinates.
	 * @return The region coordinates.
	 */
	public RegionCoordinates getCoordinates() {
		return coordinate;
	}

	/**
	 * Gets the list of players.
	 * @return The list of players.
	 */
	public Collection<Player> getPlayers() {
		synchronized(this) {
			return Collections.unmodifiableCollection(new LinkedList<Player>(players));
		}
	}
	
	/**
	 * Gets the list of NPCs.
	 * @return The list of NPCs.
	 */
	public Collection<NPC> getNpcs() {
		synchronized(this) {
			return Collections.unmodifiableCollection(new LinkedList<NPC>(npcs));
		}
	}
	
	/**
	 * Gets the list of objects.
	 * @return The list of objects.
	 */
	public Collection<GameObject> getGameObjects() {
		return objects;
	}

	/**
	 * Adds a new player.
	 * @param player The player to add.
	 */
	public void addPlayer(Player player) {
		synchronized(this) {
			players.add(player);
		}
	}

	/**
	 * Removes an old player.
	 * @param player The player to remove.
	 */
	public void removePlayer(Player player) {
		synchronized(this) {
			players.remove(player);
		}
	}

	/**
	 * Adds a new NPC.
	 * @param npc The NPC to add.
	 */
	public void addNpc(NPC npc) {
		synchronized(this) {
			npcs.add(npc);
		}
	}

	/**
	 * Removes an old NPC.
	 * @param npc The NPC to remove.
	 */
	public void removeNpc(NPC npc) {
		synchronized(this) {
			npcs.remove(npc);
		}
	}

	/**
	 * Adds a new obj.
	 * @param obj The obj to add.
	 */
	public void addObject(GameObject obj) {
		objects.add(obj);
	}

	/**
	 * Removes an old obj.
	 * @param obj The obj to remove.
	 */
	public void removeObject(GameObject obj) {
		objects.remove(obj);
	}
	
	public GameObject getGameObject(Location location, int id) {
		for(Region r : getSurroundingRegions()) {
			for(GameObject obj : r.getGameObjects()) {
				if(obj.getLocation().equals(location) && obj.getDefinition().getId() == id) {
					return GameObject.getObjectInstance(obj);
				}
			}
		}
		return null;
	}
	
	public GameObject getGameObject(Location location) {
		for(Region r : getSurroundingRegions()) {
			for(GameObject obj : r.getGameObjects()) {
				if(obj.getLocation().equals(location)) {
					return GameObject.getObjectInstance(obj);
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets the regions surrounding a location.
	 * @return The regions surrounding the location.
	 */
	public Region[] getSurroundingRegions() {
		Region[] surrounding = new Region[9];
		surrounding[0] = this;
		surrounding[1] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY() - 1);
		surrounding[2] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY() + 1);
		surrounding[3] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY());
		surrounding[4] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX(), this.getCoordinates().getY() - 1);
		surrounding[5] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY());
		surrounding[6] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX(), this.getCoordinates().getY() + 1);
		surrounding[7] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY() + 1);
		surrounding[8] = World.getInstance().getRegionManager().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY() - 1);

		
		return surrounding;
	}
	
	public Tile getTile(Location location) {
		if (tiles.get(location) == null) {
			setTile(new Tile(), location);
		}
		return tiles.get(location);
	}
	
	public Map<Location, Tile> getTiles() {
		return tiles;
	}

	public void setTile(Tile tile, Location location) {
		tiles.put(location, tile);
	}
	
	public List<GroundItem> getGroundItems() {
		return groundItems;
	}

}
