package com.runescape.entity;

import com.runescape.Class14;
import com.runescape.Class21;
import com.runescape.Class27;
import com.runescape.Client;
import com.runescape.IdentityKit;
import com.runescape.ItemDefinition;
import com.runescape.MemCache;
import com.runescape.Model;
import com.runescape.entity.def.NPCDefinition;
import com.runescape.io.ByteBuffer;

public class NPC extends Mob {

	public Model getNpcDefModel() {
		if(super.animation >= 0 && super.animationDelay == 0) {
			int i = Class14.aClass14Array293[super.animation].anIntArray295[super.anInt1625];
			int k = -1;
			if(super.anInt1588 >= 0 && super.anInt1588 != super.standAnim)
				k = Class14.aClass14Array293[super.anInt1588].anIntArray295[super.anInt1589];
			return npcDef.method362(i, k, 0, Class14.aClass14Array293[super.animation].anIntArray299);
		}
		int j = -1;
		if(super.anInt1588 >= 0)
			j = Class14.aClass14Array293[super.anInt1588].anIntArray295[super.anInt1589];
		return npcDef.method362(j, -1, 0, null);
	}

	public Model getRotatedNpcDefModel() {
		if(npcDef == null)
			return null;
		Model model = getNpcDefModel();
		if(model == null)
			return null;
		super.height = model.modelHeight;
		if(super.currentGfx != -1 && super.currentAnim != -1) {
			// Anim stuff... Blah.
			Class27 class27 = Class27.aClass27Array554[super.currentGfx];
			Model class50_sub1_sub4_sub4_1 = class27.method307();
			if(class50_sub1_sub4_sub4_1 != null) {
				int i = class27.aClass14_558.anIntArray295[super.currentAnim];
				Model class50_sub1_sub4_sub4_2 = new Model(false, false, true, class50_sub1_sub4_sub4_1, Class21.method239(i));
				class50_sub1_sub4_sub4_2.method590(0, 0, false, -super.anInt1618);
				class50_sub1_sub4_sub4_2.createBones();
				class50_sub1_sub4_sub4_2.method585(i);
				class50_sub1_sub4_sub4_2.anIntArrayArray1679 = null;
				class50_sub1_sub4_sub4_2.anIntArrayArray1678 = null;
				if(class27.anInt561 != 128 || class27.anInt562 != 128)
					class50_sub1_sub4_sub4_2.method593(class27.anInt562, class27.anInt561, 9, class27.anInt561);
				class50_sub1_sub4_sub4_2.light(64 + class27.anInt564, 850 + class27.anInt565, -30, -50, -30, true);
				Model aclass50_sub1_sub4_sub4[] = { model, class50_sub1_sub4_sub4_2 };
				model = new Model(2, true, 0, aclass50_sub1_sub4_sub4);
			}
		}
		if(npcDef.boundDim == 1)
			model.fitsOnSingleTile = true;
		return model;
	}

	public boolean isVisible() {
		if(useNpcDef) {
			return npcDef != null;
		}
		return true;
	}

	public NPC() {
		appearanceModels = new int[12];
		appearanceColours = new int[5];
		aLong1749 = -1L;
		pkIcon = -1;
		prayerIcon = -1;
		useNpcDef = true;
		name = null;
		description = "n/a";
		combatLevel = -1;
		actions = null;
		clickable = true;
		id = 0;
	}

	public NPCDefinition npcDef;
	public int appearanceModels[];
	public static MemCache modelCache = new MemCache(260);
	public long appearanceHash;
	public int appearanceColours[];
	public long aLong1749;
	public int gender;
	public int pkIcon;
	public int prayerIcon;
	public boolean useNpcDef;
	public String name;
	public String description;
	public int combatLevel;
	public String[] actions;
	public boolean clickable;
	public int id;

	public Model getModel() {
		long l = appearanceHash;
		int j = -1;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
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

	public Model getRotatedModel() {
		if(useNpcDef)
			return getRotatedNpcDefModel();
		
		if(!isVisible())
			return null;
		
		Model model = getModel();
		if(model == null)
			return null;
		
		super.height = model.modelHeight;
		model.fitsOnSingleTile = true;
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
		model.fitsOnSingleTile = true;
		return model;
	}

	public void updateAppearance(ByteBuffer buffer) {
		buffer.position = 0;
		
		useNpcDef = buffer.getUnsignedByte() == 1;
		if(useNpcDef) {
			return;
		}
		
		gender = buffer.getUnsignedByte();
		clickable = buffer.getUnsignedByte() == 1;
		pkIcon = buffer.getByte();
		prayerIcon = buffer.getByte();
		for(int j = 0; j < 12; j++) {
			int k = buffer.getUnsignedByte();
			if(k == 0) {
				appearanceModels[j] = 0;
				continue;
			}
			int i1 = buffer.getUnsignedByte();
			appearanceModels[j] = (k << 8) + i1;
		}

		for(int l = 0; l < 5; l++) {
			int j1 = buffer.getUnsignedByte();
			if(j1 < 0 || j1 >= Client.playerBodyRecolours[l].length)
				j1 = 0;
			appearanceColours[l] = j1;
		}

		super.standAnim = buffer.getUnsignedShort();
		if(super.standAnim == 65535)
			super.standAnim = -1;
		super.standTurnAnim = buffer.getUnsignedShort();
		if(super.standTurnAnim == 65535)
			super.standTurnAnim = -1;
		super.walkAnim = buffer.getUnsignedShort();
		if(super.walkAnim == 65535)
			super.walkAnim = -1;
		super.turn180Anim = buffer.getUnsignedShort();
		if(super.turn180Anim == 65535)
			super.turn180Anim = -1;
		super.turn90CWAnim = buffer.getUnsignedShort();
		if(super.turn90CWAnim == 65535)
			super.turn90CWAnim = -1;
		super.turn90CCWAnim = buffer.getUnsignedShort();
		if(super.turn90CCWAnim == 65535)
			super.turn90CCWAnim = -1;
		super.runAnim = buffer.getUnsignedShort();
		if(super.runAnim == 65535)
			super.runAnim = -1;
		
		name = buffer.getRS2String();
		description = buffer.getRS2String();
		combatLevel = buffer.getUnsignedByte();
		
		int actionCount = buffer.getUnsignedByte();
		if(actionCount > 0) {
			actions = new String[actionCount];
			for(int i = 0; i < actionCount; i++) {
				actions[i] = buffer.getRS2String();
				if(actions[i].equals("n/a")) {
					actions[i] = null;
				}
			}
		}
		
		appearanceHash = 0L;
		int k1 = appearanceModels[5];
		int i2 = appearanceModels[9];
		appearanceModels[5] = i2;
		appearanceModels[9] = k1;
		for(int j2 = 0; j2 < 12; j2++) {
			appearanceHash <<= 4;
			if(appearanceModels[j2] >= 256)
				appearanceHash += appearanceModels[j2] - 256;
		}

		if(appearanceModels[0] >= 256)
			appearanceHash += appearanceModels[0] - 256 >> 4;
		if(appearanceModels[1] >= 256)
			appearanceHash += appearanceModels[1] - 256 >> 8;
		appearanceModels[5] = k1;
		appearanceModels[9] = i2;
		for(int k2 = 0; k2 < 5; k2++) {
			appearanceHash <<= 3;
			appearanceHash += appearanceColours[k2];
		}

		appearanceHash <<= 1;
		appearanceHash += gender;
	}
	
	public Model getChatHead(boolean flag) {
		if(!isVisible())
			return null;
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
	
}
