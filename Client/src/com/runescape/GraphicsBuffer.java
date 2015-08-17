package com.runescape;
import java.awt.*;
import java.awt.image.*;

public class GraphicsBuffer implements ImageProducer, ImageObserver {

	public GraphicsBuffer(int clientWidth, int clientHeight, Component component) {
		aBoolean391 = true;
		this.clientWidth = clientWidth;
		this.clientHeight = clientHeight;
		anIntArray392 = new int[clientWidth * clientHeight];
		aColorModel395 = new DirectColorModel(32, 0xff0000, 65280, 255);
		anImage397 = component.createImage(this);
		method232();
		component.prepareImage(anImage397, this);
		method232();
		component.prepareImage(anImage397, this);
		method232();
		component.prepareImage(anImage397, this);
		initDrawingArea();
	}

	public void initDrawingArea() {
		DrawingArea.setTarget(clientWidth, clientHeight, anIntArray392);
	}

	public void drawGraphics(int x, int y, Graphics g) {
		method232();
		g.drawImage(anImage397, x, y, this);
	}

	public synchronized void addConsumer(ImageConsumer imageconsumer) {
		anImageConsumer396 = imageconsumer;
		imageconsumer.setDimensions(clientWidth, clientHeight);
		imageconsumer.setProperties(null);
		imageconsumer.setColorModel(aColorModel395);
		imageconsumer.setHints(14);
	}

	public synchronized boolean isConsumer(ImageConsumer imageconsumer) {
		return anImageConsumer396 == imageconsumer;
	}

	public synchronized void removeConsumer(ImageConsumer imageconsumer) {
		if(anImageConsumer396 == imageconsumer)
			anImageConsumer396 = null;
	}

	public void startProduction(ImageConsumer imageconsumer) {
		addConsumer(imageconsumer);
	}

	public void requestTopDownLeftRightResend(ImageConsumer imageconsumer) {
		System.out.println("TDLR");
	}

	public synchronized void method232() {
		if(anImageConsumer396 == null) {
			return;
		} else {
			anImageConsumer396.setPixels(0, 0, clientWidth, clientHeight, aColorModel395, anIntArray392, 0, clientWidth);
			anImageConsumer396.imageComplete(2);
			return;
		}
	}

	public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
		return true;
	}

	public boolean aBoolean391;
	public int anIntArray392[];
	public int clientWidth;
	public int clientHeight;
	public ColorModel aColorModel395;
	public ImageConsumer anImageConsumer396;
	public Image anImage397;
}
