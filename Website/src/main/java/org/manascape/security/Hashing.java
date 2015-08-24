package org.manascape.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

public final class Hashing {
	
	private static final Logger LOG = Logger.getLogger(Hashing.class);
	
	public static String shuffle(String hash) {
		int halfIdx = hash.length() / 2;
		String firstHalf = hash.substring(0, halfIdx);
		String secondHalf = hash.substring(halfIdx, hash.length());
		
		firstHalf = new StringBuilder(firstHalf).reverse().toString();
		
		int secondHalfHalfIdx = secondHalf.length() / 2;
		String secondHalfFirstHalf = secondHalf.substring(0, secondHalfHalfIdx);
		String secondHalfSecondHalf = secondHalf.substring(secondHalfHalfIdx, secondHalf.length());
		
		secondHalfFirstHalf = new StringBuilder(secondHalfFirstHalf).reverse().toString();
		
		return new StringBuilder().append(secondHalfSecondHalf).append(secondHalfFirstHalf).append(firstHalf).toString();
	}
	
	public static String order(String hash) {
		int halfIdx = hash.length() / 2;
		String secondHalf = hash.substring(0, halfIdx);
		String firstHalf = hash.substring(halfIdx, hash.length());
		
		firstHalf = new StringBuilder(firstHalf).reverse().toString();
		
		int secondHalfHalfIdx = secondHalf.length() / 2;
		String secondHalfSecondHalf = secondHalf.substring(0, secondHalfHalfIdx);
		String secondHalfFirstHalf = secondHalf.substring(secondHalfHalfIdx, secondHalf.length());
		
		secondHalfFirstHalf = new StringBuilder(secondHalfFirstHalf).reverse().toString();
		
		return new StringBuilder().append(firstHalf).append(secondHalfFirstHalf).append(secondHalfSecondHalf).toString();
	}
	
	public static String generateSessionHash() {
		long timestamp = new Date().getTime();
		UUID uuid = UUID.randomUUID();
		String salt = generateSalt();
		
		return sha512(uuid + salt + timestamp);
	}
	
	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[64];
		random.nextBytes(saltBytes);
		
		return convertToHex(saltBytes);
	}
	
	public static String sha512(String string) {
	    try {
	    	MessageDigest md = MessageDigest.getInstance("SHA-512");
	        return convertToHex(md.digest(string.getBytes("UTF-8")));
	    } catch (NoSuchAlgorithmException e) {
	        LOG.error("NoSuchAlgorithmException while attempting to generate SHA512 hash.", e);
	    } catch (UnsupportedEncodingException e) {
	        LOG.error("UnsupportedEncodingException while attempting to generate SHA512 hash.", e);
	    }
	    
	    return null;
	}
	
	private static String convertToHex(byte[] bytes) {
	    StringBuffer buffer = new StringBuffer();
	    
	    for(int i = 0; i < bytes.length; i++) {
	    	buffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    return buffer.toString();
	}
	
}
