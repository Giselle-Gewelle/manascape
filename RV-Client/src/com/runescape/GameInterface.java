package com.runescape;

import com.runescape.cache.CacheArchive;
import com.runescape.entity.NPC;
import com.runescape.io.ByteBuffer;

public class GameInterface {

	public static final int 
		TYPE_TOOLTIP = 8,
		TOOLTIP_DELAY = 25; // old = 100
	
	public static NPC chatNPC = null;
	
	public static RgbImage method194(int i, String s, int j) {
		long l = (NameUtils.method301(183, s) << 8) + (long) i;
		if(j <= 0)
			anInt275 = -317;
		RgbImage rgbImage = (RgbImage) aClass33_250.get(l);
		if(rgbImage != null)
			return rgbImage;
		if(aClass2_214 == null)
			return null;
		try {
			rgbImage = new RgbImage(aClass2_214, s, i);
			aClass33_250.put(rgbImage, l, 5);
		} catch(Exception _ex) {
			return null;
		}
		return rgbImage;
	}

	public static GameInterface getInterface(int id) {
		if(interfaceCache[id] == null) {
			ByteBuffer byteStream = new ByteBuffer(interfaceByteCache[id]);
			int childId = byteStream.getUnsignedShort();
			interfaceCache[id] = unpackInterface(id, childId, byteStream);
		}
		return interfaceCache[id];
	}

	public void method196(int i, int j, int k) {
		int l = anIntArray269[k];
		anIntArray269[k] = anIntArray269[i];
		anIntArray269[i] = l;
		l = anIntArray224[k];
		anIntArray224[k] = anIntArray224[i];
		if(j >= 0) {
			return;
		} else {
			anIntArray224[i] = l;
			return;
		}
	}
	
	public Model method197(int i, int j) {
		ItemDefinition class16 = null;
		if(i == 4) {
			class16 = ItemDefinition.forId(j);
			anInt280 += class16.lightIntensity;
			anInt243 += class16.lightMag;
		}
		Model model = (Model) aClass33_264.get((i << 16) + j);
		if(model != null)
			return model;
		if(i == 1)
			model = Model.getModel(j);
		if(i == 2) {
			if(chatNPC != null) {
				if(chatNPC.useNpcDef && chatNPC.npcDef != null) {
					model = chatNPC.npcDef.method359(858);
				} else {
					model = chatNPC.getChatHead(true);
				}
			}
		}
		if(i == 3)
			model = Client.sessionPlayer.getChatHead(true);
		if(i == 4)
			model = class16.method217(50);
		if(i == 5)
			model = null;
		if(model != null)
			aClass33_264.put(model, (i << 16) + j, 5);
		return model;
	}

