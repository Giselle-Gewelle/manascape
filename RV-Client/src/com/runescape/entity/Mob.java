package com.runescape.entity;

import com.runescape.Class14;
import com.runescape.Class50_Sub1_Sub4;

public abstract class Mob extends Class50_Sub1_Sub4 {

	public void method564(int i) {
		anInt1633 = 0;
		for(anInt1613 = 0; i >= 0;)
			return;

	}

	public boolean isVisible() {
		return false;
	}

	public void method566(boolean flag, int i, int j) {
		while(j >= 0)
			return;
		int k = anIntArray1586[0];
		int l = anIntArray1587[0];
		if(i == 0) {
			k--;
			l++;
		}
		if(i == 1)
			l++;
		if(i == 2) {
			k++;
			l++;
		}
		if(i == 3)
			k--;
		if(i == 4)
			k++;
		if(i == 5) {
			k--;
			l--;
		}
		if(i == 6)
			l--;
		if(i == 7) {
			k++;
			l--;
		}
		if(animation != -1 && Class14.aClass14Array293[animation].anInt306 == 1)
			animation = -1;
		if(anInt1633 < 9)
			anInt1633++;
		for(int i1 = anInt1633; i1 > 0; i1--) {
			anIntArray1586[i1] = anIntArray1586[i1 - 1];
			anIntArray1587[i1] = anIntArray1587[i1 - 1];
			aBooleanArray1591[i1] = aBooleanArray1591[i1 - 1];
		}

		anIntArray1586[0] = k;
		anIntArray1587[0] = l;
		aBooleanArray1591[0] = flag;
	}

	public void method567(int i, boolean flag, int j, int k) {
		for(int l = 0; l < 4; l++)
			if(anIntArray1632[l] <= i) {
				anIntArray1630[l] = j;
				anIntArray1631[l] = k;
				anIntArray1632[l] = i + 70;
				return;
			}

		if(flag)
			anInt1581 = -52;
	}

	public void method568(int i, byte byte0, boolean flag, int j) {
		if(animation != -1 && Class14.aClass14Array293[animation].anInt306 == 1)
			animation = -1;
		if(!flag) {
			int k = j - anIntArray1586[0];
			int i1 = i - anIntArray1587[0];
			if(k >= -8 && k <= 8 && i1 >= -8 && i1 <= 8) {
				if(anInt1633 < 9)
					anInt1633++;
				for(int j1 = anInt1633; j1 > 0; j1--) {
					anIntArray1586[j1] = anIntArray1586[j1 - 1];
					anIntArray1587[j1] = anIntArray1587[j1 - 1];
					aBooleanArray1591[j1] = aBooleanArray1591[j1 - 1];
				}

				anIntArray1586[0] = j;
				anIntArray1587[0] = i;
				aBooleanArray1591[0] = false;
				return;
			}
		}
		anInt1633 = 0;
		anInt1613 = 0;
		anInt1623 = 0;
		anIntArray1586[0] = j;
		anIntArray1587[0] = i;
		if(byte0 == 5) {
			byte0 = 0;
		} else {
			for(int l = 1; l > 0; l++)
				;
		}
		anInt1610 = anIntArray1586[0] * 128 + anInt1601 * 64;
		anInt1611 = anIntArray1587[0] * 128 + anInt1601 * 64;
	}

	public Mob() {
		anInt1581 = -89;
		anInt1582 = 100;
		anIntArray1586 = new int[10];
		anIntArray1587 = new int[10];
		anInt1588 = -1;
		aBooleanArray1591 = new boolean[10];
		aBoolean1592 = false;
		height = 200;
		anInt1595 = -1000;
		anInt1600 = 32;
		anInt1601 = 1;
		anInt1609 = -1;
		currentGfx = -1;
		walkAnim = -1;
		turn180Anim = -1;
		turn90CWAnim = -1;
		turn90CCWAnim = -1;
		animation = -1;
		runAnim = -1;
		anIntArray1630 = new int[4];
		anIntArray1631 = new int[4];
		anIntArray1632 = new int[4];
		standAnim = -1;
		standTurnAnim = -1;
	}

	public String aString1580;
	public int anInt1581;
	public int anInt1582;
	public int anInt1583;
	public int anInt1584;
	public int anInt1585;
	public int anIntArray1586[];
	public int anIntArray1587[];
	public int anInt1588;
	public int anInt1589;
	public int anInt1590;
	public boolean aBooleanArray1591[];
	public boolean aBoolean1592;
	public int anInt1593;
	public int height;
	public int anInt1595;
	public int anInt1596;
	public int anInt1597;
	public int anInt1598;
	public int anInt1599;
	public int anInt1600;
	public int anInt1601;
	public int anInt1602;
	public int anInt1603;
	public int anInt1604;
	public int anInt1605;
	public int anInt1606;
	public int anInt1607;
	public int anInt1608;
	public int anInt1609;
	public int anInt1610;
	public int anInt1611;
	public int anInt1612;
	public int anInt1613;
	public int currentGfx;
	public int currentAnim;
	public int anInt1616;
	public int anInt1617;
	public int anInt1618;
	public int walkAnim;
	public int turn180Anim;
	public int turn90CWAnim;
	public int turn90CCWAnim;
	public int anInt1623;
	public int animation;
	public int anInt1625;
	public int anInt1626;
	public int animationDelay;
	public int anInt1628;
	public int runAnim;
	public int anIntArray1630[];
	public int anIntArray1631[];
	public int anIntArray1632[];
	public int anInt1633;
	public int standAnim;
	public int standTurnAnim;
}
