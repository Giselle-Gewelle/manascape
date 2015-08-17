package com.runescape;

public class DrawingArea extends NodeSub {

	public static void setTarget(int i, int j, int ai[]) {
		pixels = ai;
		width = i;
		anInt1426 = j;
		method446(0, 0, j, i, true);
	}

	public static void method445(byte byte0) {
		viewportLeft = 0;
		viewportTop = 0;
		viewportRight = width;
		viewportBottom = anInt1426;
		anInt1431 = viewportRight - 1;
		if(byte0 != 82)
			anInt1423 = -258;
		anInt1432 = viewportRight / 2;
	}

	public static void method446(int i, int j, int k, int l, boolean flag) {
		if(j < 0)
			j = 0;
		if(i < 0)
			i = 0;
		if(l > width)
			l = width;
		if(k > anInt1426)
			k = anInt1426;
		viewportLeft = j;
		viewportTop = i;
		viewportRight = l;
		viewportBottom = k;
		if(!flag) {
			return;
		} else {
			anInt1431 = viewportRight - 1;
			anInt1432 = viewportRight / 2;
			anInt1433 = viewportBottom / 2;
			return;
		}
	}

	public static void clear() {
		int j = width * anInt1426;
		for(int k = 0; k < j; k++)
			pixels[k] = 0;

	}

	public static void method448(boolean flag, int i, int j, int k, int l, int i1, int j1) {
		if(j1 < viewportLeft) {
			k -= viewportLeft - j1;
			j1 = viewportLeft;
		}
		if(j < viewportTop) {
			l -= viewportTop - j;
			j = viewportTop;
		}
		if(j1 + k > viewportRight)
			k = viewportRight - j1;
		if(j + l > viewportBottom)
			l = viewportBottom - j;
		int k1 = 256 - i1;
		int l1 = (i >> 16 & 0xff) * i1;
		int i2 = (i >> 8 & 0xff) * i1;
		int j2 = (i & 0xff) * i1;
		if(flag)
			aBoolean1421 = !aBoolean1421;
		int j3 = width - k;
		int k3 = j1 + j * width;
		for(int l3 = 0; l3 < l; l3++) {
			for(int i4 = -k; i4 < 0; i4++) {
				int k2 = (pixels[k3] >> 16 & 0xff) * k1;
				int l2 = (pixels[k3] >> 8 & 0xff) * k1;
				int i3 = (pixels[k3] & 0xff) * k1;
				int j4 = ((l1 + k2 >> 8) << 16) + ((i2 + l2 >> 8) << 8) + (j2 + i3 >> 8);
				pixels[k3++] = j4;
			}

			k3 += j3;
		}

	}

	public static void fillRect(int i, int j, int k, byte byte0, int l, int i1) {
		if(i1 < viewportLeft) {
			l -= viewportLeft - i1;
			i1 = viewportLeft;
		}
		if(j < viewportTop) {
			i -= viewportTop - j;
			j = viewportTop;
		}
		if(i1 + l > viewportRight)
			l = viewportRight - i1;
		if(j + i > viewportBottom)
			i = viewportBottom - j;
		int j1 = width - l;
		int k1 = i1 + j * width;
		for(int l1 = -i; l1 < 0; l1++) {
			for(int i2 = -l; i2 < 0; i2++)
				pixels[k1++] = k;

			k1 += j1;
		}

		if(byte0 == -24)
			;
	}

	public static void method450(int i, int j, int k, int l, int i1, int j1) {
		method452(i1, l, j, j1, true);
		method452(i1, l, (j + k) - 1, j1, true);
		if(i != 0)
			anInt1420 = -278;
		method454(i1, l, k, false, j);
		method454((i1 + j1) - 1, l, k, false, j);
	}

	public static void method451(int i, int j, int k, int l, int i1, int j1, byte byte0) {
		if(byte0 != -113)
			return;
		method453(i1, i, j, 1388, j1, k);
		method453((i1 + l) - 1, i, j, 1388, j1, k);
		if(l >= 3) {
			method455(0, i1 + 1, i, k, l - 2, j1);
			method455(0, i1 + 1, (i + j) - 1, k, l - 2, j1);
		}
	}

