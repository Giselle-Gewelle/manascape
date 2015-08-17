package com.runescape.gameserver.world.entity.mob.player;

import org.apache.mina.core.session.IoSession;

import com.runescape.gameserver.io.IsaacCipher;

public class PlayerDetails {
	
	private IoSession session;
	private String name;
	private String pass;
	private int uid;
	private IsaacCipher inCipher;
	private IsaacCipher outCipher;

	public PlayerDetails(IoSession session, String name, String pass, int uid, IsaacCipher inCipher, IsaacCipher outCipher) {
		this.session = session;
		this.name = name;
		this.pass = pass;
		this.uid = uid;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
	}
	
	public IoSession getSession() {
		return session;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return pass;
	}
	
	public int getUID() {
		return uid;
	}
	
	public IsaacCipher getInCipher() {
		return inCipher;
	}
	
	public IsaacCipher getOutCipher() {
		return outCipher;
	}

}