	public static GameInterface unpackInterface(int id, int parentId, ByteBuffer byteStream) {
		GameInterface gameInterface = new GameInterface();
		gameInterface.id = id;
		gameInterface.parentId = parentId;
		gameInterface.type = byteStream.getUnsignedByte();
		gameInterface.actionType = byteStream.getUnsignedByte();
		gameInterface.contentType = byteStream.getUnsignedShort();
		gameInterface.width = byteStream.getUnsignedShort();
		gameInterface.height = byteStream.getUnsignedShort();
		gameInterface.alpha = (byte) byteStream.getUnsignedByte();
		gameInterface.triggersOnMouseOver = byteStream.getUnsignedByte();

		if(gameInterface.triggersOnMouseOver != 0)
			gameInterface.triggersOnMouseOver = (gameInterface.triggersOnMouseOver - 1 << 8) + byteStream.getUnsignedByte();
		else
			gameInterface.triggersOnMouseOver = -1;
		
		if(gameInterface.contentType == 600)
			anInt246 = parentId;
		if(gameInterface.contentType == 650)
			anInt255 = parentId;
		if(gameInterface.contentType == 655)
			anInt277 = parentId;
		
		int l = byteStream.getUnsignedByte();
		if(l > 0) {
			gameInterface.anIntArray273 = new int[l];
			gameInterface.anIntArray256 = new int[l];
			for(int i1 = 0; i1 < l; i1++) {
				gameInterface.anIntArray273[i1] = byteStream.getUnsignedByte();
				gameInterface.anIntArray256[i1] = byteStream.getUnsignedShort();
			}

		}
		int j1 = byteStream.getUnsignedByte();
		if(j1 > 0) {
			gameInterface.valueIndexArray = new int[j1][];
			for(int k1 = 0; k1 < j1; k1++) {
				int l2 = byteStream.getUnsignedShort();
				gameInterface.valueIndexArray[k1] = new int[l2];
				for(int k4 = 0; k4 < l2; k4++)
					gameInterface.valueIndexArray[k1][k4] = byteStream.getUnsignedShort();

			}

		}
		if(gameInterface.type == 0) {
			gameInterface.scrollHeight = byteStream.getUnsignedShort();
			gameInterface.mouseOverTriggered = byteStream.getUnsignedByte() == 1;
			int childCount = byteStream.getUnsignedShort();
			gameInterface.children = new int[childCount];
			gameInterface.childrenX = new int[childCount];
			gameInterface.childrenY = new int[childCount];
			for(int i = 0; i < childCount; i++) {
				int cid = byteStream.getUnsignedShort();
				// Equipment stats interface hack :\
				if(cid == 18952) {
					cid = 1688;
				}
				gameInterface.children[i] = cid;
				gameInterface.childrenX[i] = byteStream.getShort();
				gameInterface.childrenY[i] = byteStream.getShort();
				//System.out.println("Interface Id: " + gameInterface.id + "; child Id: " + gameInterface.children[i]);
			}
		}
		if(gameInterface.type == 1) {
			gameInterface.anInt225 = byteStream.getUnsignedShort();
			gameInterface.aBoolean233 = byteStream.getUnsignedByte() == 1;
		}
		if(gameInterface.type == 2) {
			gameInterface.anIntArray269 = new int[gameInterface.width * gameInterface.height];
			gameInterface.anIntArray224 = new int[gameInterface.width * gameInterface.height];
			gameInterface.aBoolean274 = byteStream.getUnsignedByte() == 1;
			gameInterface.aBoolean229 = byteStream.getUnsignedByte() == 1;
			gameInterface.aBoolean288 = byteStream.getUnsignedByte() == 1;
			gameInterface.aBoolean217 = byteStream.getUnsignedByte() == 1;
			gameInterface.anInt263 = byteStream.getUnsignedByte();
			gameInterface.anInt244 = byteStream.getUnsignedByte();
			gameInterface.anIntArray221 = new int[20];
			gameInterface.anIntArray213 = new int[20];
			gameInterface.aClass50_Sub1_Sub1_Sub1Array265 = new RgbImage[20];
			for(int i2 = 0; i2 < 20; i2++) {
				int j3 = byteStream.getUnsignedByte();
				if(j3 == 1) {
					gameInterface.anIntArray221[i2] = byteStream.getShort();
					gameInterface.anIntArray213[i2] = byteStream.getShort();
					String s1 = byteStream.getRS2String();
					if(s1.length() > 0) {
						int l4 = s1.lastIndexOf(",");
						gameInterface.aClass50_Sub1_Sub1_Sub1Array265[i2] = method194(Integer.parseInt(s1.substring(l4 + 1)), s1.substring(0, l4), 373);
					}
				}
			}

			gameInterface.aStringArray262 = new String[5];
			for(int k3 = 0; k3 < 5; k3++) {
				gameInterface.aStringArray262[k3] = byteStream.getRS2String();
				if(gameInterface.aStringArray262[k3].length() == 0)
					gameInterface.aStringArray262[k3] = null;
			}

		}
		if(gameInterface.type == 3)
			gameInterface.aBoolean239 = byteStream.getUnsignedByte() == 1;
		if(gameInterface.type == 4 || gameInterface.type == 1) {
			gameInterface.textCentered = byteStream.getUnsignedByte() == 1;
			int fontIndex = byteStream.getUnsignedByte();
			if(fonts != null)
				gameInterface.font = fonts[fontIndex];
			gameInterface.textShadow = byteStream.getUnsignedByte() == 1;
		}
		if(gameInterface.type == 4) {
			gameInterface.text = byteStream.getRS2String();
			gameInterface.aString249 = byteStream.getRS2String();
		}
		if(gameInterface.type == 1 || gameInterface.type == 3 || gameInterface.type == 4)
			gameInterface.textColour = byteStream.getInt();
		if(gameInterface.type == 3 || gameInterface.type == 4) {
			gameInterface.anInt260 = byteStream.getInt();
			gameInterface.anInt261 = byteStream.getInt();
			gameInterface.anInt226 = byteStream.getInt();
		}
		if(gameInterface.type == 5) {
			String s = byteStream.getRS2String();
			if(s.length() > 0) {
				int l3 = s.lastIndexOf(",");
				gameInterface.aClass50_Sub1_Sub1_Sub1_212 = method194(Integer.parseInt(s.substring(l3 + 1)), s.substring(0, l3), 373);
			}
			s = byteStream.getRS2String();
			if(s.length() > 0) {
				int i4 = s.lastIndexOf(",");
				gameInterface.aClass50_Sub1_Sub1_Sub1_245 = method194(Integer.parseInt(s.substring(i4 + 1)), s.substring(0, i4), 373);
			}
		}
		if(gameInterface.type == 6) {
			id = byteStream.getUnsignedByte();
			if(id != 0) {
				gameInterface.anInt283 = 1;
				gameInterface.anInt284 = (id - 1 << 8) + byteStream.getUnsignedByte();
			}
			id = byteStream.getUnsignedByte();
			if(id != 0) {
				gameInterface.anInt266 = 1;
				gameInterface.anInt267 = (id - 1 << 8) + byteStream.getUnsignedByte();
			}
			id = byteStream.getUnsignedByte();
			if(id != 0)
				gameInterface.anInt286 = (id - 1 << 8) + byteStream.getUnsignedByte();
			else
				gameInterface.anInt286 = -1;
			id = byteStream.getUnsignedByte();
			if(id != 0)
				gameInterface.anInt287 = (id - 1 << 8) + byteStream.getUnsignedByte();
			else
				gameInterface.anInt287 = -1;
			gameInterface.anInt251 = byteStream.getUnsignedShort();
			gameInterface.anInt252 = byteStream.getUnsignedShort();
			gameInterface.anInt253 = byteStream.getUnsignedShort();
		}
		if(gameInterface.type == 7) {
			gameInterface.anIntArray269 = new int[gameInterface.width * gameInterface.height];
			gameInterface.anIntArray224 = new int[gameInterface.width * gameInterface.height];
			gameInterface.textCentered = byteStream.getUnsignedByte() == 1;
			int k2 = byteStream.getUnsignedByte();
			if(fonts != null)
				gameInterface.font = fonts[k2];
			gameInterface.textShadow = byteStream.getUnsignedByte() == 1;
			gameInterface.textColour = byteStream.getInt();
			gameInterface.anInt263 = byteStream.getShort();
			gameInterface.anInt244 = byteStream.getShort();
			gameInterface.aBoolean229 = byteStream.getUnsignedByte() == 1;
			gameInterface.aStringArray262 = new String[5];
			for(int j4 = 0; j4 < 5; j4++) {
				gameInterface.aStringArray262[j4] = byteStream.getRS2String();
				if(gameInterface.aStringArray262[j4].length() == 0)
					gameInterface.aStringArray262[j4] = null;
			}

		}
		if(gameInterface.type == 8)
			gameInterface.text = byteStream.getRS2String();
		if(gameInterface.actionType == 2 || gameInterface.type == 2) {
			gameInterface.aString281 = byteStream.getRS2String();
			gameInterface.aString211 = byteStream.getRS2String();
			gameInterface.anInt222 = byteStream.getUnsignedShort();
		}
		if(gameInterface.actionType == 1 || gameInterface.actionType == 4 || gameInterface.actionType == 5 || gameInterface.actionType == 6) {
			gameInterface.aString268 = byteStream.getRS2String();
			if(gameInterface.aString268.length() == 0) {
				if(gameInterface.actionType == 1)
					gameInterface.aString268 = "Ok";
				if(gameInterface.actionType == 4)
					gameInterface.aString268 = "Select";
				if(gameInterface.actionType == 5)
					gameInterface.aString268 = "Select";
				if(gameInterface.actionType == 6)
					gameInterface.aString268 = "Continue";
			}
		}
		return gameInterface;
	}

