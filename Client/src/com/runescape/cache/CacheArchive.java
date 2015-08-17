package com.runescape.cache;

import com.runescape.cache.bz2.BZ2InputStream;
import com.runescape.io.ByteBuffer;

public class CacheArchive {

	public byte outputData[];
	public int dataSize;
	public int nameIndexes[];
	public int fileSizes[];
	public int onDiskFileSizes[];
	public int fileOffsets[];
	public boolean isCompressed;
	
	public CacheArchive(byte in[]) {
		ByteBuffer buffer = new ByteBuffer(in);
		int resultLength = buffer.getTriByte();
		int rawLength = buffer.getTriByte();
		if(rawLength != resultLength) {
			byte abyte1[] = new byte[resultLength];
			BZ2InputStream.decompressBuffer(abyte1, resultLength, in, rawLength, 6);
			outputData = abyte1;
			buffer = new ByteBuffer(outputData);
			isCompressed = true;
		} else {
			outputData = in;
			isCompressed = false;
		}
		dataSize = buffer.getUnsignedShort();
		nameIndexes = new int[dataSize];
		fileSizes = new int[dataSize];
		onDiskFileSizes = new int[dataSize];
		fileOffsets = new int[dataSize];
		int l = buffer.position + dataSize * 10;
		for(int i1 = 0; i1 < dataSize; i1++) {
			nameIndexes[i1] = buffer.getInt();
			fileSizes[i1] = buffer.getTriByte();
			onDiskFileSizes[i1] = buffer.getTriByte();
			fileOffsets[i1] = l;
			l += onDiskFileSizes[i1];
		}
	}

	public byte[] getDataForName(String name) {
		byte data[] = null;
		int i = 0;
		name = name.toUpperCase();
		for(int j = 0; j < name.length(); j++) {
			i = (i * 61 + name.charAt(j)) - 32;
		}
		
		for(int k = 0; k < dataSize; k++)
			if(nameIndexes[k] == i) {
				if(data == null)
					data = new byte[fileSizes[k]];
				if(!isCompressed) {
					BZ2InputStream.decompressBuffer(data, fileSizes[k], outputData, onDiskFileSizes[k], fileOffsets[k]);
				} else {
					for(int l = 0; l < fileSizes[k]; l++) {
						data[l] = outputData[fileOffsets[k] + l];
					}
				}
				return data;
			}

		return null;
	}

}
