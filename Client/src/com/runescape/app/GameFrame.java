package com.runescape.app;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

@SuppressWarnings("serial")
public class GameFrame extends Frame implements ComponentListener {

	public GameApplet gameApplet;

	public int width;
	public int height;
	
	public GameFrame(int width, int height, GameApplet gameApplet) {
		this.gameApplet = gameApplet;
		this.width = width;
		this.height = height;
		setTitle("ManaScape");
		setResizable(false);
		toFront();
		setSize(width + 8, height + 28);
		addComponentListener(this);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		g.translate(4, 24);
		return g;
	}

	public void update(Graphics g) {
		gameApplet.update(g);
	}

	public void paint(Graphics g) {
		gameApplet.paint(g);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		width = this.getWidth() - 8;
		height = this.getHeight() - 28;
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}
}