	public static void unpackAll(GameFont font[], CacheArchive archive, CacheArchive class2_1) {
		aClass33_250 = new MemCache(50000);
		aClass2_214 = class2_1;
		fonts = font;
		int childId = -1;
		ByteBuffer byteStream = new ByteBuffer(archive.getDataForName("data"));
		int interfaceCount = byteStream.getUnsignedShort();
		interfaceCache = new GameInterface[interfaceCount];
		interfaceByteCache = new byte[interfaceCount][];
		while(byteStream.position < byteStream.payload.length) {
			int id = byteStream.getUnsignedShort();
	    	//System.out.println(id);
			if(id == 65535) {
				childId = byteStream.getUnsignedShort();
				id = byteStream.getUnsignedShort();
			}
			int startingOffset = byteStream.position;
			GameInterface gameInterface = unpackInterface(id, childId, byteStream);
			byte interfaceBytes[] = interfaceByteCache[gameInterface.id] = new byte[(byteStream.position - startingOffset) + 2];
			for(int i = startingOffset; i < byteStream.position; i++)
				interfaceBytes[(i - startingOffset) + 2] = byteStream.payload[i];

			interfaceBytes[0] = (byte) (childId >> 8);
			interfaceBytes[1] = (byte) childId;
		}
		aClass2_214 = null;
	}

