package com.runescape;

import java.applet.AppletContext;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

import com.runescape.app.GameApplet;
import com.runescape.cache.CacheArchive;
import com.runescape.entity.Mob;
import com.runescape.entity.NPC;
import com.runescape.entity.Player;
import com.runescape.entity.def.NPCDefinition;
import com.runescape.io.ByteBuffer;
import com.runescape.io.ClientSocket;
import com.runescape.io.IsaacCipher;
import com.runescape.sign.Signlink;

@SuppressWarnings("serial")
public class Client extends GameApplet {

	public static final int 
		RV_REVISION = 1,
		RS_REVISION = 377,
		MAP_OFFSET_X = 34,
		MAP_ORB_SIZE = 27;
	private static final int[] orbColours = new int[] {
		0x00FF00,
		0x1CFF00,
		0x38FF00,
		0x55FF00,
		0x71FF00,
		0x8DFF00,
		0xAAFF00,
		0xC6FF00,
		0xE2FF00,
		0xFFFF00,
		0xFFF303,
		0xFFE806,
		0xFFDC0A,
		0xFFD10D,
		0xFFC511,
		0xFFBA14,
		0xFFAE18,
		0xFFA31B,
		0xFF981F,
		0xFF871B,
		0xFF7618,
		0xFF6514,
		0xFF5411,
		0xFF430D,
		0xFF320A,
		0xFF2106,
		0xFF1003,
		0xFF0000
	};
	
	private IndexedImage[] 
		dataOrbBg, 
		dataOrbFills, 
		dataOrbIcons;
	
	private boolean playerRunning;

	public void method14(String s, int i) {
		if(s == null || s.length() == 0) {
			anInt862 = 0;
			return;
		}
		String s1 = s;
		String as[] = new String[100];
		int j = 0;
		do {
			int k = s1.indexOf(" ");
			if(k == -1)
				break;
			String s2 = s1.substring(0, k).trim();
			if(s2.length() > 0)
				as[j++] = s2.toLowerCase();
			s1 = s1.substring(k + 1);
		} while(true);
		s1 = s1.trim();
		if(s1.length() > 0)
			as[j++] = s1.toLowerCase();
		anInt862 = 0;
		if(i != 2)
			aBoolean959 = !aBoolean959;
		label0: for(int l = 0; l < ItemDefinition.itemCount; l++) {
			ItemDefinition class16 = ItemDefinition.forId(l);
			if(class16.parentNoteId != -1 || class16.name == null)
				continue;
			String s3 = class16.name.toLowerCase();
			for(int i1 = 0; i1 < j; i1++)
				if(s3.indexOf(as[i1]) == -1)
					continue label0;

			aStringArray863[anInt862] = s3;
			anIntArray864[anInt862] = l;
			anInt862++;
			if(anInt862 >= aStringArray863.length)
				return;
		}

	}

	public void method15(boolean flag) {
		byteStream2.createFrame(110);
		if(flag)
			aClass6ArrayArrayArray1323 = null;
		if(anInt1089 != -1) {
			method44(aBoolean1190, anInt1089);
			anInt1089 = -1;
			tabRepaintRequested = true;
			aBoolean1239 = false;
			aBoolean950 = true;
		}
		if(anInt988 != -1) {
			method44(aBoolean1190, anInt988);
			anInt988 = -1;
			aBoolean1240 = true;
			aBoolean1239 = false;
		}
		if(anInt1053 != -1) {
			method44(aBoolean1190, anInt1053);
			anInt1053 = -1;
			repaintRequested = true;
		}
		if(anInt960 != -1) {
			method44(aBoolean1190, anInt960);
			anInt960 = -1;
		}
		if(anInt1169 != -1) {
			method44(aBoolean1190, anInt1169);
			anInt1169 = -1;
		}
	}

	public void method16(int i, byte byte0, ByteBuffer byteStream) {
		while(byteStream.bitPosition + 10 < i * 8) {
			int j = byteStream.method532(402, 11);
			if(j == 2047)
				break;
			if(sessionPlayers[j] == null) {
				sessionPlayers[j] = new Player();
				if(playerUpdateStreams[j] != null)
					sessionPlayers[j].updateAppearance(playerUpdateStreams[j], 0);
			}
			sessionPlayerList[anInt971++] = j;
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[j];
			class50_sub1_sub4_sub3_sub2.anInt1585 = currentTime;
			int k = byteStream.method532(402, 5);
			if(k > 15)
				k -= 32;
			int l = byteStream.method532(402, 1);
			if(l == 1)
				anIntArray974[sessionNpcsAwaitingUpdate++] = j;
			int i1 = byteStream.method532(402, 1);
			int j1 = byteStream.method532(402, 5);
			if(j1 > 15)
				j1 -= 32;
			class50_sub1_sub4_sub3_sub2.method568(((Mob) (sessionPlayer)).anIntArray1587[0] + j1, (byte) 5, i1 == 1, ((Mob) (sessionPlayer)).anIntArray1586[0] + k);
		}
		byteStream.finishBitAccess();
		if(byte0 == 6) {
			byte0 = 0;
			return;
		} else {
			packetOpcode = -1;
			return;
		}
	}

	public static void main(String args[]) {
		try {
			System.out.println("RS2 user client - release #" + RS_REVISION + "-" + RV_REVISION);
			if(args.length != 5) {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}
			nodeId = Integer.parseInt(args[0]);
			portOffset = Integer.parseInt(args[1]);
			if(args[2].equals("lowmem"))
				method101(true);
			else if(args[2].equals("highmem")) {
				method27(true);
			} else {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}
			if(args[3].equals("free"))
				aBoolean925 = false;
			else if(args[3].equals("members")) {
				aBoolean925 = true;
			} else {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}
			Signlink.storeid = Integer.parseInt(args[4]);
			Signlink.startpriv(InetAddress.getLocalHost());
			Client client1 = new Client();
			client1.createApplication(GameApplet.DEFAULT_WIDTH, GameApplet.DEFAULT_HEIGHT);
			return;
		} catch(Exception exception) {
			return;
		}
	}

	public void method17(byte byte0) {
		aBoolean1320 = true;
		if(byte0 == 4)
			byte0 = 0;
		else
			aClass6ArrayArrayArray1323 = null;
		try {
			long l = System.currentTimeMillis();
			int i = 0;
			int j = 20;
			while(aBoolean1243) {
				anInt1101++;
				method81((byte) 1);
				method81((byte) 1);
				method98(47);
				if(++i > 10) {
					long l1 = System.currentTimeMillis();
					int k = (int) (l1 - l) / 10 - j;
					j = 40 - k;
					if(j < 5)
						j = 5;
					i = 0;
					l = l1;
				}
				try {
					Thread.sleep(j);
				} catch(Exception _ex) {
				}
			}
		} catch(Exception _ex) {
		}
		aBoolean1320 = false;
	}

	public void method18(byte byte0) {
		if(byte0 != 3)
			return;
		for(Class50_Sub2 class50_sub2 = (Class50_Sub2) aClass6_1261.method158(); class50_sub2 != null; class50_sub2 = (Class50_Sub2) aClass6_1261.method160(1))
			if(class50_sub2.anInt1390 == -1) {
				class50_sub2.anInt1395 = 0;
				method140((byte) -61, class50_sub2);
			} else {
				class50_sub2.method442();
			}

	}

	public void method19(String s) {
		System.out.println(s);
		try {
			getAppletContext().showDocument(new URL(getCodeBase(), "loaderror_" + s + ".html"));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		do
			try {
				Thread.sleep(1000L);
			} catch(Exception _ex) {
			}
		while(true);
	}

	public static String formatMoney(int i) {
		if(i < 0x186a0)
			return String.valueOf(i);
		if(i < 0x989680)
			return i / 1000 + "K";
		else
			return i / 0xf4240 + "M";
	}

	public void cleanUp() {
		sessionPlayers = null;
		sessionPlayerList = null;
		anIntArray974 = null;
		playerUpdateStreams = null;
		anIntArray1295 = null;
		aClass18_906 = null;
		aClass18_907 = null;
		aClass18_908 = null;
		aClass18_909 = null;
		redstone0 = null;
		redstone1_2 = null;
		redstone3 = null;
		redstone6 = null;
		redstone4_5 = null;
		redstone7 = null;
		redstone8_9 = null;
		redstone10 = null;
		redstone13 = null;
		redstone11_12 = null;
		aStringArray849 = null;
		aLongArray1130 = null;
		anIntArray1267 = null;
		aClass18_1108 = null;
		aClass18_1109 = null;
		aClass18_1110 = null;
		anIntArray1039 = null;
		anIntArray856 = null;
		aByteArrayArray838 = null;
		aByteArrayArray1232 = null;
		anIntArray857 = null;
		anIntArray858 = null;
		aClass18_1203 = null;
		aClass18_1204 = null;
		aClass18_1205 = null;
		aClass18_1206 = null;
		anIntArrayArray885 = null;
		anIntArrayArray1189 = null;
		anIntArray1123 = null;
		anIntArray1124 = null;
		mapDotItem = null;
		mapDotNpc = null;
		mapDotPlayer = null;
		mapDotFriend = null;
		mapDotTeam = null;
		if(aClass7_1248 != null)
			aClass7_1248.aBoolean131 = false;
		aClass7_1248 = null;
		backbase1 = null;
		backbase2 = null;
		backhmid1 = null;
		aClass18_910 = null;
		backvmid1 = null;
		aClass18_912 = null;
		aClass18_913 = null;
		aClass18_914 = null;
		anIntArrayArrayArray891 = null;
		aByteArrayArrayArray1125 = null;
		aClass22_1164 = null;
		aClass46Array1260 = null;
		minimapImage = null;
		aClass18_1201 = null;
		aClass18_1202 = null;
		aClass18_1198 = null;
		aClass18_1199 = null;
		aClass18_1200 = null;
		compassImage = null;
		hitmarks = null;
		pkHeadicons = null;
		prayerHeadicons = null;
		hintHeadicons = null;
		cross = null;
		method50(false);
		byteStream2 = null;
		byteStream1 = null;
		byteStream4 = null;
		tabBackDrawingArea = null;
		mapDrawingArea = null;
		gameScreenDrawingArea = null;
		aClass18_1159 = null;
		invback = null;
		mapback = null;
		chatback = null;
		try {
			if(aClass17_1024 != null)
				aClass17_1024.method224();
		} catch(Exception _ex) {
		}
		aClass17_1024 = null;
		markPosX = null;
		markPosY = null;
		mapMarkImage = null;
		sessionNpcs = null;
		sessionNpcList = null;
		aByteArray1245 = null;
		byteStream3 = null;
		mapscenes = null;
		mapfunctions = null;
		anIntArrayArray886 = null;
		sideicons = null;
		aClass6_1282 = null;
		aClass6_1210 = null;
		multiwayOverlay = null;
		if(aClass32_Sub1_1291 != null)
			aClass32_Sub1_1291.method339();
		aClass32_Sub1_1291 = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionId = null;
		menuActionCmd1 = null;
		menuActionName = null;
		aClass6ArrayArrayArray1323 = null;
		aClass6_1261 = null;
		dataOrbBg = null;
		dataOrbFills = null;
		dataOrbIcons = null;
		method141(28614);
		Class47.method433(false);
		NPCDefinition.clearCache();
		ItemDefinition.method222(false);
		GameInterface.cleanUp();
		Class15.aClass15Array314 = null;
		IdentityKit.cache = null;
		Class4.aClass4Array103 = null;
		Class14.aClass14Array293 = null;
		Class27.aClass27Array554 = null;
		Class27.aClass33_566 = null;
		Class43.aClass43Array704 = null;
		super.aClass18_15 = null;
		Player.modelCache = null;
		Rasterizer.method492(false);
		Class22.method240(false);
		Model.method573(false);
		Class21.method237(false);
		System.gc();
	}

	public void handleTabButtonClicking() {
		if(super.anInt28 == 1) {
			if(super.clickX >= 539 && super.clickX <= 573 && super.clickY >= 169 && super.clickY < 205 && tabInterfaceIds[0] != -1) {
				tabRepaintRequested = true;
				selectedTab = 0;
				aBoolean950 = true;
			}
			if(super.clickX >= 569 && super.clickX <= 599 && super.clickY >= 168 && super.clickY < 205 && tabInterfaceIds[1] != -1) {
				tabRepaintRequested = true;
				selectedTab = 1;
				aBoolean950 = true;
			}
			if(super.clickX >= 597 && super.clickX <= 627 && super.clickY >= 168 && super.clickY < 205 && tabInterfaceIds[2] != -1) {
				tabRepaintRequested = true;
				selectedTab = 2;
				aBoolean950 = true;
			}
			if(super.clickX >= 625 && super.clickX <= 669 && super.clickY >= 168 && super.clickY < 203 && tabInterfaceIds[3] != -1) {
				tabRepaintRequested = true;
				selectedTab = 3;
				aBoolean950 = true;
			}
			if(super.clickX >= 666 && super.clickX <= 696 && super.clickY >= 168 && super.clickY < 205 && tabInterfaceIds[4] != -1) {
				tabRepaintRequested = true;
				selectedTab = 4;
				aBoolean950 = true;
			}
			if(super.clickX >= 694 && super.clickX <= 724 && super.clickY >= 168 && super.clickY < 205 && tabInterfaceIds[5] != -1) {
				tabRepaintRequested = true;
				selectedTab = 5;
				aBoolean950 = true;
			}
			if(super.clickX >= 722 && super.clickX <= 756 && super.clickY >= 169 && super.clickY < 205 && tabInterfaceIds[6] != -1) {
				tabRepaintRequested = true;
				selectedTab = 6;
				aBoolean950 = true;
			}
			if(super.clickX >= 540 && super.clickX <= 574 && super.clickY >= 466 && super.clickY < 502 && tabInterfaceIds[7] != -1) {
				tabRepaintRequested = true;
				selectedTab = 7;
				aBoolean950 = true;
			}
			if(super.clickX >= 572 && super.clickX <= 602 && super.clickY >= 466 && super.clickY < 503 && tabInterfaceIds[8] != -1) {
				tabRepaintRequested = true;
				selectedTab = 8;
				aBoolean950 = true;
			}
			if(super.clickX >= 599 && super.clickX <= 629 && super.clickY >= 466 && super.clickY < 503 && tabInterfaceIds[9] != -1) {
				tabRepaintRequested = true;
				selectedTab = 9;
				aBoolean950 = true;
			}
			if(super.clickX >= 627 && super.clickX <= 671 && super.clickY >= 467 && super.clickY < 502 && tabInterfaceIds[10] != -1) {
				tabRepaintRequested = true;
				selectedTab = 10;
				aBoolean950 = true;
			}
			if(super.clickX >= 669 && super.clickX <= 699 && super.clickY >= 466 && super.clickY < 503 && tabInterfaceIds[11] != -1) {
				tabRepaintRequested = true;
				selectedTab = 11;
				aBoolean950 = true;
			}
			if(super.clickX >= 696 && super.clickX <= 726 && super.clickY >= 466 && super.clickY < 503 && tabInterfaceIds[12] != -1) {
				tabRepaintRequested = true;
				selectedTab = 12;
				aBoolean950 = true;
			}
			if(super.clickX >= 724 && super.clickX <= 758 && super.clickY >= 466 && super.clickY < 502 && tabInterfaceIds[13] != -1) {
				tabRepaintRequested = true;
				selectedTab = 13;
				aBoolean950 = true;
			}
		}
	}

	public void method22(int i) {
		i = 61 / i;
		try {
			int j = ((Mob) (sessionPlayer)).anInt1610 + anInt853;
			int k = ((Mob) (sessionPlayer)).anInt1611 + anInt1009;
			if(anInt1262 - j < -500 || anInt1262 - j > 500 || anInt1263 - k < -500 || anInt1263 - k > 500) {
				anInt1262 = j;
				anInt1263 = k;
			}
			if(anInt1262 != j)
				anInt1262 += (j - anInt1262) / 16;
			if(anInt1263 != k)
				anInt1263 += (k - anInt1263) / 16;
			if(super.anIntArray32[1] == 1)
				anInt1253 += (-24 - anInt1253) / 2;
			else if(super.anIntArray32[2] == 1)
				anInt1253 += (24 - anInt1253) / 2;
			else
				anInt1253 /= 2;
			if(super.anIntArray32[3] == 1)
				anInt1254 += (12 - anInt1254) / 2;
			else if(super.anIntArray32[4] == 1)
				anInt1254 += (-12 - anInt1254) / 2;
			else
				anInt1254 /= 2;
			cameraX = cameraX + anInt1253 / 2 & 0x7ff;
			anInt1251 += anInt1254 / 2;
			if(anInt1251 < 128)
				anInt1251 = 128;
			if(anInt1251 > 383)
				anInt1251 = 383;
			int l = anInt1262 >> 7;
			int i1 = anInt1263 >> 7;
			int j1 = method110(anInt1263, anInt1262, (byte) 9, anInt1091);
			int k1 = 0;
			if(l > 3 && i1 > 3 && l < 100 && i1 < 100) {
				for(int l1 = l - 4; l1 <= l + 4; l1++) {
					for(int j2 = i1 - 4; j2 <= i1 + 4; j2++) {
						int k2 = anInt1091;
						if(k2 < 3 && (aByteArrayArrayArray1125[1][l1][j2] & 2) == 2)
							k2++;
						int l2 = j1 - anIntArrayArrayArray891[k2][l1][j2];
						if(l2 > k1)
							k1 = l2;
					}

				}

			}
			int i2 = k1 * 192;
			if(i2 > 0x17f00)
				i2 = 0x17f00;
			if(i2 < 32768)
				i2 = 32768;
			if(i2 > anInt1289) {
				anInt1289 += (i2 - anInt1289) / 24;
				return;
			}
			if(i2 < anInt1289) {
				anInt1289 += (i2 - anInt1289) / 80;
				return;
			}
		} catch(Exception _ex) {
			Signlink.reporterror("glfc_ex " + ((Mob) (sessionPlayer)).anInt1610 + "," + ((Mob) (sessionPlayer)).anInt1611 + "," + anInt1262 + "," + anInt1263 + "," + anInt889 + "," + anInt890 + ","
					+ anInt1040 + "," + anInt1041);
			throw new RuntimeException("eek");
		}
	}

	public boolean method23(GameInterface class13, int i) {
		i = 98 / i;
		int j = class13.contentType;
		if(j >= 1 && j <= 200 || j >= 701 && j <= 900) {
			if(j >= 801)
				j -= 701;
			else if(j >= 701)
				j -= 601;
			else if(j >= 101)
				j -= 101;
			else
				j--;
			menuActionName[menuActionIndex] = "Remove @whi@" + aStringArray849[j];
			menuActionId[menuActionIndex] = 775;
			menuActionIndex++;
			menuActionName[menuActionIndex] = "Message @whi@" + aStringArray849[j];
			menuActionId[menuActionIndex] = 984;
			menuActionIndex++;
			return true;
		}
		if(j >= 401 && j <= 500) {
			menuActionName[menuActionIndex] = "Remove @whi@" + class13.text;
			menuActionId[menuActionIndex] = 859;
			menuActionIndex++;
			return true;
		} else {
			return false;
		}
	}

	public void method24(boolean flag, byte abyte0[], int i) {
		if(!aBoolean1266) {
			return;
		} else {
			Signlink.midifade = flag ? 1 : 0;
			Signlink.midisave(abyte0, abyte0.length);
			i = 71 / i;
			return;
		}
	}

	public void method25(int i) {
		if(i != 0)
			byteStream2.putByte(186);
		aBoolean1277 = true;
		for(int j = 0; j < 7; j++) {
			anIntArray1326[j] = -1;
			for(int k = 0; k < IdentityKit.anInt814; k++) {
				if(IdentityKit.cache[k].aBoolean821 || IdentityKit.cache[k].anInt816 != j + (aBoolean1144 ? 0 : 7))
					continue;
				anIntArray1326[j] = k;
				break;
			}

		}

	}

	public void method26(int i, int j) {
		NodeList class6 = aClass6ArrayArrayArray1323[anInt1091][i][j];
		if(class6 == null) {
			aClass22_1164.method262(anInt1091, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Object obj = null;
		for(Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1 = (Class50_Sub1_Sub4_Sub1) class6.method158(); class50_sub1_sub4_sub1 != null; class50_sub1_sub4_sub1 = (Class50_Sub1_Sub4_Sub1) class6
				.method160(1)) {
			ItemDefinition class16 = ItemDefinition.forId(class50_sub1_sub4_sub1.anInt1550);
			int l = class16.value;
			if(class16.stackable)
				l *= class50_sub1_sub4_sub1.anInt1552 + 1;
			if(l > k) {
				k = l;
				obj = class50_sub1_sub4_sub1;
			}
		}

		class6.method156((byte) -57, ((Class50) (obj)));
		Object obj1 = null;
		Object obj2 = null;
		for(Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1_1 = (Class50_Sub1_Sub4_Sub1) class6.method158(); class50_sub1_sub4_sub1_1 != null; class50_sub1_sub4_sub1_1 = (Class50_Sub1_Sub4_Sub1) class6
				.method160(1)) {
			if(class50_sub1_sub4_sub1_1.anInt1550 != ((Class50_Sub1_Sub4_Sub1) (obj)).anInt1550 && obj1 == null)
				obj1 = class50_sub1_sub4_sub1_1;
			if(class50_sub1_sub4_sub1_1.anInt1550 != ((Class50_Sub1_Sub4_Sub1) (obj)).anInt1550 && class50_sub1_sub4_sub1_1.anInt1550 != ((Class50_Sub1_Sub4_Sub1) (obj1)).anInt1550 && obj2 == null)
				obj2 = class50_sub1_sub4_sub1_1;
		}

		int i1 = i + (j << 7) + 0x60000000;
		aClass22_1164.method248(method110(j * 128 + 64, i * 128 + 64, (byte) 9, anInt1091), anInt1091, ((Class50_Sub1_Sub4) (obj)), ((Class50_Sub1_Sub4) (obj1)), i1, ((Class50_Sub1_Sub4) (obj2)), 2,
				j, i);
	}

	public static void method27(boolean flag) {
		Class22.aBoolean451 = false;
		Rasterizer.aBoolean1527 = false;
		aBoolean926 = false;
		Class8.aBoolean169 = false;
		Class47.aBoolean772 = false;
	}

	public void method28(byte byte0) {
		if(anInt1057 > 1)
			anInt1057--;
		if(anInt873 > 0)
			anInt873--;
		for(int i = 0; i < 5; i++)
			if(!handleOpcodes())
				break;

		if(!loggedIn)
			return;
		synchronized(aClass7_1248.anObject133) {
			if(aBoolean962) {
				if(super.anInt28 != 0 || aClass7_1248.anInt136 >= 40) {
					byteStream2.createFrame(171);
					byteStream2.putByte(0);
					int i2 = byteStream2.position;
					int i3 = 0;
					for(int i4 = 0; i4 < aClass7_1248.anInt136; i4++) {
						if(i2 - byteStream2.position >= 240)
							break;
						i3++;
						int k4 = aClass7_1248.anIntArray132[i4];
						if(k4 < 0)
							k4 = 0;
						else if(k4 > 502)
							k4 = 502;
						int j5 = aClass7_1248.anIntArray137[i4];
						if(j5 < 0)
							j5 = 0;
						else if(j5 > 764)
							j5 = 764;
						int l5 = k4 * 765 + j5;
						if(aClass7_1248.anIntArray132[i4] == -1 && aClass7_1248.anIntArray137[i4] == -1) {
							j5 = -1;
							k4 = -1;
							l5 = 0x7ffff;
						}
						if(j5 == anInt1011 && k4 == anInt1012) {
							if(anInt1299 < 2047)
								anInt1299++;
						} else {
							int i6 = j5 - anInt1011;
							anInt1011 = j5;
							int j6 = k4 - anInt1012;
							anInt1012 = k4;
							if(anInt1299 < 8 && i6 >= -32 && i6 <= 31 && j6 >= -32 && j6 <= 31) {
								i6 += 32;
								j6 += 32;
								byteStream2.putShort((anInt1299 << 12) + (i6 << 6) + j6);
								anInt1299 = 0;
							} else if(anInt1299 < 8) {
								byteStream2.putTriByte(0x800000 + (anInt1299 << 19) + l5);
								anInt1299 = 0;
							} else {
								byteStream2.putInt(0xc0000000 + (anInt1299 << 19) + l5);
								anInt1299 = 0;
							}
						}
					}

					byteStream2.putSizeByte(byteStream2.position - i2);
					if(i3 >= aClass7_1248.anInt136) {
						aClass7_1248.anInt136 = 0;
					} else {
						aClass7_1248.anInt136 -= i3;
						for(int l4 = 0; l4 < aClass7_1248.anInt136; l4++) {
							aClass7_1248.anIntArray137[l4] = aClass7_1248.anIntArray137[l4 + i3];
							aClass7_1248.anIntArray132[l4] = aClass7_1248.anIntArray132[l4 + i3];
						}

					}
				}
			} else {
				aClass7_1248.anInt136 = 0;
			}
		}
		if(super.anInt28 != 0) {
			long l = (super.aLong31 - aLong902) / 50L;
			if(l > 4095L)
				l = 4095L;
			aLong902 = super.aLong31;
			int j2 = super.clickY;
			if(j2 < 0)
				j2 = 0;
			else if(j2 > 502)
				j2 = 502;
			int j3 = super.clickX;
			if(j3 < 0)
				j3 = 0;
			else if(j3 > 764)
				j3 = 764;
			int j4 = j2 * 765 + j3;
			int i5 = 0;
			if(super.anInt28 == 2)
				i5 = 1;
			int k5 = (int) l;
			byteStream2.createFrame(19);
			byteStream2.putInt((k5 << 20) + (i5 << 19) + j4);
		}
		if(anInt1264 > 0)
			anInt1264--;
		if(super.anIntArray32[1] == 1 || super.anIntArray32[2] == 1 || super.anIntArray32[3] == 1 || super.anIntArray32[4] == 1)
			aBoolean1265 = true;
		if(aBoolean1265 && anInt1264 <= 0) {
			anInt1264 = 20;
			aBoolean1265 = false;
			byteStream2.createFrame(140);
			byteStream2.writeLEShort(0, anInt1251);
			byteStream2.writeLEShort(0, cameraX);
		}
		if(super.aBoolean19 && !aBoolean1275) {
			aBoolean1275 = true;
			byteStream2.createFrame(187);
			byteStream2.putByte(1);
		}
		if(!super.aBoolean19 && aBoolean1275) {
			aBoolean1275 = false;
			byteStream2.createFrame(187);
			byteStream2.putByte(0);
		}
		method143((byte) -40);
		method36(16220);
		method152(-23763);
		anInt871++;
		if(anInt871 > 750)
			method59(1);
		method100(0);
		method67(-37214);
		method85(0);
		anInt951++;
		if(anInt1023 != 0) {
			anInt1022 += 20;
			if(anInt1022 >= 400)
				anInt1023 = 0;
		}
		if(anInt1332 != 0) {
			anInt1329++;
			if(anInt1329 >= 15) {
				if(anInt1332 == 2)
					tabRepaintRequested = true;
				if(anInt1332 == 3)
					aBoolean1240 = true;
				anInt1332 = 0;
			}
		}
		if(anInt1113 != 0) {
			anInt1269++;
			if(super.mouseX > anInt1114 + 5 || super.mouseX < anInt1114 - 5 || super.mouseY > anInt1115 + 5 || super.mouseY < anInt1115 - 5)
				aBoolean1155 = true;
			if(super.anInt21 == 0) {
				if(anInt1113 == 2)
					tabRepaintRequested = true;
				if(anInt1113 == 3)
					aBoolean1240 = true;
				anInt1113 = 0;
				if(aBoolean1155 && anInt1269 >= 5) {
					anInt1064 = -1;
					method91(-521);
					if(anInt1064 == anInt1111 && anInt1063 != anInt1112) {
						GameInterface class13 = GameInterface.getInterface(anInt1111);
						int i1 = 0;
						if(anInt955 == 1 && class13.contentType == 206)
							i1 = 1;
						if(class13.anIntArray269[anInt1063] <= 0)
							i1 = 0;
						if(class13.aBoolean217) {
							int k2 = anInt1112;
							int k3 = anInt1063;
							class13.anIntArray269[k3] = class13.anIntArray269[k2];
							class13.anIntArray224[k3] = class13.anIntArray224[k2];
							class13.anIntArray269[k2] = -1;
							class13.anIntArray224[k2] = 0;
						} else if(i1 == 1) {
							int l2 = anInt1112;
							for(int l3 = anInt1063; l2 != l3;)
								if(l2 > l3) {
									class13.method196(l2 - 1, -291, l2);
									l2--;
								} else if(l2 < l3) {
									class13.method196(l2 + 1, -291, l2);
									l2++;
								}

						} else {
							class13.method196(anInt1063, -291, anInt1112);
						}
						byteStream2.createFrame(123);
						byteStream2.method548(3, anInt1063);
						byteStream2.method537(false, i1);
						byteStream2.writeShortA(anInt1111, 0);
						byteStream2.writeLEShort(0, anInt1112);
					}
				} else if((anInt1300 == 1 || method126(menuActionIndex - 1, aByte1161)) && menuActionIndex > 2)
					method108(811);
				else if(menuActionIndex > 0)
					method120(menuActionIndex - 1, 8);
				anInt1329 = 10;
				super.anInt28 = 0;
			}
		}
		if(Class22.anInt485 != -1) {
			int j = Class22.anInt485;
			int j1 = Class22.anInt486;
			boolean flag = method35(true, false, j1, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 0, 0, j, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			Class22.anInt485 = -1;
			if(flag) {
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 1;
				anInt1022 = 0;
			}
		}
		if(super.anInt28 == 1 && aString1058 != null) {
			aString1058 = null;
			aBoolean1240 = true;
			super.anInt28 = 0;
		}
		method54(0);
		if(anInt1053 == -1) {
			method146((byte) 4);
			handleTabButtonClicking();
			handleChatButtonClicking();
		}
		if(super.anInt21 == 1 || super.anInt28 == 1)
			anInt1094++;
		if(anInt1284 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if(tooltipTimer < GameInterface.TOOLTIP_DELAY) {
				tooltipTimer++;
				if(tooltipTimer == GameInterface.TOOLTIP_DELAY) {
					if(anInt1284 != 0)
						aBoolean1240 = true;
					if(anInt1044 != 0)
						tabRepaintRequested = true;
				}
			}
		} else if(tooltipTimer > 0)
			tooltipTimer--;
		if(loadingStage == 2)
			method22(409);
		if(loadingStage == 2 && aBoolean1211)
			method29(aBoolean959);
		for(int k = 0; k < 5; k++)
			anIntArray1145[k]++;

		method30((byte) 2);
		super.anInt20++;
		if(super.anInt20 > 4500) {
			anInt873 = 250;
			super.anInt20 -= 500;
			byteStream2.createFrame(202);
		}
		anInt1118++;
		if(anInt1118 > 500) {
			anInt1118 = 0;
			int k1 = (int) (Math.random() * 8D);
			if((k1 & 1) == 1)
				anInt853 += anInt854;
			if((k1 & 2) == 2)
				anInt1009 += anInt1010;
			if((k1 & 4) == 4)
				anInt1255 += anInt1256;
		}
		if(anInt853 < -50)
			anInt854 = 2;
		if(anInt853 > 50)
			anInt854 = -2;
		if(anInt1009 < -55)
			anInt1010 = 2;
		if(anInt1009 > 55)
			anInt1010 = -2;
		if(anInt1255 < -40)
			anInt1256 = 1;
		if(anInt1255 > 40)
			anInt1256 = -1;
		anInt1045++;
		if(anInt1045 > 500) {
			anInt1045 = 0;
			int l1 = (int) (Math.random() * 8D);
			if((l1 & 1) == 1)
				minimapRotation += anInt917;
			if((l1 & 2) == 2)
				minimapZoom += anInt1234;
		}
		if(minimapRotation < -60)
			anInt917 = 2;
		if(minimapRotation > 60)
			anInt917 = -2;
		if(minimapZoom < -20)
			anInt1234 = 1;
		if(minimapZoom > 10)
			anInt1234 = -1;
		anInt872++;
		if(byte0 != 4)
			packetOpcode = byteStream4.getUnsignedByte();
		if(anInt872 > 50)
			byteStream2.createFrame(40);
		try {
			if(aClass17_1024 != null && byteStream2.position > 0) {
				aClass17_1024.method228(0, byteStream2.position, 0, byteStream2.payload);
				byteStream2.position = 0;
				anInt872 = 0;
				return;
			}
		} catch(IOException _ex) {
			method59(1);
			return;
		} catch(Exception exception) {
			method124(true);
		}
	}

	public void method29(boolean flag) {
		int i = anInt874 * 128 + 64;
		int j = anInt875 * 128 + 64;
		int k = method110(j, i, (byte) 9, anInt1091) - anInt876;
		if(anInt1216 < i) {
			anInt1216 += anInt877 + ((i - anInt1216) * anInt878) / 1000;
			if(anInt1216 > i)
				anInt1216 = i;
		}
		if(anInt1216 > i) {
			anInt1216 -= anInt877 + ((anInt1216 - i) * anInt878) / 1000;
			if(anInt1216 < i)
				anInt1216 = i;
		}
		if(anInt1217 < k) {
			anInt1217 += anInt877 + ((k - anInt1217) * anInt878) / 1000;
			if(anInt1217 > k)
				anInt1217 = k;
		}
		if(anInt1217 > k) {
			anInt1217 -= anInt877 + ((anInt1217 - k) * anInt878) / 1000;
			if(anInt1217 < k)
				anInt1217 = k;
		}
		if(anInt1218 < j) {
			anInt1218 += anInt877 + ((j - anInt1218) * anInt878) / 1000;
			if(anInt1218 > j)
				anInt1218 = j;
		}
		if(anInt1218 > j) {
			anInt1218 -= anInt877 + ((anInt1218 - j) * anInt878) / 1000;
			if(anInt1218 < j)
				anInt1218 = j;
		}
		i = anInt993 * 128 + 64;
		j = anInt994 * 128 + 64;
		k = method110(j, i, (byte) 9, anInt1091) - anInt995;
		int l = i - anInt1216;
		int i1 = k - anInt1217;
		int j1 = j - anInt1218;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		if(!flag) {
			for(int i2 = 1; i2 > 0; i2++)
				;
		}
		int j2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if(l1 < 128)
			l1 = 128;
		if(l1 > 383)
			l1 = 383;
		if(anInt1219 < l1) {
			anInt1219 += anInt996 + ((l1 - anInt1219) * anInt997) / 1000;
			if(anInt1219 > l1)
				anInt1219 = l1;
		}
		if(anInt1219 > l1) {
			anInt1219 -= anInt996 + ((anInt1219 - l1) * anInt997) / 1000;
			if(anInt1219 < l1)
				anInt1219 = l1;
		}
		int k2 = j2 - anInt1220;
		if(k2 > 1024)
			k2 -= 2048;
		if(k2 < -1024)
			k2 += 2048;
		if(k2 > 0) {
			anInt1220 += anInt996 + (k2 * anInt997) / 1000;
			anInt1220 &= 0x7ff;
		}
		if(k2 < 0) {
			anInt1220 -= anInt996 + (-k2 * anInt997) / 1000;
			anInt1220 &= 0x7ff;
		}
		int l2 = j2 - anInt1220;
		if(l2 > 1024)
			l2 -= 2048;
		if(l2 < -1024)
			l2 += 2048;
		if(l2 < 0 && k2 > 0 || l2 > 0 && k2 < 0)
			anInt1220 = j2;
	}

	public void method30(byte byte0) {
		if(byte0 == 2)
			byte0 = 0;
		else
			return;
		do {
			int i = method5(-983);
			if(i == -1)
				break;
			if(anInt1169 != -1 && anInt1169 == anInt1231) {
				if(i == 8 && aString839.length() > 0)
					aString839 = aString839.substring(0, aString839.length() - 1);
				if((i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57 || i == 32) && aString839.length() < 12)
					aString839 += (char) i;
			} else if(aBoolean866) {
				if(i >= 32 && i <= 122 && aString1026.length() < 80) {
					aString1026 += (char) i;
					aBoolean1240 = true;
				}
				if(i == 8 && aString1026.length() > 0) {
					aString1026 = aString1026.substring(0, aString1026.length() - 1);
					aBoolean1240 = true;
				}
				if(i == 13 || i == 10) {
					aBoolean866 = false;
					aBoolean1240 = true;
					if(anInt1221 == 1) {
						long l = NameUtils.nameToLong(aString1026);
						method102(l, -45229);
					}
					if(anInt1221 == 2 && anInt859 > 0) {
						long l1 = NameUtils.nameToLong(aString1026);
						method53(l1, 0);
					}
					if(anInt1221 == 3 && aString1026.length() > 0) {
						byteStream2.createFrame(227);
						byteStream2.putByte(0);
						int j = byteStream2.position;
						byteStream2.putLong(aLong1141);
						Class31.method321(aString1026, 569, byteStream2);
						byteStream2.putSizeByte(byteStream2.position - j);
						aString1026 = Class31.method322((byte) 0, aString1026);
						aString1026 = Censor.method383((byte) 0, aString1026);
						sendChatboxMessage(NameUtils.formatName(NameUtils.longToName(aLong1141)), aString1026, 6);
						if(anInt887 == 2) {
							anInt887 = 1;
							aBoolean1212 = true;
							byteStream2.createFrame(176);
							byteStream2.putByte(anInt1006);
							byteStream2.putByte(anInt887);
							byteStream2.putByte(anInt1227);
						}
					}
					if(anInt1221 == 4 && anInt855 < 100) {
						long l2 = NameUtils.nameToLong(aString1026);
						method90(anInt1154, l2);
					}
					if(anInt1221 == 5 && anInt855 > 0) {
						long l3 = NameUtils.nameToLong(aString1026);
						method97(325, l3);
					}
				}
			} else if(anInt1244 == 1) {
				if(i >= 48 && i <= 57 && aString949.length() < 10) {
					aString949 += (char) i;
					aBoolean1240 = true;
				}
				if(i == 8 && aString949.length() > 0) {
					aString949 = aString949.substring(0, aString949.length() - 1);
					aBoolean1240 = true;
				}
				if(i == 13 || i == 10) {
					if(aString949.length() > 0) {
						int k = 0;
						try {
							k = Integer.parseInt(aString949);
						} catch(Exception _ex) {
						}
						byteStream2.createFrame(75);
						byteStream2.putInt(k);
					}
					anInt1244 = 0;
					aBoolean1240 = true;
				}
			} else if(anInt1244 == 2) {
				if(i >= 32 && i <= 122 && aString949.length() < 12) {
					aString949 += (char) i;
					aBoolean1240 = true;
				}
				if(i == 8 && aString949.length() > 0) {
					aString949 = aString949.substring(0, aString949.length() - 1);
					aBoolean1240 = true;
				}
				if(i == 13 || i == 10) {
					if(aString949.length() > 0) {
						byteStream2.createFrame(206);
						byteStream2.putLong(NameUtils.nameToLong(aString949));
					}
					anInt1244 = 0;
					aBoolean1240 = true;
				}
			} else if(anInt1244 == 3) {
				if(i >= 32 && i <= 122 && aString949.length() < 40) {
					aString949 += (char) i;
					aBoolean1240 = true;
				}
				if(i == 8 && aString949.length() > 0) {
					aString949 = aString949.substring(0, aString949.length() - 1);
					aBoolean1240 = true;
				}
			} else if(anInt988 == -1 && anInt1053 == -1) {
				if(i >= 32 && i <= 122 && aString1104.length() < 80) {
					aString1104 += (char) i;
					aBoolean1240 = true;
				}
				if(i == 8 && aString1104.length() > 0) {
					aString1104 = aString1104.substring(0, aString1104.length() - 1);
					aBoolean1240 = true;
				}
				if((i == 13 || i == 10) && aString1104.length() > 0) {
					if(anInt867 == 2) {
						if(aString1104.equals("::clientdrop"))
							method59(1);
						if(aString1104.equals("::lag"))
							method138(false);
						if(aString1104.equals("::prefetchmusic")) {
							for(int i1 = 0; i1 < aClass32_Sub1_1291.method340(2, -31140); i1++)
								aClass32_Sub1_1291.method327(-44, 2, (byte) 1, i1);

						}
						if(aString1104.equals("::fpson"))
							aBoolean868 = true;
						if(aString1104.equals("::fpsoff"))
							aBoolean868 = false;
						if(aString1104.equals("::noclip")) {
							for(int j1 = 0; j1 < 4; j1++) {
								for(int k1 = 1; k1 < 103; k1++) {
									for(int j2 = 1; j2 < 103; j2++)
										aClass46Array1260[j1].anIntArrayArray757[k1][j2] = 0;

								}

							}

						}
						if(aString1104.equals("::npcdump")) {
							NPCDefinition.dump();
						}
						if(aString1104.equals("::npcpack")) {
							try {
								NPCDefinition.repack();
							} catch(IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(aString1104.equals("::itemdump")) {
							ItemDefinition.dump();
						}
						if(aString1104.equals("::itempack")) {
							ItemDefinition.repack();
						}
					}
					if(aString1104.startsWith("::")) {
						byteStream2.createFrame(56);
						byteStream2.putByte(aString1104.length() - 1);
						byteStream2.writeString(aString1104.substring(2));
					} else {
						String s = aString1104.toLowerCase();
						int i2 = 0;
						if(s.startsWith("yellow:")) {
							i2 = 0;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("red:")) {
							i2 = 1;
							aString1104 = aString1104.substring(4);
						} else if(s.startsWith("green:")) {
							i2 = 2;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("cyan:")) {
							i2 = 3;
							aString1104 = aString1104.substring(5);
						} else if(s.startsWith("purple:")) {
							i2 = 4;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("white:")) {
							i2 = 5;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("flash1:")) {
							i2 = 6;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("flash2:")) {
							i2 = 7;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("flash3:")) {
							i2 = 8;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("glow1:")) {
							i2 = 9;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("glow2:")) {
							i2 = 10;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("glow3:")) {
							i2 = 11;
							aString1104 = aString1104.substring(6);
						}
						s = aString1104.toLowerCase();
						int k2 = 0;
						if(s.startsWith("wave:")) {
							k2 = 1;
							aString1104 = aString1104.substring(5);
						} else if(s.startsWith("wave2:")) {
							k2 = 2;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("shake:")) {
							k2 = 3;
							aString1104 = aString1104.substring(6);
						} else if(s.startsWith("scroll:")) {
							k2 = 4;
							aString1104 = aString1104.substring(7);
						} else if(s.startsWith("slide:")) {
							k2 = 5;
							aString1104 = aString1104.substring(6);
						}
						byteStream2.createFrame(49);
						byteStream2.putByte(0);
						int i3 = byteStream2.position;
						byteStream2.method538((byte) 0, i2);
						byteStream2.method537(false, k2);
						byteStream3.position = 0;
						Class31.method321(aString1104, 569, byteStream3);
						byteStream2.putBytes(byteStream3.payload, 0, byteStream3.position, 0);
						byteStream2.putSizeByte(byteStream2.position - i3);
						aString1104 = Class31.method322((byte) 0, aString1104);
						aString1104 = Censor.method383((byte) 0, aString1104);
						sessionPlayer.aString1580 = aString1104;
						sessionPlayer.anInt1583 = i2;
						sessionPlayer.anInt1593 = k2;
						sessionPlayer.anInt1582 = 150;
						if(anInt867 == 2)
							sendChatboxMessage("@cr2@" + sessionPlayer.username, ((Mob) (sessionPlayer)).aString1580, 2);
						else if(anInt867 == 1)
							sendChatboxMessage("@cr1@" + sessionPlayer.username, ((Mob) (sessionPlayer)).aString1580, 2);
						else
							sendChatboxMessage(sessionPlayer.username, ((Mob) (sessionPlayer)).aString1580, 2);
						if(anInt1006 == 2) {
							anInt1006 = 3;
							aBoolean1212 = true;
							byteStream2.createFrame(176);
							byteStream2.putByte(anInt1006);
							byteStream2.putByte(anInt887);
							byteStream2.putByte(anInt1227);
						}
					}
					aString1104 = "";
					aBoolean1240 = true;
				}
			}
		} while(true);
	}

	public DataInputStream method31(String s) throws IOException {
		/*
		 * if(!aBoolean900) if(signlink.mainapp != null) return
		 * signlink.openurl(s); else return new DataInputStream((new
		 * URL(getCodeBase(), s)).openStream());
		 */
		if(aSocket1224 != null) {
			try {
				aSocket1224.close();
			} catch(Exception _ex) {
			}
			aSocket1224 = null;
		}
		aSocket1224 = method32(43595);
		aSocket1224.setSoTimeout(10000);
		java.io.InputStream inputstream = aSocket1224.getInputStream();
		OutputStream outputstream = aSocket1224.getOutputStream();
		outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	public Socket method32(int i) throws IOException {
		if(Signlink.mainapp != null)
			return Signlink.opensocket(i);
		else
			return new Socket(InetAddress.getByName(getCodeBase().getHost()), i);
	}

	public boolean handleOpcodes() {
		if(aClass17_1024 == null)
			return false;
		try {
			int k = aClass17_1024.method226();
			if(k == 0)
				return false;
			if(packetOpcode == -1) {
				aClass17_1024.method227(byteStream4.payload, 0, 1);
				packetOpcode = byteStream4.payload[0] & 0xff;
				if(aISAAC_899 != null)
					packetOpcode = packetOpcode - aISAAC_899.getNextValue() & 0xff;
				packetSize = Class12.PACKET_SIZES[packetOpcode];
				k--;
			}
			if(packetSize == -1)
				if(k > 0) {
					aClass17_1024.method227(byteStream4.payload, 0, 1);
					packetSize = byteStream4.payload[0] & 0xff;
					k--;
				} else {
					return false;
				}
			if(packetSize == -2)
				if(k > 1) {
					aClass17_1024.method227(byteStream4.payload, 0, 2);
					byteStream4.position = 0;
					packetSize = byteStream4.getUnsignedShort();
					k -= 2;
				} else {
					return false;
				}
			if(k < packetSize)
				return false;
			byteStream4.position = 0;
			aClass17_1024.method227(byteStream4.payload, 0, packetSize);
			anInt871 = 0;
			anInt905 = anInt904;
			anInt904 = anInt903;
			anInt903 = packetOpcode;
			if(packetOpcode == 166) {
				int l = byteStream4.getLEShort();
				int l10 = byteStream4.getLEShort();
				int k16 = byteStream4.getUnsignedShort();
				GameInterface class13_5 = GameInterface.getInterface(k16);
				class13_5.offsetX = l10;
				class13_5.offsetY = l;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 186) {
				int i1 = byteStream4.getUnsignedShortA();
				int i11 = byteStream4.getUnsignedLEShortA();
				int l16 = byteStream4.getUnsignedShortA();
				int i22 = byteStream4.getUnsignedLEShort();
				GameInterface.getInterface(i11).anInt252 = i1;
				GameInterface.getInterface(i11).anInt253 = i22;
				GameInterface.getInterface(i11).anInt251 = l16;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 216) {
				int j1 = byteStream4.getUnsignedLEShortA();
				int j11 = byteStream4.getUnsignedLEShortA();
				GameInterface.getInterface(j11).anInt283 = 1;
				GameInterface.getInterface(j11).anInt284 = j1;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 26) {
				int k1 = byteStream4.getUnsignedShort();
				int k11 = byteStream4.getUnsignedByte();
				int i17 = byteStream4.getUnsignedShort();
				if(i17 == 65535) {
					if(anInt1035 < 50) {
						anIntArray1090[anInt1035] = (short) k1;
						anIntArray1321[anInt1035] = k11;
						anIntArray1259[anInt1035] = 0;
						anInt1035++;
					}
				} else if(aBoolean1301 && !aBoolean926 && anInt1035 < 50) {
					anIntArray1090[anInt1035] = k1;
					anIntArray1321[anInt1035] = k11;
					anIntArray1259[anInt1035] = i17 + Class38.anIntArray669[k1];
					anInt1035++;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 182) {
				int l1 = byteStream4.getUnsignedShortA();
				byte byte0 = byteStream4.method545(43428);
				anIntArray1005[l1] = byte0;
				if(anIntArray1039[l1] != byte0) {
					anIntArray1039[l1] = byte0;
					method105(0, l1);
					tabRepaintRequested = true;
					if(anInt1191 != -1)
						aBoolean1240 = true;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 13) {
				for(int i2 = 0; i2 < sessionPlayers.length; i2++)
					if(sessionPlayers[i2] != null)
						sessionPlayers[i2].animation = -1;

				for(int l11 = 0; l11 < sessionNpcs.length; l11++)
					if(sessionNpcs[l11] != null)
						sessionNpcs[l11].animation = -1;

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 156) {
				minimapLock = byteStream4.getUnsignedByte();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 162) {
				/*
				 * int j2 = byteStream4.getUnsignedShortA(); int i12 =
				 * byteStream4.getUnsignedLEShort();
				 * GameInterface.getInterface(i12).anInt283 = 2;
				 * GameInterface.getInterface(i12).anInt284 = j2; packetOpcode =
				 * -1; return true;
				 */
				// int i6 = byteStream4.getUnsignedLEShortA();
				int j2 = byteStream4.getUnsignedShortA();
				int i12 = byteStream4.getUnsignedLEShort();
				GameInterface.chatNPC = sessionNpcs[j2];
				GameInterface.getInterface(i12).anInt283 = 2;
				GameInterface.getInterface(i12).anInt284 = (GameInterface.chatNPC.appearanceColours[0] << 25) + (GameInterface.chatNPC.appearanceColours[4] << 20)
						+ (GameInterface.chatNPC.appearanceModels[0] << 15) + (GameInterface.chatNPC.appearanceModels[8] << 10) + (GameInterface.chatNPC.appearanceModels[11] << 5)
						+ GameInterface.chatNPC.appearanceModels[1];
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 109) {
				int k2 = byteStream4.getUnsignedShort();
				method112((byte) 36, k2);
				if(anInt1089 != -1) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = -1;
					tabRepaintRequested = true;
					aBoolean950 = true;
				}
				if(anInt1053 != -1) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = -1;
					repaintRequested = true;
				}
				if(anInt960 != -1) {
					method44(aBoolean1190, anInt960);
					anInt960 = -1;
				}
				if(anInt1169 != -1) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = -1;
				}
				if(anInt988 != k2) {
					method44(aBoolean1190, anInt988);
					anInt988 = k2;
				}
				aBoolean1239 = false;
				aBoolean1240 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 220) {
				int l2 = byteStream4.getUnsignedLEShortA();
				if(l2 == 65535)
					l2 = -1;
				if(l2 != anInt1327 && aBoolean1266 && !aBoolean926 && anInt1128 == 0) {
					anInt1270 = l2;
					aBoolean1271 = true;
					aClass32_Sub1_1291.method329(2, anInt1270);
				}
				anInt1327 = l2;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 249) {
				int i3 = byteStream4.getUnsignedLEShort();
				int j12 = byteStream4.method554(-737);
				if(aBoolean1266 && !aBoolean926) {
					anInt1270 = i3;
					aBoolean1271 = false;
					aClass32_Sub1_1291.method329(2, anInt1270);
					anInt1128 = j12;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 158) {
				int j3 = byteStream4.getLEShort();
				if(j3 != anInt1191) {
					method44(aBoolean1190, anInt1191);
					anInt1191 = j3;
				}
				aBoolean1240 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 218) {
				int k3 = byteStream4.getUnsignedShort();
				int k12 = byteStream4.getUnsignedShortA();
				int j17 = k12 >> 10 & 0x1f;
				int j22 = k12 >> 5 & 0x1f;
				int l24 = k12 & 0x1f;
				GameInterface.getInterface(k3).textColour = (j17 << 19) + (j22 << 11) + (l24 << 3);
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 157) {
				int l3 = byteStream4.method541(-34545);
				String s2 = byteStream4.getRS2String();
				int k17 = byteStream4.getUnsignedByte();
				if(l3 >= 1 && l3 <= 5) {
					if(s2.equalsIgnoreCase("null"))
						s2 = null;
					aStringArray1069[l3 - 1] = s2;
					aBooleanArray1070[l3 - 1] = k17 == 0;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 6) {
				aBoolean866 = false;
				anInt1244 = 2;
				aString949 = "";
				aBoolean1240 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 201) {
				anInt1006 = byteStream4.getUnsignedByte();
				anInt887 = byteStream4.getUnsignedByte();
				anInt1227 = byteStream4.getUnsignedByte();
				aBoolean1212 = true;
				aBoolean1240 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 199) // hint
			{
				anInt1197 = byteStream4.getUnsignedByte();
				if(anInt1197 == 1)
					anInt1226 = byteStream4.getUnsignedShort();
				if(anInt1197 >= 2 && anInt1197 <= 6) {
					if(anInt1197 == 2) {
						anInt847 = 64;
						anInt848 = 64;
					}
					if(anInt1197 == 3) {
						anInt847 = 0;
						anInt848 = 64;
					}
					if(anInt1197 == 4) {
						anInt847 = 128;
						anInt848 = 64;
					}
					if(anInt1197 == 5) {
						anInt847 = 64;
						anInt848 = 0;
					}
					if(anInt1197 == 6) {
						anInt847 = 64;
						anInt848 = 128;
					}
					anInt1197 = 2;
					anInt844 = byteStream4.getUnsignedShort();
					anInt845 = byteStream4.getUnsignedShort();
					anInt846 = byteStream4.getUnsignedByte();
				}
				if(anInt1197 == 10)
					anInt1151 = byteStream4.getUnsignedShort();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 167) {
				aBoolean1211 = true;
				anInt993 = byteStream4.getUnsignedByte();
				anInt994 = byteStream4.getUnsignedByte();
				anInt995 = byteStream4.getUnsignedShort();
				anInt996 = byteStream4.getUnsignedByte();
				anInt997 = byteStream4.getUnsignedByte();
				if(anInt997 >= 100) {
					int i4 = anInt993 * 128 + 64;
					int l12 = anInt994 * 128 + 64;
					int l17 = method110(l12, i4, (byte) 9, anInt1091) - anInt995;
					int k22 = i4 - anInt1216;
					int i25 = l17 - anInt1217;
					int k27 = l12 - anInt1218;
					int i30 = (int) Math.sqrt(k22 * k22 + k27 * k27);
					anInt1219 = (int) (Math.atan2(i25, i30) * 325.94900000000001D) & 0x7ff;
					anInt1220 = (int) (Math.atan2(k22, k27) * -325.94900000000001D) & 0x7ff;
					if(anInt1219 < 128)
						anInt1219 = 128;
					if(anInt1219 > 383)
						anInt1219 = 383;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 5) {
				method124(true);
				packetOpcode = -1;
				return false;
			}
			if(packetOpcode == 115) {
				int j4 = byteStream4.method557(true);
				System.out.println("Config 2 value = " + j4);
				int i13 = byteStream4.getUnsignedLEShort();
				System.out.println("Config 2 id = " + i13);
				anIntArray1005[i13] = j4;
				if(anIntArray1039[i13] != j4) {
					anIntArray1039[i13] = j4;
					method105(0, i13);
					tabRepaintRequested = true;
					if(anInt1191 != -1)
						aBoolean1240 = true;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 29) {
				if(anInt1089 != -1) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = -1;
					tabRepaintRequested = true;
					aBoolean950 = true;
				}
				if(anInt988 != -1) {
					method44(aBoolean1190, anInt988);
					anInt988 = -1;
					aBoolean1240 = true;
				}
				if(anInt1053 != -1) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = -1;
					repaintRequested = true;
				}
				if(anInt960 != -1) {
					method44(aBoolean1190, anInt960);
					anInt960 = -1;
				}
				if(anInt1169 != -1) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = -1;
				}
				if(anInt1244 != 0) {
					anInt1244 = 0;
					aBoolean1240 = true;
				}
				aBoolean1239 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 76) {
				lastPassChange = byteStream4.getUnsignedLEShort();
				anInt1075 = byteStream4.getUnsignedLEShortA();
				byteStream4.getUnsignedShort();
				anInt1208 = byteStream4.getUnsignedShort();
				anInt1170 = byteStream4.getUnsignedLEShort();
				websiteMessages = byteStream4.getUnsignedShortA();
				anInt1215 = byteStream4.getUnsignedShortA();
				memberDaysLeft = byteStream4.getUnsignedShort();
				anInt1241 = byteStream4.method555(935);
				recoveryQuestionsDate = byteStream4.getUnsignedLEShortA();
				byteStream4.method540(0);
				Signlink.dnslookup(NameUtils.method302(anInt1241, -826));
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 63) {
				String s = byteStream4.getRS2String();
				if(s.endsWith(":tradereq:")) {
					String s3 = s.substring(0, s.indexOf(":"));
					long l18 = NameUtils.nameToLong(s3);
					boolean flag1 = false;
					for(int l27 = 0; l27 < anInt855; l27++) {
						if(aLongArray1073[l27] != l18)
							continue;
						flag1 = true;
						break;
					}

					if(!flag1 && anInt1246 == 0)
						sendChatboxMessage(s3, "wishes to trade with you.", 4);
				} else if(s.endsWith(":duelreq:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					long l19 = NameUtils.nameToLong(s4);
					boolean flag2 = false;
					for(int i28 = 0; i28 < anInt855; i28++) {
						if(aLongArray1073[i28] != l19)
							continue;
						flag2 = true;
						break;
					}

					if(!flag2 && anInt1246 == 0)
						sendChatboxMessage(s4, "wishes to duel with you.", 8);
				} else if(s.endsWith(":chalreq:")) {
					String s5 = s.substring(0, s.indexOf(":"));
					long l20 = NameUtils.nameToLong(s5);
					boolean flag3 = false;
					for(int j28 = 0; j28 < anInt855; j28++) {
						if(aLongArray1073[j28] != l20)
							continue;
						flag3 = true;
						break;
					}

					if(!flag3 && anInt1246 == 0) {
						String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
						sendChatboxMessage(s5, s8, 8);
					}
				} else {
					sendChatboxMessage("", s, 0);
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 50) {
				int k4 = byteStream4.getShort();
				if(k4 >= 0)
					method112((byte) 36, k4);
				if(k4 != anInt1279) {
					method44(aBoolean1190, anInt1279);
					anInt1279 = k4;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 82) {
				boolean flag = byteStream4.getUnsignedByte() == 1;
				int j13 = byteStream4.getUnsignedShort();
				GameInterface.getInterface(j13).mouseOverTriggered = flag;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 174) {
				if(selectedTab == 12)
					tabRepaintRequested = true;
				anInt1030 = byteStream4.getShort();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 233) {
				anInt1319 = byteStream4.getUnsignedByte();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 61) {
				anInt1120 = 0;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 128) {
				int l4 = byteStream4.getUnsignedShortA();
				int k13 = byteStream4.getUnsignedLEShortA();
				if(anInt988 != -1) {
					method44(aBoolean1190, anInt988);
					anInt988 = -1;
					aBoolean1240 = true;
				}
				if(anInt1053 != -1) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = -1;
					repaintRequested = true;
				}
				if(anInt960 != -1) {
					method44(aBoolean1190, anInt960);
					anInt960 = -1;
				}
				if(anInt1169 != l4) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = l4;
				}
				if(anInt1089 != k13) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = k13;
				}
				if(anInt1244 != 0) {
					anInt1244 = 0;
					aBoolean1240 = true;
				}
				tabRepaintRequested = true;
				aBoolean950 = true;
				aBoolean1239 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 67) {
				int i5 = byteStream4.getUnsignedByte();
				int l13 = byteStream4.getUnsignedByte();
				int i18 = byteStream4.getUnsignedByte();
				int l22 = byteStream4.getUnsignedByte();
				aBooleanArray927[i5] = true;
				anIntArray1105[i5] = l13;
				anIntArray852[i5] = i18;
				anIntArray991[i5] = l22;
				anIntArray1145[i5] = 0;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 134) {
				tabRepaintRequested = true;
				int j5 = byteStream4.getUnsignedShort();
				GameInterface class13 = GameInterface.getInterface(j5);
				while(byteStream4.position < packetSize) {
					int j18 = byteStream4.method535();
					int i23 = byteStream4.getUnsignedShort();
					int j25 = byteStream4.getUnsignedByte();
					if(j25 == 255)
						j25 = byteStream4.getInt();
					if(j18 >= 0 && j18 < class13.anIntArray269.length) {
						class13.anIntArray269[j18] = i23;
						class13.anIntArray224[j18] = j25;
					}
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 78) {
				long l5 = byteStream4.getLong();
				int k18 = byteStream4.getUnsignedByte();
				String s7 = NameUtils.formatName(NameUtils.longToName(l5));
				for(int k25 = 0; k25 < anInt859; k25++) {
					if(l5 != aLongArray1130[k25])
						continue;
					if(anIntArray1267[k25] != k18) {
						anIntArray1267[k25] = k18;
						tabRepaintRequested = true;
						if(k18 > 0)
							sendChatboxMessage("", s7 + " has logged in.", 5);
						if(k18 == 0)
							sendChatboxMessage("", s7 + " has logged out.", 5);
					}
					s7 = null;
					break;
				}

				if(s7 != null && anInt859 < 200) {
					aLongArray1130[anInt859] = l5;
					aStringArray849[anInt859] = s7;
					anIntArray1267[anInt859] = k18;
					anInt859++;
					tabRepaintRequested = true;
				}
				for(boolean flag5 = false; !flag5;) {
					flag5 = true;
					for(int j30 = 0; j30 < anInt859 - 1; j30++)
						if(anIntArray1267[j30] != nodeId && anIntArray1267[j30 + 1] == nodeId || anIntArray1267[j30] == 0 && anIntArray1267[j30 + 1] != 0) {
							int l31 = anIntArray1267[j30];
							anIntArray1267[j30] = anIntArray1267[j30 + 1];
							anIntArray1267[j30 + 1] = l31;
							String s10 = aStringArray849[j30];
							aStringArray849[j30] = aStringArray849[j30 + 1];
							aStringArray849[j30 + 1] = s10;
							long l33 = aLongArray1130[j30];
							aLongArray1130[j30] = aLongArray1130[j30 + 1];
							aLongArray1130[j30 + 1] = l33;
							tabRepaintRequested = true;
							flag5 = false;
						}

				}

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 58) {
				aBoolean866 = false;
				anInt1244 = 1;
				aString949 = "";
				aBoolean1240 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 252) {
				selectedTab = byteStream4.method541(-34545);
				tabRepaintRequested = true;
				aBoolean950 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 40) {
				anInt990 = byteStream4.method542(anInt1236);
				anInt989 = byteStream4.method541(-34545);
				for(int k5 = anInt989; k5 < anInt989 + 8; k5++) {
					for(int i14 = anInt990; i14 < anInt990 + 8; i14++)
						if(aClass6ArrayArrayArray1323[anInt1091][k5][i14] != null) {
							aClass6ArrayArrayArray1323[anInt1091][k5][i14] = null;
							method26(k5, i14);
						}

				}

				for(Class50_Sub2 class50_sub2 = (Class50_Sub2) aClass6_1261.method158(); class50_sub2 != null; class50_sub2 = (Class50_Sub2) aClass6_1261.method160(1))
					if(class50_sub2.anInt1393 >= anInt989 && class50_sub2.anInt1393 < anInt989 + 8 && class50_sub2.anInt1394 >= anInt990 && class50_sub2.anInt1394 < anInt990 + 8
							&& class50_sub2.anInt1391 == anInt1091)
						class50_sub2.anInt1390 = 0;

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 255) {
				int i6 = byteStream4.getUnsignedLEShortA();
				GameInterface.getInterface(i6).anInt283 = 3;
				if(sessionPlayer.npcTransformation == null)
					GameInterface.getInterface(i6).anInt284 = (sessionPlayer.appearanceColours[0] << 25) + (sessionPlayer.appearanceColours[4] << 20) + (sessionPlayer.appearanceModels[0] << 15)
							+ (sessionPlayer.appearanceModels[8] << 10) + (sessionPlayer.appearanceModels[11] << 5) + sessionPlayer.appearanceModels[1];
				else
					GameInterface.getInterface(i6).anInt284 = (int) (0x12345678L + sessionPlayer.npcTransformation.id);
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 135) {
				long l6 = byteStream4.getLong();
				int i19 = byteStream4.getInt();
				int j23 = byteStream4.getUnsignedByte();
				boolean flag4 = false;
				for(int k28 = 0; k28 < 100; k28++) {
					if(anIntArray1258[k28] != i19)
						continue;
					flag4 = true;
					break;
				}

				if(j23 <= 1) {
					for(int k30 = 0; k30 < anInt855; k30++) {
						if(aLongArray1073[k30] != l6)
							continue;
						flag4 = true;
						break;
					}

				}
				if(!flag4 && anInt1246 == 0)
					try {
						anIntArray1258[anInt1152] = i19;
						anInt1152 = (anInt1152 + 1) % 100;
						String s9 = Class31.method320(0, byteStream4, packetSize - 13);
						if(j23 != 3)
							s9 = Censor.method383((byte) 0, s9);
						if(j23 == 2 || j23 == 3)
							sendChatboxMessage("@cr2@" + NameUtils.formatName(NameUtils.longToName(l6)), s9, 7);
						else if(j23 == 1)
							sendChatboxMessage("@cr1@" + NameUtils.formatName(NameUtils.longToName(l6)), s9, 7);
						else
							sendChatboxMessage(NameUtils.formatName(NameUtils.longToName(l6)), s9, 3);
					} catch(Exception exception1) {
						Signlink.reporterror("cde1");
					}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 183) {
				anInt989 = byteStream4.getUnsignedByte();
				anInt990 = byteStream4.method540(0);
				while(byteStream4.position < packetSize) {
					int j6 = byteStream4.getUnsignedByte();
					method133(byteStream4, 0, j6);
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 159) {
				int k6 = byteStream4.getUnsignedLEShortA();
				method112((byte) 36, k6);
				if(anInt1089 != -1) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = -1;
					tabRepaintRequested = true;
					aBoolean950 = true;
				}
				if(anInt988 != -1) {
					method44(aBoolean1190, anInt988);
					anInt988 = -1;
					aBoolean1240 = true;
				}
				if(anInt1053 != -1) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = -1;
					repaintRequested = true;
				}
				if(anInt960 != -1) {
					method44(aBoolean1190, anInt960);
					anInt960 = -1;
				}
				if(anInt1169 != k6) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = k6;
				}
				if(anInt1244 != 0) {
					anInt1244 = 0;
					aBoolean1240 = true;
				}
				aBoolean1239 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 246) {
				int i7 = byteStream4.getUnsignedLEShortA();
				method112((byte) 36, i7);
				if(anInt988 != -1) {
					method44(aBoolean1190, anInt988);
					anInt988 = -1;
					aBoolean1240 = true;
				}
				if(anInt1053 != -1) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = -1;
					repaintRequested = true;
				}
				if(anInt960 != -1) {
					method44(aBoolean1190, anInt960);
					anInt960 = -1;
				}
				if(anInt1169 != -1) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = -1;
				}
				if(anInt1089 != i7) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = i7;
				}
				if(anInt1244 != 0) {
					anInt1244 = 0;
					aBoolean1240 = true;
				}
				tabRepaintRequested = true;
				aBoolean950 = true;
				aBoolean1239 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 49) {
				tabRepaintRequested = true;
				int skillId = byteStream4.method541(-34545);
				int level = byteStream4.getUnsignedByte();
				int experience = byteStream4.getInt();
				
				playerExps[skillId] = experience;
				playerLevels[skillId] = level;
				anIntArray1054[skillId] = 1;
				for(int k23 = 0; k23 < 98; k23++)
					if(experience >= anIntArray952[k23])
						anIntArray1054[skillId] = k23 + 2;

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 206) {
				tabRepaintRequested = true;
				int k7 = byteStream4.getUnsignedShort();
				GameInterface class13_1 = GameInterface.getInterface(k7);
				int k19 = byteStream4.getUnsignedShort();
				for(int l23 = 0; l23 < k19; l23++) {
					class13_1.anIntArray269[l23] = byteStream4.getUnsignedLEShortA();
					int l25 = byteStream4.method541(-34545);
					if(l25 == 255)
						l25 = byteStream4.method555(935);
					class13_1.anIntArray224[l23] = l25;
				}

				for(int i26 = k19; i26 < class13_1.anIntArray269.length; i26++) {
					class13_1.anIntArray269[i26] = 0;
					class13_1.anIntArray224[i26] = 0;
				}

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 222 || packetOpcode == 53) {
				int l7 = anInt889;
				int k14 = anInt890;
				if(packetOpcode == 222) {
					k14 = byteStream4.getUnsignedShort();
					l7 = byteStream4.getUnsignedLEShortA();
					aBoolean1163 = false;
				}
				if(packetOpcode == 53) {
					l7 = byteStream4.getUnsignedShortA();
					byteStream4.method531((byte) 6);
					for(int i20 = 0; i20 < 4; i20++) {
						for(int i24 = 0; i24 < 13; i24++) {
							for(int j26 = 0; j26 < 13; j26++) {
								int l28 = byteStream4.method532(402, 1);
								if(l28 == 1)
									anIntArrayArrayArray879[i20][i24][j26] = byteStream4.method532(402, 26);
								else
									anIntArrayArrayArray879[i20][i24][j26] = -1;
							}

						}

					}

					byteStream4.finishBitAccess();
					k14 = byteStream4.getUnsignedShortA();
					aBoolean1163 = true;
				}
				if(packetOpcode != 53 && anInt889 == l7 && anInt890 == k14 && loadingStage == 2) {
					packetOpcode = -1;
					return true;
				}
				anInt889 = l7;
				anInt890 = k14;
				anInt1040 = (anInt889 - 6) * 8;
				anInt1041 = (anInt890 - 6) * 8;
				aBoolean1067 = false;
				if((anInt889 / 8 == 48 || anInt889 / 8 == 49) && anInt890 / 8 == 48)
					aBoolean1067 = true;
				if(anInt889 / 8 == 48 && anInt890 / 8 == 148)
					aBoolean1067 = true;
				loadingStage = 1;
				aLong1229 = System.currentTimeMillis();
				method125(-332, null, "Loading - please wait.");
				if(packetOpcode == 222) {
					int j20 = 0;
					for(int j24 = (anInt889 - 6) / 8; j24 <= (anInt889 + 6) / 8; j24++) {
						for(int k26 = (anInt890 - 6) / 8; k26 <= (anInt890 + 6) / 8; k26++)
							j20++;

					}

					aByteArrayArray838 = new byte[j20][];
					aByteArrayArray1232 = new byte[j20][];
					anIntArray856 = new int[j20];
					anIntArray857 = new int[j20];
					anIntArray858 = new int[j20];
					j20 = 0;
					for(int l26 = (anInt889 - 6) / 8; l26 <= (anInt889 + 6) / 8; l26++) {
						for(int i29 = (anInt890 - 6) / 8; i29 <= (anInt890 + 6) / 8; i29++) {
							anIntArray856[j20] = (l26 << 8) + i29;
							if(aBoolean1067 && (i29 == 49 || i29 == 149 || i29 == 147 || l26 == 50 || l26 == 49 && i29 == 47)) {
								anIntArray857[j20] = -1;
								anIntArray858[j20] = -1;
								j20++;
							} else {
								int l30 = anIntArray857[j20] = aClass32_Sub1_1291.method344(0, l26, i29, 0);
								if(l30 != -1)
									aClass32_Sub1_1291.method329(3, l30);
								int i32 = anIntArray858[j20] = aClass32_Sub1_1291.method344(0, l26, i29, 1);
								if(i32 != -1)
									aClass32_Sub1_1291.method329(3, i32);
								j20++;
								
								System.out.println("map??? " + l30 + "," + i32 + "," + j20);
							}
						}

					}

				}
				if(packetOpcode == 53) {
					int k20 = 0;
					int ai[] = new int[676];
					for(int i27 = 0; i27 < 4; i27++) {
						for(int j29 = 0; j29 < 13; j29++) {
							for(int i31 = 0; i31 < 13; i31++) {
								int j32 = anIntArrayArrayArray879[i27][j29][i31];
								if(j32 != -1) {
									int i33 = j32 >> 14 & 0x3ff;
									int k33 = j32 >> 3 & 0x7ff;
									int j34 = (i33 / 8 << 8) + k33 / 8;
									for(int l34 = 0; l34 < k20; l34++) {
										if(ai[l34] != j34)
											continue;
										j34 = -1;
										break;
									}

									if(j34 != -1)
										ai[k20++] = j34;
								}
							}

						}

					}

					aByteArrayArray838 = new byte[k20][];
					aByteArrayArray1232 = new byte[k20][];
					anIntArray856 = new int[k20];
					anIntArray857 = new int[k20];
					anIntArray858 = new int[k20];
					for(int k29 = 0; k29 < k20; k29++) {
						int j31 = anIntArray856[k29] = ai[k29];
						int k32 = j31 >> 8 & 0xff;
						int j33 = j31 & 0xff;
						int i34 = anIntArray857[k29] = aClass32_Sub1_1291.method344(0, k32, j33, 0);
						if(i34 != -1)
							aClass32_Sub1_1291.method329(3, i34);
						int k34 = anIntArray858[k29] = aClass32_Sub1_1291.method344(0, k32, j33, 1);
						if(k34 != -1)
							aClass32_Sub1_1291.method329(3, k34);
					}

				}
				int i21 = anInt1040 - anInt1042;
				int k24 = anInt1041 - anInt1043;
				anInt1042 = anInt1040;
				anInt1043 = anInt1041;
				for(int j27 = 0; j27 < 16384; j27++) {
					NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[j27];
					if(class50_sub1_sub4_sub3_sub1 != null) {
						for(int k31 = 0; k31 < 10; k31++) {
							((Mob) (class50_sub1_sub4_sub3_sub1)).anIntArray1586[k31] -= i21;
							((Mob) (class50_sub1_sub4_sub3_sub1)).anIntArray1587[k31] -= k24;
						}

						class50_sub1_sub4_sub3_sub1.anInt1610 -= i21 * 128;
						class50_sub1_sub4_sub3_sub1.anInt1611 -= k24 * 128;
					}
				}

				for(int l29 = 0; l29 < anInt968; l29++) {
					Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[l29];
					if(class50_sub1_sub4_sub3_sub2 != null) {
						for(int l32 = 0; l32 < 10; l32++) {
							((Mob) (class50_sub1_sub4_sub3_sub2)).anIntArray1586[l32] -= i21;
							((Mob) (class50_sub1_sub4_sub3_sub2)).anIntArray1587[l32] -= k24;
						}

						class50_sub1_sub4_sub3_sub2.anInt1610 -= i21 * 128;
						class50_sub1_sub4_sub3_sub2.anInt1611 -= k24 * 128;
					}
				}

				aBoolean1209 = true;
				byte byte1 = 0;
				byte byte2 = 104;
				byte byte3 = 1;
				if(i21 < 0) {
					byte1 = 103;
					byte2 = -1;
					byte3 = -1;
				}
				byte byte4 = 0;
				byte byte5 = 104;
				byte byte6 = 1;
				if(k24 < 0) {
					byte4 = 103;
					byte5 = -1;
					byte6 = -1;
				}
				for(int i35 = byte1; i35 != byte2; i35 += byte3) {
					for(int j35 = byte4; j35 != byte5; j35 += byte6) {
						int k35 = i35 + i21;
						int l35 = j35 + k24;
						for(int i36 = 0; i36 < 4; i36++)
							if(k35 >= 0 && l35 >= 0 && k35 < 104 && l35 < 104)
								aClass6ArrayArrayArray1323[i36][i35][j35] = aClass6ArrayArrayArray1323[i36][k35][l35];
							else
								aClass6ArrayArrayArray1323[i36][i35][j35] = null;

					}

				}

				for(Class50_Sub2 class50_sub2_1 = (Class50_Sub2) aClass6_1261.method158(); class50_sub2_1 != null; class50_sub2_1 = (Class50_Sub2) aClass6_1261.method160(1)) {
					class50_sub2_1.anInt1393 -= i21;
					class50_sub2_1.anInt1394 -= k24;
					if(class50_sub2_1.anInt1393 < 0 || class50_sub2_1.anInt1394 < 0 || class50_sub2_1.anInt1393 >= 104 || class50_sub2_1.anInt1394 >= 104)
						class50_sub2_1.method442();
				}

				if(anInt1120 != 0) {
					anInt1120 -= i21;
					anInt1121 -= k24;
				}
				aBoolean1211 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 190) {
				anInt1057 = byteStream4.getUnsignedLEShort() * 30;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 41 || packetOpcode == 121 || packetOpcode == 203 || packetOpcode == 106 || packetOpcode == 59 || packetOpcode == 181 || packetOpcode == 208 || packetOpcode == 107
					|| packetOpcode == 142 || packetOpcode == 88 || packetOpcode == 152) {
				method133(byteStream4, 0, packetOpcode);
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 124) {
				playerRunning = byteStream4.getUnsignedByte() == 1;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 125) {
				if(selectedTab == 12) {
					tabRepaintRequested = true;
				}
				
				playerRunEnergy = byteStream4.getUnsignedByte();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 21) {
				int i8 = byteStream4.getUnsignedShort();
				int l14 = byteStream4.getUnsignedLEShort();
				int j21 = byteStream4.getUnsignedLEShortA();
				if(l14 == 65535) {
					GameInterface.getInterface(j21).anInt283 = 0;
					packetOpcode = -1;
					return true;
				} else {
					ItemDefinition class16 = ItemDefinition.forId(l14);
					GameInterface.getInterface(j21).anInt283 = 4;
					GameInterface.getInterface(j21).anInt284 = l14;
					GameInterface.getInterface(j21).anInt252 = class16.worldRotationX;
					GameInterface.getInterface(j21).anInt253 = class16.rotationY;
					GameInterface.getInterface(j21).anInt251 = (class16.zoom * 100) / i8;
					packetOpcode = -1;
					return true;
				}
			}
			if(packetOpcode == 3) {
				aBoolean1211 = true;
				anInt874 = byteStream4.getUnsignedByte();
				anInt875 = byteStream4.getUnsignedByte();
				anInt876 = byteStream4.getUnsignedShort();
				anInt877 = byteStream4.getUnsignedByte();
				anInt878 = byteStream4.getUnsignedByte();
				if(anInt878 >= 100) {
					anInt1216 = anInt874 * 128 + 64;
					anInt1218 = anInt875 * 128 + 64;
					anInt1217 = method110(anInt1218, anInt1216, (byte) 9, anInt1091) - anInt876;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 2) {
				int j8 = byteStream4.getUnsignedLEShortA();
				int i15 = byteStream4.method553((byte) 17);
				GameInterface class13_3 = GameInterface.getInterface(j8);
				if(class13_3.anInt286 != i15 || i15 == -1) {
					class13_3.anInt286 = i15;
					class13_3.anInt235 = 0;
					class13_3.anInt227 = 0;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 71) {
				getNpcPos(byteStream4, aBoolean1038, packetSize);
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 226) {
				anInt855 = packetSize / 8;
				for(int k8 = 0; k8 < anInt855; k8++)
					aLongArray1073[k8] = byteStream4.getLong();

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 10) {
				int l8 = byteStream4.method542(anInt1236);
				int j15 = byteStream4.getUnsignedShortA();
				if(j15 == 65535)
					j15 = -1;
				if(tabInterfaceIds[l8] != j15) {
					method44(aBoolean1190, tabInterfaceIds[l8]);
					tabInterfaceIds[l8] = j15;
				}
				tabRepaintRequested = true;
				aBoolean950 = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 219) {
				int i9 = byteStream4.getUnsignedLEShort();
				GameInterface class13_2 = GameInterface.getInterface(i9);
				for(int k21 = 0; k21 < class13_2.anIntArray269.length; k21++) {
					class13_2.anIntArray269[k21] = -1;
					class13_2.anIntArray269[k21] = 0;
				}

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 238) {
				flashingSidebarId = byteStream4.getUnsignedByte();
				if(flashingSidebarId == selectedTab) {
					if(flashingSidebarId == 3)
						selectedTab = 1;
					else
						selectedTab = 3;
					tabRepaintRequested = true;
				}
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 148) {
				aBoolean1211 = false;
				for(int j9 = 0; j9 < 5; j9++)
					aBooleanArray927[j9] = false;

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 126) {
				anInt1068 = byteStream4.getUnsignedByte();
				anInt961 = byteStream4.getUnsignedLEShort();
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 75) {
				anInt989 = byteStream4.method541(-34545);
				anInt990 = byteStream4.method540(0);
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 253) {
				int k9 = byteStream4.getUnsignedLEShort();
				int k15 = byteStream4.getUnsignedShortA();
				method112((byte) 36, k15);
				if(k9 != -1)
					method112((byte) 36, k9);
				if(anInt1169 != -1) {
					method44(aBoolean1190, anInt1169);
					anInt1169 = -1;
				}
				if(anInt1089 != -1) {
					method44(aBoolean1190, anInt1089);
					anInt1089 = -1;
				}
				if(anInt988 != -1) {
					method44(aBoolean1190, anInt988);
					anInt988 = -1;
				}
				if(anInt1053 != k15) {
					method44(aBoolean1190, anInt1053);
					anInt1053 = k15;
				}
				if(anInt960 != k15) {
					method44(aBoolean1190, anInt960);
					anInt960 = k9;
				}
				anInt1244 = 0;
				aBoolean1239 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 251) {
				anInt860 = byteStream4.getUnsignedByte();
				tabRepaintRequested = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 18) {
				int l9 = byteStream4.getUnsignedShort();
				int l15 = byteStream4.getUnsignedShortA();
				int l21 = byteStream4.getUnsignedLEShort();
				GameInterface.getInterface(l15).anInt218 = (l9 << 16) + l21;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 90) {
				method96(packetSize, 69, byteStream4);
				aBoolean1209 = false;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 113) {
				for(int i10 = 0; i10 < anIntArray1039.length; i10++)
					if(anIntArray1039[i10] != anIntArray1005[i10]) {
						anIntArray1039[i10] = anIntArray1005[i10];
						method105(0, i10);
						tabRepaintRequested = true;
					}

				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 232) {
				int j10 = byteStream4.getUnsignedLEShortA();
				String s6 = byteStream4.getRS2String();
				GameInterface.getInterface(j10).text = s6;
				if(GameInterface.getInterface(j10).parentId == tabInterfaceIds[selectedTab])
					tabRepaintRequested = true;
				packetOpcode = -1;
				return true;
			}
			if(packetOpcode == 200) {
				int k10 = byteStream4.getUnsignedShort();
				int i16 = byteStream4.getUnsignedLEShortA();
				GameInterface class13_4 = GameInterface.getInterface(k10);
				if(class13_4 != null && class13_4.type == 0) {
					if(i16 < 0)
						i16 = 0;
					if(i16 > class13_4.scrollHeight - class13_4.height)
						i16 = class13_4.scrollHeight - class13_4.height;
					class13_4.anInt231 = i16;
				}
				packetOpcode = -1;
				return true;
			}
			Signlink.reporterror("T1 - " + packetOpcode + "," + packetSize + " - " + anInt904 + "," + anInt905);
			method124(true);
		} catch(IOException _ex) {
			method59(1);
		} catch(Exception exception) {
			String s1 = "T2 - " + packetOpcode + "," + anInt904 + "," + anInt905 + " - " + packetSize + "," + (anInt1040 + ((Mob) (sessionPlayer)).anIntArray1586[0]) + ","
					+ (anInt1041 + ((Mob) (sessionPlayer)).anIntArray1587[0]) + " - ";
			for(int j16 = 0; j16 < packetSize && j16 < 50; j16++)
				s1 = s1 + byteStream4.payload[j16] + ",";

			Signlink.reporterror(s1);
			method124(true);
			exception.printStackTrace();
		}
		return true;
	}

	public void method34(byte byte0) {
		if(menuActionIndex < 2 && itemSelected == 0 && spellSelected == 0)
			return;
		if(byte0 != -79)
			return;
		String s;
		if(itemSelected == 1 && menuActionIndex < 2)
			s = "Use " + selectedItemName + " with...";
		else if(spellSelected == 1 && menuActionIndex < 2)
			s = spellTooltip + "...";
		else
			s = menuActionName[menuActionIndex - 1];
		if(menuActionIndex > 2)
			s = s + "@whi@ / " + (menuActionIndex - 2) + " more options";
		boldFont.method479(true, currentTime / 1000, 4, 0xffffff, 15, s, 0);
	}

	public boolean method35(boolean flag, boolean flag1, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
		byte byte0 = 104;
		byte byte1 = 104;
		for(int k2 = 0; k2 < byte0; k2++) {
			for(int l2 = 0; l2 < byte1; l2++) {
				anIntArrayArray885[k2][l2] = 0;
				anIntArrayArray1189[k2][l2] = 0x5f5e0ff;
			}

		}

		int i3 = j2;
		int j3 = j;
		anIntArrayArray885[j2][j] = 99;
		anIntArrayArray1189[j2][j] = 0;
		int k3 = 0;
		int l3 = 0;
		anIntArray1123[k3] = j2;
		anIntArray1124[k3++] = j;
		boolean flag2 = false;
		int i4 = anIntArray1123.length;
		int ai[][] = aClass46Array1260[anInt1091].anIntArrayArray757;
		while(l3 != k3) {
			i3 = anIntArray1123[l3];
			j3 = anIntArray1124[l3];
			l3 = (l3 + 1) % i4;
			if(i3 == k1 && j3 == i) {
				flag2 = true;
				break;
			}
			if(j1 != 0) {
				if((j1 < 5 || j1 == 10) && aClass46Array1260[anInt1091].method420(k1, 0, i, j1 - 1, i3, j3, i2)) {
					flag2 = true;
					break;
				}
				if(j1 < 10 && aClass46Array1260[anInt1091].method421(-37, j3, k1, i3, i2, j1 - 1, i)) {
					flag2 = true;
					break;
				}
			}
			if(k != 0 && l != 0 && aClass46Array1260[anInt1091].method422(k, i3, true, k1, l1, l, i, j3)) {
				flag2 = true;
				break;
			}
			int k4 = anIntArrayArray1189[i3][j3] + 1;
			if(i3 > 0 && anIntArrayArray885[i3 - 1][j3] == 0 && (ai[i3 - 1][j3] & 0x1280108) == 0) {
				anIntArray1123[k3] = i3 - 1;
				anIntArray1124[k3] = j3;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 - 1][j3] = 2;
				anIntArrayArray1189[i3 - 1][j3] = k4;
			}
			if(i3 < byte0 - 1 && anIntArrayArray885[i3 + 1][j3] == 0 && (ai[i3 + 1][j3] & 0x1280180) == 0) {
				anIntArray1123[k3] = i3 + 1;
				anIntArray1124[k3] = j3;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 + 1][j3] = 8;
				anIntArrayArray1189[i3 + 1][j3] = k4;
			}
			if(j3 > 0 && anIntArrayArray885[i3][j3 - 1] == 0 && (ai[i3][j3 - 1] & 0x1280102) == 0) {
				anIntArray1123[k3] = i3;
				anIntArray1124[k3] = j3 - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3][j3 - 1] = 1;
				anIntArrayArray1189[i3][j3 - 1] = k4;
			}
			if(j3 < byte1 - 1 && anIntArrayArray885[i3][j3 + 1] == 0 && (ai[i3][j3 + 1] & 0x1280120) == 0) {
				anIntArray1123[k3] = i3;
				anIntArray1124[k3] = j3 + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3][j3 + 1] = 4;
				anIntArrayArray1189[i3][j3 + 1] = k4;
			}
			if(i3 > 0 && j3 > 0 && anIntArrayArray885[i3 - 1][j3 - 1] == 0 && (ai[i3 - 1][j3 - 1] & 0x128010e) == 0 && (ai[i3 - 1][j3] & 0x1280108) == 0 && (ai[i3][j3 - 1] & 0x1280102) == 0) {
				anIntArray1123[k3] = i3 - 1;
				anIntArray1124[k3] = j3 - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 - 1][j3 - 1] = 3;
				anIntArrayArray1189[i3 - 1][j3 - 1] = k4;
			}
			if(i3 < byte0 - 1 && j3 > 0 && anIntArrayArray885[i3 + 1][j3 - 1] == 0 && (ai[i3 + 1][j3 - 1] & 0x1280183) == 0 && (ai[i3 + 1][j3] & 0x1280180) == 0 && (ai[i3][j3 - 1] & 0x1280102) == 0) {
				anIntArray1123[k3] = i3 + 1;
				anIntArray1124[k3] = j3 - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 + 1][j3 - 1] = 9;
				anIntArrayArray1189[i3 + 1][j3 - 1] = k4;
			}
			if(i3 > 0 && j3 < byte1 - 1 && anIntArrayArray885[i3 - 1][j3 + 1] == 0 && (ai[i3 - 1][j3 + 1] & 0x1280138) == 0 && (ai[i3 - 1][j3] & 0x1280108) == 0 && (ai[i3][j3 + 1] & 0x1280120) == 0) {
				anIntArray1123[k3] = i3 - 1;
				anIntArray1124[k3] = j3 + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 - 1][j3 + 1] = 6;
				anIntArrayArray1189[i3 - 1][j3 + 1] = k4;
			}
			if(i3 < byte0 - 1 && j3 < byte1 - 1 && anIntArrayArray885[i3 + 1][j3 + 1] == 0 && (ai[i3 + 1][j3 + 1] & 0x12801e0) == 0 && (ai[i3 + 1][j3] & 0x1280180) == 0
					&& (ai[i3][j3 + 1] & 0x1280120) == 0) {
				anIntArray1123[k3] = i3 + 1;
				anIntArray1124[k3] = j3 + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[i3 + 1][j3 + 1] = 12;
				anIntArrayArray1189[i3 + 1][j3 + 1] = k4;
			}
		}
		anInt1126 = 0;
		if(!flag2)
			if(flag) {
				int l4 = 1000;
				int j5 = 100;
				byte byte2 = 10;
				for(int i6 = k1 - byte2; i6 <= k1 + byte2; i6++) {
					for(int k6 = i - byte2; k6 <= i + byte2; k6++)
						if(i6 >= 0 && k6 >= 0 && i6 < 104 && k6 < 104 && anIntArrayArray1189[i6][k6] < 100) {
							int i7 = 0;
							if(i6 < k1)
								i7 = k1 - i6;
							else if(i6 > (k1 + k) - 1)
								i7 = i6 - ((k1 + k) - 1);
							int j7 = 0;
							if(k6 < i)
								j7 = i - k6;
							else if(k6 > (i + l) - 1)
								j7 = k6 - ((i + l) - 1);
							int k7 = i7 * i7 + j7 * j7;
							if(k7 < l4 || k7 == l4 && anIntArrayArray1189[i6][k6] < j5) {
								l4 = k7;
								j5 = anIntArrayArray1189[i6][k6];
								i3 = i6;
								j3 = k6;
							}
						}

				}

				if(l4 == 1000)
					return false;
				if(i3 == j2 && j3 == j)
					return false;
				anInt1126 = 1;
			} else {
				return false;
			}
		l3 = 0;
		if(flag1)
			startUp();
		anIntArray1123[l3] = i3;
		anIntArray1124[l3++] = j3;
		int k5;
		for(int i5 = k5 = anIntArrayArray885[i3][j3]; i3 != j2 || j3 != j; i5 = anIntArrayArray885[i3][j3]) {
			if(i5 != k5) {
				k5 = i5;
				anIntArray1123[l3] = i3;
				anIntArray1124[l3++] = j3;
			}
			if((i5 & 2) != 0)
				i3++;
			else if((i5 & 8) != 0)
				i3--;
			if((i5 & 1) != 0)
				j3++;
			else if((i5 & 4) != 0)
				j3--;
		}

		if(l3 > 0) {
			int j4 = l3;
			if(j4 > 25)
				j4 = 25;
			l3--;
			int l5 = anIntArray1123[l3];
			int j6 = anIntArray1124[l3];
			if(i1 == 0) {
				byteStream2.createFrame(28);
				byteStream2.putByte(j4 + j4 + 3);
			}
			if(i1 == 1) {
				byteStream2.createFrame(213);
				byteStream2.putByte(j4 + j4 + 3 + 14);
			}
			if(i1 == 2) {
				byteStream2.createFrame(247);
				byteStream2.putByte(j4 + j4 + 3);
			}
			byteStream2.method548(3, l5 + anInt1040);
			byteStream2.putByte(super.anIntArray32[5] != 1 ? 0 : 1);
			byteStream2.method548(3, j6 + anInt1041);
			anInt1120 = anIntArray1123[0];
			anInt1121 = anIntArray1124[0];
			for(int l6 = 1; l6 < j4; l6++) {
				l3--;
				byteStream2.putByte(anIntArray1123[l3] - l5);
				byteStream2.writeByteS(anIntArray1124[l3] - j6, 1);
			}

			return true;
		}
		return i1 != 1;
	}

	public void method36(int i) {
		if(i != 16220)
			anInt1328 = 458;
		if(loadingStage == 2) {
			for(Class50_Sub2 class50_sub2 = (Class50_Sub2) aClass6_1261.method158(); class50_sub2 != null; class50_sub2 = (Class50_Sub2) aClass6_1261.method160(1)) {
				if(class50_sub2.anInt1390 > 0)
					class50_sub2.anInt1390--;
				if(class50_sub2.anInt1390 == 0) {
					if(class50_sub2.anInt1387 < 0 || Class8.method170(class50_sub2.anInt1389, aByte1143, class50_sub2.anInt1387)) {
						method45(class50_sub2.anInt1388, class50_sub2.anInt1393, class50_sub2.anInt1387, class50_sub2.anInt1394, class50_sub2.anInt1391, class50_sub2.anInt1389, (byte) 1,
								class50_sub2.anInt1392);
						class50_sub2.method442();
					}
				} else {
					if(class50_sub2.anInt1395 > 0)
						class50_sub2.anInt1395--;
					if(class50_sub2.anInt1395 == 0 && class50_sub2.anInt1393 >= 1 && class50_sub2.anInt1394 >= 1 && class50_sub2.anInt1393 <= 102 && class50_sub2.anInt1394 <= 102
							&& (class50_sub2.anInt1384 < 0 || Class8.method170(class50_sub2.anInt1386, aByte1143, class50_sub2.anInt1384))) {
						method45(class50_sub2.anInt1385, class50_sub2.anInt1393, class50_sub2.anInt1384, class50_sub2.anInt1394, class50_sub2.anInt1391, class50_sub2.anInt1386, (byte) 1,
								class50_sub2.anInt1392);
						class50_sub2.anInt1395 = -1;
						if(class50_sub2.anInt1384 == class50_sub2.anInt1387 && class50_sub2.anInt1387 == -1)
							class50_sub2.method442();
						else if(class50_sub2.anInt1384 == class50_sub2.anInt1387 && class50_sub2.anInt1385 == class50_sub2.anInt1388 && class50_sub2.anInt1386 == class50_sub2.anInt1389)
							class50_sub2.method442();
					}
				}
			}

		}
	}

	public String getHostName() {
		if(Signlink.mainapp != null)
			return Signlink.mainapp.getDocumentBase().getHost().toLowerCase();
		if(super.gameFrame != null)
			return "manascape.org";
		else
			return super.getDocumentBase().getHost().toLowerCase();
	}

	public void method38(int i, int j, int k, Player class50_sub1_sub4_sub3_sub2, int l) {
		if(class50_sub1_sub4_sub3_sub2 == sessionPlayer)
			return;
		if(menuActionIndex >= 400)
			return;
		if(l != 0)
			aBoolean963 = !aBoolean963;
		String s;
		if(class50_sub1_sub4_sub3_sub2.totalLevel == 0)
			s = class50_sub1_sub4_sub3_sub2.username + getCombatRiskColour(class50_sub1_sub4_sub3_sub2.combatLevel, sessionPlayer.combatLevel) + " (level-" + class50_sub1_sub4_sub3_sub2.combatLevel
					+ ")";
		else
			s = class50_sub1_sub4_sub3_sub2.username + " (skill-" + class50_sub1_sub4_sub3_sub2.totalLevel + ")";
		if(itemSelected == 1) {
			menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @whi@" + s;
			menuActionId[menuActionIndex] = 596;
			menuActionCmd1[menuActionIndex] = i;
			menuActionCmd2[menuActionIndex] = k;
			menuActionCmd3[menuActionIndex] = j;
			menuActionIndex++;
		} else if(spellSelected == 1) {
			if((spellUsableOn & 8) == 8) {
				menuActionName[menuActionIndex] = spellTooltip + " @whi@" + s;
				menuActionId[menuActionIndex] = 918;
				menuActionCmd1[menuActionIndex] = i;
				menuActionCmd2[menuActionIndex] = k;
				menuActionCmd3[menuActionIndex] = j;
				menuActionIndex++;
			}
		} else {
			for(int i1 = 4; i1 >= 0; i1--)
				if(aStringArray1069[i1] != null) {
					menuActionName[menuActionIndex] = aStringArray1069[i1] + " @whi@" + s;
					char c = '\0';
					if(aStringArray1069[i1].equalsIgnoreCase("attack")) {
						if(class50_sub1_sub4_sub3_sub2.combatLevel > sessionPlayer.combatLevel)
							c = '\u07D0';
						if(sessionPlayer.teamId != 0 && class50_sub1_sub4_sub3_sub2.teamId != 0)
							if(sessionPlayer.teamId == class50_sub1_sub4_sub3_sub2.teamId)
								c = '\u07D0';
							else
								c = '\0';
					} else if(aBooleanArray1070[i1])
						c = '\u07D0';
					if(i1 == 0)
						menuActionId[menuActionIndex] = 200 + c;
					if(i1 == 1)
						menuActionId[menuActionIndex] = 493 + c;
					if(i1 == 2)
						menuActionId[menuActionIndex] = 408 + c;
					if(i1 == 3)
						menuActionId[menuActionIndex] = 677 + c;
					if(i1 == 4)
						menuActionId[menuActionIndex] = 876 + c;
					menuActionCmd1[menuActionIndex] = i;
					menuActionCmd2[menuActionIndex] = k;
					menuActionCmd3[menuActionIndex] = j;
					menuActionIndex++;
				}

		}
		for(int j1 = 0; j1 < menuActionIndex; j1++)
			if(menuActionId[j1] == 14) {
				menuActionName[j1] = "Walk here @whi@" + s;
				return;
			}

	}

	public void handleChatButtonClicking() {
		if(super.anInt28 == 1) {
			if(super.clickX >= 6 && super.clickX <= 106 && super.clickY >= 467 && super.clickY <= 499) {
				anInt1006 = (anInt1006 + 1) % 4;
				aBoolean1212 = true;
				aBoolean1240 = true;
				byteStream2.createFrame(176);
				byteStream2.putByte(anInt1006);
				byteStream2.putByte(anInt887);
				byteStream2.putByte(anInt1227);
			}
			if(super.clickX >= 135 && super.clickX <= 235 && super.clickY >= 467 && super.clickY <= 499) {
				anInt887 = (anInt887 + 1) % 3;
				aBoolean1212 = true;
				aBoolean1240 = true;
				byteStream2.createFrame(176);
				byteStream2.putByte(anInt1006);
				byteStream2.putByte(anInt887);
				byteStream2.putByte(anInt1227);
			}
			if(super.clickX >= 273 && super.clickX <= 373 && super.clickY >= 467 && super.clickY <= 499) {
				anInt1227 = (anInt1227 + 1) % 3;
				aBoolean1212 = true;
				aBoolean1240 = true;
				byteStream2.createFrame(176);
				byteStream2.putByte(anInt1006);
				byteStream2.putByte(anInt887);
				byteStream2.putByte(anInt1227);
			}
			if(super.clickX >= 412 && super.clickX <= 512 && super.clickY >= 467 && super.clickY <= 499)
				if(anInt1169 == -1) {
					method15(false);
					aString839 = "";
					aBoolean1098 = false;
					anInt1231 = anInt1169 = GameInterface.anInt246;
				} else {
					sendChatboxMessage("", "Please close the interface you have open before using 'report abuse'", 0);
				}
			anInt1160++;
			if(anInt1160 > 161) {
				anInt1160 = 0;
				byteStream2.createFrame(22);
				byteStream2.putShort(38304);
			}
		}
	}

	public void method40(int i, ByteBuffer byteStream, int j) {
		for(int k = 0; k < sessionNpcsAwaitingUpdate; k++) {
			int l = anIntArray974[k];
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[l];
			int i1 = byteStream.getUnsignedByte();
			if((i1 & 0x20) != 0)
				i1 += byteStream.getUnsignedByte() << 8;
			appendPlayerUpdateMask(2, l, class50_sub1_sub4_sub3_sub2, i1, byteStream);
		}

		i = 70 / i;
	}

	public void method41(int i, boolean flag, ByteBuffer byteStream) {
		byteStream.method531((byte) 6);
		int j = byteStream.method532(402, 1);
		if(j == 0)
			return;
		int k = byteStream.method532(402, 2);
		loggedIn &= flag;
		if(k == 0) {
			anIntArray974[sessionNpcsAwaitingUpdate++] = anInt969;
			return;
		}
		if(k == 1) {
			int l = byteStream.method532(402, 3);
			sessionPlayer.method566(false, l, -808);
			int k1 = byteStream.method532(402, 1);
			if(k1 == 1)
				anIntArray974[sessionNpcsAwaitingUpdate++] = anInt969;
			return;
		}
		if(k == 2) {
			int i1 = byteStream.method532(402, 3);
			sessionPlayer.method566(true, i1, -808);
			int l1 = byteStream.method532(402, 3);
			sessionPlayer.method566(true, l1, -808);
			int j2 = byteStream.method532(402, 1);
			if(j2 == 1)
				anIntArray974[sessionNpcsAwaitingUpdate++] = anInt969;
			return;
		}
		if(k == 3) {
			int j1 = byteStream.method532(402, 1);
			anInt1091 = byteStream.method532(402, 2);
			int i2 = byteStream.method532(402, 7);
			int k2 = byteStream.method532(402, 7);
			int l2 = byteStream.method532(402, 1);
			if(l2 == 1)
				anIntArray974[sessionNpcsAwaitingUpdate++] = anInt969;
			sessionPlayer.method568(i2, (byte) 5, j1 == 1, k2);
		}
	}

	public void method42(int i, int j, GameInterface class13, byte byte0, int k, int l, int i1, int j1, int k1) {
		if(aBoolean1127)
			anInt1303 = 32;
		else
			anInt1303 = 0;
		aBoolean1127 = false;
		if(byte0 != 102) {
			for(int l1 = 1; l1 > 0; l1++)
				;
		}
		if(i1 >= k1 && i1 < k1 + 16 && k >= j && k < j + 16) {
			class13.anInt231 -= anInt1094 * 4;
			if(l == 1)
				tabRepaintRequested = true;
			if(l == 2 || l == 3)
				aBoolean1240 = true;
			return;
		}
		if(i1 >= k1 && i1 < k1 + 16 && k >= (j + j1) - 16 && k < j + j1) {
			class13.anInt231 += anInt1094 * 4;
			if(l == 1)
				tabRepaintRequested = true;
			if(l == 2 || l == 3)
				aBoolean1240 = true;
			return;
		}
		if(i1 >= k1 - anInt1303 && i1 < k1 + 16 + anInt1303 && k >= j + 16 && k < (j + j1) - 16 && anInt1094 > 0) {
			int i2 = ((j1 - 32) * j1) / i;
			if(i2 < 8)
				i2 = 8;
			int j2 = k - j - 16 - i2 / 2;
			int k2 = j1 - 32 - i2;
			class13.anInt231 = ((i - j1) * j2) / k2;
			if(l == 1)
				tabRepaintRequested = true;
			if(l == 2 || l == 3)
				aBoolean1240 = true;
			aBoolean1127 = true;
		}
	}

	public void method43(byte byte0) {
		if(itemSelected == 0 && spellSelected == 0) {
			menuActionName[menuActionIndex] = "Walk here";
			menuActionId[menuActionIndex] = 14;
			menuActionCmd2[menuActionIndex] = super.mouseX;
			menuActionCmd3[menuActionIndex] = super.mouseY;
			menuActionIndex++;
		}
		int i = -1;
		if(byte0 != 7)
			packetOpcode = -1;
		for(int j = 0; j < Model.anInt1708; j++) {
			int k = Model.anIntArray1709[j];
			int l = k & 0x7f;
			int i1 = k >> 7 & 0x7f;
			int j1 = k >> 29 & 3;
			int k1 = k >> 14 & 0x7fff;
			if(k == i)
				continue;
			i = k;
			if(j1 == 2 && aClass22_1164.method271(anInt1091, l, i1, k) >= 0) {
				Class47 class47 = Class47.method423(k1);
				if(class47.anIntArray805 != null)
					class47 = class47.method424(0);
				if(class47 == null)
					continue;
				if(itemSelected == 1) {
					menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @cya@" + class47.aString776;
					menuActionId[menuActionIndex] = 467;
					menuActionCmd1[menuActionIndex] = k;
					menuActionCmd2[menuActionIndex] = l;
					menuActionCmd3[menuActionIndex] = i1;
					menuActionIndex++;
				} else if(spellSelected == 1) {
					if((spellUsableOn & 4) == 4) {
						menuActionName[menuActionIndex] = spellTooltip + " @cya@" + class47.aString776;
						menuActionId[menuActionIndex] = 376;
						menuActionCmd1[menuActionIndex] = k;
						menuActionCmd2[menuActionIndex] = l;
						menuActionCmd3[menuActionIndex] = i1;
						menuActionIndex++;
					}
				} else {
					if(class47.aStringArray790 != null) {
						for(int l1 = 4; l1 >= 0; l1--)
							if(class47.aStringArray790[l1] != null) {
								menuActionName[menuActionIndex] = class47.aStringArray790[l1] + " @cya@" + class47.aString776;
								if(l1 == 0)
									menuActionId[menuActionIndex] = 35;
								if(l1 == 1)
									menuActionId[menuActionIndex] = 389;
								if(l1 == 2)
									menuActionId[menuActionIndex] = 888;
								if(l1 == 3)
									menuActionId[menuActionIndex] = 892;
								if(l1 == 4)
									menuActionId[menuActionIndex] = 1280;
								menuActionCmd1[menuActionIndex] = k;
								menuActionCmd2[menuActionIndex] = l;
								menuActionCmd3[menuActionIndex] = i1;
								menuActionIndex++;
							}

					}
					menuActionName[menuActionIndex] = "Examine @cya@" + class47.aString776;
					menuActionId[menuActionIndex] = 1412;
					menuActionCmd1[menuActionIndex] = class47.anInt773 << 14;
					menuActionCmd2[menuActionIndex] = l;
					menuActionCmd3[menuActionIndex] = i1;
					menuActionIndex++;
				}
			}
			if(j1 == 1) {
				NPC npc = sessionNpcs[k1];
				if(npc.npcDef.boundDim == 1 && (((Mob) (npc)).anInt1610 & 0x7f) == 64 && (((Mob) (npc)).anInt1611 & 0x7f) == 64) {
					for(int i2 = 0; i2 < sessionNpcCount; i2++) {
						NPC npc2 = sessionNpcs[sessionNpcList[i2]];
						if(npc2 != null && npc2 != npc && npc2.npcDef.boundDim == 1 && ((Mob) (npc2)).anInt1610 == ((Mob) (npc)).anInt1610 && ((Mob) (npc2)).anInt1611 == ((Mob) (npc)).anInt1611)
							buildAtNpcMenu(npc2, i1, l, sessionNpcList[i2]);
					}

					for(int k2 = 0; k2 < anInt971; k2++) {
						Player class50_sub1_sub4_sub3_sub2_1 = sessionPlayers[sessionPlayerList[k2]];
						if(class50_sub1_sub4_sub3_sub2_1 != null && ((Mob) (class50_sub1_sub4_sub3_sub2_1)).anInt1610 == ((Mob) (npc)).anInt1610
								&& ((Mob) (class50_sub1_sub4_sub3_sub2_1)).anInt1611 == ((Mob) (npc)).anInt1611)
							method38(sessionPlayerList[k2], i1, l, class50_sub1_sub4_sub3_sub2_1, 0);
					}

				}
				buildAtNpcMenu(npc, i1, l, k1);
			}
			if(j1 == 0) {
				Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[k1];
				if((((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610 & 0x7f) == 64 && (((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611 & 0x7f) == 64) {
					for(int j2 = 0; j2 < sessionNpcCount; j2++) {
						NPC class50_sub1_sub4_sub3_sub1_2 = sessionNpcs[sessionNpcList[j2]];
						if(class50_sub1_sub4_sub3_sub1_2 != null && class50_sub1_sub4_sub3_sub1_2.npcDef.boundDim == 1
								&& ((Mob) (class50_sub1_sub4_sub3_sub1_2)).anInt1610 == ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610
								&& ((Mob) (class50_sub1_sub4_sub3_sub1_2)).anInt1611 == ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611)
							buildAtNpcMenu(class50_sub1_sub4_sub3_sub1_2, i1, l, sessionNpcList[j2]);
					}

					for(int l2 = 0; l2 < anInt971; l2++) {
						Player class50_sub1_sub4_sub3_sub2_2 = sessionPlayers[sessionPlayerList[l2]];
						if(class50_sub1_sub4_sub3_sub2_2 != null && class50_sub1_sub4_sub3_sub2_2 != class50_sub1_sub4_sub3_sub2
								&& ((Mob) (class50_sub1_sub4_sub3_sub2_2)).anInt1610 == ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610
								&& ((Mob) (class50_sub1_sub4_sub3_sub2_2)).anInt1611 == ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611)
							method38(sessionPlayerList[l2], i1, l, class50_sub1_sub4_sub3_sub2_2, 0);
					}

				}
				method38(k1, i1, l, class50_sub1_sub4_sub3_sub2, 0);
			}
			if(j1 == 3) {
				NodeList class6 = aClass6ArrayArrayArray1323[anInt1091][l][i1];
				if(class6 != null) {
					for(Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1 = (Class50_Sub1_Sub4_Sub1) class6.method159(false); class50_sub1_sub4_sub1 != null; class50_sub1_sub4_sub1 = (Class50_Sub1_Sub4_Sub1) class6
							.method161(173)) {
						ItemDefinition class16 = ItemDefinition.forId(class50_sub1_sub4_sub1.anInt1550);
						if(itemSelected == 1) {
							menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @lre@" + class16.name;
							menuActionId[menuActionIndex] = 100;
							menuActionCmd1[menuActionIndex] = class50_sub1_sub4_sub1.anInt1550;
							menuActionCmd2[menuActionIndex] = l;
							menuActionCmd3[menuActionIndex] = i1;
							menuActionIndex++;
						} else if(spellSelected == 1) {
							if((spellUsableOn & 1) == 1) {
								menuActionName[menuActionIndex] = spellTooltip + " @lre@" + class16.name;
								menuActionId[menuActionIndex] = 199;
								menuActionCmd1[menuActionIndex] = class50_sub1_sub4_sub1.anInt1550;
								menuActionCmd2[menuActionIndex] = l;
								menuActionCmd3[menuActionIndex] = i1;
								menuActionIndex++;
							}
						} else {
							for(int i3 = 4; i3 >= 0; i3--)
								if(class16.groundActions != null && class16.groundActions[i3] != null) {
									menuActionName[menuActionIndex] = class16.groundActions[i3] + " @lre@" + class16.name;
									if(i3 == 0)
										menuActionId[menuActionIndex] = 68;
									if(i3 == 1)
										menuActionId[menuActionIndex] = 26;
									if(i3 == 2)
										menuActionId[menuActionIndex] = 684;
									if(i3 == 3)
										menuActionId[menuActionIndex] = 930;
									if(i3 == 4)
										menuActionId[menuActionIndex] = 270;
									menuActionCmd1[menuActionIndex] = class50_sub1_sub4_sub1.anInt1550;
									menuActionCmd2[menuActionIndex] = l;
									menuActionCmd3[menuActionIndex] = i1;
									menuActionIndex++;
								} else if(i3 == 2) {
									menuActionName[menuActionIndex] = "Take @lre@" + class16.name;
									menuActionId[menuActionIndex] = 684;
									menuActionCmd1[menuActionIndex] = class50_sub1_sub4_sub1.anInt1550;
									menuActionCmd2[menuActionIndex] = l;
									menuActionCmd3[menuActionIndex] = i1;
									menuActionIndex++;
								}

							menuActionName[menuActionIndex] = "Examine @lre@" + class16.name;
							menuActionId[menuActionIndex] = 1564;
							menuActionCmd1[menuActionIndex] = class50_sub1_sub4_sub1.anInt1550;
							menuActionCmd2[menuActionIndex] = l;
							menuActionCmd3[menuActionIndex] = i1;
							menuActionIndex++;
						}
					}

				}
			}
		}

	}

	public void method44(boolean flag, int i) {
		if(!flag) {
			return;
		} else {
			GameInterface.method200(aBoolean1190, i);
			return;
		}
	}

	public void method45(int i, int j, int k, int l, int i1, int j1, byte byte0, int k1) {
		if(byte0 != aByte1066)
			anInt1175 = -380;
		if(j >= 1 && l >= 1 && j <= 102 && l <= 102) {
			if(aBoolean926 && i1 != anInt1091)
				return;
			int l1 = 0;
			if(k1 == 0)
				l1 = aClass22_1164.method267(i1, j, l);
			if(k1 == 1)
				l1 = aClass22_1164.method268(j, (byte) 4, i1, l);
			if(k1 == 2)
				l1 = aClass22_1164.method269(i1, j, l);
			if(k1 == 3)
				l1 = aClass22_1164.method270(i1, j, l);
			if(l1 != 0) {
				int l2 = aClass22_1164.method271(i1, j, l, l1);
				int i2 = l1 >> 14 & 0x7fff;
				int j2 = l2 & 0x1f;
				int k2 = l2 >> 6;
				if(k1 == 0) {
					aClass22_1164.method258(l, i1, j, true);
					Class47 class47 = Class47.method423(i2);
					if(class47.aBoolean810)
						aClass46Array1260[i1].method416(k2, j, 0, l, j2, class47.aBoolean809);
				}
				if(k1 == 1)
					aClass22_1164.method259(false, j, l, i1);
				if(k1 == 2) {
					aClass22_1164.method260(l, i1, -779, j);
					Class47 class47_1 = Class47.method423(i2);
					if(j + class47_1.anInt801 > 103 || l + class47_1.anInt801 > 103 || j + class47_1.anInt775 > 103 || l + class47_1.anInt775 > 103)
						return;
					if(class47_1.aBoolean810)
						aClass46Array1260[i1].method417(anInt1055, l, j, k2, class47_1.anInt775, class47_1.aBoolean809, class47_1.anInt801);
				}
				if(k1 == 3) {
					aClass22_1164.method261(j, l, true, i1);
					Class47 class47_2 = Class47.method423(i2);
					if(class47_2.aBoolean810 && class47_2.aBoolean759)
						aClass46Array1260[i1].method419(j, (byte) -122, l);
				}
			}
			if(k >= 0) {
				int i3 = i1;
				if(i3 < 3 && (aByteArrayArrayArray1125[1][j][l] & 2) == 2)
					i3++;
				Class8.method165(k, i3, j1, l, aClass46Array1260[i1], i, j, 0, i1, aClass22_1164, anIntArrayArrayArray891);
			}
		}
	}

	public void method46(int i, byte byte0, ByteBuffer buffer) {
		buffer.method531((byte) 6);
		int j = buffer.method532(402, 8);
		if(byte0 != aByte1317)
			anInt1281 = -460;
		if(j < sessionNpcCount) {
			for(int k = j; k < sessionNpcCount; k++)
				anIntArray1295[anInt1294++] = sessionNpcList[k];

		}
		if(j > sessionNpcCount) {
			Signlink.reporterror(aString1092 + " Too many npcs");
			throw new RuntimeException("eek");
		}
		sessionNpcCount = 0;
		for(int l = 0; l < j; l++) {
			int i1 = sessionNpcList[l];
			NPC npc = sessionNpcs[i1];
			int j1 = buffer.method532(402, 1);
			if(j1 == 0) {
				sessionNpcList[sessionNpcCount++] = i1;
				npc.anInt1585 = currentTime;
			} else {
				int k1 = buffer.method532(402, 2);
				if(k1 == 0) {
					sessionNpcList[sessionNpcCount++] = i1;
					npc.anInt1585 = currentTime;
					anIntArray974[sessionNpcsAwaitingUpdate++] = i1;
				} else if(k1 == 1) {
					sessionNpcList[sessionNpcCount++] = i1;
					npc.anInt1585 = currentTime;
					int l1 = buffer.method532(402, 3);
					npc.method566(false, l1, -808);
					int j2 = buffer.method532(402, 1);
					if(j2 == 1)
						anIntArray974[sessionNpcsAwaitingUpdate++] = i1;
				} else if(k1 == 2) {
					sessionNpcList[sessionNpcCount++] = i1;
					npc.anInt1585 = currentTime;
					int i2 = buffer.method532(402, 3);
					npc.method566(true, i2, -808);
					int k2 = buffer.method532(402, 3);
					npc.method566(true, k2, -808);
					int l2 = buffer.method532(402, 1);
					if(l2 == 1)
						anIntArray974[sessionNpcsAwaitingUpdate++] = i1;
				} else if(k1 == 3)
					anIntArray1295[anInt1294++] = i1;
			}
		}

	}

	public void sendChatboxMessage(String s, String s1, int i) {
		if(i == 0 && anInt1191 != -1) {
			aString1058 = s1;
			super.anInt28 = 0;
		}
		if(anInt988 == -1)
			aBoolean1240 = true;
		for(int j = 99; j > 0; j--) {
			anIntArray1296[j] = anIntArray1296[j - 1];
			aStringArray1297[j] = aStringArray1297[j - 1];
			aStringArray1298[j] = aStringArray1298[j - 1];
		}

		anIntArray1296[0] = i;
		aStringArray1297[0] = s;
		aStringArray1298[0] = s1;
	}

	public void getNpcPos(ByteBuffer buffer, boolean flag, int i) {
		loggedIn &= flag;
		anInt1294 = 0;
		sessionNpcsAwaitingUpdate = 0;
		method46(i, (byte) -58, buffer);
		method132(buffer, i, false);
		appendNpcUpdateMask(buffer, i, 838);
		for(int j = 0; j < anInt1294; j++) {
			int k = anIntArray1295[j];
			if(((Mob) (sessionNpcs[k])).anInt1585 != currentTime) {
				sessionNpcs[k].npcDef = null;
				sessionNpcs[k] = null;
			}
		}

		if(buffer.position != i) {
			Signlink.reporterror(aString1092 + " size mismatch in getnpcpos - pos:" + buffer.position + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for(int l = 0; l < sessionNpcCount; l++)
			if(sessionNpcs[sessionNpcList[l]] == null) {
				Signlink.reporterror(aString1092 + " null entry in npc list - pos:" + l + " size:" + sessionNpcCount);
				throw new RuntimeException("eek");
			}

	}

	public void method49(int i) {
		Class47.aClass33_779.method347();
		Class47.aClass33_762.method347();
		if(i <= 0) {
			for(int j = 1; j > 0; j++)
				;
		}
		NPCDefinition.modelCache.method347();
		ItemDefinition.modelCache.method347();
		ItemDefinition.imageCache.method347();
		Player.modelCache.method347();
		Class27.aClass33_566.method347();
	}

	public void method50(boolean flag) {
		Signlink.midiplay = false;
		if(flag)
			anInt1119 = 466;
		Signlink.midifade = 0;
		Signlink.midi = "stop";
	}

	public void method51(boolean flag) {
		Class50_Sub1_Sub4_Sub2 class50_sub1_sub4_sub2 = (Class50_Sub1_Sub4_Sub2) aClass6_1282.method158();
		if(flag)
			anInt1328 = 153;
		for(; class50_sub1_sub4_sub2 != null; class50_sub1_sub4_sub2 = (Class50_Sub1_Sub4_Sub2) aClass6_1282.method160(1))
			if(class50_sub1_sub4_sub2.anInt1554 != anInt1091 || currentTime > class50_sub1_sub4_sub2.anInt1566)
				class50_sub1_sub4_sub2.method442();
			else if(currentTime >= class50_sub1_sub4_sub2.anInt1565) {
				if(class50_sub1_sub4_sub2.anInt1560 > 0) {
					NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[class50_sub1_sub4_sub2.anInt1560 - 1];
					if(class50_sub1_sub4_sub3_sub1 != null && ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610 >= 0 && ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610 < 13312
							&& ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611 >= 0 && ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611 < 13312)
						class50_sub1_sub4_sub2.method562(((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610, ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611,
								method110(((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610, (byte) 9, class50_sub1_sub4_sub2.anInt1554)
										- class50_sub1_sub4_sub2.anInt1579, currentTime, 0);
				}
				if(class50_sub1_sub4_sub2.anInt1560 < 0) {
					int i = -class50_sub1_sub4_sub2.anInt1560 - 1;
					Player class50_sub1_sub4_sub3_sub2;
					if(i == anInt961)
						class50_sub1_sub4_sub3_sub2 = sessionPlayer;
					else
						class50_sub1_sub4_sub3_sub2 = sessionPlayers[i];
					if(class50_sub1_sub4_sub3_sub2 != null && ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610 >= 0 && ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610 < 13312
							&& ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611 >= 0 && ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611 < 13312)
						class50_sub1_sub4_sub2.method562(((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611,
								method110(((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610, (byte) 9, class50_sub1_sub4_sub2.anInt1554)
										- class50_sub1_sub4_sub2.anInt1579, currentTime, 0);
				}
				class50_sub1_sub4_sub2.method563(anInt951, false);
				aClass22_1164.method252(-1, class50_sub1_sub4_sub2, (int) class50_sub1_sub4_sub2.aDouble1555, (int) class50_sub1_sub4_sub2.aDouble1557, false, 0, anInt1091, 60,
						(int) class50_sub1_sub4_sub2.aDouble1556, class50_sub1_sub4_sub2.anInt1562);
			}

		anInt1168++;
		if(anInt1168 > 51) {
			anInt1168 = 0;
			byteStream2.createFrame(248);
		}
	}

	public void method52(boolean flag) {
		aClass50_Sub1_Sub1_Sub3_1292 = new IndexedImage(aClass2_888, "titlebox", 0);
		aClass50_Sub1_Sub1_Sub3_1293 = new IndexedImage(aClass2_888, "titlebutton", 0);
		aClass50_Sub1_Sub1_Sub3Array1117 = new IndexedImage[12];
		if(flag)
			startUp();
		for(int i = 0; i < 12; i++)
			aClass50_Sub1_Sub1_Sub3Array1117[i] = new IndexedImage(aClass2_888, "runes", i);

		aClass50_Sub1_Sub1_Sub1_1017 = new RgbImage(128, 265);
		aClass50_Sub1_Sub1_Sub1_1018 = new RgbImage(128, 265);
		for(int j = 0; j < 33920; j++)
			aClass50_Sub1_Sub1_Sub1_1017.pixels[j] = aClass18_1201.anIntArray392[j];

		for(int k = 0; k < 33920; k++)
			aClass50_Sub1_Sub1_Sub1_1018.pixels[k] = aClass18_1202.anIntArray392[k];

		anIntArray1311 = new int[256];
		for(int l = 0; l < 64; l++)
			anIntArray1311[l] = l * 0x40000;

		for(int i1 = 0; i1 < 64; i1++)
			anIntArray1311[i1 + 64] = 0xff0000 + 1024 * i1;

		for(int j1 = 0; j1 < 64; j1++)
			anIntArray1311[j1 + 128] = 0xffff00 + 4 * j1;

		for(int k1 = 0; k1 < 64; k1++)
			anIntArray1311[k1 + 192] = 0xffffff;

		anIntArray1312 = new int[256];
		for(int l1 = 0; l1 < 64; l1++)
			anIntArray1312[l1] = l1 * 1024;

		for(int i2 = 0; i2 < 64; i2++)
			anIntArray1312[i2 + 64] = 65280 + 4 * i2;

		for(int j2 = 0; j2 < 64; j2++)
			anIntArray1312[j2 + 128] = 65535 + 0x40000 * j2;

		for(int k2 = 0; k2 < 64; k2++)
			anIntArray1312[k2 + 192] = 0xffffff;

		anIntArray1313 = new int[256];
		for(int l2 = 0; l2 < 64; l2++)
			anIntArray1313[l2] = l2 * 4;

		for(int i3 = 0; i3 < 64; i3++)
			anIntArray1313[i3 + 64] = 255 + 0x40000 * i3;

		for(int j3 = 0; j3 < 64; j3++)
			anIntArray1313[j3 + 128] = 0xff00ff + 1024 * j3;

		for(int k3 = 0; k3 < 64; k3++)
			anIntArray1313[k3 + 192] = 0xffffff;

		anIntArray1310 = new int[256];
		anIntArray1176 = new int[32768];
		anIntArray1177 = new int[32768];
		method83(null, 0);
		anIntArray1084 = new int[32768];
		anIntArray1085 = new int[32768];
		drawLoadingBar(10, "Connecting to fileserver");
		if(!aBoolean1243) {
			aBoolean1314 = true;
			aBoolean1243 = true;
			method12(this, 2);
		}
	}

	public void method53(long l, int i) {
		try {
			if(l == 0L)
				return;
			for(int j = 0; j < anInt859; j++) {
				if(aLongArray1130[j] != l)
					continue;
				anInt859--;
				tabRepaintRequested = true;
				for(int k = j; k < anInt859; k++) {
					aStringArray849[k] = aStringArray849[k + 1];
					anIntArray1267[k] = anIntArray1267[k + 1];
					aLongArray1130[k] = aLongArray1130[k + 1];
				}

				byteStream2.createFrame(141);
				byteStream2.putLong(l);
				break;
			}

			packetSize += i;
			return;
		} catch(RuntimeException runtimeexception) {
			Signlink.reporterror("38799, " + l + ", " + i + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void method54(int i) {
		if(anInt1113 != 0)
			return;
		int j = super.anInt28;
		if(i != 0)
			packetOpcode = byteStream4.getUnsignedByte();
		if(spellSelected == 1 && super.clickX >= 516 && super.clickY >= 160 && super.clickX <= 765 && super.clickY <= 205)
			j = 0;
		if(menuOpen) {
			if(j != 1) {
				int k = super.mouseX;
				int j1 = super.mouseY;
				if(menuScreenArea == 0) {
					k -= 4;
					j1 -= 4;
				}
				if(menuScreenArea == 1) {
					k -= 553;
					j1 -= 205;
				}
				if(menuScreenArea == 2) {
					k -= 17;
					j1 -= 357;
				}
				if(k < anInt1305 - 10 || k > anInt1305 + anInt1307 + 10 || j1 < anInt1306 - 10 || j1 > anInt1306 + anInt1308 + 10) {
					menuOpen = false;
					if(menuScreenArea == 1)
						tabRepaintRequested = true;
					if(menuScreenArea == 2)
						aBoolean1240 = true;
				}
			}
			if(j == 1) {
				int l = anInt1305;
				int k1 = anInt1306;
				int i2 = anInt1307;
				int k2 = super.clickX;
				int l2 = super.clickY;
				if(menuScreenArea == 0) {
					k2 -= 4;
					l2 -= 4;
				}
				if(menuScreenArea == 1) {
					k2 -= 553;
					l2 -= 205;
				}
				if(menuScreenArea == 2) {
					k2 -= 17;
					l2 -= 357;
				}
				int i3 = -1;
				for(int j3 = 0; j3 < menuActionIndex; j3++) {
					int k3 = k1 + 31 + (menuActionIndex - 1 - j3) * 15;
					if(k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
						i3 = j3;
				}

				if(i3 != -1)
					method120(i3, 8);
				menuOpen = false;
				if(menuScreenArea == 1)
					tabRepaintRequested = true;
				if(menuScreenArea == 2) {
					aBoolean1240 = true;
					return;
				}
			}
		} else {
			if(j == 1 && menuActionIndex > 0) {
				int i1 = menuActionId[menuActionIndex - 1];
				if(i1 == 9 || i1 == 225 || i1 == 444 || i1 == 564 || i1 == 894 || i1 == 961 || i1 == 399 || i1 == 324 || i1 == 227 || i1 == 891 || i1 == 52 || i1 == 1094) {
					int l1 = menuActionCmd2[menuActionIndex - 1];
					int j2 = menuActionCmd3[menuActionIndex - 1];
					GameInterface class13 = GameInterface.getInterface(j2);
					if(class13.aBoolean274 || class13.aBoolean217) {
						aBoolean1155 = false;
						anInt1269 = 0;
						anInt1111 = j2;
						anInt1112 = l1;
						anInt1113 = 2;
						anInt1114 = super.clickX;
						anInt1115 = super.clickY;
						if(GameInterface.getInterface(j2).parentId == anInt1169)
							anInt1113 = 1;
						if(GameInterface.getInterface(j2).parentId == anInt988)
							anInt1113 = 3;
						return;
					}
				}
			}
			if(j == 1 && (anInt1300 == 1 || method126(menuActionIndex - 1, aByte1161)) && menuActionIndex > 2)
				j = 2;
			if(j == 1 && menuActionIndex > 0)
				method120(menuActionIndex - 1, 8);
			if(j == 2 && menuActionIndex > 0)
				method108(811);
		}
	}

	public void drawTargetIndicator(int i, RgbImage class50_sub1_sub1_sub1, int j, int k) {
		int l = k * k + i * i;
		while(j >= 0)
			packetOpcode = -1;
		if(l > 4225 && l < 0x15f90) {
			int i1 = cameraX + minimapRotation & 0x7ff;
			int j1 = Model.anIntArray1710[i1];
			int k1 = Model.anIntArray1711[i1];
			j1 = (j1 * 256) / (minimapZoom + 256);
			k1 = (k1 * 256) / (minimapZoom + 256);
			int l1 = i * j1 + k * k1 >> 16;
			int i2 = i * k1 - k * j1 >> 16;
			double d = Math.atan2(l1, i2);
			int j2 = (int) (Math.sin(d) * 63D);
			int k2 = (int) (Math.cos(d) * 57D);
			mapedge.method466(256, 15, (94 + j2 + 4) - 10, 15, 20, anInt1119, 20, d, 83 - k2 - 20);
			return;
		} else {
			markMinimap(class50_sub1_sub1_sub1, k, i);
			return;
		}
	}

	public void method56(boolean flag, int i, int j, int k, int l, int i1) {
		aClass50_Sub1_Sub1_Sub3_1095.drawImage(j, i1);
		aClass50_Sub1_Sub1_Sub3_1096.drawImage(j, (i1 + k) - 16);
		DrawingArea.fillRect(k - 32, i1 + 16, anInt931, (byte) -24, 16, j);
		int j1 = ((k - 32) * k) / l;
		if(j1 < 8)
			j1 = 8;
		int k1 = ((k - 32 - j1) * i) / (l - k);
		DrawingArea.fillRect(j1, i1 + 16 + k1, anInt1080, (byte) -24, 16, j);
		DrawingArea.method454(j, anInt1135, j1, false, i1 + 16 + k1);
		DrawingArea.method454(j + 1, anInt1135, j1, false, i1 + 16 + k1);
		if(!flag)
			anInt921 = -136;
		DrawingArea.method452(j, anInt1135, i1 + 16 + k1, 16, true);
		DrawingArea.method452(j, anInt1135, i1 + 17 + k1, 16, true);
		DrawingArea.method454(j + 15, anInt1287, j1, false, i1 + 16 + k1);
		DrawingArea.method454(j + 14, anInt1287, j1 - 1, false, i1 + 17 + k1);
		DrawingArea.method452(j, anInt1287, i1 + 15 + k1 + j1, 16, true);
		DrawingArea.method452(j + 1, anInt1287, i1 + 14 + k1 + j1, 15, true);
	}

	public void method57(int i, boolean flag) {
		i = 26 / i;
		for(int j = 0; j < sessionNpcCount; j++) {
			NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[sessionNpcList[j]];
			int k = 0x20000000 + (sessionNpcList[j] << 14);
			if(class50_sub1_sub4_sub3_sub1 == null || !class50_sub1_sub4_sub3_sub1.isVisible() || class50_sub1_sub4_sub3_sub1.npcDef.aBoolean644 != flag
					|| !class50_sub1_sub4_sub3_sub1.npcDef.method360(-993))
				continue;
			int l = ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610 >> 7;
			int i1 = ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611 >> 7;
			if(l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
				continue;
			if(((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1601 == 1 && (((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610 & 0x7f) == 64 && (((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611 & 0x7f) == 64) {
				if(anIntArrayArray886[l][i1] == anInt1138)
					continue;
				anIntArrayArray886[l][i1] = anInt1138;
			}
			if(!class50_sub1_sub4_sub3_sub1.npcDef.clickable)
				k += 0x80000000;
			aClass22_1164.method252(k, class50_sub1_sub4_sub3_sub1, ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610,
					method110(((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610, (byte) 9, anInt1091),
					((Mob) (class50_sub1_sub4_sub3_sub1)).aBoolean1592, 0, anInt1091, (((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1601 - 1) * 64 + 60, ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611,
					((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1612);
		}

	}

	public void method58(int i, int j) {
		Signlink.wavevol = j;
		if(i <= 0)
			anInt1051 = 57;
	}

	public void method59(int i) {
		if(anInt873 > 0) {
			method124(true);
			return;
		}
		method125(-332, "Please wait - attempting to reestablish", "Connection lost");
		minimapLock = 0;
		if(i != 1)
			aBoolean1242 = !aBoolean1242;
		anInt1120 = 0;
		ClientSocket class17 = aClass17_1024;
		loggedIn = false;
		anInt850 = 0;
		method79(aString1092, aString1093, true);
		if(!loggedIn)
			method124(true);
		try {
			class17.method224();
			return;
		} catch(Exception _ex) {
			return;
		}
	}

	public boolean method60(int i, GameInterface class13) {
		int j = class13.contentType;
		if(i <= 0)
			packetOpcode = -1;
		if(anInt860 == 2) {
			if(j == 201) {
				aBoolean1240 = true;
				anInt1244 = 0;
				aBoolean866 = true;
				aString1026 = "";
				anInt1221 = 1;
				aString937 = "Enter name of friend to add to list";
			}
			if(j == 202) {
				aBoolean1240 = true;
				anInt1244 = 0;
				aBoolean866 = true;
				aString1026 = "";
				anInt1221 = 2;
				aString937 = "Enter name of friend to delete from list";
			}
		}
		if(j == 205) {
			anInt873 = 250;
			return true;
		}
		if(j == 501) {
			aBoolean1240 = true;
			anInt1244 = 0;
			aBoolean866 = true;
			aString1026 = "";
			anInt1221 = 4;
			aString937 = "Enter name of player to add to list";
		}
		if(j == 502) {
			aBoolean1240 = true;
			anInt1244 = 0;
			aBoolean866 = true;
			aString1026 = "";
			anInt1221 = 5;
			aString937 = "Enter name of player to delete from list";
		}
		if(j >= 300 && j <= 313) {
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = anIntArray1326[k];
			if(i2 != -1) {
				do {
					if(j1 == 0 && --i2 < 0)
						i2 = IdentityKit.anInt814 - 1;
					if(j1 == 1 && ++i2 >= IdentityKit.anInt814)
						i2 = 0;
				} while(IdentityKit.cache[i2].aBoolean821 || IdentityKit.cache[i2].anInt816 != k + (aBoolean1144 ? 0 : 7));
				anIntArray1326[k] = i2;
				aBoolean1277 = true;
			}
		}
		if(j >= 314 && j <= 323) {
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = anIntArray1099[l];
			if(k1 == 0 && --j2 < 0)
				j2 = playerBodyRecolours[l].length - 1;
			if(k1 == 1 && ++j2 >= playerBodyRecolours[l].length)
				j2 = 0;
			anIntArray1099[l] = j2;
			aBoolean1277 = true;
		}
		if(j == 324 && !aBoolean1144) {
			aBoolean1144 = true;
			method25(anInt1015);
		}
		if(j == 325 && aBoolean1144) {
			aBoolean1144 = false;
			method25(anInt1015);
		}
		if(j == 326) {
			byteStream2.createFrame(163);
			byteStream2.putByte(aBoolean1144 ? 0 : 1);
			for(int i1 = 0; i1 < 7; i1++)
				byteStream2.putByte(anIntArray1326[i1]);

			for(int l1 = 0; l1 < 5; l1++)
				byteStream2.putByte(anIntArray1099[l1]);

			return true;
		}
		if(j == 620)
			aBoolean1098 = !aBoolean1098;
		if(j >= 601 && j <= 613) {
			method15(false);
			if(aString839.length() > 0) {
				byteStream2.createFrame(184);
				byteStream2.putLong(NameUtils.nameToLong(aString839));
				byteStream2.putByte(j - 601);
				byteStream2.putByte(aBoolean1098 ? 1 : 0);
			}
		}
		return false;
	}

	public CacheArchive method61(int i, int j, String s, int k, int l, String s1) {
		byte abyte0[] = null;
		int i1 = 5;
		try {
			if(aClass23Array1228[0] != null)
				abyte0 = aClass23Array1228[0].method292(aByte898, l);
		} catch(Exception _ex) {
		}
		if(abyte0 != null) {
			/*
			 * aCRC32_1088.reset(); aCRC32_1088.update(abyte0); int j1 =
			 * (int)aCRC32_1088.getValue(); if(j1 != j) abyte0 = null;
			 */
		}
		if(abyte0 != null) {
			CacheArchive class2 = new CacheArchive(abyte0);
			return class2;
		}
		int k1 = 0;
		if(i != 14076)
			anInt1281 = -343;
		while(abyte0 == null) {
			String s2 = "Unknown error";
			drawLoadingBar(k, "Requesting " + s1);
			try {
				int l1 = 0;
				DataInputStream datainputstream = method31(s + j);
				byte abyte1[] = new byte[6];
				datainputstream.readFully(abyte1, 0, 6);
				ByteBuffer byteStream = new ByteBuffer(abyte1);
				byteStream.position = 3;
				int j2 = byteStream.getTriByte() + 6;
				int k2 = 6;
				abyte0 = new byte[j2];
				for(int l2 = 0; l2 < 6; l2++)
					abyte0[l2] = abyte1[l2];

				while(k2 < j2) {
					int i3 = j2 - k2;
					if(i3 > 1000)
						i3 = 1000;
					int k3 = datainputstream.read(abyte0, k2, i3);
					if(k3 < 0) {
						s2 = "Length error: " + k2 + "/" + j2;
						throw new IOException("EOF");
					}
					k2 += k3;
					int l3 = (k2 * 100) / j2;
					if(l3 != l1)
						drawLoadingBar(k, "Loading " + s1 + " - " + l3 + "%");
					l1 = l3;
				}
				datainputstream.close();
				try {
					if(aClass23Array1228[0] != null)
						aClass23Array1228[0].method293(abyte0.length, true, abyte0, l);
				} catch(Exception _ex) {
					aClass23Array1228[0] = null;
				}
				if(abyte0 != null) {
					/*
					 * aCRC32_1088.reset(); aCRC32_1088.update(abyte0); int j3 =
					 * (int)aCRC32_1088.getValue(); if(j3 != j) { abyte0 = null;
					 * k1++; s2 = "Checksum error: " + j3; }
					 */
				}
			} catch(IOException ioexception) {
				if(s2.equals("Unknown error"))
					s2 = "Connection error";
				abyte0 = null;
			} catch(NullPointerException _ex) {
				s2 = "Null error";
				abyte0 = null;
				if(!Signlink.reporterror)
					return null;
			} catch(ArrayIndexOutOfBoundsException _ex) {
				s2 = "Bounds error";
				abyte0 = null;
				if(!Signlink.reporterror)
					return null;
			} catch(Exception _ex) {
				s2 = "Unexpected error";
				abyte0 = null;
				if(!Signlink.reporterror)
					return null;
			}
			if(abyte0 == null) {
				for(int i2 = i1; i2 > 0; i2--) {
					if(k1 >= 3) {
						drawLoadingBar(k, "Game updated - please reload page");
						i2 = 10;
					} else {
						drawLoadingBar(k, s2 + " - Retrying in " + i2);
					}
					try {
						Thread.sleep(1000L);
					} catch(Exception _ex) {
					}
				}

				i1 *= 2;
				if(i1 > 60)
					i1 = 60;
				aBoolean900 = !aBoolean900;
			}
		}
		CacheArchive class2_1 = new CacheArchive(abyte0);
		return class2_1;
	}

	public void method10(byte byte0) {
		repaintRequested = true;
		if(byte0 == -99)
			;
	}

	public void appendNpcUpdateMask(ByteBuffer buffer, int i, int j) {
		j = 24 / j;
		for(int k = 0; k < sessionNpcsAwaitingUpdate; k++) {
			int l = anIntArray974[k];
			NPC npc = sessionNpcs[l];

			int i1 = buffer.getUnsignedByte();
			if((i1 & 0x20) != 0) {
				i1 += buffer.getUnsignedByte() << 8;
			}

			if((i1 & 1) != 0) {
				int len = buffer.getUnsignedByte();
				byte data[] = new byte[len];
				ByteBuffer byteStream_1 = new ByteBuffer(data);
				buffer.method558((byte) -73, data, len, 0);
				// playerUpdateStreams[j] = byteStream_1;
				npc.updateAppearance(byteStream_1);
			}
			/*
			 * if((i1 & 1) != 0) { npc.npcDef =
			 * NPCDefinition.forId(buffer.getUnsignedShortA()); npc.anInt1601 =
			 * npc.npcDef.boundDim; npc.anInt1600 = npc.npcDef.degreesToTurn;
			 * npc.walkAnim = npc.npcDef.walkAnim; npc.turn180Anim =
			 * npc.npcDef.turn180Anim; npc.turn90CWAnim =
			 * npc.npcDef.turn90CWAnim; npc.turn90CCWAnim =
			 * npc.npcDef.turn90CCWAnim; npc.standAnim = npc.npcDef.idleAnim; }
			 */
			if((i1 & 0x40) != 0) {
				npc.anInt1609 = buffer.getUnsignedLEShort();
				if(((Mob) (npc)).anInt1609 == 65535)
					npc.anInt1609 = -1;
			}
			if((i1 & 0x80) != 0) {
				int j1 = buffer.method540(0);
				int j2 = buffer.method540(0);
				npc.method567(currentTime, false, j1, j2);
				npc.anInt1595 = currentTime + 300;
				npc.anInt1596 = buffer.getUnsignedByte();
				npc.anInt1597 = buffer.method542(anInt1236);
			}
			if((i1 & 4) != 0) {
				npc.currentGfx = buffer.getUnsignedShort();
				int k1 = buffer.method556(3);
				npc.anInt1618 = k1 >> 16;
				npc.anInt1617 = currentTime + (k1 & 0xffff);
				npc.currentAnim = 0;
				npc.anInt1616 = 0;
				if(((Mob) (npc)).anInt1617 > currentTime)
					npc.currentAnim = -1;
				if(((Mob) (npc)).currentGfx == 65535)
					npc.currentGfx = -1;
			}
			if((i1 & 0x400) != 0) { // 20
				npc.aString1580 = buffer.getRS2String();
				npc.anInt1582 = 100;
			}
			if((i1 & 8) != 0) {
				npc.anInt1598 = buffer.getUnsignedLEShortA();
				npc.anInt1599 = buffer.getUnsignedLEShort();
			}
			if((i1 & 2) != 0) {
				int l1 = buffer.getUnsignedShort();
				if(l1 == 65535)
					l1 = -1;
				int k2 = buffer.method542(anInt1236);
				if(l1 == ((Mob) (npc)).animation && l1 != -1) {
					int i3 = Class14.aClass14Array293[l1].anInt307;
					if(i3 == 1) {
						npc.anInt1625 = 0;
						npc.anInt1626 = 0;
						npc.animationDelay = k2;
						npc.anInt1628 = 0;
					}
					if(i3 == 2)
						npc.anInt1628 = 0;
				} else if(l1 == -1 || ((Mob) (npc)).animation == -1 || Class14.aClass14Array293[l1].anInt301 >= Class14.aClass14Array293[((Mob) (npc)).animation].anInt301) {
					npc.animation = l1;
					npc.anInt1625 = 0;
					npc.anInt1626 = 0;
					npc.animationDelay = k2;
					npc.anInt1628 = 0;
					npc.anInt1613 = ((Mob) (npc)).anInt1633;
				}
			}
			if((i1 & 0x10) != 0) {
				int i2 = buffer.method542(anInt1236);
				int l2 = buffer.method542(anInt1236);
				npc.method567(currentTime, false, i2, l2);
				npc.anInt1595 = currentTime + 300;
				npc.anInt1596 = buffer.getUnsignedByte();
				npc.anInt1597 = buffer.method541(-34545);
			}
			/*
			 * if((i1 & 0x200) != 0) { System.out.println("update me: " +
			 * buffer.position + ", size: " + i); int len =
			 * buffer.getUnsignedByte(); byte data[] = new byte[len]; ByteBuffer
			 * byteStream_1 = new ByteBuffer(data); buffer.method558((byte) -73,
			 * data, len, 0); //playerUpdateStreams[j] = byteStream_1;
			 * npc.updateAppearance(byteStream_1);
			 * System.out.println("lengths: " + len + "," + buffer.position +
			 * ", size: " + i); }
			 */
		}

	}

	public void appendPlayerUpdateMask(int i, int j, Player player, int k, ByteBuffer buffer) {
		if(i != 2) {
			for(int l = 1; l > 0; l++)
				;
		}
		if((k & 8) != 0) {
			int i1 = buffer.getUnsignedShort();
			if(i1 == 65535)
				i1 = -1;
			int k2 = buffer.method542(anInt1236);
			if(i1 == ((Mob) (player)).animation && i1 != -1) {
				int k3 = Class14.aClass14Array293[i1].anInt307;
				if(k3 == 1) {
					player.anInt1625 = 0;
					player.anInt1626 = 0;
					player.animationDelay = k2;
					player.anInt1628 = 0;
				}
				if(k3 == 2)
					player.anInt1628 = 0;
			} else if(i1 == -1 || ((Mob) (player)).animation == -1 || Class14.aClass14Array293[i1].anInt301 >= Class14.aClass14Array293[((Mob) (player)).animation].anInt301) {
				player.animation = i1;
				player.anInt1625 = 0;
				player.anInt1626 = 0;
				player.animationDelay = k2;
				player.anInt1628 = 0;
				player.anInt1613 = ((Mob) (player)).anInt1633;
			}
		}
		if((k & 0x10) != 0) {
			player.aString1580 = buffer.getRS2String();
			if(((Mob) (player)).aString1580.charAt(0) == '~') {
				player.aString1580 = ((Mob) (player)).aString1580.substring(1);
				sendChatboxMessage(player.username, ((Mob) (player)).aString1580, 2);
			} else if(player == sessionPlayer)
				sendChatboxMessage(player.username, ((Mob) (player)).aString1580, 2);
			player.anInt1583 = 0;
			player.anInt1593 = 0;
			player.anInt1582 = 150;
		}
		if((k & 0x100) != 0) {
			player.anInt1602 = buffer.method540(0);
			player.anInt1604 = buffer.method541(-34545);
			player.anInt1603 = buffer.method542(anInt1236);
			player.anInt1605 = buffer.getUnsignedByte();
			player.anInt1606 = buffer.getUnsignedShort() + currentTime;
			player.anInt1607 = buffer.getUnsignedShortA() + currentTime;
			player.anInt1608 = buffer.getUnsignedByte();
			player.method564(-56);
		}
		if((k & 1) != 0) {
			player.anInt1609 = buffer.getUnsignedShortA();
			if(((Mob) (player)).anInt1609 == 65535)
				player.anInt1609 = -1;
		}
		if((k & 2) != 0) {
			player.anInt1598 = buffer.getUnsignedShort();
			player.anInt1599 = buffer.getUnsignedShort();
		}
		if((k & 0x200) != 0) {
			player.currentGfx = buffer.getUnsignedShortA();
			int j1 = buffer.method556(3);
			player.anInt1618 = j1 >> 16;
			player.anInt1617 = currentTime + (j1 & 0xffff);
			player.currentAnim = 0;
			player.anInt1616 = 0;
			if(((Mob) (player)).anInt1617 > currentTime)
				player.currentAnim = -1;
			if(((Mob) (player)).currentGfx == 65535)
				player.currentGfx = -1;
		}
		if((k & 4) != 0) {
			int k1 = buffer.getUnsignedByte();
			byte abyte0[] = new byte[k1];
			ByteBuffer byteStream_1 = new ByteBuffer(abyte0);
			buffer.method558((byte) -73, abyte0, k1, 0);
			playerUpdateStreams[j] = byteStream_1;
			player.updateAppearance(byteStream_1, 0);
		}
		if((k & 0x400) != 0) {
			int l1 = buffer.method540(0);
			int l2 = buffer.method542(anInt1236);
			player.method567(currentTime, false, l1, l2);
			player.anInt1595 = currentTime + 300;
			player.anInt1596 = buffer.method541(-34545);
			player.anInt1597 = buffer.getUnsignedByte();
		}
		if((k & 0x40) != 0) {
			int i2 = buffer.getUnsignedShort();
			int i3 = buffer.method541(-34545);
			int l3 = buffer.method540(0);
			int i4 = buffer.position;
			if(player.username != null && player.visible) {
				long l4 = NameUtils.nameToLong(player.username);
				boolean flag = false;
				if(i3 <= 1) {
					for(int j4 = 0; j4 < anInt855; j4++) {
						if(aLongArray1073[j4] != l4)
							continue;
						flag = true;
						break;
					}

				}
				if(!flag && anInt1246 == 0)
					try {
						byteStream3.position = 0;
						buffer.method559(byteStream3.payload, l3, 0, 0);
						byteStream3.position = 0;
						String s = Class31.method320(0, byteStream3, l3);
						s = Censor.method383((byte) 0, s);
						player.aString1580 = s;
						player.anInt1583 = i2 >> 8;
						player.anInt1593 = i2 & 0xff;
						player.anInt1582 = 150;
						if(i3 == 2 || i3 == 3)
							sendChatboxMessage("@cr2@" + player.username, s, 1);
						else if(i3 == 1)
							sendChatboxMessage("@cr1@" + player.username, s, 1);
						else
							sendChatboxMessage(player.username, s, 2);
					} catch(Exception exception) {
						Signlink.reporterror("cde2");
					}
			}
			buffer.position = i4 + l3;
		}
		if((k & 0x80) != 0) {
			int j2 = buffer.method542(anInt1236);
			int j3 = buffer.method541(-34545);
			player.method567(currentTime, false, j2, j3);
			player.anInt1595 = currentTime + 300;
			player.anInt1596 = buffer.method542(anInt1236);
			player.anInt1597 = buffer.getUnsignedByte();
		}
	}

	public void method64(int i) {
		if(aClass18_1198 != null)
			return;
		super.aClass18_15 = null;
		aClass18_1159 = null;
		mapDrawingArea = null;
		tabBackDrawingArea = null;
		gameScreenDrawingArea = null;
		aClass18_1108 = null;
		aClass18_1109 = null;
		for(aClass18_1110 = null; i >= 0;)
			return;

		aClass18_1201 = new GraphicsBuffer(128, 265, getGameComponent());
		DrawingArea.clear();
		aClass18_1202 = new GraphicsBuffer(128, 265, getGameComponent());
		DrawingArea.clear();
		aClass18_1198 = new GraphicsBuffer(509, 171, getGameComponent());
		DrawingArea.clear();
		aClass18_1199 = new GraphicsBuffer(360, 132, getGameComponent());
		DrawingArea.clear();
		aClass18_1200 = new GraphicsBuffer(360, 200, getGameComponent());
		DrawingArea.clear();
		aClass18_1203 = new GraphicsBuffer(202, 238, getGameComponent());
		DrawingArea.clear();
		aClass18_1204 = new GraphicsBuffer(203, 238, getGameComponent());
		DrawingArea.clear();
		aClass18_1205 = new GraphicsBuffer(74, 94, getGameComponent());
		DrawingArea.clear();
		aClass18_1206 = new GraphicsBuffer(75, 94, getGameComponent());
		DrawingArea.clear();
		if(aClass2_888 != null) {
			method139(aBoolean1207);
			method52(false);
		}
		repaintRequested = true;
	}

	public void startUp() {
		drawLoadingBar(20, "Starting up");
		if(Signlink.sunjava)
			super.anInt8 = 5;
		if(aBoolean999) {
			// aBoolean1016 = true;
			// return;
		}
		aBoolean999 = true;

		boolean validHost = false;
		String hostName = getHostName();
		String[] validHosts = { "jagex.com", "manascape.org", "127.0.0.1" };
		for(String host : validHosts) {
			if(hostName.endsWith(host)) {
				validHost = true;
				break;
			}
		}
		if(!validHost) {
			invalidHost = true;
			return;
		}

		if(Signlink.cache_dat != null) {
			for(int i = 0; i < 5; i++) {
				aClass23Array1228[i] = new Class23(i + 1, 0x927c0, Signlink.cache_dat, Signlink.cache_idx[i], 4);
			}
		}
		try {
			method86(false);
			aClass2_888 = method61(14076, anIntArray837[1], "title", 25, 1, "title screen");
			smallFont = new GameFont(false, aClass2_888, -914, "p11_full");
			normalFont = new GameFont(false, aClass2_888, -914, "p12_full");
			boldFont = new GameFont(false, aClass2_888, -914, "b12_full");
			questFont = new GameFont(true, aClass2_888, -914, "q8_full");
			method139(aBoolean1207);
			method52(false);
			CacheArchive class2 = method61(14076, anIntArray837[2], "config", 30, 2, "config");
			CacheArchive interfaceArchive = method61(14076, anIntArray837[3], "interface", 35, 3, "interface");
			CacheArchive mediaArchive = method61(14076, anIntArray837[4], "media", 40, 4, "2d graphics");
			CacheArchive class2_3 = method61(14076, anIntArray837[6], "textures", 45, 6, "textures");
			CacheArchive class2_4 = method61(14076, anIntArray837[7], "wordenc", 50, 7, "chat system");
			CacheArchive class2_5 = method61(14076, anIntArray837[8], "sounds", 55, 8, "sound effects");
			aByteArrayArrayArray1125 = new byte[4][104][104];
			anIntArrayArrayArray891 = new int[4][105][105];
			aClass22_1164 = new Class22(anIntArrayArrayArray891, 104, 4, 104, (byte) 5);
			for(int j = 0; j < 4; j++)
				aClass46Array1260[j] = new Class46(104, 0, 104);

			minimapImage = new RgbImage(512, 512);
			CacheArchive class2_6 = method61(14076, anIntArray837[5], "versionlist", 60, 5, "update list");
			drawLoadingBar(60, "Connecting to update server");
			aClass32_Sub1_1291 = new Class32_Sub1();
			aClass32_Sub1_1291.method335(class2_6, this);
			Class21.method235(aClass32_Sub1_1291.method343(553));
			Model.method574(aClass32_Sub1_1291.method340(0, -31140), aClass32_Sub1_1291);
			if(!aBoolean926) {
				anInt1270 = 0;
				aBoolean1271 = true;
				aClass32_Sub1_1291.method329(2, anInt1270);
				while(aClass32_Sub1_1291.method333() > 0) {
					method77(false);
					try {
						Thread.sleep(100L);
					} catch(Exception _ex) {
					}
					if(aClass32_Sub1_1291.anInt1379 > 3) {
						method19("ondemand");
						return;
					}
				}
			}
			drawLoadingBar(65, "Requesting animations");
			int k = aClass32_Sub1_1291.method340(1, -31140);
			for(int l = 0; l < k; l++)
				aClass32_Sub1_1291.method329(1, l);

			while(aClass32_Sub1_1291.method333() > 0) {
				int i1 = k - aClass32_Sub1_1291.method333();
				if(i1 > 0)
					drawLoadingBar(65, "Loading animations - " + (i1 * 100) / k + "%");
				method77(false);
				try {
					Thread.sleep(100L);
				} catch(Exception _ex) {
				}
				if(aClass32_Sub1_1291.anInt1379 > 3) {
					method19("ondemand");
					return;
				}
			}
			drawLoadingBar(70, "Requesting models");
			k = aClass32_Sub1_1291.method340(0, -31140);
			for(int j1 = 0; j1 < k; j1++) {
				int k1 = aClass32_Sub1_1291.method325(j1, -493);
				if((k1 & 1) != 0)
					aClass32_Sub1_1291.method329(0, j1);
			}

			k = aClass32_Sub1_1291.method333();
			while(aClass32_Sub1_1291.method333() > 0) {
				int l1 = k - aClass32_Sub1_1291.method333();
				if(l1 > 0)
					drawLoadingBar(70, "Loading models - " + (l1 * 100) / k + "%");
				method77(false);
				try {
					Thread.sleep(100L);
				} catch(Exception _ex) {
				}
			}
			if(aClass23Array1228[0] != null) {
				drawLoadingBar(75, "Requesting maps");
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 47, 48, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 47, 48, 1));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 48, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 48, 1));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 49, 48, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 49, 48, 1));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 47, 47, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 47, 47, 1));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 47, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 47, 1));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 148, 0));
				aClass32_Sub1_1291.method329(3, aClass32_Sub1_1291.method344(0, 48, 148, 1));
				k = aClass32_Sub1_1291.method333();
				while(aClass32_Sub1_1291.method333() > 0) {
					int i2 = k - aClass32_Sub1_1291.method333();
					if(i2 > 0)
						drawLoadingBar(75, "Loading maps - " + (i2 * 100) / k + "%");
					method77(false);
					try {
						Thread.sleep(100L);
					} catch(Exception _ex) {
					}
				}
			}
			k = aClass32_Sub1_1291.method340(0, -31140);
			for(int j2 = 0; j2 < k; j2++) {
				int k2 = aClass32_Sub1_1291.method325(j2, -493);
				byte byte0 = 0;
				if((k2 & 8) != 0)
					byte0 = 10;
				else if((k2 & 0x20) != 0)
					byte0 = 9;
				else if((k2 & 0x10) != 0)
					byte0 = 8;
				else if((k2 & 0x40) != 0)
					byte0 = 7;
				else if((k2 & 0x80) != 0)
					byte0 = 6;
				else if((k2 & 2) != 0)
					byte0 = 5;
				else if((k2 & 4) != 0)
					byte0 = 4;
				if((k2 & 1) != 0)
					byte0 = 3;
				if(byte0 != 0)
					aClass32_Sub1_1291.method327(-44, 0, byte0, j2);
			}

			aClass32_Sub1_1291.method332(aBoolean925, (byte) 109);
			if(!aBoolean926) {
				k = aClass32_Sub1_1291.method340(2, -31140);
				for(int l2 = 1; l2 < k; l2++)
					if(aClass32_Sub1_1291.method328(l2, aBoolean963))
						aClass32_Sub1_1291.method327(-44, 2, (byte) 1, l2);

			}
			k = aClass32_Sub1_1291.method340(0, -31140);
			for(int i3 = 0; i3 < k; i3++) {
				int j3 = aClass32_Sub1_1291.method325(i3, -493);
				if(j3 == 0 && aClass32_Sub1_1291.anInt1350 < 200)
					aClass32_Sub1_1291.method327(-44, 0, (byte) 1, i3);
			}

			drawLoadingBar(80, "Unpacking media");
			invback = new IndexedImage(mediaArchive, "invback", 0);
			chatback = new IndexedImage(mediaArchive, "chatback", 0);
			mapback = new IndexedImage(mediaArchive, "mapback", 0);
			backbase1 = new IndexedImage(mediaArchive, "backbase1", 0);
			backbase2 = new IndexedImage(mediaArchive, "backbase2", 0);
			backhmid1 = new IndexedImage(mediaArchive, "backhmid1", 0);
			
			for(int k3 = 0; k3 < 13; k3++) {
				sideicons[k3] = new IndexedImage(mediaArchive, "sideicons", k3);
			}
			
			dataOrbBg[0] = new IndexedImage(mediaArchive, "orbbg", 0);
			dataOrbBg[1] = new IndexedImage(mediaArchive, "orbbg", 1);
			for(int d = 0; d < 4; d++) {
				dataOrbFills[d] = new IndexedImage(mediaArchive, "orbfill", d);
				dataOrbIcons[d] = new IndexedImage(mediaArchive, "orbicon", d);
			}
			
			compassImage = new RgbImage(mediaArchive, "compass", 0);
			mapedge = new RgbImage(mediaArchive, "mapedge", 0);
			mapedge.method458();
			for(int l3 = 0; l3 < 72; l3++)
				mapscenes[l3] = new IndexedImage(mediaArchive, "mapscene", l3);

			for(int i4 = 0; i4 < 70; i4++)
				mapfunctions[i4] = new RgbImage(mediaArchive, "mapfunction", i4);

			for(int j4 = 0; j4 < 5; j4++)
				hitmarks[j4] = new RgbImage(mediaArchive, "hitmarks", j4);

			for(int k4 = 0; k4 < 6; k4++)
				pkHeadicons[k4] = new RgbImage(mediaArchive, "headicons_pk", k4);

			for(int l4 = 0; l4 < 9; l4++)
				prayerHeadicons[l4] = new RgbImage(mediaArchive, "headicons_prayer", l4);

			for(int i5 = 0; i5 < 6; i5++)
				hintHeadicons[i5] = new RgbImage(mediaArchive, "headicons_hint", i5);

			multiwayOverlay = new RgbImage(mediaArchive, "overlay_multiway", 0);
			mapFlag = new RgbImage(mediaArchive, "mapmarker", 0);
			aClass50_Sub1_Sub1_Sub1_1037 = new RgbImage(mediaArchive, "mapmarker", 1);
			for(int j5 = 0; j5 < 8; j5++)
				cross[j5] = new RgbImage(mediaArchive, "cross", j5);

			mapDotItem = new RgbImage(mediaArchive, "mapdots", 0);
			mapDotNpc = new RgbImage(mediaArchive, "mapdots", 1);
			mapDotPlayer = new RgbImage(mediaArchive, "mapdots", 2);
			mapDotFriend = new RgbImage(mediaArchive, "mapdots", 3);
			mapDotTeam = new RgbImage(mediaArchive, "mapdots", 4);
			aClass50_Sub1_Sub1_Sub3_1095 = new IndexedImage(mediaArchive, "scrollbar", 0);
			aClass50_Sub1_Sub1_Sub3_1096 = new IndexedImage(mediaArchive, "scrollbar", 1);
			redstone0 = new IndexedImage(mediaArchive, "redstone1", 0);
			redstone1_2 = new IndexedImage(mediaArchive, "redstone2", 0);
			redstone3 = new IndexedImage(mediaArchive, "redstone3", 0);
			redstone6 = new IndexedImage(mediaArchive, "redstone1", 0);
			redstone6.method487(0);
			redstone4_5 = new IndexedImage(mediaArchive, "redstone2", 0);
			redstone4_5.method487(0);
			redstone7 = new IndexedImage(mediaArchive, "redstone1", 0);
			redstone7.method488((byte) 7);
			redstone8_9 = new IndexedImage(mediaArchive, "redstone2", 0);
			redstone8_9.method488((byte) 7);
			redstone10 = new IndexedImage(mediaArchive, "redstone3", 0);
			redstone10.method488((byte) 7);
			redstone13 = new IndexedImage(mediaArchive, "redstone1", 0);
			redstone13.method487(0);
			redstone13.method488((byte) 7);
			redstone11_12 = new IndexedImage(mediaArchive, "redstone2", 0);
			redstone11_12.method487(0);
			redstone11_12.method488((byte) 7);
			for(int k5 = 0; k5 < 2; k5++)
				aClass50_Sub1_Sub1_Sub3Array1142[k5] = new IndexedImage(mediaArchive, "mod_icons", k5);

			RgbImage rgbImage = new RgbImage(mediaArchive, "backleft1", 0);
			aClass18_906 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backleft2", 0);
			aClass18_907 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backright1", 0);
			aClass18_908 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backright2", 0);
			aClass18_909 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backtop1", 0);
			aClass18_910 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backvmid1", 0);
			backvmid1 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backvmid2", 0);
			aClass18_912 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backvmid3", 0);
			aClass18_913 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			rgbImage = new RgbImage(mediaArchive, "backhmid2", 0);
			aClass18_914 = new GraphicsBuffer(rgbImage.imgWidth, rgbImage.imgHeight, getGameComponent());
			rgbImage.draw(0, 0);
			int l5 = (int) (Math.random() * 21D) - 10;
			int i6 = (int) (Math.random() * 21D) - 10;
			int j6 = (int) (Math.random() * 21D) - 10;
			int k6 = (int) (Math.random() * 41D) - 20;
			for(int l6 = 0; l6 < 100; l6++) {
				if(mapfunctions[l6] != null)
					mapfunctions[l6].addRGB(l5 + k6, i6 + k6, j6 + k6);
				if(mapscenes[l6] != null)
					mapscenes[l6].method489(j6 + k6, i6 + k6, l5 + k6, -235);
			}

			drawLoadingBar(83, "Unpacking textures");
			Rasterizer.method497(class2_3, -17551);
			Rasterizer.method501(0.80000000000000004D, (byte) 6);
			Rasterizer.method496((byte) 7, 20);
			drawLoadingBar(86, "Unpacking config");
			Class14.method204(class2, 36135);
			Class47.method426(class2);
			Class15.method207(class2, 36135);
			ItemDefinition.unpack(class2);
			NPCDefinition.unpack(class2);
			IdentityKit.method434(class2, 36135);
			Class27.method305(class2, 36135);
			Class43.method371(class2, 36135);
			Class49.method440(class2, 36135);
			ItemDefinition.memberItemsEnabled = aBoolean925;
			if(!aBoolean926) {
				drawLoadingBar(90, "Unpacking sounds");
				byte abyte0[] = class2_5.getDataForName("sounds.dat");
				ByteBuffer byteStream = new ByteBuffer(abyte0);
				Class38.method365(byteStream, 36135);
			}
			drawLoadingBar(95, "Unpacking interfaces");
			GameFont aclass50_sub1_sub1_sub2[] = { smallFont, normalFont, boldFont, questFont };
			GameInterface.unpackAll(aclass50_sub1_sub1_sub2, interfaceArchive, mediaArchive);
			drawLoadingBar(100, "Preparing game engine");
			
			for(int pixelY = 0; pixelY < 33; pixelY++) {
				int j7 = 999;
				int l7 = 0;
				//for(int pixelX = 0; pixelX < 34; pixelX++) {
				for(int pixelX = (0 + MAP_OFFSET_X); pixelX < (34 + MAP_OFFSET_X); pixelX++) {
					if(mapback.imgPixels[pixelX + pixelY * mapback.imgWidth] == 0) {
						if(j7 == 999)
							j7 = pixelX;
						continue;
					}
					if(j7 == 999)
						continue;
					l7 = pixelX;
					break;
				}

				compassShape1[pixelY] = j7;
				compassShape2[pixelY] = l7 - j7;
			}

			for(int pixelY = 5; pixelY < 156; pixelY++) {
				int i8 = 999;
				int k8 = 0;
				//for(int i9 = 25; i9 < 172; i9++) {
				for(int pixelX = 59; pixelX < 206; pixelX++) {
					if(mapback.imgPixels[pixelX + pixelY * mapback.imgWidth] == 0 && (pixelX > (34 + MAP_OFFSET_X) || pixelY > 34)) {
						if(i8 == 999)
							i8 = pixelX;
						continue;
					}
					if(i8 == 999)
						continue;
					k8 = pixelX;
					break;
				}

				//mapShape1[pixelX - 5] = i8 - 25;
				mapShape1[pixelY - 5] = i8 - 59;
				mapShape2[pixelY - 5] = k8 - i8;
			}

			Rasterizer.method494(503, 7, 765);
			anIntArray1003 = Rasterizer.lineOffsets;
			Rasterizer.method494(96, 7, 479);
			anIntArray1000 = Rasterizer.lineOffsets;
			Rasterizer.method494(261, 7, 190);
			anIntArray1001 = Rasterizer.lineOffsets;
			Rasterizer.method494(334, 7, 512);
			anIntArray1002 = Rasterizer.lineOffsets;
			int ai[] = new int[9];
			for(int l8 = 0; l8 < 9; l8++) {
				int j9 = 128 + l8 * 32 + 15;
				int k9 = 600 + j9 * 3;
				int l9 = Rasterizer.anIntArray1536[j9];
				ai[l8] = k9 * l9 >> 16;
			}

			Class22.method277(334, 22845, ai, 800, 500, 512);
			Censor.unpackFilters(class2_4);
			aClass7_1248 = new Class7(this, (byte) -116);
			method12(aClass7_1248, 10);
			Class50_Sub1_Sub4_Sub5.aClient1723 = this;
			Class47.aClient770 = this;
			NPCDefinition.aClient629 = this;
			return;
		} catch(Exception exception) {
			Signlink.reporterror("loaderror " + aString1027 + " " + anInt1322);
		}
		loadingError = true;
	}

	public void method65(int i, int j) {
		while(j >= 0)
			return;
		if(!aBoolean926) {
			for(int k = 0; k < anIntArray1290.length; k++) {
				int l = anIntArray1290[k];
				if(Rasterizer.anIntArray1546[l] >= i) {
					IndexedImage class50_sub1_sub1_sub3 = Rasterizer.aClass50_Sub1_Sub1_Sub3Array1540[l];
					int i1 = class50_sub1_sub1_sub3.imgWidth * class50_sub1_sub1_sub3.imgHeight - 1;
					int j1 = class50_sub1_sub1_sub3.imgWidth * anInt951 * 2;
					byte abyte0[] = class50_sub1_sub1_sub3.imgPixels;
					byte abyte1[] = aByteArray1245;
					for(int k1 = 0; k1 <= i1; k1++)
						abyte1[k1] = abyte0[k1 - j1 & i1];

					class50_sub1_sub1_sub3.imgPixels = abyte1;
					aByteArray1245 = abyte0;
					Rasterizer.method499(l, 9);
				}
			}

		}
	}

	public void method66(int i, GameInterface class13, int j, int k, int l, int i1, int j1, int k1) {
		if(j1 != 23658)
			return;
		if(class13.type != 0 || class13.children == null || class13.mouseOverTriggered)
			return;
		if(i1 < l || k1 < i || i1 > l + class13.width || k1 > i + class13.height)
			return;
		int l1 = class13.children.length;
		for(int i2 = 0; i2 < l1; i2++) {
			int j2 = class13.childrenX[i2] + l;
			int k2 = (class13.childrenY[i2] + i) - k;
			GameInterface child = GameInterface.getInterface(class13.children[i2]);
			j2 += child.offsetX;
			k2 += child.offsetY;
			if((child.triggersOnMouseOver >= 0 || child.anInt261 != 0) && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height)
				if(child.triggersOnMouseOver >= 0)
					anInt915 = child.triggersOnMouseOver;
				else
					anInt915 = child.id;
			if(child.type == 8 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height)
				anInt1315 = child.id;
			if(child.type == 0) {
				method66(k2, child, j, child.anInt231, j2, i1, 23658, k1);
				if(child.scrollHeight > child.height)
					method42(child.scrollHeight, k2, child, (byte) 102, k1, j, i1, child.height, j2 + child.width);
			} else {
				if(child.actionType == 1 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					boolean flag = false;
					if(child.contentType != 0)
						flag = method23(child, 8);
					if(!flag) {
						menuActionName[menuActionIndex] = child.aString268;
						menuActionId[menuActionIndex] = 352;
						menuActionCmd3[menuActionIndex] = child.id;
						menuActionIndex++;
					}
				}
				if(child.actionType == 2 && spellSelected == 0 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					String s = child.aString281;
					if(s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					menuActionName[menuActionIndex] = s + " @gre@" + child.aString211;
					menuActionId[menuActionIndex] = 70;
					menuActionCmd3[menuActionIndex] = child.id;
					menuActionIndex++;
				}
				if(child.actionType == 3 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					menuActionName[menuActionIndex] = "Close";
					if(j == 3)
						menuActionId[menuActionIndex] = 55;
					else
						menuActionId[menuActionIndex] = 639;
					menuActionCmd3[menuActionIndex] = child.id;
					menuActionIndex++;
				}
				if(child.actionType == 4 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					menuActionName[menuActionIndex] = child.aString268;
					menuActionId[menuActionIndex] = 890;
					menuActionCmd3[menuActionIndex] = child.id;
					menuActionIndex++;
				}
				if(child.actionType == 5 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					menuActionName[menuActionIndex] = child.aString268;
					menuActionId[menuActionIndex] = 518;
					menuActionCmd3[menuActionIndex] = child.id;
					menuActionIndex++;
				}
				if(child.actionType == 6 && !aBoolean1239 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width && k1 < k2 + child.height) {
					menuActionName[menuActionIndex] = child.aString268;
					menuActionId[menuActionIndex] = 575;
					menuActionCmd3[menuActionIndex] = child.id;
					menuActionIndex++;
				}
				if(child.type == 2) {
					int l2 = 0;
					for(int i3 = 0; i3 < child.height; i3++) {
						for(int j3 = 0; j3 < child.width; j3++) {
							int k3 = j2 + j3 * (32 + child.anInt263);
							int l3 = k2 + i3 * (32 + child.anInt244);
							if(l2 < 20) {
								k3 += child.anIntArray221[l2];
								l3 += child.anIntArray213[l2];
							}
							if(i1 >= k3 && k1 >= l3 && i1 < k3 + 32 && k1 < l3 + 32) {
								anInt1063 = l2;
								anInt1064 = child.id;
								if(child.anIntArray269[l2] > 0) {
									ItemDefinition class16 = ItemDefinition.forId(child.anIntArray269[l2] - 1);
									if(itemSelected == 1 && child.aBoolean229) {
										if(child.id != anInt1148 || l2 != anInt1147) {
											menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @lre@" + class16.name;
											menuActionId[menuActionIndex] = 903;
											menuActionCmd1[menuActionIndex] = class16.id;
											menuActionCmd2[menuActionIndex] = l2;
											menuActionCmd3[menuActionIndex] = child.id;
											menuActionIndex++;
										}
									} else if(spellSelected == 1 && child.aBoolean229) {
										if((spellUsableOn & 0x10) == 16) {
											menuActionName[menuActionIndex] = spellTooltip + " @lre@" + class16.name;
											menuActionId[menuActionIndex] = 361;
											menuActionCmd1[menuActionIndex] = class16.id;
											menuActionCmd2[menuActionIndex] = l2;
											menuActionCmd3[menuActionIndex] = child.id;
											menuActionIndex++;
										}
									} else {
										if(child.aBoolean229) {
											for(int i4 = 4; i4 >= 3; i4--)
												if(class16.actions != null && class16.actions[i4] != null) {
													menuActionName[menuActionIndex] = class16.actions[i4] + " @lre@" + class16.name;
													if(i4 == 3)
														menuActionId[menuActionIndex] = 227;
													if(i4 == 4)
														menuActionId[menuActionIndex] = 891;
													menuActionCmd1[menuActionIndex] = class16.id;
													menuActionCmd2[menuActionIndex] = l2;
													menuActionCmd3[menuActionIndex] = child.id;
													menuActionIndex++;
												} else if(i4 == 4) {
													menuActionName[menuActionIndex] = "Drop @lre@" + class16.name;
													menuActionId[menuActionIndex] = 891;
													menuActionCmd1[menuActionIndex] = class16.id;
													menuActionCmd2[menuActionIndex] = l2;
													menuActionCmd3[menuActionIndex] = child.id;
													menuActionIndex++;
												}

										}
										if(child.aBoolean288) {
											menuActionName[menuActionIndex] = "Use @lre@" + class16.name;
											menuActionId[menuActionIndex] = 52;
											menuActionCmd1[menuActionIndex] = class16.id;
											menuActionCmd2[menuActionIndex] = l2;
											menuActionCmd3[menuActionIndex] = child.id;
											menuActionIndex++;
										}
										if(child.aBoolean229 && class16.actions != null) {
											for(int j4 = 2; j4 >= 0; j4--)
												if(class16.actions[j4] != null) {
													menuActionName[menuActionIndex] = class16.actions[j4] + " @lre@" + class16.name;
													if(j4 == 0)
														menuActionId[menuActionIndex] = 961;
													if(j4 == 1)
														menuActionId[menuActionIndex] = 399;
													if(j4 == 2)
														menuActionId[menuActionIndex] = 324;
													menuActionCmd1[menuActionIndex] = class16.id;
													menuActionCmd2[menuActionIndex] = l2;
													menuActionCmd3[menuActionIndex] = child.id;
													menuActionIndex++;
												}

										}
										if(child.aStringArray262 != null) {
											for(int k4 = 4; k4 >= 0; k4--)
												if(child.aStringArray262[k4] != null) {
													menuActionName[menuActionIndex] = child.aStringArray262[k4] + " @lre@" + class16.name;
													if(k4 == 0)
														menuActionId[menuActionIndex] = 9;
													if(k4 == 1)
														menuActionId[menuActionIndex] = 225;
													if(k4 == 2)
														menuActionId[menuActionIndex] = 444;
													if(k4 == 3)
														menuActionId[menuActionIndex] = 564;
													if(k4 == 4)
														menuActionId[menuActionIndex] = 894;
													menuActionCmd1[menuActionIndex] = class16.id;
													menuActionCmd2[menuActionIndex] = l2;
													menuActionCmd3[menuActionIndex] = child.id;
													menuActionIndex++;
												}

										}
										menuActionName[menuActionIndex] = "Examine @lre@" + class16.name;
										menuActionId[menuActionIndex] = 1094;
										menuActionCmd1[menuActionIndex] = class16.id;
										menuActionCmd2[menuActionIndex] = l2;
										menuActionCmd3[menuActionIndex] = child.id;
										menuActionIndex++;
									}
								}
							}
							l2++;
						}

					}

				}
			}
		}

	}

	public void method67(int i) {
		for(int j = 0; j < sessionNpcCount; j++) {
			int k = sessionNpcList[j];
			NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[k];
			if(class50_sub1_sub4_sub3_sub1 != null)
				method68(class50_sub1_sub4_sub3_sub1.npcDef.boundDim, (byte) -97, class50_sub1_sub4_sub3_sub1);
		}

		if(i != -37214)
			byteStream2.putByte(41);
	}

	public void method68(int i, byte byte0, Mob class50_sub1_sub4_sub3) {
		if(class50_sub1_sub4_sub3.anInt1610 < 128 || class50_sub1_sub4_sub3.anInt1611 < 128 || class50_sub1_sub4_sub3.anInt1610 >= 13184 || class50_sub1_sub4_sub3.anInt1611 >= 13184) {
			class50_sub1_sub4_sub3.animation = -1;
			class50_sub1_sub4_sub3.currentGfx = -1;
			class50_sub1_sub4_sub3.anInt1606 = 0;
			class50_sub1_sub4_sub3.anInt1607 = 0;
			class50_sub1_sub4_sub3.anInt1610 = class50_sub1_sub4_sub3.anIntArray1586[0] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			class50_sub1_sub4_sub3.anInt1611 = class50_sub1_sub4_sub3.anIntArray1587[0] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			class50_sub1_sub4_sub3.method564(-56);
		}
		if(class50_sub1_sub4_sub3 == sessionPlayer
				&& (class50_sub1_sub4_sub3.anInt1610 < 1536 || class50_sub1_sub4_sub3.anInt1611 < 1536 || class50_sub1_sub4_sub3.anInt1610 >= 11776 || class50_sub1_sub4_sub3.anInt1611 >= 11776)) {
			class50_sub1_sub4_sub3.animation = -1;
			class50_sub1_sub4_sub3.currentGfx = -1;
			class50_sub1_sub4_sub3.anInt1606 = 0;
			class50_sub1_sub4_sub3.anInt1607 = 0;
			class50_sub1_sub4_sub3.anInt1610 = class50_sub1_sub4_sub3.anIntArray1586[0] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			class50_sub1_sub4_sub3.anInt1611 = class50_sub1_sub4_sub3.anIntArray1587[0] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			class50_sub1_sub4_sub3.method564(-56);
		}
		if(class50_sub1_sub4_sub3.anInt1606 > currentTime)
			method69(class50_sub1_sub4_sub3, true);
		else if(class50_sub1_sub4_sub3.anInt1607 >= currentTime)
			method70(class50_sub1_sub4_sub3, -31135);
		else
			method71(class50_sub1_sub4_sub3, 0);
		method72((byte) 8, class50_sub1_sub4_sub3);
		method73(class50_sub1_sub4_sub3, -136);
		if(byte0 == -97)
			;
	}

	public void method69(Mob class50_sub1_sub4_sub3, boolean flag) {
		if(!flag)
			aBoolean963 = !aBoolean963;
		int i = class50_sub1_sub4_sub3.anInt1606 - currentTime;
		int j = class50_sub1_sub4_sub3.anInt1602 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
		int k = class50_sub1_sub4_sub3.anInt1604 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
		class50_sub1_sub4_sub3.anInt1610 += (j - class50_sub1_sub4_sub3.anInt1610) / i;
		class50_sub1_sub4_sub3.anInt1611 += (k - class50_sub1_sub4_sub3.anInt1611) / i;
		class50_sub1_sub4_sub3.anInt1623 = 0;
		if(class50_sub1_sub4_sub3.anInt1608 == 0)
			class50_sub1_sub4_sub3.anInt1584 = 1024;
		if(class50_sub1_sub4_sub3.anInt1608 == 1)
			class50_sub1_sub4_sub3.anInt1584 = 1536;
		if(class50_sub1_sub4_sub3.anInt1608 == 2)
			class50_sub1_sub4_sub3.anInt1584 = 0;
		if(class50_sub1_sub4_sub3.anInt1608 == 3)
			class50_sub1_sub4_sub3.anInt1584 = 512;
	}

	public void method70(Mob class50_sub1_sub4_sub3, int i) {
		if(class50_sub1_sub4_sub3.anInt1607 == currentTime || class50_sub1_sub4_sub3.animation == -1 || class50_sub1_sub4_sub3.animationDelay != 0
				|| class50_sub1_sub4_sub3.anInt1626 + 1 > Class14.aClass14Array293[class50_sub1_sub4_sub3.animation].method205(0, class50_sub1_sub4_sub3.anInt1625)) {
			int j = class50_sub1_sub4_sub3.anInt1607 - class50_sub1_sub4_sub3.anInt1606;
			int k = currentTime - class50_sub1_sub4_sub3.anInt1606;
			int l = class50_sub1_sub4_sub3.anInt1602 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			int i1 = class50_sub1_sub4_sub3.anInt1604 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			int j1 = class50_sub1_sub4_sub3.anInt1603 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			int k1 = class50_sub1_sub4_sub3.anInt1605 * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
			class50_sub1_sub4_sub3.anInt1610 = (l * (j - k) + j1 * k) / j;
			class50_sub1_sub4_sub3.anInt1611 = (i1 * (j - k) + k1 * k) / j;
		}
		class50_sub1_sub4_sub3.anInt1623 = 0;
		if(class50_sub1_sub4_sub3.anInt1608 == 0)
			class50_sub1_sub4_sub3.anInt1584 = 1024;
		if(class50_sub1_sub4_sub3.anInt1608 == 1)
			class50_sub1_sub4_sub3.anInt1584 = 1536;
		if(class50_sub1_sub4_sub3.anInt1608 == 2)
			class50_sub1_sub4_sub3.anInt1584 = 0;
		if(class50_sub1_sub4_sub3.anInt1608 == 3)
			class50_sub1_sub4_sub3.anInt1584 = 512;
		class50_sub1_sub4_sub3.anInt1612 = class50_sub1_sub4_sub3.anInt1584;
		if(i == -31135)
			;
	}

	public void method71(Mob class50_sub1_sub4_sub3, int i) {
		class50_sub1_sub4_sub3.anInt1588 = class50_sub1_sub4_sub3.standAnim;
		if(class50_sub1_sub4_sub3.anInt1633 == 0) {
			class50_sub1_sub4_sub3.anInt1623 = 0;
			return;
		}
		if(class50_sub1_sub4_sub3.animation != -1 && class50_sub1_sub4_sub3.animationDelay == 0) {
			Class14 class14 = Class14.aClass14Array293[class50_sub1_sub4_sub3.animation];
			if(class50_sub1_sub4_sub3.anInt1613 > 0 && class14.anInt305 == 0) {
				class50_sub1_sub4_sub3.anInt1623++;
				return;
			}
			if(class50_sub1_sub4_sub3.anInt1613 <= 0 && class14.anInt306 == 0) {
				class50_sub1_sub4_sub3.anInt1623++;
				return;
			}
		}
		int j = class50_sub1_sub4_sub3.anInt1610;
		int k = class50_sub1_sub4_sub3.anInt1611;
		int l = class50_sub1_sub4_sub3.anIntArray1586[class50_sub1_sub4_sub3.anInt1633 - 1] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
		int i1 = class50_sub1_sub4_sub3.anIntArray1587[class50_sub1_sub4_sub3.anInt1633 - 1] * 128 + class50_sub1_sub4_sub3.anInt1601 * 64;
		if(l - j > 256 || l - j < -256 || i1 - k > 256 || i1 - k < -256) {
			class50_sub1_sub4_sub3.anInt1610 = l;
			class50_sub1_sub4_sub3.anInt1611 = i1;
			return;
		}
		if(j < l) {
			if(k < i1)
				class50_sub1_sub4_sub3.anInt1584 = 1280;
			else if(k > i1)
				class50_sub1_sub4_sub3.anInt1584 = 1792;
			else
				class50_sub1_sub4_sub3.anInt1584 = 1536;
		} else if(j > l) {
			if(k < i1)
				class50_sub1_sub4_sub3.anInt1584 = 768;
			else if(k > i1)
				class50_sub1_sub4_sub3.anInt1584 = 256;
			else
				class50_sub1_sub4_sub3.anInt1584 = 512;
		} else if(k < i1)
			class50_sub1_sub4_sub3.anInt1584 = 1024;
		else
			class50_sub1_sub4_sub3.anInt1584 = 0;
		int j1 = class50_sub1_sub4_sub3.anInt1584 - class50_sub1_sub4_sub3.anInt1612 & 0x7ff;
		if(j1 > 1024)
			j1 -= 2048;
		int k1 = class50_sub1_sub4_sub3.turn180Anim;
		if(i != 0)
			byteStream2.putByte(34);
		if(j1 >= -256 && j1 <= 256)
			k1 = class50_sub1_sub4_sub3.walkAnim;
		else if(j1 >= 256 && j1 < 768)
			k1 = class50_sub1_sub4_sub3.turn90CCWAnim;
		else if(j1 >= -768 && j1 <= -256)
			k1 = class50_sub1_sub4_sub3.turn90CWAnim;
		if(k1 == -1)
			k1 = class50_sub1_sub4_sub3.walkAnim;
		class50_sub1_sub4_sub3.anInt1588 = k1;
		int l1 = 4;
		if(class50_sub1_sub4_sub3.anInt1612 != class50_sub1_sub4_sub3.anInt1584 && class50_sub1_sub4_sub3.anInt1609 == -1 && class50_sub1_sub4_sub3.anInt1600 != 0)
			l1 = 2;
		if(class50_sub1_sub4_sub3.anInt1633 > 2)
			l1 = 6;
		if(class50_sub1_sub4_sub3.anInt1633 > 3)
			l1 = 8;
		if(class50_sub1_sub4_sub3.anInt1623 > 0 && class50_sub1_sub4_sub3.anInt1633 > 1) {
			l1 = 8;
			class50_sub1_sub4_sub3.anInt1623--;
		}
		if(class50_sub1_sub4_sub3.aBooleanArray1591[class50_sub1_sub4_sub3.anInt1633 - 1])
			l1 <<= 1;
		if(l1 >= 8 && class50_sub1_sub4_sub3.anInt1588 == class50_sub1_sub4_sub3.walkAnim && class50_sub1_sub4_sub3.runAnim != -1)
			class50_sub1_sub4_sub3.anInt1588 = class50_sub1_sub4_sub3.runAnim;
		if(j < l) {
			class50_sub1_sub4_sub3.anInt1610 += l1;
			if(class50_sub1_sub4_sub3.anInt1610 > l)
				class50_sub1_sub4_sub3.anInt1610 = l;
		} else if(j > l) {
			class50_sub1_sub4_sub3.anInt1610 -= l1;
			if(class50_sub1_sub4_sub3.anInt1610 < l)
				class50_sub1_sub4_sub3.anInt1610 = l;
		}
		if(k < i1) {
			class50_sub1_sub4_sub3.anInt1611 += l1;
			if(class50_sub1_sub4_sub3.anInt1611 > i1)
				class50_sub1_sub4_sub3.anInt1611 = i1;
		} else if(k > i1) {
			class50_sub1_sub4_sub3.anInt1611 -= l1;
			if(class50_sub1_sub4_sub3.anInt1611 < i1)
				class50_sub1_sub4_sub3.anInt1611 = i1;
		}
		if(class50_sub1_sub4_sub3.anInt1610 == l && class50_sub1_sub4_sub3.anInt1611 == i1) {
			class50_sub1_sub4_sub3.anInt1633--;
			if(class50_sub1_sub4_sub3.anInt1613 > 0)
				class50_sub1_sub4_sub3.anInt1613--;
		}
	}

	public void method72(byte byte0, Mob class50_sub1_sub4_sub3) {
		if(byte0 != 8)
			anInt928 = aISAAC_899.getNextValue();
		if(class50_sub1_sub4_sub3.anInt1600 == 0)
			return;
		if(class50_sub1_sub4_sub3.anInt1609 != -1 && class50_sub1_sub4_sub3.anInt1609 < 32768) {
			NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[class50_sub1_sub4_sub3.anInt1609];
			if(class50_sub1_sub4_sub3_sub1 != null) {
				int l = class50_sub1_sub4_sub3.anInt1610 - ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1610;
				int j1 = class50_sub1_sub4_sub3.anInt1611 - ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1611;
				if(l != 0 || j1 != 0)
					class50_sub1_sub4_sub3.anInt1584 = (int) (Math.atan2(l, j1) * 325.94900000000001D) & 0x7ff;
			}
		}
		if(class50_sub1_sub4_sub3.anInt1609 >= 32768) {
			int i = class50_sub1_sub4_sub3.anInt1609 - 32768;
			if(i == anInt961)
				i = anInt969;
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[i];
			if(class50_sub1_sub4_sub3_sub2 != null) {
				int k1 = class50_sub1_sub4_sub3.anInt1610 - ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610;
				int l1 = class50_sub1_sub4_sub3.anInt1611 - ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611;
				if(k1 != 0 || l1 != 0)
					class50_sub1_sub4_sub3.anInt1584 = (int) (Math.atan2(k1, l1) * 325.94900000000001D) & 0x7ff;
			}
		}
		if((class50_sub1_sub4_sub3.anInt1598 != 0 || class50_sub1_sub4_sub3.anInt1599 != 0) && (class50_sub1_sub4_sub3.anInt1633 == 0 || class50_sub1_sub4_sub3.anInt1623 > 0)) {
			int j = class50_sub1_sub4_sub3.anInt1610 - (class50_sub1_sub4_sub3.anInt1598 - anInt1040 - anInt1040) * 64;
			int i1 = class50_sub1_sub4_sub3.anInt1611 - (class50_sub1_sub4_sub3.anInt1599 - anInt1041 - anInt1041) * 64;
			if(j != 0 || i1 != 0)
				class50_sub1_sub4_sub3.anInt1584 = (int) (Math.atan2(j, i1) * 325.94900000000001D) & 0x7ff;
			class50_sub1_sub4_sub3.anInt1598 = 0;
			class50_sub1_sub4_sub3.anInt1599 = 0;
		}
		int k = class50_sub1_sub4_sub3.anInt1584 - class50_sub1_sub4_sub3.anInt1612 & 0x7ff;
		if(k != 0) {
			if(k < class50_sub1_sub4_sub3.anInt1600 || k > 2048 - class50_sub1_sub4_sub3.anInt1600)
				class50_sub1_sub4_sub3.anInt1612 = class50_sub1_sub4_sub3.anInt1584;
			else if(k > 1024)
				class50_sub1_sub4_sub3.anInt1612 -= class50_sub1_sub4_sub3.anInt1600;
			else
				class50_sub1_sub4_sub3.anInt1612 += class50_sub1_sub4_sub3.anInt1600;
			class50_sub1_sub4_sub3.anInt1612 &= 0x7ff;
			if(class50_sub1_sub4_sub3.anInt1588 == class50_sub1_sub4_sub3.standAnim && class50_sub1_sub4_sub3.anInt1612 != class50_sub1_sub4_sub3.anInt1584) {
				if(class50_sub1_sub4_sub3.standTurnAnim != -1) {
					class50_sub1_sub4_sub3.anInt1588 = class50_sub1_sub4_sub3.standTurnAnim;
					return;
				}
				class50_sub1_sub4_sub3.anInt1588 = class50_sub1_sub4_sub3.walkAnim;
			}
		}
	}

	public void method73(Mob class50_sub1_sub4_sub3, int i) {
		while(i >= 0)
			anInt1328 = aISAAC_899.getNextValue();
		class50_sub1_sub4_sub3.aBoolean1592 = false;
		if(class50_sub1_sub4_sub3.anInt1588 != -1) {
			Class14 class14 = Class14.aClass14Array293[class50_sub1_sub4_sub3.anInt1588];
			class50_sub1_sub4_sub3.anInt1590++;
			if(class50_sub1_sub4_sub3.anInt1589 < class14.anInt294 && class50_sub1_sub4_sub3.anInt1590 > class14.method205(0, class50_sub1_sub4_sub3.anInt1589)) {
				class50_sub1_sub4_sub3.anInt1590 = 1;
				class50_sub1_sub4_sub3.anInt1589++;
			}
			if(class50_sub1_sub4_sub3.anInt1589 >= class14.anInt294) {
				class50_sub1_sub4_sub3.anInt1590 = 1;
				class50_sub1_sub4_sub3.anInt1589 = 0;
			}
		}
		if(class50_sub1_sub4_sub3.currentGfx != -1 && currentTime >= class50_sub1_sub4_sub3.anInt1617) {
			if(class50_sub1_sub4_sub3.currentAnim < 0)
				class50_sub1_sub4_sub3.currentAnim = 0;
			Class14 class14_1 = Class27.aClass27Array554[class50_sub1_sub4_sub3.currentGfx].aClass14_558;
			class50_sub1_sub4_sub3.anInt1616++;
			if(class50_sub1_sub4_sub3.currentAnim < class14_1.anInt294 && class50_sub1_sub4_sub3.anInt1616 > class14_1.method205(0, class50_sub1_sub4_sub3.currentAnim)) {
				class50_sub1_sub4_sub3.anInt1616 = 1;
				class50_sub1_sub4_sub3.currentAnim++;
			}
			if(class50_sub1_sub4_sub3.currentAnim >= class14_1.anInt294 && (class50_sub1_sub4_sub3.currentAnim < 0 || class50_sub1_sub4_sub3.currentAnim >= class14_1.anInt294))
				class50_sub1_sub4_sub3.currentGfx = -1;
		}
		if(class50_sub1_sub4_sub3.animation != -1 && class50_sub1_sub4_sub3.animationDelay <= 1) {
			Class14 class14_2 = Class14.aClass14Array293[class50_sub1_sub4_sub3.animation];
			if(class14_2.anInt305 == 1 && class50_sub1_sub4_sub3.anInt1613 > 0 && class50_sub1_sub4_sub3.anInt1606 <= currentTime && class50_sub1_sub4_sub3.anInt1607 < currentTime) {
				class50_sub1_sub4_sub3.animationDelay = 1;
				return;
			}
		}
		if(class50_sub1_sub4_sub3.animation != -1 && class50_sub1_sub4_sub3.animationDelay == 0) {
			Class14 class14_3 = Class14.aClass14Array293[class50_sub1_sub4_sub3.animation];
			class50_sub1_sub4_sub3.anInt1626++;
			if(class50_sub1_sub4_sub3.anInt1625 < class14_3.anInt294 && class50_sub1_sub4_sub3.anInt1626 > class14_3.method205(0, class50_sub1_sub4_sub3.anInt1625)) {
				class50_sub1_sub4_sub3.anInt1626 = 1;
				class50_sub1_sub4_sub3.anInt1625++;
			}
			if(class50_sub1_sub4_sub3.anInt1625 >= class14_3.anInt294) {
				class50_sub1_sub4_sub3.anInt1625 -= class14_3.anInt298;
				class50_sub1_sub4_sub3.anInt1628++;
				if(class50_sub1_sub4_sub3.anInt1628 >= class14_3.anInt304)
					class50_sub1_sub4_sub3.animation = -1;
				if(class50_sub1_sub4_sub3.anInt1625 < 0 || class50_sub1_sub4_sub3.anInt1625 >= class14_3.anInt294)
					class50_sub1_sub4_sub3.animation = -1;
			}
			class50_sub1_sub4_sub3.aBoolean1592 = class14_3.aBoolean300;
		}
		if(class50_sub1_sub4_sub3.animationDelay > 0)
			class50_sub1_sub4_sub3.animationDelay--;
	}

	public void drawGameScreen() {
		if(anInt1053 != -1 && (loadingStage == 2 || super.aClass18_15 != null)) {
			if(loadingStage == 2) {
				method88(anInt951, anInt1053, (byte) 5);
				if(anInt960 != -1)
					method88(anInt951, anInt960, (byte) 5);
				anInt951 = 0;
				method147(anInt1140);
				super.aClass18_15.initDrawingArea();
				Rasterizer.lineOffsets = anIntArray1003;
				DrawingArea.clear();
				repaintRequested = true;
				GameInterface class13 = GameInterface.getInterface(anInt1053);
				if(class13.width == 512 && class13.height == 334 && class13.type == 0) {
					class13.width = 765;
					class13.height = 503;
				}
				drawInterface(0, 0, 0, class13);
				if(anInt960 != -1) {
					GameInterface class13_1 = GameInterface.getInterface(anInt960);
					if(class13_1.width == 512 && class13_1.height == 334 && class13_1.type == 0) {
						class13_1.width = 765;
						class13_1.height = 503;
					}
					drawInterface(0, 0, 0, class13_1);
				}
				if(!menuOpen) {
					method91(-521);
					method34((byte) -79);
				} else {
					drawMenu();
				}
			}
			super.aClass18_15.drawGraphics(0, 0, super.graphics);
			return;
		}
		
		if(repaintRequested || super.sizeUpdateRequired) {
			if(super.sizeUpdateRequired) {
			System.out.println("cunt fucking whore dick tits");
			super.sizeUpdateRequired = false;
			}
			method122(-906);
			repaintRequested = false;
			aClass18_906.drawGraphics(0, 4, super.graphics);
			aClass18_907.drawGraphics(0, 357, super.graphics);
			aClass18_908.drawGraphics(722, 4, super.graphics);
			aClass18_909.drawGraphics(743, 205, super.graphics);
			aClass18_910.drawGraphics(0, 0, super.graphics);
			//backvmid1.drawGraphics(516, 4, super.graphics);
			aClass18_912.drawGraphics(516, 205, super.graphics);
			aClass18_913.drawGraphics(496, 357, super.graphics);
			aClass18_914.drawGraphics(0, 338, super.graphics);
			tabRepaintRequested = true;
			aBoolean1240 = true;
			aBoolean950 = true;
			aBoolean1212 = true;
			
			if(loadingStage != 2) {
				gameScreenDrawingArea.drawGraphics(4, 4, super.graphics);
				//mapDrawingArea.drawGraphics(550, 4, super.graphics);
				mapDrawingArea.drawGraphics(516, 4, super.graphics);
			}
			
			anInt1237++;
			
			if(anInt1237 > 85) {
				anInt1237 = 0;
				byteStream2.createFrame(168);
			}
		}
		
		if(loadingStage == 2)
			renderGameView();
		
		if(menuOpen && menuScreenArea == 1)
			tabRepaintRequested = true;
		
		if(anInt1089 != -1) {
			boolean flag = method88(anInt951, anInt1089, (byte) 5);
			if(flag)
				tabRepaintRequested = true;
		}
		
		if(anInt1332 == 2)
			tabRepaintRequested = true;
		
		if(anInt1113 == 2)
			tabRepaintRequested = true;
		
		if(tabRepaintRequested) {
			drawTabInterfaceArea();
			tabRepaintRequested = false;
		}
		
		if(anInt988 == -1 && anInt1244 == 0) {
			aClass13_1249.anInt231 = anInt1107 - anInt851 - 77;
			
			if(super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332)
				method42(anInt1107, 0, aClass13_1249, (byte) 102, super.mouseY - 357, -1, super.mouseX - 17, 77, 463);
			
			int j = anInt1107 - 77 - aClass13_1249.anInt231;
			if(j < 0)
				j = 0;
			
			if(j > anInt1107 - 77)
				j = anInt1107 - 77;
			
			if(anInt851 != j) {
				anInt851 = j;
				aBoolean1240 = true;
			}
		}
		
		if(anInt988 == -1 && anInt1244 == 3) {
			int k = anInt862 * 14 + 7;
			aClass13_1249.anInt231 = anInt865;
			if(super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332)
				method42(k, 0, aClass13_1249, (byte) 102, super.mouseY - 357, -1, super.mouseX - 17, 77, 463);
			
			int i1 = aClass13_1249.anInt231;
			if(i1 < 0)
				i1 = 0;
			
			if(i1 > k - 77)
				i1 = k - 77;
			
			if(anInt865 != i1) {
				anInt865 = i1;
				aBoolean1240 = true;
			}
		}
		
		if(anInt988 != -1) {
			boolean flag1 = method88(anInt951, anInt988, (byte) 5);
			if(flag1)
				aBoolean1240 = true;
		}
		
		if(anInt1332 == 3)
			aBoolean1240 = true;
		
		if(anInt1113 == 3)
			aBoolean1240 = true;
		
		if(aString1058 != null)
			aBoolean1240 = true;
		
		if(menuOpen && menuScreenArea == 2)
			aBoolean1240 = true;
		
		if(aBoolean1240) {
			method84(0);
			aBoolean1240 = false;
		}
		
		if(loadingStage == 2) {
			drawMinimap();
			//mapDrawingArea.drawGraphics(550, 4, super.graphics);
			mapDrawingArea.drawGraphics(516, 4, super.graphics);
		}
		
		if(flashingSidebarId != -1)
			aBoolean950 = true;
		
		if(aBoolean950) {
			if(flashingSidebarId != -1 && flashingSidebarId == selectedTab) {
				flashingSidebarId = -1;
				byteStream2.createFrame(119);
				byteStream2.putByte(selectedTab);
			}
			
			aBoolean950 = false;
			aClass18_1110.initDrawingArea();
			backhmid1.drawImage(0, 0);
			
			if(anInt1089 == -1) {
				if(tabInterfaceIds[selectedTab] != -1) {
					if(selectedTab == 0)
						redstone0.drawImage(22, 10);
					if(selectedTab == 1)
						redstone1_2.drawImage(54, 8);
					if(selectedTab == 2)
						redstone1_2.drawImage(82, 8);
					if(selectedTab == 3)
						redstone3.drawImage(110, 8);
					if(selectedTab == 4)
						redstone4_5.drawImage(153, 8);
					if(selectedTab == 5)
						redstone4_5.drawImage(181, 8);
					if(selectedTab == 6)
						redstone6.drawImage(209, 9);
				}
				
				if(tabInterfaceIds[0] != -1 && (flashingSidebarId != 0 || currentTime % 20 < 10))
					sideicons[0].drawImage(25, 8);
				if(tabInterfaceIds[1] != -1 && (flashingSidebarId != 1 || currentTime % 20 < 10))
					sideicons[1].drawImage(50, 7);
				if(tabInterfaceIds[2] != -1 && (flashingSidebarId != 2 || currentTime % 20 < 10))
					sideicons[2].drawImage(79, 7);
				if(tabInterfaceIds[3] != -1 && (flashingSidebarId != 3 || currentTime % 20 < 10))
					sideicons[3].drawImage(114, 12);
				if(tabInterfaceIds[4] != -1 && (flashingSidebarId != 4 || currentTime % 20 < 10))
					sideicons[4].drawImage(149, 11);
				if(tabInterfaceIds[5] != -1 && (flashingSidebarId != 5 || currentTime % 20 < 10))
					sideicons[5].drawImage(177, 8);
				if(tabInterfaceIds[6] != -1 && (flashingSidebarId != 6 || currentTime % 20 < 10))
					sideicons[6].drawImage(205, 11);
			}
			
			aClass18_1110.drawGraphics(516, 160, super.graphics);
			aClass18_1109.initDrawingArea();
			backbase2.drawImage(0, 0);
			if(anInt1089 == -1) {
				if(tabInterfaceIds[selectedTab] != -1) {
					if(selectedTab == 7)
						redstone7.drawImage(42, 0);
					if(selectedTab == 8)
						redstone8_9.drawImage(74, 0);
					if(selectedTab == 9)
						redstone8_9.drawImage(102, 0);
					if(selectedTab == 10)
						redstone10.drawImage(130, 1);
					if(selectedTab == 11)
						redstone11_12.drawImage(173, 0);
					if(selectedTab == 12)
						redstone11_12.drawImage(201, 0);
					if(selectedTab == 13)
						redstone13.drawImage(229, 0);
				}
				
				if(tabInterfaceIds[8] != -1 && (flashingSidebarId != 8 || currentTime % 20 < 10))
					sideicons[7].drawImage(71, 0);
				if(tabInterfaceIds[9] != -1 && (flashingSidebarId != 9 || currentTime % 20 < 10))
					sideicons[8].drawImage(99, 0);
				if(tabInterfaceIds[10] != -1 && (flashingSidebarId != 10 || currentTime % 20 < 10))
					sideicons[9].drawImage(134, 2);
				if(tabInterfaceIds[11] != -1 && (flashingSidebarId != 11 || currentTime % 20 < 10))
					sideicons[10].drawImage(172, 0);
				if(tabInterfaceIds[12] != -1 && (flashingSidebarId != 12 || currentTime % 20 < 10))
					sideicons[11].drawImage(195, 1);
				if(tabInterfaceIds[13] != -1 && (flashingSidebarId != 13 || currentTime % 20 < 10))
					sideicons[12].drawImage(222, 1);
			}
			aClass18_1109.drawGraphics(496, 466, super.graphics);
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
		}
		
		if(aBoolean1212) {
			aBoolean1212 = false;
			aClass18_1108.initDrawingArea();
			backbase1.drawImage(0, 0);
			normalFont.drawCenteredText(true, 0xffffff, 55, 28, "Public chat");
			if(anInt1006 == 0)
				normalFont.drawCenteredText(true, 65280, 55, 41, "On");
			if(anInt1006 == 1)
				normalFont.drawCenteredText(true, 0xffff00, 55, 41, "Friends");
			if(anInt1006 == 2)
				normalFont.drawCenteredText(true, 0xff0000, 55, 41, "Off");
			if(anInt1006 == 3)
				normalFont.drawCenteredText(true, 65535, 55, 41, "Hide");
			normalFont.drawCenteredText(true, 0xffffff, 184, 28, "Private chat");
			if(anInt887 == 0)
				normalFont.drawCenteredText(true, 65280, 184, 41, "On");
			if(anInt887 == 1)
				normalFont.drawCenteredText(true, 0xffff00, 184, 41, "Friends");
			if(anInt887 == 2)
				normalFont.drawCenteredText(true, 0xff0000, 184, 41, "Off");
			normalFont.drawCenteredText(true, 0xffffff, 324, 28, "Trade/compete");
			if(anInt1227 == 0)
				normalFont.drawCenteredText(true, 65280, 324, 41, "On");
			if(anInt1227 == 1)
				normalFont.drawCenteredText(true, 0xffff00, 324, 41, "Friends");
			if(anInt1227 == 2)
				normalFont.drawCenteredText(true, 0xff0000, 324, 41, "Off");
			normalFont.drawCenteredText(true, 0xffffff, 458, 33, "Report abuse");
			aClass18_1108.drawGraphics(0, 453, super.graphics);
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
		}
		
		anInt951 = 0;
	}

	public void method75(int i) {
		packetSize += i;
		if(anInt1223 == 0)
			return;
		GameFont class50_sub1_sub1_sub2 = normalFont;
		int j = 0;
		if(anInt1057 != 0)
			j = 1;
		for(int k = 0; k < 100; k++)
			if(aStringArray1298[k] != null) {
				int l = anIntArray1296[k];
				String s = aStringArray1297[k];
				byte byte0 = 0;
				if(s != null && s.startsWith("@cr1@")) {
					s = s.substring(5);
					byte0 = 1;
				}
				if(s != null && s.startsWith("@cr2@")) {
					s = s.substring(5);
					byte0 = 2;
				}
				if((l == 3 || l == 7) && (l == 7 || anInt887 == 0 || anInt887 == 1 && method148(13292, s))) {
					int i1 = 329 - j * 13;
					int l1 = 4;
					class50_sub1_sub1_sub2.method474("From", l1, i1, 0);
					class50_sub1_sub1_sub2.method474("From", l1, i1 - 1, 65535);
					l1 += class50_sub1_sub1_sub2.getFormattedStringWidth("From ");
					if(byte0 == 1) {
						aClass50_Sub1_Sub1_Sub3Array1142[0].drawImage(l1, i1 - 12);
						l1 += 14;
					}
					if(byte0 == 2) {
						aClass50_Sub1_Sub1_Sub3Array1142[1].drawImage(l1, i1 - 12);
						l1 += 14;
					}
					class50_sub1_sub1_sub2.method474(s + ": " + aStringArray1298[k], l1, i1, 0);
					class50_sub1_sub1_sub2.method474(s + ": " + aStringArray1298[k], l1, i1 - 1, 65535);
					if(++j >= 5)
						return;
				}
				if(l == 5 && anInt887 < 2) {
					int j1 = 329 - j * 13;
					class50_sub1_sub1_sub2.method474(aStringArray1298[k], 4, j1, 0);
					class50_sub1_sub1_sub2.method474(aStringArray1298[k], 4, j1 - 1, 65535);
					if(++j >= 5)
						return;
				}
				if(l == 6 && anInt887 < 2) {
					int k1 = 329 - j * 13;
					class50_sub1_sub1_sub2.method474("To " + s + ": " + aStringArray1298[k], 4, k1, 0);
					class50_sub1_sub1_sub2.method474("To " + s + ": " + aStringArray1298[k], 4, k1 - 1, 65535);
					if(++j >= 5)
						return;
				}
			}

	}

	public void init() {
		nodeId = Integer.parseInt(getParameter("nodeid"));
		portOffset = Integer.parseInt(getParameter("portoff"));
		String paramLowmem = getParameter("lowmem");
		if(paramLowmem != null && paramLowmem.equals("1"))
			method101(true);
		else
			method27(true);
		String paramFree = getParameter("free");
		if(paramFree != null && paramFree.equals("1"))
			aBoolean925 = false;
		else
			aBoolean925 = true;
		createApplet(GameApplet.DEFAULT_WIDTH, GameApplet.DEFAULT_HEIGHT);
	}

	public void method76(int i) {
		while(i >= 0)
			aClass6ArrayArrayArray1323 = null;
		for(Class50_Sub1_Sub4_Sub6 class50_sub1_sub4_sub6 = (Class50_Sub1_Sub4_Sub6) aClass6_1210.method158(); class50_sub1_sub4_sub6 != null; class50_sub1_sub4_sub6 = (Class50_Sub1_Sub4_Sub6) aClass6_1210
				.method160(1))
			if(class50_sub1_sub4_sub6.anInt1731 != anInt1091 || class50_sub1_sub4_sub6.aBoolean1736)
				class50_sub1_sub4_sub6.method442();
			else if(currentTime >= class50_sub1_sub4_sub6.anInt1740) {
				class50_sub1_sub4_sub6.method604((byte) 1, anInt951);
				if(class50_sub1_sub4_sub6.aBoolean1736)
					class50_sub1_sub4_sub6.method442();
				else
					aClass22_1164.method252(-1, class50_sub1_sub4_sub6, class50_sub1_sub4_sub6.anInt1732, class50_sub1_sub4_sub6.anInt1734, false, 0, class50_sub1_sub4_sub6.anInt1731, 60,
							class50_sub1_sub4_sub6.anInt1733, 0);
			}

	}

	public void method77(boolean flag) {
		if(flag)
			packetOpcode = -1;
		do {
			Class50_Sub1_Sub3 class50_sub1_sub3;
			do {
				class50_sub1_sub3 = aClass32_Sub1_1291.method330();
				if(class50_sub1_sub3 == null)
					return;
				if(class50_sub1_sub3.anInt1467 == 0) {
					Model.method575(class50_sub1_sub3.aByteArray1470, class50_sub1_sub3.anInt1468, (byte) 7);
					if((aClass32_Sub1_1291.method325(class50_sub1_sub3.anInt1468, -493) & 0x62) != 0) {
						tabRepaintRequested = true;
						if(anInt988 != -1 || anInt1191 != -1)
							aBoolean1240 = true;
					}
				}
				if(class50_sub1_sub3.anInt1467 == 1 && class50_sub1_sub3.aByteArray1470 != null)
					Class21.method236(class50_sub1_sub3.aByteArray1470, true);
				if(class50_sub1_sub3.anInt1467 == 2 && class50_sub1_sub3.anInt1468 == anInt1270 && class50_sub1_sub3.aByteArray1470 != null)
					method24(aBoolean1271, class50_sub1_sub3.aByteArray1470, 659);
				if(class50_sub1_sub3.anInt1467 == 3 && loadingStage == 1) {
					for(int i = 0; i < aByteArrayArray838.length; i++) {
						if(anIntArray857[i] == class50_sub1_sub3.anInt1468) {
							aByteArrayArray838[i] = class50_sub1_sub3.aByteArray1470;
							if(class50_sub1_sub3.aByteArray1470 == null)
								anIntArray857[i] = -1;
							break;
						}
						if(anIntArray858[i] != class50_sub1_sub3.anInt1468)
							continue;
						aByteArrayArray1232[i] = class50_sub1_sub3.aByteArray1470;
						if(class50_sub1_sub3.aByteArray1470 == null)
							anIntArray858[i] = -1;
						break;
					}

				}
			} while(class50_sub1_sub3.anInt1467 != 93 || !aClass32_Sub1_1291.method334(class50_sub1_sub3.anInt1468, false));
			Class8.method169(aClass32_Sub1_1291, new ByteBuffer(class50_sub1_sub3.aByteArray1470), (byte) -3);
		} while(true);
	}

	public boolean method78(int i) {
		if(i <= 0) {
			for(int j = 1; j > 0; j++)
				;
		}
		return Signlink.wavereplay();
	}

	public void method79(String username, String password, boolean flag) {
		Signlink.errorname = username;
		try {
			if(!flag) {
				aString957 = "";
				aString958 = "Connecting to server...";
				drawLoginScreen(true);
			}
			aClass17_1024 = new ClientSocket((byte) 2, method32(43594 + portOffset), this);
			long l = NameUtils.nameToLong(username);
			int i = (int) (l >> 16 & 31L);
			byteStream2.position = 0;
			byteStream2.putByte(14);
			byteStream2.putByte(i);
			aClass17_1024.method228(0, 2, 0, byteStream2.payload);
			for(int j = 0; j < 8; j++)
				aClass17_1024.method225();

			int k = aClass17_1024.method225();
			int i1 = k;
			if(k == 0) {
				aClass17_1024.method227(byteStream4.payload, 0, 8);
				byteStream4.position = 0;
				aLong930 = byteStream4.getLong();
				int ai[] = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong930 >> 32);
				ai[3] = (int) aLong930;
				byteStream2.position = 0;
				byteStream2.putByte(10);
				byteStream2.putInt(ai[0]);
				byteStream2.putInt(ai[1]);
				byteStream2.putInt(ai[2]);
				byteStream2.putInt(ai[3]);
				byteStream2.putInt(Signlink.uid);
				byteStream2.writeString(username);
				byteStream2.writeString(password);
				byteStream2.startRSAEncryption();
				byteStream1.position = 0;
				if(flag)
					byteStream1.putByte(18);
				else
					byteStream1.putByte(16);
				byteStream1.putByte(byteStream2.position + 36 + 1 + 1 + 2);
				byteStream1.putByte(255);
				byteStream1.putShort(RS_REVISION);
				byteStream1.putShort(RV_REVISION);
				byteStream1.putByte(aBoolean926 ? 1 : 0);
				for(int l1 = 0; l1 < 9; l1++)
					byteStream1.putInt(anIntArray837[l1]);

				byteStream1.putBytes(byteStream2.payload, 0, byteStream2.position, 0);
				byteStream2.encryption = new IsaacCipher(anInt1175, ai);
				for(int j2 = 0; j2 < 4; j2++)
					ai[j2] += 50;

				aISAAC_899 = new IsaacCipher(anInt1175, ai);
				aClass17_1024.method228(0, byteStream1.position, 0, byteStream1.payload);
				k = aClass17_1024.method225();
			}
			if(k == 1) {
				try {
					Thread.sleep(2000L);
				} catch(Exception _ex) {
				}
				method79(username, password, flag);
				return;
			}
			if(k == 2) {
				anInt867 = aClass17_1024.method225();
				aBoolean962 = aClass17_1024.method225() == 1;
				aLong902 = 0L;
				anInt1299 = 0;
				aClass7_1248.anInt136 = 0;
				super.aBoolean19 = true;
				aBoolean1275 = true;
				loggedIn = true;
				byteStream2.position = 0;
				byteStream4.position = 0;
				packetOpcode = -1;
				anInt903 = -1;
				anInt904 = -1;
				anInt905 = -1;
				packetSize = 0;
				anInt871 = 0;
				anInt1057 = 0;
				anInt873 = 0;
				anInt1197 = 0;
				menuActionIndex = 0;
				menuOpen = false;
				super.anInt20 = 0;
				for(int j1 = 0; j1 < 100; j1++)
					aStringArray1298[j1] = null;

				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				anInt1035 = 0;
				anInt853 = (int) (Math.random() * 100D) - 50;
				anInt1009 = (int) (Math.random() * 110D) - 55;
				anInt1255 = (int) (Math.random() * 80D) - 40;
				minimapRotation = (int) (Math.random() * 120D) - 60;
				minimapZoom = (int) (Math.random() * 30D) - 20;
				cameraX = (int) (Math.random() * 20D) - 10 & 0x7ff;
				minimapLock = 0;
				anInt1276 = -1;
				anInt1120 = 0;
				anInt1121 = 0;
				anInt971 = 0;
				sessionNpcCount = 0;
				for(int i2 = 0; i2 < anInt968; i2++) {
					sessionPlayers[i2] = null;
					playerUpdateStreams[i2] = null;
				}

				for(int k2 = 0; k2 < 16384; k2++)
					sessionNpcs[k2] = null;

				sessionPlayer = sessionPlayers[anInt969] = new Player();
				aClass6_1282.method162();
				aClass6_1210.method162();
				for(int l2 = 0; l2 < 4; l2++) {
					for(int i3 = 0; i3 < 104; i3++) {
						for(int k3 = 0; k3 < 104; k3++)
							aClass6ArrayArrayArray1323[l2][i3][k3] = null;

					}

				}

				aClass6_1261 = new NodeList(true);
				anInt860 = 0;
				anInt859 = 0;
				method44(aBoolean1190, anInt1191);
				anInt1191 = -1;
				method44(aBoolean1190, anInt988);
				anInt988 = -1;
				method44(aBoolean1190, anInt1169);
				anInt1169 = -1;
				method44(aBoolean1190, anInt1053);
				anInt1053 = -1;
				method44(aBoolean1190, anInt960);
				anInt960 = -1;
				method44(aBoolean1190, anInt1089);
				anInt1089 = -1;
				method44(aBoolean1190, anInt1279);
				anInt1279 = -1;
				aBoolean1239 = false;
				selectedTab = 3;
				anInt1244 = 0;
				menuOpen = false;
				aBoolean866 = false;
				aString1058 = null;
				anInt1319 = 0;
				flashingSidebarId = -1;
				aBoolean1144 = true;
				method25(anInt1015);
				for(int j3 = 0; j3 < 5; j3++)
					anIntArray1099[j3] = 0;

				for(int l3 = 0; l3 < 5; l3++) {
					aStringArray1069[l3] = null;
					aBooleanArray1070[l3] = false;
				}

				anInt1100 = 0;
				anInt1165 = 0;
				anInt1235 = 0;
				anInt1052 = 0;
				anInt1139 = 0;
				anInt841 = 0;
				anInt1230 = 0;
				anInt1013 = 0;
				anInt1049 = 0;
				anInt1162 = 0;
				method122(-906);
				return;
			}
			if(k == 3) {
				aString957 = "";
				aString958 = "Invalid username or password.";
				return;
			}
			if(k == 4) {
				aString957 = "Your account has been disabled.";
				aString958 = "Please check your message-centre for details.";
				return;
			}
			if(k == 5) {
				aString957 = "Your account is already logged in.";
				aString958 = "Try again in 60 secs...";
				return;
			}
			if(k == 6) {
				aString957 = "ManaScape has been updated!";
				aString958 = "Please reload this page.";
				return;
			}
			if(k == 7) {
				aString957 = "This world is full.";
				aString958 = "Please use a different world.";
				return;
			}
			if(k == 8) {
				aString957 = "Unable to connect.";
				aString958 = "Login server offline.";
				return;
			}
			if(k == 9) {
				aString957 = "Login limit exceeded.";
				aString958 = "Too many connections from your address.";
				return;
			}
			if(k == 10) {
				aString957 = "Unable to connect.";
				aString958 = "Bad session id.";
				return;
			}
			if(k == 12) {
				aString957 = "You need a members account to login to this world.";
				aString958 = "Please subscribe, or use a different world.";
				return;
			}
			if(k == 13) {
				aString957 = "Could not complete login.";
				aString958 = "Please try using a different world.";
				return;
			}
			if(k == 14) {
				aString957 = "The server is being updated.";
				aString958 = "Please wait 1 minute and try again.";
				return;
			}
			if(k == 15) {
				loggedIn = true;
				byteStream2.position = 0;
				byteStream4.position = 0;
				packetOpcode = -1;
				anInt903 = -1;
				anInt904 = -1;
				anInt905 = -1;
				packetSize = 0;
				anInt871 = 0;
				anInt1057 = 0;
				menuActionIndex = 0;
				menuOpen = false;
				aLong1229 = System.currentTimeMillis();
				return;
			}
			if(k == 16) {
				aString957 = "Login attempts exceeded.";
				aString958 = "Please wait 1 minute and try again.";
				return;
			}
			if(k == 17) {
				aString957 = "You are standing in a members-only area.";
				aString958 = "To play on this world move to a free area first";
				return;
			}
			if(k == 18) {
				aString957 = "Account locked as we suspect it has been stolen.";
				aString958 = "Press 'recover a locked account' on front page.";
				return;
			}
			if(k == 20) {
				aString957 = "Invalid loginserver requested";
				aString958 = "Please try using a different world.";
				return;
			}
			if(k == 21) {
				int k1 = aClass17_1024.method225();
				for(k1 += 3; k1 >= 0; k1--) {
					aString957 = "You have only just left another world";
					aString958 = "Your profile will be transferred in: " + k1;
					drawLoginScreen(true);
					try {
						Thread.sleep(1200L);
					} catch(Exception _ex) {
					}
				}

				method79(username, password, flag);
				return;
			}
			if(k == 22) {
				aString957 = "Malformed login packet.";
				aString958 = "Please try again.";
				return;
			}
			if(k == 23) {
				aString957 = "No reply from loginserver.";
				aString958 = "Please try again.";
				return;
			}
			if(k == 24) {
				aString957 = "Error loading your profile.";
				aString958 = "Please contact customer support.";
				return;
			}
			if(k == 25) {
				aString957 = "Unexpected loginserver response.";
				aString958 = "Please try using a different world.";
				return;
			}
			if(k == 26) {
				aString957 = "This computers address has been blocked";
				aString958 = "as it was used to break our rules";
				return;
			}
			if(k == -1) {
				if(i1 == 0) {
					if(anInt850 < 2) {
						try {
							Thread.sleep(2000L);
						} catch(Exception _ex) {
						}
						anInt850++;
						method79(username, password, flag);
						return;
					} else {
						aString957 = "No response from loginserver";
						aString958 = "Please wait 1 minute and try again.";
						return;
					}
				} else {
					aString957 = "No response from server";
					aString958 = "Please try using a different world.";
					return;
				}
			} else {
				System.out.println("response:" + k);
				aString957 = "Unexpected server response";
				aString958 = "Please try using a different world.";
				return;
			}
		} catch(IOException _ex) {
			aString957 = "";
		}
		aString958 = "Error connecting to server.";
	}

	public boolean method80(int i, int j, int k, int l) {
		int i1 = l >> 14 & 0x7fff;
		int j1 = aClass22_1164.method271(anInt1091, k, i, l);
		if(j1 == -1)
			return false;
		int k1 = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
		if(k1 == 10 || k1 == 11 || k1 == 22) {
			Class47 class47 = Class47.method423(i1);
			int i2;
			int j2;
			if(l1 == 0 || l1 == 2) {
				i2 = class47.anInt801;
				j2 = class47.anInt775;
			} else {
				i2 = class47.anInt775;
				j2 = class47.anInt801;
			}
			int k2 = class47.anInt764;
			if(l1 != 0)
				k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
			method35(true, false, i, ((Mob) (sessionPlayer)).anIntArray1587[0], i2, j2, 2, 0, k, k2, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
		} else {
			method35(true, false, i, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, k1 + 1, k, 0, l1, ((Mob) (sessionPlayer)).anIntArray1586[0]);
		}
		anInt1020 = super.clickX;
		anInt1021 = super.clickY;
		anInt1023 = 2;
		anInt1022 = 0;
		packetSize += j;
		return true;
	}

	public void method81(byte byte0) {
		char c = '\u0100';
		for(int i = 10; i < 117; i++) {
			int j = (int) (Math.random() * 100D);
			if(j < 50)
				anIntArray1084[i + (c - 2 << 7)] = 255;
		}

		for(int k = 0; k < 100; k++) {
			int l = (int) (Math.random() * 124D) + 2;
			int j1 = (int) (Math.random() * 128D) + 128;
			int j2 = l + (j1 << 7);
			anIntArray1084[j2] = 192;
		}

		for(int i1 = 1; i1 < c - 1; i1++) {
			for(int k1 = 1; k1 < 127; k1++) {
				int k2 = k1 + (i1 << 7);
				anIntArray1085[k2] = (anIntArray1084[k2 - 1] + anIntArray1084[k2 + 1] + anIntArray1084[k2 - 128] + anIntArray1084[k2 + 128]) / 4;
			}

		}

		anInt1238 += 128;
		if(anInt1238 > anIntArray1176.length) {
			anInt1238 -= anIntArray1176.length;
			int l1 = (int) (Math.random() * 12D);
			method83(aClass50_Sub1_Sub1_Sub3Array1117[l1], 0);
		}
		for(int i2 = 1; i2 < c - 1; i2++) {
			for(int l2 = 1; l2 < 127; l2++) {
				int k3 = l2 + (i2 << 7);
				int i4 = anIntArray1085[k3 + 128] - anIntArray1176[k3 + anInt1238 & anIntArray1176.length - 1] / 5;
				if(i4 < 0)
					i4 = 0;
				anIntArray1084[k3] = i4;
			}

		}

		if(byte0 == 1) {
			byte0 = 0;
		} else {
			for(int i3 = 1; i3 > 0; i3++)
				;
		}
		for(int j3 = 0; j3 < c - 1; j3++)
			anIntArray1166[j3] = anIntArray1166[j3 + 1];

		anIntArray1166[c - 1] = (int) (Math.sin((double) currentTime / 14D) * 16D + Math.sin((double) currentTime / 15D) * 14D + Math.sin((double) currentTime / 16D) * 12D);
		if(anInt1047 > 0)
			anInt1047 -= 4;
		if(anInt1048 > 0)
			anInt1048 -= 4;
		if(anInt1047 == 0 && anInt1048 == 0) {
			int l3 = (int) (Math.random() * 2000D);
			if(l3 == 0)
				anInt1047 = 1024;
			if(l3 == 1)
				anInt1048 = 1024;
		}
	}

	public void buildAtNpcMenu(NPC npc, int i, int j, int k) {
		if(menuActionIndex >= 400)
			return;

		if(!npc.useNpcDef) {
			if(!npc.clickable)
				return;

			String name = npc.name;
			if(npc.combatLevel > 0) {
				name = name + getCombatRiskColour(npc.combatLevel, sessionPlayer.combatLevel) + " (level-" + npc.combatLevel + ")";
			}

			if(itemSelected == 1) {
				menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @yel@" + name;
				menuActionId[menuActionIndex] = 347;
				menuActionCmd1[menuActionIndex] = k;
				menuActionCmd2[menuActionIndex] = j;
				menuActionCmd3[menuActionIndex] = i;
				menuActionIndex++;
				return;
			}
			if(spellSelected == 1) {
				if((spellUsableOn & 2) == 2) {
					menuActionName[menuActionIndex] = spellTooltip + " @yel@" + name;
					menuActionId[menuActionIndex] = 67;
					menuActionCmd1[menuActionIndex] = k;
					menuActionCmd2[menuActionIndex] = j;
					menuActionCmd3[menuActionIndex] = i;
					menuActionIndex++;
					return;
				}
			} else {
				if(npc.actions != null) {
					for(int l = 4; l >= 0; l--)
						if(npc.actions[l] != null && !npc.actions[l].equalsIgnoreCase("attack")) {
							menuActionName[menuActionIndex] = npc.actions[l] + " @yel@" + name;
							if(l == 0)
								menuActionId[menuActionIndex] = 318;
							if(l == 1)
								menuActionId[menuActionIndex] = 921;
							if(l == 2)
								menuActionId[menuActionIndex] = 118;
							if(l == 3)
								menuActionId[menuActionIndex] = 553;
							if(l == 4)
								menuActionId[menuActionIndex] = 432;
							menuActionCmd1[menuActionIndex] = k;
							menuActionCmd2[menuActionIndex] = j;
							menuActionCmd3[menuActionIndex] = i;
							menuActionIndex++;
						}

				}
				if(npc.actions != null) {
					for(int i1 = 4; i1 >= 0; i1--)
						if(npc.actions[i1] != null && npc.actions[i1].equalsIgnoreCase("attack")) {
							char c = '\0';
							if(npc.combatLevel > sessionPlayer.combatLevel)
								c = '\u07D0';
							menuActionName[menuActionIndex] = npc.actions[i1] + " @yel@" + name;
							if(i1 == 0)
								menuActionId[menuActionIndex] = 318 + c;
							if(i1 == 1)
								menuActionId[menuActionIndex] = 921 + c;
							if(i1 == 2)
								menuActionId[menuActionIndex] = 118 + c;
							if(i1 == 3)
								menuActionId[menuActionIndex] = 553 + c;
							if(i1 == 4)
								menuActionId[menuActionIndex] = 432 + c;
							menuActionCmd1[menuActionIndex] = k;
							menuActionCmd2[menuActionIndex] = j;
							menuActionCmd3[menuActionIndex] = i;
							menuActionIndex++;
						}

				}
				menuActionName[menuActionIndex] = "Examine @yel@" + name;
				menuActionId[menuActionIndex] = 1668;
				menuActionCmd1[menuActionIndex] = k;
				menuActionCmd2[menuActionIndex] = j;
				menuActionCmd3[menuActionIndex] = i;
				menuActionIndex++;
			}
		} else {
			NPCDefinition npcDef = npc.npcDef;

			if(npcDef.childIds != null)
				npcDef = npcDef.method363();

			if(npcDef == null)
				return;

			if(!npcDef.clickable)
				return;

			String s = npcDef.name;
			if(npcDef.combatLevel != 0)
				s = s + getCombatRiskColour(npcDef.combatLevel, sessionPlayer.combatLevel) + " (level-" + npcDef.combatLevel + ")";

			if(itemSelected == 1) {
				menuActionName[menuActionIndex] = "Use " + selectedItemName + " with @yel@" + s;
				menuActionId[menuActionIndex] = 347;
				menuActionCmd1[menuActionIndex] = k;
				menuActionCmd2[menuActionIndex] = j;
				menuActionCmd3[menuActionIndex] = i;
				menuActionIndex++;
				return;
			}
			if(spellSelected == 1) {
				if((spellUsableOn & 2) == 2) {
					menuActionName[menuActionIndex] = spellTooltip + " @yel@" + s;
					menuActionId[menuActionIndex] = 67;
					menuActionCmd1[menuActionIndex] = k;
					menuActionCmd2[menuActionIndex] = j;
					menuActionCmd3[menuActionIndex] = i;
					menuActionIndex++;
					return;
				}
			} else {
				if(npcDef.actions != null) {
					for(int l = 4; l >= 0; l--)
						if(npcDef.actions[l] != null && !npcDef.actions[l].equalsIgnoreCase("attack")) {
							menuActionName[menuActionIndex] = npcDef.actions[l] + " @yel@" + s;
							if(l == 0)
								menuActionId[menuActionIndex] = 318;
							if(l == 1)
								menuActionId[menuActionIndex] = 921;
							if(l == 2)
								menuActionId[menuActionIndex] = 118;
							if(l == 3)
								menuActionId[menuActionIndex] = 553;
							if(l == 4)
								menuActionId[menuActionIndex] = 432;
							menuActionCmd1[menuActionIndex] = k;
							menuActionCmd2[menuActionIndex] = j;
							menuActionCmd3[menuActionIndex] = i;
							menuActionIndex++;
						}

				}
				if(npcDef.actions != null) {
					for(int i1 = 4; i1 >= 0; i1--)
						if(npcDef.actions[i1] != null && npcDef.actions[i1].equalsIgnoreCase("attack")) {
							char c = '\0';
							if(npcDef.combatLevel > sessionPlayer.combatLevel)
								c = '\u07D0';
							menuActionName[menuActionIndex] = npcDef.actions[i1] + " @yel@" + s;
							if(i1 == 0)
								menuActionId[menuActionIndex] = 318 + c;
							if(i1 == 1)
								menuActionId[menuActionIndex] = 921 + c;
							if(i1 == 2)
								menuActionId[menuActionIndex] = 118 + c;
							if(i1 == 3)
								menuActionId[menuActionIndex] = 553 + c;
							if(i1 == 4)
								menuActionId[menuActionIndex] = 432 + c;
							menuActionCmd1[menuActionIndex] = k;
							menuActionCmd2[menuActionIndex] = j;
							menuActionCmd3[menuActionIndex] = i;
							menuActionIndex++;
						}

				}
				menuActionName[menuActionIndex] = "Examine @yel@" + s;
				menuActionId[menuActionIndex] = 1668;
				menuActionCmd1[menuActionIndex] = k;
				menuActionCmd2[menuActionIndex] = j;
				menuActionCmd3[menuActionIndex] = i;
				menuActionIndex++;
			}
		}
	}

	public void method83(IndexedImage class50_sub1_sub1_sub3, int i) {
		packetSize += i;
		int j = 256;
		for(int k = 0; k < anIntArray1176.length; k++)
			anIntArray1176[k] = 0;

		for(int l = 0; l < 5000; l++) {
			int i1 = (int) (Math.random() * 128D * (double) j);
			anIntArray1176[i1] = (int) (Math.random() * 256D);
		}

		for(int j1 = 0; j1 < 20; j1++) {
			for(int k1 = 1; k1 < j - 1; k1++) {
				for(int i2 = 1; i2 < 127; i2++) {
					int k2 = i2 + (k1 << 7);
					anIntArray1177[k2] = (anIntArray1176[k2 - 1] + anIntArray1176[k2 + 1] + anIntArray1176[k2 - 128] + anIntArray1176[k2 + 128]) / 4;
				}

			}

			int ai[] = anIntArray1176;
			anIntArray1176 = anIntArray1177;
			anIntArray1177 = ai;
		}

		if(class50_sub1_sub1_sub3 != null) {
			int l1 = 0;
			for(int j2 = 0; j2 < class50_sub1_sub1_sub3.imgHeight; j2++) {
				for(int l2 = 0; l2 < class50_sub1_sub1_sub3.imgWidth; l2++)
					if(class50_sub1_sub1_sub3.imgPixels[l1++] != 0) {
						int i3 = l2 + 16 + class50_sub1_sub1_sub3.xDrawOffset;
						int j3 = j2 + 16 + class50_sub1_sub1_sub3.yDrawOffset;
						int k3 = i3 + (j3 << 7);
						anIntArray1176[k3] = 0;
					}

			}

		}
	}

	public void method84(int i) {
		aClass18_1159.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1000;
		chatback.drawImage(0, 0);
		if(aBoolean866) {
			boldFont.method470(239, 452, 40, 0, aString937);
			boldFont.method470(239, 452, 60, 128, aString1026 + "*");
		} else if(anInt1244 == 1) {
			boldFont.method470(239, 452, 40, 0, "Enter amount:");
			boldFont.method470(239, 452, 60, 128, aString949 + "*");
		} else if(anInt1244 == 2) {
			boldFont.method470(239, 452, 40, 0, "Enter name:");
			boldFont.method470(239, 452, 60, 128, aString949 + "*");
		} else if(anInt1244 == 3) {
			if(aString949 != aString861) {
				method14(aString949, 2);
				aString861 = aString949;
			}
			GameFont class50_sub1_sub1_sub2 = normalFont;
			DrawingArea.method446(0, 0, 77, 463, true);
			for(int j = 0; j < anInt862; j++) {
				int l = (18 + j * 14) - anInt865;
				if(l > 0 && l < 110)
					class50_sub1_sub1_sub2.method470(239, 452, l, 0, aStringArray863[j]);
			}

			DrawingArea.method445((byte) 82);
			if(anInt862 > 5)
				method56(true, anInt865, 463, 77, anInt862 * 14 + 7, 0);
			if(aString949.length() == 0)
				boldFont.method470(239, 452, 40, 255, "Enter object name");
			else if(anInt862 == 0)
				boldFont.method470(239, 452, 40, 0, "No matching objects found, please shorten search");
			class50_sub1_sub1_sub2.method470(239, 452, 90, 0, aString949 + "*");
			DrawingArea.method452(0, 0, 77, 479, true);
		} else if(aString1058 != null) {
			boldFont.method470(239, 452, 40, 0, aString1058);
			boldFont.method470(239, 452, 60, 128, "Click to continue");
		} else if(anInt988 != -1)
			drawInterface(0, 0, 0, GameInterface.getInterface(anInt988));
		else if(anInt1191 != -1) {
			drawInterface(0, 0, 0, GameInterface.getInterface(anInt1191));
		} else {
			GameFont class50_sub1_sub1_sub2_1 = normalFont;
			int k = 0;
			DrawingArea.method446(0, 0, 77, 463, true);
			for(int i1 = 0; i1 < 100; i1++)
				if(aStringArray1298[i1] != null) {
					int j1 = anIntArray1296[i1];
					int k1 = (70 - k * 14) + anInt851;
					String s1 = aStringArray1297[i1];
					byte byte0 = 0;
					if(s1 != null && s1.startsWith("@cr1@")) {
						s1 = s1.substring(5);
						byte0 = 1;
					}
					if(s1 != null && s1.startsWith("@cr2@")) {
						s1 = s1.substring(5);
						byte0 = 2;
					}
					if(j1 == 0) {
						if(k1 > 0 && k1 < 110)
							class50_sub1_sub1_sub2_1.method474(aStringArray1298[i1], 4, k1, 0);
						k++;
					}
					if((j1 == 1 || j1 == 2) && (j1 == 1 || anInt1006 == 0 || anInt1006 == 1 && method148(13292, s1))) {
						if(k1 > 0 && k1 < 110) {
							int l1 = 4;
							if(byte0 == 1) {
								aClass50_Sub1_Sub1_Sub3Array1142[0].drawImage(l1, k1 - 12);
								l1 += 14;
							}
							if(byte0 == 2) {
								aClass50_Sub1_Sub1_Sub3Array1142[1].drawImage(l1, k1 - 12);
								l1 += 14;
							}
							class50_sub1_sub1_sub2_1.method474(s1 + ":", l1, k1, 0);
							l1 += class50_sub1_sub1_sub2_1.getFormattedStringWidth(s1) + 8;
							class50_sub1_sub1_sub2_1.method474(aStringArray1298[i1], l1, k1, 255);
						}
						k++;
					}
					if((j1 == 3 || j1 == 7) && anInt1223 == 0 && (j1 == 7 || anInt887 == 0 || anInt887 == 1 && method148(13292, s1))) {
						if(k1 > 0 && k1 < 110) {
							int i2 = 4;
							class50_sub1_sub1_sub2_1.method474("From", i2, k1, 0);
							i2 += class50_sub1_sub1_sub2_1.getFormattedStringWidth("From ");
							if(byte0 == 1) {
								aClass50_Sub1_Sub1_Sub3Array1142[0].drawImage(i2, k1 - 12);
								i2 += 14;
							}
							if(byte0 == 2) {
								aClass50_Sub1_Sub1_Sub3Array1142[1].drawImage(i2, k1 - 12);
								i2 += 14;
							}
							class50_sub1_sub1_sub2_1.method474(s1 + ":", i2, k1, 0);
							i2 += class50_sub1_sub1_sub2_1.getFormattedStringWidth(s1) + 8;
							class50_sub1_sub1_sub2_1.method474(aStringArray1298[i1], i2, k1, 0x800000);
						}
						k++;
					}
					if(j1 == 4 && (anInt1227 == 0 || anInt1227 == 1 && method148(13292, s1))) {
						if(k1 > 0 && k1 < 110)
							class50_sub1_sub1_sub2_1.method474(s1 + " " + aStringArray1298[i1], 4, k1, 0x800080);
						k++;
					}
					if(j1 == 5 && anInt1223 == 0 && anInt887 < 2) {
						if(k1 > 0 && k1 < 110)
							class50_sub1_sub1_sub2_1.method474(aStringArray1298[i1], 4, k1, 0x800000);
						k++;
					}
					if(j1 == 6 && anInt1223 == 0 && anInt887 < 2) {
						if(k1 > 0 && k1 < 110) {
							class50_sub1_sub1_sub2_1.method474("To " + s1 + ":", 4, k1, 0);
							class50_sub1_sub1_sub2_1.method474(aStringArray1298[i1], 12 + class50_sub1_sub1_sub2_1.getFormattedStringWidth("To " + s1), k1, 0x800000);
						}
						k++;
					}
					if(j1 == 8 && (anInt1227 == 0 || anInt1227 == 1 && method148(13292, s1))) {
						if(k1 > 0 && k1 < 110)
							class50_sub1_sub1_sub2_1.method474(s1 + " " + aStringArray1298[i1], 4, k1, 0x7e3200);
						k++;
					}
				}

			DrawingArea.method445((byte) 82);
			anInt1107 = k * 14 + 7;
			if(anInt1107 < 78)
				anInt1107 = 78;
			method56(true, anInt1107 - anInt851 - 77, 463, 77, anInt1107, 0);
			String s;
			if(sessionPlayer != null && sessionPlayer.username != null)
				s = sessionPlayer.username;
			else
				s = NameUtils.formatName(aString1092);
			class50_sub1_sub1_sub2_1.method474(s + ":", 4, 90, 0);
			class50_sub1_sub1_sub2_1.method474(aString1104 + "*", 6 + class50_sub1_sub1_sub2_1.getFormattedStringWidth(s + ": "), 90, 255);
			DrawingArea.method452(0, 0, 77, 479, true);
		}
		if(menuOpen && menuScreenArea == 2)
			drawMenu();
		aClass18_1159.drawGraphics(17, 357, super.graphics);
		gameScreenDrawingArea.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1002;
		if(i != 0)
			aClass6ArrayArrayArray1323 = null;
	}

	public void method85(int i) {
		for(int j = -1; j < anInt971; j++) {
			int k;
			if(j == -1)
				k = anInt969;
			else
				k = sessionPlayerList[j];
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[k];
			if(class50_sub1_sub4_sub3_sub2 != null && ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1582 > 0) {
				class50_sub1_sub4_sub3_sub2.anInt1582--;
				if(((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1582 == 0)
					class50_sub1_sub4_sub3_sub2.aString1580 = null;
			}
		}

		packetSize += i;
		for(int l = 0; l < sessionNpcCount; l++) {
			int i1 = sessionNpcList[l];
			NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[i1];
			if(class50_sub1_sub4_sub3_sub1 != null && ((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1582 > 0) {
				class50_sub1_sub4_sub3_sub1.anInt1582--;
				if(((Mob) (class50_sub1_sub4_sub3_sub1)).anInt1582 == 0)
					class50_sub1_sub4_sub3_sub1.aString1580 = null;
			}
		}

	}

	public void method86(boolean flag) {
		/*
		 * int i = 5; anIntArray837[8] = 0; if(flag) { for(int j = 1; j > 0;
		 * j++); } int k = 0;req while(anIntArray837[8] == 0) { String s =
		 * "Unknown problem"; method13(20, true, "Connecting to web server");
		 * try { DataInputStream datainputstream = method31("crc" +
		 * (int)(Math.random() * 99999999D) + "-" + RS_REVISION); ByteStream
		 * class50_sub1_sub2 = new ByteStream(true, new byte[40]);
		 * datainputstream.readFully(class50_sub1_sub2.aByteArray1453, 0, 40);
		 * datainputstream.close(); for(int i1 = 0; i1 < 9; i1++)
		 * anIntArray837[i1] = class50_sub1_sub2.method526();
		 * 
		 * int j1 = class50_sub1_sub2.method526(); int k1 = 1234; for(int l1 =
		 * 0; l1 < 9; l1++) k1 = (k1 << 1) + anIntArray837[l1];
		 * 
		 * if(j1 != k1) { s = "checksum problem"; anIntArray837[8] = 0; } }
		 * catch(EOFException _ex) { s = "EOF problem"; anIntArray837[8] = 0; }
		 * catch(IOException _ex) { s = "connection problem"; anIntArray837[8] =
		 * 0; } catch(Exception _ex) { s = "logic problem"; anIntArray837[8] =
		 * 0; if(!signlink.reporterror) return; } if(anIntArray837[8] == 0) {
		 * k++; for(int l = i; l > 0; l--) { if(k >= 10) { method13(10, true,
		 * "Game updated - please reload page"); l = 10; } else { method13(10,
		 * true, s + " - Will retry in " + l + " secs."); } try {
		 * Thread.sleep(1000L); } catch(Exception _ex) { } }
		 * 
		 * i *= 2; if(i > 60) i = 60; aBoolean900 = !aBoolean900; } }
		 */
	}
	
	private int getLevelForExp(int exp) {
		int points = 0;
		int output = 0;
		
		if(exp > 13034430) {
			return 99;
		}
		
		for(int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor(points / 4);
			
			if(output >= exp) {
				return lvl;
			}
		}
		
		return 0;
	}
	
	private int getOrbFill(int maxVal, int val) {
		double fill = Double.valueOf(maxVal) / Double.valueOf(MAP_ORB_SIZE);
		
		fill = Double.valueOf(val) / fill;
		
		fill = Math.abs(fill - MAP_ORB_SIZE);
		
		if(fill < 0) {
			fill = 0;
		}
		if(fill > MAP_ORB_SIZE) {
			fill = MAP_ORB_SIZE;
		}
		
		return (int) Math.round(fill);
	}
	private int getOrbTextColour(int maxVal, int val) {
		int fill = getOrbFill(maxVal, val);
		int colour = orbColours[fill];
		return colour;
	}
	
	private void drawOrb(int x, int y, int orbIndex, int max, int cur) {
		dataOrbBg[0].drawImage(x, y);
		
		int fill = getOrbFill(max, cur);
		dataOrbBg[1].setHeight(fill);

		dataOrbFills[orbIndex].drawImage(x + 28, y + 5);
		dataOrbBg[1].drawImage(x + 28, y + 5);
		
		int offX = 33;
		int offY = 8;
		
		if(orbIndex == 0) {
			offX = 34;
			offY = 12;
		} else if(orbIndex == 1) {
			offX = 31;
		}
		dataOrbIcons[orbIndex].drawImage(x + offX, y + offY);
		
		drawOrbText(x, y, max, cur);
	}
	
	private void drawOrbText(int x, int y, int maxVal, int val) {
		smallFont.drawCenteredText(true, getOrbTextColour(maxVal, val), x + 16, y + 27, String.valueOf(val));
	}
	
	public void drawMinimap() {
		mapDrawingArea.initDrawingArea();
		
		drawOrb(0, 40, 0, getLevelForExp(playerExps[3]), playerLevels[3]);
		drawOrb(2, 80, 1, getLevelForExp(playerExps[5]), playerLevels[5]);
		drawOrb(25, 120, (playerRunning ? 3 : 2), 100, playerRunEnergy);
		
		if(minimapLock == 2) {
			byte abyte0[] = mapback.imgPixels;
			int ai[] = DrawingArea.pixels;
			int l2 = abyte0.length;
			for(int j5 = 0; j5 < l2; j5++)
				if(abyte0[j5] == 0)
					ai[j5] = 0;

			compassImage.rotate(25, 25, 33 + (MAP_OFFSET_X * 2), 33, cameraX, compassShape2, 256, compassShape1, 0, 0);
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
			return;
		}
		int angle = cameraX + minimapRotation & 0x7ff;
		int centerX = 48 + ((Mob) (sessionPlayer)).anInt1610 / 32;
		int centerY = 464 - ((Mob) (sessionPlayer)).anInt1611 / 32;
		minimapImage.rotate(centerX, centerY, 146, 151, angle, mapShape2, 256 + minimapZoom, mapShape1, 25 + MAP_OFFSET_X, 5);
		compassImage.rotate(25, 25, 33 + (MAP_OFFSET_X * 2), 33, cameraX, compassShape2, 256, compassShape1, 0, 0);
		
		for(int k5 = 0; k5 < anInt1076; k5++) {
			int l = (markPosX[k5] * 4 + 2) - ((Mob) (sessionPlayer)).anInt1610 / 32;
			int j3 = (markPosY[k5] * 4 + 2) - ((Mob) (sessionPlayer)).anInt1611 / 32;
			markMinimap(mapMarkImage[k5], l, j3);
		}

		for(int l5 = 0; l5 < 104; l5++) {
			for(int i6 = 0; i6 < 104; i6++) {
				NodeList class6 = aClass6ArrayArrayArray1323[anInt1091][l5][i6];
				if(class6 != null) {
					int i1 = (l5 * 4 + 2) - ((Mob) (sessionPlayer)).anInt1610 / 32;
					int k3 = (i6 * 4 + 2) - ((Mob) (sessionPlayer)).anInt1611 / 32;
					markMinimap(mapDotItem, i1, k3);
				}
			}

		}

		for(int j6 = 0; j6 < sessionNpcCount; j6++) {
			NPC npc = sessionNpcs[sessionNpcList[j6]];
			if(npc != null && npc.isVisible()) {
				NPCDefinition npcDef = npc.npcDef;
				if(npcDef.childIds != null)
					npcDef = npcDef.method363();
				if(npcDef != null && npcDef.hasMapDot && npcDef.clickable) {
					int j1 = ((Mob) (npc)).anInt1610 / 32 - ((Mob) (sessionPlayer)).anInt1610 / 32;
					int l3 = ((Mob) (npc)).anInt1611 / 32 - ((Mob) (sessionPlayer)).anInt1611 / 32;
					markMinimap(mapDotNpc, j1, l3);
				}
			}
		}

		for(int k6 = 0; k6 < anInt971; k6++) {
			Player player = sessionPlayers[sessionPlayerList[k6]];
			if(player != null && player.isVisible()) {
				int k1 = ((Mob) (player)).anInt1610 / 32 - ((Mob) (sessionPlayer)).anInt1610 / 32;
				int i4 = ((Mob) (player)).anInt1611 / 32 - ((Mob) (sessionPlayer)).anInt1611 / 32;
				boolean flag = false;
				long l6 = NameUtils.nameToLong(player.username);
				for(int i7 = 0; i7 < anInt859; i7++) {
					if(l6 != aLongArray1130[i7] || anIntArray1267[i7] == 0)
						continue;
					flag = true;
					break;
				}

				boolean flag1 = false;
				if(sessionPlayer.teamId != 0 && player.teamId != 0 && sessionPlayer.teamId == player.teamId)
					flag1 = true;
				if(flag)
					markMinimap(mapDotFriend, k1, i4);
				else if(flag1)
					markMinimap(mapDotTeam, k1, i4);
				else
					markMinimap(mapDotPlayer, k1, i4);
			}
		}

		if(anInt1197 != 0 && currentTime % 20 < 10) {
			if(anInt1197 == 1 && anInt1226 >= 0 && anInt1226 < sessionNpcs.length) {
				NPC class50_sub1_sub4_sub3_sub1_1 = sessionNpcs[anInt1226];
				if(class50_sub1_sub4_sub3_sub1_1 != null) {
					int l1 = ((Mob) (class50_sub1_sub4_sub3_sub1_1)).anInt1610 / 32 - ((Mob) (sessionPlayer)).anInt1610 / 32;
					int j4 = ((Mob) (class50_sub1_sub4_sub3_sub1_1)).anInt1611 / 32 - ((Mob) (sessionPlayer)).anInt1611 / 32;
					drawTargetIndicator(j4, aClass50_Sub1_Sub1_Sub1_1037, -687, l1);
				}
			}
			if(anInt1197 == 2) {
				int i2 = ((anInt844 - anInt1040) * 4 + 2) - ((Mob) (sessionPlayer)).anInt1610 / 32;
				int k4 = ((anInt845 - anInt1041) * 4 + 2) - ((Mob) (sessionPlayer)).anInt1611 / 32;
				drawTargetIndicator(k4, aClass50_Sub1_Sub1_Sub1_1037, -687, i2);
			}
			if(anInt1197 == 10 && anInt1151 >= 0 && anInt1151 < sessionPlayers.length) {
				Player class50_sub1_sub4_sub3_sub2_1 = sessionPlayers[anInt1151];
				if(class50_sub1_sub4_sub3_sub2_1 != null) {
					int j2 = ((Mob) (class50_sub1_sub4_sub3_sub2_1)).anInt1610 / 32 - ((Mob) (sessionPlayer)).anInt1610 / 32;
					int l4 = ((Mob) (class50_sub1_sub4_sub3_sub2_1)).anInt1611 / 32 - ((Mob) (sessionPlayer)).anInt1611 / 32;
					drawTargetIndicator(l4, aClass50_Sub1_Sub1_Sub1_1037, -687, j2);
				}
			}
		}
		if(anInt1120 != 0) {
			int k2 = (anInt1120 * 4 + 2) - ((Mob) (sessionPlayer)).anInt1610 / 32;
			int i5 = (anInt1121 * 4 + 2) - ((Mob) (sessionPlayer)).anInt1611 / 32;
			markMinimap(mapFlag, k2, i5);
		}
		DrawingArea.fillRect(3, 78, 0xffffff, (byte) -24, 3, 97 + MAP_OFFSET_X);
		gameScreenDrawingArea.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1002;
	}

	public URL getCodeBase() {
		if(Signlink.mainapp != null)
			return Signlink.mainapp.getCodeBase();
		try {
			if(super.gameFrame != null)
				return new URL("http://127.0.0.1:" + (80 + portOffset));
		} catch(Exception _ex) {
		}
		return super.getCodeBase();
	}

	public boolean method88(int i, int j, byte byte0) {
		boolean flag = false;
		GameInterface class13 = GameInterface.getInterface(j);
		for(int k = 0; k < class13.children.length; k++) {
			if(class13.children[k] == -1)
				break;
			GameInterface class13_1 = GameInterface.getInterface(class13.children[k]);
			if(class13_1.type == 0)
				flag |= method88(i, class13_1.id, (byte) 5);
			if(class13_1.type == 6 && (class13_1.anInt286 != -1 || class13_1.anInt287 != -1)) {
				boolean flag1 = method95(class13_1, -693);
				int i1;
				if(flag1)
					i1 = class13_1.anInt287;
				else
					i1 = class13_1.anInt286;
				if(i1 != -1) {
					Class14 class14 = Class14.aClass14Array293[i1];
					for(class13_1.anInt227 += i; class13_1.anInt227 > class14.method205(0, class13_1.anInt235);) {
						class13_1.anInt227 -= class14.method205(0, class13_1.anInt235);
						class13_1.anInt235++;
						if(class13_1.anInt235 >= class14.anInt294) {
							class13_1.anInt235 -= class14.anInt298;
							if(class13_1.anInt235 < 0 || class13_1.anInt235 >= class14.anInt294)
								class13_1.anInt235 = 0;
						}
						flag = true;
					}

				}
			}
			if(class13_1.type == 6 && class13_1.anInt218 != 0) {
				int l = class13_1.anInt218 >> 16;
				int j1 = (class13_1.anInt218 << 16) >> 16;
				l *= i;
				j1 *= i;
				class13_1.anInt252 = class13_1.anInt252 + l & 0x7ff;
				class13_1.anInt253 = class13_1.anInt253 + j1 & 0x7ff;
				flag = true;
			}
		}

		if(byte0 == 5)
			byte0 = 0;
		else
			anInt1236 = -424;
		return flag;
	}

	public String method89(int i, int j) {
		if(j < 8 || j > 8)
			throw new NullPointerException();
		if(i < 0x3b9ac9ff)
			return String.valueOf(i);
		else
			return "*";
	}

	public void method90(int i, long l) {
		try {
			if(i != -916)
				packetOpcode = byteStream4.getUnsignedByte();
			if(l == 0L)
				return;
			if(anInt855 >= 100) {
				sendChatboxMessage("", "Your ignore list is full. Max of 100 hit.", 0);
				return;
			}
			String s = NameUtils.formatName(NameUtils.longToName(l));
			for(int j = 0; j < anInt855; j++)
				if(aLongArray1073[j] == l) {
					sendChatboxMessage("", s + " is already on your ignore list.", 0);
					return;
				}

			for(int k = 0; k < anInt859; k++)
				if(aLongArray1130[k] == l) {
					sendChatboxMessage("", "Please remove " + s + " from your friend list first.", 0);
					return;
				}

			aLongArray1073[anInt855++] = l;
			tabRepaintRequested = true;
			byteStream2.createFrame(217);
			byteStream2.putLong(l);
			return;
		} catch(RuntimeException runtimeexception) {
			Signlink.reporterror("27939, " + i + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void method7() {
		if(gameAlreadyLoaded || loadingError || invalidHost)
			return;
		currentTime++;
		if(!loggedIn)
			method149(-724);
		else
			method28((byte) 4);
		method77(false);
	}

	public void method91(int i) {
		if(anInt1113 != 0)
			return;
		menuActionName[0] = "Cancel";
		menuActionId[0] = 1016;
		menuActionIndex = 1;
		if(i >= 0)
			anInt1004 = aISAAC_899.getNextValue();
		if(anInt1053 != -1) {
			anInt915 = 0;
			anInt1315 = 0;
			method66(0, GameInterface.getInterface(anInt1053), 0, 0, 0, super.mouseX, 23658, super.mouseY);
			if(anInt915 != anInt1302)
				anInt1302 = anInt915;
			if(anInt1315 != anInt1129)
				anInt1129 = anInt1315;
			return;
		}
		method111(anInt1178);
		anInt915 = 0;
		anInt1315 = 0;
		if(super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338)
			if(anInt1169 != -1)
				method66(4, GameInterface.getInterface(anInt1169), 0, 0, 4, super.mouseX, 23658, super.mouseY);
			else
				method43((byte) 7);
		if(anInt915 != anInt1302)
			anInt1302 = anInt915;
		if(anInt1315 != anInt1129)
			anInt1129 = anInt1315;
		anInt915 = 0;
		anInt1315 = 0;
		if(super.mouseX > 553 && super.mouseY > 205 && super.mouseX < 743 && super.mouseY < 466)
			if(anInt1089 != -1)
				method66(205, GameInterface.getInterface(anInt1089), 1, 0, 553, super.mouseX, 23658, super.mouseY);
			else if(tabInterfaceIds[selectedTab] != -1)
				method66(205, GameInterface.getInterface(tabInterfaceIds[selectedTab]), 1, 0, 553, super.mouseX, 23658, super.mouseY);
		if(anInt915 != anInt1280) {
			tabRepaintRequested = true;
			anInt1280 = anInt915;
		}
		if(anInt1315 != anInt1044) {
			tabRepaintRequested = true;
			anInt1044 = anInt1315;
		}
		anInt915 = 0;
		anInt1315 = 0;
		if(super.mouseX > 17 && super.mouseY > 357 && super.mouseX < 496 && super.mouseY < 453)
			if(anInt988 != -1)
				method66(357, GameInterface.getInterface(anInt988), 2, 0, 17, super.mouseX, 23658, super.mouseY);
			else if(anInt1191 != -1)
				method66(357, GameInterface.getInterface(anInt1191), 3, 0, 17, super.mouseX, 23658, super.mouseY);
			else if(super.mouseY < 434 && super.mouseX < 426 && anInt1244 == 0)
				method113(466, super.mouseX - 17, super.mouseY - 357);
		if((anInt988 != -1 || anInt1191 != -1) && anInt915 != anInt1106) {
			aBoolean1240 = true;
			anInt1106 = anInt915;
		}
		if((anInt988 != -1 || anInt1191 != -1) && anInt1315 != anInt1284) {
			aBoolean1240 = true;
			anInt1284 = anInt1315;
		}
		for(boolean flag = false; !flag;) {
			flag = true;
			for(int j = 0; j < menuActionIndex - 1; j++)
				if(menuActionId[j] < 1000 && menuActionId[j + 1] > 1000) {
					String s = menuActionName[j];
					menuActionName[j] = menuActionName[j + 1];
					menuActionName[j + 1] = s;
					int k = menuActionId[j];
					menuActionId[j] = menuActionId[j + 1];
					menuActionId[j + 1] = k;
					k = menuActionCmd2[j];
					menuActionCmd2[j] = menuActionCmd2[j + 1];
					menuActionCmd2[j + 1] = k;
					k = menuActionCmd3[j];
					menuActionCmd3[j] = menuActionCmd3[j + 1];
					menuActionCmd3[j + 1] = k;
					k = menuActionCmd1[j];
					menuActionCmd1[j] = menuActionCmd1[j + 1];
					menuActionCmd1[j + 1] = k;
					flag = false;
				}

		}

	}

	public static String getCombatRiskColour(int i, int j) {
		int l = j - i;
		if(l < -9)
			return "@red@";
		if(l < -6)
			return "@or3@";
		if(l < -3)
			return "@or2@";
		if(l < 0)
			return "@or1@";
		if(l > 9)
			return "@gre@";
		if(l > 6)
			return "@gr3@";
		if(l > 3)
			return "@gr2@";
		if(l > 0)
			return "@gr1@";
		else
			return "@yel@";
	}

	public void method93(int i) {
		try {
			anInt1276 = -1;
			aClass6_1210.method162();
			aClass6_1282.method162();
			Rasterizer.method495((byte) 71);
			method49(383);
			aClass22_1164.method241((byte) 7);
			System.gc();
			for(int j = 0; j < 4; j++)
				aClass46Array1260[j].method411();

			for(int i1 = 0; i1 < 4; i1++) {
				for(int l1 = 0; l1 < 104; l1++) {
					for(int k2 = 0; k2 < 104; k2++)
						aByteArrayArrayArray1125[i1][l1][k2] = 0;

				}

			}

			Class8 class8 = new Class8(anIntArrayArrayArray891, 14290, aByteArrayArrayArray1125, 104, 104);
			int l2 = aByteArrayArray838.length;
			byteStream2.createFrame(40);
			if(!aBoolean1163) {
				for(int j3 = 0; j3 < l2; j3++) {
					int j4 = (anIntArray856[j3] >> 8) * 64 - anInt1040;
					int l5 = (anIntArray856[j3] & 0xff) * 64 - anInt1041;
					byte abyte0[] = aByteArrayArray838[j3];
					if(abyte0 != null)
						class8.method174(l5, false, (anInt890 - 6) * 8, j4, abyte0, (anInt889 - 6) * 8, aClass46Array1260);
				}

				for(int k4 = 0; k4 < l2; k4++) {
					int i6 = (anIntArray856[k4] >> 8) * 64 - anInt1040;
					int l7 = (anIntArray856[k4] & 0xff) * 64 - anInt1041;
					byte abyte2[] = aByteArrayArray838[k4];
					if(abyte2 == null && anInt890 < 800)
						class8.method180(i6, l7, 64, -810, 64);
				}

				byteStream2.createFrame(40);
				for(int j6 = 0; j6 < l2; j6++) {
					byte abyte1[] = aByteArrayArray1232[j6];
					if(abyte1 != null) {
						int l8 = (anIntArray856[j6] >> 8) * 64 - anInt1040;
						int k9 = (anIntArray856[j6] & 0xff) * 64 - anInt1041;
						class8.method179(k9, aClass46Array1260, l8, -571, aClass22_1164, abyte1);
					}
				}

			}
			if(aBoolean1163) {
				for(int k3 = 0; k3 < 4; k3++) {
					for(int l4 = 0; l4 < 13; l4++) {
						for(int k6 = 0; k6 < 13; k6++) {
							boolean flag = false;
							int i9 = anIntArrayArrayArray879[k3][l4][k6];
							if(i9 != -1) {
								int l9 = i9 >> 24 & 3;
								int j10 = i9 >> 1 & 3;
								int l10 = i9 >> 14 & 0x3ff;
								int j11 = i9 >> 3 & 0x7ff;
								int l11 = (l10 / 8 << 8) + j11 / 8;
								for(int j12 = 0; j12 < anIntArray856.length; j12++) {
									if(anIntArray856[j12] != l11 || aByteArrayArray838[j12] == null)
										continue;
									class8.method168(j10, (j11 & 7) * 8, false, aByteArrayArray838[j12], k3, l9, l4 * 8, aClass46Array1260, k6 * 8, (l10 & 7) * 8);
									flag = true;
									break;
								}

							}
							if(!flag)
								class8.method166(anInt1072, k3, k6 * 8, l4 * 8);
						}

					}

				}

				for(int i5 = 0; i5 < 13; i5++) {
					for(int l6 = 0; l6 < 13; l6++) {
						int i8 = anIntArrayArrayArray879[0][i5][l6];
						if(i8 == -1)
							class8.method180(i5 * 8, l6 * 8, 8, -810, 8);
					}

				}

				byteStream2.createFrame(40);
				for(int i7 = 0; i7 < 4; i7++) {
					for(int j8 = 0; j8 < 13; j8++) {
						for(int j9 = 0; j9 < 13; j9++) {
							int i10 = anIntArrayArrayArray879[i7][j8][j9];
							if(i10 != -1) {
								int k10 = i10 >> 24 & 3;
								int i11 = i10 >> 1 & 3;
								int k11 = i10 >> 14 & 0x3ff;
								int i12 = i10 >> 3 & 0x7ff;
								int k12 = (k11 / 8 << 8) + i12 / 8;
								for(int l12 = 0; l12 < anIntArray856.length; l12++) {
									if(anIntArray856[l12] != k12 || aByteArrayArray1232[l12] == null)
										continue;
									class8.method172(i7, aClass46Array1260, aClass22_1164, false, aByteArrayArray1232[l12], j9 * 8, i11, (k11 & 7) * 8, j8 * 8, (i12 & 7) * 8, k10);
									break;
								}

							}
						}

					}

				}

			}
			byteStream2.createFrame(40);
			class8.method167(aClass46Array1260, anInt1318, aClass22_1164);
			if(gameScreenDrawingArea != null) {
				gameScreenDrawingArea.initDrawingArea();
				Rasterizer.lineOffsets = anIntArray1002;
			}
			byteStream2.createFrame(40);
			int l3 = Class8.anInt150;
			if(l3 > anInt1091)
				l3 = anInt1091;
			if(l3 < anInt1091 - 1)
				l3 = anInt1091 - 1;
			if(aBoolean926)
				aClass22_1164.method242(Class8.anInt150, true);
			else
				aClass22_1164.method242(0, true);
			for(int j5 = 0; j5 < 104; j5++) {
				for(int j7 = 0; j7 < 104; j7++)
					method26(j5, j7);

			}

			method18((byte) 3);
		} catch(Exception exception) {
		}
		Class47.aClass33_779.method347();
		if(super.gameFrame != null) {
			byteStream2.createFrame(78);
			byteStream2.putInt(0x3f008edd);
		}
		if(aBoolean926 && Signlink.cache_dat != null) {
			int k = aClass32_Sub1_1291.method340(0, -31140);
			for(int j1 = 0; j1 < k; j1++) {
				int i2 = aClass32_Sub1_1291.method325(j1, -493);
				if((i2 & 0x79) == 0)
					Model.method576(j1, 1);
			}

		}
		System.gc();
		Rasterizer.method496((byte) 7, 20);
		aClass32_Sub1_1291.method336((byte) -125);
		int l = (anInt889 - 6) / 8 - 1;
		int k1 = (anInt889 + 6) / 8 + 1;
		int j2 = (anInt890 - 6) / 8 - 1;
		int i3 = (anInt890 + 6) / 8 + 1;
		i = 94 / i;
		if(aBoolean1067) {
			l = 49;
			k1 = 50;
			j2 = 49;
			i3 = 50;
		}
		for(int i4 = l; i4 <= k1; i4++) {
			for(int k5 = j2; k5 <= i3; k5++)
				if(i4 == l || i4 == k1 || k5 == j2 || k5 == i3) {
					int k7 = aClass32_Sub1_1291.method344(0, i4, k5, 0);
					if(k7 != -1)
						aClass32_Sub1_1291.method337(k7, 3, aByte936);
					int k8 = aClass32_Sub1_1291.method344(0, i4, k5, 1);
					if(k8 != -1)
						aClass32_Sub1_1291.method337(k8, 3, aByte936);
				}

		}

	}

	public void method94(int i, int j, int k, int l, int i1, int j1, byte byte0) {
		int k1 = 2048 - k & 0x7ff;
		int l1 = 2048 - i1 & 0x7ff;
		if(byte0 != -103)
			packetOpcode = -1;
		int i2 = 0;
		int j2 = 0;
		int k2 = l;
		if(k1 != 0) {
			int l2 = Model.anIntArray1710[k1];
			int j3 = Model.anIntArray1711[k1];
			int l3 = j2 * j3 - k2 * l2 >> 16;
			k2 = j2 * l2 + k2 * j3 >> 16;
			j2 = l3;
		}
		if(l1 != 0) {
			int i3 = Model.anIntArray1710[l1];
			int k3 = Model.anIntArray1711[l1];
			int i4 = k2 * i3 + i2 * k3 >> 16;
			k2 = k2 * k3 - i2 * i3 >> 16;
			i2 = i4;
		}
		anInt1216 = j - i2;
		anInt1217 = i - j2;
		anInt1218 = j1 - k2;
		anInt1219 = k;
		anInt1220 = i1;
	}

	public boolean method95(GameInterface class13, int i) {
		if(i >= 0)
			anInt1175 = 276;
		if(class13.anIntArray273 == null)
			return false;
		for(int j = 0; j < class13.anIntArray273.length; j++) {
			int k = method129(3, j, class13);
			int l = class13.anIntArray256[j];
			if(class13.anIntArray273[j] == 2) {
				if(k >= l)
					return false;
			} else if(class13.anIntArray273[j] == 3) {
				if(k <= l)
					return false;
			} else if(class13.anIntArray273[j] == 4) {
				if(k == l)
					return false;
			} else if(k != l)
				return false;
		}

		return true;
	}

	public void method96(int i, int j, ByteBuffer byteStream) {
		anInt1294 = 0;
		sessionNpcsAwaitingUpdate = 0;
		method41(i, aBoolean1274, byteStream);
		method114(i, -138, byteStream);
		j = 40 / j;
		method16(i, (byte) 6, byteStream);
		method40(808, byteStream, i);
		for(int k = 0; k < anInt1294; k++) {
			int l = anIntArray1295[k];
			if(((Mob) (sessionPlayers[l])).anInt1585 != currentTime)
				sessionPlayers[l] = null;
		}

		if(byteStream.position != i) {
			Signlink.reporterror("Error packet size mismatch in getplayer pos:" + byteStream.position + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for(int i1 = 0; i1 < anInt971; i1++)
			if(sessionPlayers[sessionPlayerList[i1]] == null) {
				Signlink.reporterror(aString1092 + " null entry in pl list - pos:" + i1 + " size:" + anInt971);
				throw new RuntimeException("eek");
			}

	}

	public void method97(int i, long l) {
		try {
			if(l == 0L)
				return;
			for(int j = 0; j < anInt855; j++) {
				if(aLongArray1073[j] != l)
					continue;
				anInt855--;
				tabRepaintRequested = true;
				for(int k = j; k < anInt855; k++)
					aLongArray1073[k] = aLongArray1073[k + 1];

				byteStream2.createFrame(160);
				byteStream2.putLong(l);
				break;
			}

			i = 42 / i;
			return;
		} catch(RuntimeException runtimeexception) {
			Signlink.reporterror("45745, " + i + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public String getParameter(String s) {
		if(Signlink.mainapp != null)
			return Signlink.mainapp.getParameter(s);
		else
			return super.getParameter(s);
	}

	public void method98(int i) {
		char c = '\u0100';
		if(anInt1047 > 0) {
			for(int j = 0; j < 256; j++)
				if(anInt1047 > 768)
					anIntArray1310[j] = method106(anIntArray1311[j], anIntArray1312[j], 1024 - anInt1047, 8);
				else if(anInt1047 > 256)
					anIntArray1310[j] = anIntArray1312[j];
				else
					anIntArray1310[j] = method106(anIntArray1312[j], anIntArray1311[j], 256 - anInt1047, 8);

		} else if(anInt1048 > 0) {
			for(int k = 0; k < 256; k++)
				if(anInt1048 > 768)
					anIntArray1310[k] = method106(anIntArray1311[k], anIntArray1313[k], 1024 - anInt1048, 8);
				else if(anInt1048 > 256)
					anIntArray1310[k] = anIntArray1313[k];
				else
					anIntArray1310[k] = method106(anIntArray1313[k], anIntArray1311[k], 256 - anInt1048, 8);

		} else {
			for(int l = 0; l < 256; l++)
				anIntArray1310[l] = anIntArray1311[l];

		}
		for(int i1 = 0; i1 < 33920; i1++)
			aClass18_1201.anIntArray392[i1] = aClass50_Sub1_Sub1_Sub1_1017.pixels[i1];

		int j1 = 0;
		int k1 = 1152;
		for(int l1 = 1; l1 < c - 1; l1++) {
			int i2 = (anIntArray1166[l1] * (c - l1)) / c;
			int k2 = 22 + i2;
			if(k2 < 0)
				k2 = 0;
			j1 += k2;
			for(int i3 = k2; i3 < 128; i3++) {
				int k3 = anIntArray1084[j1++];
				if(k3 != 0) {
					int i4 = k3;
					int k4 = 256 - k3;
					k3 = anIntArray1310[k3];
					int i5 = aClass18_1201.anIntArray392[k1];
					aClass18_1201.anIntArray392[k1++] = ((k3 & 0xff00ff) * i4 + (i5 & 0xff00ff) * k4 & 0xff00ff00) + ((k3 & 0xff00) * i4 + (i5 & 0xff00) * k4 & 0xff0000) >> 8;
				} else {
					k1++;
				}
			}

			k1 += k2;
		}

		aClass18_1201.drawGraphics(0, 0, super.graphics);
		i = 66 / i;
		for(int j2 = 0; j2 < 33920; j2++)
			aClass18_1202.anIntArray392[j2] = aClass50_Sub1_Sub1_Sub1_1018.pixels[j2];

		j1 = 0;
		k1 = 1176;
		for(int l2 = 1; l2 < c - 1; l2++) {
			int j3 = (anIntArray1166[l2] * (c - l2)) / c;
			int l3 = 103 - j3;
			k1 += j3;
			for(int j4 = 0; j4 < l3; j4++) {
				int l4 = anIntArray1084[j1++];
				if(l4 != 0) {
					int j5 = l4;
					int k5 = 256 - l4;
					l4 = anIntArray1310[l4];
					int l5 = aClass18_1202.anIntArray392[k1];
					aClass18_1202.anIntArray392[k1++] = ((l4 & 0xff00ff) * j5 + (l5 & 0xff00ff) * k5 & 0xff00ff00) + ((l4 & 0xff00) * j5 + (l5 & 0xff00) * k5 & 0xff0000) >> 8;
				} else {
					k1++;
				}
			}

			j1 += 128 - l3;
			k1 += 128 - l3 - j3;
		}

		aClass18_1202.drawGraphics(637, 0, super.graphics);
	}

	public void method99(boolean flag, byte byte0, int i) {
		if(byte0 != 8)
			byteStream2.putByte(49);
		Signlink.midivol = i;
		if(flag)
			Signlink.midi = "voladjust";
	}

	public void method100(int i) {
		for(int j = -1; j < anInt971; j++) {
			int k;
			if(j == -1)
				k = anInt969;
			else
				k = sessionPlayerList[j];
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[k];
			if(class50_sub1_sub4_sub3_sub2 != null)
				method68(1, (byte) -97, class50_sub1_sub4_sub3_sub2);
		}

		if(i < anInt1222 || i > anInt1222) {
			for(int l = 1; l > 0; l++)
				;
		}
	}

	public static void method101(boolean flag) {
		Class22.aBoolean451 = true;
		if(!flag)
			aBoolean1242 = !aBoolean1242;
		Rasterizer.aBoolean1527 = true;
		aBoolean926 = true;
		Class8.aBoolean169 = true;
		Class47.aBoolean772 = true;
	}

	public void method102(long l, int i) {
		try {
			if(l == 0L)
				return;
			if(anInt859 >= 100 && anInt1068 != 1) {
				sendChatboxMessage("", "Your friendlist is full. Max of 100 for free users, and 200 for members", 0);
				return;
			}
			if(anInt859 >= 200) {
				sendChatboxMessage("", "Your friendlist is full. Max of 100 for free users, and 200 for members", 0);
				return;
			}
			String s = NameUtils.formatName(NameUtils.longToName(l));
			for(int j = 0; j < anInt859; j++)
				if(aLongArray1130[j] == l) {
					sendChatboxMessage("", s + " is already on your friend list", 0);
					return;
				}

			for(int k = 0; k < anInt855; k++)
				if(aLongArray1073[k] == l) {
					sendChatboxMessage("", "Please remove " + s + " from your ignore list first", 0);
					return;
				}

			if(s.equals(sessionPlayer.username))
				return;
			aStringArray849[anInt859] = s;
			if(i != -45229)
				anInt1178 = -30;
			aLongArray1130[anInt859] = l;
			anIntArray1267[anInt859] = 0;
			anInt859++;
			tabRepaintRequested = true;
			byteStream2.createFrame(120);
			byteStream2.putLong(l);
			return;
		} catch(RuntimeException runtimeexception) {
			Signlink.reporterror("94629, " + l + ", " + i + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void method103(byte byte0, GameInterface gameInterface) {
		if(byte0 == 2)
			byte0 = 0;
		else
			anInt1004 = -82;
		int i = gameInterface.contentType;
		if(i >= 1 && i <= 100 || i >= 701 && i <= 800) {
			if(i == 1 && anInt860 == 0) {
				gameInterface.text = "Loading friend list";
				gameInterface.actionType = 0;
				return;
			}
			if(i == 1 && anInt860 == 1) {
				gameInterface.text = "Connecting to friendserver";
				gameInterface.actionType = 0;
				return;
			}
			if(i == 2 && anInt860 != 2) {
				gameInterface.text = "Please wait...";
				gameInterface.actionType = 0;
				return;
			}
			int j = anInt859;
			if(anInt860 != 2)
				j = 0;
			if(i > 700)
				i -= 601;
			else
				i--;
			if(i >= j) {
				gameInterface.text = "";
				gameInterface.actionType = 0;
				return;
			} else {
				gameInterface.text = aStringArray849[i];
				gameInterface.actionType = 1;
				return;
			}
		}
		if(i >= 101 && i <= 200 || i >= 801 && i <= 900) {
			int k = anInt859;
			if(anInt860 != 2)
				k = 0;
			if(i > 800)
				i -= 701;
			else
				i -= 101;
			if(i >= k) {
				gameInterface.text = "";
				gameInterface.actionType = 0;
				return;
			}
			if(anIntArray1267[i] == 0)
				gameInterface.text = "@red@Offline";
			else if(anIntArray1267[i] < 200) {
				if(anIntArray1267[i] == nodeId)
					gameInterface.text = "@gre@World" + (anIntArray1267[i] - 9);
				else
					gameInterface.text = "@yel@World" + (anIntArray1267[i] - 9);
			} else if(anIntArray1267[i] == nodeId)
				gameInterface.text = "@gre@Classic" + (anIntArray1267[i] - 219);
			else
				gameInterface.text = "@yel@Classic" + (anIntArray1267[i] - 219);
			gameInterface.actionType = 1;
			return;
		}
		if(i == 203) {
			int l = anInt859;
			if(anInt860 != 2)
				l = 0;
			gameInterface.scrollHeight = l * 15 + 20;
			if(gameInterface.scrollHeight <= gameInterface.height)
				gameInterface.scrollHeight = gameInterface.height + 1;
			return;
		}
		if(i >= 401 && i <= 500) {
			if((i -= 401) == 0 && anInt860 == 0) {
				gameInterface.text = "Loading ignore list";
				gameInterface.actionType = 0;
				return;
			}
			if(i == 1 && anInt860 == 0) {
				gameInterface.text = "Please wait...";
				gameInterface.actionType = 0;
				return;
			}
			int i1 = anInt855;
			if(anInt860 == 0)
				i1 = 0;
			if(i >= i1) {
				gameInterface.text = "";
				gameInterface.actionType = 0;
				return;
			} else {
				gameInterface.text = NameUtils.formatName(NameUtils.longToName(aLongArray1073[i]));
				gameInterface.actionType = 1;
				return;
			}
		}
		if(i == 503) {
			gameInterface.scrollHeight = anInt855 * 15 + 20;
			if(gameInterface.scrollHeight <= gameInterface.height)
				gameInterface.scrollHeight = gameInterface.height + 1;
			return;
		}
		if(i == 327) {
			gameInterface.anInt252 = 150;
			gameInterface.anInt253 = (int) (Math.sin((double) currentTime / 40D) * 256D) & 0x7ff;
			if(aBoolean1277) {
				for(int j1 = 0; j1 < 7; j1++) {
					int i2 = anIntArray1326[j1];
					if(i2 >= 0 && !IdentityKit.cache[i2].method436(256))
						return;
				}

				aBoolean1277 = false;
				Model models[] = new Model[7];
				int j2 = 0;
				for(int k2 = 0; k2 < 7; k2++) {
					int l2 = anIntArray1326[k2];
					if(l2 >= 0)
						models[j2++] = IdentityKit.cache[l2].getBodyModel();
				}

				Model model = new Model(j2, models);
				for(int i3 = 0; i3 < 5; i3++)
					if(anIntArray1099[i3] != 0) {
						model.recolour(playerBodyRecolours[i3][0], playerBodyRecolours[i3][anIntArray1099[i3]]);
						if(i3 == 1)
							model.recolour(skinColours[0], skinColours[anIntArray1099[i3]]);
					}

				model.createBones();
				model.method585(Class14.aClass14Array293[((Mob) (sessionPlayer)).standAnim].anIntArray295[0]);
				model.light(64, 850, -30, -50, -30, true);
				gameInterface.anInt283 = 5;
				gameInterface.anInt284 = 0;
				GameInterface.method201(5, model, 0);
			}
			return;
		}
		if(i == 328) {
			/*gameInterface.anInt252 = 150;
			gameInterface.anInt253 = (int) (Math.sin((double) anInt1325 / 40D) * 256D) & 0x7ff;
			if(aBoolean1277) {
				for(int k1 = 0; k1 < 7; k1++) {
					int l1 = anIntArray1326[k1];
					if(l1 >= 0 && !IdentityKit.cache[l1].method436((byte) 2))
						return;
				}

				aBoolean1277 = false;
				Model models[] = new Model[7];
				int i2 = 0;
				for(int j2 = 0; j2 < 7; j2++) {
					int k2 = anIntArray1326[j2];
					if(k2 >= 0)
						models[i2++] = IdentityKit.cache[k2].getBodyModel();
				}

				Model model = new Model(i2, models);
				for(int l2 = 0; l2 < 5; l2++)
					if(anIntArray1099[l2] != 0) {
						model.recolour(playerBodyRecolours[l2][0], playerBodyRecolours[l2][anIntArray1099[l2]]);
						if(l2 == 1)
							model.recolour(skinColours[0], skinColours[anIntArray1099[l2]]);
					}

				model.createBones();
				model.method585(Class14.aClass14Array293[((Mob) (sessionPlayer)).standAnim].anIntArray295[0], (byte) 6);
				model.light(64, 850, -30, -50, -30, true);
				gameInterface.anInt283 = 5;
				gameInterface.anInt284 = 0;
				GameInterface.method201(5, model, 0);
			}
			return;*/
			int verticleTilt = 150;
			int animationSpeed = (int)(Math.sin((double) currentTime / 40D) * 256D) & 0x7ff;
			gameInterface.anInt252 = verticleTilt;
			gameInterface.anInt253 = animationSpeed;
			if(aBoolean1277) {
				Model playerModel = sessionPlayer.getRotatedModel();
				for(int l2 = 0; l2 < 5; l2++) {
					if(anIntArray1099[l2] != 0) {
						playerModel.recolour(playerBodyRecolours[l2][0], playerBodyRecolours[l2][anIntArray1099[l2]]);
						if(l2 == 1)
							playerModel.recolour(skinColours[0], skinColours[anIntArray1099[l2]]);
					}
				}
				playerModel.createBones();
				playerModel.method585(Class14.aClass14Array293[sessionPlayer.standAnim].anIntArray295[0]);
				gameInterface.anInt283 = 5;
				gameInterface.anInt284 = 0;
				//GameInterface.method208(aBoolean994, playerModel);
				GameInterface.method201(5, playerModel, 0);
			}
			return;
		}
		if(i == 324) {
			if(aClass50_Sub1_Sub1_Sub1_1102 == null) {
				aClass50_Sub1_Sub1_Sub1_1102 = gameInterface.aClass50_Sub1_Sub1_Sub1_212;
				aClass50_Sub1_Sub1_Sub1_1103 = gameInterface.aClass50_Sub1_Sub1_Sub1_245;
			}
			if(aBoolean1144) {
				gameInterface.aClass50_Sub1_Sub1_Sub1_212 = aClass50_Sub1_Sub1_Sub1_1103;
				return;
			} else {
				gameInterface.aClass50_Sub1_Sub1_Sub1_212 = aClass50_Sub1_Sub1_Sub1_1102;
				return;
			}
		}
		if(i == 325) {
			if(aClass50_Sub1_Sub1_Sub1_1102 == null) {
				aClass50_Sub1_Sub1_Sub1_1102 = gameInterface.aClass50_Sub1_Sub1_Sub1_212;
				aClass50_Sub1_Sub1_Sub1_1103 = gameInterface.aClass50_Sub1_Sub1_Sub1_245;
			}
			if(aBoolean1144) {
				gameInterface.aClass50_Sub1_Sub1_Sub1_212 = aClass50_Sub1_Sub1_Sub1_1102;
				return;
			} else {
				gameInterface.aClass50_Sub1_Sub1_Sub1_212 = aClass50_Sub1_Sub1_Sub1_1103;
				return;
			}
		}
		if(i == 600) {
			gameInterface.text = aString839;
			if(currentTime % 20 < 10) {
				gameInterface.text += "|";
				return;
			} else {
				gameInterface.text += " ";
				return;
			}
		}
		if(i == 620)
			if(anInt867 >= 1) {
				if(aBoolean1098) {
					gameInterface.textColour = 0xff0000;
					gameInterface.text = "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					gameInterface.textColour = 0xffffff;
					gameInterface.text = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				gameInterface.text = "";
			}
		if(i == 660) {
			int k1 = anInt1170 - anInt1215;
			String s1;
			if(k1 <= 0)
				s1 = "earlier today";
			else if(k1 == 1)
				s1 = "yesterday";
			else
				s1 = k1 + " days ago";
			gameInterface.text = "You last logged in @red@" + s1 + "@bla@ from: @red@" + Signlink.dns;
		}
		if(i == 661)
			if(recoveryQuestionsDate == 0)
				gameInterface.text = "\\nYou have not yet set any recovery questions.\\nIt is @lre@strongly@yel@ recommended that you do so.\\n\\nIf you don't you will be @lre@unable to recover your\\n@lre@password@yel@ if you forget it, or it is stolen.";
			else if(recoveryQuestionsDate <= anInt1170) {
				gameInterface.text = "\\n\\nRecovery Questions Last Set:\\n@gre@" + formatDate(recoveryQuestionsDate);
			} else {
				int l1 = (anInt1170 + 14) - recoveryQuestionsDate;
				String s2;
				if(l1 <= 0)
					s2 = "Earlier today";
				else if(l1 == 1)
					s2 = "Yesterday";
				else
					s2 = l1 + " days ago";
				gameInterface.text = s2 + " you requested@lre@ new recovery\\n@lre@questions.@yel@ The requested change will occur\\non: @lre@" + formatDate(recoveryQuestionsDate)
						+ "\\n\\nIf you do not remember making this request\\ncancel it immediately, and change your password.";
			}
		if(i == 662) {
			String s;
			if(websiteMessages == 0)
				s = "@yel@0 unread messages";
			else if(websiteMessages == 1)
				s = "@gre@1 unread message";
			else
				s = "@gre@" + websiteMessages + " unread messages";
			gameInterface.text = "You have " + s + "\\nin your message centre.";
		}
		if(i == 663)
			if(lastPassChange <= 0 || lastPassChange > anInt1170 + 10)
				gameInterface.text = "Last password change:\\n@gre@Never changed";
			else
				gameInterface.text = "Last password change:\\n@gre@" + formatDate(lastPassChange);
		if(i == 665)
			if(memberDaysLeft > 2 && !aBoolean925)
				gameInterface.text = "This is a non-members\\nworld. To enjoy your\\nmembers benefits we\\nrecommend you play on a\\nmembers world instead.";
			else if(memberDaysLeft > 2)
				gameInterface.text = "\\n\\nYou have @gre@" + memberDaysLeft + "@yel@ days of\\nmember credit remaining.";
			else if(memberDaysLeft > 0)
				gameInterface.text = "You have @gre@" + memberDaysLeft + "@yel@ days of\\nmember credit remaining.\\n\\n@lre@Credit low! Renew now\\n@lre@to avoid losing members.";
			else
				gameInterface.text = "You are not a member.\\n\\nChoose to subscribe and\\nyou'll get loads of extra\\nbenefits and features.";
		if(i == 667)
			if(memberDaysLeft > 2 && !aBoolean925)
				gameInterface.text = "To switch to a members-only world:\\n1) Logout and return to the world selection page.\\n2) Choose one of the members world with a gold star next to it's name.\\n\\nIf you prefer you can continue to use this world,\\nbut members only features will be unavailable here.";
			else if(memberDaysLeft > 0)
				gameInterface.text = "To extend or cancel a subscription:\\n1) Logout and return to the frontpage of this website.\\n2)Choose the relevant option from the 'membership' section.\\n\\nNote: If you are a credit card subscriber a top-up payment will\\nautomatically be taken when 3 days credit remain.\\n(unless you cancel your subscription, which can be done at any time.)";
			else
				gameInterface.text = "To start a subscripton:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Start a new subscription'";
		if(i == 668) {
			if(recoveryQuestionsDate > anInt1170) {
				gameInterface.text = "To cancel this request:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Cancel recovery questions'.";
				return;
			}
			gameInterface.text = "To change your recovery questions:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Set new recovery questions'.";
		}
	}

	public String formatDate(int i) {
		if(i > anInt1170 + 10) {
			return "Unknown";
		} else {
			long l = ((long) i + 11745L) * 0x5265c00L;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(l));
			int j = calendar.get(5);
			int k = calendar.get(2);
			int i1 = calendar.get(1);
			String as[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			return j + "-" + as[k] + "-" + i1;
		}
	}

	public void method105(int i, int j) {
		packetSize += i;
		int k = Class43.aClass43Array704[j].anInt712;
		if(k == 0)
			return;
		int l = anIntArray1039[j];
		if(k == 1) {
			if(l == 1)
				Rasterizer.method501(0.90000000000000002D, (byte) 6);
			if(l == 2)
				Rasterizer.method501(0.80000000000000004D, (byte) 6);
			if(l == 3)
				Rasterizer.method501(0.69999999999999996D, (byte) 6);
			if(l == 4)
				Rasterizer.method501(0.59999999999999998D, (byte) 6);
			ItemDefinition.imageCache.method347();
			repaintRequested = true;
		}
		if(k == 3) {
			boolean flag = aBoolean1266;
			if(l == 0) {
				method99(aBoolean1266, (byte) 8, 0);
				aBoolean1266 = true;
			}
			if(l == 1) {
				method99(aBoolean1266, (byte) 8, -400);
				aBoolean1266 = true;
			}
			if(l == 2) {
				method99(aBoolean1266, (byte) 8, -800);
				aBoolean1266 = true;
			}
			if(l == 3) {
				method99(aBoolean1266, (byte) 8, -1200);
				aBoolean1266 = true;
			}
			if(l == 4)
				aBoolean1266 = false;
			if(aBoolean1266 != flag && !aBoolean926) {
				if(aBoolean1266) {
					anInt1270 = anInt1327;
					aBoolean1271 = true;
					aClass32_Sub1_1291.method329(2, anInt1270);
				} else {
					method50(false);
				}
				anInt1128 = 0;
			}
		}
		if(k == 4) {
			if(l == 0) {
				aBoolean1301 = true;
				method58(822, 0);
			}
			if(l == 1) {
				aBoolean1301 = true;
				method58(822, -400);
			}
			if(l == 2) {
				aBoolean1301 = true;
				method58(822, -800);
			}
			if(l == 3) {
				aBoolean1301 = true;
				method58(822, -1200);
			}
			if(l == 4)
				aBoolean1301 = false;
		}
		if(k == 5)
			anInt1300 = l;
		if(k == 6)
			anInt998 = l;
		if(k == 8) {
			anInt1223 = l;
			aBoolean1240 = true;
		}
		if(k == 9)
			anInt955 = l;
	}

	public int method106(int i, int j, int k, int l) {
		if(l < 8 || l > 8)
			byteStream2.putByte(235);
		int i1 = 256 - k;
		return ((i & 0xff00ff) * i1 + (j & 0xff00ff) * k & 0xff00ff00) + ((i & 0xff00) * i1 + (j & 0xff00) * k & 0xff0000) >> 8;
	}

	public void method107(int i) {
		anInt1246 = 0;
		int j = (((Mob) (sessionPlayer)).anInt1610 >> 7) + anInt1040;
		int k;
		for(k = (((Mob) (sessionPlayer)).anInt1611 >> 7) + anInt1041; i >= 0;)
			return;

		if(j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
			anInt1246 = 1;
		if(j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
			anInt1246 = 1;
		if(anInt1246 == 1 && j >= 3139 && j <= 3199 && k >= 3008 && k <= 3062)
			anInt1246 = 0;
	}

	public void method108(int i) {
		int j = boldFont.getFormattedStringWidth("Choose Option");
		for(int k = 0; k < menuActionIndex; k++) {
			int l = boldFont.getFormattedStringWidth(menuActionName[k]);
			if(l > j)
				j = l;
		}

		j += 8;
		if(i <= 0)
			aBoolean1190 = !aBoolean1190;
		int i1 = 15 * menuActionIndex + 21;
		if(super.clickX > 4 && super.clickY > 4 && super.clickX < 516 && super.clickY < 338) {
			int j1 = super.clickX - 4 - j / 2;
			if(j1 + j > 512)
				j1 = 512 - j;
			if(j1 < 0)
				j1 = 0;
			int i2 = super.clickY - 4;
			if(i2 + i1 > 334)
				i2 = 334 - i1;
			if(i2 < 0)
				i2 = 0;
			menuOpen = true;
			menuScreenArea = 0;
			anInt1305 = j1;
			anInt1306 = i2;
			anInt1307 = j;
			anInt1308 = 15 * menuActionIndex + 22;
		}
		if(super.clickX > 553 && super.clickY > 205 && super.clickX < 743 && super.clickY < 466) {
			int k1 = super.clickX - 553 - j / 2;
			if(k1 < 0)
				k1 = 0;
			else if(k1 + j > 190)
				k1 = 190 - j;
			int j2 = super.clickY - 205;
			if(j2 < 0)
				j2 = 0;
			else if(j2 + i1 > 261)
				j2 = 261 - i1;
			menuOpen = true;
			menuScreenArea = 1;
			anInt1305 = k1;
			anInt1306 = j2;
			anInt1307 = j;
			anInt1308 = 15 * menuActionIndex + 22;
		}
		if(super.clickX > 17 && super.clickY > 357 && super.clickX < 496 && super.clickY < 453) {
			int l1 = super.clickX - 17 - j / 2;
			if(l1 < 0)
				l1 = 0;
			else if(l1 + j > 479)
				l1 = 479 - j;
			int k2 = super.clickY - 357;
			if(k2 < 0)
				k2 = 0;
			else if(k2 + i1 > 96)
				k2 = 96 - i1;
			menuOpen = true;
			menuScreenArea = 2;
			anInt1305 = l1;
			anInt1306 = k2;
			anInt1307 = j;
			anInt1308 = 15 * menuActionIndex + 22;
		}
	}

	public void method109(int i) {
		if(i != 30729)
			anInt1056 = aISAAC_899.getNextValue();
		method75(0);
		if(anInt1023 == 1)
			cross[anInt1022 / 100].method461(anInt1021 - 8 - 4, anInt1020 - 8 - 4, -488);
		if(anInt1023 == 2)
			cross[4 + anInt1022 / 100].method461(anInt1021 - 8 - 4, anInt1020 - 8 - 4, -488);
		if(anInt1279 != -1) {
			method88(anInt951, anInt1279, (byte) 5);
			drawInterface(0, 0, 0, GameInterface.getInterface(anInt1279));
		}
		if(anInt1169 != -1) {
			method88(anInt951, anInt1169, (byte) 5);
			drawInterface(0, 0, 0, GameInterface.getInterface(anInt1169));
		}
		method107(-7);
		if(!menuOpen) {
			method91(-521);
			method34((byte) -79);
		} else if(menuScreenArea == 0)
			drawMenu();
		if(anInt1319 == 1)
			multiwayOverlay.method461(296, 472, -488);
		if(aBoolean868) {
			char c = '\u01FB';
			int k = 20;
			int i1 = 0xffff00;
			if(super.anInt10 < 30 && aBoolean926)
				i1 = 0xff0000;
			if(super.anInt10 < 20 && !aBoolean926)
				i1 = 0xff0000;
			normalFont.method469(true, "Fps:" + super.anInt10, i1, c, k);
			k += 15;
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			i1 = 0xffff00;
			if(j1 > 0x2000000 && aBoolean926)
				i1 = 0xff0000;
			if(j1 > 0x4000000 && !aBoolean926)
				i1 = 0xff0000;
			normalFont.method469(true, "Mem:" + j1 + "k", 0xffff00, c, k);
			k += 15;
		}
		if(anInt1057 != 0) {
			int j = anInt1057 / 50;
			int l = j / 60;
			j %= 60;
			if(j < 10)
				normalFont.method474("System update in: " + l + ":0" + j, 4, 329, 0xffff00);
			else
				normalFont.method474("System update in: " + l + ":" + j, 4, 329, 0xffff00);
			anInt895++;
			if(anInt895 > 112) {
				anInt895 = 0;
				byteStream2.createFrame(197);
				byteStream2.putInt(0);
			}
		}
	}

	public void run() {
		if(aBoolean1314) {
			method17((byte) 4);
			return;
		} else {
			super.run();
			return;
		}
	}

	public int method110(int i, int j, byte byte0, int k) {
		int l = j >> 7;
		int i1 = i >> 7;
		if(l < 0 || i1 < 0 || l > 103 || i1 > 103)
			return 0;
		int j1 = k;
		if(j1 < 3 && (aByteArrayArrayArray1125[1][l][i1] & 2) == 2)
			j1++;
		int k1 = j & 0x7f;
		int l1 = i & 0x7f;
		if(byte0 != 9)
			aBoolean953 = !aBoolean953;
		int i2 = anIntArrayArrayArray891[j1][l][i1] * (128 - k1) + anIntArrayArrayArray891[j1][l + 1][i1] * k1 >> 7;
		int j2 = anIntArrayArrayArray891[j1][l][i1 + 1] * (128 - k1) + anIntArrayArrayArray891[j1][l + 1][i1 + 1] * k1 >> 7;
		return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	public AppletContext getAppletContext() {
		if(Signlink.mainapp != null)
			return Signlink.mainapp.getAppletContext();
		else
			return super.getAppletContext();
	}

	public void method111(int i) {
		i = 21 / i;
		if(anInt1223 == 0)
			return;
		int j = 0;
		if(anInt1057 != 0)
			j = 1;
		for(int k = 0; k < 100; k++)
			if(aStringArray1298[k] != null) {
				int l = anIntArray1296[k];
				String s = aStringArray1297[k];
				if(s != null && s.startsWith("@cr1@")) {
					s = s.substring(5);
				}
				if(s != null && s.startsWith("@cr2@")) {
					s = s.substring(5);
				}
				if((l == 3 || l == 7) && (l == 7 || anInt887 == 0 || anInt887 == 1 && method148(13292, s))) {
					int i1 = 329 - j * 13;
					if(super.mouseX > 4 && super.mouseY - 4 > i1 - 10 && super.mouseY - 4 <= i1 + 3) {
						int j1 = normalFont.getFormattedStringWidth("From:  " + s + aStringArray1298[k]) + 25;
						if(j1 > 450)
							j1 = 450;
						if(super.mouseX < 4 + j1) {
							if(anInt867 >= 1) {
								menuActionName[menuActionIndex] = "Report abuse @whi@" + s;
								menuActionId[menuActionIndex] = 2507;
								menuActionIndex++;
							}
							menuActionName[menuActionIndex] = "Add ignore @whi@" + s;
							menuActionId[menuActionIndex] = 2574;
							menuActionIndex++;
							menuActionName[menuActionIndex] = "Add friend @whi@" + s;
							menuActionId[menuActionIndex] = 2762;
							menuActionIndex++;
						}
					}
					if(++j >= 5)
						return;
				}
				if((l == 5 || l == 6) && anInt887 < 2 && ++j >= 5)
					return;
			}

	}

	public void method112(byte byte0, int i) {
		if(byte0 != 36)
			byteStream2.putByte(6);
		GameInterface class13 = GameInterface.getInterface(i);
		for(int j = 0; j < class13.children.length; j++) {
			if(class13.children[j] == -1)
				break;
			GameInterface class13_1 = GameInterface.getInterface(class13.children[j]);
			if(class13_1.type == 1)
				method112((byte) 36, class13_1.id);
			class13_1.anInt235 = 0;
			class13_1.anInt227 = 0;
		}

	}

	public void method113(int i, int j, int k) {
		int l = 0;
		i = 44 / i;
		for(int i1 = 0; i1 < 100; i1++) {
			if(aStringArray1298[i1] == null)
				continue;
			int j1 = anIntArray1296[i1];
			int k1 = (70 - l * 14) + anInt851 + 4;
			if(k1 < -20)
				break;
			String s = aStringArray1297[i1];
			if(s != null && s.startsWith("@cr1@")) {
				s = s.substring(5);
			}
			if(s != null && s.startsWith("@cr2@")) {
				s = s.substring(5);
			}
			if(j1 == 0)
				l++;
			if((j1 == 1 || j1 == 2) && (j1 == 1 || anInt1006 == 0 || anInt1006 == 1 && method148(13292, s))) {
				if(k > k1 - 14 && k <= k1 && !s.equals(sessionPlayer.username)) {
					if(anInt867 >= 1) {
						menuActionName[menuActionIndex] = "Report abuse @whi@" + s;
						menuActionId[menuActionIndex] = 507;
						menuActionIndex++;
					}
					menuActionName[menuActionIndex] = "Add ignore @whi@" + s;
					menuActionId[menuActionIndex] = 574;
					menuActionIndex++;
					menuActionName[menuActionIndex] = "Add friend @whi@" + s;
					menuActionId[menuActionIndex] = 762;
					menuActionIndex++;
				}
				l++;
			}
			if((j1 == 3 || j1 == 7) && anInt1223 == 0 && (j1 == 7 || anInt887 == 0 || anInt887 == 1 && method148(13292, s))) {
				if(k > k1 - 14 && k <= k1) {
					if(anInt867 >= 1) {
						menuActionName[menuActionIndex] = "Report abuse @whi@" + s;
						menuActionId[menuActionIndex] = 507;
						menuActionIndex++;
					}
					menuActionName[menuActionIndex] = "Add ignore @whi@" + s;
					menuActionId[menuActionIndex] = 574;
					menuActionIndex++;
					menuActionName[menuActionIndex] = "Add friend @whi@" + s;
					menuActionId[menuActionIndex] = 762;
					menuActionIndex++;
				}
				l++;
			}
			if(j1 == 4 && (anInt1227 == 0 || anInt1227 == 1 && method148(13292, s))) {
				if(k > k1 - 14 && k <= k1) {
					menuActionName[menuActionIndex] = "Accept trade @whi@" + s;
					menuActionId[menuActionIndex] = 544;
					menuActionIndex++;
				}
				l++;
			}
			if((j1 == 5 || j1 == 6) && anInt1223 == 0 && anInt887 < 2)
				l++;
			if(j1 == 8 && (anInt1227 == 0 || anInt1227 == 1 && method148(13292, s))) {
				if(k > k1 - 14 && k <= k1) {
					menuActionName[menuActionIndex] = "Accept challenge @whi@" + s;
					menuActionId[menuActionIndex] = 695;
					menuActionIndex++;
				}
				l++;
			}
		}

	}

	public void method114(int i, int j, ByteBuffer byteStream) {
		int k = byteStream.method532(402, 8);
		if(k < anInt971) {
			for(int l = k; l < anInt971; l++)
				anIntArray1295[anInt1294++] = sessionPlayerList[l];

		}
		if(k > anInt971) {
			Signlink.reporterror(aString1092 + " Too many players");
			throw new RuntimeException("eek");
		}
		anInt971 = 0;
		if(j >= 0)
			packetOpcode = -1;
		for(int i1 = 0; i1 < k; i1++) {
			int j1 = sessionPlayerList[i1];
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[j1];
			int k1 = byteStream.method532(402, 1);
			if(k1 == 0) {
				sessionPlayerList[anInt971++] = j1;
				class50_sub1_sub4_sub3_sub2.anInt1585 = currentTime;
			} else {
				int l1 = byteStream.method532(402, 2);
				if(l1 == 0) {
					sessionPlayerList[anInt971++] = j1;
					class50_sub1_sub4_sub3_sub2.anInt1585 = currentTime;
					anIntArray974[sessionNpcsAwaitingUpdate++] = j1;
				} else if(l1 == 1) {
					sessionPlayerList[anInt971++] = j1;
					class50_sub1_sub4_sub3_sub2.anInt1585 = currentTime;
					int i2 = byteStream.method532(402, 3);
					class50_sub1_sub4_sub3_sub2.method566(false, i2, -808);
					int k2 = byteStream.method532(402, 1);
					if(k2 == 1)
						anIntArray974[sessionNpcsAwaitingUpdate++] = j1;
				} else if(l1 == 2) {
					sessionPlayerList[anInt971++] = j1;
					class50_sub1_sub4_sub3_sub2.anInt1585 = currentTime;
					int j2 = byteStream.method532(402, 3);
					class50_sub1_sub4_sub3_sub2.method566(true, j2, -808);
					int l2 = byteStream.method532(402, 3);
					class50_sub1_sub4_sub3_sub2.method566(true, l2, -808);
					int i3 = byteStream.method532(402, 1);
					if(i3 == 1)
						anIntArray974[sessionNpcsAwaitingUpdate++] = j1;
				} else if(l1 == 3)
					anIntArray1295[anInt1294++] = j1;
			}
		}

	}

	public void method115(int i, int j) {
		int ai[] = minimapImage.pixels;
		int k = ai.length;
		for(int l = 0; l < k; l++)
			ai[l] = 0;

		for(int i1 = 1; i1 < 103; i1++) {
			int j1 = 24628 + (103 - i1) * 512 * 4;
			for(int l1 = 1; l1 < 103; l1++) {
				if((aByteArrayArrayArray1125[i][l1][i1] & 0x18) == 0)
					aClass22_1164.method276(ai, j1, 512, i, l1, i1);
				if(i < 3 && (aByteArrayArrayArray1125[i + 1][l1][i1] & 8) != 0)
					aClass22_1164.method276(ai, j1, 512, i + 1, l1, i1);
				j1 += 4;
			}

		}

		int k1 = ((238 + (int) (Math.random() * 20D)) - 10 << 16) + ((238 + (int) (Math.random() * 20D)) - 10 << 8) + ((238 + (int) (Math.random() * 20D)) - 10);
		if(j != 0)
			packetOpcode = byteStream4.getUnsignedByte();
		int i2 = (238 + (int) (Math.random() * 20D)) - 10 << 16;
		minimapImage.initDrawingArea();
		for(int j2 = 1; j2 < 103; j2++) {
			for(int k2 = 1; k2 < 103; k2++) {
				if((aByteArrayArrayArray1125[i][k2][j2] & 0x18) == 0)
					method150(j2, i, k2, i2, 563, k1);
				if(i < 3 && (aByteArrayArrayArray1125[i + 1][k2][j2] & 8) != 0)
					method150(j2, i + 1, k2, i2, 563, k1);
			}

		}

		if(gameScreenDrawingArea != null) {
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
		}
		anInt1082++;
		if(anInt1082 > 177) {
			anInt1082 = 0;
			byteStream2.createFrame(173);
			byteStream2.putTriByte(0x288b80);
		}
		anInt1076 = 0;
		for(int l2 = 0; l2 < 104; l2++) {
			for(int i3 = 0; i3 < 104; i3++) {
				int j3 = aClass22_1164.method270(anInt1091, l2, i3);
				if(j3 != 0) {
					j3 = j3 >> 14 & 0x7fff;
					int k3 = Class47.method423(j3).anInt806;
					if(k3 >= 0) {
						int l3 = l2;
						int i4 = i3;
						if(k3 != 22 && k3 != 29 && k3 != 34 && k3 != 36 && k3 != 46 && k3 != 47 && k3 != 48) {
							byte byte0 = 104;
							byte byte1 = 104;
							int ai1[][] = aClass46Array1260[anInt1091].anIntArrayArray757;
							for(int j4 = 0; j4 < 10; j4++) {
								int k4 = (int) (Math.random() * 4D);
								if(k4 == 0 && l3 > 0 && l3 > l2 - 3 && (ai1[l3 - 1][i4] & 0x1280108) == 0)
									l3--;
								if(k4 == 1 && l3 < byte0 - 1 && l3 < l2 + 3 && (ai1[l3 + 1][i4] & 0x1280180) == 0)
									l3++;
								if(k4 == 2 && i4 > 0 && i4 > i3 - 3 && (ai1[l3][i4 - 1] & 0x1280102) == 0)
									i4--;
								if(k4 == 3 && i4 < byte1 - 1 && i4 < i3 + 3 && (ai1[l3][i4 + 1] & 0x1280120) == 0)
									i4++;
							}

						}
						mapMarkImage[anInt1076] = mapfunctions[k3];
						markPosX[anInt1076] = l3;
						markPosY[anInt1076] = i4;
						anInt1076++;
					}
				}
			}

		}

	}

	public boolean method116(int i, int j, byte abyte0[]) {
		if(i < 3 || i > 3)
			throw new NullPointerException();
		if(abyte0 == null)
			return true;
		else
			return Signlink.wavesave(abyte0, j);
	}

	public int method117(byte byte0) {
		int i = 3;
		if(byte0 == aByte956)
			byte0 = 0;
		else
			startUp();
		if(anInt1219 < 310) {
			anInt978++;
			if(anInt978 > 1457) {
				anInt978 = 0;
				byteStream2.createFrame(244);
				byteStream2.putByte(0);
				int j = byteStream2.position;
				byteStream2.putByte(219);
				byteStream2.putShort(37745);
				byteStream2.putByte(61);
				byteStream2.putShort(43756);
				byteStream2.putShort((int) (Math.random() * 65536D));
				byteStream2.putByte((int) (Math.random() * 256D));
				byteStream2.putShort(51171);
				if((int) (Math.random() * 2D) == 0)
					byteStream2.putShort(15808);
				byteStream2.putByte(97);
				byteStream2.putByte((int) (Math.random() * 256D));
				byteStream2.putSizeByte(byteStream2.position - j);
			}
			int k = anInt1216 >> 7;
			int l = anInt1218 >> 7;
			int i1 = ((Mob) (sessionPlayer)).anInt1610 >> 7;
			int j1 = ((Mob) (sessionPlayer)).anInt1611 >> 7;
			if((aByteArrayArrayArray1125[anInt1091][k][l] & 4) != 0)
				i = anInt1091;
			int k1;
			if(i1 > k)
				k1 = i1 - k;
			else
				k1 = k - i1;
			int l1;
			if(j1 > l)
				l1 = j1 - l;
			else
				l1 = l - j1;
			if(k1 > l1) {
				int i2 = (l1 * 0x10000) / k1;
				int k2 = 32768;
				while(k != i1) {
					if(k < i1)
						k++;
					else if(k > i1)
						k--;
					if((aByteArrayArrayArray1125[anInt1091][k][l] & 4) != 0)
						i = anInt1091;
					k2 += i2;
					if(k2 >= 0x10000) {
						k2 -= 0x10000;
						if(l < j1)
							l++;
						else if(l > j1)
							l--;
						if((aByteArrayArrayArray1125[anInt1091][k][l] & 4) != 0)
							i = anInt1091;
					}
				}
			} else {
				int j2 = (k1 * 0x10000) / l1;
				int l2 = 32768;
				while(l != j1) {
					if(l < j1)
						l++;
					else if(l > j1)
						l--;
					if((aByteArrayArrayArray1125[anInt1091][k][l] & 4) != 0)
						i = anInt1091;
					l2 += j2;
					if(l2 >= 0x10000) {
						l2 -= 0x10000;
						if(k < i1)
							k++;
						else if(k > i1)
							k--;
						if((aByteArrayArrayArray1125[anInt1091][k][l] & 4) != 0)
							i = anInt1091;
					}
				}
			}
		}
		if((aByteArrayArrayArray1125[anInt1091][((Mob) (sessionPlayer)).anInt1610 >> 7][((Mob) (sessionPlayer)).anInt1611 >> 7] & 4) != 0)
			i = anInt1091;
		return i;
	}

	public int method118(int i) {
		int j = method110(anInt1218, anInt1216, (byte) 9, anInt1091);
		while(i >= 0)
			packetOpcode = byteStream4.getUnsignedByte();
		if(j - anInt1217 < 800 && (aByteArrayArrayArray1125[anInt1091][anInt1216 >> 7][anInt1218 >> 7] & 4) != 0)
			return anInt1091;
		else
			return 3;
	}

	public void method12(Runnable runnable, int i) {
		if(i > 10)
			i = 10;
		if(Signlink.mainapp != null) {
			Signlink.startthread(runnable, i);
			return;
		} else {
			super.method12(runnable, i);
			return;
		}
	}

	public void method119(int i, boolean flag) {
		if(((Mob) (sessionPlayer)).anInt1610 >> 7 == anInt1120 && ((Mob) (sessionPlayer)).anInt1611 >> 7 == anInt1121)
			anInt1120 = 0;
		int j = anInt971;
		if(flag)
			j = 1;
		for(int k = 0; k < j; k++) {
			Player class50_sub1_sub4_sub3_sub2;
			int l;
			if(flag) {
				class50_sub1_sub4_sub3_sub2 = sessionPlayer;
				l = anInt969 << 14;
			} else {
				class50_sub1_sub4_sub3_sub2 = sessionPlayers[sessionPlayerList[k]];
				l = sessionPlayerList[k] << 14;
			}
			if(class50_sub1_sub4_sub3_sub2 == null || !class50_sub1_sub4_sub3_sub2.isVisible())
				continue;
			class50_sub1_sub4_sub3_sub2.aBoolean1763 = false;
			if((aBoolean926 && anInt971 > 50 || anInt971 > 200) && !flag && ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1588 == ((Mob) (class50_sub1_sub4_sub3_sub2)).standAnim)
				class50_sub1_sub4_sub3_sub2.aBoolean1763 = true;
			int i1 = ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610 >> 7;
			int j1 = ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611 >> 7;
			if(i1 < 0 || i1 >= 104 || j1 < 0 || j1 >= 104)
				continue;
			if(class50_sub1_sub4_sub3_sub2.unknownModel != null && currentTime >= class50_sub1_sub4_sub3_sub2.anInt1764 && currentTime < class50_sub1_sub4_sub3_sub2.anInt1765) {
				class50_sub1_sub4_sub3_sub2.aBoolean1763 = false;
				class50_sub1_sub4_sub3_sub2.anInt1750 = method110(((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610, (byte) 9, anInt1091);
				aClass22_1164.method253(class50_sub1_sub4_sub3_sub2.anInt1750, class50_sub1_sub4_sub3_sub2.anInt1769, 60, 7, class50_sub1_sub4_sub3_sub2, class50_sub1_sub4_sub3_sub2.anInt1768,
						((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611, class50_sub1_sub4_sub3_sub2.anInt1771, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610,
						((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1612, class50_sub1_sub4_sub3_sub2.anInt1770, anInt1091, l);
				continue;
			}
			if((((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610 & 0x7f) == 64 && (((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611 & 0x7f) == 64) {
				if(anIntArrayArray886[i1][j1] == anInt1138)
					continue;
				anIntArrayArray886[i1][j1] = anInt1138;
			}
			class50_sub1_sub4_sub3_sub2.anInt1750 = method110(((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610, (byte) 9, anInt1091);
			aClass22_1164.method252(l, class50_sub1_sub4_sub3_sub2, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1610, class50_sub1_sub4_sub3_sub2.anInt1750,
					((Mob) (class50_sub1_sub4_sub3_sub2)).aBoolean1592, 0, anInt1091, 60, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1611, ((Mob) (class50_sub1_sub4_sub3_sub2)).anInt1612);
		}

		if(i == 0)
			;
	}

	public void method120(int i, int j) {
		if(i < 0)
			return;
		int k = menuActionCmd2[i];
		int l = menuActionCmd3[i];
		int i1 = menuActionId[i];
		int j1 = menuActionCmd1[i];
		if(j < anInt921 || j > anInt921)
			packetOpcode = byteStream4.getUnsignedByte();
		if(i1 >= 2000)
			i1 -= 2000;
		if(anInt1244 != 0 && i1 != 1016) {
			anInt1244 = 0;
			aBoolean1240 = true;
		}
		if(i1 == 200) {
			Player class50_sub1_sub4_sub3_sub2 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(245);
				byteStream2.method548(3, j1);
			}
		}
		if(i1 == 227) {
			anInt1165++;
			if(anInt1165 >= 62) {
				byteStream2.createFrame(165);
				byteStream2.putByte(206);
				anInt1165 = 0;
			}
			byteStream2.createFrame(228);
			byteStream2.writeLEShort(0, k);
			byteStream2.writeShortA(j1, 0);
			byteStream2.putShort(l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 876) {
			Player class50_sub1_sub4_sub3_sub2_1 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_1 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_1)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_1)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(45);
				byteStream2.writeShortA(j1, 0);
			}
		}
		if(i1 == 921) {
			NPC class50_sub1_sub4_sub3_sub1 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(67);
				byteStream2.writeShortA(j1, 0);
			}
		}
		if(i1 == 961) {
			anInt1139 += j1;
			/*
			 * if(anInt1139 >= 115) { byteStream2.createFrame(126);
			 * byteStream2.putByte(125); anInt1139 = 0; }
			 */
			byteStream2.createFrame(203);
			byteStream2.writeShortA(l, 0);
			byteStream2.writeLEShort(0, k);
			byteStream2.writeLEShort(0, j1);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 467 && method80(l, 0, k, j1)) {
			byteStream2.createFrame(152);
			byteStream2.writeLEShort(0, j1 >> 14 & 0x7fff);
			byteStream2.writeLEShort(0, anInt1148);
			byteStream2.writeLEShort(0, anInt1149);
			byteStream2.writeLEShort(0, l + anInt1041);
			byteStream2.putShort(anInt1147);
			byteStream2.method548(3, k + anInt1040);
		}
		if(i1 == 9) {
			byteStream2.createFrame(3);
			byteStream2.writeShortA(j1, 0);
			byteStream2.putShort(l);
			byteStream2.putShort(k);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 553) {
			NPC class50_sub1_sub4_sub3_sub1_1 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_1 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_1)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_1)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(42);
				byteStream2.writeLEShort(0, j1);
			}
		}
		if(i1 == 677) {
			Player class50_sub1_sub4_sub3_sub2_2 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_2 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_2)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_2)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(116);
				byteStream2.writeLEShort(0, j1);
			}
		}
		if(i1 == 762 || i1 == 574 || i1 == 775 || i1 == 859) {
			String s = menuActionName[i];
			int l1 = s.indexOf("@whi@");
			if(l1 != -1) {
				long l3 = NameUtils.nameToLong(s.substring(l1 + 5).trim());
				if(i1 == 762)
					method102(l3, -45229);
				if(i1 == 574)
					method90(anInt1154, l3);
				if(i1 == 775)
					method53(l3, 0);
				if(i1 == 859)
					method97(325, l3);
			}
		}
		if(i1 == 930) {
			boolean flag = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag)
				flag = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			byteStream2.createFrame(54);
			byteStream2.writeShortA(j1, 0);
			byteStream2.writeLEShort(0, l + anInt1041);
			byteStream2.putShort(k + anInt1040);
		}
		if(i1 == 399) {
			byteStream2.createFrame(24);
			byteStream2.writeLEShort(0, l);
			byteStream2.writeLEShort(0, j1);
			byteStream2.writeShortA(k, 0);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 347) {
			NPC class50_sub1_sub4_sub3_sub1_2 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_2 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_2)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_2)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(57);
				byteStream2.putShort(j1);
				byteStream2.writeLEShort(0, anInt1149);
				byteStream2.method548(3, anInt1148);
				byteStream2.putShort(anInt1147);
			}
		}
		if(i1 == 890) {
			byteStream2.createFrame(79);
			byteStream2.putShort(l);
			GameInterface class13 = GameInterface.getInterface(l);
			if(class13.valueIndexArray != null && class13.valueIndexArray[0][0] == 5) {
				int i2 = class13.valueIndexArray[0][1];
				anIntArray1039[i2] = 1 - anIntArray1039[i2];
				method105(0, i2);
				tabRepaintRequested = true;
			}
		}
		if(i1 == 493) {
			Player class50_sub1_sub4_sub3_sub2_3 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_3 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_3)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_3)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(233);
				byteStream2.writeShortA(j1, 0);
			}
		}
		if(i1 == 14)
			if(!menuOpen)
				aClass22_1164.method279(0, super.clickX - 4, super.clickY - 4);
			else
				aClass22_1164.method279(0, k - 4, l - 4);
		if(i1 == 903) {
			byteStream2.createFrame(1);
			byteStream2.putShort(j1);
			byteStream2.writeLEShort(0, anInt1147);
			byteStream2.writeLEShort(0, anInt1149);
			byteStream2.method548(3, anInt1148);
			byteStream2.writeShortA(k, 0);
			byteStream2.writeShortA(l, 0);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 361) {
			byteStream2.createFrame(36);
			byteStream2.putShort(anInt1172);
			byteStream2.writeShortA(l, 0);
			byteStream2.writeShortA(k, 0);
			byteStream2.writeShortA(j1, 0);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 118) {
			NPC class50_sub1_sub4_sub3_sub1_3 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_3 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_3)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_3)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				anInt1235 += j1;
				if(anInt1235 >= 143) {
					byteStream2.createFrame(157);
					byteStream2.putInt(0);
					anInt1235 = 0;
				}
				byteStream2.createFrame(13);
				byteStream2.method548(3, j1);
			}
		}
		if(i1 == 376 && method80(l, 0, k, j1)) {
			byteStream2.createFrame(210);
			byteStream2.putShort(anInt1172);
			byteStream2.writeLEShort(0, j1 >> 14 & 0x7fff);
			byteStream2.writeShortA(k + anInt1040, 0);
			byteStream2.writeLEShort(0, l + anInt1041);
		}
		if(i1 == 432) {
			NPC class50_sub1_sub4_sub3_sub1_4 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_4 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_4)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_4)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(8);
				byteStream2.writeLEShort(0, j1);
			}
		}
		if(i1 == 639)
			method15(false);
		if(i1 == 918) {
			Player class50_sub1_sub4_sub3_sub2_4 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_4 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_4)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_4)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(31);
				byteStream2.putShort(j1);
				byteStream2.writeLEShort(0, anInt1172);
			}
		}
		if(i1 == 67) {
			NPC class50_sub1_sub4_sub3_sub1_5 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_5 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_5)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_5)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(104);
				byteStream2.writeShortA(anInt1172, 0);
				byteStream2.writeLEShort(0, j1);
			}
		}
		if(i1 == 68) {
			boolean flag1 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag1)
				flag1 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			byteStream2.createFrame(77);
			byteStream2.writeShortA(k + anInt1040, 0);
			byteStream2.putShort(l + anInt1041);
			byteStream2.method548(3, j1);
		}
		if(i1 == 684) {
			boolean flag2 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag2)
				flag2 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			if((j1 & 3) == 0)
				anInt1052++;
			if(anInt1052 >= 84) {
				byteStream2.createFrame(222);
				byteStream2.putTriByte(0xabc842);
				anInt1052 = 0;
			}
			byteStream2.createFrame(71);
			byteStream2.method548(3, j1);
			byteStream2.method548(3, k + anInt1040);
			byteStream2.writeShortA(l + anInt1041, 0);
		}
		if(i1 == 544 || i1 == 695) {
			String s1 = menuActionName[i];
			int j2 = s1.indexOf("@whi@");
			if(j2 != -1) {
				s1 = s1.substring(j2 + 5).trim();
				String s7 = NameUtils.formatName(NameUtils.longToName(NameUtils.nameToLong(s1)));
				boolean flag8 = false;
				for(int j3 = 0; j3 < anInt971; j3++) {
					Player class50_sub1_sub4_sub3_sub2_7 = sessionPlayers[sessionPlayerList[j3]];
					if(class50_sub1_sub4_sub3_sub2_7 == null || class50_sub1_sub4_sub3_sub2_7.username == null || !class50_sub1_sub4_sub3_sub2_7.username.equalsIgnoreCase(s7))
						continue;
					method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_7)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
							((Mob) (class50_sub1_sub4_sub3_sub2_7)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
					if(i1 == 544) {
						byteStream2.createFrame(116);
						byteStream2.writeLEShort(0, sessionPlayerList[j3]);
					}
					if(i1 == 695) {
						byteStream2.createFrame(245);
						byteStream2.method548(3, sessionPlayerList[j3]);
					}
					flag8 = true;
					break;
				}

				if(!flag8)
					sendChatboxMessage("", "Unable to find " + s7, 0);
			}
		}
		if(i1 == 225) {
			byteStream2.createFrame(177);
			byteStream2.writeShortA(k, 0);
			byteStream2.writeLEShort(0, j1);
			byteStream2.writeLEShort(0, l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 70) {
			GameInterface class13_1 = GameInterface.getInterface(l);
			spellSelected = 1;
			anInt1172 = l;
			spellUsableOn = class13_1.anInt222;
			itemSelected = 0;
			tabRepaintRequested = true;
			String s4 = class13_1.aString281;
			if(s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = class13_1.aString281;
			if(s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + class13_1.aString211 + " " + s8;
			if(spellUsableOn == 16) {
				tabRepaintRequested = true;
				selectedTab = 3;
				aBoolean950 = true;
			}
			return;
		}
		if(i1 == 891) {
			byteStream2.createFrame(4);
			byteStream2.writeLEShort(0, k);
			byteStream2.method548(3, j1);
			byteStream2.method548(3, l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 894) {
			byteStream2.createFrame(158);
			byteStream2.method548(3, k);
			byteStream2.method548(3, j1);
			byteStream2.writeLEShort(0, l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 1280) {
			method80(l, 0, k, j1);
			byteStream2.createFrame(55);
			byteStream2.writeLEShort(0, j1 >> 14 & 0x7fff);
			byteStream2.writeLEShort(0, l + anInt1041);
			byteStream2.putShort(k + anInt1040);
		}
		if(i1 == 35) {
			method80(l, 0, k, j1);
			byteStream2.createFrame(181);
			byteStream2.writeShortA(k + anInt1040, 0);
			byteStream2.writeLEShort(0, l + anInt1041);
			byteStream2.writeLEShort(0, j1 >> 14 & 0x7fff);
		}
		if(i1 == 888) {
			method80(l, 0, k, j1);
			byteStream2.createFrame(50);
			byteStream2.writeShortA(l + anInt1041, 0);
			byteStream2.writeLEShort(0, j1 >> 14 & 0x7fff);
			byteStream2.method548(3, k + anInt1040);
		}
		if(i1 == 324) {
			byteStream2.createFrame(161);
			byteStream2.method548(3, k);
			byteStream2.method548(3, j1);
			byteStream2.writeLEShort(0, l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 1094) {
			ItemDefinition class16 = ItemDefinition.forId(j1);
			GameInterface class13_4 = GameInterface.getInterface(l);
			String s5;
			if(class13_4 != null && class13_4.anIntArray224[k] >= 0x186a0)
				s5 = class13_4.anIntArray224[k] + " x " + class16.name;
			else if(class16.description != null)
				s5 = new String(class16.description);
			else
				s5 = "It's a " + class16.name + ".";
			sendChatboxMessage("", s5, 0);
		}
		if(i1 == 352) {
			GameInterface class13_2 = GameInterface.getInterface(l);
			boolean flag7 = true;
			if(class13_2.contentType > 0)
				flag7 = method60(631, class13_2);
			if(flag7) {
				byteStream2.createFrame(79);
				byteStream2.putShort(l);
			}
		}
		if(i1 == 1412) {
			int k1 = j1 >> 14 & 0x7fff;
			Class47 class47 = Class47.method423(k1);
			String s9;
			if(class47.aByteArray783 != null)
				s9 = new String(class47.aByteArray783);
			else
				s9 = "It's a " + class47.aString776 + ".";
			sendChatboxMessage("", s9, 0);
		}
		if(i1 == 575 && !aBoolean1239) {
			byteStream2.createFrame(226);
			byteStream2.putShort(l);
			aBoolean1239 = true;
		}
		if(i1 == 892) {
			method80(l, 0, k, j1);
			byteStream2.createFrame(136);
			byteStream2.putShort(k + anInt1040);
			byteStream2.writeLEShort(0, l + anInt1041);
			byteStream2.putShort(j1 >> 14 & 0x7fff);
		}
		if(i1 == 270) {
			boolean flag3 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag3)
				flag3 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			byteStream2.createFrame(230);
			byteStream2.writeLEShort(0, j1);
			byteStream2.writeShortA(k + anInt1040, 0);
			byteStream2.putShort(l + anInt1041);
		}
		if(i1 == 596) {
			Player class50_sub1_sub4_sub3_sub2_5 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_5 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_5)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_5)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(143);
				byteStream2.writeLEShort(0, anInt1149);
				byteStream2.method548(3, anInt1147);
				byteStream2.putShort(anInt1148);
				byteStream2.writeShortA(j1, 0);
			}
		}
		if(i1 == 100) {
			boolean flag4 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag4)
				flag4 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			byteStream2.createFrame(211);
			byteStream2.method548(3, anInt1147);
			byteStream2.writeShortA(anInt1149, 0);
			byteStream2.method548(3, l + anInt1041);
			byteStream2.method548(3, k + anInt1040);
			byteStream2.writeLEShort(0, anInt1148);
			byteStream2.writeLEShort(0, j1);
		}
		if(i1 == 1668) {
			NPC npc = sessionNpcs[j1];
			if(npc != null) {
				if(npc.name == null) {
					NPCDefinition npcDef = npc.npcDef;
					if(npcDef.childIds != null)
						npcDef = npcDef.method363();
					if(npcDef != null) {
						String s10;
						if(npcDef.description != null)
							s10 = new String(npcDef.description);
						else
							s10 = "It's a " + npcDef.name + ".";
						sendChatboxMessage("", s10, 0);
					}
				} else {
					String message = "";

					if(!npc.description.equals("n/a")) {
						message = npc.description;
					} else {
						message = "It's a " + npc.name + ".";
					}

					sendChatboxMessage("", message, 0);
				}
			}
		}
		if(i1 == 26) {
			boolean flag5 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag5)
				flag5 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			anInt1100++;
			if(anInt1100 >= 120) {
				byteStream2.createFrame(95);
				byteStream2.putInt(0);
				anInt1100 = 0;
			}
			byteStream2.createFrame(100);
			byteStream2.putShort(k + anInt1040);
			byteStream2.writeShortA(l + anInt1041, 0);
			byteStream2.method548(3, j1);
		}
		if(i1 == 444) {
			byteStream2.createFrame(91);
			byteStream2.writeLEShort(0, j1);
			byteStream2.method548(3, k);
			byteStream2.putShort(l);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 507) {
			String s2 = menuActionName[i];
			int k2 = s2.indexOf("@whi@");
			if(k2 != -1)
				if(anInt1169 == -1) {
					method15(false);
					aString839 = s2.substring(k2 + 5).trim();
					aBoolean1098 = false;
					anInt1231 = anInt1169 = GameInterface.anInt246;
				} else {
					sendChatboxMessage("", "Please close the interface you have open before using 'report abuse'", 0);
				}
		}
		if(i1 == 389) {
			method80(l, 0, k, j1);
			byteStream2.createFrame(241);
			byteStream2.putShort(j1 >> 14 & 0x7fff);
			byteStream2.putShort(k + anInt1040);
			byteStream2.writeShortA(l + anInt1041, 0);
		}
		if(i1 == 564) {
			byteStream2.createFrame(231);
			byteStream2.method548(3, l);
			byteStream2.writeLEShort(0, k);
			byteStream2.putShort(j1);
			anInt1329 = 0;
			anInt1330 = l;
			anInt1331 = k;
			anInt1332 = 2;
			if(GameInterface.getInterface(l).parentId == anInt1169)
				anInt1332 = 1;
			if(GameInterface.getInterface(l).parentId == anInt988)
				anInt1332 = 3;
		}
		if(i1 == 984) {
			String s3 = menuActionName[i];
			int l2 = s3.indexOf("@whi@");
			if(l2 != -1) {
				long l4 = NameUtils.nameToLong(s3.substring(l2 + 5).trim());
				int k3 = -1;
				for(int i4 = 0; i4 < anInt859; i4++) {
					if(aLongArray1130[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if(k3 != -1 && anIntArray1267[k3] > 0) {
					aBoolean1240 = true;
					anInt1244 = 0;
					aBoolean866 = true;
					aString1026 = "";
					anInt1221 = 3;
					aLong1141 = aLongArray1130[k3];
					aString937 = "Enter message to send to " + aStringArray849[k3];
				}
			}
		}
		if(i1 == 518) {
			byteStream2.createFrame(79);
			byteStream2.putShort(l);
			GameInterface class13_3 = GameInterface.getInterface(l);
			if(class13_3.valueIndexArray != null && class13_3.valueIndexArray[0][0] == 5) {
				int i3 = class13_3.valueIndexArray[0][1];
				if(anIntArray1039[i3] != class13_3.anIntArray256[0]) {
					anIntArray1039[i3] = class13_3.anIntArray256[0];
					method105(0, i3);
					tabRepaintRequested = true;
				}
			}
		}
		if(i1 == 318) {
			NPC class50_sub1_sub4_sub3_sub1_7 = sessionNpcs[j1];
			if(class50_sub1_sub4_sub3_sub1_7 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub1_7)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub1_7)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(112);
				byteStream2.writeLEShort(0, j1);
			}
		}
		if(i1 == 199) {
			boolean flag6 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			if(!flag6)
				flag6 = method35(false, false, l, ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0, k, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
			anInt1020 = super.clickX;
			anInt1021 = super.clickY;
			anInt1023 = 2;
			anInt1022 = 0;
			byteStream2.createFrame(83);
			byteStream2.writeLEShort(0, j1);
			byteStream2.putShort(l + anInt1041);
			byteStream2.writeLEShort(0, anInt1172);
			byteStream2.method548(3, k + anInt1040);
		}
		if(i1 == 55) {
			method44(aBoolean1190, anInt1191);
			anInt1191 = -1;
			aBoolean1240 = true;
		}
		if(i1 == 52) {
			itemSelected = 1;
			anInt1147 = k;
			anInt1148 = l;
			anInt1149 = j1;
			selectedItemName = String.valueOf(ItemDefinition.forId(j1).name);
			spellSelected = 0;
			tabRepaintRequested = true;
			return;
		}
		if(i1 == 1564) {
			ItemDefinition class16_1 = ItemDefinition.forId(j1);
			String s6;
			if(class16_1.description != null)
				s6 = new String(class16_1.description);
			else
				s6 = "It's a " + class16_1.name + ".";
			sendChatboxMessage("", s6, 0);
		}
		if(i1 == 408) {
			Player class50_sub1_sub4_sub3_sub2_6 = sessionPlayers[j1];
			if(class50_sub1_sub4_sub3_sub2_6 != null) {
				method35(false, false, ((Mob) (class50_sub1_sub4_sub3_sub2_6)).anIntArray1587[0], ((Mob) (sessionPlayer)).anIntArray1587[0], 1, 1, 2, 0,
						((Mob) (class50_sub1_sub4_sub3_sub2_6)).anIntArray1586[0], 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				anInt1020 = super.clickX;
				anInt1021 = super.clickY;
				anInt1023 = 2;
				anInt1022 = 0;
				byteStream2.createFrame(194);
				byteStream2.writeLEShort(0, j1);
			}
		}
		itemSelected = 0;
		spellSelected = 0;
		tabRepaintRequested = true;
	}

	public void method121(boolean flag) {
		anInt939 = 0;
		for(int i = -1; i < anInt971 + sessionNpcCount; i++) {
			Object obj;
			if(i == -1)
				obj = sessionPlayer;
			else if(i < anInt971)
				obj = sessionPlayers[sessionPlayerList[i]];
			else
				obj = sessionNpcs[sessionNpcList[i - anInt971]];
			if(obj == null || !((Mob) (obj)).isVisible())
				continue;
			if(obj instanceof NPC) {
				NPCDefinition class37 = ((NPC) obj).npcDef;
				if(class37.childIds != null)
					class37 = class37.method363();
				if(class37 == null)
					continue;
			}
			if(i < anInt971) {
				int k = 30;
				Player class50_sub1_sub4_sub3_sub2 = (Player) obj;
				if(class50_sub1_sub4_sub3_sub2.pkIcon != -1 || class50_sub1_sub4_sub3_sub2.prayerIcon != -1) {
					method136(((Mob) (obj)), false, ((Mob) (obj)).height + 15);
					if(anInt932 > -1) {
						if(class50_sub1_sub4_sub3_sub2.pkIcon != -1) {
							pkHeadicons[class50_sub1_sub4_sub3_sub2.pkIcon].method461(anInt933 - k, anInt932 - 12, -488);
							k += 25;
						}
						if(class50_sub1_sub4_sub3_sub2.prayerIcon != -1) {
							prayerHeadicons[class50_sub1_sub4_sub3_sub2.prayerIcon].method461(anInt933 - k, anInt932 - 12, -488);
							k += 25;
						}
					}
				}
				if(i >= 0 && anInt1197 == 10 && anInt1151 == sessionPlayerList[i]) {
					method136(((Mob) (obj)), false, ((Mob) (obj)).height + 15);
					if(anInt932 > -1)
						hintHeadicons[1].method461(anInt933 - k, anInt932 - 12, -488);
				}
			} else {
				NPCDefinition class37_1 = ((NPC) obj).npcDef;
				if(class37_1.headIcon >= 0 && class37_1.headIcon < prayerHeadicons.length) {
					method136(((Mob) (obj)), false, ((Mob) (obj)).height + 15);
					if(anInt932 > -1)
						prayerHeadicons[class37_1.headIcon].method461(anInt933 - 30, anInt932 - 12, -488);
				}
				if(anInt1197 == 1 && anInt1226 == sessionNpcList[i - anInt971] && currentTime % 20 < 10) {
					method136(((Mob) (obj)), false, ((Mob) (obj)).height + 15);
					if(anInt932 > -1)
						hintHeadicons[0].method461(anInt933 - 28, anInt932 - 12, -488);
				}
			}
			if(((Mob) (obj)).aString1580 != null && (i >= anInt971 || anInt1006 == 0 || anInt1006 == 3 || anInt1006 == 1 && method148(13292, ((Player) obj).username))) {
				method136(((Mob) (obj)), false, ((Mob) (obj)).height);
				if(anInt932 > -1 && anInt939 < anInt940) {
					anIntArray944[anInt939] = boldFont.getStringWidth(((Mob) (obj)).aString1580) / 2;
					anIntArray943[anInt939] = boldFont.charHeight;
					anIntArray941[anInt939] = anInt932;
					anIntArray942[anInt939] = anInt933;
					anIntArray945[anInt939] = ((Mob) (obj)).anInt1583;
					anIntArray946[anInt939] = ((Mob) (obj)).anInt1593;
					anIntArray947[anInt939] = ((Mob) (obj)).anInt1582;
					aStringArray948[anInt939++] = ((Mob) (obj)).aString1580;
					if(anInt998 == 0 && ((Mob) (obj)).anInt1593 >= 1 && ((Mob) (obj)).anInt1593 <= 3) {
						anIntArray943[anInt939] += 10;
						anIntArray942[anInt939] += 5;
					}
					if(anInt998 == 0 && ((Mob) (obj)).anInt1593 == 4)
						anIntArray944[anInt939] = 60;
					if(anInt998 == 0 && ((Mob) (obj)).anInt1593 == 5)
						anIntArray943[anInt939] += 5;
				}
			}
			if(((Mob) (obj)).anInt1595 > currentTime) {
				method136(((Mob) (obj)), false, ((Mob) (obj)).height + 15);
				if(anInt932 > -1) {
					int l = (((Mob) (obj)).anInt1596 * 30) / ((Mob) (obj)).anInt1597;
					if(l > 30)
						l = 30;
					DrawingArea.fillRect(5, anInt933 - 3, 65280, (byte) -24, l, anInt932 - 15);
					DrawingArea.fillRect(5, anInt933 - 3, 0xff0000, (byte) -24, 30 - l, (anInt932 - 15) + l);
				}
			}
			for(int i1 = 0; i1 < 4; i1++)
				if(((Mob) (obj)).anIntArray1632[i1] > currentTime) {
					method136(((Mob) (obj)), false, ((Mob) (obj)).height / 2);
					if(anInt932 > -1) {
						if(i1 == 1)
							anInt933 -= 20;
						if(i1 == 2) {
							anInt932 -= 15;
							anInt933 -= 10;
						}
						if(i1 == 3) {
							anInt932 += 15;
							anInt933 -= 10;
						}
						hitmarks[((Mob) (obj)).anIntArray1631[i1]].method461(anInt933 - 12, anInt932 - 12, -488);
						smallFont.method470(anInt932, 452, anInt933 + 4, 0, String.valueOf(((Mob) (obj)).anIntArray1630[i1]));
						smallFont.method470(anInt932 - 1, 452, anInt933 + 3, 0xffffff, String.valueOf(((Mob) (obj)).anIntArray1630[i1]));
					}
				}

		}

		for(int j = 0; j < anInt939; j++) {
			int j1 = anIntArray941[j];
			int k1 = anIntArray942[j];
			int l1 = anIntArray944[j];
			int i2 = anIntArray943[j];
			boolean flag1 = true;
			while(flag1) {
				flag1 = false;
				for(int j2 = 0; j2 < j; j2++)
					if(k1 + 2 > anIntArray942[j2] - anIntArray943[j2] && k1 - i2 < anIntArray942[j2] + 2 && j1 - l1 < anIntArray941[j2] + anIntArray944[j2]
							&& j1 + l1 > anIntArray941[j2] - anIntArray944[j2] && anIntArray942[j2] - anIntArray943[j2] < k1) {
						k1 = anIntArray942[j2] - anIntArray943[j2];
						flag1 = true;
					}

			}
			anInt932 = anIntArray941[j];
			anInt933 = anIntArray942[j] = k1;
			String s = aStringArray948[j];
			if(anInt998 == 0) {
				int k2 = 0xffff00;
				if(anIntArray945[j] < 6)
					k2 = anIntArray842[anIntArray945[j]];
				if(anIntArray945[j] == 6)
					k2 = anInt1138 % 20 >= 10 ? 0xffff00 : 0xff0000;
				if(anIntArray945[j] == 7)
					k2 = anInt1138 % 20 >= 10 ? 65535 : 255;
				if(anIntArray945[j] == 8)
					k2 = anInt1138 % 20 >= 10 ? 0x80ff80 : 45056;
				if(anIntArray945[j] == 9) {
					int l2 = 150 - anIntArray947[j];
					if(l2 < 50)
						k2 = 0xff0000 + 1280 * l2;
					else if(l2 < 100)
						k2 = 0xffff00 - 0x50000 * (l2 - 50);
					else if(l2 < 150)
						k2 = 65280 + 5 * (l2 - 100);
				}
				if(anIntArray945[j] == 10) {
					int i3 = 150 - anIntArray947[j];
					if(i3 < 50)
						k2 = 0xff0000 + 5 * i3;
					else if(i3 < 100)
						k2 = 0xff00ff - 0x50000 * (i3 - 50);
					else if(i3 < 150)
						k2 = (255 + 0x50000 * (i3 - 100)) - 5 * (i3 - 100);
				}
				if(anIntArray945[j] == 11) {
					int j3 = 150 - anIntArray947[j];
					if(j3 < 50)
						k2 = 0xffffff - 0x50005 * j3;
					else if(j3 < 100)
						k2 = 65280 + 0x50005 * (j3 - 50);
					else if(j3 < 150)
						k2 = 0xffffff - 0x50000 * (j3 - 100);
				}
				if(anIntArray946[j] == 0) {
					boldFont.method470(anInt932, 452, anInt933 + 1, 0, s);
					boldFont.method470(anInt932, 452, anInt933, k2, s);
				}
				if(anIntArray946[j] == 1) {
					boldFont.method475(anInt933 + 1, (byte) 4, anInt1138, s, anInt932, 0);
					boldFont.method475(anInt933, (byte) 4, anInt1138, s, anInt932, k2);
				}
				if(anIntArray946[j] == 2) {
					boldFont.method476(anInt933 + 1, 0, (byte) 1, s, anInt932, anInt1138);
					boldFont.method476(anInt933, k2, (byte) 1, s, anInt932, anInt1138);
				}
				if(anIntArray946[j] == 3) {
					boldFont.method477(-601, s, 0, anInt932, anInt933 + 1, 150 - anIntArray947[j], anInt1138);
					boldFont.method477(-601, s, k2, anInt932, anInt933, 150 - anIntArray947[j], anInt1138);
				}
				if(anIntArray946[j] == 4) {
					int k3 = boldFont.getStringWidth(s);
					int i4 = ((150 - anIntArray947[j]) * (k3 + 100)) / 150;
					DrawingArea.method446(0, anInt932 - 50, 334, anInt932 + 50, true);
					boldFont.method474(s, (anInt932 + 50) - i4, anInt933 + 1, 0);
					boldFont.method474(s, (anInt932 + 50) - i4, anInt933, k2);
					DrawingArea.method445((byte) 82);
				}
				if(anIntArray946[j] == 5) {
					int l3 = 150 - anIntArray947[j];
					int j4 = 0;
					if(l3 < 25)
						j4 = l3 - 25;
					else if(l3 > 125)
						j4 = l3 - 125;
					DrawingArea.method446(anInt933 - boldFont.charHeight - 1, 0, anInt933 + 5, 512, true);
					boldFont.method470(anInt932, 452, anInt933 + 1 + j4, 0, s);
					boldFont.method470(anInt932, 452, anInt933 + j4, k2, s);
					DrawingArea.method445((byte) 82);
				}
			} else {
				boldFont.method470(anInt932, 452, anInt933 + 1, 0, s);
				boldFont.method470(anInt932, 452, anInt933, 0xffff00, s);
			}
		}

		if(flag)
			packetOpcode = -1;
	}

	public void method122(int i) {
		while(i >= 0)
			aBoolean1242 = !aBoolean1242;
		if(aClass18_1159 != null) {
			return;
		} else {
			method141(28614);
			super.aClass18_15 = null;
			aClass18_1198 = null;
			aClass18_1199 = null;
			aClass18_1200 = null;
			aClass18_1201 = null;
			aClass18_1202 = null;
			aClass18_1203 = null;
			aClass18_1204 = null;
			aClass18_1205 = null;
			aClass18_1206 = null;
			aClass18_1159 = new GraphicsBuffer(479, 96, getGameComponent());
			//mapDrawingArea = new GraphicsBuffer(172, 156, getGameComponent());
			mapDrawingArea = new GraphicsBuffer(206, 156, getGameComponent());
			DrawingArea.clear();
			mapback.drawImage(0, 0);
			tabBackDrawingArea = new GraphicsBuffer(190, 261, getGameComponent());
			
			if(super.isResized) {
				gameScreenDrawingArea = new GraphicsBuffer(super.width, super.height, getGameComponent());
			} else {
				gameScreenDrawingArea = new GraphicsBuffer(512, 334, getGameComponent());
			}
			
			DrawingArea.clear();
			aClass18_1108 = new GraphicsBuffer(496, 50, getGameComponent());
			aClass18_1109 = new GraphicsBuffer(269, 37, getGameComponent());
			aClass18_1110 = new GraphicsBuffer(249, 45, getGameComponent());
			repaintRequested = true;
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
			return;
		}
	}

	public void showErrorScreen() {
		Graphics g = getGameComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		method4((byte) 103, 1);
		if(loadingError) {
			aBoolean1243 = false;
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int j = 35;
			g.drawString("Sorry, an error has occured whilst loading ManaScape", 30, j);
			j += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, j);
			j += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, j);
			j += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, j);
			j += 30;
			g.drawString("3: Try using a different game-world", 30, j);
			j += 30;
			g.drawString("4: Try rebooting your computer", 30, j);
			j += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, j);
		}
		if(invalidHost) {
			aBoolean1243 = false;
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play ManaScape make sure you play from", 50, 100);
			g.drawString("http://www.manascape.org", 50, 150);
		}
		if(gameAlreadyLoaded) {
			aBoolean1243 = false;
			g.setColor(Color.yellow);
			int k = 35;
			g.drawString("Error a copy of ManaScape already appears to be loaded", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
			k += 30;
			g.drawString("2: Try rebooting your computer, and reloading", 30, k);
			k += 30;
		}
	}

	public void method124(boolean flag) {
		try {
			if(aClass17_1024 != null)
				aClass17_1024.method224();
		} catch(Exception _ex) {
		}
		aClass17_1024 = null;
		loggedIn = false;
		anInt1225 = 0;
		// aString1092 = "";
		// aString1093 = "";
		method49(383);
		loggedIn &= flag;
		aClass22_1164.method241((byte) 7);
		for(int i = 0; i < 4; i++)
			aClass46Array1260[i].method411();

		System.gc();
		method50(false);
		anInt1327 = -1;
		anInt1270 = -1;
		anInt1128 = 0;
	}

	public void method125(int i, String s, String s1) {
		while(i >= 0)
			return;
		if(gameScreenDrawingArea != null) {
			gameScreenDrawingArea.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1002;
			int j = 151;
			if(s != null)
				j -= 7;
			normalFont.method470(257, 452, j, 0, s1);
			normalFont.method470(256, 452, j - 1, 0xffffff, s1);
			j += 15;
			if(s != null) {
				normalFont.method470(257, 452, j, 0, s);
				normalFont.method470(256, 452, j - 1, 0xffffff, s);
			}
			gameScreenDrawingArea.drawGraphics(4, 4, super.graphics);
			return;
		}
		if(super.aClass18_15 != null) {
			super.aClass18_15.initDrawingArea();
			Rasterizer.lineOffsets = anIntArray1003;
			int k = 251;
			char c = '\u012C';
			byte byte0 = 50;
			DrawingArea.fillRect(byte0, k - 5 - byte0 / 2, 0, (byte) -24, c, 383 - c / 2);
			DrawingArea.method450(0, k - 5 - byte0 / 2, byte0, 0xffffff, 383 - c / 2, c);
			if(s != null)
				k -= 7;
			normalFont.method470(383, 452, k, 0, s1);
			normalFont.method470(382, 452, k - 1, 0xffffff, s1);
			k += 15;
			if(s != null) {
				normalFont.method470(383, 452, k, 0, s);
				normalFont.method470(382, 452, k - 1, 0xffffff, s);
			}
			super.aClass18_15.drawGraphics(0, 0, super.graphics);
		}
	}

	public boolean method126(int i, byte byte0) {
		if(i < 0)
			return false;
		int j = menuActionId[i];
		if(byte0 != 97)
			throw new NullPointerException();
		if(j >= 2000)
			j -= 2000;
		return j == 762;
	}

	public void method127(boolean flag) {
		if(!flag)
			anInt1056 = aISAAC_899.getNextValue();
		if(anInt1197 != 2)
			return;
		method137((anInt844 - anInt1040 << 7) + anInt847, anInt846 * 2, (anInt845 - anInt1041 << 7) + anInt848, -214);
		if(anInt932 > -1 && currentTime % 20 < 10)
			hintHeadicons[0].method461(anInt933 - 28, anInt932 - 12, -488);
	}

	public void drawGame() {
		if(gameAlreadyLoaded || loadingError || invalidHost) {
			showErrorScreen();
			return;
		}
		
		drawCycle++;
		
		if(!loggedIn)
			drawLoginScreen(false);
		else
			drawGameScreen();
		
		anInt1094 = 0;
	}

	public void drawMenu() {
		int i = anInt1305;
		int j = anInt1306;
		int k = anInt1307;
		int l = anInt1308;
		int i1 = 0x5d5447;
		DrawingArea.fillRect(l, j, i1, (byte) -24, k, i);
		DrawingArea.fillRect(16, j + 1, 0, (byte) -24, k - 2, i + 1);
		DrawingArea.method450(0, j + 18, l - 19, 0, i + 1, k - 2);
		boldFont.method474("Choose Option", i + 3, j + 14, i1);
		int j1 = super.mouseX;
		int k1 = super.mouseY;
		if(menuScreenArea == 0) {
			j1 -= 4;
			k1 -= 4;
		}
		if(menuScreenArea == 1) {
			j1 -= 553;
			k1 -= 205;
		}
		if(menuScreenArea == 2) {
			j1 -= 17;
			k1 -= 357;
		}
		for(int l1 = 0; l1 < menuActionIndex; l1++) {
			int i2 = j + 31 + (menuActionIndex - 1 - l1) * 15;
			int j2 = 0xffffff;
			if(j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3)
				j2 = 0xffff00;
			boldFont.method478(menuActionName[l1], i + 3, i2, j2, true);
		}

	}

	public int method129(int i, int j, GameInterface class13) {
		if(i != 3)
			return anInt1222;
		if(class13.valueIndexArray == null || j >= class13.valueIndexArray.length)
			return -2;
		try {
			int ai[] = class13.valueIndexArray[j];
			int k = 0;
			int l = 0;
			int i1 = 0;
			do {
				int j1 = ai[l++];
				int k1 = 0;
				byte byte0 = 0;
				if(j1 == 0)
					return k;
				if(j1 == 1)
					k1 = playerLevels[ai[l++]];
				if(j1 == 2)
					k1 = anIntArray1054[ai[l++]];
				if(j1 == 3)
					k1 = playerExps[ai[l++]];
				if(j1 == 4) {
					GameInterface class13_1 = GameInterface.getInterface(ai[l++]);
					int k2 = ai[l++];
					if(k2 >= 0 && k2 < ItemDefinition.itemCount && (!ItemDefinition.forId(k2).members || aBoolean925)) {
						for(int j3 = 0; j3 < class13_1.anIntArray269.length; j3++)
							if(class13_1.anIntArray269[j3] == k2 + 1)
								k1 += class13_1.anIntArray224[j3];

					}
				}
				if(j1 == 5)
					k1 = anIntArray1039[ai[l++]];
				if(j1 == 6)
					k1 = anIntArray952[anIntArray1054[ai[l++]] - 1];
				if(j1 == 7)
					k1 = (anIntArray1039[ai[l++]] * 100) / 46875;
				if(j1 == 8)
					k1 = sessionPlayer.combatLevel;
				if(j1 == 9) {
					for(int l1 = 0; l1 < SkillConstants.SKILL_COUNT; l1++)
						if(SkillConstants.skillSlotInUse[l1])
							k1 += anIntArray1054[l1];

				}
				if(j1 == 10) {
					GameInterface class13_2 = GameInterface.getInterface(ai[l++]);
					int l2 = ai[l++] + 1;
					if(l2 >= 0 && l2 < ItemDefinition.itemCount && (!ItemDefinition.forId(l2).members || aBoolean925)) {
						for(int k3 = 0; k3 < class13_2.anIntArray269.length; k3++) {
							if(class13_2.anIntArray269[k3] != l2)
								continue;
							k1 = 0x3b9ac9ff;
							break;
						}

					}
				}
				if(j1 == 11)
					k1 = playerRunEnergy;
				if(j1 == 12)
					k1 = anInt1030;
				if(j1 == 13) {
					int i2 = anIntArray1039[ai[l++]];
					int i3 = ai[l++];
					k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if(j1 == 14) {
					int j2 = ai[l++];
					Class49 class49 = Class49.aClass49Array824[j2];
					int l3 = class49.anInt826;
					int i4 = class49.anInt827;
					int j4 = class49.anInt828;
					int k4 = anIntArray1214[j4 - i4];
					k1 = anIntArray1039[l3] >> i4 & k4;
				}
				if(j1 == 15)
					byte0 = 1;
				if(j1 == 16)
					byte0 = 2;
				if(j1 == 17)
					byte0 = 3;
				if(j1 == 18)
					k1 = (((Mob) (sessionPlayer)).anInt1610 >> 7) + anInt1040;
				if(j1 == 19)
					k1 = (((Mob) (sessionPlayer)).anInt1611 >> 7) + anInt1041;
				if(j1 == 20)
					k1 = ai[l++];
				if(byte0 == 0) {
					if(i1 == 0)
						k += k1;
					if(i1 == 1)
						k -= k1;
					if(i1 == 2 && k1 != 0)
						k /= k1;
					if(i1 == 3)
						k *= k1;
					i1 = 0;
				} else {
					i1 = byte0;
				}
			} while(true);
		} catch(Exception _ex) {
			return -1;
		}
	}

	public void markMinimap(RgbImage image, int j, int i) {
		if(image == null)
			return;
		int k = cameraX + minimapRotation & 0x7ff;
		int l = j * j + i * i;
		if(l > 6400)
			return;
		int i1 = Model.anIntArray1710[k];
		int j1 = Model.anIntArray1711[k];
		i1 = (i1 * 256) / (minimapZoom + 256);
		j1 = (j1 * 256) / (minimapZoom + 256);
		int k1 = i * i1 + j * j1 >> 16;
		int l1 = i * j1 - j * i1 >> 16;
					
		//94x83 = CENTER POINT
					
		if(l > 2500) {
			image.method467(mapback, 83 - l1 - image.libHeight / 2 - 4, -49993, ((/*94*/128 + k1) - image.libWidth / 2) + 4);
			return;
		} else {
			image.method461(83 - l1 - image.libHeight / 2 - 4, ((/*94*/128 + k1) - image.libWidth / 2) + 4, -488);
			return;
		}
	}

	public void drawLoginScreen(boolean flag) {
		method64(-188);
		aClass18_1200.initDrawingArea();
		aClass50_Sub1_Sub1_Sub3_1292.drawImage(0, 0);
		char c = '\u0168';
		char c1 = '\310';
		if(anInt1225 == 0) {
			int j = c1 / 2 + 80;
			smallFont.drawCenteredText(true, 0x75a9a9, c / 2, j, aClass32_Sub1_1291.aString1347);
			j = c1 / 2 - 20;
			boldFont.drawCenteredText(true, 0xffff00, c / 2, j, "Welcome to ManaScape");
			j += 30;
			int i1 = c / 2 - 80;
			int l1 = c1 / 2 + 20;
			aClass50_Sub1_Sub1_Sub3_1293.drawImage(i1 - 73, l1 - 20);
			boldFont.drawCenteredText(true, 0xffffff, i1, l1 + 5, "New User");
			i1 = c / 2 + 80;
			aClass50_Sub1_Sub1_Sub3_1293.drawImage(i1 - 73, l1 - 20);
			boldFont.drawCenteredText(true, 0xffffff, i1, l1 + 5, "Existing User");
		}
		if(anInt1225 == 2) {
			int k = c1 / 2 - 40;
			if(aString957.length() > 0) {
				boldFont.drawCenteredText(true, 0xffff00, c / 2, k - 15, aString957);
				boldFont.drawCenteredText(true, 0xffff00, c / 2, k, aString958);
				k += 30;
			} else {
				boldFont.drawCenteredText(true, 0xffff00, c / 2, k - 7, aString958);
				k += 30;
			}
			boldFont.method478("Username: " + aString1092 + ((anInt977 == 0) & (currentTime % 40 < 20) ? "@yel@|" : ""), c / 2 - 90, k, 0xffffff, true);
			k += 15;
			boldFont.method478("Password: " + NameUtils.method304(2934, aString1093) + ((anInt977 == 1) & (currentTime % 40 < 20) ? "@yel@|" : ""), c / 2 - 88, k, 0xffffff, true);
			k += 15;
			if(!flag) {
				int j1 = c / 2 - 80;
				int i2 = c1 / 2 + 50;
				aClass50_Sub1_Sub1_Sub3_1293.drawImage(j1 - 73, i2 - 20);
				boldFont.drawCenteredText(true, 0xffffff, j1, i2 + 5, "Login");
				j1 = c / 2 + 80;
				aClass50_Sub1_Sub1_Sub3_1293.drawImage(j1 - 73, i2 - 20);
				boldFont.drawCenteredText(true, 0xffffff, j1, i2 + 5, "Cancel");
			}
		}
		if(anInt1225 == 3) {
			boldFont.drawCenteredText(true, 0xffff00, c / 2, c1 / 2 - 60, "Create a free account");
			int l = c1 / 2 - 35;
			boldFont.drawCenteredText(true, 0xffffff, c / 2, l, "To create a new account you need to");
			l += 15;
			boldFont.drawCenteredText(true, 0xffffff, c / 2, l, "go back to the main ManaScape webpage");
			l += 15;
			boldFont.drawCenteredText(true, 0xffffff, c / 2, l, "and choose the 'create account'");
			l += 15;
			boldFont.drawCenteredText(true, 0xffffff, c / 2, l, "button near the top of that page.");
			l += 15;
			int k1 = c / 2;
			int j2 = c1 / 2 + 50;
			aClass50_Sub1_Sub1_Sub3_1293.drawImage(k1 - 73, j2 - 20);
			boldFont.drawCenteredText(true, 0xffffff, k1, j2 + 5, "Cancel");
		}
		aClass18_1200.drawGraphics(202, 171, super.graphics);
		if(repaintRequested) {
			repaintRequested = false;
			aClass18_1198.drawGraphics(128, 0, super.graphics);
			aClass18_1199.drawGraphics(202, 371, super.graphics);
			aClass18_1203.drawGraphics(0, 265, super.graphics);
			aClass18_1204.drawGraphics(562, 265, super.graphics);
			aClass18_1205.drawGraphics(128, 171, super.graphics);
			aClass18_1206.drawGraphics(562, 171, super.graphics);
		}
	}

	public void method132(ByteBuffer byteStream, int i, boolean flag) {
		if(flag)
			anInt1140 = 287;
		while(byteStream.bitPosition + 21 < i * 8) {
			int j = byteStream.method532(402, 14);
			if(j == 16383)
				break;
			if(sessionNpcs[j] == null)
				sessionNpcs[j] = new NPC();
			NPC npc = sessionNpcs[j];
			sessionNpcList[sessionNpcCount++] = j;
			npc.anInt1585 = currentTime;
			int k = byteStream.method532(402, 1);
			if(k == 1)
				anIntArray974[sessionNpcsAwaitingUpdate++] = j;
			int l = byteStream.method532(402, 5);
			if(l > 15)
				l -= 32;
			int i1 = byteStream.method532(402, 5);
			if(i1 > 15)
				i1 -= 32;
			int j1 = byteStream.method532(402, 1);
			npc.npcDef = NPCDefinition.forId(byteStream.method532(402, 13));
			npc.anInt1601 = npc.npcDef.boundDim;
			npc.anInt1600 = npc.npcDef.degreesToTurn;
			// NPC ANIM STUFF
			if(npc.useNpcDef) {
				npc.walkAnim = npc.npcDef.walkAnim;
				npc.turn180Anim = npc.npcDef.turn180Anim;
				npc.turn90CWAnim = npc.npcDef.turn90CWAnim;
				npc.turn90CCWAnim = npc.npcDef.turn90CCWAnim;
				npc.standAnim = npc.npcDef.idleAnim;
			}
			npc.method568(((Mob) (sessionPlayer)).anIntArray1587[0] + l, (byte) 5, j1 == 1, ((Mob) (sessionPlayer)).anIntArray1586[0] + i1);
		}
		byteStream.finishBitAccess();
	}

	public void method133(ByteBuffer byteStream, int i, int opcode) {
		if(i != 0)
			aClass6ArrayArrayArray1323 = null;
		if(opcode == 203) {
			int k = byteStream.getUnsignedShort();
			int j3 = byteStream.getUnsignedByte();
			int i6 = j3 >> 2;
			int l8 = j3 & 3;
			int k11 = anIntArray1032[i6];
			byte byte0 = byteStream.method544(0);
			int i16 = byteStream.method540(0);
			int k17 = anInt989 + (i16 >> 4 & 7);
			int k18 = anInt990 + (i16 & 7);
			byte byte1 = byteStream.method543(anInt1087);
			int l19 = byteStream.getUnsignedShortA();
			int k20 = byteStream.getUnsignedLEShort();
			byte byte2 = byteStream.getByte();
			byte byte3 = byteStream.method543(anInt1087);
			int l21 = byteStream.getUnsignedShort();
			Player class50_sub1_sub4_sub3_sub2;
			if(k20 == anInt961)
				class50_sub1_sub4_sub3_sub2 = sessionPlayer;
			else
				class50_sub1_sub4_sub3_sub2 = sessionPlayers[k20];
			if(class50_sub1_sub4_sub3_sub2 != null) {
				Class47 class47 = Class47.method423(k);
				int i22 = anIntArrayArrayArray891[anInt1091][k17][k18];
				int j22 = anIntArrayArrayArray891[anInt1091][k17 + 1][k18];
				int k22 = anIntArrayArrayArray891[anInt1091][k17 + 1][k18 + 1];
				int l22 = anIntArrayArrayArray891[anInt1091][k17][k18 + 1];
				Model class50_sub1_sub4_sub4 = class47.method431(i6, l8, i22, j22, k22, l22, -1);
				if(class50_sub1_sub4_sub4 != null) {
					method145(true, anInt1091, k17, 0, l19 + 1, 0, -1, l21 + 1, k11, k18);
					class50_sub1_sub4_sub3_sub2.anInt1764 = l21 + currentTime;
					class50_sub1_sub4_sub3_sub2.anInt1765 = l19 + currentTime;
					class50_sub1_sub4_sub3_sub2.unknownModel = class50_sub1_sub4_sub4;
					int i23 = class47.anInt801;
					int j23 = class47.anInt775;
					if(l8 == 1 || l8 == 3) {
						i23 = class47.anInt775;
						j23 = class47.anInt801;
					}
					class50_sub1_sub4_sub3_sub2.anInt1743 = k17 * 128 + i23 * 64;
					class50_sub1_sub4_sub3_sub2.anInt1745 = k18 * 128 + j23 * 64;
					class50_sub1_sub4_sub3_sub2.anInt1744 = method110(class50_sub1_sub4_sub3_sub2.anInt1745, class50_sub1_sub4_sub3_sub2.anInt1743, (byte) 9, anInt1091);
					if(byte1 > byte0) {
						byte byte4 = byte1;
						byte1 = byte0;
						byte0 = byte4;
					}
					if(byte3 > byte2) {
						byte byte5 = byte3;
						byte3 = byte2;
						byte2 = byte5;
					}
					class50_sub1_sub4_sub3_sub2.anInt1768 = k17 + byte1;
					class50_sub1_sub4_sub3_sub2.anInt1770 = k17 + byte0;
					class50_sub1_sub4_sub3_sub2.anInt1769 = k18 + byte3;
					class50_sub1_sub4_sub3_sub2.anInt1771 = k18 + byte2;
				}
			}
		}
		if(opcode == 106) {
			int l = byteStream.method540(0);
			int k3 = anInt989 + (l >> 4 & 7);
			int j6 = anInt990 + (l & 7);
			int i9 = byteStream.getUnsignedLEShortA();
			int l11 = byteStream.getUnsignedShortA();
			int i14 = byteStream.getUnsignedShortA();
			if(k3 >= 0 && j6 >= 0 && k3 < 104 && j6 < 104 && i14 != anInt961) {
				Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1_2 = new Class50_Sub1_Sub4_Sub1();
				class50_sub1_sub4_sub1_2.anInt1550 = l11;
				class50_sub1_sub4_sub1_2.anInt1552 = i9;
				if(aClass6ArrayArrayArray1323[anInt1091][k3][j6] == null)
					aClass6ArrayArrayArray1323[anInt1091][k3][j6] = new NodeList(true);
				aClass6ArrayArrayArray1323[anInt1091][k3][j6].method155(class50_sub1_sub4_sub1_2);
				method26(k3, j6);
			}
			return;
		}
		if(opcode == 142) {
			int i1 = byteStream.getUnsignedShort();
			int l3 = byteStream.method540(0);
			int k6 = l3 >> 2;
			int j9 = l3 & 3;
			int i12 = anIntArray1032[k6];
			int j14 = byteStream.getUnsignedByte();
			int j16 = anInt989 + (j14 >> 4 & 7);
			int l17 = anInt990 + (j14 & 7);
			if(j16 >= 0 && l17 >= 0 && j16 < 103 && l17 < 103) {
				int l18 = anIntArrayArrayArray891[anInt1091][j16][l17];
				int j19 = anIntArrayArrayArray891[anInt1091][j16 + 1][l17];
				int i20 = anIntArrayArrayArray891[anInt1091][j16 + 1][l17 + 1];
				int l20 = anIntArrayArrayArray891[anInt1091][j16][l17 + 1];
				if(i12 == 0) {
					Class44 class44 = aClass22_1164.method263(anInt1091, 17734, j16, l17);
					if(class44 != null) {
						int k21 = class44.anInt726 >> 14 & 0x7fff;
						if(k6 == 2) {
							class44.aClass50_Sub1_Sub4_724 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, 2, (byte) 3, k21, false, l18, 4 + j9);
							class44.aClass50_Sub1_Sub4_725 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, 2, (byte) 3, k21, false, l18, j9 + 1 & 3);
						} else {
							class44.aClass50_Sub1_Sub4_724 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, k6, (byte) 3, k21, false, l18, j9);
						}
					}
				}
				if(i12 == 1) {
					Class35 class35 = aClass22_1164.method264(anInt1091, l17, j16, false);
					if(class35 != null)
						class35.aClass50_Sub1_Sub4_608 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, 4, (byte) 3, class35.anInt609 >> 14 & 0x7fff, false, l18, 0);
				}
				if(i12 == 2) {
					Class5 class5 = aClass22_1164.method265(j16, (byte) 32, l17, anInt1091);
					if(k6 == 11)
						k6 = 10;
					if(class5 != null)
						class5.aClass50_Sub1_Sub4_117 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, k6, (byte) 3, class5.anInt125 >> 14 & 0x7fff, false, l18, j9);
				}
				if(i12 == 3) {
					Class28 class28 = aClass22_1164.method266(anInt1091, l17, 0, j16);
					if(class28 != null)
						class28.aClass50_Sub1_Sub4_570 = new Class50_Sub1_Sub4_Sub5(i1, i20, l20, j19, 22, (byte) 3, class28.anInt571 >> 14 & 0x7fff, false, l18, j9);
				}
			}
			return;
		}
		if(opcode == 107) {
			int j1 = byteStream.getUnsignedShort();
			int i4 = byteStream.method541(-34545);
			int l6 = anInt989 + (i4 >> 4 & 7);
			int k9 = anInt990 + (i4 & 7);
			int j12 = byteStream.getUnsignedShortA();
			if(l6 >= 0 && k9 >= 0 && l6 < 104 && k9 < 104) {
				Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1 = new Class50_Sub1_Sub4_Sub1();
				class50_sub1_sub4_sub1.anInt1550 = j1;
				class50_sub1_sub4_sub1.anInt1552 = j12;
				if(aClass6ArrayArrayArray1323[anInt1091][l6][k9] == null)
					aClass6ArrayArrayArray1323[anInt1091][l6][k9] = new NodeList(true);
				aClass6ArrayArrayArray1323[anInt1091][l6][k9].method155(class50_sub1_sub4_sub1);
				method26(l6, k9);
			}
			return;
		}
		if(opcode == 121) {
			int k1 = byteStream.getUnsignedByte();
			int j4 = anInt989 + (k1 >> 4 & 7);
			int i7 = anInt990 + (k1 & 7);
			int l9 = byteStream.getUnsignedShort();
			int k12 = byteStream.getUnsignedShort();
			int k14 = byteStream.getUnsignedShort();
			if(j4 >= 0 && i7 >= 0 && j4 < 104 && i7 < 104) {
				NodeList class6_1 = aClass6ArrayArrayArray1323[anInt1091][j4][i7];
				if(class6_1 != null) {
					for(Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1_3 = (Class50_Sub1_Sub4_Sub1) class6_1.method158(); class50_sub1_sub4_sub1_3 != null; class50_sub1_sub4_sub1_3 = (Class50_Sub1_Sub4_Sub1) class6_1
							.method160(1)) {
						if(class50_sub1_sub4_sub1_3.anInt1550 != (l9 & 0x7fff) || class50_sub1_sub4_sub1_3.anInt1552 != k12)
							continue;
						class50_sub1_sub4_sub1_3.anInt1552 = k14;
						break;
					}

					method26(j4, i7);
				}
			}
			return;
		}
		if(opcode == 181) {
			int l1 = byteStream.getUnsignedByte();
			int k4 = anInt989 + (l1 >> 4 & 7);
			int j7 = anInt990 + (l1 & 7);
			int i10 = k4 + byteStream.getByte();
			int l12 = j7 + byteStream.getByte();
			int l14 = byteStream.getShort();
			int k16 = byteStream.getUnsignedShort();
			int i18 = byteStream.getUnsignedByte() * 4;
			int i19 = byteStream.getUnsignedByte() * 4;
			int k19 = byteStream.getUnsignedShort();
			int j20 = byteStream.getUnsignedShort();
			int i21 = byteStream.getUnsignedByte();
			int j21 = byteStream.getUnsignedByte();
			if(k4 >= 0 && j7 >= 0 && k4 < 104 && j7 < 104 && i10 >= 0 && l12 >= 0 && i10 < 104 && l12 < 104 && k16 != 65535) {
				k4 = k4 * 128 + 64;
				j7 = j7 * 128 + 64;
				i10 = i10 * 128 + 64;
				l12 = l12 * 128 + 64;
				Class50_Sub1_Sub4_Sub2 class50_sub1_sub4_sub2 = new Class50_Sub1_Sub4_Sub2(anInt1091, i19, j21, j7, k16, j20 + currentTime, i21, l14, (byte) -41, method110(j7, k4, (byte) 9, anInt1091)
						- i18, k4, k19 + currentTime);
				class50_sub1_sub4_sub2.method562(i10, l12, method110(l12, i10, (byte) 9, anInt1091) - i19, k19 + currentTime, 0);
				aClass6_1282.method155(class50_sub1_sub4_sub2);
			}
			return;
		}
		if(opcode == 41) {
			int i2 = byteStream.getUnsignedByte();
			int l4 = anInt989 + (i2 >> 4 & 7);
			int k7 = anInt990 + (i2 & 7);
			int j10 = byteStream.getUnsignedShort();
			int i13 = byteStream.getUnsignedByte();
			int i15 = i13 >> 4 & 0xf;
			int l16 = i13 & 7;
			if(((Mob) (sessionPlayer)).anIntArray1586[0] >= l4 - i15 && ((Mob) (sessionPlayer)).anIntArray1586[0] <= l4 + i15 && ((Mob) (sessionPlayer)).anIntArray1587[0] >= k7 - i15
					&& ((Mob) (sessionPlayer)).anIntArray1587[0] <= k7 + i15 && aBoolean1301 && !aBoolean926 && anInt1035 < 50) {
				anIntArray1090[anInt1035] = j10;
				anIntArray1321[anInt1035] = l16;
				anIntArray1259[anInt1035] = Class38.anIntArray669[j10];
				anInt1035++;
			}
		}
		if(opcode == 59) {
			int j2 = byteStream.getUnsignedByte();
			int i5 = anInt989 + (j2 >> 4 & 7);
			int l7 = anInt990 + (j2 & 7);
			int k10 = byteStream.getUnsignedShort();
			int j13 = byteStream.getUnsignedByte();
			int j15 = byteStream.getUnsignedShort();
			if(i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104) {
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				Class50_Sub1_Sub4_Sub6 class50_sub1_sub4_sub6 = new Class50_Sub1_Sub4_Sub6(i5, anInt1091, method110(l7, i5, (byte) 9, anInt1091) - j13, j15, k10, currentTime, l7, 10709);
				aClass6_1210.method155(class50_sub1_sub4_sub6);
			}
			return;
		}
		if(opcode == 152) {
			int k2 = byteStream.method541(-34545);
			int j5 = k2 >> 2;
			int i8 = k2 & 3;
			int l10 = anIntArray1032[j5];
			int k13 = byteStream.getUnsignedLEShortA();
			int k15 = byteStream.method540(0);
			int i17 = anInt989 + (k15 >> 4 & 7);
			int j18 = anInt990 + (k15 & 7);
			if(i17 >= 0 && j18 >= 0 && i17 < 104 && j18 < 104)
				method145(true, anInt1091, i17, i8, -1, j5, k13, 0, l10, j18);
			return;
		}
		if(opcode == 208) {
			int l2 = byteStream.getUnsignedShortA();
			int k5 = byteStream.method540(0);
			int j8 = anInt989 + (k5 >> 4 & 7);
			int i11 = anInt990 + (k5 & 7);
			if(j8 >= 0 && i11 >= 0 && j8 < 104 && i11 < 104) {
				NodeList class6 = aClass6ArrayArrayArray1323[anInt1091][j8][i11];
				if(class6 != null) {
					for(Class50_Sub1_Sub4_Sub1 class50_sub1_sub4_sub1_1 = (Class50_Sub1_Sub4_Sub1) class6.method158(); class50_sub1_sub4_sub1_1 != null; class50_sub1_sub4_sub1_1 = (Class50_Sub1_Sub4_Sub1) class6
							.method160(1)) {
						if(class50_sub1_sub4_sub1_1.anInt1550 != (l2 & 0x7fff))
							continue;
						class50_sub1_sub4_sub1_1.method442();
						break;
					}

					if(class6.method158() == null)
						aClass6ArrayArrayArray1323[anInt1091][j8][i11] = null;
					method26(j8, i11);
				}
			}
			return;
		}
		if(opcode == 88) {
			int i3 = byteStream.method542(anInt1236);
			int l5 = anInt989 + (i3 >> 4 & 7);
			int k8 = anInt990 + (i3 & 7);
			int j11 = byteStream.method542(anInt1236);
			int l13 = j11 >> 2;
			int l15 = j11 & 3;
			int j17 = anIntArray1032[l13];
			if(l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104)
				method145(true, anInt1091, l5, l15, -1, l13, -1, 0, j17, k8);
		}
	}

	public void drawTabInterfaceArea() {
		tabBackDrawingArea.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1001;
		invback.drawImage(0, 0);
		
		if(anInt1089 != -1)
			drawInterface(0, 0, 0, GameInterface.getInterface(anInt1089));
		else if(tabInterfaceIds[selectedTab] != -1)
			drawInterface(0, 0, 0, GameInterface.getInterface(tabInterfaceIds[selectedTab]));
		
		if(menuOpen && menuScreenArea == 1)
			drawMenu();
		
		tabBackDrawingArea.drawGraphics(553, 205, super.graphics);
		gameScreenDrawingArea.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1002;
	}

	public static String method135(int i, int j) {
		String s = String.valueOf(j);
		if(i != 0)
			throw new NullPointerException();
		for(int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);

		if(s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if(s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	public void method136(Mob class50_sub1_sub4_sub3, boolean flag, int i) {
		method137(class50_sub1_sub4_sub3.anInt1610, i, class50_sub1_sub4_sub3.anInt1611, -214);
		if(!flag)
			;
	}

	public void method137(int i, int j, int k, int l) {
		if(i < 128 || k < 128 || i > 13056 || k > 13056) {
			anInt932 = -1;
			anInt933 = -1;
			return;
		}
		int i1 = method110(k, i, (byte) 9, anInt1091) - j;
		i -= anInt1216;
		i1 -= anInt1217;
		k -= anInt1218;
		int j1 = Model.anIntArray1710[anInt1219];
		int k1 = Model.anIntArray1711[anInt1219];
		int l1 = Model.anIntArray1710[anInt1220];
		int i2 = Model.anIntArray1711[anInt1220];
		int j2 = k * l1 + i * i2 >> 16;
		k = k * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - k * j1 >> 16;
		k = i1 * j1 + k * k1 >> 16;
		while(l >= 0)
			packetOpcode = -1;
		i1 = j2;
		if(k >= 50) {
			anInt932 = Rasterizer.anInt1532 + (i << 9) / k;
			anInt933 = Rasterizer.anInt1533 + (i1 << 9) / k;
			return;
		} else {
			anInt932 = -1;
			anInt933 = -1;
			return;
		}
	}

	public void method138(boolean flag) {
		System.out.println("============");
		System.out.println("flame-cycle:" + anInt1101);
		if(aClass32_Sub1_1291 != null)
			System.out.println("Od-cycle:" + aClass32_Sub1_1291.anInt1348);
		System.out.println("loop-cycle:" + currentTime);
		System.out.println("draw-cycle:" + drawCycle);
		System.out.println("ptype:" + packetOpcode);
		System.out.println("psize:" + packetSize);
		if(flag)
			aBoolean1028 = !aBoolean1028;
		if(aClass17_1024 != null)
			aClass17_1024.method229(false);
		super.aBoolean11 = true;
	}

	public Component getGameComponent() {
		if(Signlink.mainapp != null)
			return Signlink.mainapp;
		if(super.gameFrame != null)
			return super.gameFrame;
		else
			return this;
	}

	public void drawLoadingBar(int i, String s) {
		anInt1322 = i;
		aString1027 = s;
		method64(-188);
		if(aClass2_888 == null) {
			super.drawLoadingBar(i, s);
			return;
		}
		aClass18_1200.initDrawingArea();
		char c = '\u0168';
		char c1 = '\310';
		byte byte0 = 20;
		boldFont.method470(c / 2, 452, c1 / 2 - 26 - byte0, 0xffffff, "ManaScape is loading - please wait...");
		int j = c1 / 2 - 18 - byte0;
		DrawingArea.method450(0, j, 34, 0x8c1111, c / 2 - 152, 304);
		DrawingArea.method450(0, j + 1, 32, 0, c / 2 - 151, 302);
		DrawingArea.fillRect(30, j + 2, 0x8c1111, (byte) -24, i * 3, c / 2 - 150);
		DrawingArea.fillRect(30, j + 2, 0, (byte) -24, 300 - i * 3, (c / 2 - 150) + i * 3);
		boldFont.method470(c / 2, 452, (c1 / 2 + 5) - byte0, 0xffffff, s);
		aClass18_1200.drawGraphics(202, 171, super.graphics);
		if(repaintRequested) {
			repaintRequested = false;
			if(!aBoolean1243) {
				aClass18_1201.drawGraphics(0, 0, super.graphics);
				aClass18_1202.drawGraphics(637, 0, super.graphics);
			}
			aClass18_1198.drawGraphics(128, 0, super.graphics);
			aClass18_1199.drawGraphics(202, 371, super.graphics);
			aClass18_1203.drawGraphics(0, 265, super.graphics);
			aClass18_1204.drawGraphics(562, 265, super.graphics);
			aClass18_1205.drawGraphics(128, 171, super.graphics);
			aClass18_1206.drawGraphics(562, 171, super.graphics);
		}
	}

	public void method139(boolean flag) {
		byte abyte0[] = aClass2_888.getDataForName("title.dat");
		RgbImage class50_sub1_sub1_sub1 = new RgbImage(abyte0, this);
		aClass18_1201.initDrawingArea();
		class50_sub1_sub1_sub1.draw(0, 0);
		aClass18_1202.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-637, 0);
		aClass18_1198.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-128, 0);
		aClass18_1199.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-202, -371);
		aClass18_1200.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-202, -171);
		aClass18_1203.initDrawingArea();
		class50_sub1_sub1_sub1.draw(0, -265);
		aClass18_1204.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-562, -265);
		aClass18_1205.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-128, -171);
		aClass18_1206.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-562, -171);
		int ai[] = new int[class50_sub1_sub1_sub1.imgWidth];
		for(int i = 0; i < class50_sub1_sub1_sub1.imgHeight; i++) {
			for(int j = 0; j < class50_sub1_sub1_sub1.imgWidth; j++)
				ai[j] = class50_sub1_sub1_sub1.pixels[(class50_sub1_sub1_sub1.imgWidth - j - 1) + class50_sub1_sub1_sub1.imgWidth * i];

			for(int l = 0; l < class50_sub1_sub1_sub1.imgWidth; l++)
				class50_sub1_sub1_sub1.pixels[l + class50_sub1_sub1_sub1.imgWidth * i] = ai[l];

		}

		aClass18_1201.initDrawingArea();
		class50_sub1_sub1_sub1.draw(382, 0);
		aClass18_1202.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-255, 0);
		aClass18_1198.initDrawingArea();
		class50_sub1_sub1_sub1.draw(254, 0);
		aClass18_1199.initDrawingArea();
		class50_sub1_sub1_sub1.draw(180, -371);
		aClass18_1200.initDrawingArea();
		class50_sub1_sub1_sub1.draw(180, -171);
		aClass18_1203.initDrawingArea();
		if(flag) {
			for(int k = 1; k > 0; k++)
				;
		}
		class50_sub1_sub1_sub1.draw(382, -265);
		aClass18_1204.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-180, -265);
		aClass18_1205.initDrawingArea();
		class50_sub1_sub1_sub1.draw(254, -171);
		aClass18_1206.initDrawingArea();
		class50_sub1_sub1_sub1.draw(-180, -171);
		class50_sub1_sub1_sub1 = new RgbImage(aClass2_888, "logo", 0);
		aClass18_1198.initDrawingArea();
		class50_sub1_sub1_sub1.method461(18, 382 - class50_sub1_sub1_sub1.imgWidth / 2 - 128, -488);
		class50_sub1_sub1_sub1 = null;
		abyte0 = null;
		ai = null;
		System.gc();
	}

	public void method140(byte byte0, Class50_Sub2 class50_sub2) {
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if(byte0 != -61)
			byteStream2.putByte(175);
		if(class50_sub2.anInt1392 == 0)
			i = aClass22_1164.method267(class50_sub2.anInt1391, class50_sub2.anInt1393, class50_sub2.anInt1394);
		if(class50_sub2.anInt1392 == 1)
			i = aClass22_1164.method268(class50_sub2.anInt1393, (byte) 4, class50_sub2.anInt1391, class50_sub2.anInt1394);
		if(class50_sub2.anInt1392 == 2)
			i = aClass22_1164.method269(class50_sub2.anInt1391, class50_sub2.anInt1393, class50_sub2.anInt1394);
		if(class50_sub2.anInt1392 == 3)
			i = aClass22_1164.method270(class50_sub2.anInt1391, class50_sub2.anInt1393, class50_sub2.anInt1394);
		if(i != 0) {
			int i1 = aClass22_1164.method271(class50_sub2.anInt1391, class50_sub2.anInt1393, class50_sub2.anInt1394, i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		class50_sub2.anInt1387 = j;
		class50_sub2.anInt1389 = k;
		class50_sub2.anInt1388 = l;
	}

	public void method141(int i) {
		aBoolean1243 = false;
		while(aBoolean1320) {
			aBoolean1243 = false;
			try {
				Thread.sleep(50L);
			} catch(Exception _ex) {
			}
		}
		aClass50_Sub1_Sub1_Sub3_1292 = null;
		aClass50_Sub1_Sub1_Sub3_1293 = null;
		aClass50_Sub1_Sub1_Sub3Array1117 = null;
		anIntArray1310 = null;
		anIntArray1311 = null;
		if(i != 28614)
			aBoolean1074 = !aBoolean1074;
		anIntArray1312 = null;
		anIntArray1313 = null;
		anIntArray1176 = null;
		anIntArray1177 = null;
		anIntArray1084 = null;
		anIntArray1085 = null;
		aClass50_Sub1_Sub1_Sub1_1017 = null;
		aClass50_Sub1_Sub1_Sub1_1018 = null;
	}

	public void drawInterface(int i, int j, int k, GameInterface gameInterface) {
		if(gameInterface.type != 0 || gameInterface.children == null)
			return;
		if(gameInterface.mouseOverTriggered && anInt1302 != gameInterface.id && anInt1280 != gameInterface.id && anInt1106 != gameInterface.id)
			return;
		int i1 = DrawingArea.viewportLeft;
		int j1 = DrawingArea.viewportTop;
		int k1 = DrawingArea.viewportRight;
		int l1 = DrawingArea.viewportBottom;
		DrawingArea.method446(i, j, i + gameInterface.height, j + gameInterface.width, true);
		int i2 = gameInterface.children.length;
		for(int j2 = 0; j2 < i2; j2++) {
			int childX = gameInterface.childrenX[j2] + j;
			int childY = (gameInterface.childrenY[j2] + i) - k;
			GameInterface child = GameInterface.getInterface(gameInterface.children[j2]);
			childX += child.offsetX;
			childY += child.offsetY;
			if(child.contentType > 0)
				method103((byte) 2, child);
			if(child.type == 0) {
				if(child.anInt231 > child.scrollHeight - child.height)
					child.anInt231 = child.scrollHeight - child.height;
				if(child.anInt231 < 0)
					child.anInt231 = 0;
				drawInterface(childY, childX, child.anInt231, child);
				if(child.scrollHeight > child.height)
					method56(true, child.anInt231, childX + child.width, child.height, child.scrollHeight, childY);
			} else if(child.type != 1)
				if(child.type == 2) {
					int i3 = 0;
					for(int i4 = 0; i4 < child.height; i4++) {
						for(int j5 = 0; j5 < child.width; j5++) {
							int i6 = childX + j5 * (32 + child.anInt263);
							int l6 = childY + i4 * (32 + child.anInt244);
							if(i3 < 20) {
								i6 += child.anIntArray221[i3];
								l6 += child.anIntArray213[i3];
							}
							if(child.anIntArray269[i3] > 0) {
								int i7 = 0;
								int j8 = 0;
								int l10 = child.anIntArray269[i3] - 1;
								if(i6 > DrawingArea.viewportLeft - 32 && i6 < DrawingArea.viewportRight && l6 > DrawingArea.viewportTop - 32 && l6 < DrawingArea.viewportBottom || anInt1113 != 0
										&& anInt1112 == i3) {
									int k11 = 0;
									if(itemSelected == 1 && anInt1147 == i3 && anInt1148 == child.id)
										k11 = 0xffffff;
									RgbImage class50_sub1_sub1_sub1_2 = ItemDefinition.method221((byte) -33, k11, child.anIntArray224[i3], l10);
									if(class50_sub1_sub1_sub1_2 != null) {
										if(anInt1113 != 0 && anInt1112 == i3 && anInt1111 == child.id) {
											i7 = super.mouseX - anInt1114;
											j8 = super.mouseY - anInt1115;
											if(i7 < 5 && i7 > -5)
												i7 = 0;
											if(j8 < 5 && j8 > -5)
												j8 = 0;
											if(anInt1269 < 5) {
												i7 = 0;
												j8 = 0;
											}
											class50_sub1_sub1_sub1_2.method463(0, i6 + i7, l6 + j8, 128);
											if(l6 + j8 < DrawingArea.viewportTop && gameInterface.anInt231 > 0) {
												int i12 = (anInt951 * (DrawingArea.viewportTop - l6 - j8)) / 3;
												if(i12 > anInt951 * 10)
													i12 = anInt951 * 10;
												if(i12 > gameInterface.anInt231)
													i12 = gameInterface.anInt231;
												gameInterface.anInt231 -= i12;
												anInt1115 += i12;
											}
											if(l6 + j8 + 32 > DrawingArea.viewportBottom && gameInterface.anInt231 < gameInterface.scrollHeight - gameInterface.height) {
												int j12 = (anInt951 * ((l6 + j8 + 32) - DrawingArea.viewportBottom)) / 3;
												if(j12 > anInt951 * 10)
													j12 = anInt951 * 10;
												if(j12 > gameInterface.scrollHeight - gameInterface.height - gameInterface.anInt231)
													j12 = gameInterface.scrollHeight - gameInterface.height - gameInterface.anInt231;
												gameInterface.anInt231 += j12;
												anInt1115 -= j12;
											}
										} else if(anInt1332 != 0 && anInt1331 == i3 && anInt1330 == child.id)
											class50_sub1_sub1_sub1_2.method463(0, i6, l6, 128);
										else
											class50_sub1_sub1_sub1_2.method461(l6, i6, -488);
										if(class50_sub1_sub1_sub1_2.libWidth == 33 || child.anIntArray224[i3] != 1) {
											int k12 = child.anIntArray224[i3];
											smallFont.method474(formatMoney(k12), i6 + 1 + i7, l6 + 10 + j8, 0);
											smallFont.method474(formatMoney(k12), i6 + i7, l6 + 9 + j8, 0xffff00);
										}
									}
								}
							} else if(child.aClass50_Sub1_Sub1_Sub1Array265 != null && i3 < 20) {
								RgbImage class50_sub1_sub1_sub1_1 = child.aClass50_Sub1_Sub1_Sub1Array265[i3];
								if(class50_sub1_sub1_sub1_1 != null)
									class50_sub1_sub1_sub1_1.method461(l6, i6, -488);
							}
							i3++;
						}

					}

				} else if(child.type == 3) {
					boolean flag = false;
					if(anInt1106 == child.id || anInt1280 == child.id || anInt1302 == child.id)
						flag = true;
					int j3;
					if(method95(child, -693)) {
						j3 = child.anInt260;
						if(flag && child.anInt226 != 0)
							j3 = child.anInt226;
					} else {
						j3 = child.textColour;
						if(flag && child.anInt261 != 0)
							j3 = child.anInt261;
					}
					if(child.alpha == 0) {
						if(child.aBoolean239)
							DrawingArea.fillRect(child.height, childY, j3, (byte) -24, child.width, childX);
						else
							DrawingArea.method450(0, childY, child.height, j3, childX, child.width);
					} else if(child.aBoolean239)
						DrawingArea.method448(false, j3, childY, child.width, child.height, 256 - (child.alpha & 0xff), childX);
					else
						DrawingArea.method451(childX, child.width, j3, child.height, childY, 256 - (child.alpha & 0xff), (byte) -113);
				} else if(child.type == 4) {
					GameFont font = child.font;
					String text = child.text;
					boolean flag1 = false;
					if(anInt1106 == child.id || anInt1280 == child.id || anInt1302 == child.id)
						flag1 = true;
					int textColour;
					if(method95(child, -693)) {
						textColour = child.anInt260;
						if(flag1 && child.anInt226 != 0)
							textColour = child.anInt226;
						if(child.aString249.length() > 0)
							text = child.aString249;
					} else {
						textColour = child.textColour;
						if(flag1 && child.anInt261 != 0)
							textColour = child.anInt261;
					}
					if(child.actionType == 6 && aBoolean1239) {
						text = "Please wait...";
						textColour = child.textColour;
					}
					if(DrawingArea.width == 479) {
						if(textColour == 0xffff00)
							textColour = 255;
						if(textColour == 49152)
							textColour = 0xffffff;
					}
					for(int origY = childY + font.charHeight; text.length() > 0; origY += font.charHeight) {
						if(text.indexOf("%") != -1) {
							do {
								int k8 = text.indexOf("%1");
								if(k8 == -1)
									break;
								text = text.substring(0, k8) + method89(method129(3, 0, child), 8) + text.substring(k8 + 2);
							} while(true);
							do {
								int l8 = text.indexOf("%2");
								if(l8 == -1)
									break;
								text = text.substring(0, l8) + method89(method129(3, 1, child), 8) + text.substring(l8 + 2);
							} while(true);
							do {
								int i9 = text.indexOf("%3");
								if(i9 == -1)
									break;
								text = text.substring(0, i9) + method89(method129(3, 2, child), 8) + text.substring(i9 + 2);
							} while(true);
							do {
								int j9 = text.indexOf("%4");
								if(j9 == -1)
									break;
								text = text.substring(0, j9) + method89(method129(3, 3, child), 8) + text.substring(j9 + 2);
							} while(true);
							do {
								int k9 = text.indexOf("%5");
								if(k9 == -1)
									break;
								text = text.substring(0, k9) + method89(method129(3, 4, child), 8) + text.substring(k9 + 2);
							} while(true);
						}
						int stringHeight = 0;
						/*int l9 = text.indexOf("\\n");
						String finalString;
						if(l9 != -1) {
							finalString = text.substring(0, l9);
							text = text.substring(l9 + 2);
						} else {
							finalString = text;
							text = "";
						}
						stringHeight += font.charHeight + 1;*/
						for(String s = child.text; s.length() > 0;) {
							int lineBreaks = s.indexOf("\\n");
							if(lineBreaks != -1) {
								s = s.substring(lineBreaks + 2);
							} else {
								s = "";
							}
							stringHeight += font.charHeight + 1;
						}
						if(gameInterface.scrollHeight < stringHeight) {
							gameInterface.scrollHeight = stringHeight + font.charHeight + 1;
						}
						
						

						for(int newY = origY; text.length() > 0; newY += font.charHeight + 1) {
							int l11 = text.indexOf("\\n");
							String stringLine;
							if(l11 != -1) {
								stringLine = text.substring(0, l11);
								text = text.substring(l11 + 2);
							} else {
								stringLine = text;
								text = "";
							}
							//font.method478(s5, childX, newY, 0, false);
							if(child.textCentered)
								font.drawCenteredText(child.textShadow, textColour, childX + child.width / 2, newY, stringLine);
							else
								font.method478(stringLine, childX, newY, textColour, child.textShadow);
						}
						/*if(child.aBoolean272)
							font.method471(child.aBoolean247, anInt1056, j4, j7, childX + child.width / 2, finalString);
						else
							font.method478(finalString, childX, j7, j4, child.aBoolean247);*/
					}

				} else if(child.type == 5) {
					RgbImage class50_sub1_sub1_sub1;
					if(method95(child, -693))
						class50_sub1_sub1_sub1 = child.aClass50_Sub1_Sub1_Sub1_245;
					else
						class50_sub1_sub1_sub1 = child.aClass50_Sub1_Sub1_Sub1_212;
					if(class50_sub1_sub1_sub1 != null)
						class50_sub1_sub1_sub1.method461(childY, childX, -488);
				} else if(child.type == 6) {
					int k3 = Rasterizer.anInt1532;
					int k4 = Rasterizer.anInt1533;
					Rasterizer.anInt1532 = childX + child.width / 2;
					Rasterizer.anInt1533 = childY + child.height / 2;
					int k5 = Rasterizer.anIntArray1536[child.anInt252] * child.anInt251 >> 16;
					int j6 = Rasterizer.anIntArray1537[child.anInt252] * child.anInt251 >> 16;
					boolean flag2 = method95(child, -693);
					int k7;
					if(flag2)
						k7 = child.anInt287;
					else
						k7 = child.anInt286;
					Model class50_sub1_sub4_sub4;
					if(k7 == -1) {
						class50_sub1_sub4_sub4 = child.method203(-1, -1, 0, flag2);
					} else {
						Class14 class14 = Class14.aClass14Array293[k7];
						class50_sub1_sub4_sub4 = child.method203(class14.anIntArray295[child.anInt235], class14.anIntArray296[child.anInt235], 0, flag2);
					}
					if(class50_sub1_sub4_sub4 != null)
						class50_sub1_sub4_sub4.method598(0, child.anInt253, 0, child.anInt252, 0, k5, j6);
					Rasterizer.anInt1532 = k3;
					Rasterizer.anInt1533 = k4;
				} else {
					if(child.type == 7) {
						GameFont class50_sub1_sub1_sub2_1 = child.font;
						int l4 = 0;
						for(int l5 = 0; l5 < child.height; l5++) {
							for(int k6 = 0; k6 < child.width; k6++) {
								if(child.anIntArray269[l4] > 0) {
									ItemDefinition class16 = ItemDefinition.forId(child.anIntArray269[l4] - 1);
									String s6 = String.valueOf(class16.name);
									if(class16.stackable || child.anIntArray224[l4] != 1)
										s6 = s6 + " x" + method135(0, child.anIntArray224[l4]);
									int i10 = childX + k6 * (115 + child.anInt263);
									int i11 = childY + l5 * (12 + child.anInt244);
									if(child.textCentered)
										class50_sub1_sub1_sub2_1.drawCenteredText(child.textShadow, child.textColour, i10 + child.width / 2, i11, s6);
									else
										class50_sub1_sub1_sub2_1.method478(s6, i10, i11, child.textColour, child.textShadow);
								}
								l4++;
							}

						}

					}
					if(child.type == GameInterface.TYPE_TOOLTIP && (anInt1284 == child.id || anInt1044 == child.id || anInt1129 == child.id) && tooltipTimer == GameInterface.TOOLTIP_DELAY) {
						int tipWidth = 0;
						int tipHeight = 0;
						GameFont tipFont = normalFont;
						for(String tooltipText = child.text; tooltipText.length() > 0;) {
							int lineBreaks = tooltipText.indexOf("\\n");
							String s4;
							if(lineBreaks != -1) {
								s4 = tooltipText.substring(0, lineBreaks);
								tooltipText = tooltipText.substring(lineBreaks + 2);
							} else {
								s4 = tooltipText;
								tooltipText = "";
							}
							int stringWidth = tipFont.getFormattedStringWidth(s4);
							if(stringWidth > tipWidth)
								tipWidth = stringWidth;
							tipHeight += tipFont.charHeight + 1;
						}

						tipWidth += 6;
						tipHeight += 7;
						// int tipX = (childX + child.width) - /*5 - */tipWidth;
						int tipX = childX;
						int tipY = childY + child.height + 2; // + 5
						// if(tipX < childX + 5)
						// tipX = childX + 5;
						if(tipX + tipWidth > j + gameInterface.width)
							tipX = (j + gameInterface.width) - tipWidth;
						if(tipY + tipHeight > i + gameInterface.height)
							tipY = (i + gameInterface.height) - tipHeight;
						DrawingArea.fillRect(tipHeight, tipY, 0xffffa0, (byte) -24, tipWidth, tipX);
						DrawingArea.method450(0, tipY, tipHeight, 0, tipX, tipWidth);
						String s2 = child.text;
						for(int j11 = tipY + tipFont.charHeight + 2; s2.length() > 0; j11 += tipFont.charHeight + 1) {
							int l11 = s2.indexOf("\\n");
							String s5;
							if(l11 != -1) {
								s5 = s2.substring(0, l11);
								s2 = s2.substring(l11 + 2);
							} else {
								s5 = s2;
								s2 = "";
							}
							tipFont.method478(s5, tipX + 3, j11, 0, false);
						}

					}
				}
		}

		DrawingArea.method446(j1, i1, l1, k1, true);
	}

	public void method143(byte byte0) {
		if(byte0 != -40)
			aBoolean1207 = !aBoolean1207;
		if(aBoolean926 && loadingStage == 2 && Class8.anInt162 != anInt1091) {
			method125(-332, null, "Loading - please wait.");
			loadingStage = 1;
			aLong1229 = System.currentTimeMillis();
		}
		if(loadingStage == 1) {
			int i = method144(5);
			if(i != 0 && System.currentTimeMillis() - aLong1229 > 0x57e40L) {
				Signlink.reporterror(aString1092 + " glcfb " + aLong930 + "," + i + "," + aBoolean926 + "," + aClass23Array1228[0] + "," + aClass32_Sub1_1291.method333() + "," + anInt1091 + ","
						+ anInt889 + "," + anInt890);
				aLong1229 = System.currentTimeMillis();
			}
		}
		if(loadingStage == 2 && anInt1091 != anInt1276) {
			anInt1276 = anInt1091;
			method115(anInt1091, 0);
		}
	}

	public int method144(int i) {
		for(int j = 0; j < aByteArrayArray838.length; j++) {
			if(aByteArrayArray838[j] == null && anIntArray857[j] != -1)
				return -1;
			if(aByteArrayArray1232[j] == null && anIntArray858[j] != -1)
				return -2;
		}

		boolean flag = true;
		if(i < 5 || i > 5)
			aBoolean953 = !aBoolean953;
		for(int k = 0; k < aByteArrayArray838.length; k++) {
			byte abyte0[] = aByteArrayArray1232[k];
			if(abyte0 != null) {
				int l = (anIntArray856[k] >> 8) * 64 - anInt1040;
				int i1 = (anIntArray856[k] & 0xff) * 64 - anInt1041;
				if(aBoolean1163) {
					l = 10;
					i1 = 10;
				}
				flag &= Class8.method181(l, i1, abyte0, 24515);
			}
		}

		if(!flag)
			return -3;
		if(aBoolean1209) {
			return -4;
		} else {
			loadingStage = 2;
			Class8.anInt162 = anInt1091;
			method93(175);
			byteStream2.createFrame(6);
			return 0;
		}
	}

	public void method145(boolean flag, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
		Class50_Sub2 class50_sub2 = null;
		for(Class50_Sub2 class50_sub2_1 = (Class50_Sub2) aClass6_1261.method158(); class50_sub2_1 != null; class50_sub2_1 = (Class50_Sub2) aClass6_1261.method160(1)) {
			if(class50_sub2_1.anInt1391 != i || class50_sub2_1.anInt1393 != j || class50_sub2_1.anInt1394 != i2 || class50_sub2_1.anInt1392 != l1)
				continue;
			class50_sub2 = class50_sub2_1;
			break;
		}

		if(class50_sub2 == null) {
			class50_sub2 = new Class50_Sub2();
			class50_sub2.anInt1391 = i;
			class50_sub2.anInt1392 = l1;
			class50_sub2.anInt1393 = j;
			class50_sub2.anInt1394 = i2;
			method140((byte) -61, class50_sub2);
			aClass6_1261.method155(class50_sub2);
		}
		class50_sub2.anInt1384 = j1;
		class50_sub2.anInt1386 = i1;
		class50_sub2.anInt1385 = k;
		class50_sub2.anInt1395 = k1;
		class50_sub2.anInt1390 = l;
		loggedIn &= flag;
	}

	public void method146(byte byte0) {
		if(byte0 != 4)
			return;
		if(minimapLock != 0)
			return;
		if(super.anInt28 == 1) {
			int i = super.clickX - 25 - 550;
			int j = super.clickY - 5 - 4;
			if(i >= 0 && j >= 0 && i < 146 && j < 151) {
				i -= 73;
				j -= 75;
				int k = cameraX + minimapRotation & 0x7ff;
				int l = Rasterizer.anIntArray1536[k];
				int i1 = Rasterizer.anIntArray1537[k];
				l = l * (minimapZoom + 256) >> 8;
				i1 = i1 * (minimapZoom + 256) >> 8;
				int j1 = j * l + i * i1 >> 11;
				int k1 = j * i1 - i * l >> 11;
				int l1 = ((Mob) (sessionPlayer)).anInt1610 + j1 >> 7;
				int i2 = ((Mob) (sessionPlayer)).anInt1611 - k1 >> 7;
				boolean flag = method35(true, false, i2, ((Mob) (sessionPlayer)).anIntArray1587[0], 0, 0, 1, 0, l1, 0, 0, ((Mob) (sessionPlayer)).anIntArray1586[0]);
				if(flag) {
					byteStream2.putByte(i);
					byteStream2.putByte(j);
					byteStream2.putShort(cameraX);
					byteStream2.putByte(57);
					byteStream2.putByte(minimapRotation);
					byteStream2.putByte(minimapZoom);
					byteStream2.putByte(89);
					byteStream2.putShort(((Mob) (sessionPlayer)).anInt1610);
					byteStream2.putShort(((Mob) (sessionPlayer)).anInt1611);
					byteStream2.putByte(anInt1126);
					byteStream2.putByte(63);
				}
			}
		}
	}

	public void method147(int i) {
		if(super.aClass18_15 != null)
			return;
		method141(28614);
		aClass18_1198 = null;
		aClass18_1199 = null;
		aClass18_1200 = null;
		if(i >= 0)
			anInt1004 = -4;
		aClass18_1201 = null;
		aClass18_1202 = null;
		aClass18_1203 = null;
		aClass18_1204 = null;
		aClass18_1205 = null;
		aClass18_1206 = null;
		aClass18_1159 = null;
		mapDrawingArea = null;
		tabBackDrawingArea = null;
		gameScreenDrawingArea = null;
		aClass18_1108 = null;
		aClass18_1109 = null;
		aClass18_1110 = null;
		super.aClass18_15 = new GraphicsBuffer(765, 503, getGameComponent());
		repaintRequested = true;
	}

	public boolean method148(int i, String s) {
		if(s == null)
			return false;
		for(int j = 0; j < anInt859; j++)
			if(s.equalsIgnoreCase(aStringArray849[j]))
				return true;

		if(i != 13292)
			aBoolean1014 = !aBoolean1014;
		return s.equalsIgnoreCase(sessionPlayer.username);
	}

	public void method149(int i) {
		while(i >= 0)
			packetOpcode = byteStream4.getUnsignedByte();
		if(anInt1225 == 0) {
			int j = super.width / 2 - 80;
			int i1 = super.height / 2 + 20;
			i1 += 20;
			if(super.anInt28 == 1 && super.clickX >= j - 75 && super.clickX <= j + 75 && super.clickY >= i1 - 20 && super.clickY <= i1 + 20) {
				anInt1225 = 3;
				anInt977 = 0;
			}
			j = super.width / 2 + 80;
			if(super.anInt28 == 1 && super.clickX >= j - 75 && super.clickX <= j + 75 && super.clickY >= i1 - 20 && super.clickY <= i1 + 20) {
				aString957 = "";
				aString958 = "Enter your username & password.";
				anInt1225 = 2;
				anInt977 = 0;
				return;
			}
		} else {
			if(anInt1225 == 2) {
				int k = super.height / 2 - 40;
				k += 30;
				k += 25;
				if(super.anInt28 == 1 && super.clickY >= k - 15 && super.clickY < k)
					anInt977 = 0;
				k += 15;
				if(super.anInt28 == 1 && super.clickY >= k - 15 && super.clickY < k)
					anInt977 = 1;
				k += 15;
				int j1 = super.width / 2 - 80;
				int l1 = super.height / 2 + 50;
				l1 += 20;
				if(super.anInt28 == 1 && super.clickX >= j1 - 75 && super.clickX <= j1 + 75 && super.clickY >= l1 - 20 && super.clickY <= l1 + 20) {
					anInt850 = 0;
					method79(aString1092, aString1093, false);
					if(loggedIn)
						return;
				}
				j1 = super.width / 2 + 80;
				if(super.anInt28 == 1 && super.clickX >= j1 - 75 && super.clickX <= j1 + 75 && super.clickY >= l1 - 20 && super.clickY <= l1 + 20) {
					anInt1225 = 0;
					aString1092 = "";
					aString1093 = "";
				}
				do {
					int i2 = method5(-983);
					if(i2 == -1)
						break;
					boolean flag = false;
					for(int j2 = 0; j2 < aString1007.length(); j2++) {
						if(i2 != aString1007.charAt(j2))
							continue;
						flag = true;
						break;
					}

					if(anInt977 == 0) {
						if(i2 == 8 && aString1092.length() > 0)
							aString1092 = aString1092.substring(0, aString1092.length() - 1);
						if(i2 == 9 || i2 == 10 || i2 == 13)
							anInt977 = 1;
						if(flag)
							aString1092 += (char) i2;
						if(aString1092.length() > NameUtils.NAME_LEN)
							aString1092 = aString1092.substring(0, NameUtils.NAME_LEN);
					} else if(anInt977 == 1) {
						if(i2 == 8 && aString1093.length() > 0)
							aString1093 = aString1093.substring(0, aString1093.length() - 1);
						if(i2 == 9 || i2 == 10 || i2 == 13)
							anInt977 = 0;
						if(flag)
							aString1093 += (char) i2;
						if(aString1093.length() > 20)
							aString1093 = aString1093.substring(0, 20);
					}
				} while(true);
				return;
			}
			if(anInt1225 == 3) {
				int l = super.width / 2;
				int k1 = super.height / 2 + 50;
				k1 += 20;
				if(super.anInt28 == 1 && super.clickX >= l - 75 && super.clickX <= l + 75 && super.clickY >= k1 - 20 && super.clickY <= k1 + 20)
					anInt1225 = 0;
			}
		}
	}

	public void method150(int i, int j, int k, int l, int i1, int j1) {
		int k1 = aClass22_1164.method267(j, k, i);
		i1 = 62 / i1;
		if(k1 != 0) {
			int l1 = aClass22_1164.method271(j, k, i, k1);
			int k2 = l1 >> 6 & 3;
			int i3 = l1 & 0x1f;
			int k3 = j1;
			if(k1 > 0)
				k3 = l;
			int ai[] = minimapImage.pixels;
			int k4 = 24624 + k * 4 + (103 - i) * 512 * 4;
			int i5 = k1 >> 14 & 0x7fff;
			Class47 class47_2 = Class47.method423(i5);
			if(class47_2.anInt795 != -1) {
				IndexedImage class50_sub1_sub1_sub3_2 = mapscenes[class47_2.anInt795];
				if(class50_sub1_sub1_sub3_2 != null) {
					int i6 = (class47_2.anInt801 * 4 - class50_sub1_sub1_sub3_2.imgWidth) / 2;
					int j6 = (class47_2.anInt775 * 4 - class50_sub1_sub1_sub3_2.imgHeight) / 2;
					class50_sub1_sub1_sub3_2.drawImage(48 + k * 4 + i6, 48 + (104 - i - class47_2.anInt775) * 4 + j6);
				}
			} else {
				if(i3 == 0 || i3 == 2)
					if(k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if(k2 == 1) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if(k2 == 2) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if(k2 == 3) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
				if(i3 == 3)
					if(k2 == 0)
						ai[k4] = k3;
					else if(k2 == 1)
						ai[k4 + 3] = k3;
					else if(k2 == 2)
						ai[k4 + 3 + 1536] = k3;
					else if(k2 == 3)
						ai[k4 + 1536] = k3;
				if(i3 == 2)
					if(k2 == 3) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if(k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if(k2 == 1) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if(k2 == 2) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
			}
		}
		k1 = aClass22_1164.method269(j, k, i);
		if(k1 != 0) {
			int i2 = aClass22_1164.method271(j, k, i, k1);
			int l2 = i2 >> 6 & 3;
			int j3 = i2 & 0x1f;
			int l3 = k1 >> 14 & 0x7fff;
			Class47 class47_1 = Class47.method423(l3);
			if(class47_1.anInt795 != -1) {
				IndexedImage class50_sub1_sub1_sub3_1 = mapscenes[class47_1.anInt795];
				if(class50_sub1_sub1_sub3_1 != null) {
					int j5 = (class47_1.anInt801 * 4 - class50_sub1_sub1_sub3_1.imgWidth) / 2;
					int k5 = (class47_1.anInt775 * 4 - class50_sub1_sub1_sub3_1.imgHeight) / 2;
					class50_sub1_sub1_sub3_1.drawImage(48 + k * 4 + j5, 48 + (104 - i - class47_1.anInt775) * 4 + k5);
				}
			} else if(j3 == 9) {
				int l4 = 0xeeeeee;
				if(k1 > 0)
					l4 = 0xee0000;
				int ai1[] = minimapImage.pixels;
				int l5 = 24624 + k * 4 + (103 - i) * 512 * 4;
				if(l2 == 0 || l2 == 2) {
					ai1[l5 + 1536] = l4;
					ai1[l5 + 1024 + 1] = l4;
					ai1[l5 + 512 + 2] = l4;
					ai1[l5 + 3] = l4;
				} else {
					ai1[l5] = l4;
					ai1[l5 + 512 + 1] = l4;
					ai1[l5 + 1024 + 2] = l4;
					ai1[l5 + 1536 + 3] = l4;
				}
			}
		}
		k1 = aClass22_1164.method270(j, k, i);
		if(k1 != 0) {
			int j2 = k1 >> 14 & 0x7fff;
			Class47 class47 = Class47.method423(j2);
			if(class47.anInt795 != -1) {
				IndexedImage class50_sub1_sub1_sub3 = mapscenes[class47.anInt795];
				if(class50_sub1_sub1_sub3 != null) {
					int i4 = (class47.anInt801 * 4 - class50_sub1_sub1_sub3.imgWidth) / 2;
					int j4 = (class47.anInt775 * 4 - class50_sub1_sub1_sub3.imgHeight) / 2;
					class50_sub1_sub1_sub3.drawImage(48 + k * 4 + i4, 48 + (104 - i - class47.anInt775) * 4 + j4);
				}
			}
		}
	}

	public void renderGameView() {
		anInt1138++;
		method119(0, true);
		method57(751, true);
		method119(0, false);
		method57(751, false);
		method51(false);
		method76(-992);
		if(!aBoolean1211) {
			int j = anInt1251;
			if(anInt1289 / 256 > j)
				j = anInt1289 / 256;
			if(aBooleanArray927[4] && anIntArray852[4] + 128 > j)
				j = anIntArray852[4] + 128;
			int l = cameraX + anInt1255 & 0x7ff;
			method94(method110(((Mob) (sessionPlayer)).anInt1611, ((Mob) (sessionPlayer)).anInt1610, (byte) 9, anInt1091) - 50, anInt1262, j, 600 + j * 3, l, anInt1263, (byte) -103);
		}
		int k;
		if(!aBoolean1211)
			k = method117((byte) 1);
		else
			k = method118(-276);
		int i1 = anInt1216;
		int j1 = anInt1217;
		int k1 = anInt1218;
		int l1 = anInt1219;
		int i2 = anInt1220;
		for(int j2 = 0; j2 < 5; j2++)
			if(aBooleanArray927[j2]) {
				int k2 = (int) ((Math.random() * (double) (anIntArray1105[j2] * 2 + 1) - (double) anIntArray1105[j2]) + Math.sin((double) anIntArray1145[j2] * ((double) anIntArray991[j2] / 100D))
						* (double) anIntArray852[j2]);
				if(j2 == 0)
					anInt1216 += k2;
				if(j2 == 1)
					anInt1217 += k2;
				if(j2 == 2)
					anInt1218 += k2;
				if(j2 == 3)
					anInt1220 = anInt1220 + k2 & 0x7ff;
				if(j2 == 4) {
					anInt1219 += k2;
					if(anInt1219 < 128)
						anInt1219 = 128;
					if(anInt1219 > 383)
						anInt1219 = 383;
				}
			}

		int l2 = Rasterizer.anInt1547;
		Model.aBoolean1705 = true;
		Model.anInt1708 = 0;
		Model.anInt1706 = super.mouseX - 4;
		Model.anInt1707 = super.mouseY - 4;
		DrawingArea.clear();
		aClass22_1164.method280(anInt1216, k, 0, anInt1217, anInt1218, anInt1220, anInt1219);
		aClass22_1164.method255(anInt897);
		method121(false);
		method127(true);
		method65(l2, -927);
		method109(30729);
		gameScreenDrawingArea.drawGraphics(4, 4, super.graphics);
		anInt1216 = i1;
		anInt1217 = j1;
		anInt1218 = k1;
		anInt1219 = l1;
		anInt1220 = i2;
	}

	public void method152(int i) {
		if(i != -23763)
			startUp();
		for(int j = 0; j < anInt1035; j++)
			if(anIntArray1259[j] <= 0) {
				boolean flag = false;
				try {
					if(anIntArray1090[j] == anInt1272 && anIntArray1321[j] == anInt935) {
						if(!method78(295))
							flag = true;
					} else {
						ByteBuffer byteStream = Class38.method366(anIntArray1321[j], (byte) 6, anIntArray1090[j]);
						if(System.currentTimeMillis() + (long) (byteStream.position / 22) > aLong1250 + (long) (anInt1179 / 22)) {
							anInt1179 = byteStream.position;
							aLong1250 = System.currentTimeMillis();
							if(method116(3, byteStream.position, byteStream.payload)) {
								anInt1272 = anIntArray1090[j];
								anInt935 = anIntArray1321[j];
							} else {
								flag = true;
							}
						}
					}
				} catch(Exception exception) {
					if(Signlink.reporterror) {
						byteStream2.createFrame(80);
						byteStream2.putShort(anIntArray1090[j] & 0x7fff);
					} else {
						byteStream2.createFrame(80);
						byteStream2.putShort(-1);
					}
				}
				if(!flag || anIntArray1259[j] == -5) {
					anInt1035--;
					for(int k = j; k < anInt1035; k++) {
						anIntArray1090[k] = anIntArray1090[k + 1];
						anIntArray1321[k] = anIntArray1321[k + 1];
						anIntArray1259[k] = anIntArray1259[k + 1];
					}

					j--;
				} else {
					anIntArray1259[j] = -5;
				}
			} else {
				anIntArray1259[j]--;
			}

		if(anInt1128 > 0) {
			anInt1128 -= 20;
			if(anInt1128 < 0)
				anInt1128 = 0;
			if(anInt1128 == 0 && aBoolean1266 && !aBoolean926) {
				anInt1270 = anInt1327;
				aBoolean1271 = true;
				aClass32_Sub1_1291.method329(2, anInt1270);
			}
		}
	}

	public Client() {
		anIntArray837 = new int[9];
		aString839 = "";
		playerExps = new int[SkillConstants.SKILL_COUNT];
		aStringArray849 = new String[200];
		anIntArray852 = new int[5];
		anInt854 = 2;
		aString861 = "";
		aStringArray863 = new String[100];
		anIntArray864 = new int[100];
		aBoolean866 = false;
		anIntArrayArrayArray879 = new int[4][13][13];
		anIntArrayArray885 = new int[104][104];
		anIntArrayArray886 = new int[104][104];
		aBoolean892 = false;
		anInt894 = -992;
		cross = new RgbImage[8];
		anInt897 = 559;
		aByte898 = 6;
		aBoolean900 = false;
		aByte901 = -123;
		anInt917 = 2;
		aBoolean918 = true;
		aBoolean919 = true;
		mapShape2 = new int[151];
		anInt921 = 8;
		aBooleanArray927 = new boolean[5];
		anInt928 = -188;
		byteStream1 = ByteBuffer.create();
		anInt931 = 0x23201b;
		anInt932 = -1;
		anInt933 = -1;
		aBoolean934 = true;
		anInt935 = -1;
		aByte936 = -113;
		aString937 = "";
		anInt938 = -214;
		anInt940 = 50;
		anIntArray941 = new int[anInt940];
		anIntArray942 = new int[anInt940];
		anIntArray943 = new int[anInt940];
		anIntArray944 = new int[anInt940];
		anIntArray945 = new int[anInt940];
		anIntArray946 = new int[anInt940];
		anIntArray947 = new int[anInt940];
		aStringArray948 = new String[anInt940];
		aString949 = "";
		aBoolean950 = false;
		aBoolean953 = false;
		hintHeadicons = new RgbImage[32];
		aByte956 = 1;
		aString957 = "";
		aString958 = "";
		aBoolean959 = true;
		anInt960 = -1;
		anInt961 = -1;
		byteStream2 = ByteBuffer.create();
		anInt968 = 2048;
		anInt969 = 2047;
		sessionPlayers = new Player[anInt968];
		sessionPlayerList = new int[anInt968];
		anIntArray974 = new int[anInt968];
		playerUpdateStreams = new ByteBuffer[anInt968];
		sideicons = new IndexedImage[13];
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionId = new int[500];
		menuActionCmd1 = new int[500];
		anInt988 = -1;
		anIntArray991 = new int[5];
		anIntArray1005 = new int[2000];
		anInt1010 = 2;
		aBoolean1014 = false;
		gameAlreadyLoaded = false;
		mapShape1 = new int[151];
		aString1026 = "";
		aBoolean1028 = false;
		playerLevels = new int[SkillConstants.SKILL_COUNT];
		mapfunctions = new RgbImage[100];
		aBoolean1033 = false;
		aBoolean1038 = true;
		anIntArray1039 = new int[2000];
		repaintRequested = false;
		anInt1051 = 69;
		anInt1053 = -1;
		anIntArray1054 = new int[SkillConstants.SKILL_COUNT];
		anInt1055 = 2;
		anInt1056 = 3;
		menuOpen = false;
		aByte1066 = 1;
		aBoolean1067 = false;
		aStringArray1069 = new String[5];
		aBooleanArray1070 = new boolean[5];
		anInt1072 = 20411;
		aLongArray1073 = new long[100];
		aBoolean1074 = false;
		markPosX = new int[1000];
		markPosY = new int[1000];
		prayerHeadicons = new RgbImage[32];
		anInt1080 = 0x4d4233;
		aCRC32_1088 = new CRC32();
		anInt1089 = -1;
		anIntArray1090 = new int[50];
		aString1092 = "Setsuna";
		aString1093 = "wwe";
		invalidHost = false;
		aBoolean1098 = false;
		anIntArray1099 = new int[5];
		aString1104 = "";
		anIntArray1105 = new int[5];
		anInt1107 = 78;
		anInt1119 = -30658;
		anIntArray1123 = new int[4000];
		anIntArray1124 = new int[4000];
		aBoolean1127 = false;
		aLongArray1130 = new long[200];
		byteStream3 = new ByteBuffer(new byte[5000]);
		sessionNpcs = new NPC[16384];
		sessionNpcList = new int[16384];
		anInt1135 = 0x766654;
		aBoolean1136 = false;
		loggedIn = false;
		anInt1140 = -110;
		aClass50_Sub1_Sub1_Sub3Array1142 = new IndexedImage[2];
		aByte1143 = -80;
		aBoolean1144 = true;
		anIntArray1145 = new int[5];
		mapscenes = new IndexedImage[100];
		anInt1154 = -916;
		aBoolean1155 = false;
		aByte1161 = 97;
		aBoolean1163 = false;
		anIntArray1166 = new int[256];
		anInt1169 = -1;
		anInt1175 = -89;
		anInt1178 = 300;
		compassShape1 = new int[33];
		tabRepaintRequested = false;
		hitmarks = new RgbImage[20];
		menuActionName = new String[500];
		byteStream4 = ByteBuffer.create();
		anIntArrayArray1189 = new int[104][104];
		anInt1191 = -1;
		aBoolean1209 = false;
		aClass6_1210 = new NodeList(true);
		aBoolean1211 = false;
		aBoolean1212 = false;
		flashingSidebarId = -1;
		aClass23Array1228 = new Class23[5];
		anInt1231 = -1;
		anInt1234 = 1;
		anInt1236 = 326;
		aBoolean1239 = false;
		aBoolean1240 = false;
		aBoolean1243 = false;
		aByteArray1245 = new byte[16384];
		aClass13_1249 = new GameInterface();
		anInt1251 = 128;
		anInt1256 = 1;
		anIntArray1258 = new int[100];
		anIntArray1259 = new int[50];
		aClass46Array1260 = new Class46[4];
		aClass6_1261 = new NodeList(true);
		aBoolean1265 = false;
		aBoolean1266 = true;
		anIntArray1267 = new int[200];
		aBoolean1271 = true;
		anInt1272 = -1;
		aBoolean1274 = true;
		aBoolean1275 = true;
		anInt1276 = -1;
		aBoolean1277 = false;
		mapMarkImage = new RgbImage[1000];
		anInt1279 = -1;
		anInt1281 = -939;
		aClass6_1282 = new NodeList(true);
		loadingError = false;
		selectedTab = 3;
		compassShape2 = new int[33];
		anInt1287 = 0x332d25;
		pkHeadicons = new RgbImage[32];
		anIntArray1295 = new int[1000];
		anIntArray1296 = new int[100];
		aStringArray1297 = new String[100];
		aStringArray1298 = new String[100];
		aBoolean1301 = true;
		aBoolean1314 = false;
		aByte1317 = -58;
		anInt1318 = 416;
		aBoolean1320 = false;
		anIntArray1321 = new int[50];
		aClass6ArrayArrayArray1323 = new NodeList[4][104][104];
		anIntArray1326 = new int[7];
		anInt1327 = -1;
		anInt1328 = 409;
		dataOrbBg = new IndexedImage[2];
		dataOrbFills = new IndexedImage[4];
		dataOrbIcons = new IndexedImage[4];
		playerRunning = false;
	}

	public int anIntArray837[];
	public byte aByteArrayArray838[][];
	public String aString839;
	public static BigInteger aBigInteger840 = new BigInteger(
			"7162900525229798032761816791230527296329313291232324290237849263501208207972894053929065636522363163621000728841182238772712427862772219676577293600221789");
	public static int anInt841;
	public int anIntArray842[] = { 0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff };
	public int playerExps[];
	public int anInt844;
	public int anInt845;
	public int anInt846;
	public int anInt847;
	public int anInt848;
	public String aStringArray849[];
	public int anInt850;
	public int anInt851;
	public int anIntArray852[];
	public int anInt853;
	public int anInt854;
	public int anInt855;
	public int anIntArray856[];
	public int anIntArray857[];
	public int anIntArray858[];
	public int anInt859;
	public int anInt860;
	public String aString861;
	public int anInt862;
	public String aStringArray863[];
	public int anIntArray864[];
	public int anInt865;
	public boolean aBoolean866;
	public int anInt867;
	public static boolean aBoolean868;
	public int packetSize;
	public int packetOpcode;
	public int anInt871;
	public int anInt872;
	public int anInt873;
	public int anInt874;
	public int anInt875;
	public int anInt876;
	public int anInt877;
	public int anInt878;
	public int anIntArrayArrayArray879[][][];
	public IndexedImage redstone0;
	public IndexedImage redstone1_2;
	public IndexedImage redstone3;
	public IndexedImage redstone6;
	public IndexedImage redstone4_5;
	public int anIntArrayArray885[][];
	public int anIntArrayArray886[][];
	public int anInt887;
	public CacheArchive aClass2_888;
	public int anInt889;
	public int anInt890;
	public int anIntArrayArrayArray891[][][];
	public boolean aBoolean892;
	public int tooltipTimer;
	public int anInt894;
	public static int anInt895;
	public RgbImage cross[];
	public int anInt897;
	public byte aByte898;
	public IsaacCipher aISAAC_899;
	public boolean aBoolean900;
	public byte aByte901;
	public long aLong902;
	public int anInt903;
	public int anInt904;
	public int anInt905;
	public GraphicsBuffer aClass18_906;
	public GraphicsBuffer aClass18_907;
	public GraphicsBuffer aClass18_908;
	public GraphicsBuffer aClass18_909;
	public GraphicsBuffer aClass18_910;
	public GraphicsBuffer backvmid1;
	public GraphicsBuffer aClass18_912;
	public GraphicsBuffer aClass18_913;
	public GraphicsBuffer aClass18_914;
	public int anInt915;
	public int minimapRotation;
	public int anInt917;
	public boolean aBoolean918;
	public boolean aBoolean919;
	public int mapShape2[];
	public int anInt921;
	public int anInt922;
	public static int nodeId = 10;
	public static int portOffset;
	public static boolean aBoolean925 = true;
	public static boolean aBoolean926;
	public boolean aBooleanArray927[];
	public int anInt928;
	public ByteBuffer byteStream1;
	public long aLong930;
	public int anInt931;
	public int anInt932;
	public int anInt933;
	public boolean aBoolean934;
	public int anInt935;
	public byte aByte936;
	public String aString937;
	public int anInt938;
	public int anInt939;
	public int anInt940;
	public int anIntArray941[];
	public int anIntArray942[];
	public int anIntArray943[];
	public int anIntArray944[];
	public int anIntArray945[];
	public int anIntArray946[];
	public int anIntArray947[];
	public String aStringArray948[];
	public String aString949;
	public boolean aBoolean950;
	public int anInt951;
	public static int anIntArray952[];
	public boolean aBoolean953;
	public RgbImage hintHeadicons[];
	public int anInt955;
	public byte aByte956;
	public String aString957;
	public String aString958;
	public boolean aBoolean959;
	public int anInt960;
	public int anInt961;
	public static boolean aBoolean962;
	public static boolean aBoolean963 = true;
	public ByteBuffer byteStream2;
	public IndexedImage backbase1;
	public IndexedImage backbase2;
	public IndexedImage backhmid1;
	public int anInt968;
	public int anInt969;
	public Player sessionPlayers[];
	public int anInt971;
	public int sessionPlayerList[];
	public int sessionNpcsAwaitingUpdate;
	public int anIntArray974[];
	public ByteBuffer playerUpdateStreams[];
	public IndexedImage sideicons[];
	public int anInt977;
	public static int anInt978;
	public int menuActionCmd2[];
	public int menuActionCmd3[];
	public int menuActionId[];
	public int menuActionCmd1[];
	public IndexedImage redstone7;
	public IndexedImage redstone8_9;
	public IndexedImage redstone10;
	public IndexedImage redstone13;
	public IndexedImage redstone11_12;
	public int anInt988;
	public int anInt989;
	public int anInt990;
	public int anIntArray991[];
	public int memberDaysLeft;
	public int anInt993;
	public int anInt994;
	public int anInt995;
	public int anInt996;
	public int anInt997;
	public int anInt998;
	public static boolean aBoolean999;
	public int anIntArray1000[];
	public int anIntArray1001[];
	public int anIntArray1002[];
	public int anIntArray1003[];
	public int anInt1004;
	public int anIntArray1005[];
	public int anInt1006;
	public static String aString1007 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public static final int playerBodyRecolours[][] = { { 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193 },
			{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239 },
			{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003 }, { 4626, 11146, 6439, 12, 4758, 10270 },
			{ 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 } };
	public int anInt1009;
	public int anInt1010;
	public int anInt1011;
	public int anInt1012;
	public static int anInt1013;
	public boolean aBoolean1014;
	public int anInt1015;
	public boolean gameAlreadyLoaded;
	public RgbImage aClass50_Sub1_Sub1_Sub1_1017;
	public RgbImage aClass50_Sub1_Sub1_Sub1_1018;
	public int mapShape1[];
	public int anInt1020;
	public int anInt1021;
	public int anInt1022;
	public int anInt1023;
	public ClientSocket aClass17_1024;
	public String aString1026;
	public String aString1027;
	public boolean aBoolean1028;
	public int playerLevels[];
	public int anInt1030;
	public RgbImage mapfunctions[];
	public final int anIntArray1032[] = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	public boolean aBoolean1033;
	public int recoveryQuestionsDate;
	public int anInt1035;
	public RgbImage mapFlag;
	public RgbImage aClass50_Sub1_Sub1_Sub1_1037;
	public boolean aBoolean1038;
	public int anIntArray1039[];
	public int anInt1040;
	public int anInt1041;
	public int anInt1042;
	public int anInt1043;
	public int anInt1044;
	public int anInt1045;
	public boolean repaintRequested;
	public int anInt1047;
	public int anInt1048;
	public static int anInt1049;
	public int minimapLock;
	public int anInt1051;
	public static int anInt1052;
	public int anInt1053;
	public int anIntArray1054[];
	public int anInt1055;
	public int anInt1056;
	public int anInt1057;
	public String aString1058;
	public GameFont smallFont;
	public GameFont normalFont;
	public GameFont boldFont;
	public GameFont questFont;
	public int anInt1063;
	public int anInt1064;
	public boolean menuOpen;
	public byte aByte1066;
	public boolean aBoolean1067;
	public int anInt1068;
	public String aStringArray1069[];
	public boolean aBooleanArray1070[];
	public int loadingStage;
	public int anInt1072;
	public long aLongArray1073[];
	public boolean aBoolean1074;
	public int anInt1075;
	public int anInt1076;
	public int markPosX[];
	public int markPosY[];
	public RgbImage prayerHeadicons[];
	public int anInt1080;
	public int tabInterfaceIds[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	public static int anInt1082;
	public int lastPassChange;
	public int anIntArray1084[];
	public int anIntArray1085[];
	public RgbImage multiwayOverlay;
	public int anInt1087;
	public CRC32 aCRC32_1088;
	public int anInt1089;
	public int anIntArray1090[];
	public int anInt1091;
	public String aString1092;
	public String aString1093;
	public int anInt1094;
	public IndexedImage aClass50_Sub1_Sub1_Sub3_1095;
	public IndexedImage aClass50_Sub1_Sub1_Sub3_1096;
	public boolean invalidHost;
	public boolean aBoolean1098;
	public int anIntArray1099[];
	public static int anInt1100;
	public int anInt1101;
	public RgbImage aClass50_Sub1_Sub1_Sub1_1102;
	public RgbImage aClass50_Sub1_Sub1_Sub1_1103;
	public String aString1104;
	public int anIntArray1105[];
	public int anInt1106;
	public int anInt1107;
	public GraphicsBuffer aClass18_1108;
	public GraphicsBuffer aClass18_1109;
	public GraphicsBuffer aClass18_1110;
	public int anInt1111;
	public int anInt1112;
	public int anInt1113;
	public int anInt1114;
	public int anInt1115;
	public RgbImage compassImage;
	public IndexedImage aClass50_Sub1_Sub1_Sub3Array1117[];
	public int anInt1118;
	public int anInt1119;
	public int anInt1120;
	public int anInt1121;
	public RgbImage minimapImage;
	public int anIntArray1123[];
	public int anIntArray1124[];
	public byte aByteArrayArrayArray1125[][][];
	public int anInt1126;
	public boolean aBoolean1127;
	public int anInt1128;
	public int anInt1129;
	public long aLongArray1130[];
	public ByteBuffer byteStream3;
	public NPC sessionNpcs[];
	public int sessionNpcCount;
	public int sessionNpcList[];
	public int anInt1135;
	public boolean aBoolean1136;
	public boolean loggedIn;
	public int anInt1138;
	public static int anInt1139;
	public int anInt1140;
	public long aLong1141;
	public IndexedImage aClass50_Sub1_Sub1_Sub3Array1142[];
	public byte aByte1143;
	public boolean aBoolean1144;
	public int anIntArray1145[];
	public int itemSelected;
	public int anInt1147;
	public int anInt1148;
	public int anInt1149;
	public String selectedItemName;
	public int anInt1151;
	public int anInt1152;
	public IndexedImage mapscenes[];
	public int anInt1154;
	public boolean aBoolean1155;
	public GraphicsBuffer tabBackDrawingArea;
	public GraphicsBuffer mapDrawingArea;
	public GraphicsBuffer gameScreenDrawingArea;
	public GraphicsBuffer aClass18_1159;
	public static int anInt1160;
	public byte aByte1161;
	public static int anInt1162;
	public boolean aBoolean1163;
	public Class22 aClass22_1164;
	public static int anInt1165;
	public int anIntArray1166[];
	public static Player sessionPlayer;
	public static int anInt1168;
	public int anInt1169;
	public int anInt1170;
	public int spellSelected;
	public int anInt1172;
	public int spellUsableOn;
	public String spellTooltip;
	public int anInt1175;
	public int anIntArray1176[];
	public int anIntArray1177[];
	public int anInt1178;
	public int anInt1179;
	public int compassShape1[];
	public boolean tabRepaintRequested;
	public RgbImage hitmarks[];
	public int menuActionIndex;
	public String menuActionName[];
	public IndexedImage invback;
	public IndexedImage mapback;
	public IndexedImage chatback;
	public ByteBuffer byteStream4;
	public int anIntArrayArray1189[][];
	public static boolean aBoolean1190 = true;
	public int anInt1191;
	public RgbImage mapDotItem;
	public RgbImage mapDotNpc;
	public RgbImage mapDotPlayer;
	public RgbImage mapDotFriend;
	public RgbImage mapDotTeam;
	public int anInt1197;
	public GraphicsBuffer aClass18_1198;
	public GraphicsBuffer aClass18_1199;
	public GraphicsBuffer aClass18_1200;
	public GraphicsBuffer aClass18_1201;
	public GraphicsBuffer aClass18_1202;
	public GraphicsBuffer aClass18_1203;
	public GraphicsBuffer aClass18_1204;
	public GraphicsBuffer aClass18_1205;
	public GraphicsBuffer aClass18_1206;
	public static boolean aBoolean1207;
	public int anInt1208;
	public boolean aBoolean1209;
	public NodeList aClass6_1210;
	public boolean aBoolean1211;
	public boolean aBoolean1212;
	public int flashingSidebarId;
	public static int anIntArray1214[];
	public int anInt1215;
	public int anInt1216;
	public int anInt1217;
	public int anInt1218;
	public int anInt1219;
	public int anInt1220;
	public int anInt1221;
	public int anInt1222;
	public int anInt1223;
	public Socket aSocket1224;
	public int anInt1225;
	public int anInt1226;
	public int anInt1227;
	public Class23 aClass23Array1228[];
	public long aLong1229;
	public static int anInt1230;
	public int anInt1231;
	public byte aByteArrayArray1232[][];
	public int minimapZoom;
	public int anInt1234;
	public static int anInt1235;
	public int anInt1236;
	public static int anInt1237;
	public int anInt1238;
	public boolean aBoolean1239;
	public boolean aBoolean1240;
	public int anInt1241;
	public static boolean aBoolean1242 = true;
	public volatile boolean aBoolean1243;
	public int anInt1244;
	public byte aByteArray1245[];
	public int anInt1246;
	public RgbImage mapedge;
	public Class7 aClass7_1248;
	public GameInterface aClass13_1249;
	public long aLong1250;
	public int anInt1251;
	public int cameraX;
	public int anInt1253;
	public int anInt1254;
	public int anInt1255;
	public int anInt1256;
	public final int anInt1257 = 100;
	public int anIntArray1258[];
	public int anIntArray1259[];
	public Class46 aClass46Array1260[];
	public NodeList aClass6_1261;
	public int anInt1262;
	public int anInt1263;
	public int anInt1264;
	public boolean aBoolean1265;
	public boolean aBoolean1266;
	public int anIntArray1267[];
	public static final int skinColours[] = { 9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486 };
	public int anInt1269;
	public int anInt1270;
	public boolean aBoolean1271;
	public int anInt1272;
	public int websiteMessages;
	public boolean aBoolean1274;
	public boolean aBoolean1275;
	public int anInt1276;
	public boolean aBoolean1277;
	public RgbImage mapMarkImage[];
	public int anInt1279;
	public int anInt1280;
	public int anInt1281;
	public NodeList aClass6_1282;
	public boolean loadingError;
	public int anInt1284;
	public int selectedTab;
	public int compassShape2[];
	public int anInt1287;
	public RgbImage pkHeadicons[];
	public int anInt1289;
	public int anIntArray1290[] = { 17, 24, 34, 40 };
	public Class32_Sub1 aClass32_Sub1_1291;
	public IndexedImage aClass50_Sub1_Sub1_Sub3_1292;
	public IndexedImage aClass50_Sub1_Sub1_Sub3_1293;
	public int anInt1294;
	public int anIntArray1295[];
	public int anIntArray1296[];
	public String aStringArray1297[];
	public String aStringArray1298[];
	public int anInt1299;
	public int anInt1300;
	public boolean aBoolean1301;
	public int anInt1302;
	public int anInt1303;
	public int menuScreenArea;
	public int anInt1305;
	public int anInt1306;
	public int anInt1307;
	public int anInt1308;
	public static int drawCycle;
	public int anIntArray1310[];
	public int anIntArray1311[];
	public int anIntArray1312[];
	public int anIntArray1313[];
	public volatile boolean aBoolean1314;
	public int anInt1315;
	public static BigInteger aBigInteger1316 = new BigInteger("58778699976184461502525193738213253649000149147835990136706041084440742975821");
	public byte aByte1317;
	public int anInt1318;
	public int anInt1319;
	public volatile boolean aBoolean1320;
	public int anIntArray1321[];
	public int anInt1322;
	public NodeList aClass6ArrayArrayArray1323[][][];
	public int playerRunEnergy;
	public static int currentTime;
	public int anIntArray1326[];
	public int anInt1327;
	public int anInt1328;
	public int anInt1329;
	public int anInt1330;
	public int anInt1331;
	public int anInt1332;
	public static int anInt1333;

	static {
		anIntArray952 = new int[99];
		int i = 0;
		for(int j = 0; j < 99; j++) {
			int l = j + 1;
			int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
			i += i1;
			anIntArray952[j] = i / 4;
		}

		anIntArray1214 = new int[32];
		i = 2;
		for(int k = 0; k < 32; k++) {
			anIntArray1214[k] = i - 1;
			i += i;
		}

	}
}
