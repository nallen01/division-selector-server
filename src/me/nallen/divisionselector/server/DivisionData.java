package me.nallen.divisionselector.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DivisionData {
	private Map<String, Team> teams;
	private List<String> divisions;
	private Map<String, List<String>> divisionTeams;
	
	public DivisionData() {
		
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
