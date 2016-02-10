package me.nallen.divisionselector.server;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class ScrollingTeamListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static double ENTRY_FONT_SIZE = 0.08;
	private static int MAX_ENTRY_FONT_SIZE = 25;
	private static double ROW_SPACING = 0.04;
	
	private int numColumns;
	
	private String[] teams;
	
	private JPanel panelA;
	private JPanel panelB;

	public ScrollingTeamListPanel(int numColumns) {
		this.numColumns = numColumns;
		
		setLayout(null);
		setOpaque(false);
		
		panelA = new JPanel();
		panelA.setLayout(new MigLayout("fillx"));
		panelA.setOpaque(false);
		
		panelB = new JPanel();
		panelB.setLayout(new MigLayout("fillx"));
		panelB.setOpaque(false);
		
		add(panelA);
		add(panelB);
		
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
		
		panelA.setBounds(0, 0, width, height);
		updateFontSizeForPanel(panelA);
		int row_gap = (int) (height * ROW_SPACING);
		((MigLayout)panelA.getLayout()).setRowConstraints("[]" + row_gap + "[]");
		panelA.revalidate();
	}
	
	public void updateTeamList(String[] teams) {
		this.teams = teams;
		
		updatePanel(panelA);
		updatePanel(panelB);
	}
}
