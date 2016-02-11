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

	public ScrollingTeamListPanel(int numColumns) {
		this.numColumns = numColumns;
		
		setLayout(null);
		setOpaque(false);

		rootPanel = new JPanel(new MigLayout("ins 0, fillx"));
		((MigLayout)rootPanel.getLayout()).setRowConstraints("[]0[]");
		((MigLayout)rootPanel.getLayout()).setColumnConstraints("[]0[]");
		rootPanel.setOpaque(false);
		
		panelA = new JPanel();
		panelA.setLayout(new MigLayout("ins 0, fillx"));
		panelA.setOpaque(false);
		
		panelB = new JPanel();
		panelB.setLayout(new MigLayout("ins 0, fillx"));
		panelB.setOpaque(false);

		rootPanel.add(panelA, "w 100%, wrap");
		rootPanel.add(panelB, "w 100%, wrap");
		add(rootPanel);
		
		ComponentListener resizeListener = new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}

			public void componentMoved(ComponentEvent arg0) {}

			public void componentResized(ComponentEvent arg0) {
				updatePositions();
			}

			public void componentShown(ComponentEvent arg0) {}
		};
		
		addComponentListener(resizeListener);
		panelA.addComponentListener(resizeListener);
		
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable() {
			public void run() {
				updateScrolling();
			}
		}, 0, SCROLL_MILLISECONDS, TimeUnit.MILLISECONDS);
		
		updatePositions();
	}
	
	private void updatePanel(JPanel panel) {
		panel.removeAll();
		
		int i = 1;
		double percent = 100 / this.numColumns;
		for(String team : teams) {
			JLabel teamLabel = new JLabel(team);
		    teamLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			if(i % this.numColumns == 0 || i == teams.length) {
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
		int height = getHeight();
		
		int row_gap = (int) (height * ROW_SPACING);
		
		((MigLayout)panelA.getLayout()).setRowConstraints("[]" + row_gap + "[]");
		updateFontSizeForPanel(panelA);

		((MigLayout)panelB.getLayout()).setRowConstraints("[]" + row_gap + "[]");
		updateFontSizeForPanel(panelB);
		
		if(panelA.getHeight() > height) {
			scroll = true;
		}
		else {
			scroll = false;
		}
	}
	
	private void updateScrolling() {
		if(scroll) {
			panelB.setVisible(true);
			
			scrollPos = (scrollPos + ((int) (getHeight() * SCROLL_INCREMENT))) % panelA.getHeight();
		}
		else {
			panelB.setVisible(false);
			
			scrollPos = 0;
		}
		
		rootPanel.setBounds(0, -1 * scrollPos, getWidth(), panelA.getHeight() + panelB.getHeight());
	}
	
	public void updateTeamList(String[] teams) {
		this.teams = teams;
		
		updatePanel(panelA);			
		updatePanel(panelB);
	}
}
