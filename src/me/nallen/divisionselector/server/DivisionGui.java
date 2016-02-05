package me.nallen.divisionselector.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class DivisionGui extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final Color chromaColor = new Color(0, 204, 0);
	private static final Color redColor = new Color(255, 0, 0);
	private static final Color blueColor = new Color(0, 0, 255);
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	
	private GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	public DivisionGui() {
		super("Division Selector");
		
		getContentPane().setBackground(chromaColor);
		getContentPane().setPreferredSize(new Dimension(1280, 720));
		
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(null);
	    
		addKeyListener(this);
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}

			public void componentMoved(ComponentEvent arg0) {}

			public void componentResized(ComponentEvent arg0) {
				updatePositions();
			}

			public void componentShown(ComponentEvent arg0) {}
		});
		
		pack();
	    updatePositions();
	}
	
	private void updatePositions() {
		
	}
	
	public void toggleFullScreen() {
		if(isFullScreen) {
			dispose();
			
			setExtendedState(JFrame.NORMAL);
			setUndecorated(false);
			setSize(priorDimension);
			setLocation(priorLocation);
			
			isFullScreen = false;
			setVisible(true);
		}
		else {
			if (gd.isFullScreenSupported()) {
				priorDimension = getSize();
				priorLocation = getLocation();
				
				dispose();
				
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				setUndecorated(true);
	
		        gd.setFullScreenWindow(this);
				
				isFullScreen = true;
				setVisible(true);
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
	        toggleFullScreen();
		}
		else if(isFullScreen && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	        toggleFullScreen();
		}
	}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {}
}
