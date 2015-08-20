package org.manascape.security;

import java.util.Random;

public final class Password {
	
private static final int SALT_LENGTH = 50;
	
	private static final char[] ALPHA = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	private static final char[] NUMERIC = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};
	private static final char[] SPECIAL = {
		'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '=', '_', '+', 
		'[', ']', '{', '}', '|', ';', ':', ',', '.', '/', '<', '>', '?'
	};
	
	
	private String hash;
	private String salt;
	
	public Password(String password) {
		this.salt = generateSalt();
		this.hash = generateHash(password);
	}
	
	public Password(String hash, String salt) {
		this.hash = hash;
		this.salt = salt;
	}
	
	public boolean equals(String password) {
		return generateHash(password).equals(this.hash);
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public String getSalt() {
		return this.salt;
	}
	
	private String generateHash(String password) {
		return Hashing.sha512(salt + password);
	}
	
	private String generateSalt() {
		String salt = "";
		Random random = new Random();
		
		int lastCharType = -1;
		while(salt.length() != SALT_LENGTH) {
			int charType = random.nextInt(4);
			if(lastCharType == charType) {
				continue;
			}
			
			int charIdx = 0;
			char newChar = ' ';
			switch(charType) {
				default:
				case 0:
					charIdx = random.nextInt(ALPHA.length);
					newChar = ALPHA[charIdx];
					break;
				case 1:
					charIdx = random.nextInt(ALPHA.length);
					newChar = Character.toUpperCase(ALPHA[charIdx]);
					break;
				case 2:
					charIdx = random.nextInt(NUMERIC.length);
					newChar = NUMERIC[charIdx];
					break;
				case 3:
					charIdx = random.nextInt(SPECIAL.length);
					newChar = SPECIAL[charIdx];
					break;
			}
			
			salt += newChar;
		}
		
		return salt;
	}
	
}
