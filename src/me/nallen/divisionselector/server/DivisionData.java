package me.nallen.divisionselector.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class DivisionData {
	private SortedMap<String, Team> teams = new TreeMap<String, Team>();
	private SortedMap<String, Boolean> teamsHaveDivisions = new TreeMap<String, Boolean>();
	private List<String> divisions = new ArrayList<String>();
	private Map<String, SortedSet<String>> divisionTeams = new HashMap<String, SortedSet<String>>();

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	private Random rand = new Random();
	
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
	private synchronized void fireUpdate(MessageType type) {
		fireUpdate(type, null);
	}
	private synchronized void fireUpdate(MessageType type, String[] args) {
		Iterator<DataListener> i = _listeners.iterator();
		while(i.hasNext())  {
			((DataListener) i.next()).update(type, args);
		}
	}
	
	public void clear() {
		clearTeams();
		clearDivisions();
		
		fireUpdate(MessageType.CLEAR_ALL);
	}
	
	public void clearTeams() {
		teams.clear();
		teamsHaveDivisions.clear();
		for(String division : divisions) {
			divisionTeams.get(division).clear();
		}
		
		fireUpdate(MessageType.CLEAR_TEAMS);
	}
	
	public void clearDivisions() {
		divisions.clear();
		divisionTeams.clear();
		
		for(String team : teamsHaveDivisions.keySet()) {
			teamsHaveDivisions.put(team, false);
		}
	
		fireUpdate(MessageType.CLEAR_DIVISIONS);
	}
	
	public void addDivision(String name) {
		if(!divisions.contains(name)) {
			divisions.add(name);
			divisionTeams.put(name, new TreeSet<String>());
			
			fireUpdate(MessageType.ADD_DIVISION, new String[] { name });
		}
	}
	
	public void removeDivision(String name) {
		if(divisions.contains(name)) {
			divisions.remove(name);
			
			for(String team : divisionTeams.get(name)) {
				teamsHaveDivisions.put(team, false);
			}
			
			divisionTeams.remove(name);
			
			fireUpdate(MessageType.REMOVE_DIVISION, new String[] { name });
		}
	}
	
	public void addTeam(Team team) {
		if(!teams.containsKey(team.number)) {
			teams.put(team.number, team);
			teamsHaveDivisions.put(team.number, false);
			
			fireUpdate(MessageType.ADD_TEAM, new String[] { team.number });
		}
	}
	
	public void removeDivisionForTeam(String number) {
		for(String division : divisions) {
			if(divisionTeams.get(division).contains(number)) {
				divisionTeams.get(division).remove(number);
				teamsHaveDivisions.put(number, false);
				
				fireUpdate(MessageType.UNASSIGN_TEAM, new String[] { number });
			}
		}
	}
	
	public void assignDivisionForTeam(String number, String division) {
		if(teams.containsKey(number)) {
			if(divisions.contains(division)) {
				removeDivisionForTeam(number);
				
				divisionTeams.get(division).add(number);
				teamsHaveDivisions.put(number, true);
				
				fireUpdate(MessageType.ASSIGN_TEAM, new String[] { number, division });
			}
		}
	}
	
	public void randomiseRemainingTeams() {
		String[] unassignedTeams = getAllUnassignedTeams();
		
		for(int i=unassignedTeams.length; i>0; i--) {
			int index = rand.nextInt(i);
			assignDivisionForTeam(unassignedTeams[index], getNextDivisionToAssign());
			
			for(int j=index; j<(i-1); j++) {
				unassignedTeams[j] = unassignedTeams[j+1];
			}
		}
	}
	
	public String getNextDivisionToAssign() {
		int current_minimum = Integer.MAX_VALUE;
		String retval = null;
		
		for(String division : divisions) {
			if(divisionTeams.get(division).size() < current_minimum) {
				current_minimum = divisionTeams.get(division).size();
				retval = division;
			}
		}
		
		return retval;
	}
	
	public Team[] getAllTeams() {
		return teams.values().toArray(new Team[teams.size()]);
	}
	
	public String[] getAllDivisions() {
		return divisions.toArray(new String[divisions.size()]);
	}
	
	public String[] getTeamsForDivision(String division) {
		if(divisionTeams.containsKey(division)) {
			return divisionTeams.get(division).toArray(new String[divisionTeams.get(division).size()]);
		}
		
		return null;
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
	
	public void createDivisionCSVs(File directory) throws Exception {
		for(String division : divisions) {
			String path = directory.getAbsolutePath() + File.separator + division + ".csv";
			
			try {
				Writer out = new FileWriter(path);
				CSVFormat csvFormat = CSVFormat.EXCEL;
				CSVPrinter printer = new CSVPrinter(out, csvFormat);
				printer.printRecord(new Object[] { "Number", "Name", "City", "State", "Country", "Short Name", "School", "Sponsor", "Age Group" });
				
				for(String teamNumber : divisionTeams.get(division)) {
					Team team = teams.get(teamNumber);
					Object[] data = { team.number, team.name, team.city, team.state, team.country, team.shortName, team.school, team.sponsor, team.ageGroup };
					printer.printRecord(data);
				}
				
				out.flush();
				out.close();
				printer.close();
			}
			catch(Exception ex) {
				throw new Exception("Unable to output CSV for division " + division);
			}
		}
	}
}
