package com.runescape.gameserver.util;

public final class ClassLoaderUtils extends ClassLoader {
	
	private static final ClassLoaderUtils loader = new ClassLoaderUtils();
	
	public Class<?> load(String path) throws ClassNotFoundException {
		if (findLoadedClass(path) == null) {
			return getSystemClassLoader().loadClass(path);
		} else {
			resolveClass(loadClass(path));
			return getSystemClassLoader().loadClass(path);
		}
	}
	
	public static ClassLoaderUtils getLoader() {
		return loader;
	}
}