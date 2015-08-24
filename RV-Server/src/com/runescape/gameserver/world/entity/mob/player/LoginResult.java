package com.runescape.gameserver.world.entity.mob.player;


public class LoginResult {
	
	private int returnCode;
	private Player player;
	
	public LoginResult(int returnCode) {
		this(returnCode, null);
	}
	
	public LoginResult(int returnCode, Player player) {
		this.returnCode = returnCode;
		this.player = player;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
