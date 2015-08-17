package com.runescape.entity;

import com.runescape.Class14;
import com.runescape.Class21;
import com.runescape.Class27;
import com.runescape.Client;
import com.runescape.IdentityKit;
import com.runescape.ItemDefinition;
import com.runescape.MemCache;
import com.runescape.Model;
import com.runescape.NameUtils;
import com.runescape.entity.def.NPCDefinition;
import com.runescape.io.ByteBuffer;

public class Player extends Mob {

	public Model getChatHead(boolean flag) {
		if(!visible)
			return null;
		if(npcTransformation != null)
			return npcTransformation.method359(858);
		boolean flag1 = false;
		for(int i = 0; i < 12; i++) {
			int j = appearanceModels[i];
			if(j >= 256 && j < 512 && !IdentityKit.cache[j - 256].method438(-10584))
				flag1 = true;
			if(j >= 512 && !ItemDefinition.forId(j - 512).isChatModelCached(gender))
				flag1 = true;
		}

		if(flag1)
			return null;
		Model aclass50_sub1_sub4_sub4[] = new Model[12];
		int k = 0;
		for(int l = 0; l < 12; l++) {
			int i1 = appearanceModels[l];
			if(i1 >= 256 && i1 < 512) {
				Model class50_sub1_sub4_sub4_1 = IdentityKit.cache[i1 - 256].method439();
				if(class50_sub1_sub4_sub4_1 != null)
					aclass50_sub1_sub4_sub4[k++] = class50_sub1_sub4_sub4_1;
			}
			if(i1 >= 512) {
				Model class50_sub1_sub4_sub4_2 = ItemDefinition.forId(i1 - 512).getChatModel(gender);
				if(class50_sub1_sub4_sub4_2 != null)
					aclass50_sub1_sub4_sub4[k++] = class50_sub1_sub4_sub4_2;
			}
		}

		Model chatHeadModel = new Model(k, aclass50_sub1_sub4_sub4);
		if(!flag)
			throw new NullPointerException();
		for(int j1 = 0; j1 < 5; j1++)
			if(appearanceColours[j1] != 0) {
				chatHeadModel.recolour(Client.playerBodyRecolours[j1][0], Client.playerBodyRecolours[j1][appearanceColours[j1]]);
				if(j1 == 1)
					chatHeadModel.recolour(Client.skinColours[0], Client.skinColours[appearanceColours[j1]]);
			}

		return chatHeadModel;
	}

	public Model getModel(byte byte0) {
		if(npcTransformation != null) {
			int i = -1;
			if(super.animation >= 0 && super.animationDelay == 0)
				i = Class14.aClass14Array293[super.animation].anIntArray295[super.anInt1625];
			else if(super.anInt1588 >= 0)
				i = Class14.aClass14Array293[super.anInt1588].anIntArray295[super.anInt1589];
			Model class50_sub1_sub4_sub4 = npcTransformation.method362(i, -1, 0, null);
			return class50_sub1_sub4_sub4;
		}
		long l = aLong1754;
		int j = -1;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
		if(byte0 != 122)
			aBoolean1767 = !aBoolean1767;
		if(super.animation >= 0 && super.animationDelay == 0) {
			Class14 class14 = Class14.aClass14Array293[super.animation];
			j = class14.anIntArray295[super.anInt1625];
			if(super.anInt1588 >= 0 && super.anInt1588 != super.standAnim)
				k = Class14.aClass14Array293[super.anInt1588].anIntArray295[super.anInt1589];
			if(class14.anInt302 >= 0) {
				i1 = class14.anInt302;
				l += i1 - appearanceModels[5] << 40;
			}
			if(class14.anInt303 >= 0) {
				j1 = class14.anInt303;
				l += j1 - appearanceModels[3] << 48;
			}
		} else if(super.anInt1588 >= 0)
			j = Class14.aClass14Array293[super.anInt1588].anIntArray295[super.anInt1589];
		Model cachedModel = (Model) modelCache.get(l);
		if(cachedModel == null) {
			boolean flag = false;
			for(int k1 = 0; k1 < 12; k1++) {
				int i2 = appearanceModels[k1];
				if(j1 >= 0 && k1 == 3)
					i2 = j1;
				if(i1 >= 0 && k1 == 5)
					i2 = i1;
				if(i2 >= 256 && i2 < 512 && !IdentityKit.cache[i2 - 256].method436(256))
					flag = true;
				if(i2 >= 512 && !ItemDefinition.forId(i2 - 512).isEquippedModelCached(gender))
					flag = true;
			}

			if(flag) {
				if(aLong1749 != -1L)
					cachedModel = (Model) modelCache.get(aLong1749);
				if(cachedModel == null)
					return null;
			}
		}
		if(cachedModel == null) {
			Model models[] = new Model[12];
			int modelCount = 0;
			for(int j2 = 0; j2 < 12; j2++) {
				int k2 = appearanceModels[j2];
				if(j1 >= 0 && j2 == 3)
					k2 = j1;
				if(i1 >= 0 && j2 == 5)
					k2 = i1;
				if(k2 >= 256 && k2 < 512) {
					Model model = IdentityKit.cache[k2 - 256].getBodyModel();
					if(model != null)
						models[modelCount++] = model;
				}
				if(k2 >= 512) {
					Model model = ItemDefinition.forId(k2 - 512).getEquippedModel(gender);
					if(model != null)
						models[modelCount++] = model;
				}
			}

			cachedModel = new Model(modelCount, models);
			for(int l2 = 0; l2 < 5; l2++)
				if(appearanceColours[l2] != 0) {
					cachedModel.recolour(Client.playerBodyRecolours[l2][0], Client.playerBodyRecolours[l2][appearanceColours[l2]]);
					if(l2 == 1)
						cachedModel.recolour(Client.skinColours[0], Client.skinColours[appearanceColours[l2]]);
				}

			cachedModel.createBones();
			cachedModel.light(64, 850, -30, -50, -30, true);
			modelCache.put(cachedModel, l, 5);
			aLong1749 = l;
		}
		if(aBoolean1763)
			return cachedModel;
		Model model = Model.aClass50_Sub1_Sub4_Sub4_1643;
		model.method579(Class21.method239(j) & Class21.method239(k), cachedModel, 1244);
		if(j != -1 && k != -1)
			model.method586(k, 0, j, Class14.aClass14Array293[super.animation].anIntArray299);
		else if(j != -1)
			model.method585(j);
		model.method581();
		model.anIntArrayArray1679 = null;
		model.anIntArrayArray1678 = null;
		return model;
	}

