package com.runescape.gameserver.world.entity.mob.player;

public class InterfaceConfigs {

	public static final int ATTACK_STYLE = 43;
	public static final int SWAPPING = 304;
	public static final int WITHDRAW_NOTES = 115;
	public static final int AUTO_RETALIATE = 172;
	public static final int RUNNING = 173;
	
	public static void setLoginConfigs(Player player) {
		Settings settings = player.getSettings();
		player.getPacketSender().sendConfig(WITHDRAW_NOTES, settings.getBool("withdrawNotes") ? 1 : 0);
		player.getPacketSender().sendConfig(SWAPPING, settings.getBool("swapping") ? 0 : 1);
		player.getPacketSender().sendConfig(RUNNING, settings.getBool("running") ? 1 : 0);
		player.getPacketSender().sendConfig(AUTO_RETALIATE, settings.getBool("autoRetaliate") ? 0 : 1);
	}
	
	/*public static final int SIZE = 50;
	
	public static final int ATTACK_STYLE = 0;
	
	private int[] configs = new int[SIZE];
	
	public int[] getConfigs() {
		return this.configs;
	}
	
	public int get(int configId) {
		return this.configs[configId];
	}
	
	public void set(int configId, int configValue) {
		this.configs[configId] = configValue;
	}*/
	
}
