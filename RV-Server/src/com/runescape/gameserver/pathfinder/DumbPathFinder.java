package com.runescape.gameserver.pathfinder;
 
import com.runescape.gameserver.world.Location;
 
/**
 * An implementation of a <code>PathFinder</code> which is 'dumb' and only looks
 * at surrounding tiles for a path, suitable for an NPC.
 * 
 * <br /><br /><b>Edits by Aizen Sousuke:</b> Fixed the diagonal clipping problem.
 * @author Graham Edgecombe
 * @author Aizen Sousuke
 */
public class DumbPathFinder implements PathFinder {
 
    @Override
    public Path findPath(Location location, int radius, TileMap map, int srcX,
            int srcY, int dstX, int dstY) {
        int stepX = 0, stepY = 0;
        Tile thisTile = map.getTile(srcX, srcY);
        Tile destTile = map.getTile(dstX, dstY);
        
        //NORTHWEST
        if(srcX > dstX && srcY < dstY) {
        	//check this tile
        	if(thisTile.isNorthernTraversalPermitted() && thisTile.isWesternTraversalPermitted()) {
        		//check the destination tile
        		if(destTile.isSouthernTraversalPermitted() && destTile.isEasternTraversalPermitted()) {
        			//check the tile vertical to the current tile
        			Tile vertTile = map.getTile(srcX, srcY + 1);
        			if(vertTile.isSouthernTraversalPermitted() && vertTile.isWesternTraversalPermitted()) {
        				//check the tile horizontal to the current tile
        				Tile horiTile = map.getTile(srcX - 1, srcY);
        				if(horiTile.isNorthernTraversalPermitted() && horiTile.isEasternTraversalPermitted()) {
        					stepX = -1;
        					stepY = 1;
        				}
        			}
        		}
        	}
        //SOUTHWEST
        } else if(srcX > dstX && srcY > dstY) {
        	//check this tile
        	if(thisTile.isSouthernTraversalPermitted() && thisTile.isWesternTraversalPermitted()) {
        		//check the destination tile
        		if(destTile.isNorthernTraversalPermitted() && destTile.isEasternTraversalPermitted()) {
        			//check the tile vertical to the current tile
        			Tile vertTile = map.getTile(srcX, srcY - 1);
        			if(vertTile.isNorthernTraversalPermitted() && vertTile.isWesternTraversalPermitted()) {
        				//check the tile horizontal to the current tile
        				Tile horiTile = map.getTile(srcX - 1, srcY);
        				if(horiTile.isSouthernTraversalPermitted() && horiTile.isEasternTraversalPermitted()) {
        					stepX = -1;
        					stepY = -1;
        				}
        			}
        		}
        	}
        //NORTHEAST
        } else if(srcX < dstY && srcY < dstY) {
        	//check this tile
        	if(thisTile.isNorthernTraversalPermitted() && thisTile.isEasternTraversalPermitted()) {
        		//check the destination tile
        		if(destTile.isSouthernTraversalPermitted() && destTile.isWesternTraversalPermitted()) {
        			//check the tile vertical to the current tile
        			Tile vertTile = map.getTile(srcX, srcY + 1);
        			if(vertTile.isSouthernTraversalPermitted() && vertTile.isEasternTraversalPermitted()) {
        				//check the tile horizontal to the current tile
        				Tile horiTile = map.getTile(srcX + 1, srcY);
        				if(horiTile.isNorthernTraversalPermitted() && horiTile.isWesternTraversalPermitted()) {
        					stepX = 1;
        					stepY = 1;
        				}
        			}
        		}
        	}
        //SOUTHEAST
        } else if(srcX < dstY && srcY > dstY) {
        	//check this tile
        	if(thisTile.isSouthernTraversalPermitted() && thisTile.isEasternTraversalPermitted()) {
        		//check the destination tile
        		if(destTile.isNorthernTraversalPermitted() && destTile.isWesternTraversalPermitted()) {
        			//check the tile vertical to the current tile
        			Tile vertTile = map.getTile(srcX, srcY - 1);
        			if(vertTile.isNorthernTraversalPermitted() && vertTile.isEasternTraversalPermitted()) {
        				//check the tile horizontal to the current tile
        				Tile horiTile = map.getTile(srcX + 1, srcY);
        				if(horiTile.isSouthernTraversalPermitted() && horiTile.isWesternTraversalPermitted()) {
        					stepX = 1;
        					stepY = -1;
        				}
        			}
        		}
        	}
        //WEST, should check western on this tile and eastern on dest
        } else if (srcX > dstX
                && map.getTile(srcX, srcY).isWesternTraversalPermitted()
                && map.getTile(dstX, dstY).isEasternTraversalPermitted()) {
            stepX = -1;
        //EAST, should check eastern on this tile and western on dest
        } else if (srcX < dstX
                && map.getTile(srcX, srcY).isEasternTraversalPermitted()
                && map.getTile(dstX, dstY).isWesternTraversalPermitted()) {
            stepX = 1;
        }
        //SOUTH, should check southern on this and northern on dest
        if (srcY > dstY
                && map.getTile(srcX, srcY).isSouthernTraversalPermitted()
                && map.getTile(dstX, dstY).isNorthernTraversalPermitted()) {
            stepY = -1;
        //NORTH, should check northern on this and southern on dest
        } else if (srcY < dstY
                && map.getTile(srcX, srcY).isNorthernTraversalPermitted()
                && map.getTile(dstX, dstY).isSouthernTraversalPermitted()) {
            stepY = 1;
        }
        
        if (stepX != 0 || stepY != 0) {
            Path p = new Path();
            p.addPoint(new PFPoint(location.getX() + stepX, location.getY() + stepY));
            p.addPoint(new PFPoint(srcX + location.getX() - radius, srcY + location.getY() - radius));
            return p;
        }
        return null;
    }
 
}