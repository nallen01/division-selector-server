package me.nallen.divisionselector.server;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SelectorGui extends JFrame implements DataListener {
	private static final long serialVersionUID = 1L;
	
	private static final int PADDING = 5;
	private static final double IMPORT_PANEL_HEIGHT = 0.2;
	private static final double EXPORT_PANEL_HEIGHT = 0.2;
	
	private JPanel importPanel;
	
	private JPanel exportPanel;
	
	private JPanel actionsPanel;

	public SelectorGui() {
		super("Division Selector");
		
		getContentPane().setPreferredSize(new Dimension(400, 500));
		setResizable(false);
		
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(null);
	    
	    importPanel = new JPanel();
	    importPanel.setBorder(BorderFactory.createTitledBorder("Import"));
	    add(importPanel);
	    
	    exportPanel = new JPanel();
	    exportPanel.setBorder(BorderFactory.createTitledBorder("Export"));
	    add(exportPanel);
	    
	    actionsPanel = new JPanel();
	    actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
	    add(actionsPanel);
	    
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
	    
	    DivisionSelectorServer.divisionData.addListener(this);
	}
	
	private void updatePositions() {
		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();
		
		int current_y = PADDING;
		
		importPanel.setBounds(PADDING, current_y, width - 2*PADDING, (int) (IMPORT_PANEL_HEIGHT*height));
		current_y += (int) (IMPORT_PANEL_HEIGHT*height) + PADDING;
		
		exportPanel.setBounds(PADDING, current_y, width - 2*PADDING, (int) (EXPORT_PANEL_HEIGHT*height));
		current_y += (int) (EXPORT_PANEL_HEIGHT*height) + PADDING;
		
		actionsPanel.setBounds(PADDING, current_y, width - 2*PADDING, height - PADDING - current_y);
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
}
