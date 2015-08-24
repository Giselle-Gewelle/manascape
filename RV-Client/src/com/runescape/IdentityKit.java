package com.runescape;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class IdentityKit {

	public static void method434(CacheArchive class2, int i) {
		ByteBuffer byteStream = new ByteBuffer(class2.getDataForName("idk.dat"));
		anInt814 = byteStream.getUnsignedShort();
		if(cache == null)
			cache = new IdentityKit[anInt814];
		for(int j = 0; j < anInt814; j++) {
			if(cache[j] == null)
				cache[j] = new IdentityKit();
			cache[j].method435(aByte811, byteStream);
		}

		if(i == 36135)
			;
	}

	public void method435(byte byte0, ByteBuffer byteStream) {
		if(byte0 == 6)
			byte0 = 0;
		else
			throw new NullPointerException();
		do {
			int i = byteStream.getUnsignedByte();
			if(i == 0)
				return;
			if(i == 1)
				anInt816 = byteStream.getUnsignedByte();
			else if(i == 2) {
				int j = byteStream.getUnsignedByte();
				anIntArray817 = new int[j];
				for(int k = 0; k < j; k++)
					anIntArray817[k] = byteStream.getUnsignedShort();

			} else if(i == 3)
				aBoolean821 = true;
			else if(i >= 40 && i < 50)
				anIntArray818[i - 40] = byteStream.getUnsignedShort();
			else if(i >= 50 && i < 60)
				anIntArray819[i - 50] = byteStream.getUnsignedShort();
			else if(i >= 60 && i < 70)
				anIntArray820[i - 60] = byteStream.getUnsignedShort();
			else
				System.out.println("Error unrecognised config code: " + i);
		} while(true);
	}

	public boolean method436(int i) {
		if(anIntArray817 == null)
			return true;
		boolean flag = true;
		i = 89 / i;
		for(int j = 0; j < anIntArray817.length; j++)
			if(!Model.isCached(anIntArray817[j]))
				flag = false;

		return flag;
	}

	public Model getBodyModel() {
		if(anIntArray817 == null)
			return null;
		Model aclass50_sub1_sub4_sub4[] = new Model[anIntArray817.length];
		for(int i = 0; i < anIntArray817.length; i++)
			aclass50_sub1_sub4_sub4[i] = Model.getModel(anIntArray817[i]);

		Model class50_sub1_sub4_sub4;
		if(aclass50_sub1_sub4_sub4.length == 1)
			class50_sub1_sub4_sub4 = aclass50_sub1_sub4_sub4[0];
		else
			class50_sub1_sub4_sub4 = new Model(aclass50_sub1_sub4_sub4.length, aclass50_sub1_sub4_sub4);
		for(int j = 0; j < 6; j++) {
			if(anIntArray818[j] == 0)
				break;
			class50_sub1_sub4_sub4.recolour(anIntArray818[j], anIntArray819[j]);
		}

		return class50_sub1_sub4_sub4;
	}

	public boolean method438(int i) {
		if(i != -10584)
			throw new NullPointerException();
		boolean flag = true;
		for(int j = 0; j < 5; j++)
			if(anIntArray820[j] != -1 && !Model.isCached(anIntArray820[j]))
				flag = false;

		return flag;
	}

	public Model method439() {
		Model aclass50_sub1_sub4_sub4[] = new Model[5];
		int i = 0;
		for(int j = 0; j < 5; j++)
			if(anIntArray820[j] != -1)
				aclass50_sub1_sub4_sub4[i++] = Model.getModel(anIntArray820[j]);

		Model class50_sub1_sub4_sub4 = new Model(i, aclass50_sub1_sub4_sub4);
		for(int k = 0; k < 6; k++) {
			if(anIntArray818[k] == 0)
				break;
			class50_sub1_sub4_sub4.recolour(anIntArray818[k], anIntArray819[k]);
		}

		return class50_sub1_sub4_sub4;
	}

	public IdentityKit() {
		anInt812 = -766;
		anInt813 = 256;
		anInt816 = -1;
		anIntArray818 = new int[6];
		anIntArray819 = new int[6];
		aBoolean821 = false;
	}

	public static byte aByte811 = 6;
	public int anInt812;
	public int anInt813;
	public static int anInt814;
	public static IdentityKit cache[];
	public int anInt816;
	public int anIntArray817[];
	public int anIntArray818[];
	public int anIntArray819[];
	public int anIntArray820[] = { -1, -1, -1, -1, -1 };
	public boolean aBoolean821;

}
