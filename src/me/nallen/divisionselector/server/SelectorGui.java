package me.nallen.divisionselector.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
	private JFileChooser generateTicketsPicker;
	private FileNameExtensionFilter generateTicketsPickerFilter;
	
	private JPanel addTeamPanel;
	private JComboBox<String> addTeamSelector;
	private DefaultComboBoxModel<String> addTeamSelectorModel;
	private JComboBox<String> addTeamDivisionSelector;
	private DefaultComboBoxModel<String> addTeamDivisionSelectorModel;
	private JButton addTeamButton;
	
	private JPanel removeTeamPanel;
	private JComboBox<String> removeTeamSelector;
	private DefaultComboBoxModel<String> removeTeamSelectorModel;
	private JButton removeTeamButton;

	private JPanel addDivisionPanel;
	private JTextField addDivisionName;
	private JButton addDivisionButton;

	private JPanel removeDivisionPanel;
	private JComboBox<String> removeDivisionSelector;
	private DefaultComboBoxModel<String> removeDivisionSelectorModel;
	private JButton removeDivisionButton;

	public SelectorGui() {
		super("Division Selector");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		
		getContentPane().setPreferredSize(new Dimension(500, 700));
		
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
		randomiseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null, "Are you sure you want to randomise the remaining teams?");
				if(ret == JOptionPane.YES_OPTION) {
					DivisionSelectorServer.divisionData.randomiseRemainingTeams();
				}
			}
		});
		actionsPanel.add(randomiseButton, "w 50%");
		
		generateTicketsButton = new JButton("Generate Tickets");
		generateTicketsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int retval = generateTicketsPicker.showSaveDialog(importPanel);
    			if(retval == JFileChooser.APPROVE_OPTION) {
    				File output = generateTicketsPicker.getSelectedFile();
    				
    				String path = output.getAbsolutePath();
    				if(!path.endsWith(".pdf")) {
    					path = path + ".pdf";
    				}
    				
    				Object[] options = { 1, 2, 4, 8 };
    				Object selectedValue = JOptionPane.showInputDialog(null, "Number of tickets per page", "Layout", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    				
    				if(selectedValue != null && selectedValue instanceof Integer) {
    					int pow = 0;
    					switch((Integer) selectedValue) {
	    					case 1: pow = 0; break;
	    					case 2: pow = 1; break;
	    					case 4: pow = 2; break;
	    					case 8: pow = 3; break;
    					}
    					
        				try {
        					TicketGenerator.createTicketPDF(path, pow);
        					
        				} catch (Exception ex) {
        					ex.printStackTrace();
        					JOptionPane.showMessageDialog(null, ex.getMessage(), "Generation Error", JOptionPane.ERROR_MESSAGE);
        				}
    				}
    			}
			}
		});
		actionsPanel.add(generateTicketsButton, "w 50%, wrap");
		
		generateTicketsPicker = new JFileChooser();
	    generateTicketsPickerFilter = new FileNameExtensionFilter("PDF File", "pdf");
	    generateTicketsPicker.setFileFilter(generateTicketsPickerFilter);

	    addTeamPanel = new JPanel();
	    addTeamPanel.setBorder(BorderFactory.createTitledBorder("Add Team"));
	    addTeamPanel.setLayout(new MigLayout("fill"));
	    
		addTeamSelector = new JComboBox<String>();
		addTeamSelectorModel = new DefaultComboBoxModel<String>();
		addTeamSelector.setModel(addTeamSelectorModel);
		addTeamPanel.add(addTeamSelector, "w 33%");
		
		addTeamDivisionSelector = new JComboBox<String>();
		addTeamDivisionSelectorModel = new DefaultComboBoxModel<String>();
		addTeamDivisionSelector.setModel(addTeamDivisionSelectorModel);
		addTeamPanel.add(addTeamDivisionSelector, "w 33%");
		
		addTeamButton = new JButton("Add");
		addTeamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String team = addTeamSelector.getItemAt(addTeamSelector.getSelectedIndex());
				String division = addTeamDivisionSelector.getItemAt(addTeamDivisionSelector.getSelectedIndex());
				if(team != null && division != null) {
					DivisionSelectorServer.divisionData.assignDivisionForTeam(team, division);					
				}
			}
		});
		addTeamPanel.add(addTeamButton, "w 33%, wrap");
		
		actionsPanel.add(addTeamPanel, "w 100%, span, wrap");

	    removeTeamPanel = new JPanel();
	    removeTeamPanel.setBorder(BorderFactory.createTitledBorder("Remove Team"));
	    removeTeamPanel.setLayout(new MigLayout("fill"));
	    
	    removeTeamSelector = new JComboBox<String>();
		removeTeamSelectorModel = new DefaultComboBoxModel<String>();
		removeTeamSelector.setModel(removeTeamSelectorModel);
	    removeTeamPanel.add(removeTeamSelector, "w 50%");
		
	    removeTeamButton = new JButton("Remove");
	    removeTeamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String team = removeTeamSelector.getItemAt(removeTeamSelector.getSelectedIndex());
				if(team != null) {
					DivisionSelectorServer.divisionData.removeDivisionForTeam(team);
				}
			}
	    });
	    removeTeamPanel.add(removeTeamButton, "w 50%, wrap");
		
		actionsPanel.add(removeTeamPanel, "w 100%, span, wrap");
		
		addDivisionPanel = new JPanel();
		addDivisionPanel.setBorder(BorderFactory.createTitledBorder("Add Division"));
		addDivisionPanel.setLayout(new MigLayout("fill"));
	    
	    addDivisionName = new JTextField();
		addDivisionPanel.add(addDivisionName, "w 50%");
		
	    addDivisionButton = new JButton("Add Division");
	    addDivisionButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String name = addDivisionName.getText();
    			if(name.length() > 0) {
    				DivisionSelectorServer.divisionData.addDivision(name);    				
    			}
    			
    			addDivisionName.setText("");
			}
	    });
	    addDivisionPanel.add(addDivisionButton, "w 50%, wrap");
		
		actionsPanel.add(addDivisionPanel, "w 100%, span, wrap");
		
		removeDivisionPanel = new JPanel();
		removeDivisionPanel.setBorder(BorderFactory.createTitledBorder("Remove Division"));
		removeDivisionPanel.setLayout(new MigLayout("fill"));

		removeDivisionSelector = new JComboBox<String>();
		removeDivisionSelectorModel = new DefaultComboBoxModel<String>();
		removeDivisionSelector.setModel(removeDivisionSelectorModel);
	    removeDivisionPanel.add(removeDivisionSelector, "w 50%");
		
	    removeDivisionButton = new JButton("Remove Division");
	    removeDivisionButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String name = removeDivisionSelector.getItemAt(removeDivisionSelector.getSelectedIndex());
    			if(name != null) {
    				DivisionSelectorServer.divisionData.removeDivision(name);    				
    			}
    			
    			addDivisionName.setText("");
			}
	    });
	    removeDivisionPanel.add(removeDivisionButton, "w 50%, wrap");
		
		actionsPanel.add(removeDivisionPanel, "w 100%, span, wrap");
	    
	    add(actionsPanel, "wrap, w 100%");
		
		pack();
	    updateData();
	    
	    DivisionSelectorServer.divisionData.addListener(this);
	}
	
	private <E> void updateSelectorWithData(JComboBox<E> combo, DefaultComboBoxModel<E> model, E[] data) {
		E previousSelection = combo.getItemAt(combo.getSelectedIndex());
		boolean validPreviousSelection = false;
		
		model.removeAllElements();
		for(E entry : data) {
			model.addElement(entry);
			if(entry.equals(previousSelection)) {
				validPreviousSelection = true;
			}
		}
		
		if(validPreviousSelection) {
			model.setSelectedItem(previousSelection);
		}
		combo.setEnabled(model.getSize() > 0);
	}
	
	public void updateData() {
		// Add Team Panel
		updateSelectorWithData(addTeamSelector, addTeamSelectorModel,
				DivisionSelectorServer.divisionData.getAllUnassignedTeams());
		
		updateSelectorWithData(addTeamDivisionSelector, addTeamDivisionSelectorModel,
				DivisionSelectorServer.divisionData.getAllDivisions());
		
		addTeamButton.setEnabled(addTeamSelector.isEnabled() && addTeamDivisionSelector.isEnabled());
		
		// Remove Team Panel
		updateSelectorWithData(removeTeamSelector, removeTeamSelectorModel,
				DivisionSelectorServer.divisionData.getAllAssignedTeams());
		
		removeTeamButton.setEnabled(removeTeamSelector.isEnabled());
		
		// Randomise Button
		randomiseButton.setEnabled(addTeamButton.isEnabled());
		
		// Generate Tickets Button
		generateTicketsButton.setEnabled(DivisionSelectorServer.divisionData.getAllTeams().length > 0);
		
		// Remove Division Panel
		updateSelectorWithData(removeDivisionSelector, removeDivisionSelectorModel,
				DivisionSelectorServer.divisionData.getAllDivisions());
		
		removeDivisionButton.setEnabled(removeDivisionSelector.isEnabled());
	}

	public void update() {
		updateData();
	}
}
