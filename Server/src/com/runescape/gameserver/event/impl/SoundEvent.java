package com.runescape.gameserver.event.impl;

import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class SoundEvent extends Event {

	private Player player;
	private int soundId;
	private long laterDelay;
	private int cycles;
	
	public SoundEvent(Player player, int soundId, long delay, long laterDelay, int cycles) {
		super(delay);
		this.player = player;
		this.soundId = soundId;
		this.laterDelay = laterDelay;
		this.cycles = cycles;
	}
	
	public SoundEvent(Player player, int soundId, long delay) {
		this(player, soundId, delay, delay, -1);
	}
	
	public SoundEvent(Player player, int soundId, long delay, int cycles) {
		this(player, soundId, delay, delay, cycles);
	}
	
	public SoundEvent(Player player, int soundId) {
		this(player, soundId, 600);
	}
	
	public SoundEvent(Player player, int soundId, int cycles) {
		this(player, soundId, 600, 600, cycles);
	}

	@Override
	public void execute() {
		if(cycles != -1) {
			cycles--;
		}
		if(cycles == 0) {
			this.stop();
		}
		if(this.getDelay() != laterDelay) {
			this.setDelay(laterDelay);
		}
		player.getPacketSender().sendSound(soundId);
	}

}
