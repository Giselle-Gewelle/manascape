package com.runescape;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class IndexedImage extends DrawingArea {

	public IndexedImage(CacheArchive class2, String s, int i) {
		anInt1509 = 3;
		aBoolean1510 = true;
		anInt1512 = -235;
		aByte1513 = 5;
		anInt1514 = -3539;
		aBoolean1515 = true;
		ByteBuffer byteStream = new ByteBuffer(class2.getDataForName(s + ".dat"));
		ByteBuffer byteStream_1 = new ByteBuffer(class2.getDataForName("index.dat"));
		byteStream_1.position = byteStream.getUnsignedShort();
		anInt1522 = byteStream_1.getUnsignedShort();
		anInt1523 = byteStream_1.getUnsignedShort();
		int j = byteStream_1.getUnsignedByte();
		anIntArray1517 = new int[j];
		for(int k = 0; k < j - 1; k++)
			anIntArray1517[k + 1] = byteStream_1.getTriByte();

		for(int l = 0; l < i; l++) {
			byteStream_1.position += 2;
			byteStream.position += byteStream_1.getUnsignedShort() * byteStream_1.getUnsignedShort();
			byteStream_1.position++;
		}

		xDrawOffset = byteStream_1.getUnsignedByte();
		yDrawOffset = byteStream_1.getUnsignedByte();
		imgWidth = byteStream_1.getUnsignedShort();
		imgHeight = byteStream_1.getUnsignedShort();
		int i1 = byteStream_1.getUnsignedByte();
		int j1 = imgWidth * imgHeight;
		imgPixels = new byte[j1];
		if(i1 == 0) {
			for(int k1 = 0; k1 < j1; k1++)
				imgPixels[k1] = byteStream.getByte();

			return;
		}
		if(i1 == 1) {
			for(int l1 = 0; l1 < imgWidth; l1++) {
				for(int i2 = 0; i2 < imgHeight; i2++)
					imgPixels[l1 + i2 * imgWidth] = byteStream.getByte();

			}

		}
	}
	
	public void setHeight(int height) {
		imgHeight = height;
	}

	public void method485(int i) {
		anInt1522 /= 2;
		anInt1523 /= 2;
		byte abyte0[] = new byte[anInt1522 * anInt1523];
		int j = 0;
		if(i != 0)
			return;
		for(int k = 0; k < imgHeight; k++) {
			for(int l = 0; l < imgWidth; l++)
				abyte0[(l + xDrawOffset >> 1) + (k + yDrawOffset >> 1) * anInt1522] = imgPixels[j++];

		}

		imgPixels = abyte0;
		imgWidth = anInt1522;
		imgHeight = anInt1523;
		xDrawOffset = 0;
		yDrawOffset = 0;
	}

	public void method486(boolean flag) {
		if(imgWidth == anInt1522 && imgHeight == anInt1523)
			return;
		byte abyte0[] = new byte[anInt1522 * anInt1523];
		int i = 0;
		for(int j = 0; j < imgHeight; j++) {
			for(int k = 0; k < imgWidth; k++)
				abyte0[k + xDrawOffset + (j + yDrawOffset) * anInt1522] = imgPixels[i++];

		}

		imgPixels = abyte0;
		imgWidth = anInt1522;
		if(!flag) {
			return;
		} else {
			imgHeight = anInt1523;
			xDrawOffset = 0;
			yDrawOffset = 0;
			return;
		}
	}

	public void method487(int i) {
		byte abyte0[] = new byte[imgWidth * imgHeight];
		int j = 0;
		for(int k = 0; k < imgHeight; k++) {
			for(int l = imgWidth - 1; l >= 0; l--)
				abyte0[j++] = imgPixels[l + k * imgWidth];

		}

		imgPixels = abyte0;
		if(i != 0) {
			return;
		} else {
			xDrawOffset = anInt1522 - imgWidth - xDrawOffset;
			return;
		}
	}

	public void method488(byte byte0) {
		byte abyte0[] = new byte[imgWidth * imgHeight];
		int i = 0;
		if(byte0 != 7)
			aBoolean1515 = !aBoolean1515;
		for(int j = imgHeight - 1; j >= 0; j--) {
			for(int k = 0; k < imgWidth; k++)
				abyte0[i++] = imgPixels[k + j * imgWidth];

		}

		imgPixels = abyte0;
		yDrawOffset = anInt1523 - imgHeight - yDrawOffset;
	}

	public void method489(int i, int j, int k, int l) {
		for(int i1 = 0; i1 < anIntArray1517.length; i1++) {
			int j1 = anIntArray1517[i1] >> 16 & 0xff;
			j1 += k;
			if(j1 < 0)
				j1 = 0;
			else if(j1 > 255)
				j1 = 255;
			int k1 = anIntArray1517[i1] >> 8 & 0xff;
			k1 += j;
			if(k1 < 0)
				k1 = 0;
			else if(k1 > 255)
				k1 = 255;
			int l1 = anIntArray1517[i1] & 0xff;
			l1 += i;
			if(l1 < 0)
				l1 = 0;
			else if(l1 > 255)
				l1 = 255;
			anIntArray1517[i1] = (j1 << 16) + (k1 << 8) + l1;
		}

		if(l == anInt1512)
			;
	}

	public void drawImage(int x, int y) {
		x += xDrawOffset;
		y += yDrawOffset;
		int i1 = x + y * DrawingArea.width;
		int j1 = 0;
		int k1 = imgHeight;
		int l1 = imgWidth;
		int i2 = DrawingArea.width - l1;
		int j2 = 0;
		if(y < DrawingArea.viewportTop) {
			int k2 = DrawingArea.viewportTop - y;
			k1 -= k2;
			y = DrawingArea.viewportTop;
			j1 += k2 * l1;
			i1 += k2 * DrawingArea.width;
		}
		if(y + k1 > DrawingArea.viewportBottom)
			k1 -= (y + k1) - DrawingArea.viewportBottom;
		if(x < DrawingArea.viewportLeft) {
			int l2 = DrawingArea.viewportLeft - x;
			l1 -= l2;
			x = DrawingArea.viewportLeft;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if(x + l1 > DrawingArea.viewportRight) {
			int i3 = (x + l1) - DrawingArea.viewportRight;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if(l1 <= 0 || k1 <= 0) {
			return;
		} else {
			method491(j1, DrawingArea.pixels, imgPixels, j2, anIntArray1517, k1, l1, i1, false, i2);
			return;
		}
	}

	public void method491(int i, int ai[], byte abyte0[], int j, int ai1[], int k, int l, int i1, boolean flag, int j1) {
		int k1 = -(l >> 2);
		l = -(l & 3);
		if(flag)
			anInt1511 = 264;
		for(int l1 = -k; l1 < 0; l1++) {
			for(int i2 = k1; i2 < 0; i2++) {
				byte byte0 = abyte0[i++];
				if(byte0 != 0)
					ai[i1++] = ai1[byte0 & 0xff];
				else
					i1++;
				byte0 = abyte0[i++];
				if(byte0 != 0)
					ai[i1++] = ai1[byte0 & 0xff];
				else
					i1++;
				byte0 = abyte0[i++];
				if(byte0 != 0)
					ai[i1++] = ai1[byte0 & 0xff];
				else
					i1++;
				byte0 = abyte0[i++];
				if(byte0 != 0)
					ai[i1++] = ai1[byte0 & 0xff];
				else
					i1++;
			}

			for(int j2 = l; j2 < 0; j2++) {
				byte byte1 = abyte0[i++];
				if(byte1 != 0)
					ai[i1++] = ai1[byte1 & 0xff];
				else
					i1++;
			}

			i1 += j1;
			i += j;
		}

	}

	public int anInt1509;
	public boolean aBoolean1510;
	public int anInt1511;
	public int anInt1512;
	public byte aByte1513;
	public int anInt1514;
	public boolean aBoolean1515;
	public byte imgPixels[];
	public int anIntArray1517[];
	public int imgWidth;
	public int imgHeight;
	public int xDrawOffset;
	public int yDrawOffset;
	public int anInt1522;
	public int anInt1523;
}
