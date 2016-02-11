package me.nallen.divisionselector.server;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class ScrollingTeamListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final double ENTRY_FONT_SIZE = 0.08;
	private static final int MAX_ENTRY_FONT_SIZE = 25;
	private static final double ROW_SPACING = 0.04;
	
	private static final double SCROLL_INCREMENT = 0.002;
	private static final double SCROLL_FREQUENCY = 30;
	private static final int SCROLL_MILLISECONDS = (int) (1000 / SCROLL_FREQUENCY);
	
	private int numColumns;
	
	private String[] teams;
	
	private JPanel rootPanel;
	private JPanel panelA;
	private JPanel panelB;
	
	private boolean scroll = false;
	private int scrollPos = 0;
	
	private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

	public ScrollingTeamListPanel(int numColumns) {
		this.numColumns = numColumns;
		
		setOpaque(false);

		rootPanel = new JPanel(new MigLayout("fillx"));
		((MigLayout)rootPanel.getLayout()).setRowConstraints("[]0[]");
		rootPanel.setOpaque(false);
		
		panelA = new JPanel();
		panelA.setLayout(new MigLayout("fillx"));
		panelA.setOpaque(false);
		
		panelB = new JPanel();
		panelB.setLayout(new MigLayout("fillx"));
		panelB.setOpaque(false);
		
		rootPanel.add(panelA, "w 100%, wrap");
		rootPanel.add(panelB, "w 100%, wrap");
		add(rootPanel);
		
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}

			public void componentMoved(ComponentEvent arg0) {}

			public void componentResized(ComponentEvent arg0) {
				updatePositions();
			}

			public void componentShown(ComponentEvent arg0) {}
		});
		
		updatePositions();
	}
	
	private void updatePanel(JPanel panel) {
		panel.removeAll();
		
		int i = 1;
		double percent = 100 / this.numColumns;
		for(String team : teams) {
			JLabel teamLabel = new JLabel(team);
		    teamLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			if(i % this.numColumns == 0) {
				panel.add(teamLabel, "w " + percent + "%, wrap");
			}
			else {
				panel.add(teamLabel, "w " + percent + "%");
			}
			
			i++;
		}
		
		updateFontSizeForPanel(panel);
		
		panel.revalidate();
	}
	
	private void updateFontSizeForPanel(JPanel panel) {
		int font_size = (int) (panel.getWidth() * ENTRY_FONT_SIZE);
		if(font_size > MAX_ENTRY_FONT_SIZE) {
			font_size = MAX_ENTRY_FONT_SIZE;
		}

		JLabel temp = new JLabel();
		Font font = new Font(temp.getFont().getFontName(), Font.BOLD, font_size);
		
		for(Component comp : panel.getComponents()) {
			comp.setFont(font);
		}
		
		panel.revalidate();
	}
	
	private void updatePositions() {
		int width = getWidth();
		int height = getHeight();
		
		int row_gap = (int) (height * ROW_SPACING);
		
		rootPanel.setSize(width, rootPanel.getHeight());
		
		panelA.setSize(width, panelA.getHeight());
		updateFontSizeForPanel(panelA);
		((MigLayout)panelA.getLayout()).setRowConstraints("[]" + row_gap + "[]");
		panelA.revalidate();

		panelB.setSize(width, panelB.getHeight());
		updateFontSizeForPanel(panelB);
		((MigLayout)panelB.getLayout()).setRowConstraints("[]" + row_gap + "[]");
		panelB.revalidate();
		
		if(panelA.getHeight() > height) {
			scroll = true;
		}
		else {
			scroll = false;
			scrollPos = 0;
		}
		
		updateScrolling();
	}
	
	private void updateScrolling() {
		if(scroll) {
			panelB.setVisible(true);
			
			scrollPos = (scrollPos + ((int) (getHeight() * SCROLL_INCREMENT))) % panelA.getHeight();
			
			rootPanel.setBounds(rootPanel.getBounds().x, -1 * scrollPos, rootPanel.getWidth(), rootPanel.getHeight());
			
			if(ses.isShutdown()) {
				ses = Executors.newSingleThreadScheduledExecutor();
				
				ses.scheduleAtFixedRate(new Runnable() {
					public void run() {
						updateScrolling();
					}
				}, 0, SCROLL_MILLISECONDS, TimeUnit.MILLISECONDS);
			}
		}
		else {
			if(!ses.isShutdown()) {
				ses.shutdown();				
			}
			
			rootPanel.setBounds(rootPanel.getBounds().x, -1 * scrollPos, rootPanel.getWidth(), rootPanel.getHeight());
			
			panelB.setVisible(false);
		}
	}
	
	public void updateTeamList(String[] teams) {
		this.teams = teams;
		
		updatePanel(panelA);			
		updatePanel(panelB);
		
		updatePositions();
	}
}
