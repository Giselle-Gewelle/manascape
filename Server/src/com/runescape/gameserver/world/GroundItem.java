package com.runescape.gameserver.world;

import com.runescape.gameserver.world.region.Region;

public class GroundItem {
	
	private String controllerName = "";
	private Item item;
	private Location location;
	private Region region;
	private boolean global;
	private boolean registered;

	public GroundItem(String controllerName, Item item, Location location) {
		this.controllerName = controllerName;
		this.item = item;
		this.location = location;
		this.region = World.getInstance().getRegionManager().getRegionByLocation(this.location);
		this.registered = true;
	}

	public String getControllerName() {
		return controllerName;
	}

	public Item getItem() {
		return item;
	}

	public Location getLocation() {
		return location;
	}

	public Region getRegion() {
		return region;
	}

	public boolean isGlobal() {
		return global;
	}

	public boolean isOwnedBy(String name) {
		return isGlobal() || controllerName.equalsIgnoreCase(name);
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public Item setItem(Item item) {
		return this.item = item;
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

}