	public boolean isVisible() {
		return visible;
	}

	public Model getRotatedModel() {
		if(!visible)
			return null;
		Model model = getModel((byte) 122);
		if(model == null)
			return null;
		super.height = model.modelHeight;
		model.fitsOnSingleTile = true;
		if(aBoolean1763)
			return model;
		if(super.currentGfx != -1 && super.currentAnim != -1) {
			Class27 class27 = Class27.aClass27Array554[super.currentGfx];
			Model class50_sub1_sub4_sub4_2 = class27.method307();
			if(class50_sub1_sub4_sub4_2 != null) {
				Model class50_sub1_sub4_sub4_3 = new Model(false, false, true, class50_sub1_sub4_sub4_2, Class21.method239(super.currentAnim));
				class50_sub1_sub4_sub4_3.method590(0, 0, false, -super.anInt1618);
				class50_sub1_sub4_sub4_3.createBones();
				class50_sub1_sub4_sub4_3.method585(class27.aClass14_558.anIntArray295[super.currentAnim]);
				class50_sub1_sub4_sub4_3.anIntArrayArray1679 = null;
				class50_sub1_sub4_sub4_3.anIntArrayArray1678 = null;
				if(class27.anInt561 != 128 || class27.anInt562 != 128)
					class50_sub1_sub4_sub4_3.method593(class27.anInt562, class27.anInt561, 9, class27.anInt561);
				class50_sub1_sub4_sub4_3.light(64 + class27.anInt564, 850 + class27.anInt565, -30, -50, -30, true);
				Model aclass50_sub1_sub4_sub4_1[] = { model, class50_sub1_sub4_sub4_3 };
				model = new Model(2, true, 0, aclass50_sub1_sub4_sub4_1);
			}
		}
		if(unknownModel != null) {
			if(Client.currentTime >= anInt1765)
				unknownModel = null;
			if(Client.currentTime >= anInt1764 && Client.currentTime < anInt1765) {
				Model class50_sub1_sub4_sub4_1 = unknownModel;
				class50_sub1_sub4_sub4_1.method590(anInt1743 - super.anInt1610, anInt1745 - super.anInt1611, false, anInt1744 - anInt1750);
				if(super.anInt1584 == 512) {
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
				} else if(super.anInt1584 == 1024) {
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
				} else if(super.anInt1584 == 1536)
					class50_sub1_sub4_sub4_1.method588(true);
				Model aclass50_sub1_sub4_sub4[] = { model, class50_sub1_sub4_sub4_1 };
				model = new Model(2, true, 0, aclass50_sub1_sub4_sub4);
				if(super.anInt1584 == 512)
					class50_sub1_sub4_sub4_1.method588(true);
				else if(super.anInt1584 == 1024) {
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
				} else if(super.anInt1584 == 1536) {
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
					class50_sub1_sub4_sub4_1.method588(true);
				}
				class50_sub1_sub4_sub4_1.method590(super.anInt1610 - anInt1743, super.anInt1611 - anInt1745, false, anInt1750 - anInt1744);
			}
		}
		model.fitsOnSingleTile = true;
		return model;
	}

