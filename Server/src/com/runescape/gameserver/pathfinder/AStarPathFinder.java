package com.runescape.gameserver.pathfinder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.region.Region;

/**
 * An implementation of a <code>PathFinder</code> which uses the A* search
 * algorithm. Unlike the <code>DumbPathFinder</code>, this will attempt to find
 * a possible path and is more suited for player following.
 * 
 * <br /><br /><b>Edits by Aizen Sousuke:</b> Fixed the diagonal clipping problem.
 * @author Graham Edgecombe
 * @author Aizen Sousuke
 */
public class AStarPathFinder implements PathFinder {
	
	/**
	 * The cost of moving in a straight line.
	 */
	private static final int COST_STRAIGHT = 10;
	
	/**
	 * Represents a node used by the A* algorithm.
	 * @author Graham Edgecombe
	 *
	 */
	private static class Node implements Comparable<Node> {
		
		/**
		 * The parent node.
		 */
		private Node parent;
		
		/**
		 * The cost.
		 */
		private int cost;
		
		/**
		 * The heuristic.
		 */
		private int heuristic;
		
		/**
		 * The depth.
		 */
		private int depth;
		
		/**
		 * The x coordinate.
		 */
		private final int x;
		
		/**
		 * The y coordinate.
		 */
		private final int y;
		
		/**
		 * Creates a node.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 */
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Sets the parent.
		 * @param parent The parent.
		 */
		public void setParent(Node parent) {
			this.parent = parent;
		}
		
		/**
		 * Gets the parent node.
		 * @return The parent node.
		 */
		public Node getParent() {
			return parent;
		}
		
		public void setCost(int cost) {
			this.cost = cost;
		}
		
		public int getCost() {
			return cost;
		}
		
		/**
		 * Gets the X coordinate.
		 * @return The X coordinate.
		 */
		public int getX() {
			return x;
		}
		
		/**
		 * Gets the Y coordinate.
		 * @return The Y coordinate.
		 */
		public int getY() {
			return y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cost;
			result = prime * result + depth;
			result = prime * result + heuristic;
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (cost != other.cost)
				return false;
			if (depth != other.depth)
				return false;
			if (heuristic != other.heuristic)
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public int compareTo(Node arg0) {
			return cost - arg0.cost;
		}
		
	}
	
	private Node current;
	private Node[][] nodes;
	private Set<Node> closed = new HashSet<Node>();
	private Set<Node> open = new HashSet<Node>();
	private boolean noClip = false;
	private boolean isNpc;
	private Mob mob;
	
	public AStarPathFinder(Mob mob) {
		this.mob = mob;
	}
	
	public AStarPathFinder(boolean isNpc) {
		this.isNpc = isNpc;
	}
	
	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
	}
	
	private boolean locExists(Map<String, NPC> locs, int x, int y) {
		return (locs.containsKey(x + "," + y));
	}
	