	public static void method452(int i, int j, int k, int l, boolean flag) {
		if(k < viewportTop || k >= viewportBottom)
			return;
		if(i < viewportLeft) {
			l -= viewportLeft - i;
			i = viewportLeft;
		}
		if(i + l > viewportRight)
			l = viewportRight - i;
		int i1 = i + k * width;
		if(!flag) {
			for(int j1 = 1; j1 > 0; j1++)
				;
		}
		for(int k1 = 0; k1 < l; k1++)
			pixels[i1 + k1] = j;

	}

	public static void method453(int i, int j, int k, int l, int i1, int j1) {
		if(i < viewportTop || i >= viewportBottom)
			return;
		if(j < viewportLeft) {
			k -= viewportLeft - j;
			j = viewportLeft;
		}
		if(j + k > viewportRight)
			k = viewportRight - j;
		int k1 = 256 - i1;
		int l1 = (j1 >> 16 & 0xff) * i1;
		int i2 = (j1 >> 8 & 0xff) * i1;
		int j2 = (j1 & 0xff) * i1;
		int j3 = j + i * width;
		for(int k3 = 0; k3 < k; k3++) {
			int k2 = (pixels[j3] >> 16 & 0xff) * k1;
			int l2 = (pixels[j3] >> 8 & 0xff) * k1;
			int i3 = (pixels[j3] & 0xff) * k1;
			int l3 = ((l1 + k2 >> 8) << 16) + ((i2 + l2 >> 8) << 8) + (j2 + i3 >> 8);
			pixels[j3++] = l3;
		}

		if(l != 1388)
			anInt1420 = -36;
	}

	public static void method454(int i, int j, int k, boolean flag, int l) {
		if(flag)
			return;
		if(i < viewportLeft || i >= viewportRight)
			return;
		if(l < viewportTop) {
			k -= viewportTop - l;
			l = viewportTop;
		}
		if(l + k > viewportBottom)
			k = viewportBottom - l;
		int i1 = i + l * width;
		for(int j1 = 0; j1 < k; j1++)
			pixels[i1 + j1 * width] = j;

	}

	public static void method455(int i, int j, int k, int l, int i1, int j1) {
		if(k < viewportLeft || k >= viewportRight)
			return;
		if(j < viewportTop) {
			i1 -= viewportTop - j;
			j = viewportTop;
		}
		if(j + i1 > viewportBottom)
			i1 = viewportBottom - j;
		int k1 = 256 - j1;
		int l1 = (l >> 16 & 0xff) * j1;
		int i2 = (l >> 8 & 0xff) * j1;
		int j2 = (l & 0xff) * j1;
		if(i != 0) {
			for(int j3 = 1; j3 > 0; j3++)
				;
		}
		int k3 = k + j * width;
		for(int l3 = 0; l3 < i1; l3++) {
			int k2 = (pixels[k3] >> 16 & 0xff) * k1;
			int l2 = (pixels[k3] >> 8 & 0xff) * k1;
			int i3 = (pixels[k3] & 0xff) * k1;
			int i4 = ((l1 + k2 >> 8) << 16) + ((i2 + l2 >> 8) << 8) + (j2 + i3 >> 8);
			pixels[k3] = i4;
			k3 += width;
		}

	}

	public DrawingArea() {
	}

	public static int anInt1420;
	public static boolean aBoolean1421;
	public static boolean aBoolean1422 = true;
	public static int anInt1423 = -291;
	public static int pixels[];
	public static int width;
	public static int anInt1426;
	public static int viewportTop;
	public static int viewportBottom;
	public static int viewportLeft;
	public static int viewportRight;
	public static int anInt1431;
	public static int anInt1432;
	public static int anInt1433;
	public static boolean aBoolean1434;

}
