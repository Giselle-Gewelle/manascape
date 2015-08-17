package com.runescape;

import java.awt.*;
import java.awt.image.PixelGrabber;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;

public class RgbImage extends DrawingArea {

	public int pixels[];
	public int imgWidth;
	public int imgHeight;
	public int offsetX;
	public int offsetY;
	public int libWidth;
	public int libHeight;

	public RgbImage(int width, int height) {
		pixels = new int[width * height];
		imgWidth = libWidth = width;
		imgHeight = libHeight = height;
		offsetX = offsetY = 0;
	}

	public RgbImage(byte imgData[], Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(imgData);
			MediaTracker mediatracker = new MediaTracker(component);
			mediatracker.addImage(image, 0);
			mediatracker.waitForAll();
			imgWidth = image.getWidth(component);
			imgHeight = image.getHeight(component);
			libWidth = imgWidth;
			libHeight = imgHeight;
			offsetX = 0;
			offsetY = 0;
			pixels = new int[imgWidth * imgHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, imgWidth, imgHeight, pixels, 0, imgWidth);
			pixelgrabber.grabPixels();
			return;
		} catch(Exception _ex) {
			System.out.println("Error converting jpg");
		}
	}

	public RgbImage(CacheArchive archive, String imgGroupName, int imgId) {
		ByteBuffer byteStream = new ByteBuffer(archive.getDataForName(imgGroupName + ".dat"));
		ByteBuffer byteStream_1 = new ByteBuffer(archive.getDataForName("index.dat"));
		byteStream_1.position = byteStream.getUnsignedShort();
		libWidth = byteStream_1.getUnsignedShort();
		libHeight = byteStream_1.getUnsignedShort();
		int j = byteStream_1.getUnsignedByte();
		int ai[] = new int[j];
		for(int k = 0; k < j - 1; k++) {
			ai[k + 1] = byteStream_1.getTriByte();
			if(ai[k + 1] == 0)
				ai[k + 1] = 1;
		}

		for(int l = 0; l < imgId; l++) {
			byteStream_1.position += 2;
			byteStream.position += byteStream_1.getUnsignedShort() * byteStream_1.getUnsignedShort();
			byteStream_1.position++;
		}

		offsetX = byteStream_1.getUnsignedByte();
		offsetY = byteStream_1.getUnsignedByte();
		imgWidth = byteStream_1.getUnsignedShort();
		imgHeight = byteStream_1.getUnsignedShort();
		int i1 = byteStream_1.getUnsignedByte();
		int j1 = imgWidth * imgHeight;
		pixels = new int[j1];
		if(i1 == 0) {
			for(int k1 = 0; k1 < j1; k1++)
				pixels[k1] = ai[byteStream.getUnsignedByte()];

			return;
		}
		if(i1 == 1) {
			for(int l1 = 0; l1 < imgWidth; l1++) {
				for(int i2 = 0; i2 < imgHeight; i2++)
					pixels[l1 + i2 * imgWidth] = ai[byteStream.getUnsignedByte()];

			}

		}
	}

	public void initDrawingArea() {
		DrawingArea.setTarget(imgWidth, imgHeight, pixels);
	}

	public void addRGB(int r, int g, int b) {
		for(int i = 0; i < pixels.length; i++) {
			int inputRGB = pixels[i];
			if(inputRGB != 0) {
				int endR = inputRGB >> 16 & 0xff;
				endR += r;
				if(endR < 1)
					endR = 1;
				else if(endR > 255)
					endR = 255;
				
				int endG = inputRGB >> 8 & 0xff;
				endG += g;
				if(endG < 1)
					endG = 1;
				else if(endG > 255)
					endG = 255;
				
				int endB = inputRGB & 0xff;
				endB += b;
				if(endB < 1)
					endB = 1;
				else if(endB > 255)
					endB = 255;
				
				pixels[i] = (endR << 16) + (endG << 8) + endB;
			}
		}
	}

	public void method458() {
		int ai[] = new int[libWidth * libHeight];
		for(int j = 0; j < imgHeight; j++) {
			for(int k = 0; k < imgWidth; k++) {
				ai[(j + offsetY) * libWidth + (k + offsetX)] = pixels[j * imgWidth + k];
			}
		}

		pixels = ai;
		imgWidth = libWidth;
		imgHeight = libHeight;
		offsetX = 0;
		offsetY = 0;
	}

	public void draw(int x, int y) {
		x += offsetX;
		y += offsetY;
		int lineOffsetDest = x + y * DrawingArea.width;
		int sourceOffset = 0;
		int lineCount = imgHeight;
		int lineWidth = imgWidth;
		int destOffset = DrawingArea.width - lineWidth;
		int lineOffsetSrc = 0;
		
		if(y < DrawingArea.viewportTop) {
			int clipHeight = DrawingArea.viewportTop - y;
			lineCount -= clipHeight;
			y = DrawingArea.viewportTop;
			sourceOffset += clipHeight * lineWidth;
			lineOffsetDest += clipHeight * DrawingArea.width;
		}
		if(y + lineCount > DrawingArea.viewportBottom) {
			lineCount -= (y + lineCount) - DrawingArea.viewportBottom;
		}
		
		if(x < DrawingArea.viewportLeft) {
			int clipWidth = DrawingArea.viewportLeft - x;
			lineWidth -= clipWidth;
			x = DrawingArea.viewportLeft;
			sourceOffset += clipWidth;
			lineOffsetDest += clipWidth;
			lineOffsetSrc += clipWidth;
			destOffset += clipWidth;
		}
		if(x + lineWidth > DrawingArea.viewportRight) {
			int clipWidth = (x + lineWidth) - DrawingArea.viewportRight;
			lineWidth -= clipWidth;
			lineOffsetSrc += clipWidth;
			destOffset += clipWidth;
		}
		
		if(lineWidth <= 0 || lineCount <= 0) {
			return;
		}
		
		blockCopy(pixels, DrawingArea.pixels, lineWidth, destOffset, lineCount, sourceOffset, lineOffsetSrc, lineOffsetDest);
	}

	public void blockCopy(int source[], int dest[], int copyLength, int destBlockLen, int lineCount, int sourceIndex, int sourceBlockLen, int destIndex) {
		int numBlocks = -(copyLength >> 2);
		copyLength = -(copyLength & 3);
		for(int l1 = -lineCount; l1 < 0; l1++) {
			for(int i2 = numBlocks; i2 < 0; i2++) {
				dest[destIndex++] = source[sourceIndex++];
				dest[destIndex++] = source[sourceIndex++];
				dest[destIndex++] = source[sourceIndex++];
				dest[destIndex++] = source[sourceIndex++];
			}

			for(int j2 = copyLength; j2 < 0; j2++) {
				dest[destIndex++] = source[sourceIndex++];
			}
			destIndex += destBlockLen;
			sourceIndex += sourceBlockLen;
		}

	}

	public void method461(int i, int j, int k) {
		j += offsetX;
		if(k >= 0)
			return;
		i += offsetY;
		int l = j + i * DrawingArea.width;
		int i1 = 0;
		int j1 = imgHeight;
		int k1 = imgWidth;
		int l1 = DrawingArea.width - k1;
		int i2 = 0;
		if(i < DrawingArea.viewportTop) {
			int j2 = DrawingArea.viewportTop - i;
			j1 -= j2;
			i = DrawingArea.viewportTop;
			i1 += j2 * k1;
			l += j2 * DrawingArea.width;
		}
		if(i + j1 > DrawingArea.viewportBottom)
			j1 -= (i + j1) - DrawingArea.viewportBottom;
		if(j < DrawingArea.viewportLeft) {
			int k2 = DrawingArea.viewportLeft - j;
			k1 -= k2;
			j = DrawingArea.viewportLeft;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if(j + k1 > DrawingArea.viewportRight) {
			int l2 = (j + k1) - DrawingArea.viewportRight;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if(k1 <= 0 || j1 <= 0) {
			return;
		} else {
			method462(DrawingArea.pixels, pixels, 0, i1, l, k1, j1, l1, i2);
			return;
		}
	}

	public void method462(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = -(l >> 2);
		l = -(l & 3);
		for(int i2 = -i1; i2 < 0; i2++) {
			for(int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if(i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if(i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if(i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if(i != 0)
					ai[k++] = i;
				else
					k++;
			}

			for(int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if(i != 0)
					ai[k++] = i;
				else
					k++;
			}

			k += j1;
			j += k1;
		}

	}

	public void method463(int i, int j, int k, int l) {
		j += offsetX;
		k += offsetY;
		int i1 = j + k * DrawingArea.width;
		int j1 = 0;
		if(i != 0)
			return;
		int k1 = imgHeight;
		int l1 = imgWidth;
		int i2 = DrawingArea.width - l1;
		int j2 = 0;
		if(k < DrawingArea.viewportTop) {
			int k2 = DrawingArea.viewportTop - k;
			k1 -= k2;
			k = DrawingArea.viewportTop;
			j1 += k2 * l1;
			i1 += k2 * DrawingArea.width;
		}
		if(k + k1 > DrawingArea.viewportBottom)
			k1 -= (k + k1) - DrawingArea.viewportBottom;
		if(j < DrawingArea.viewportLeft) {
			int l2 = DrawingArea.viewportLeft - j;
			l1 -= l2;
			j = DrawingArea.viewportLeft;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if(j + l1 > DrawingArea.viewportRight) {
			int i3 = (j + l1) - DrawingArea.viewportRight;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if(l1 <= 0 || k1 <= 0) {
			return;
		} else {
			method464(l1, j2, 0, i2, j1, l, i1, k1, DrawingArea.pixels, pixels);
			return;
		}
	}

	public void method464(int i, int j, int k, int l, int i1, int k1, int l1, int i2, int ai[], int ai1[]) {
		int j2 = 256 - k1;
		for(int k2 = -i2; k2 < 0; k2++) {
			for(int l2 = -i; l2 < 0; l2++) {
				k = ai1[i1++];
				if(k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00) + ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}

			l1 += l;
			i1 += j;
		}
	}

	public void rotate(int centerX, int centerY, int width, int height, int angle, int widthMap[], int hingeSize, int ai1[], int drawX, int drawY) {
		try {
			int j2 = -width / 2;
			int k2 = -height / 2;
			int l2 = (int) (Math.sin((double) angle / 326.11000000000001D) * 65536D);
			int i3 = (int) (Math.cos((double) angle / 326.11000000000001D) * 65536D);
			l2 = l2 * hingeSize >> 8;
			i3 = i3 * hingeSize >> 8;
			int j3 = (centerX << 16) + (k2 * l2 + j2 * i3);
			int k3 = (centerY << 16) + (k2 * i3 - j2 * l2);
			int l3 = drawX + drawY * DrawingArea.width;
			for(drawY = 0; drawY < height; drawY++) {
				int i4 = ai1[drawY];
				int j4 = l3 + i4;
				int k4 = j3 + i3 * i4;
				int l4 = k3 - l2 * i4;
				for(drawX = -widthMap[drawY]; drawX < 0; drawX++) {
					DrawingArea.pixels[j4++] = pixels[(k4 >> 16) + (l4 >> 16) * imgWidth];
					k4 += i3;
					l4 -= l2;
				}

				j3 += l2;
				k3 += i3;
				l3 += DrawingArea.width;
			}

			return;
		} catch(Exception _ex) {
			return;
		}
	}

	public void method466(int i, int j, int k, int l, int i1, int j1, int k1, double d, int l1) {
		if(j1 != -30658)
			return;
		try {
			int i2 = -k1 / 2;
			int j2 = -i1 / 2;
			int k2 = (int) (Math.sin(d) * 65536D);
			int l2 = (int) (Math.cos(d) * 65536D);
			k2 = k2 * i >> 8;
			l2 = l2 * i >> 8;
			int i3 = (j << 16) + (j2 * k2 + i2 * l2);
			int j3 = (l << 16) + (j2 * l2 - i2 * k2);
			int k3 = k + l1 * DrawingArea.width;
			for(l1 = 0; l1 < i1; l1++) {
				int l3 = k3;
				int i4 = i3;
				int j4 = j3;
				for(k = -k1; k < 0; k++) {
					int k4 = pixels[(i4 >> 16) + (j4 >> 16) * imgWidth];
					if(k4 != 0)
						DrawingArea.pixels[l3++] = k4;
					else
						l3++;
					i4 += l2;
					j4 -= k2;
				}

				i3 += k2;
				j3 += l2;
				k3 += DrawingArea.width;
			}

			return;
		} catch(Exception _ex) {
			return;
		}
	}

	public void method467(IndexedImage class50_sub1_sub1_sub3, int i, int j, int k) {
		if(j != -49993)
			return;
		k += offsetX;
		i += offsetY;
		int l = k + i * DrawingArea.width;
		int i1 = 0;
		int j1 = imgHeight;
		int k1 = imgWidth;
		int l1 = DrawingArea.width - k1;
		int i2 = 0;
		if(i < DrawingArea.viewportTop) {
			int j2 = DrawingArea.viewportTop - i;
			j1 -= j2;
			i = DrawingArea.viewportTop;
			i1 += j2 * k1;
			l += j2 * DrawingArea.width;
		}
		if(i + j1 > DrawingArea.viewportBottom)
			j1 -= (i + j1) - DrawingArea.viewportBottom;
		if(k < DrawingArea.viewportLeft) {
			int k2 = DrawingArea.viewportLeft - k;
			k1 -= k2;
			k = DrawingArea.viewportLeft;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if(k + k1 > DrawingArea.viewportRight) {
			int l2 = (k + k1) - DrawingArea.viewportRight;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if(k1 <= 0 || j1 <= 0) {
			return;
		} else {
			method468(l, l1, pixels, k1, DrawingArea.pixels, class50_sub1_sub1_sub3.imgPixels, 40303, j1, i1, 0, i2);
			return;
		}
	}

	public void method468(int i, int j, int ai[], int k, int ai1[], byte abyte0[], int l, int i1, int j1, int k1, int l1) {
		int i2 = -(k >> 2);
		k = -(k & 3);
		for(int j2 = -i1; j2 < 0; j2++) {
			for(int k2 = i2; k2 < 0; k2++) {
				k1 = ai[j1++];
				if(k1 != 0 && abyte0[i] == 0)
					ai1[i++] = k1;
				else
					i++;
				k1 = ai[j1++];
				if(k1 != 0 && abyte0[i] == 0)
					ai1[i++] = k1;
				else
					i++;
				k1 = ai[j1++];
				if(k1 != 0 && abyte0[i] == 0)
					ai1[i++] = k1;
				else
					i++;
				k1 = ai[j1++];
				if(k1 != 0 && abyte0[i] == 0)
					ai1[i++] = k1;
				else
					i++;
			}

			for(int l2 = k; l2 < 0; l2++) {
				k1 = ai[j1++];
				if(k1 != 0 && abyte0[i] == 0)
					ai1[i++] = k1;
				else
					i++;
			}

			i += j;
			j1 += l1;
		}

	}
}
