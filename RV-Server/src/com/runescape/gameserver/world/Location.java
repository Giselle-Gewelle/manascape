package com.runescape.gameserver.world;

public class Location {
	
	private final int x;
	private final int y;
	private final int height;
	
	public static Location create(int x, int y, int height) {
		return new Location(x, y, height);
	}
	public static Location create(int x, int y) {
		return new Location(x, y);
	}
	
	public Location(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
		this.height = 0;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getHeight() {
		return height;
	}
	
	public int getLocalX() {
		return getLocalX(this);
	}
	public int getLocalY() {
		return getLocalY(this);
	}
	
	public int getLocalX(Location l) {
		return x - 8 * l.getRegionX();
	}
	public int getLocalY(Location l) {
		return y - 8 * l.getRegionY();
	}
	
	public int getRegionX() {
		return (x >> 3) - 6;
	}
	public int getRegionY() {
		return (y >> 3) - 6;
	}
	
	public boolean isWithinDistance(Location other) {
		if(height != other.height) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}
	
	public boolean isWithinInteractionDistance(Location other) {
		if(height != other.height) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
	}
	
	public Location getClosestLocation(Location loc) {
		if(height != loc.height) {
			return null;
		}
		
		int offX = 0;
		int offY = 0;
		
		if(x > loc.x) {
			offX = 1;
		} else {
			offX = -1;
		}
		if(y > loc.y){
			offY = 1;
		} else {
			offY = -1;
		}
		
		return new Location(loc.x + offX, loc.y + offY);
	}
	
	public boolean isWithinInteractionDistance(Location other, int distance) {
		if(height != other.height) {
			return false;
		}
		int neg = -distance;
		int deltaX = other.x - x;
		int deltaY = other.y - y;
		boolean returnType = (deltaX <= distance && deltaX >= neg && deltaY <= distance && deltaY >= neg);
		return returnType;
	}
	
	public boolean isWithinViewingDistance(Location other) {
		if (other == null) {
			return false;
		}
		if (height != other.height) {
			return false;
		}
		final int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}
	
	@Override
	public int hashCode() {
		return height << 30 | x << 15 | y;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.height == height;
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "," + height + "]";
	}

	public Location transform(int diffX, int diffY, int diffHeight) {
		return Location.create(x + diffX, y + diffY, height + diffHeight);
	}

}
