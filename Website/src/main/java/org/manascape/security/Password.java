package org.manascape.security;

public final class Password {
	
	private String hash;
	private String salt;
	
	public Password(String password) {
		this.salt = Hashing.generateSalt();
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
	
}
