package me.nallen.divisionselector.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DivisionGui extends JFrame implements KeyListener, DataListener {
	private static final long serialVersionUID = 1L;
	private static final Color chromaColor = new Color(255, 0, 255);
	private static final Color redColor = new Color(238, 49, 36);
	private static final Color blueColor = new Color(0, 130, 200);
	private static final Color whiteColor = new Color(255, 255, 255);
	private static final Color grayColor = new Color(63, 63, 63);
	private static final Color blackColor = new Color(0, 0, 0);
	

	private static final double TEAMS_BOX_WIDTH = 0.2;
	private static final double TEAMS_BOX_HEIGHT = 0.906;
	private static final double TEAMS_BOX_TOP_OFFSET = 0.047;
	private static final double TEAMS_BOX_SIDE_OFFSET = 0.03;
	private static final double TEAMS_BOX_X_CURVE = 0.075;
	private static final double TEAMS_BOX_Y_CURVE = 0.0221;
	private static final double TEAMS_BOX_SIDE_GAP = 0.046875;
	private static final double TEAMS_BOX_BOTTOM_GAP = 0.0031;
	
	private static final double TITLE_FONT_SIZE = 0.11;
	private static final int MAX_TITLE_FONT_SIZE = 35;
	private static final double TITLE_TOP_OFFSET = 0.01;
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	

	private JPanel scienceTeamsPanel;
	private JLabel scienceTeamsTitleLabel;
	
	private JPanel technologyTeamsPanel;
	private JLabel technologyTeamsTitleLabel;

	
	public DivisionGui() {
		super("Division Selector");
		
		getContentPane().setBackground(chromaColor);
		getContentPane().setPreferredSize(new Dimension(1280, 720));
		
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(null);
	    
	    scienceTeamsPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           
	           drawTeamBox(g, getWidth(), getHeight(), redColor);
			}
	    };
	    scienceTeamsPanel.setOpaque(false);
	    scienceTeamsPanel.setLayout(null);
	    
	    scienceTeamsTitleLabel = new JLabel("SCIENCE");
	    scienceTeamsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    scienceTeamsPanel.add(scienceTeamsTitleLabel);
	    
	    add(scienceTeamsPanel);
	    
	    technologyTeamsPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           
	           drawTeamBox(g, getWidth(), getHeight(), blueColor);
			}
	    };
	    technologyTeamsPanel.setOpaque(false);
	    technologyTeamsPanel.setLayout(null);
	    
	    technologyTeamsTitleLabel = new JLabel("TECHNOLOGY");
	    technologyTeamsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    technologyTeamsPanel.add(technologyTeamsTitleLabel);
	    
	    add(technologyTeamsPanel);
	    
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
	    updateTeams();
	    
	    DivisionSelectorServer.divisionData.addListener(this);
	}
	
	private void drawTeamBox(Graphics g, int width, int height, Color color) {
		Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(color);

        graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, TEAMS_BOX_X_CURVE * width, TEAMS_BOX_Y_CURVE * height));
        
        graphics.setColor(whiteColor);

        graphics.fill(new RoundRectangle2D.Double(TEAMS_BOX_SIDE_GAP * width, 0, width - 2*(TEAMS_BOX_SIDE_GAP * width), height - TEAMS_BOX_BOTTOM_GAP * height, TEAMS_BOX_X_CURVE * width, TEAMS_BOX_Y_CURVE * height));
        graphics.fill(new Rectangle2D.Double(TEAMS_BOX_SIDE_GAP * width, 0, width - 2*(TEAMS_BOX_SIDE_GAP * width), height - TEAMS_BOX_Y_CURVE * height - TEAMS_BOX_BOTTOM_GAP * height));
	}
	
	private void updatePositions() {
		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();
		
		int team_box_width = (int) (TEAMS_BOX_WIDTH * width);
		int team_box_height = (int) (TEAMS_BOX_HEIGHT * height);
	    int team_box_x_offset = (int) (TEAMS_BOX_SIDE_OFFSET * width);
	    int team_box_y = (int) (TEAMS_BOX_TOP_OFFSET * height);
		
	    
	    scienceTeamsPanel.setBounds(team_box_x_offset, team_box_y, team_box_width, team_box_height);
	    technologyTeamsPanel.setBounds(width - team_box_width - team_box_x_offset, team_box_y, team_box_width, team_box_height);
	    
	    int title_font_size = (int) (team_box_width * TITLE_FONT_SIZE);
	    if(title_font_size > MAX_TITLE_FONT_SIZE)
	    	title_font_size = MAX_TITLE_FONT_SIZE;
	    
	    int title_y = (int) (team_box_height * TITLE_TOP_OFFSET);
	    
	    scienceTeamsTitleLabel.setBounds(0, title_y, team_box_width, title_font_size);
	    scienceTeamsTitleLabel.setFont(new Font(scienceTeamsTitleLabel.getFont().getFontName(), Font.BOLD, title_font_size));
	    
	    technologyTeamsTitleLabel.setBounds(0, title_y, team_box_width, title_font_size);
	    technologyTeamsTitleLabel.setFont(new Font(technologyTeamsTitleLabel.getFont().getFontName(), Font.BOLD, title_font_size));
	}
	
	private void updateTeams() {
		
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
		updateTeams();
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
