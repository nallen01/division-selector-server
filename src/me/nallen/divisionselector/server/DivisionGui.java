package me.nallen.divisionselector.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;

import javax.swing.JFrame;

public class DivisionGui extends JFrame implements KeyListener, DataListener {
	private static final long serialVersionUID = 1L;
	public static final Color chromaColor = new Color(255, 0, 255);
	public static final Color redColor = new Color(238, 49, 36);
	public static final Color blueColor = new Color(0, 130, 200);
	public static final Color whiteColor = new Color(255, 255, 255);
	public static final Color grayColor = new Color(63, 63, 63);
	public static final Color blackColor = new Color(0, 0, 0);
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	
	private OverlayPanel contentPanel;
	private int currentDivisions = -1;
	
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
		
		
		if (System.getProperty("os.name").equals("Mac OS X")) {
			try {
				Class<?> c = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method m = c.getMethod("setWindowCanFullScreen", Window.class, boolean.class);
				m.invoke(c, this, true);
			} catch (Exception e) { e.printStackTrace();}
		}
		
		pack();
	    updatePositions();
	    updateData();
	    
	    DivisionSelectorServer.divisionData.addListener(this);
	}
	
	private void updatePositions() {
		if(contentPanel != null) {
			int width = getContentPane().getWidth();
			int height = getContentPane().getHeight();
	
			contentPanel.setBounds(0, 0, width, height);
		}
	}
	
	private void updateData() {
		if(currentDivisions != DivisionSelectorServer.divisionData.getAllDivisions().length) {
			if(contentPanel != null) {
				remove(contentPanel);
				revalidate();
				repaint();
			}
			
			switch(DivisionSelectorServer.divisionData.getAllDivisions().length) {
				case 2: contentPanel = new TwoDivisionGui(); break;
				default: contentPanel = null; break;
			}
			
			if(contentPanel != null) {
				add(contentPanel);
				updatePositions();
			}
			
			currentDivisions = DivisionSelectorServer.divisionData.getAllDivisions().length;
		}
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
			priorDimension = getSize();
			priorLocation = getLocation();
			
			dispose();
			
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
			
			isFullScreen = true;
			setVisible(true);
		}
	}

	public void update() {
		updateData();
		
		if(contentPanel != null) 
			contentPanel.updateData();
	}

	public void keyPressed(KeyEvent e) {
		if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
	        toggleFullScreen();
		}
		else if(isFullScreen && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	        toggleFullScreen();
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
}
