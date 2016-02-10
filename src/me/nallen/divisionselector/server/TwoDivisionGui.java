package me.nallen.divisionselector.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TwoDivisionGui extends OverlayPanel {
	private static final long serialVersionUID = 1L;
	private static final double TEAMS_BOX_WIDTH = 0.2;
	private static final double TEAMS_BOX_HEIGHT = 0.906;
	private static final double TEAMS_BOX_TOP_OFFSET = 0.047;
	private static final double TEAMS_BOX_SIDE_OFFSET = 0.03;
	
	private static final double TEAMS_BOX_X_CURVE = 0.015 / TEAMS_BOX_WIDTH;
	private static final double TEAMS_BOX_Y_CURVE = 0.02 / TEAMS_BOX_HEIGHT;
	private static final double TEAMS_BOX_SIDE_GAP = 0.009375 / TEAMS_BOX_WIDTH;
	private static final double TEAMS_BOX_BOTTOM_GAP = 0.0028 / TEAMS_BOX_HEIGHT;
	
	private static final double TITLE_FONT_SIZE = 0.11;
	private static final int MAX_TITLE_FONT_SIZE = 35;
	private static final double TITLE_TOP_OFFSET = 0.01;
	
	private static final int NUM_TEAM_COLUMNS = 2;
	
	private JPanel scienceTeamsPanel;
	private JLabel scienceTeamsTitleLabel;
	private ScrollingTeamListPanel scienceTeamsListPanel;
	
	private JPanel technologyTeamsPanel;
	private JLabel technologyTeamsTitleLabel;
	private ScrollingTeamListPanel technologyTeamsListPanel;
	
	public TwoDivisionGui() {
		setLayout(null);
		setOpaque(false);
		
		scienceTeamsPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           
	           drawTeamBox(g, getWidth(), getHeight(), DivisionGui.redColor);
			}
	    };
	    scienceTeamsPanel.setOpaque(false);
	    scienceTeamsPanel.setLayout(null);
	    
	    scienceTeamsTitleLabel = new JLabel("SCIENCE");
	    scienceTeamsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    scienceTeamsPanel.add(scienceTeamsTitleLabel);
	    
	    scienceTeamsListPanel = new ScrollingTeamListPanel(NUM_TEAM_COLUMNS);
	    scienceTeamsPanel.add(scienceTeamsListPanel);
	    
	    add(scienceTeamsPanel);
	    
	    technologyTeamsPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           
	           drawTeamBox(g, getWidth(), getHeight(), DivisionGui.blueColor);
			}
	    };
	    technologyTeamsPanel.setOpaque(false);
	    technologyTeamsPanel.setLayout(null);
	    
	    technologyTeamsTitleLabel = new JLabel("TECHNOLOGY");
	    technologyTeamsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    technologyTeamsPanel.add(technologyTeamsTitleLabel);
	    
	    technologyTeamsListPanel = new ScrollingTeamListPanel(NUM_TEAM_COLUMNS);
	    technologyTeamsPanel.add(technologyTeamsListPanel);
	    
	    add(technologyTeamsPanel);
	    
	    addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}

			public void componentMoved(ComponentEvent arg0) {}

			public void componentResized(ComponentEvent arg0) {
				updatePositions();
			}

			public void componentShown(ComponentEvent arg0) {}
		});
	    
	    updatePositions();
	    updateData();
	}
	
	public void updateData() {
		String scienceTitle = DivisionSelectorServer.divisionData.getAllDivisions()[0];
		String technologyTitle = DivisionSelectorServer.divisionData.getAllDivisions()[1];
		
		scienceTeamsTitleLabel.setText(scienceTitle.toUpperCase());
		technologyTeamsTitleLabel.setText(technologyTitle.toUpperCase());
		
		scienceTeamsListPanel.updateTeamList(DivisionSelectorServer.divisionData.getTeamsForDivision(scienceTitle));
		technologyTeamsListPanel.updateTeamList(DivisionSelectorServer.divisionData.getTeamsForDivision(technologyTitle));
	}

	private void updatePositions() {
		int width = getWidth();
		int height = getHeight();
		
		int team_box_width = (int) (TEAMS_BOX_WIDTH * width);
		int team_box_height = (int) (TEAMS_BOX_HEIGHT * height);
	    int team_box_x_offset = (int) (TEAMS_BOX_SIDE_OFFSET * width);
	    int team_box_y = (int) (TEAMS_BOX_TOP_OFFSET * height);
	    
	    int team_box_inner_offset_x = (int) Math.ceil(TEAMS_BOX_SIDE_GAP * team_box_width);
	    int team_box_inner_width = team_box_width - 2*team_box_inner_offset_x;
	    int team_box_inner_height = team_box_height - (int) Math.ceil(TEAMS_BOX_BOTTOM_GAP * team_box_height);
		
	    
	    scienceTeamsPanel.setBounds(team_box_x_offset, team_box_y, team_box_width, team_box_height);
	    technologyTeamsPanel.setBounds(width - team_box_width - team_box_x_offset, team_box_y, team_box_width, team_box_height);
	    
	    int title_font_size = (int) (team_box_width * TITLE_FONT_SIZE);
	    if(title_font_size > MAX_TITLE_FONT_SIZE)
	    	title_font_size = MAX_TITLE_FONT_SIZE;
	    
	    int title_y = (int) (team_box_height * TITLE_TOP_OFFSET);
	    
	    scienceTeamsTitleLabel.setBounds(0, title_y, team_box_width, title_font_size);
	    scienceTeamsTitleLabel.setFont(new Font(scienceTeamsTitleLabel.getFont().getFontName(), Font.BOLD, title_font_size));
	    
	    scienceTeamsListPanel.setBounds(team_box_inner_offset_x, title_y + title_font_size, team_box_inner_width, team_box_inner_height - title_font_size - title_y);
	    
	    technologyTeamsTitleLabel.setBounds(0, title_y, team_box_width, title_font_size);
	    technologyTeamsTitleLabel.setFont(new Font(technologyTeamsTitleLabel.getFont().getFontName(), Font.BOLD, title_font_size));
	
	    technologyTeamsListPanel.setBounds(team_box_inner_offset_x, title_y + title_font_size, team_box_inner_width, team_box_inner_height - title_font_size - title_y);
	}
	
	private void drawTeamBox(Graphics g, int width, int height, Color color) {
		Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(color);

        graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, TEAMS_BOX_X_CURVE * width, TEAMS_BOX_Y_CURVE * height));
        
        graphics.setColor(DivisionGui.whiteColor);

        graphics.fill(new RoundRectangle2D.Double(TEAMS_BOX_SIDE_GAP * width, 0, width - 2*(TEAMS_BOX_SIDE_GAP * width), height - TEAMS_BOX_BOTTOM_GAP * height, TEAMS_BOX_X_CURVE * width, TEAMS_BOX_Y_CURVE * height));
        graphics.fill(new Rectangle2D.Double(TEAMS_BOX_SIDE_GAP * width, 0, width - 2*(TEAMS_BOX_SIDE_GAP * width), height - TEAMS_BOX_Y_CURVE * height - TEAMS_BOX_BOTTOM_GAP * height));
	}

}
