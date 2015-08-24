package com.runescape;

import java.util.Random;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;

public class GameFont extends DrawingArea {

	public byte glyphPixels[][];
	public int glyphWidth[];
	public int glyphHeight[];
	public int horizontalKerning[];
	public int verticalKerning[];
	public int charEffectiveWidth[];
	public int charHeight;
	public Random random;
	public boolean strikeThrough;
	public int textColor;
	public int defaultColor;

	public GameFont(boolean flag, CacheArchive archive, int i, String s) {
		glyphPixels = new byte[256][];
		glyphWidth = new int[256];
		glyphHeight = new int[256];
		horizontalKerning = new int[256];
		verticalKerning = new int[256];
		charEffectiveWidth = new int[256];
		random = new Random();
		strikeThrough = false;
		textColor = 0;
		defaultColor = 0;
		ByteBuffer data = new ByteBuffer(archive.getDataForName(s + ".dat"));
		ByteBuffer index = new ByteBuffer(archive.getDataForName("index.dat"));
		index.position = data.getUnsignedShort() + 4;
		int k = index.getUnsignedByte();
		if(k > 0) {
			index.position += 3 * (k - 1);
		}
		
		for(int l = 0; l < 256; l++) {
			horizontalKerning[l] = index.getUnsignedByte();
			verticalKerning[l] = index.getUnsignedByte();
			int width = glyphWidth[l] = index.getUnsignedShort();
			int height = glyphHeight[l] = index.getUnsignedShort();
			int k1 = index.getUnsignedByte();
			int l1 = width * height;
			glyphPixels[l] = new byte[l1];
			if(k1 == 0) {
				for(int i2 = 0; i2 < l1; i2++)
					glyphPixels[l][i2] = data.getByte();

			} else if(k1 == 1) {
				for(int j2 = 0; j2 < width; j2++) {
					for(int l2 = 0; l2 < height; l2++)
						glyphPixels[l][j2 + l2 * width] = data.getByte();

				}

			}
			if(height > charHeight && l < 128)
				charHeight = height;
			horizontalKerning[l] = 1;
			charEffectiveWidth[l] = width + 2;
			int k2 = 0;
			for(int i3 = height / 7; i3 < height; i3++)
				k2 += glyphPixels[l][i3 * width];

			if(k2 <= height / 7) {
				charEffectiveWidth[l]--;
				horizontalKerning[l] = 0;
			}
			k2 = 0;
			for(int j3 = height / 7; j3 < height; j3++)
				k2 += glyphPixels[l][(width - 1) + j3 * width];

			if(k2 <= height / 7)
				charEffectiveWidth[l]--;
		}

		if(flag) {
			charEffectiveWidth[32] = charEffectiveWidth[73];
			return;
		} else {
			charEffectiveWidth[32] = charEffectiveWidth[105];
			return;
		}
	}

	public void method469(boolean flag, String s, int i, int j, int k) {
		method474(s, j - getStringWidth(s), k, i);
	}

	public void method470(int i, int j, int k, int l, String s) {
		method474(s, i - getStringWidth(s) / 2, k, l);
	}

	public void drawCenteredText(boolean shadowed, int colour, int x, int y, String s) {
		method478(s, x - getFormattedStringWidth(s) / 2, y, colour, shadowed);
	}

	public int getFormattedStringWidth(String string) {
		if(string == null) {
			return 0;
		}
		
		int startIndex = -1;
		int width = 0;
		for(int charIndex = 0; charIndex < string.length(); charIndex++) {
			char c = string.charAt(charIndex);
			
			if(c == 60) {
				startIndex = charIndex;
			} else {
				if(c == 62 && startIndex != -1) {
					String effect = string.substring(startIndex + 1, charIndex);
					startIndex = -1;
					
					if(effect.equals("<")) {
						c = 60;
					} else if(effect.equals(">")) {
						c = 62;
					}
					
					continue;
				}
				
				if(startIndex == -1) {
					if(c == '@' && charIndex + 4 < string.length() && string.charAt(charIndex + 4) == '@') {
						charIndex += 4;
					} else {
						width += charEffectiveWidth[string.charAt(charIndex)];
					}
				}
			}
		}

		return width;
	}

