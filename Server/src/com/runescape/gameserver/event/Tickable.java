package com.runescape.gameserver.event;

public abstract class Tickable {
	
	private int ticks = 0;
	
	private final int delay;

	private boolean running = true;
	
	public Tickable(int delay) {
		this.delay = delay;
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean isRunnable() {
		ticks++;
		if (ticks == delay) {
			ticks = 0;
		}
		return (ticks == 0 || delay == 0) && running;
	}

	public void stop() {
		running = false;
	}

	public abstract void execute();

}