	public static void method200(boolean flag, int i) {
		if(!flag)
			aBoolean257 = !aBoolean257;
		if(i == -1)
			return;
		for(int j = 0; j < interfaceCache.length; j++)
			if(interfaceCache[j] != null && interfaceCache[j].parentId == i && interfaceCache[j].type != 2)
				interfaceCache[j] = null;

	}

	public static void method201(int i, Model model, int j) {
		aClass33_264.method347();
		if(model != null && i != 4)
			aClass33_264.put(model, (i << 16) + j, 5);
	}

	public static void cleanUp() {
		interfaceCache = null;
		aClass2_214 = null;
		aClass33_250 = null;
		fonts = null;
		interfaceByteCache = null;
	}

	public Model method203(int i, int j, int k, boolean flag) {
		anInt280 = 64;
		anInt243 = 768;
		Model class50_sub1_sub4_sub4;
		if(flag)
			class50_sub1_sub4_sub4 = method197(anInt266, anInt267);
		else
			class50_sub1_sub4_sub4 = method197(anInt283, anInt284);
		if(class50_sub1_sub4_sub4 == null)
			return null;
		if(i == -1 && j == -1 && class50_sub1_sub4_sub4.anIntArray1662 == null)
			return class50_sub1_sub4_sub4;
		Model class50_sub1_sub4_sub4_1 = new Model(false, false, true, class50_sub1_sub4_sub4, Class21.method239(i) & Class21.method239(j));
		if(k != 0)
			aBoolean271 = !aBoolean271;
		if(i != -1 || j != -1)
			class50_sub1_sub4_sub4_1.createBones();
		if(i != -1)
			class50_sub1_sub4_sub4_1.method585(i);
		if(j != -1)
			class50_sub1_sub4_sub4_1.method585(j);
		class50_sub1_sub4_sub4_1.light(anInt280, anInt243, -50, -10, -50, true);
		return class50_sub1_sub4_sub4_1;
	}

	public GameInterface() {
		anInt270 = -68;
		aBoolean271 = true;
	}

	public static int anInt210;
	public String aString211;
	public RgbImage aClass50_Sub1_Sub1_Sub1_212;
	public int anIntArray213[];
	public static CacheArchive aClass2_214;
	public int id;
	public static GameInterface interfaceCache[];
	public boolean aBoolean217;
	public int anInt218;
	public boolean mouseOverTriggered;
	public byte alpha;
	public int anIntArray221[];
	public int anInt222;
	public static GameFont fonts[];
	public int anIntArray224[];
	public int anInt225;
	public int anInt226;
	public int anInt227;
	public int offsetX;
	public boolean aBoolean229;
	public String text;
	public int anInt231;
	public int childrenX[];
	public boolean aBoolean233;
	public int valueIndexArray[][];
	public int anInt235;
	public int type;
	public GameFont font;
	public int height;
	public boolean aBoolean239;
	public int textColour;
	public int width;
	public int contentType;
	public static int anInt243;
	public int anInt244;
	public RgbImage aClass50_Sub1_Sub1_Sub1_245;
	public static int anInt246 = -1;
	public boolean textShadow;
	public int parentId;
	public String aString249;
	public static MemCache aClass33_250;
	public int anInt251;
	public int anInt252;
	public int anInt253;
	public int triggersOnMouseOver;
	public static int anInt255 = -1;
	public int anIntArray256[];
	public static boolean aBoolean257;
	public int children[];
	public int offsetY;
	public int anInt260;
	public int anInt261;
	public String aStringArray262[];
	public int anInt263;
	public static MemCache aClass33_264 = new MemCache(30);
	public RgbImage aClass50_Sub1_Sub1_Sub1Array265[];
	public int anInt266;
	public int anInt267;
	public String aString268;
	public int anIntArray269[];
	public int anInt270;
	public boolean aBoolean271;
	public boolean textCentered;
	public int anIntArray273[];
	public boolean aBoolean274;
	public static int anInt275 = -291;
	public int childrenY[];
	public static int anInt277 = -1;
	public static boolean aBoolean278 = true;
	public static int anInt279 = 373;
	public static int anInt280;
	public String aString281;
	/**
	 * Not sure on the naming entirely for this one... Looks like what I named it, though.
	 */
	public static byte interfaceByteCache[][];
	public int anInt283;
	public int anInt284;
	public int scrollHeight;
	public int anInt286;
	public int anInt287;
	public boolean aBoolean288;
	public int actionType;

}
