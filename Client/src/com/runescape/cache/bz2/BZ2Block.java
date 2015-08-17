package com.runescape.cache.bz2;

public class BZ2Block {

	public final int anInt38 = 4096;
	public final int anInt39 = 16;
	public final int anInt40 = 258;
	public final int anInt41 = 23;
	public final int anInt42 = 1;
	public final int anInt43 = 6;
	public final int anInt44 = 50;
	public final int anInt45 = 4;
	public final int anInt46 = 18002;
	public byte input[];
	public int nextInput;
	public int compressedSize;
	public int totalInputLo32;
	public int totalInputHi32;
	public byte aByteArray52[];
	public int anInt53;
	public int decompressedSize;
	public int anInt55;
	public int anInt56;
	public byte aByte57;
	public int anInt58;
	public boolean randomized;
	public int bsBuff;
	public int bsLive;
	public int blockSize100k;
	public int anInt63;
	public int origPointer;
	public int anInt65;
	public int anInt66;
	public int anIntArray67[];
	public int anInt68;
	public int anIntArray69[];
	public int anIntArray70[];
	public static int ll8[];
	public int nInUse;
	public boolean inUse[];
	public boolean aBooleanArray74[];
	public byte seqToUnseq[];
	public byte aByteArray76[];
	public int anIntArray77[];
	public byte aByteArray78[];
	public byte aByteArray79[];
	public byte aByteArrayArray80[][];
	public int anIntArrayArray81[][];
	public int anIntArrayArray82[][];
	public int anIntArrayArray83[][];
	public int anIntArray84[];
	public int anInt85;

	public BZ2Block() {
		anIntArray67 = new int[256];
		anIntArray69 = new int[257];
		anIntArray70 = new int[257];
		inUse = new boolean[256];
		aBooleanArray74 = new boolean[16];
		seqToUnseq = new byte[256];
		aByteArray76 = new byte[4096];
		anIntArray77 = new int[16];
		aByteArray78 = new byte[18002];
		aByteArray79 = new byte[18002];
		aByteArrayArray80 = new byte[6][258];
		anIntArrayArray81 = new int[6][258];
		anIntArrayArray82 = new int[6][258];
		anIntArrayArray83 = new int[6][258];
		anIntArray84 = new int[6];
	}
}