	@SuppressWarnings("unused")
	private Path findNpcPath(Location location, int radius, TileMap map, int srcX, int srcY, int dstX, int dstY) {
		Region region = World.getInstance().getRegionManager().getRegionByLocation(location);
		Collection<NPC> npcs = region.getNpcs();
		Collection<NPC> closeNpcs = new LinkedList<NPC>();
		Map<String, NPC> locs = new HashMap<String, NPC>();
		for(NPC npc : npcs) {
			if(location.isWithinInteractionDistance(npc.getLocation())) {
				closeNpcs.add(npc);
				locs.put(npc.getLocation().getX() + "," + npc.getLocation().getY(), npc);
				System.out.println("NPC at " + npc.getLocation().getX() + "," + npc.getLocation().getY());
			}
		}
		
		nodes = new Node[map.getWidth()][map.getHeight()];
		for(int x = 0; x < map.getWidth(); x++) {
			for(int y = 0; y < map.getHeight(); y++) {
				nodes[x][y] = new Node(x, y);
			}
		}
		
		open.add(nodes[srcX][srcY]);
		
		while(open.size() > 0) {
			current = getLowestCost();
			if(current == nodes[dstX][dstY]) {
				break;
			}
			open.remove(current);
			closed.add(current);
			
			int x = current.getX(), y = current.getY();
			
			// north west
			if(noClip || (x > 0 && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
				if(noClip || (y < (map.getHeight() - 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
					if(map.getTile(x - 1, y + 1).isEasternTraversalPermitted() && map.getTile(x - 1, y + 1).isSouthernTraversalPermitted()) {
						Node n = nodes[x-1][y+1];
						examineNode(n);
					}
				}
			}
			// north east
			if(noClip || (x < (map.getWidth() - 1) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
				if(noClip || (y < (map.getHeight() - 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
					if(map.getTile(x + 1, y + 1).isWesternTraversalPermitted() && map.getTile(x + 1, y + 1).isSouthernTraversalPermitted()) {
						Node n = nodes[x+1][y+1];
						examineNode(n);
					}
				}
			}
			// south west
			if(noClip || (y > 0 && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				if(noClip || (x > 0 && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
					if(map.getTile(x - 1, y - 1).isEasternTraversalPermitted() && map.getTile(x - 1, y - 1).isNorthernTraversalPermitted()) {
						Node n = nodes[x-1][y-1];
						examineNode(n);
					}
				}
			}
			// south east
			if(noClip || (y > 0 && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				if(noClip || (x < (map.getWidth() - 1) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
					if(map.getTile(x + 1, y - 1).isWesternTraversalPermitted() && map.getTile(x + 1, y - 1).isNorthernTraversalPermitted()) {
						Node n = nodes[x+1][y-1];
						examineNode(n);
					}
				}
			}
			// west
			if(noClip || (x > 0 && !locExists(locs, x - 1, y) && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
				Node n = nodes[x-1][y];
				examineNode(n);
			}
			// east
			if(noClip || (x < (map.getWidth() - 1) && !locExists(locs, x + 1, y) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
				Node n = nodes[x+1][y];
				examineNode(n);
			}
			// south
			if(noClip || (y > 0 && !locExists(locs, x, y - 1) && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				Node n = nodes[x][y-1];
				examineNode(n);
			}
			// north
			if(noClip || (y < (map.getHeight() - 1) && !locExists(locs, x, y + 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
				Node n = nodes[x][y+1];
				examineNode(n);
			}
		}
		
		if(nodes[dstX][dstY].getParent() == null) {
			return null;
		}
		
		Path p = new Path();
		Node n = nodes[dstX][dstY];
		while(n != nodes[srcX][srcY]) {
			p.addPoint(new PFPoint(n.getX() + location.getX() - radius, n.getY() + location.getY() - radius));
			n = n.getParent();
		}
		p.addPoint(new PFPoint(srcX + location.getX() - radius, srcY + location.getY() - radius));
		
		return p;
	}
	
	private boolean tileOccupied(int x, int y) {
		if(mob == null || !(mob instanceof NPC)) {
			return false;
		}
		
		Location loc = Location.create(x, y);
		NPC npc = World.getInstance().getRegionManager().getNpcAtLocation(loc);
		if(npc == null) {
			return false;
		}
		
		if(npc.equals(mob)) {
			return false;
		}
		
		if(npc.getLocation().equals(mob.getLocation())) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public Path findPath(Location location, int radius, TileMap map, int srcX, int srcY, int dstX, int dstY) {
		if(dstX < 0 || dstY < 0 || dstX >= map.getWidth() || dstY >= map.getHeight()) {
			return null; // out of range
		}
		
		if(isNpc) {
			//return findNpcPath(location, radius, map, srcX, srcY, dstX, dstY);
		}
		
		nodes = new Node[map.getWidth()][map.getHeight()];
		for(int x = 0; x < map.getWidth(); x++) {
			for(int y = 0; y < map.getHeight(); y++) {
				nodes[x][y] = new Node(x, y);
			}
		}
		
		open.add(nodes[srcX][srcY]);
		
		while(open.size() > 0) {
			current = getLowestCost();
			if(current == nodes[dstX][dstY]) {
				break;
			}
			open.remove(current);
			closed.add(current);
			
			int x = current.getX(), y = current.getY();
			
			// north west
			if(noClip || (x > 0 && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
				if(noClip || (y < (map.getHeight() - 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
					if(map.getTile(x - 1, y + 1).isEasternTraversalPermitted() && map.getTile(x - 1, y + 1).isSouthernTraversalPermitted()) {
						if(!tileOccupied(x - 1, y + 1)) {
							Node n = nodes[x-1][y+1];
							examineNode(n);
						}
					}
				}
			}
			// north east
			if(noClip || (x < (map.getWidth() - 1) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
				if(noClip || (y < (map.getHeight() - 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
					if(map.getTile(x + 1, y + 1).isWesternTraversalPermitted() && map.getTile(x + 1, y + 1).isSouthernTraversalPermitted()) {
						if(!tileOccupied(x + 1, y + 1)) {
							Node n = nodes[x+1][y+1];
							examineNode(n);
						}
					}
				}
			}
			// south west
			if(noClip || (y > 0 && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				if(noClip || (x > 0 && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
					if(map.getTile(x - 1, y - 1).isEasternTraversalPermitted() && map.getTile(x - 1, y - 1).isNorthernTraversalPermitted()) {
						if(!tileOccupied(x - 1, y - 1)) {
							Node n = nodes[x-1][y-1];
							examineNode(n);
						}
					}
				}
			}
			// south east
			if(noClip || (y > 0 && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				if(noClip || (x < (map.getWidth() - 1) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
					if(map.getTile(x + 1, y - 1).isWesternTraversalPermitted() && map.getTile(x + 1, y - 1).isNorthernTraversalPermitted()) {
						if(!tileOccupied(x + 1, y - 1)) {
							Node n = nodes[x+1][y-1];
							examineNode(n);
						}
					}
				}
			}
			// west
			if(noClip || (x > 0 && map.getTile(x - 1, y).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted())) {
				if(!tileOccupied(x - 1, y)) {
					Node n = nodes[x-1][y];
					examineNode(n);
				}
			}
			// east
			if(noClip || (x < (map.getWidth() - 1) && map.getTile(x + 1, y).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted())) {
				if(!tileOccupied(x + 1, y)) {
					Node n = nodes[x+1][y];
					examineNode(n);
				}
			}
			// south
			if(noClip || (y > 0 && map.getTile(x, y - 1).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted())) {
				if(!tileOccupied(x, y - 1)) {
					Node n = nodes[x][y-1];
					examineNode(n);
				}
			}
			// north
			if(noClip || (y < (map.getHeight() - 1) && map.getTile(x, y + 1).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted())) {
				if(!tileOccupied(x, y + 1)) {
					Node n = nodes[x][y+1];
					examineNode(n);
				}
			}
		}
		
		if(nodes[dstX][dstY].getParent() == null) {
			return null;
		}
		
		Path p = new Path();
		Node n = nodes[dstX][dstY];
		while(n != nodes[srcX][srcY]) {
			p.addPoint(new PFPoint(n.getX() + location.getX() - radius, n.getY() + location.getY() - radius));
			
			n = n.getParent();
		}
		p.addPoint(new PFPoint(srcX + location.getX() - radius, srcY + location.getY() - radius));
		
		return p;
	}

	private Node getLowestCost() {
		Node curLowest = null;
		for(Node n : open) {
			if(curLowest == null) {
				curLowest = n;
			} else {
				if(n.getCost() < curLowest.getCost()) {
					curLowest = n;
				}
			}
		}
		return curLowest;
	}

	private void examineNode(Node n) {
		int heuristic = estimateDistance(current, n);
		int nextStepCost = current.getCost() + heuristic;
		if(nextStepCost < n.getCost()) {
			open.remove(n);
			closed.remove(n);
		}
		if(!open.contains(n) && !closed.contains(n)) {
			n.setParent(current);
			n.setCost(nextStepCost);
			open.add(n);
		}
	}

	/**
	 * Estimates a distance between the two points.
	 * @param src The source node.
	 * @param dst The distance node.
	 * @return The distance.
	 */
	public int estimateDistance(Node src, Node dst) {
		int deltaX = src.getX() - dst.getX();
		int deltaY = src.getY() - dst.getY();
		return (Math.abs(deltaX) + Math.abs(deltaY)) *  COST_STRAIGHT;
	}

}
