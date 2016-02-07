package me.nallen.divisionselector.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DivisionData {
	private Map<String, Team> teams = new HashMap<String, Team>();
	private List<String> divisions = new ArrayList<String>();
	private Map<String, List<String>> divisionTeams = new HashMap<String, List<String>>();

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public DivisionData() {
		
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
		teams.clear();
		divisions.clear();
		divisionTeams.clear();
	}
	
	public void addDivision(String name) {
		if(!divisions.contains(name)) {
			divisions.add(name);
			divisionTeams.put(name, new ArrayList<String>());
		}
	}
	
	public void removeDivision(String name) {
		if(divisions.contains(name)) {
			divisions.remove(name);
			divisionTeams.remove(name);
		}
	}
	
	public void addTeam(Team team) {
		if(!teams.containsKey(team.number)) {
			teams.put(team.number, team);
		}
	}
	
	public void removeDivisionForTeam(String number) {
		for(String division : divisions) {
			if(divisionTeams.get(division).contains(number)) {
				divisionTeams.get(division).remove(number);
			}
		}
	}
	
	public void assignDivisionForTeam(String number, String division) {
		if(teams.containsKey(number)) {
			if(divisions.contains(division)) {
				removeDivisionForTeam(number);
				
				divisionTeams.get(division).add(number);
			}
		}
	}
	
	public void initFromCSV() {
		
	}
	
	public void createDivisionCSVs() {
		
	}
}
