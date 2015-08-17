package com.runescape.entity.def;

import java.io.*;

import com.runescape.Class21;
import com.runescape.Class49;
import com.runescape.Client;
import com.runescape.MemCache;
import com.runescape.Model;
import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;

public class NPCDefinition {

	public static NPCDefinition forId(int npcId) {
		for(int j = 0; j < 20; j++) {
			if(npcCache[j].id == (long) npcId) {
				return npcCache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		NPCDefinition npcDef = npcCache[cacheIndex] = new NPCDefinition();
		npcData.position = npcOffsets[npcId];
		npcDef.id = npcId;
		npcDef.readValues(npcData);

		/*
		 * if(npcId == 178) { for(int j = 0; j < npcDef.anIntArray634.length;
		 * j++) { System.out.println(npcDef.anIntArray634[j] + " to " +
		 * npcDef.anIntArray656[j]); } for(int j = 0; j < npcDef.models.length;
		 * j++) { System.out.println("model " + j + ": " + npcDef.models[j]); }
		 * }
		 */
		
		if(npcId == 3852) {
			npcDef.name = "Commander Adriea";
			npcDef.description = new String("A commander under the service of Saradomin himself.").getBytes();
			npcDef.combatLevel = 126;
			
			npcDef.idleAnim = 808;
			npcDef.walkAnim = 819;
			npcDef.turn180Anim = 820;
			npcDef.turn90CWAnim = 821;
			npcDef.turn90CCWAnim = 822;
			npcDef.degreesToTurn = 32;
			
			npcDef.actions = new String[5];
			npcDef.actions[0] = "Talk-to";
			
			npcDef.models = new int[11];
			// head
			npcDef.models[0] = 414;
			npcDef.models[10] = 390;
			
			// body
			npcDef.models[1] = 3383;
			npcDef.models[2] = 344;
			
			// gloves
			npcDef.models[3] = 356;
			
			// weapon
			npcDef.models[4] = 546;
			
			// legs
			npcDef.models[5] = 432;
			
			// boots
			npcDef.models[6] = 5031;
			
			// shield
			npcDef.models[7] = 517;
			
			// cape
			npcDef.models[8] = 481;
			
			// chest symbol
			npcDef.models[9] = 3330;
			
			
			npcDef.chatModels = new int[2];
			npcDef.chatModels[0] = 113;
			npcDef.chatModels[1] = 131;
			

			npcDef.recolorOriginal = new int[14];
			npcDef.recolorNew = new int[14];
			
			npcDef.recolorOriginal[0] = 24;
			npcDef.recolorNew[0] = 20;
			npcDef.recolorOriginal[1] = 61;
			npcDef.recolorNew[1] = 99;
			npcDef.recolorOriginal[2] = 41;
			npcDef.recolorNew[2] = 82;
			npcDef.recolorOriginal[3] = 11187;
			npcDef.recolorNew[3] = 20;
			npcDef.recolorOriginal[4] = 57;
			npcDef.recolorNew[4] = 20;
			npcDef.recolorOriginal[5] = 5400;
			npcDef.recolorNew[5] = 61;
			npcDef.recolorOriginal[6] = 5648;
			npcDef.recolorNew[6] = 99;
			npcDef.recolorOriginal[7] = 10004;
			npcDef.recolorNew[7] = 82;
			
			// shield
			npcDef.recolorOriginal[8] = 7054;
			npcDef.recolorNew[8] = 82;
			
			// cape
			npcDef.recolorOriginal[9] = 926;
			npcDef.recolorNew[9] = 20;
			npcDef.recolorOriginal[10] = 7700;
			npcDef.recolorNew[10] = 43943;
			npcDef.recolorOriginal[11] = 11200;
			npcDef.recolorNew[11] = 99;
			npcDef.recolorOriginal[12] = 6032;
			npcDef.recolorNew[12] = 20;
			
			// hair
			npcDef.recolorOriginal[13] = 6798;
			npcDef.recolorNew[13] = 6852;
		}

		return npcDef;
	}

	public static void unpack(CacheArchive class2) {
		npcData = new ByteBuffer(class2.getDataForName("npc.dat"));
		ByteBuffer byteStream = new ByteBuffer(class2.getDataForName("npc.idx"));
		
		/*byte[] data = new byte[1000000];
		byte[] idxData = new byte[1000000];
		boolean read = true;
		int index = 0;
		int idxIndex = 0;
		try {
			DataInputStream stream = new DataInputStream(new FileInputStream("npc.dat"));
			do {
				try {
					data[index++] = stream.readByte();
				} catch(EOFException eof) {
					read = false;
					stream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			} while(read);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		read = true;
		try {
			DataInputStream stream = new DataInputStream(new FileInputStream("npc.idx"));
			do {
				try {
					idxData[idxIndex++] = stream.readByte();
				} catch(EOFException eof) {
					read = false;
					stream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			} while(read);
		} catch(IOException e) {
			e.printStackTrace();
		}
		byte[] newData = new byte[index];
		System.arraycopy(data, 0, newData, 0, index);
		//npcData = new ByteBuffer(newData);
		
		byte[] newIdxData = new byte[idxIndex];
		System.arraycopy(idxData, 0, newIdxData, 0, idxIndex);
		//byteStream = new ByteBuffer(newIdxData);*/
		
		System.out.println("npc idx = " + byteStream.payload.length);
		System.out.println("npc dat = " + npcData.payload.length);
		npcCount = byteStream.getUnsignedShort();
		npcOffsets = new int[npcCount + 500];
		int i = 2;
		for(int j = 0; j < npcCount; j++) {
			npcOffsets[j] = i;
			i += byteStream.getUnsignedShort();
		}

		npcCache = new NPCDefinition[20];
		for(int k = 0; k < 20; k++) {
			npcCache[k] = new NPCDefinition();
		}
	}
	
	public static void writeString(final DataOutputStream dos, final String toWrite) {
		try {
			dos.write(toWrite.getBytes());
			dos.writeByte(10);
		} catch (final IOException ioe) {
		}
	}
	
	public static void repack() throws IOException {
		ByteBuffer idx = ByteBuffer.create(100000000);
		ByteBuffer dat = ByteBuffer.create(100000000);
		//DataOutputStream idx = new DataOutputStream(new FileOutputStream("npc.idx"));
		//DataOutputStream dat = new DataOutputStream(new FileOutputStream("npc.dat"));
		int npcCount = 3853;
		idx.putShort(npcCount);
		dat.putShort(npcCount);

		ByteBuffer[] buffers = new ByteBuffer[npcCount + 1];
		for(int i = 0; i < npcCount + 1; i++) {
			//int start = dat.size();
			
			//NPCDefinition def = forId2(dat, i);
			
			NPCDefinition def = forId(i);
			
			ByteBuffer npcBuffer = ByteBuffer.create();

			if(def.models != null && def.models.length > 0) {
				npcBuffer.putByte(1);
				npcBuffer.putByte(def.models.length);
				for(int m = 0; m < def.models.length; m++) {
					npcBuffer.putShort(def.models[m]);
				}
			}
			
			if(def.name != null) {
				npcBuffer.putByte(2);
				//writeString(dat, def.name);
				npcBuffer.writeString(def.name);
			}
			
			if(def.description != null) {
				npcBuffer.putByte(3);
				//writeString(dat, new String(def.description));
				npcBuffer.writeString(new String(def.description));
			}
			
			if(def.boundDim != 1) {
				npcBuffer.putByte(12);
				npcBuffer.putByte(def.boundDim);
			}
			
			if(def.idleAnim != -1) {
				npcBuffer.putByte(13);
				npcBuffer.putShort(def.idleAnim);
			}
			
			if(def.turn180Anim != -1 || def.turn90CWAnim != -1 || def.turn90CCWAnim != -1) {
				npcBuffer.putByte(17);
				if(def.walkAnim != -1) {
					npcBuffer.putShort(def.walkAnim);
				} else {
					npcBuffer.putShort(65535);
				}
				if(def.turn180Anim != -1) {
					npcBuffer.putShort(def.turn180Anim);
				} else {
					npcBuffer.putShort(65535);
				}
				if(def.turn90CWAnim != -1) {
					npcBuffer.putShort(def.turn90CWAnim);
				} else {
					npcBuffer.putShort(65535);
				}
				if(def.turn90CCWAnim != -1) {
					npcBuffer.putShort(def.turn90CCWAnim);
				} else {
					npcBuffer.putShort(65535);
				}
			} else if(def.walkAnim != -1) {
				npcBuffer.putByte(14);
				npcBuffer.putShort(def.walkAnim);
			}
			
			if(def.actions != null && def.actions.length > 0) {
				for(int a = 0; a < def.actions.length; a++) {
					if(def.actions[a] == null) {
						continue;
					}
					npcBuffer.putByte(30 + a);
					//writeString(dat, def.actions[a]);
					npcBuffer.writeString(def.actions[a]);
				}
			}
			
			if(def.recolorOriginal != null && def.recolorNew.length > 0) {
				npcBuffer.putByte(40);
				npcBuffer.putByte(def.recolorOriginal.length);
				for(int c = 0; c < def.recolorOriginal.length; c++) {
					npcBuffer.putShort(def.recolorOriginal[c]);
					npcBuffer.putShort(def.recolorNew[c]);
				}
			}
			
			if(def.chatModels != null && def.chatModels.length > 0) {
				npcBuffer.putByte(60);
				npcBuffer.putByte(def.chatModels.length);
				for(int m = 0; m < def.chatModels.length; m++) {
					npcBuffer.putShort(def.chatModels[m]);
				}
			}
			
			if(!def.hasMapDot) {
				npcBuffer.putByte(93);
			}
			
			if(def.combatLevel != -1) {
				npcBuffer.putByte(95);
				npcBuffer.putShort(def.combatLevel);
			}
			
			if(def.scaleXZ != 128) {
				npcBuffer.putByte(97);
				npcBuffer.putShort(def.scaleXZ);
			}
			if(def.scaleY != 128) {
				npcBuffer.putByte(98);
				npcBuffer.putShort(def.scaleY);
			}
			
			if(def.aBoolean644) {
				npcBuffer.putByte(99);
			}
			
			if(def.lightModifier != 0) {
				npcBuffer.putByte(100);
				npcBuffer.putByte(def.lightModifier);
			}
			if(def.shadowModifier != 0) {
				npcBuffer.putByte(101);
				npcBuffer.putByte(def.shadowModifier / 5);
			}
			
			if(def.headIcon != -1) {
				npcBuffer.putByte(102);
				npcBuffer.putShort(def.headIcon);
			}
			
			if(def.degreesToTurn != 32) {
				npcBuffer.putByte(103);
				npcBuffer.putShort(def.degreesToTurn);
			}

			if(def.varBitId != -1 || def.sessionSettingId != -1) {
				npcBuffer.putByte(106);
				if(def.varBitId != -1) {
					npcBuffer.putShort(def.varBitId);
				} else {
					npcBuffer.putShort(65535);
				}
				if(def.sessionSettingId != -1) {
					npcBuffer.putShort(def.sessionSettingId);
				} else {
					npcBuffer.putShort(65535);
				}
				if(def.childIds != null) {
					for(int c = 0; c < def.childIds.length; c++) {
						npcBuffer.putShort(def.childIds[c]);
					}
				} else {
					npcBuffer.putByte(0);
				}
			}
			
			/*npcBuffer.putByte(106);
			if(def.varBitId != -1) {
				npcBuffer.putShort(def.varBitId);
			} else {
				npcBuffer.putShort(65535);
			}
			if(def.sessionSettingId != -1) {
				npcBuffer.putShort(def.sessionSettingId);
			} else {
				npcBuffer.putShort(65535);
			}
			if(def.childIds != null) {
				for(int c = 0; c < def.childIds.length; c++) {
					npcBuffer.putShort(def.childIds[c]);
				}
			} else {
				npcBuffer.putByte(0);
			}*/
			
			if(!def.clickable) {
				npcBuffer.putByte(107);
			}
			
			npcBuffer.putByte(0);
			//int end = dat.size();
			//int writeOffset = end - start;
			//idx.putShort(writeOffset);
			
			buffers[i] = npcBuffer;
		}

		//System.out.println("npc idx out = " + idx.size());
		//System.out.println("npc dat out = " + dat.size());
		//idx.flush();
		//dat.flush();
		//idx.close();
		//dat.close();
		
		for(int i = 0; i < buffers.length; i++) {
			idx.putShort(buffers[i].position);
		}
		
		for(int i = 0; i < buffers.length; i++) {
			byte[] payload = new byte[buffers[i].position];
			System.arraycopy(buffers[i].payload, 0, payload, 0, buffers[i].position);
			dat.putBytes(payload, 0, payload.length, 0);
		}
		
		try {
			DataOutputStream stream = new DataOutputStream(new FileOutputStream("npc.idx"));
			byte[] payload = new byte[idx.position];
			System.arraycopy(idx.payload, 0, payload, 0, idx.position);
			System.out.println("idx out = " + payload.length);
			stream.write(payload);
			stream.flush();
			stream.close();
			
			stream = new DataOutputStream(new FileOutputStream("npc.dat"));
			payload = new byte[dat.position];
			System.arraycopy(dat.payload, 0, payload, 0, dat.position);
			System.out.println("dat out = " + payload.length);
			stream.write(payload);
			stream.flush();
			stream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void readValues(ByteBuffer byteStream) {
		do {
			int opcode = byteStream.getUnsignedByte();
			if(opcode == 0)
				return;
			if(opcode == 1) {
				int modelCount = byteStream.getUnsignedByte();
				models = new int[modelCount];
				for(int j1 = 0; j1 < modelCount; j1++) {
					models[j1] = byteStream.getUnsignedShort();
				}
			} else if(opcode == 2)
				name = byteStream.getRS2String();
			else if(opcode == 3)
				description = byteStream.getBytes();
			else if(opcode == 12)
				boundDim = byteStream.getByte();
			else if(opcode == 13)
				idleAnim = byteStream.getUnsignedShort();
			else if(opcode == 14)
				walkAnim = byteStream.getUnsignedShort();
			else if(opcode == 17) {
				walkAnim = byteStream.getUnsignedShort();
				turn180Anim = byteStream.getUnsignedShort();
				turn90CWAnim = byteStream.getUnsignedShort();
				turn90CCWAnim = byteStream.getUnsignedShort();
			} else if(opcode >= 30 && opcode < 40) {
				if(actions == null)
					actions = new String[5];
				actions[opcode - 30] = byteStream.getRS2String();
				if(actions[opcode - 30].equalsIgnoreCase("hidden"))
					actions[opcode - 30] = null;
			} else if(opcode == 40) {
				int colorCount = byteStream.getUnsignedByte();
				recolorOriginal = new int[colorCount];
				recolorNew = new int[colorCount];
				for(int k1 = 0; k1 < colorCount; k1++) {
					recolorOriginal[k1] = byteStream.getUnsignedShort();
					recolorNew[k1] = byteStream.getUnsignedShort();
				}

			} else if(opcode == 60) {
				int chatModelCount = byteStream.getUnsignedByte();
				chatModels = new int[chatModelCount];
				for(int l1 = 0; l1 < chatModelCount; l1++)
					chatModels[l1] = byteStream.getUnsignedShort();

			} else if(opcode == 90)
				byteStream.getUnsignedShort();
			else if(opcode == 91)
				byteStream.getUnsignedShort();
			else if(opcode == 92)
				byteStream.getUnsignedShort();
			else if(opcode == 93)
				hasMapDot = false;
			else if(opcode == 95)
				combatLevel = byteStream.getUnsignedShort();
			else if(opcode == 97)
				scaleXZ = byteStream.getUnsignedShort();
			else if(opcode == 98)
				scaleY = byteStream.getUnsignedShort();
			else if(opcode == 99)
				aBoolean644 = true;
			else if(opcode == 100)
				lightModifier = byteStream.getByte();
			else if(opcode == 101)
				shadowModifier = byteStream.getByte() * 5;
			else if(opcode == 102)
				headIcon = byteStream.getUnsignedShort();
			else if(opcode == 103)
				degreesToTurn = byteStream.getUnsignedShort();
			else if(opcode == 106) {
				varBitId = byteStream.getUnsignedShort();
				if(varBitId == 65535)
					varBitId = -1;
				sessionSettingId = byteStream.getUnsignedShort();
				if(sessionSettingId == 65535)
					sessionSettingId = -1;
				int i1 = byteStream.getUnsignedByte();
				childIds = new int[i1 + 1];
				for(int i2 = 0; i2 <= i1; i2++) {
					childIds[i2] = byteStream.getUnsignedShort();
					if(childIds[i2] == 65535)
						childIds[i2] = -1;
				}
				
				//System.out.println("NPC " + dumpId + ": " + varBitId + "," + sessionSettingId + "," + childIds.length);
			} else if(opcode == 107)
				clickable = false;
		} while(true);
	}

	public static void dump() {
		try {
			PrintWriter writer = new PrintWriter("npcDump.txt", "UTF-8");

			for(int i = 0; i < npcCount; i++) {
				NPCDefinition def = forId(i);
				writer.println("-------------------------------------------------");
				writer.println("Id: " + def.id);
				writer.println("Name: " + def.name);
				if(def.description != null) {
					writer.println("Desc: " + new String(def.description));
				}
				if(def.models != null && def.models.length > 0) {
					for(int m = 0; m < def.models.length; m++) {
						writer.println("Model " + m + ": " + def.models[m]);
					}
				}
				if(def.chatModels != null && def.chatModels.length > 0) {
					for(int m = 0; m < def.chatModels.length; m++) {
						writer.println("Chat Model " + m + ": " + def.chatModels[m]);
					}
				}
				writer.println("Bound Dim: " + def.boundDim);

				writer.println("Idle Anim: " + def.idleAnim);
				writer.println("Walk Anim: " + def.walkAnim);
				writer.println("Turn 180 Anim: " + def.turn180Anim);
				writer.println("Turn 90 CW Anim: " + def.turn90CWAnim);
				writer.println("Turn 90 CCW Anim: " + def.turn90CCWAnim);
				writer.println("Degrees to Turn: " + def.degreesToTurn);

				if(def.actions != null && def.actions.length > 0) {
					for(int a = 0; a < def.actions.length; a++) {
						writer.println("Action " + a + ": " + def.actions[a]);
					}
				}

				if(def.recolorOriginal != null && def.recolorOriginal.length > 0) {
					for(int c = 0; c < def.recolorOriginal.length; c++) {
						writer.println("Orig Color " + c + ": " + def.recolorOriginal[c]);
						writer.println("New Color  " + c + ": " + def.recolorNew[c]);
					}
				}

				writer.println("Combat Level: " + def.combatLevel);
			}

			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void clearCache() {
		modelCache = null;
		npcOffsets = null;
		npcCache = null;
		npcData = null;
	}

	public Model method359(int i) {
		if(childIds != null) {
			NPCDefinition class37 = method363();
			if(class37 == null)
				return null;
			else
				return class37.method359(858);
		}
		if(chatModels == null)
			return null;
		boolean flag = false;
		for(int k = 0; k < chatModels.length; k++)
			if(!Model.isCached(chatModels[k]))
				flag = true;

		if(flag)
			return null;
		Model aclass50_sub1_sub4_sub4[] = new Model[chatModels.length];
		for(int l = 0; l < chatModels.length; l++)
			aclass50_sub1_sub4_sub4[l] = Model.getModel(chatModels[l]);

		Model class50_sub1_sub4_sub4;
		if(aclass50_sub1_sub4_sub4.length == 1)
			class50_sub1_sub4_sub4 = aclass50_sub1_sub4_sub4[0];
		else
			class50_sub1_sub4_sub4 = new Model(aclass50_sub1_sub4_sub4.length, aclass50_sub1_sub4_sub4);
		if(recolorOriginal != null) {
			for(int i1 = 0; i1 < recolorOriginal.length; i1++)
				class50_sub1_sub4_sub4.recolour(recolorOriginal[i1], recolorNew[i1]);

		}
		return class50_sub1_sub4_sub4;
	}

	public boolean method360(int i) {
		if(childIds == null)
			return true;
		int j = -1;
		if(varBitId != -1) {
			Class49 class49 = Class49.aClass49Array824[varBitId];
			int k = class49.anInt826;
			int l = class49.anInt827;
			int i1 = class49.anInt828;
			int j1 = Client.anIntArray1214[i1 - l];
			j = aClient629.anIntArray1039[k] >> l & j1;
		} else if(sessionSettingId != -1)
			j = aClient629.anIntArray1039[sessionSettingId];
		return j >= 0 && j < childIds.length && childIds[j] != -1;
	}

	public Model method362(int i, int j, int k, int ai[]) {
		if(childIds != null) {
			NPCDefinition class37 = method363();
			if(class37 == null)
				return null;
			else
				return class37.method362(i, j, 0, ai);
		}
		Model class50_sub1_sub4_sub4 = (Model) modelCache.get(id);
		if(class50_sub1_sub4_sub4 == null) {
			boolean flag = false;
			for(int l = 0; l < models.length; l++)
				if(!Model.isCached(models[l]))
					flag = true;

			if(flag)
				return null;
			Model aclass50_sub1_sub4_sub4[] = new Model[models.length];
			for(int i1 = 0; i1 < models.length; i1++)
				aclass50_sub1_sub4_sub4[i1] = Model.getModel(models[i1]);

			if(aclass50_sub1_sub4_sub4.length == 1)
				class50_sub1_sub4_sub4 = aclass50_sub1_sub4_sub4[0];
			else
				class50_sub1_sub4_sub4 = new Model(aclass50_sub1_sub4_sub4.length, aclass50_sub1_sub4_sub4);
			if(recolorOriginal != null) {
				for(int j1 = 0; j1 < recolorOriginal.length; j1++)
					class50_sub1_sub4_sub4.recolour(recolorOriginal[j1], recolorNew[j1]);

			}
			class50_sub1_sub4_sub4.createBones();
			class50_sub1_sub4_sub4.light(64 + lightModifier, 850 + shadowModifier, -30, -50, -30, true);
			modelCache.put(class50_sub1_sub4_sub4, id, 5);
		}
		Model class50_sub1_sub4_sub4_1 = Model.aClass50_Sub1_Sub4_Sub4_1643;
		class50_sub1_sub4_sub4_1.method579(Class21.method239(i) & Class21.method239(j), class50_sub1_sub4_sub4, 1244);
		if(i != -1 && j != -1)
			class50_sub1_sub4_sub4_1.method586(j, 0, i, ai);
		else if(i != -1)
			class50_sub1_sub4_sub4_1.method585(i);
		if(scaleXZ != 128 || scaleY != 128)
			class50_sub1_sub4_sub4_1.method593(scaleY, scaleXZ, 9, scaleXZ);
		class50_sub1_sub4_sub4_1.method581();
		class50_sub1_sub4_sub4_1.anIntArrayArray1679 = null;
		class50_sub1_sub4_sub4_1.anIntArrayArray1678 = null;
		if(boundDim == 1)
			class50_sub1_sub4_sub4_1.fitsOnSingleTile = true;
		return class50_sub1_sub4_sub4_1;
	}

	public NPCDefinition method363() {
		int i = -1;
		if(varBitId != -1) {
			Class49 class49 = Class49.aClass49Array824[varBitId];
			int j = class49.anInt826;
			int k = class49.anInt827;
			int l = class49.anInt828;
			int i1 = Client.anIntArray1214[l - k];
			i = aClient629.anIntArray1039[j] >> k & i1;
		} else if(sessionSettingId != -1)
			i = aClient629.anIntArray1039[sessionSettingId];
		if(i < 0 || i >= childIds.length || childIds[i] == -1)
			return null;
		else
			return forId(childIds[i]);
	}

	public NPCDefinition() {
		idleAnim = -1;
		id = -1L;
		scaleY = 128;
		clickable = true;
		scaleXZ = 128;
		turn90CCWAnim = -1;
		hasMapDot = true;
		headIcon = -1;
		combatLevel = -1;
		turn90CWAnim = -1;
		boundDim = 1;
		turn180Anim = -1;
		aBoolean644 = false;
		walkAnim = -1;
		degreesToTurn = 32;
		name = "null";
		varBitId = -1;
		sessionSettingId = -1;
	}

	public int idleAnim;
	public int childIds[];
	public int chatModels[];
	public int models[];
	public long id;
	public static Client aClient629;
	public int scaleY;
	public boolean clickable;
	public int scaleXZ;
	public int turn90CCWAnim;
	public int recolorOriginal[];
	public static MemCache modelCache = new MemCache(30);
	public boolean hasMapDot;
	public int headIcon;
	public int combatLevel;
	public int turn90CWAnim;
	public byte boundDim;
	public int turn180Anim;
	public boolean aBoolean644;
	public int walkAnim;
	public String actions[];
	public static int npcCount;
	public static int npcOffsets[];
	public int degreesToTurn;
	public String name;
	public int varBitId;
	public static NPCDefinition npcCache[];
	public int recolorNew[];
	public static ByteBuffer npcData;
	public int shadowModifier;
	public int sessionSettingId;
	public byte description[];
	public static int cacheIndex;
	public int lightModifier;

}
