package me.nallen.divisionselector.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class SelectorGui extends JFrame implements DataListener {
	private static final long serialVersionUID = 1L;
	
	private JPanel importPanel;
	private JButton importPickerButton;
	private JFileChooser importPicker;
	private FileNameExtensionFilter importPickerFilter;
	
	private JPanel exportPanel;
	private JButton exportPickerButton;
	private JFileChooser exportPicker;
	
	private JPanel actionsPanel;
	private JButton randomiseButton;
	private JButton generateTicketsButton;
	
	private JPanel addTeamPanel;
	private JComboBox<String> addTeamSelector;
	private JComboBox<String> addTeamDivisionSelector;
	private JButton addTeamButton;
	
	private JPanel removeTeamPanel;
	private JComboBox<String> removeTeamSelector;
	private JButton removeTeamButton;

	public SelectorGui() {
		super("Division Selector");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		
		getContentPane().setPreferredSize(new Dimension(345, 490));
		
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(new MigLayout("fill"));
	    
	    importPanel = new JPanel();
	    importPanel.setBorder(BorderFactory.createTitledBorder("Import"));
	    importPanel.setLayout(new MigLayout("fill"));
	    
	    importPickerButton = new JButton("Import from CSV");
	    importPickerButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			int retval = importPicker.showOpenDialog(importPanel);
    			if(retval == JFileChooser.APPROVE_OPTION) {
    				File csv = importPicker.getSelectedFile();
    				
    				try {
    					DivisionSelectorServer.divisionData.initFromCSV(csv);
    				}
    				catch(Exception ex) {
    					JOptionPane.showMessageDialog(null, ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
    				}
    			}
			}
	    });
	    importPanel.add(importPickerButton, "w 100%");
	    
	    importPicker = new JFileChooser();
	    importPickerFilter = new FileNameExtensionFilter("CSV Files", "csv");
	    importPicker.setFileFilter(importPickerFilter);
	    
	    add(importPanel, "wrap, w 100%");
	    
	    exportPanel = new JPanel();
	    exportPanel.setBorder(BorderFactory.createTitledBorder("Export"));
	    exportPanel.setLayout(new MigLayout("fill"));
	    
	    exportPickerButton = new JButton("Export Division CSVs");
	    exportPickerButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			int retval = exportPicker.showOpenDialog(exportPanel);
    			if(retval == JFileChooser.APPROVE_OPTION) {
    				File dir = exportPicker.getCurrentDirectory();
    				
    				try {
    					DivisionSelectorServer.divisionData.createDivisionCSVs(dir);
    				}
    				catch(Exception ex) {
    					JOptionPane.showMessageDialog(null, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
    				}
    			}
			}
	    });
	    exportPanel.add(exportPickerButton, "w 100%");
	    
	    exportPicker = new JFileChooser();
	    exportPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    exportPicker.setAcceptAllFileFilterUsed(false);
	    
	    add(exportPanel, "wrap, w 100%");
	    
	    actionsPanel = new JPanel();
	    actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
	    actionsPanel.setLayout(new MigLayout("fill"));

		randomiseButton = new JButton("Randomise");
		actionsPanel.add(randomiseButton, "w 50%");
		
		generateTicketsButton = new JButton("Generate Tickets");
		actionsPanel.add(generateTicketsButton, "w 50%, wrap");

	    addTeamPanel = new JPanel();
	    addTeamPanel.setBorder(BorderFactory.createTitledBorder("Add Team"));
	    addTeamPanel.setLayout(new MigLayout("fill"));
	    
		addTeamSelector = new JComboBox<String>();
		addTeamPanel.add(addTeamSelector, "w 33%");
		
		addTeamDivisionSelector = new JComboBox<String>();
		addTeamPanel.add(addTeamDivisionSelector, "w 33%");
		
		addTeamButton = new JButton("Add");
		addTeamPanel.add(addTeamButton, "w 33%, wrap");
		
		actionsPanel.add(addTeamPanel, "w 100%, span, wrap");

	    removeTeamPanel = new JPanel();
	    removeTeamPanel.setBorder(BorderFactory.createTitledBorder("Remove Team"));
	    removeTeamPanel.setLayout(new MigLayout("fill"));
	    
	    removeTeamSelector = new JComboBox<String>();
	    removeTeamPanel.add(removeTeamSelector, "w 50%");
		
	    removeTeamButton = new JButton("Remove");
	    removeTeamPanel.add(removeTeamButton, "w 50%, wrap");
		
		actionsPanel.add(removeTeamPanel, "w 100%, span, wrap");
	    
	    add(actionsPanel, "wrap, w 100%");
		
		pack();
	    updateData();
	    
	    DivisionSelectorServer.divisionData.addListener(this);
	}
	
	public void updateData() {
		
	}

	public void update() {
		updateData();
	}
}