	public int getStringWidth(String s) {
		if(s == null)
			return 0;
		int i = 0;
		for(int k = 0; k < s.length(); k++)
			i += charEffectiveWidth[s.charAt(k)];

		return i;
	}

	public void method474(String string, int x, int y, int color) {
		if(string == null) {
			return;
		}
		
		textColor = defaultColor = color;
		y -= charHeight;
		int startIndex = -1;
		
		for(int charIndex = 0; charIndex < string.length(); charIndex++) {
			char c = string.charAt(charIndex);
			
			if(c != ' ') {
				//drawGlyph(glyphPixels[c], x + horizontalKerning[c], y + verticalKerning[c], glyphWidth[c], glyphHeight[c], color);
				
				if(c == 60) {
					startIndex = charIndex;
				} else {
					if(c == 62 && startIndex != -1) {
						String effect = string.substring(startIndex + 1, charIndex);
						startIndex = -1;
						
						if(effect.equals("<")) {
							c = 60;
						} else if(effect.equals(">")) {
							c = 62;
						} else {
							setTextEffects(effect);
						}
						
						continue;
					}
					
					if(startIndex == -1) {
						drawGlyph(glyphPixels[c], x + horizontalKerning[c], y + verticalKerning[c], glyphWidth[c], glyphHeight[c], textColor);
						x += charEffectiveWidth[c];
					}
				}
			} else {
				x += charEffectiveWidth[c];
			}
		}

	}

	public void method475(int i, byte byte0, int j, String s, int k, int l) {
		if(s == null)
			return;
		k -= getStringWidth(s) / 2;
		i -= charHeight;
		for(int i1 = 0; i1 < s.length(); i1++) {
			char c = s.charAt(i1);
			if(c != ' ')
				drawGlyph(glyphPixels[c], k + horizontalKerning[c], i + verticalKerning[c] + (int) (Math.sin((double) i1 / 2D + (double) j / 5D) * 5D), glyphWidth[c], glyphHeight[c], l);
			k += charEffectiveWidth[c];
		}

	}

	public void method476(int i, int j, byte byte0, String s, int k, int l) {
		if(s == null)
			return;
		k -= getStringWidth(s) / 2;
		if(byte0 != 1) {
			for(int i1 = 1; i1 > 0; i1++)
				;
		}
		i -= charHeight;
		for(int j1 = 0; j1 < s.length(); j1++) {
			char c = s.charAt(j1);
			if(c != ' ')
				drawGlyph(glyphPixels[c], k + horizontalKerning[c] + (int) (Math.sin((double) j1 / 5D + (double) l / 5D) * 5D),
						i + verticalKerning[c] + (int) (Math.sin((double) j1 / 3D + (double) l / 5D) * 5D), glyphWidth[c], glyphHeight[c], j);
			k += charEffectiveWidth[c];
		}

	}

	public void method477(int i, String s, int j, int k, int l, int i1, int j1) {
		if(s == null)
			return;
		double d = 7D - (double) i1 / 8D;
		while(i >= 0) {
			for(int k1 = 1; k1 > 0; k1++)
				;
		}
		if(d < 0.0D)
			d = 0.0D;
		k -= getStringWidth(s) / 2;
		l -= charHeight;
		for(int l1 = 0; l1 < s.length(); l1++) {
			char c = s.charAt(l1);
			if(c != ' ')
				drawGlyph(glyphPixels[c], k + horizontalKerning[c], l + verticalKerning[c] + (int) (Math.sin((double) l1 / 1.5D + (double) j1) * d), glyphWidth[c], glyphHeight[c], j);
			k += charEffectiveWidth[c];
		}

	}

