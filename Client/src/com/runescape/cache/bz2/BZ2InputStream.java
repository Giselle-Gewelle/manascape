package com.runescape.cache.bz2;

public class BZ2InputStream {

	public static BZ2Block block = new BZ2Block();

	public static int decompressBuffer(byte abyte0[], int decompressedSize, byte abyte1[], int j, int k) {
		synchronized(block) {
			block.input = abyte1;
			block.nextInput = k;
			block.aByteArray52 = abyte0;
			block.anInt53 = 0;
			block.compressedSize = j;
			block.decompressedSize = decompressedSize;
			block.bsLive = 0;
			block.bsBuff = 0;
			block.totalInputLo32 = 0;
			block.totalInputHi32 = 0;
			block.anInt55 = 0;
			block.anInt56 = 0;
			block.anInt63 = 0;
			decompress(block);
			decompressedSize -= block.decompressedSize;
			return decompressedSize;
		}
	}

	public static void getNextFileHeader(BZ2Block archive) {
		byte byte4 = archive.aByte57;
		int i = archive.anInt58;
		int j = archive.anInt68;
		int k = archive.anInt66;
		int ai[] = BZ2Block.ll8;
		int l = archive.anInt65;
		byte abyte0[] = archive.aByteArray52;
		int i1 = archive.anInt53;
		int j1 = archive.decompressedSize;
		int k1 = j1;
		int l1 = archive.anInt85 + 1;
		label0: do {
			if(i > 0) {
				do {
					if(j1 == 0)
						break label0;
					if(i == 1)
						break;
					abyte0[i1] = byte4;
					i--;
					i1++;
					j1--;
				} while(true);
				if(j1 == 0) {
					i = 1;
					break;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
			}
			boolean flag = true;
			while(flag) {
				flag = false;
				if(j == l1) {
					i = 0;
					break label0;
				}
				byte4 = (byte) k;
				l = ai[l];
				byte byte0 = (byte) (l & 0xff);
				l >>= 8;
				j++;
				if(byte0 != k) {
					k = byte0;
					if(j1 == 0) {
						i = 1;
					} else {
						abyte0[i1] = byte4;
						i1++;
						j1--;
						flag = true;
						continue;
					}
					break label0;
				}
				if(j != l1)
					continue;
				if(j1 == 0) {
					i = 1;
					break label0;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
				flag = true;
			}
			i = 2;
			l = ai[l];
			byte byte1 = (byte) (l & 0xff);
			l >>= 8;
			if(++j != l1)
				if(byte1 != k) {
					k = byte1;
				} else {
					i = 3;
					l = ai[l];
					byte byte2 = (byte) (l & 0xff);
					l >>= 8;
					if(++j != l1)
						if(byte2 != k) {
							k = byte2;
						} else {
							l = ai[l];
							byte byte3 = (byte) (l & 0xff);
							l >>= 8;
							j++;
							i = (byte3 & 0xff) + 4;
							l = ai[l];
							k = (byte) (l & 0xff);
							l >>= 8;
							j++;
						}
				}
		} while(true);
		int i2 = archive.anInt55;
		archive.anInt55 += k1 - j1;
		if(archive.anInt55 < i2)
			archive.anInt56++;
		archive.aByte57 = byte4;
		archive.anInt58 = i;
		archive.anInt68 = j;
		archive.anInt66 = k;
		BZ2Block.ll8 = ai;
		archive.anInt65 = l;
		archive.aByteArray52 = abyte0;
		archive.anInt53 = i1;
		archive.decompressedSize = j1;
	}

	public static void decompress(BZ2Block bz2Block) {
		int tMinLen = 0;
		int tLimit[] = null;
		int tBase[] = null;
		int tPerm[] = null;
		bz2Block.blockSize100k = 1;
		if(BZ2Block.ll8 == null)
			BZ2Block.ll8 = new int[bz2Block.blockSize100k * 0x186a0];
		
		boolean reading = true;
		while(reading) {
			byte head = readUChar(bz2Block);
			if(head == 23)
				return;
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			bz2Block.anInt63++;
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readUChar(bz2Block);
			head = readBit(bz2Block);
			
			if(head != 0)
				bz2Block.randomized = true;
			else
				bz2Block.randomized = false;
			if(bz2Block.randomized)
				System.out.println("PANIC! RANDOMISED BLOCK!");
			
			bz2Block.origPointer = 0;
			head = readUChar(bz2Block);
			bz2Block.origPointer = bz2Block.origPointer << 8 | head & 0xff;
			head = readUChar(bz2Block);
			bz2Block.origPointer = bz2Block.origPointer << 8 | head & 0xff;
			head = readUChar(bz2Block);
			bz2Block.origPointer = bz2Block.origPointer << 8 | head & 0xff;
			
			for(int j = 0; j < 16; j++) {
				byte byte1 = readBit(bz2Block);
				if(byte1 == 1)
					bz2Block.aBooleanArray74[j] = true;
				else
					bz2Block.aBooleanArray74[j] = false;
			}

			for(int k = 0; k < 256; k++)
				bz2Block.inUse[k] = false;

			for(int l = 0; l < 16; l++)
				if(bz2Block.aBooleanArray74[l]) {
					for(int i3 = 0; i3 < 16; i3++) {
						byte byte2 = readBit(bz2Block);
						if(byte2 == 1)
							bz2Block.inUse[l * 16 + i3] = true;
					}

				}

			makeMaps(bz2Block);
			int i4 = bz2Block.nInUse + 2;
			int j4 = getBits(3, bz2Block);
			int k4 = getBits(15, bz2Block);
			for(int i1 = 0; i1 < k4; i1++) {
				int j3 = 0;
				do {
					byte byte3 = readBit(bz2Block);
					if(byte3 == 0)
						break;
					j3++;
				} while(true);
				bz2Block.aByteArray79[i1] = (byte) j3;
			}

			byte abyte0[] = new byte[6];
			for(byte byte16 = 0; byte16 < j4; byte16++)
				abyte0[byte16] = byte16;

			for(int j1 = 0; j1 < k4; j1++) {
				byte byte17 = bz2Block.aByteArray79[j1];
				byte byte15 = abyte0[byte17];
				for(; byte17 > 0; byte17--)
					abyte0[byte17] = abyte0[byte17 - 1];

				abyte0[0] = byte15;
				bz2Block.aByteArray78[j1] = byte15;
			}

			for(int k3 = 0; k3 < j4; k3++) {
				int l6 = getBits(5, bz2Block);
				for(int k1 = 0; k1 < i4; k1++) {
					do {
						byte byte4 = readBit(bz2Block);
						if(byte4 == 0)
							break;
						byte4 = readBit(bz2Block);
						if(byte4 == 0)
							l6++;
						else
							l6--;
					} while(true);
					bz2Block.aByteArrayArray80[k3][k1] = (byte) l6;
				}

			}

			for(int l3 = 0; l3 < j4; l3++) {
				byte byte8 = 32;
				int i = 0;
				for(int l1 = 0; l1 < i4; l1++) {
					if(bz2Block.aByteArrayArray80[l3][l1] > i)
						i = bz2Block.aByteArrayArray80[l3][l1];
					if(bz2Block.aByteArrayArray80[l3][l1] < byte8)
						byte8 = bz2Block.aByteArrayArray80[l3][l1];
				}

				createDecodeTables(bz2Block.anIntArrayArray81[l3], bz2Block.anIntArrayArray82[l3], bz2Block.anIntArrayArray83[l3], bz2Block.aByteArrayArray80[l3], byte8, i, i4);
				bz2Block.anIntArray84[l3] = byte8;
			}

			int l4 = bz2Block.nInUse + 1;
			int i5 = -1;
			int j5 = 0;
			for(int i2 = 0; i2 <= 255; i2++)
				bz2Block.anIntArray67[i2] = 0;

			int j9 = 4095;
			for(int l8 = 15; l8 >= 0; l8--) {
				for(int i9 = 15; i9 >= 0; i9--) {
					bz2Block.aByteArray76[j9] = (byte) (l8 * 16 + i9);
					j9--;
				}

				bz2Block.anIntArray77[l8] = j9 + 1;
			}

			int i6 = 0;
			if(j5 == 0) {
				i5++;
				j5 = 50;
				byte byte12 = bz2Block.aByteArray78[i5];
				tMinLen = bz2Block.anIntArray84[byte12];
				tLimit = bz2Block.anIntArrayArray81[byte12];
				tPerm = bz2Block.anIntArrayArray83[byte12];
				tBase = bz2Block.anIntArrayArray82[byte12];
			}
			j5--;
			int i7 = tMinLen;
			int l7;
			byte byte9;
			for(l7 = getBits(i7, bz2Block); l7 > tLimit[i7]; l7 = l7 << 1 | byte9) {
				i7++;
				byte9 = readBit(bz2Block);
			}

			for(int k5 = tPerm[l7 - tBase[i7]]; k5 != l4;)
				if(k5 == 0 || k5 == 1) {
					int j6 = -1;
					int k6 = 1;
					do {
						if(k5 == 0)
							j6 += k6;
						else if(k5 == 1)
							j6 += 2 * k6;
						k6 *= 2;
						if(j5 == 0) {
							i5++;
							j5 = 50;
							byte byte13 = bz2Block.aByteArray78[i5];
							tMinLen = bz2Block.anIntArray84[byte13];
							tLimit = bz2Block.anIntArrayArray81[byte13];
							tPerm = bz2Block.anIntArrayArray83[byte13];
							tBase = bz2Block.anIntArrayArray82[byte13];
						}
						j5--;
						int j7 = tMinLen;
						int i8;
						byte byte10;
						for(i8 = getBits(j7, bz2Block); i8 > tLimit[j7]; i8 = i8 << 1 | byte10) {
							j7++;
							byte10 = readBit(bz2Block);
						}

						k5 = tPerm[i8 - tBase[j7]];
					} while(k5 == 0 || k5 == 1);
					j6++;
					byte byte5 = bz2Block.seqToUnseq[bz2Block.aByteArray76[bz2Block.anIntArray77[0]] & 0xff];
					bz2Block.anIntArray67[byte5 & 0xff] += j6;
					for(; j6 > 0; j6--) {
						BZ2Block.ll8[i6] = byte5 & 0xff;
						i6++;
					}

				} else {
					int j11 = k5 - 1;
					byte byte6;
					if(j11 < 16) {
						int j10 = bz2Block.anIntArray77[0];
						byte6 = bz2Block.aByteArray76[j10 + j11];
						for(; j11 > 3; j11 -= 4) {
							int k11 = j10 + j11;
							bz2Block.aByteArray76[k11] = bz2Block.aByteArray76[k11 - 1];
							bz2Block.aByteArray76[k11 - 1] = bz2Block.aByteArray76[k11 - 2];
							bz2Block.aByteArray76[k11 - 2] = bz2Block.aByteArray76[k11 - 3];
							bz2Block.aByteArray76[k11 - 3] = bz2Block.aByteArray76[k11 - 4];
						}

						for(; j11 > 0; j11--)
							bz2Block.aByteArray76[j10 + j11] = bz2Block.aByteArray76[(j10 + j11) - 1];

						bz2Block.aByteArray76[j10] = byte6;
					} else {
						int l10 = j11 / 16;
						int i11 = j11 % 16;
						int k10 = bz2Block.anIntArray77[l10] + i11;
						byte6 = bz2Block.aByteArray76[k10];
						for(; k10 > bz2Block.anIntArray77[l10]; k10--)
							bz2Block.aByteArray76[k10] = bz2Block.aByteArray76[k10 - 1];

						bz2Block.anIntArray77[l10]++;
						for(; l10 > 0; l10--) {
							bz2Block.anIntArray77[l10]--;
							bz2Block.aByteArray76[bz2Block.anIntArray77[l10]] = bz2Block.aByteArray76[(bz2Block.anIntArray77[l10 - 1] + 16) - 1];
						}

						bz2Block.anIntArray77[0]--;
						bz2Block.aByteArray76[bz2Block.anIntArray77[0]] = byte6;
						if(bz2Block.anIntArray77[0] == 0) {
							int i10 = 4095;
							for(int k9 = 15; k9 >= 0; k9--) {
								for(int l9 = 15; l9 >= 0; l9--) {
									bz2Block.aByteArray76[i10] = bz2Block.aByteArray76[bz2Block.anIntArray77[k9] + l9];
									i10--;
								}

								bz2Block.anIntArray77[k9] = i10 + 1;
							}

						}
					}
					bz2Block.anIntArray67[bz2Block.seqToUnseq[byte6 & 0xff] & 0xff]++;
					BZ2Block.ll8[i6] = bz2Block.seqToUnseq[byte6 & 0xff] & 0xff;
					i6++;
					if(j5 == 0) {
						i5++;
						j5 = 50;
						byte byte14 = bz2Block.aByteArray78[i5];
						tMinLen = bz2Block.anIntArray84[byte14];
						tLimit = bz2Block.anIntArrayArray81[byte14];
						tPerm = bz2Block.anIntArrayArray83[byte14];
						tBase = bz2Block.anIntArrayArray82[byte14];
					}
					j5--;
					int k7 = tMinLen;
					int j8;
					byte byte11;
					for(j8 = getBits(k7, bz2Block); j8 > tLimit[k7]; j8 = j8 << 1 | byte11) {
						k7++;
						byte11 = readBit(bz2Block);
					}

					k5 = tPerm[j8 - tBase[k7]];
				}

			bz2Block.anInt58 = 0;
			bz2Block.aByte57 = 0;
			bz2Block.anIntArray69[0] = 0;
			for(int j2 = 1; j2 <= 256; j2++)
				bz2Block.anIntArray69[j2] = bz2Block.anIntArray67[j2 - 1];

			for(int k2 = 1; k2 <= 256; k2++)
				bz2Block.anIntArray69[k2] += bz2Block.anIntArray69[k2 - 1];

			for(int l2 = 0; l2 < i6; l2++) {
				byte byte7 = (byte) (BZ2Block.ll8[l2] & 0xff);
				BZ2Block.ll8[bz2Block.anIntArray69[byte7 & 0xff]] |= l2 << 8;
				bz2Block.anIntArray69[byte7 & 0xff]++;
			}

			bz2Block.anInt65 = BZ2Block.ll8[bz2Block.origPointer] >> 8;
			bz2Block.anInt68 = 0;
			bz2Block.anInt65 = BZ2Block.ll8[bz2Block.anInt65];
			bz2Block.anInt66 = (byte) (bz2Block.anInt65 & 0xff);
			bz2Block.anInt65 >>= 8;
			bz2Block.anInt68++;
			bz2Block.anInt85 = i6;
			getNextFileHeader(bz2Block);
			if(bz2Block.anInt68 == bz2Block.anInt85 + 1 && bz2Block.anInt58 == 0)
				reading = true;
			else
				reading = false;
		}
	}

	public static byte readUChar(BZ2Block bz2Block) {
		return (byte) getBits(8, bz2Block);
	}

	public static byte readBit(BZ2Block bz2Block) {
		return (byte) getBits(1, bz2Block);
	}

	public static int getBits(int i, BZ2Block bz2Block) {
		int j;
		do {
			if(bz2Block.bsLive >= i) {
				int k = bz2Block.bsBuff >> bz2Block.bsLive - i & (1 << i) - 1;
				bz2Block.bsLive -= i;
				j = k;
				break;
			}
			bz2Block.bsBuff = bz2Block.bsBuff << 8 | bz2Block.input[bz2Block.nextInput] & 0xff;
			bz2Block.bsLive += 8;
			bz2Block.nextInput++;
			bz2Block.compressedSize--;
			bz2Block.totalInputLo32++;
			if(bz2Block.totalInputLo32 == 0)
				bz2Block.totalInputHi32++;
		} while(true);
		return j;
	}

	public static void makeMaps(BZ2Block bz2Block) {
		bz2Block.nInUse = 0;
		for(int i = 0; i < 256; i++) {
			if(bz2Block.inUse[i]) {
				bz2Block.seqToUnseq[bz2Block.nInUse] = (byte) i;
				bz2Block.nInUse++;
			}
		}
	}

	public static void createDecodeTables(int limit[], int base[], int perm[], byte len[], int minLen, int maxLen, int alphaSize) {
		int l = 0;
		for(int i1 = minLen; i1 <= maxLen; i1++) {
			for(int l2 = 0; l2 < alphaSize; l2++) {
				if(len[l2] == i1) {
					perm[l] = l2;
					l++;
				}
			}
		}

		for(int j1 = 0; j1 < 23; j1++)
			base[j1] = 0;

		for(int k1 = 0; k1 < alphaSize; k1++)
			base[len[k1] + 1]++;

		for(int l1 = 1; l1 < 23; l1++)
			base[l1] += base[l1 - 1];

		for(int i2 = 0; i2 < 23; i2++)
			limit[i2] = 0;

		int i3 = 0;
		for(int j2 = minLen; j2 <= maxLen; j2++) {
			i3 += base[j2 + 1] - base[j2];
			limit[j2] = i3 - 1;
			i3 <<= 1;
		}

		for(int k2 = minLen + 1; k2 <= maxLen; k2++) {
			base[k2] = (limit[k2 - 1] + 1 << 1) - base[k2];
		}
	}

}
