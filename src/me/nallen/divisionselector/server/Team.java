package me.nallen.divisionselector.server;

public class Team {
	public String number;
	public String name;
	public String city;
	public String state;
	public String country;
	public String shortName;
	public String school;
	public String sponsor;
	public String ageGroup;
	
	public Team(String number, String name, String city,
			String state, String country, String shortName,
			String school, String sponsor, String ageGroup) {
		this.number = number;
		this.name = name;
		this.city = city;
		this.state = state;
		this.country = country;
		this.shortName = shortName;
		this.school = school;
		this.sponsor = sponsor;
		this.ageGroup = ageGroup;
	}
}