	public void method478(String string, int x, int y, int color, boolean shadowed) {
		strikeThrough = false;
		int i1 = x;
		
		if(string == null) {
			return;
		}
		
		textColor = defaultColor = color;
		y -= charHeight;
		int startIndex = -1;
		
		for(int charIndex = 0; charIndex < string.length(); charIndex++) {
			if(string.charAt(charIndex) == '@' && charIndex + 4 < string.length() && string.charAt(charIndex + 4) == '@') {
				int k1 = getColorByName(string.substring(charIndex + 1, charIndex + 4));
				if(k1 != -1) {
					textColor = k1;
				}
				charIndex += 4;
			} else {
				char c = string.charAt(charIndex);
				if(c != ' ') {
					if(c == 60) {
						startIndex = charIndex;
					} else {
						if(c == 62 && startIndex != -1) {
							String effect = string.substring(startIndex + 1, charIndex);
							startIndex = -1;
							
							if(effect.equals("<")) {
								c = 60;
							} else if(effect.equals(">")) {
								c = 62;
							} else {
								setTextEffects(effect);
							}
							
							continue;
						}
						
						if(startIndex == -1) {
							if(shadowed) {
								drawGlyph(glyphPixels[c], x + horizontalKerning[c] + 1, y + verticalKerning[c] + 1, glyphWidth[c], glyphHeight[c], 0);
							}
							
							drawGlyph(glyphPixels[c], x + horizontalKerning[c], y + verticalKerning[c], glyphWidth[c], glyphHeight[c], textColor);
							x += charEffectiveWidth[c];
						}
					}
					
					/*if(shadowed) {
						drawGlyph(glyphPixels[c], x + horizontalKerning[c] + 1, y + verticalKerning[c] + 1, glyphWidth[c], glyphHeight[c], 0);
					}
					
					drawGlyph(glyphPixels[c], x + horizontalKerning[c], y + verticalKerning[c], glyphWidth[c], glyphHeight[c], color);*/
				} else {
					x += charEffectiveWidth[c];
				}
			}
		}
		
		if(strikeThrough) {
			DrawingArea.method452(i1, 0x800000, y + (int) ((double) charHeight * 0.69999999999999996D), x - i1, true);
		}
	}

	public void method479(boolean flag, int i, int j, int k, int l, String s, int i1) {
		if(s == null)
			return;
		random.setSeed(i);
		int j1 = 192 + (random.nextInt() & 0x1f);
		l -= charHeight;
		for(int k1 = 0; k1 < s.length(); k1++)
			if(s.charAt(k1) == '@' && k1 + 4 < s.length() && s.charAt(k1 + 4) == '@') {
				int l1 = getColorByName(s.substring(k1 + 1, k1 + 4));
				if(l1 != -1)
					k = l1;
				k1 += 4;
			} else {
				char c = s.charAt(k1);
				if(c != ' ') {
					if(flag)
						method483(j + horizontalKerning[c] + 1, true, 0, glyphPixels[c], l + verticalKerning[c] + 1, glyphHeight[c], glyphWidth[c], 192);
					method483(j + horizontalKerning[c], true, k, glyphPixels[c], l + verticalKerning[c], glyphHeight[c], glyphWidth[c], j1);
				}
				j += charEffectiveWidth[c];
				if((random.nextInt() & 3) == 0)
					j++;
			}

	}

	public int getColorByName(String s) {
		if(s.equals("red"))
			return 0xff0000;
		if(s.equals("gre"))
			return 65280;
		if(s.equals("blu"))
			return 255;
		if(s.equals("yel"))
			return 0xffff00;
		if(s.equals("cya"))
			return 65535;
		if(s.equals("mag"))
			return 0xff00ff;
		if(s.equals("whi"))
			return 0xffffff;
		if(s.equals("bla"))
			return 0;
		if(s.equals("lre"))
			return 0xff9040;
		if(s.equals("dre"))
			return 0x800000;
		if(s.equals("dbl"))
			return 128;
		if(s.equals("or1"))
			return 0xffb000;
		if(s.equals("or2"))
			return 0xff7000;
		if(s.equals("or3"))
			return 0xff3000;
		if(s.equals("gr1"))
			return 0xc0ff00;
		if(s.equals("gr2"))
			return 0x80ff00;
		if(s.equals("gr3"))
			return 0x40ff00;
		if(s.equals("str"))
			strikeThrough = true;
		if(s.equals("end"))
			strikeThrough = false;
		return -1;
	}
	
	public void setTextEffects(String string) {
		if(string.startsWith("col=")) {
			textColor = Integer.valueOf(string.substring(4), 16);
		} else if(string.equals("/col")) {
			textColor = defaultColor;
		}
	}

