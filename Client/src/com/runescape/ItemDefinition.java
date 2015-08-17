package com.runescape;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;

public class ItemDefinition {

	public boolean isChatModelCached(int gender) {
		int chatModel = modelMaleChat;
		int chatModelHat = modelMaleChatHat;
		if(gender == 1) {
			chatModel = modelFemaleChat;
			chatModelHat = modelFemaleChatHat;
		}
		if(chatModel == -1)
			return true;
		boolean cached = true;
		if(!Model.isCached(chatModel))
			cached = false;
		if(chatModelHat != -1 && !Model.isCached(chatModelHat))
			cached = false;
		return cached;
	}

	public static ItemDefinition forId(int itemId) {
		for(int j = 0; j < 10; j++) {
			if(itemCache[j].id == itemId) {
				return itemCache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 10;
		ItemDefinition itemDef = itemCache[cacheIndex];
		itemData.position = itemOffsets[itemId];
		itemDef.id = itemId;
		itemDef.setDefaults();
		itemDef.readValues(itemData);
		if(itemDef.parentNoteId != -1)
			itemDef.toNote();
		if(!memberItemsEnabled && itemDef.members) {
			itemDef.name = "Members Object";
			itemDef.description = "Login to a members' server to use this object.".getBytes();
			itemDef.groundActions = null;
			itemDef.actions = null;
			itemDef.teamId = 0;
		}
		
		if(itemId == 7982) {
			itemDef.name = "Commander platebody";
			itemDef.description = new String("A Black Knight commander's Armour.").getBytes();
			itemDef.inventoryModel = 2378;
			itemDef.modelMale1 = 3379;
			itemDef.modelMale2 = 164;
			itemDef.modelFemale1 = 3383;
			itemDef.modelFemale2 = 344;
			itemDef.modelMale3 = 3381;
			itemDef.modelFemale3 = 3381;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[3];
			itemDef.recolorNew = new int[3];
			itemDef.recolorOriginal[0] = 24;
			itemDef.recolorNew[0] = 925;
			itemDef.recolorOriginal[1] = 61;
			itemDef.recolorNew[1] = 24;
			itemDef.recolorOriginal[2] = 41;
			itemDef.recolorNew[2] = 10508;
			itemDef.rotationY = 0;
			itemDef.rotationZ = 0;
			itemDef.zoom = 1250;
			itemDef.worldRotationX = 488;
			itemDef.translateX = -1;
			itemDef.translateYZ = 0;
		} if(itemId == 7983) {
			itemDef.name = "Commander platelegs";
			itemDef.description = new String("A Black Knight commander's leg armour.").getBytes();
			itemDef.inventoryModel = 2582;
			itemDef.modelMale1 = 268;
			itemDef.modelFemale1 = 432;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[3];
			itemDef.recolorNew = new int[3];
			itemDef.recolorOriginal[0] = 61;
			itemDef.recolorNew[0] = 24;
			itemDef.recolorOriginal[1] = 41;
			itemDef.recolorNew[1] = 10508;
			itemDef.recolorOriginal[2] = 57;
			itemDef.recolorNew[2] = 925;
			itemDef.rotationY = 0;
			itemDef.rotationZ = 0;
			itemDef.zoom = 1740;
			itemDef.worldRotationX = 444;
			itemDef.translateX = 0;
			itemDef.translateYZ = -8;
		} if(itemId == 7984) {
			itemDef.name = "Commander full helm";
			itemDef.description = new String("A Black Knight commander's helm.").getBytes();
			itemDef.inventoryModel = 2813;
			itemDef.modelMale1 = 218;
			itemDef.modelFemale1 = 394;
			itemDef.modelMaleChat = 56;
			itemDef.modelFemaleChat = 116;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[2];
			itemDef.recolorNew = new int[2];
			itemDef.recolorOriginal[0] = 61;
			itemDef.recolorNew[0] = 24;
			itemDef.recolorOriginal[1] = 926;
			itemDef.recolorNew[1] = 925;
			itemDef.rotationY = 152;
			itemDef.rotationZ = 0;
			itemDef.zoom = 800;
			itemDef.worldRotationX = 160;
			itemDef.translateX = -1;
			itemDef.translateYZ = 6;
		} else if(itemId == 7985) {
			itemDef.name = "Black Knight platebody";
			itemDef.description = new String("A Black Knight's Armour.").getBytes();
			itemDef.inventoryModel = 2378;
			itemDef.modelMale1 = 306;
			itemDef.modelMale2 = 164;
			itemDef.modelFemale1 = 468;
			itemDef.modelFemale2 = 344;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[5];
			itemDef.recolorNew = new int[5];
			itemDef.recolorOriginal[0] = 61;
			itemDef.recolorNew[0] = 24;
			itemDef.recolorOriginal[1] = 41;
			itemDef.recolorNew[1] = 10508;
			itemDef.recolorOriginal[2] = 70;
			itemDef.recolorNew[2] = 24;
			itemDef.recolorOriginal[3] = 57;
			itemDef.recolorNew[3] = 24;
			itemDef.recolorOriginal[4] = 5532;
			itemDef.recolorNew[4] = 10508;
			itemDef.rotationY = 0;
			itemDef.rotationZ = 0;
			itemDef.zoom = 1250;
			itemDef.worldRotationX = 488;
			itemDef.translateX = -1;
			itemDef.translateYZ = 0;
		} if(itemId == 7986) {
			itemDef.name = "Black Knight platelegs";
			itemDef.description = new String("A Black Knight's leg armour.").getBytes();
			itemDef.inventoryModel = 2582;
			itemDef.modelMale1 = 268;
			itemDef.modelFemale1 = 432;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[3];
			itemDef.recolorNew = new int[3];
			itemDef.recolorOriginal[0] = 61;
			itemDef.recolorNew[0] = 24;
			itemDef.recolorOriginal[1] = 41;
			itemDef.recolorNew[1] = 10508;
			itemDef.recolorOriginal[2] = 57;
			itemDef.recolorNew[2] = 24;
			itemDef.rotationY = 0;
			itemDef.rotationZ = 0;
			itemDef.zoom = 1740;
			itemDef.worldRotationX = 444;
			itemDef.translateX = 0;
			itemDef.translateYZ = -8;
		} if(itemId == 7987) {
			itemDef.name = "Black Knight full helm";
			itemDef.description = new String("A Black Knight's helm.").getBytes();
			itemDef.inventoryModel = 2813;
			itemDef.modelMale1 = 218;
			itemDef.modelFemale1 = 394;
			itemDef.modelMaleChat = 56;
			itemDef.modelFemaleChat = 116;
			itemDef.actions = new String[5];
			itemDef.actions[1] = "Wear";
			itemDef.recolorOriginal = new int[2];
			itemDef.recolorNew = new int[2];
			itemDef.recolorOriginal[0] = 61;
			itemDef.recolorNew[0] = 24;
			itemDef.recolorOriginal[1] = 926;
			itemDef.recolorNew[1] = 925;
			itemDef.rotationY = 152;
			itemDef.rotationZ = 0;
			itemDef.zoom = 800;
			itemDef.worldRotationX = 160;
			itemDef.translateX = -1;
			itemDef.translateYZ = 6;
		}
		
		return itemDef;
	}

	public Model getEquippedModel(int i) {
		int j = modelMale1;
		int k = modelMale2;
		int l = modelMale3;
		if(i == 1) {
			j = modelFemale1;
			k = modelFemale2;
			l = modelFemale3;
		}
		if(j == -1)
			return null;
		Model class50_sub1_sub4_sub4 = Model.getModel(j);
		if(k != -1)
			if(l != -1) {
				Model class50_sub1_sub4_sub4_1 = Model.getModel(k);
				Model class50_sub1_sub4_sub4_3 = Model.getModel(l);
				Model aclass50_sub1_sub4_sub4_1[] = { class50_sub1_sub4_sub4, class50_sub1_sub4_sub4_1, class50_sub1_sub4_sub4_3 };
				class50_sub1_sub4_sub4 = new Model(3, aclass50_sub1_sub4_sub4_1);
			} else {
				Model class50_sub1_sub4_sub4_2 = Model.getModel(k);
				Model aclass50_sub1_sub4_sub4[] = { class50_sub1_sub4_sub4, class50_sub1_sub4_sub4_2 };
				class50_sub1_sub4_sub4 = new Model(2, aclass50_sub1_sub4_sub4);
			}
		if(i == 0 && modelMaleTranslationY != 0)
			class50_sub1_sub4_sub4.method590(0, 0, false, modelMaleTranslationY);
		if(i == 1 && modelFemaleTranslationY != 0)
			class50_sub1_sub4_sub4.method590(0, 0, false, modelFemaleTranslationY);
		if(recolorOriginal != null) {
			for(int i1 = 0; i1 < recolorOriginal.length; i1++)
				class50_sub1_sub4_sub4.recolour(recolorOriginal[i1], recolorNew[i1]);

		}
		return class50_sub1_sub4_sub4;
	}

	public static void unpack(CacheArchive archive) {
		itemData = new ByteBuffer(archive.getDataForName("obj.dat"));
		ByteBuffer byteStream = new ByteBuffer(archive.getDataForName("obj.idx"));

		/*byte[] data = new byte[1000000];
		byte[] idxData = new byte[1000000];
		boolean read = true;
		int index = 0;
		int idxIndex = 0;
		try {
			DataInputStream stream = new DataInputStream(new FileInputStream("obj.dat"));
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
			DataInputStream stream = new DataInputStream(new FileInputStream("obj.idx"));
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
		itemData = new ByteBuffer(newData);
		
		byte[] newIdxData = new byte[idxIndex];
		System.arraycopy(idxData, 0, newIdxData, 0, idxIndex);
		byteStream = new ByteBuffer(newIdxData);*/
		
		System.out.println("item idx = " + byteStream.payload.length);
		System.out.println("item dat = " + itemData.payload.length);
		itemCount = byteStream.getUnsignedShort();
		itemOffsets = new int[itemCount + 500];
		int offset = 2;
		for(int j = 0; j < itemCount; j++) {
			itemOffsets[j] = offset;
			int len = byteStream.getUnsignedShort();
			offset += len;
		}

		itemCache = new ItemDefinition[10];
		for(int k = 0; k < 10; k++) {
			itemCache[k] = new ItemDefinition();
		}
	}

	public void toNote() {
		ItemDefinition unNoted = forId(parentNoteId);
		inventoryModel = unNoted.inventoryModel;
		zoom = unNoted.zoom;
		worldRotationX = unNoted.worldRotationX;
		rotationY = unNoted.rotationY;
		rotationZ = unNoted.rotationZ;
		translateX = unNoted.translateX;
		translateYZ = unNoted.translateYZ;
		recolorOriginal = unNoted.recolorOriginal;
		recolorNew = unNoted.recolorNew;
		ItemDefinition note = forId(unNotedItemId);
		name = note.name;
		members = note.members;
		value = note.value;
		String s = "a";
		char c = note.name.charAt(0);
		if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
			s = "an";
		description = ("Swap this note at any bank for " + s + " " + note.name + ".").getBytes();
		stackable = true;
	}

	public boolean isEquippedModelCached(int j) {
		int k = modelMale1;
		int l = modelMale2;
		int i1 = modelMale3;
		if(j == 1) {
			k = modelFemale1;
			l = modelFemale2;
			i1 = modelFemale3;
		}
		if(k == -1)
			return true;
		boolean flag = true;
		if(!Model.isCached(k))
			flag = false;
		if(l != -1 && !Model.isCached(l))
			flag = false;
		if(i1 != -1 && !Model.isCached(i1))
			flag = false;
		return flag;
	}

	public Model method217(int j) {
		if(stackVariantId != null && j > 1) {
			int k = -1;
			for(int l = 0; l < 10; l++)
				if(j >= stackVariantSize[l] && stackVariantSize[l] != 0)
					k = stackVariantId[l];

			if(k != -1)
				return forId(k).method217(1);
		}
		Model class50_sub1_sub4_sub4 = Model.getModel(inventoryModel);
		if(class50_sub1_sub4_sub4 == null)
			return null;
		if(recolorOriginal != null) {
			for(int i1 = 0; i1 < recolorOriginal.length; i1++)
				class50_sub1_sub4_sub4.recolour(recolorOriginal[i1], recolorNew[i1]);

		}
		return class50_sub1_sub4_sub4;
	}
	
	public static void dump() {
		try {
			PrintWriter writer = new PrintWriter("itemDump.txt", "UTF-8");
			
			for(int i = 0; i < itemCount; i++) {
				ItemDefinition def = forId(i);
				writer.println("-------------------------------------------------");
				writer.println("Id: " + def.id);
				writer.println("Name: " + def.name);
				if(def.description != null) {
					writer.println("Desc: " + new String(def.description));
				}
				writer.println("Inventory Model: " + def.inventoryModel);
				writer.println("Male Model 1: " + def.modelMale1);
				writer.println("Female Model 1: " + def.modelFemale1);
				writer.println("Male Model 2: " + def.modelMale2);
				writer.println("Female Model 2: " + def.modelFemale2);
				writer.println("Male Model 3: " + def.modelMale3);
				writer.println("Female Model 3: " + def.modelFemale3);
				writer.println("Male Model Chat: " + def.modelMaleChat);
				writer.println("Male Model Chat Hat: " + def.modelMaleChatHat);
				writer.println("Female Model Chat: " + def.modelFemaleChat);
				writer.println("Female Model Chat Hat: " + def.modelFemaleChatHat);
				if(def.actions != null) {
					for(int a = 0; a < def.actions.length; a++) {
						writer.println("Action " + a + ": " + def.actions[a]);
					}
				}
				if(def.groundActions != null) {
					for(int g = 0; g < def.groundActions.length; g++) {
						writer.println("G Action " + g + ": " + def.groundActions[g]);
					}
				}
				if(def.recolorOriginal != null) {
					for(int o = 0; o < def.recolorOriginal.length; o++) {
						writer.println("Orig Color " + o + ": " + def.recolorOriginal[o]);
						writer.println("New Color  " + o + ": " + def.recolorNew[o]);
					}
				}
				writer.println("Rotation Y: " + def.rotationY);
				writer.println("Rotation Z: " + def.rotationZ);
				writer.println("Zoom: " + def.zoom);
				writer.println("World Rotation X: " + def.worldRotationX);
				writer.println("Translate X: " + def.translateX);
				writer.println("Translate YZ: " + def.translateYZ);
				writer.println("Model Scale X: " + def.modelScaleX);
				writer.println("Model Scale Y: " + def.modelScaleY);
				writer.println("Model Scale Z: " + def.modelScaleZ);
				writer.println("Un-Noted Item Id: " + def.unNotedItemId);
				writer.println("Parent Note Id: " + def.parentNoteId);
			}
			
			writer.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void repack() {
		ByteBuffer idx = ByteBuffer.create(100000000);
		ByteBuffer dat = ByteBuffer.create(100000000);
		int itemCount = 7982;
		idx.putShort(itemCount);
		dat.putShort(itemCount);

		ByteBuffer[] buffers = new ByteBuffer[itemCount];
		for(int i = 0; i < itemCount; i++) {
			ItemDefinition def = forId(i);
			
			ByteBuffer itemBuffer = ByteBuffer.create();
			
			if(def.parentNoteId == -1) {
				if(def.inventoryModel > 0) {
					itemBuffer.putByte(1);
					itemBuffer.putShort(def.inventoryModel);
					//System.out.println(def.inventoryModel);
				}
				
				if(def.name != null) {
					itemBuffer.putByte(2);
					itemBuffer.writeString(def.name);
				}
				
				if(def.description != null) {
					itemBuffer.putByte(3);
					itemBuffer.writeString(new String(def.description));
					//itemBuffer.putBytes(def.description, 0, def.description.length, 0);
				}
				
				if(def.zoom != 2000) {
					itemBuffer.putByte(4);
					itemBuffer.putShort(def.zoom);
				}
				
				if(def.worldRotationX != 0) {
					itemBuffer.putByte(5);
					itemBuffer.putShort(def.worldRotationX);
				}
				
				if(def.rotationY != 0) {
					itemBuffer.putByte(6);
					itemBuffer.putShort(def.rotationY);
				}
	
				if(def.translateX != 0) {
					itemBuffer.putByte(7);
					itemBuffer.putShort(def.translateX);
				}
	
				if(def.translateYZ != 0) {
					itemBuffer.putByte(8);
					itemBuffer.putShort(def.translateYZ);
				}
				
				if(def.stackable) {
					itemBuffer.putByte(11);
				}
				
				if(def.value != 1) {
					itemBuffer.putByte(12);
					itemBuffer.putInt(def.value);
				}
				
				if(def.members) {
					itemBuffer.putByte(16);
				}
				
				if(def.modelMale1 > 0) {
					itemBuffer.putByte(23);
					itemBuffer.putShort(def.modelMale1);
					itemBuffer.putByte(def.modelMaleTranslationY);
				}
				
				if(def.modelMale2 > 0) {
					itemBuffer.putByte(24);
					itemBuffer.putShort(def.modelMale2);
				}
	
				if(def.modelFemale1 > 0) {
					itemBuffer.putByte(25);
					itemBuffer.putShort(def.modelFemale1);
					itemBuffer.putByte(def.modelFemaleTranslationY);
				}
	
				if(def.modelFemale2 > 0) {
					itemBuffer.putByte(26);
					itemBuffer.putShort(def.modelFemale2);
				}
				
				if(def.groundActions != null && def.groundActions.length > 0) {
					for(int a = 0; a < def.groundActions.length; a++) {
						if(def.groundActions[a] == null) {
							continue;
						}
						itemBuffer.putByte(30 + a);
						itemBuffer.writeString(def.groundActions[a]);
					}
				}
				
				if(def.actions != null && def.actions.length > 0) {
					for(int a = 0; a < def.actions.length; a++) {
						if(def.actions[a] == null) {
							continue;
						}
						itemBuffer.putByte(35 + a);
						itemBuffer.writeString(def.actions[a]);
					}
				}
				
				if(def.recolorOriginal != null && def.recolorOriginal.length > 0) {
					itemBuffer.putByte(40);
					itemBuffer.putByte(def.recolorOriginal.length);
					for(int r = 0; r < def.recolorOriginal.length; r++) {
						itemBuffer.putShort(def.recolorOriginal[r]);
						itemBuffer.putShort(def.recolorNew[r]);
					}
				}
	
				if(def.modelMale3 > 0) {
					itemBuffer.putByte(78);
					itemBuffer.putShort(def.modelMale3);
				}
	
				if(def.modelFemale3 > 0) {
					itemBuffer.putByte(79);
					itemBuffer.putShort(def.modelFemale3);
				}
	
				if(def.modelMaleChat > 0) {
					itemBuffer.putByte(90);
					itemBuffer.putShort(def.modelMaleChat);
				}
	
				if(def.modelFemaleChat > 0) {
					itemBuffer.putByte(91);
					itemBuffer.putShort(def.modelFemaleChat);
				}
	
				if(def.modelMaleChatHat > 0) {
					itemBuffer.putByte(92);
					itemBuffer.putShort(def.modelMaleChatHat);
				}
	
				if(def.modelFemaleChatHat > 0) {
					itemBuffer.putByte(93);
					itemBuffer.putShort(def.modelFemaleChatHat);
				}
	
				if(def.rotationZ != 0) {
					itemBuffer.putByte(95);
					itemBuffer.putShort(def.rotationZ);
				}
				if(def.stackVariantId != null && def.stackVariantId.length > 0) {
					for(int a = 0; a < def.stackVariantId.length; a++) {
						itemBuffer.putByte(100 + a);
						itemBuffer.putShort(def.stackVariantId[a]);
						itemBuffer.putShort(def.stackVariantSize[a]);
					}
				}
	
				if(def.modelScaleX != 128) {
					itemBuffer.putByte(110);
					itemBuffer.putShort(def.modelScaleX);
				}
	
				if(def.modelScaleY != 128) {
					itemBuffer.putByte(111);
					itemBuffer.putShort(def.modelScaleY);
				}
	
				if(def.modelScaleZ != 128) {
					itemBuffer.putByte(112);
					itemBuffer.putShort(def.modelScaleZ);
				}
	
				if(def.lightIntensity != 0) {
					itemBuffer.putByte(113);
					itemBuffer.putByte(def.lightIntensity);
				}
	
				if(def.lightMag != 0) {
					itemBuffer.putByte(114);
					itemBuffer.putByte(def.lightMag / 5);
				}
	
				if(def.teamId != 0) {
					itemBuffer.putByte(115);
					itemBuffer.putByte(def.teamId);
				}
			} else {
				itemBuffer.putByte(97);
				itemBuffer.putShort(def.unNotedItemId);

				itemBuffer.putByte(98);
				itemBuffer.putShort(def.parentNoteId);
			}
			
			itemBuffer.putByte(0);
			
			buffers[i] = itemBuffer;
		}
		
		for(int i = 0; i < buffers.length; i++) {
			idx.putShort(buffers[i].position);
		}
		
		for(int i = 0; i < buffers.length; i++) {
			byte[] payload = new byte[buffers[i].position];
			System.arraycopy(buffers[i].payload, 0, payload, 0, buffers[i].position);
			dat.putBytes(payload, 0, payload.length, 0);
		}
		
		try {
			DataOutputStream stream = new DataOutputStream(new FileOutputStream("obj.idx"));
			byte[] payload = new byte[idx.position];
			System.arraycopy(idx.payload, 0, payload, 0, idx.position);
			System.out.println("idx out = " + payload.length);
			stream.write(payload);
			stream.flush();
			stream.close();
			
			stream = new DataOutputStream(new FileOutputStream("obj.dat"));
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
				inventoryModel = byteStream.getUnsignedShort();
				//System.out.println(inventoryModel);
			} else if(opcode == 2)
				name = byteStream.getRS2String();
			else if(opcode == 3)
				description = byteStream.getBytes();
			else if(opcode == 4)
				zoom = byteStream.getUnsignedShort();
			else if(opcode == 5)
				worldRotationX = byteStream.getUnsignedShort();
			else if(opcode == 6)
				rotationY = byteStream.getUnsignedShort();
			else if(opcode == 7) {
				translateX = byteStream.getUnsignedShort();
				if(translateX > 32767)
					translateX -= 0x10000;
			} else if(opcode == 8) {
				translateYZ = byteStream.getUnsignedShort();
				if(translateYZ > 32767)
					translateYZ -= 0x10000;
			} else if(opcode == 10)
				anInt372 = byteStream.getUnsignedShort();
			else if(opcode == 11)
				stackable = true;
			else if(opcode == 12)
				value = byteStream.getInt();
			else if(opcode == 16)
				members = true;
			else if(opcode == 23) {
				modelMale1 = byteStream.getUnsignedShort();
				modelMaleTranslationY = byteStream.getByte();
			} else if(opcode == 24)
				modelMale2 = byteStream.getUnsignedShort();
			else if(opcode == 25) {
				modelFemale1 = byteStream.getUnsignedShort();
				modelFemaleTranslationY = byteStream.getByte();
			} else if(opcode == 26)
				modelFemale2 = byteStream.getUnsignedShort();
			else if(opcode >= 30 && opcode < 35) {
				if(groundActions == null)
					groundActions = new String[5];
				groundActions[opcode - 30] = byteStream.getRS2String();
				if(groundActions[opcode - 30].equalsIgnoreCase("hidden"))
					groundActions[opcode - 30] = null;
			} else if(opcode >= 35 && opcode < 40) {
				if(actions == null)
					actions = new String[5];
				actions[opcode - 35] = byteStream.getRS2String();
			} else if(opcode == 40) {
				int colors = byteStream.getUnsignedByte();
				recolorOriginal = new int[colors];
				recolorNew = new int[colors];
				for(int k = 0; k < colors; k++) {
					recolorOriginal[k] = byteStream.getUnsignedShort();
					recolorNew[k] = byteStream.getUnsignedShort();
				}

			} else if(opcode == 78)
				modelMale3 = byteStream.getUnsignedShort();
			else if(opcode == 79)
				modelFemale3 = byteStream.getUnsignedShort();
			else if(opcode == 90)
				modelMaleChat = byteStream.getUnsignedShort();
			else if(opcode == 91)
				modelFemaleChat = byteStream.getUnsignedShort();
			else if(opcode == 92)
				modelMaleChatHat = byteStream.getUnsignedShort();
			else if(opcode == 93)
				modelFemaleChatHat = byteStream.getUnsignedShort();
			else if(opcode == 95)
				rotationZ = byteStream.getUnsignedShort();
			else if(opcode == 97)
				unNotedItemId = byteStream.getUnsignedShort();
			else if(opcode == 98)
				parentNoteId = byteStream.getUnsignedShort();
			else if(opcode >= 100 && opcode < 110) {
				if(stackVariantId == null) {
					stackVariantId = new int[10];
					stackVariantSize = new int[10];
				}
				stackVariantId[opcode - 100] = byteStream.getUnsignedShort();
				stackVariantSize[opcode - 100] = byteStream.getUnsignedShort();
			} else if(opcode == 110)
				modelScaleX = byteStream.getUnsignedShort();
			else if(opcode == 111)
				modelScaleY = byteStream.getUnsignedShort();
			else if(opcode == 112)
				modelScaleZ = byteStream.getUnsignedShort();
			else if(opcode == 113)
				lightIntensity = byteStream.getByte();
			else if(opcode == 114)
				lightMag = byteStream.getByte() * 5;
			else if(opcode == 115)
				teamId = byteStream.getUnsignedByte();
		} while(true);
	}

	public Model getChatModel(int gender) {
		int chatModel = modelMaleChat;
		int chatModelHat = modelMaleChatHat;
		if(gender == 1) {
			chatModel = modelFemaleChat;
			chatModelHat = modelFemaleChatHat;
		}
		if(chatModel == -1)
			return null;
		Model model = Model.getModel(chatModel);
		if(chatModelHat != -1) {
			Model hatModel = Model.getModel(chatModelHat);
			Model models[] = { model, hatModel };
			model = new Model(2, models);
		}
		if(recolorOriginal != null) {
			for(int l = 0; l < recolorOriginal.length; l++) {
				model.recolour(recolorOriginal[l], recolorNew[l]);
			}
		}
		return model;
	}

	public Model method220(int i) {
		if(stackVariantId != null && i > 1) {
			int j = -1;
			for(int k = 0; k < 10; k++)
				if(i >= stackVariantSize[k] && stackVariantSize[k] != 0)
					j = stackVariantId[k];

			if(j != -1)
				return forId(j).method220(1);
		}
		Model class50_sub1_sub4_sub4 = (Model) modelCache.get(id);
		if(class50_sub1_sub4_sub4 != null)
			return class50_sub1_sub4_sub4;
		class50_sub1_sub4_sub4 = Model.getModel(inventoryModel);
		if(class50_sub1_sub4_sub4 == null)
			return null;
		if(modelScaleX != 128 || modelScaleY != 128 || modelScaleZ != 128)
			class50_sub1_sub4_sub4.method593(modelScaleY, modelScaleZ, 9, modelScaleX);
		if(recolorOriginal != null) {
			for(int l = 0; l < recolorOriginal.length; l++)
				class50_sub1_sub4_sub4.recolour(recolorOriginal[l], recolorNew[l]);

		}
		class50_sub1_sub4_sub4.light(64 + lightIntensity, 768 + lightMag, -50, -10, -50, true);
		class50_sub1_sub4_sub4.fitsOnSingleTile = true;
		modelCache.put(class50_sub1_sub4_sub4, id, 5);
		return class50_sub1_sub4_sub4;
	}

	public static RgbImage method221(byte byte0, int i, int j, int k) {
		if(i == 0) {
			RgbImage class50_sub1_sub1_sub1 = (RgbImage) imageCache.get(k);
			if(class50_sub1_sub1_sub1 != null && class50_sub1_sub1_sub1.libHeight != j && class50_sub1_sub1_sub1.libHeight != -1) {
				class50_sub1_sub1_sub1.method442();
				class50_sub1_sub1_sub1 = null;
			}
			if(class50_sub1_sub1_sub1 != null)
				return class50_sub1_sub1_sub1;
		}
		ItemDefinition itemDef = forId(k);
		if(itemDef.stackVariantId == null)
			j = -1;
		if(j > 1) {
			int l = -1;
			for(int i1 = 0; i1 < 10; i1++)
				if(j >= itemDef.stackVariantSize[i1] && itemDef.stackVariantSize[i1] != 0)
					l = itemDef.stackVariantId[i1];

			if(l != -1)
				itemDef = forId(l);
		}
		Model class50_sub1_sub4_sub4 = itemDef.method220(1);
		if(class50_sub1_sub4_sub4 == null)
			return null;
		RgbImage class50_sub1_sub1_sub1_2 = null;
		if(itemDef.parentNoteId != -1) {
			class50_sub1_sub1_sub1_2 = method221((byte) -33, -1, 10, itemDef.unNotedItemId);
			if(class50_sub1_sub1_sub1_2 == null)
				return null;
		}
		RgbImage class50_sub1_sub1_sub1_1 = new RgbImage(32, 32);
		int j1 = Rasterizer.anInt1532;
		int k1 = Rasterizer.anInt1533;
		int ai[] = Rasterizer.lineOffsets;
		int ai1[] = DrawingArea.pixels;
		int l1 = DrawingArea.width;
		int i2 = DrawingArea.anInt1426;
		int j2 = DrawingArea.viewportLeft;
		int k2 = DrawingArea.viewportRight;
		int l2 = DrawingArea.viewportTop;
		int i3 = DrawingArea.viewportBottom;
		Rasterizer.aBoolean1530 = false;
		DrawingArea.setTarget(32, 32, class50_sub1_sub1_sub1_1.pixels);
		DrawingArea.fillRect(32, 0, 0, (byte) -24, 32, 0);
		Rasterizer.method493(568);
		int j3 = itemDef.zoom;
		if(i == -1)
			j3 = (int) ((double) j3 * 1.5D);
		if(i > 0)
			j3 = (int) ((double) j3 * 1.04D);
		int k3 = Rasterizer.anIntArray1536[itemDef.worldRotationX] * j3 >> 16;
		int l3 = Rasterizer.anIntArray1537[itemDef.worldRotationX] * j3 >> 16;
		class50_sub1_sub4_sub4.method598(0, itemDef.rotationY, itemDef.rotationZ, itemDef.worldRotationX, itemDef.translateX, k3 + ((Class50_Sub1_Sub4) (class50_sub1_sub4_sub4)).modelHeight / 2
				+ itemDef.translateYZ, l3 + itemDef.translateYZ);
		for(int l4 = 31; l4 >= 0; l4--) {
			for(int i4 = 31; i4 >= 0; i4--)
				if(class50_sub1_sub1_sub1_1.pixels[l4 + i4 * 32] == 0)
					if(l4 > 0 && class50_sub1_sub1_sub1_1.pixels[(l4 - 1) + i4 * 32] > 1)
						class50_sub1_sub1_sub1_1.pixels[l4 + i4 * 32] = 1;
					else if(i4 > 0 && class50_sub1_sub1_sub1_1.pixels[l4 + (i4 - 1) * 32] > 1)
						class50_sub1_sub1_sub1_1.pixels[l4 + i4 * 32] = 1;
					else if(l4 < 31 && class50_sub1_sub1_sub1_1.pixels[l4 + 1 + i4 * 32] > 1)
						class50_sub1_sub1_sub1_1.pixels[l4 + i4 * 32] = 1;
					else if(i4 < 31 && class50_sub1_sub1_sub1_1.pixels[l4 + (i4 + 1) * 32] > 1)
						class50_sub1_sub1_sub1_1.pixels[l4 + i4 * 32] = 1;

		}

		if(i > 0) {
			for(int i5 = 31; i5 >= 0; i5--) {
				for(int j4 = 31; j4 >= 0; j4--)
					if(class50_sub1_sub1_sub1_1.pixels[i5 + j4 * 32] == 0)
						if(i5 > 0 && class50_sub1_sub1_sub1_1.pixels[(i5 - 1) + j4 * 32] == 1)
							class50_sub1_sub1_sub1_1.pixels[i5 + j4 * 32] = i;
						else if(j4 > 0 && class50_sub1_sub1_sub1_1.pixels[i5 + (j4 - 1) * 32] == 1)
							class50_sub1_sub1_sub1_1.pixels[i5 + j4 * 32] = i;
						else if(i5 < 31 && class50_sub1_sub1_sub1_1.pixels[i5 + 1 + j4 * 32] == 1)
							class50_sub1_sub1_sub1_1.pixels[i5 + j4 * 32] = i;
						else if(j4 < 31 && class50_sub1_sub1_sub1_1.pixels[i5 + (j4 + 1) * 32] == 1)
							class50_sub1_sub1_sub1_1.pixels[i5 + j4 * 32] = i;

			}

		} else if(i == 0) {
			for(int j5 = 31; j5 >= 0; j5--) {
				for(int k4 = 31; k4 >= 0; k4--)
					if(class50_sub1_sub1_sub1_1.pixels[j5 + k4 * 32] == 0 && j5 > 0 && k4 > 0 && class50_sub1_sub1_sub1_1.pixels[(j5 - 1) + (k4 - 1) * 32] > 0)
						class50_sub1_sub1_sub1_1.pixels[j5 + k4 * 32] = 0x302020;

			}

		}
		if(itemDef.parentNoteId != -1) {
			int k5 = class50_sub1_sub1_sub1_2.libWidth;
			int l5 = class50_sub1_sub1_sub1_2.libHeight;
			class50_sub1_sub1_sub1_2.libWidth = 32;
			class50_sub1_sub1_sub1_2.libHeight = 32;
			class50_sub1_sub1_sub1_2.method461(0, 0, -488);
			class50_sub1_sub1_sub1_2.libWidth = k5;
			class50_sub1_sub1_sub1_2.libHeight = l5;
		}
		if(i == 0)
			imageCache.put(class50_sub1_sub1_sub1_1, k, 5);
		DrawingArea.setTarget(l1, i2, ai1);
		DrawingArea.method446(l2, j2, i3, k2, true);
		Rasterizer.anInt1532 = j1;
		Rasterizer.anInt1533 = k1;
		Rasterizer.lineOffsets = ai;
		Rasterizer.aBoolean1530 = true;
		if(itemDef.stackable)
			class50_sub1_sub1_sub1_1.libWidth = 33;
		else
			class50_sub1_sub1_sub1_1.libWidth = 32;
		class50_sub1_sub1_sub1_1.libHeight = j;
		if(byte0 != -33)
			throw new NullPointerException();
		else
			return class50_sub1_sub1_sub1_1;
	}

	public static void method222(boolean flag) {
		modelCache = null;
		if(flag) {
			for(int i = 1; i > 0; i++)
				;
		}
		imageCache = null;
		itemOffsets = null;
		itemCache = null;
		itemData = null;
	}

	public void setDefaults() {
		inventoryModel = 0;
		name = null;
		description = null;
		recolorOriginal = null;
		recolorNew = null;
		zoom = 2000;
		worldRotationX = 0;
		rotationY = 0;
		rotationZ = 0;
		translateX = 0;
		translateYZ = 0;
		anInt372 = -1;
		stackable = false;
		value = 1;
		members = false;
		groundActions = null;
		actions = null;
		modelMale1 = -1;
		modelMale2 = -1;
		modelMaleTranslationY = 0;
		modelFemale1 = -1;
		modelFemale2 = -1;
		modelFemaleTranslationY = 0;
		modelMale3 = -1;
		modelFemale3 = -1;
		modelMaleChat = -1;
		modelMaleChatHat = -1;
		modelFemaleChat = -1;
		modelFemaleChatHat = -1;
		stackVariantId = null;
		stackVariantSize = null;
		unNotedItemId = -1;
		parentNoteId = -1;
		modelScaleX = 128;
		modelScaleY = 128;
		modelScaleZ = 128;
		lightIntensity = 0;
		lightMag = 0;
		teamId = 0;
	}

	public ItemDefinition() {
		id = -1;
	}

	public int modelFemale1;
	public int translateX;
	public byte description[];
	public String name;
	public byte modelFemaleTranslationY;
	public int modelMale2;
	public int teamId;
	public int unNotedItemId;
	public int modelMaleChat;
	public static int itemCount;
	public static ItemDefinition itemCache[];
	public static MemCache modelCache = new MemCache(50);
	public String groundActions[];
	public int rotationZ;
	public int translateYZ;
	public int recolorNew[];
	public static int itemOffsets[];
	public int parentNoteId;
	public static boolean memberItemsEnabled = true;
	public int value;
	public static MemCache imageCache = new MemCache(100);
	public String actions[];
	public static boolean aBoolean350 = true;
	public static int cacheIndex;
	public int modelMale1;
	public int lightIntensity;
	public int modelFemale2;
	public int rotationY;
	public int modelScaleY;
	public int lightMag;
	public int worldRotationX;
	public int inventoryModel;
	public int modelMaleChatHat;
	public int modelFemaleChatHat;
	public int id;
	public int recolorOriginal[];
	public int stackVariantId[];
	public int modelScaleX;
	public int modelFemale3;
	public int modelScaleZ;
	public int zoom;
	public int modelMale3;
	public boolean stackable;
	public int anInt372;
	public static ByteBuffer itemData;
	public int modelFemaleChat;
	public int stackVariantSize[];
	public boolean members;
	public byte modelMaleTranslationY;

}
