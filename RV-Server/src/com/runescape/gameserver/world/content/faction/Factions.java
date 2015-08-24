package com.runescape.gameserver.world.content.faction;

import java.util.ArrayList;
import java.util.List;

import com.runescape.gameserver.world.entity.mob.Mob;

public class Factions {

	private Mob mob;
	private GodFaction godFaction = null;
	private List<CityFaction> cityFactions = null;
	
	public Factions(Mob mob) {
		this.mob = mob;
		godFaction = null;
		cityFactions = null;
	}
	
	public GodFaction getGodFaction() {
		return godFaction;
	}
	public boolean hasGodFaction() {
		return godFaction != null;
	}
	public void setGodFaction(GodFaction faction) {
		godFaction = faction;
	}
	
	public List<CityFaction> getCityFactions() {
		return cityFactions;
	}
	public boolean hasCityFaction() {
		return cityFactions != null;
	}
	public void addCityFaction(CityFaction faction) {
		if(cityFactions == null) {
			cityFactions = new ArrayList<CityFaction>();
		}
		cityFactions.add(faction);
	}
	public boolean isInCityFaction(CityFaction faction) {
		for(CityFaction f : cityFactions) {
			if(f == faction) {
				return true;
			}
		}
		
		return false;
	}
	
	public Mob getMob() {
		return mob;
	}
	
}
