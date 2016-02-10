package me.nallen.divisionselector.server;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class DivisionData {
	private Map<String, Team> teams = new TreeMap<String, Team>();
	private Map<String, Boolean> teamsHaveDivisions = new TreeMap<String, Boolean>();
	private List<String> divisions = new ArrayList<String>();
	private Map<String, List<String>> divisionTeams = new HashMap<String, List<String>>();

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public DivisionData() {
		addDivision("Science");
		addDivision("Technology");
	}
	
	public synchronized void addListener(DataListener listener)  {
		_listeners.add(listener);
	}
	public synchronized void removeListener(DataListener listener)   {
		_listeners.remove(listener);
	}
	private synchronized void fireUpdate() {
		Iterator<DataListener> i = _listeners.iterator();
		while(i.hasNext())  {
			((DataListener) i.next()).update();
		}
	}
	
	public void clear() {
		clearTeams();
		clearDivisions();
		
		fireUpdate();
	}
	
	public void clearTeams() {
		teams.clear();
		teamsHaveDivisions.clear();
		for(String division : divisions) {
			divisionTeams.get(division).clear();
		}
		
		fireUpdate();
	}
	
	public void clearDivisions() {
		divisions.clear();
		divisionTeams.clear();
		
		for(String team : teamsHaveDivisions.keySet()) {
			teamsHaveDivisions.put(team, false);
		}
	
		fireUpdate();
	}
	
	public void addDivision(String name) {
		if(!divisions.contains(name)) {
			divisions.add(name);
			divisionTeams.put(name, new ArrayList<String>());
			
			fireUpdate();
		}
	}
	
	public void removeDivision(String name) {
		if(divisions.contains(name)) {
			divisions.remove(name);
			
			for(String team : divisionTeams.get(name)) {
				teamsHaveDivisions.put(team, false);
			}
			
			divisionTeams.remove(name);
			
			fireUpdate();
		}
	}
	
	public void addTeam(Team team) {
		if(!teams.containsKey(team.number)) {
			teams.put(team.number, team);
			teamsHaveDivisions.put(team.number, false);
			
			fireUpdate();
		}
	}
	
	public void removeDivisionForTeam(String number) {
		for(String division : divisions) {
			if(divisionTeams.get(division).contains(number)) {
				divisionTeams.get(division).remove(number);
				teamsHaveDivisions.put(number, false);
				
				fireUpdate();
			}
		}
	}
	
	public void assignDivisionForTeam(String number, String division) {
		if(teams.containsKey(number)) {
			if(divisions.contains(division)) {
				removeDivisionForTeam(number);
				
				divisionTeams.get(division).add(number);
				teamsHaveDivisions.put(number, true);
				
				fireUpdate();
			}
		}
	}
	
	public String[] getAllDivisions() {
		return divisions.toArray(new String[divisions.size()]);
	}
	
	public String[] getAllUnassignedTeams() {
		return getTeamsForAssignedStatus(false);
	}
	
	public String[] getAllAssignedTeams() {
		return getTeamsForAssignedStatus(true);
	}
	
	private String[] getTeamsForAssignedStatus(Boolean status) {
		List<String> teamList = new ArrayList<String>();
		
		for(Map.Entry<String, Boolean> e : teamsHaveDivisions.entrySet()) {
			if(e.getValue() == status) {
				teamList.add(e.getKey());
			}
		}
		
		return teamList.toArray(new String[teamList.size()]);
	}
	
	public void initFromCSV(File csv) throws Exception {
		List<Team> teamList = new ArrayList<Team>();
		
		try {
			Reader in = new FileReader(csv);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			for(CSVRecord record : records) {
				Team team = new Team(record.get("Number"), record.get("Name"), record.get("City"), record.get("State"), record.get("Country"), record.get("Short Name"), record.get("School"), record.get("Sponsor"), record.get("Age Group"));
				teamList.add(team);
			}
		}
		catch(Exception ex) {
			throw new Exception("Invalid CSV file supplied.");
		}
		
		clearTeams();
		for(Team team : teamList) {
			addTeam(team);
		}
	}
	
	public void createDivisionCSVs(File directory) {
		
	}
}