	public void drawGlyph(byte glyphPixels[], int i, int j, int k, int l, int colour) {
		int j1 = i + j * DrawingArea.width;
		int k1 = DrawingArea.width - k;
		int l1 = 0;
		int i2 = 0;
		if(j < DrawingArea.viewportTop) {
			int j2 = DrawingArea.viewportTop - j;
			l -= j2;
			j = DrawingArea.viewportTop;
			i2 += j2 * k;
			j1 += j2 * DrawingArea.width;
		}
		if(j + l >= DrawingArea.viewportBottom)
			l -= ((j + l) - DrawingArea.viewportBottom) + 1;
		if(i < DrawingArea.viewportLeft) {
			int k2 = DrawingArea.viewportLeft - i;
			k -= k2;
			i = DrawingArea.viewportLeft;
			i2 += k2;
			j1 += k2;
			l1 += k2;
			k1 += k2;
		}
		if(i + k >= DrawingArea.viewportRight) {
			int l2 = ((i + k) - DrawingArea.viewportRight) + 1;
			k -= l2;
			l1 += l2;
			k1 += l2;
		}
		if(k <= 0 || l <= 0) {
			return;
		} else {
			method482(DrawingArea.pixels, glyphPixels, colour, i2, j1, k, l, k1, l1);
			return;
		}
	}

	public void method482(int pixels[], byte glyphPixels[], int colour, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = -(l >> 2);
		l = -(l & 3);
		for(int i2 = -i1; i2 < 0; i2++) {
			for(int j2 = l1; j2 < 0; j2++) {
				if(glyphPixels[j++] != 0)
					pixels[k++] = colour;
				else
					k++;
				if(glyphPixels[j++] != 0)
					pixels[k++] = colour;
				else
					k++;
				if(glyphPixels[j++] != 0)
					pixels[k++] = colour;
				else
					k++;
				if(glyphPixels[j++] != 0)
					pixels[k++] = colour;
				else
					k++;
			}

			for(int k2 = l; k2 < 0; k2++)
				if(glyphPixels[j++] != 0)
					pixels[k++] = colour;
				else
					k++;

			k += j1;
			j += k1;
		}

	}

	public void method483(int i, boolean flag, int j, byte abyte0[], int k, int l, int i1, int j1) {
		int k1 = i + k * DrawingArea.width;
		int l1 = DrawingArea.width - i1;
		int i2 = 0;
		int j2 = 0;
		if(!flag)
			return;
		if(k < DrawingArea.viewportTop) {
			int k2 = DrawingArea.viewportTop - k;
			l -= k2;
			k = DrawingArea.viewportTop;
			j2 += k2 * i1;
			k1 += k2 * DrawingArea.width;
		}
		if(k + l >= DrawingArea.viewportBottom)
			l -= ((k + l) - DrawingArea.viewportBottom) + 1;
		if(i < DrawingArea.viewportLeft) {
			int l2 = DrawingArea.viewportLeft - i;
			i1 -= l2;
			i = DrawingArea.viewportLeft;
			j2 += l2;
			k1 += l2;
			i2 += l2;
			l1 += l2;
		}
		if(i + i1 >= DrawingArea.viewportRight) {
			int i3 = ((i + i1) - DrawingArea.viewportRight) + 1;
			i1 -= i3;
			i2 += i3;
			l1 += i3;
		}
		if(i1 <= 0 || l <= 0) {
			return;
		} else {
			method484(j2, l1, i2, k1, j1, DrawingArea.pixels, j, 2, l, i1, abyte0);
			return;
		}
	}

	public void method484(int i, int j, int k, int l, int i1, int ai[], int j1, int k1, int l1, int i2, byte abyte0[]) {
		j1 = ((j1 & 0xff00ff) * i1 & 0xff00ff00) + ((j1 & 0xff00) * i1 & 0xff0000) >> 8;
		i1 = 256 - i1;
		for(int j2 = -l1; j2 < 0; j2++) {
			for(int k2 = -i2; k2 < 0; k2++)
				if(abyte0[i++] != 0) {
					int l2 = ai[l];
					ai[l++] = (((l2 & 0xff00ff) * i1 & 0xff00ff00) + ((l2 & 0xff00) * i1 & 0xff0000) >> 8) + j1;
				} else {
					l++;
				}

			l += j;
			i += k;
		}

	}
	
}
