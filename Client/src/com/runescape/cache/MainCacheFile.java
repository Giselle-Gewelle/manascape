package com.runescape.cache;

import java.io.*;

public class MainCacheFile {

	private static final int MAX_FILE_SIZE = 0x927c0;
	private static final int BUFFER_SIZE = 520;
	
	private static byte desiredFileBuffer[] = new byte[BUFFER_SIZE];
	
	private final RandomAccessFile dataFile;
	private final RandomAccessFile indexFile;
	private final int cacheFileIndex;

	public MainCacheFile(int cacheFileIndex, RandomAccessFile dataFile, RandomAccessFile indexFile) {
		this.cacheFileIndex = cacheFileIndex;
		this.dataFile = dataFile;
		this.indexFile = indexFile;
	}

	public synchronized byte[] decompressFile(int fileIndex) {
		try {
			seek(fileIndex * 6, indexFile);
			
			int l;
			for(int j = 0; j < 6; j += l) {
				l = indexFile.read(desiredFileBuffer, j, 6 - j);
				if(l == -1)
					return null;
			}

			int fileSize = ((desiredFileBuffer[0] & 0xff) << 16) + ((desiredFileBuffer[1] & 0xff) << 8) + (desiredFileBuffer[2] & 0xff);
			int j1 = ((desiredFileBuffer[3] & 0xff) << 16) + ((desiredFileBuffer[4] & 0xff) << 8) + (desiredFileBuffer[5] & 0xff);
			
			if(fileSize < 0 || fileSize > MAX_FILE_SIZE)
				return null;
			
			if(j1 <= 0 || (long) j1 > dataFile.length() / 520L)
				return null;
			
			byte fileBuffer[] = new byte[fileSize];
			int fileBufferIndex = 0;
			
			for(int l1 = 0; fileBufferIndex < fileSize; l1++) {
				if(j1 == 0)
					return null;
				seek(j1 * BUFFER_SIZE, dataFile);
				int k = 0;
				int i2 = fileSize - fileBufferIndex;
				if(i2 > 512)
					i2 = 512;
				int j2;
				for(; k < i2 + 8; k += j2) {
					j2 = dataFile.read(desiredFileBuffer, k, (i2 + 8) - k);
					if(j2 == -1)
						return null;
				}

				int k2 = ((desiredFileBuffer[0] & 0xff) << 8) + (desiredFileBuffer[1] & 0xff);
				int l2 = ((desiredFileBuffer[2] & 0xff) << 8) + (desiredFileBuffer[3] & 0xff);
				int i3 = ((desiredFileBuffer[4] & 0xff) << 16) + ((desiredFileBuffer[5] & 0xff) << 8) + (desiredFileBuffer[6] & 0xff);
				int j3 = desiredFileBuffer[7] & 0xff;
				
				if(k2 != fileIndex || l2 != l1 || j3 != cacheFileIndex)
					return null;
				
				if(i3 < 0 || (long) i3 > dataFile.length() / BUFFER_SIZE)
					return null;
				
				for(int k3 = 0; k3 < i2; k3++)
					fileBuffer[fileBufferIndex++] = desiredFileBuffer[k3 + 8];

				j1 = i3;
			}

			return fileBuffer;
		} catch(IOException _ex) {
			return null;
		}
	}

	public synchronized boolean method293(int i, boolean flag, byte abyte0[], int j) {
		if(!flag) {
			for(int k = 1; k > 0; k++)
				;
		}
		boolean flag1 = method294(abyte0, j, true, i);
		if(!flag1)
			flag1 = method294(abyte0, j, false, i);
		return flag1;
	}

	public synchronized boolean method294(byte abyte0[], int j, boolean flag, int k) {
		try {
			int l;
			if(flag) {
				seek(j * 6, indexFile);
				int k1;
				for(int i1 = 0; i1 < 6; i1 += k1) {
					k1 = indexFile.read(desiredFileBuffer, i1, 6 - i1);
					if(k1 == -1)
						return false;
				}

				l = ((desiredFileBuffer[3] & 0xff) << 16) + ((desiredFileBuffer[4] & 0xff) << 8) + (desiredFileBuffer[5] & 0xff);
				if(l <= 0 || (long) l > dataFile.length() / 520L)
					return false;
			} else {
				l = (int) ((dataFile.length() + 519L) / 520L);
				if(l == 0)
					l = 1;
			}
			desiredFileBuffer[0] = (byte) (k >> 16);
			desiredFileBuffer[1] = (byte) (k >> 8);
			desiredFileBuffer[2] = (byte) k;
			desiredFileBuffer[3] = (byte) (l >> 16);
			desiredFileBuffer[4] = (byte) (l >> 8);
			desiredFileBuffer[5] = (byte) l;
			seek(j * 6, indexFile);
			indexFile.write(desiredFileBuffer, 0, 6);
			int j1 = 0;
			for(int l1 = 0; j1 < k; l1++) {
				int i2 = 0;
				if(flag) {
					seek(l * 520, dataFile);
					int j2;
					int l2;
					for(j2 = 0; j2 < 8; j2 += l2) {
						l2 = dataFile.read(desiredFileBuffer, j2, 8 - j2);
						if(l2 == -1)
							break;
					}

					if(j2 == 8) {
						int i3 = ((desiredFileBuffer[0] & 0xff) << 8) + (desiredFileBuffer[1] & 0xff);
						int j3 = ((desiredFileBuffer[2] & 0xff) << 8) + (desiredFileBuffer[3] & 0xff);
						i2 = ((desiredFileBuffer[4] & 0xff) << 16) + ((desiredFileBuffer[5] & 0xff) << 8) + (desiredFileBuffer[6] & 0xff);
						int k3 = desiredFileBuffer[7] & 0xff;
						if(i3 != j || j3 != l1 || k3 != cacheFileIndex)
							return false;
						if(i2 < 0 || (long) i2 > dataFile.length() / 520L)
							return false;
					}
				}
				if(i2 == 0) {
					flag = false;
					i2 = (int) ((dataFile.length() + 519L) / 520L);
					if(i2 == 0)
						i2++;
					if(i2 == l)
						i2++;
				}
				if(k - j1 <= 512)
					i2 = 0;
				desiredFileBuffer[0] = (byte) (j >> 8);
				desiredFileBuffer[1] = (byte) j;
				desiredFileBuffer[2] = (byte) (l1 >> 8);
				desiredFileBuffer[3] = (byte) l1;
				desiredFileBuffer[4] = (byte) (i2 >> 16);
				desiredFileBuffer[5] = (byte) (i2 >> 8);
				desiredFileBuffer[6] = (byte) i2;
				desiredFileBuffer[7] = (byte) cacheFileIndex;
				seek(l * 520, dataFile);
				dataFile.write(desiredFileBuffer, 0, 8);
				int k2 = k - j1;
				if(k2 > 512)
					k2 = 512;
				dataFile.write(abyte0, j1, k2);
				j1 += k2;
				l = i2;
			}

			return true;
		} catch(IOException _ex) {
			return false;
		}
	}

	public synchronized void seek(int pos, RandomAccessFile file) throws IOException {
		if(pos < 0 || pos > 0x3c00000) {
			System.out.println("Badseek - pos:" + pos + " len:" + file.length());
			pos = 0x3c00000;
			
			try {
				Thread.sleep(1000L);
			} catch(Exception e) {}
		}
		
		file.seek(pos);
	}

}
