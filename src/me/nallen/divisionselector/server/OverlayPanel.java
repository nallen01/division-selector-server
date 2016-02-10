package me.nallen.divisionselector.server;

import javax.swing.JPanel;

public abstract class OverlayPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public abstract void updateData();
}
