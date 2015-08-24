package com.runescape.gameserver.pathfinder;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents a path found by a <code>PathFinder</code> between two points.
 * @author Graham Edgecombe
 *
 */
public class Path {
	
	/**
	 * The queue of points.
	 */
	private Deque<PFPoint> tiles = new LinkedList<PFPoint>();
	
	/**
	 * Creates an empty path.
	 */
	public Path() {
		
	}
	
	/**
	 * Adds a point onto the queue.
	 * @param p The point to add.
	 */
	public void addPoint(PFPoint p) {
		tiles.addFirst(p);
	}
	
	/**
	 * Gets the deque backing this path.
	 * @return The deque backing this path.
	 */
	public Deque<PFPoint> getPoints() {
		return tiles;
	}
	
}
