package com.runescape.io;
import java.math.BigInteger;

import com.runescape.NodeList;
import com.runescape.NodeSub;
import com.runescape.sign.Signlink;

public class ByteBuffer extends NodeSub {

	public static final int anIntArray1457[] = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };
	public IsaacCipher encryption;
	public static NodeList nodeList = new NodeList(true);
	public byte payload[];
	public int position;
	public int bitPosition;
	public static int pop;
	private static final BigInteger rsaModulus = new BigInteger("108624200374573610186158874989173249480271063850664536021411519641676158455634506443247873589723861746525311021125441283923237562487401954091346575284582461457453013951789999002029406051097514683965194004030886405056156793281686698698469498749182113160974980338065817624942861880069691102861920480342248733383");
	private static final BigInteger rsaExponent = new BigInteger("65537");

	public static ByteBuffer create(int size) {
		synchronized(nodeList) {
			ByteBuffer byteStream_1 = null;
			if(pop > 0) {
				pop--;
				byteStream_1 = (ByteBuffer) nodeList.method157();
			}
			if(byteStream_1 != null) {
				byteStream_1.position = 0;
				return byteStream_1;
			}
		}
		ByteBuffer byteStream_2 = new ByteBuffer();
		byteStream_2.position = 0;
		byteStream_2.payload = new byte[size];
		return byteStream_2;
	}

	public static ByteBuffer create() {
		return create(ClientSocket.SIZE);
	}

	public ByteBuffer() {  }

	public ByteBuffer(byte payload[]) {
		this.payload = payload;
		position = 0;
	}

	public void createFrame(int opcode) {
		payload[position++] = (byte) (opcode + encryption.getNextValue());
	}

	public void putByte(int i) {
		payload[position++] = (byte) i;
	}

	public void putShort(int i) {
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	public void putLEShort(int i) {
		payload[position++] = (byte) i;
		payload[position++] = (byte) (i >> 8);
	}

	public void putTriByte(int i) {
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	public void putInt(int i) {
		payload[position++] = (byte) (i >> 24);
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	public void putLEInt(int i, boolean flag) {
		payload[position++] = (byte) i;
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 24);
	}

	public void putLong(long l) {
		try {
			payload[position++] = (byte) (int) (l >> 56);
			payload[position++] = (byte) (int) (l >> 48);
			payload[position++] = (byte) (int) (l >> 40);
			payload[position++] = (byte) (int) (l >> 32);
			payload[position++] = (byte) (int) (l >> 24);
			payload[position++] = (byte) (int) (l >> 16);
			payload[position++] = (byte) (int) (l >> 8);
			payload[position++] = (byte) (int) l;
		} catch(RuntimeException runtimeexception) {
			Signlink.reporterror("88423, " + l + ", " + runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	public void writeString(String s) {
		//DEPRICATED - s.getBytes(0, s.length(), payload, currentOffset);
		System.arraycopy(s.getBytes(), 0, payload, position, s.length());
		position += s.length();
		payload[position++] = 10;
	}

	public void putBytes(byte bytes[], int i, int j, int k) {
		for(int l = k; l < k + j; l++)
			payload[position++] = bytes[l];

	}

	public void putSizeByte(int i) {
		payload[position - i - 1] = (byte) i;
	}

	public int getUnsignedByte() {
		return payload[position++] & 0xff;
	}

	public byte getByte() {
		return payload[position++];
	}

	public int getUnsignedShort() {
		position += 2;
		return ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public int getShort() {
		position += 2;
		int i = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
		if(i > 32767)
			i -= 0x10000;
		return i;
	}

	public int getTriByte() {
		position += 3;
		return ((payload[position - 3] & 0xff) << 16) + ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public int getInt() {
		position += 4;
		return ((payload[position - 4] & 0xff) << 24) + ((payload[position - 3] & 0xff) << 16) + ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public long getLong() {
		long l = (long) getInt() & 0xffffffffL;
		long l1 = (long) getInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String getRS2String() {
		int i = position;
		while(payload[position++] != 10)
			;
		return new String(payload, i, position - i - 1);
	}

	public byte[] getBytes() {
		int j = position;
		while(payload[position++] != 10);
		byte abyte0[] = new byte[position - j - 1];
		for(int k = j; k < position - 1; k++)
			abyte0[k - j] = payload[k];

		return abyte0;
	}

	public void method530(int i, int j, int k, byte abyte0[]) {
		for(int l = j; l < j + i; l++)
			abyte0[l] = payload[position++];

	}

	public void method531(byte byte0) {
		bitPosition = position * 8;
		if(byte0 == 6)
			byte0 = 0;
	}

	public int method532(int i, int j) {
		int k = bitPosition >> 3;
		int l = 8 - (bitPosition & 7);
		int i1 = 0;
		bitPosition += j;
		for(; j > l; l = 8) {
			i1 += (payload[k++] & anIntArray1457[l]) << j - l;
			j -= l;
		}

		if(j == l)
			i1 += payload[k] & anIntArray1457[l];
		else
			i1 += payload[k] >> l - j & anIntArray1457[j];
		return i1;
	}

	public void finishBitAccess() {
		position = (bitPosition + 7) / 8;
	}

	public int method534() {
		int i = payload[position] & 0xff;
		if(i < 128)
			return getUnsignedByte() - 64;
		else
			return getUnsignedShort() - 49152;
	}

	public int method535() {
		int i = payload[position] & 0xff;
		if(i < 128)
			return getUnsignedByte();
		else
			return getUnsignedShort() - 32768;
	}

	public void method537(boolean flag, int i) {
		payload[position++] = (byte) (i + 128);
		if(flag) {
			for(int j = 1; j > 0; j++)
				;
		}
	}

	public void method538(byte byte0, int i) {
		if(byte0 == 0) {
			byte0 = 0;
		} else {
			for(int j = 1; j > 0; j++)
				;
		}
		payload[position++] = (byte) (-i);
	}

	public void writeByteS(int i, int j) {
		if(j != 1) {
			return;
		} else {
			payload[position++] = (byte) (128 - i);
			return;
		}
	}

	public int method540(int i) {
		return payload[position++] - 128 & 0xff;
	}

	public int method541(int i) {
		return -payload[position++] & 0xff;
	}

	public int method542(int i) {
		i = 77 / i;
		return 128 - payload[position++] & 0xff;
	}

	public byte method543(int i) {
		if(i != 0) {
			for(int j = 1; j > 0; j++)
				;
		}
		return (byte) (payload[position++] - 128);
	}

	public byte method544(int i) {
		return (byte) (-payload[position++]);
	}

	public byte method545(int i) {
		if(i != 43428) {
			for(int j = 1; j > 0; j++)
				;
		}
		return (byte) (128 - payload[position++]);
	}

	public void writeLEShort(int i, int j) {
		payload[position++] = (byte) j;
		payload[position++] = (byte) (j >> 8);
	}

	public void writeShortA(int i, int j) {
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) (i + 128);
		if(j == 0)
			;
	}

	public void method548(int i, int j) {
		payload[position++] = (byte) (j + 128);
		payload[position++] = (byte) (j >> 8);
	}

	public int getUnsignedLEShort() {
		position += 2;
		return ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
	}

	public int getUnsignedShortA() {
		position += 2;
		return ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] - 128 & 0xff);
	}

	public int getUnsignedLEShortA() {
		position += 2;
		return ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] - 128 & 0xff);
	}

	public int getLEShort() {
		position += 2;
		int j = ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
		if(j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method553(byte byte0) {
		position += 2;
		int i = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] - 128 & 0xff);
		if(i > 32767)
			i -= 0x10000;
		return i;
	}

	public int method554(int i) {
		position += 3;
		if(i >= 0)
			return 1;
		else
			return ((payload[position - 2] & 0xff) << 16) + ((payload[position - 3] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public int method555(int i) {
		position += 4;
		return ((payload[position - 1] & 0xff) << 24) + ((payload[position - 2] & 0xff) << 16) + ((payload[position - 3] & 0xff) << 8) + (payload[position - 4] & 0xff);
	}

	public int method556(int i) {
		position += 4;
		return ((payload[position - 2] & 0xff) << 24) + ((payload[position - 1] & 0xff) << 16) + ((payload[position - 4] & 0xff) << 8) + (payload[position - 3] & 0xff);
	}

	public int method557(boolean flag) {
		position += 4;
		if(!flag) {
			for(int i = 1; i > 0; i++)
				;
		}
		return ((payload[position - 3] & 0xff) << 24) + ((payload[position - 4] & 0xff) << 16) + ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
	}

	public void method558(byte byte0, byte abyte0[], int i, int j) {
		if(byte0 != -73)
			return;
		for(int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = payload[position++];

	}

	public void method559(byte abyte0[], int i, int j, int k) {
		for(int l = k; l < k + i; l++)
			abyte0[l] = (byte) (payload[position++] - 128);

	}

	public void startRSAEncryption() {
		int j = position;
		position = 0;
		byte abyte0[] = new byte[j];
		method530(j, 0, -21, abyte0);
		BigInteger biginteger2 = new BigInteger(abyte0);
		BigInteger biginteger3 = biginteger2.modPow(rsaExponent, rsaModulus);
		byte abyte1[] = biginteger3.toByteArray();
		position = 0;
		putByte(abyte1.length);
		putBytes(abyte1, 0, abyte1.length, 0);
	}
	
}
