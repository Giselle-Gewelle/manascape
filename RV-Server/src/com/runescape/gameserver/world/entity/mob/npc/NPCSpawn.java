package com.runescape.gameserver.world.entity.mob.npc;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;


/**
 * Handles NPC spawns.
 * @author Aizen Sousuke
 */
public class NPCSpawn {

	private static final Logger logger = Logger.getLogger(NPCSpawn.class.getName());
	
	private static NPCSpawn[] spawns;
	
	public static NPCSpawn forId(int id) {
		return spawns[id];
	}
	
	public static void init() throws IOException, ParseException {
		
		if(spawns != null) {
			throw new IllegalStateException("NPC spawns already loaded.");
		}
		
		logger.info("Loading NPC spawns...");
		
		final JSONParser parser = new JSONParser();
		final JSONArray json = (JSONArray) parser.parse(new FileReader("data/npcspawns.json"));
		
		List<NPCSpawn> spawnList = new ArrayList<NPCSpawn>();

		for(Object o : json) {
			
			JSONObject spawn = (JSONObject) o;
			
			int npcId = ((Long) spawn.get("npcId")).intValue();
			boolean canWalk = true;
			int face = -1;
			Location spawnLocation = null;
			Location minimumLocation = null;
			Location maximumLocation = null;
			
			if(spawn.containsKey("canWalk")) {
				canWalk = (boolean) spawn.get("canWalk");
			}
			if(spawn.containsKey("face")) {
				face = ((Long) spawn.get("face")).intValue();
			}
			if(spawn.containsKey("spawn")) {
				int[] coords = new int[3];
				JSONArray locationArray = (JSONArray) spawn.get("spawn");
				int size = locationArray.size();
				if(size < 3) {
					coords[2] = 0;
				}
				for(int i = 0; i < size; i++) {
					coords[i] = ((Long) locationArray.get(i)).intValue();
				}
				spawnLocation = Location.create(coords[0], coords[1], coords[2]);
			}
			if(spawn.containsKey("min")) {
				int[] coords = new int[3];
				JSONArray locationArray = (JSONArray) spawn.get("min");
				int size = locationArray.size();
				if(size < 3) {
					coords[2] = 0;
				}
				for(int i = 0; i < size; i++) {
					coords[i] = ((Long) locationArray.get(i)).intValue();
				}
				minimumLocation = Location.create(coords[0], coords[1], coords[2]);
			}
			if(spawn.containsKey("max")) {
				int[] coords = new int[3];
				JSONArray locationArray = (JSONArray) spawn.get("max");
				int size = locationArray.size();
				if(size < 3) {
					coords[2] = 0;
				}
				for(int i = 0; i < size; i++) {
					coords[i] = ((Long) locationArray.get(i)).intValue();
				}
				maximumLocation = Location.create(coords[0], coords[1], coords[2]);
			}
			
			NPCSpawn npcSpawn = new NPCSpawn(npcId, canWalk, face, spawnLocation, minimumLocation, maximumLocation);
			
			spawnList.add(npcSpawn);
			
			NPC npc = NPC.getNpcInstance(npcSpawn.getNpcId());
			npc.setNPCSpawn(npcSpawn);
			npc.setLocation(spawnLocation);
			World.getInstance().register(npc);
			
		}
		
		spawns = spawnList.toArray(new NPCSpawn[spawnList.size()]);
		
		logger.info("Loaded " + spawns.length + " NPC spawns.");
		
	}

	private int npcId;
	private boolean canWalk;
	private int face;
	private Location spawnLocation;
	private Location minimumLocation;
	private Location maximumLocation;
	
	public NPCSpawn(int npcId, boolean canWalk, int face, Location spawnLocation, Location minimumLocation, Location maximumLocation) {
		this.npcId = npcId;
		this.canWalk = canWalk;
		this.face = face;
		this.spawnLocation = spawnLocation;
		this.minimumLocation = minimumLocation;
		this.maximumLocation = maximumLocation;
	}
	
	public NPCSpawn(NPC npc) {
		this.npcId = npc.getDefinition().getId();
		this.canWalk = false;
		this.face = 1;
		this.spawnLocation = npc.getLocation();
		this.minimumLocation = null;
		this.maximumLocation = null;
	}
	
	public NPCSpawn(NPC npc, boolean canWalk, int face, Location spawnLocation, Location minimumLocation, Location maximumLocation) {
		if(npc.getDefinition() != null) {
			this.npcId = npc.getDefinition().getId();
		} else {
			this.npcId = 255;
			npc.setId(255);
		}
		this.canWalk = canWalk;
		this.face = face;
		this.spawnLocation = spawnLocation;
		this.minimumLocation = minimumLocation;
		this.maximumLocation = maximumLocation;
	}
	
	public int getNpcId() {
		return npcId;
	}
	
	public boolean canWalk() {
		return canWalk;
	}
	
	public int getFace() {
		return face;
	}
	
	public Location getSpawnLocation() {
		return spawnLocation;
	}
	
	public Location getMinimumLocation() {
		return minimumLocation;
	}
	
	public Location getMaximumLocation() {
		return maximumLocation;
	}
	
}
