package com.runescape.gameserver.world.content.faction;

public enum GodFaction {

	SARADOMIN("000080"),
	ZAMORAK("CC0000"),
	GUTHIX("006600"),
	ARMADYL("CC5200"),
	BANDOS("4D4B00"),
	ZAROS("B800CC"),
	GODLESS("CCFF33");
	
	private String name;
	private String colour;
	
	private GodFaction(String colour) {
		String name = this.name().toLowerCase();
		this.name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		this.colour = colour;
	}
	
	public String getColour() {
		return colour;
	}
	public String colouredString(String string) {
		return "<col=" + colour + ">" + string + "</col>";
	}
	public String colouredName() {
		return "<col=" + colour + ">" + name + "</col>";
	}
	
}
