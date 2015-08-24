package com.runescape.gameserver.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.entity.mob.Mob;

public class Distance {

	public static int calculateDistance(Mob entity, Mob following) {
		List<Integer> distances = new ArrayList<Integer>();

		List<Location> entityTiles = entity.getInternalTiles();
		List<Location> followingTiles = following.getInternalTiles();

		for(Location en : entityTiles) {
			for(Location fol : followingTiles) {
				distances.add((int) Math.floor(calculateDistance(en, fol)));
			}
		}

		Collections.sort(distances);

		return distances.get(0);
	}
	
	private static double calculateDistance(Location location, Location other) {
		return Math
				.sqrt(Math.pow(other.getX() - location.getX(), 2)
						+ Math.pow(other.getY() - location.getY(), 2));
	}
}