	public void updateAppearance(ByteBuffer byteStream, int i) {
		byteStream.position = 0;
		gender = byteStream.getUnsignedByte();
		pkIcon = byteStream.getByte();
		prayerIcon = byteStream.getByte();
		npcTransformation = null;
		teamId = 0;
		for(int j = 0; j < 12; j++) {
			int k = byteStream.getUnsignedByte();
			if(k == 0) {
				appearanceModels[j] = 0;
				continue;
			}
			int i1 = byteStream.getUnsignedByte();
			appearanceModels[j] = (k << 8) + i1;
			if(j == 0 && appearanceModels[0] == 65535) {
				npcTransformation = NPCDefinition.forId(byteStream.getUnsignedShort());
				break;
			}
			if(appearanceModels[j] >= 512 && appearanceModels[j] - 512 < ItemDefinition.itemCount) {
				int l1 = ItemDefinition.forId(appearanceModels[j] - 512).teamId;
				if(l1 != 0)
					teamId = l1;
			}
		}

		for(int l = 0; l < 5; l++) {
			int j1 = byteStream.getUnsignedByte();
			if(j1 < 0 || j1 >= Client.playerBodyRecolours[l].length)
				j1 = 0;
			appearanceColours[l] = j1;
		}

		super.standAnim = byteStream.getUnsignedShort();
		if(super.standAnim == 65535)
			super.standAnim = -1;
		super.standTurnAnim = byteStream.getUnsignedShort();
		if(super.standTurnAnim == 65535)
			super.standTurnAnim = -1;
		super.walkAnim = byteStream.getUnsignedShort();
		if(super.walkAnim == 65535)
			super.walkAnim = -1;
		super.turn180Anim = byteStream.getUnsignedShort();
		if(super.turn180Anim == 65535)
			super.turn180Anim = -1;
		super.turn90CWAnim = byteStream.getUnsignedShort();
		if(super.turn90CWAnim == 65535)
			super.turn90CWAnim = -1;
		super.turn90CCWAnim = byteStream.getUnsignedShort();
		if(super.turn90CCWAnim == 65535)
			super.turn90CCWAnim = -1;
		super.runAnim = byteStream.getUnsignedShort();
		if(super.runAnim == 65535)
			super.runAnim = -1;
		//username = NameUtils.formatName(NameUtils.longToName(byteStream.getLong()));
		username = NameUtils.formatName(byteStream.getRS2String());
		System.out.println(username);
		combatLevel = byteStream.getUnsignedByte();
		totalLevel = byteStream.getUnsignedShort();
		visible = true;
		
		aLong1754 = 0L;
		int k1 = appearanceModels[5];
		int i2 = appearanceModels[9];
		if(i != 0)
			return;
		appearanceModels[5] = i2;
		appearanceModels[9] = k1;
		for(int j2 = 0; j2 < 12; j2++) {
			aLong1754 <<= 4;
			if(appearanceModels[j2] >= 256)
				aLong1754 += appearanceModels[j2] - 256;
		}

		if(appearanceModels[0] >= 256)
			aLong1754 += appearanceModels[0] - 256 >> 4;
		if(appearanceModels[1] >= 256)
			aLong1754 += appearanceModels[1] - 256 >> 8;
		appearanceModels[5] = k1;
		appearanceModels[9] = i2;
		for(int k2 = 0; k2 < 5; k2++) {
			aLong1754 <<= 3;
			aLong1754 += appearanceColours[k2];
		}

		aLong1754 <<= 1;
		aLong1754 += gender;
	}

	public Player() {
		aBoolean1747 = false;
		prayerIcon = -1;
		aLong1749 = -1L;
		appearanceModels = new int[12];
		pkIcon = -1;
		visible = false;
		appearanceColours = new int[5];
		aBoolean1762 = true;
		aBoolean1763 = false;
		aBoolean1767 = false;
		anInt1772 = 932;
	}

	public int anInt1743;
	public int anInt1744;
	public int anInt1745;
	public Model unknownModel;
	public boolean aBoolean1747;
	public int prayerIcon;
	public long aLong1749;
	public int anInt1750;
	public String username;
	public int appearanceModels[];
	public int combatLevel;
	public long aLong1754;
	public int gender;
	public int pkIcon;
	public NPCDefinition npcTransformation;
	public boolean visible;
	public int totalLevel;
	public int appearanceColours[];
	public static MemCache modelCache = new MemCache(260);
	public boolean aBoolean1762;
	public boolean aBoolean1763;
	public int anInt1764;
	public int anInt1765;
	public int teamId;
	public boolean aBoolean1767;
	public int anInt1768;
	public int anInt1769;
	public int anInt1770;
	public int anInt1771;
	public int anInt1772;

}
