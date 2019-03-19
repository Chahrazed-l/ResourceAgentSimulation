package com.resource.start;

public class Resource {
	public String username;
	public String rname;
	public long id;
	public int nbtask;
	public String password;
	public double batchrate;
	public double nonbatchrate;
	public double fiforate;
	public double liforate;
	public double randomrate;
	public int totworkingdays;
	public int wdays;

	public Resource() {

	}

	public Resource(String username, String password, double batchrate, double nonbatchrate, double fiforate,
			double liforate, double randomrate) {
		this.username = username;
		this.password = password;
		this.batchrate = batchrate;
		this.nonbatchrate = nonbatchrate;
		this.fiforate = fiforate;
		this.liforate = liforate;
		this.randomrate = randomrate;

	}

	public Resource(String rname, String username, int nbtask, long id) {
		this.rname = rname;
		this.username = username;
		this.id = id;
		this.nbtask = nbtask;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNbtask() {
		return nbtask;
	}

	public void setNbtask(int nbtask) {
		this.nbtask = nbtask;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getBatchrate() {
		return batchrate;
	}

	public void setBatchrate(double batchrate) {
		this.batchrate = batchrate;
	}

	public double getNonbatchrate() {
		return nonbatchrate;
	}

	public void setNonbatchrate(double nonbatchrate) {
		this.nonbatchrate = nonbatchrate;
	}

	public double getFiforate() {
		return fiforate;
	}

	public void setFiforate(double fiforate) {
		this.fiforate = fiforate;
	}

	public double getLiforate() {
		return liforate;
	}

	public void setLiforate(double liforate) {
		this.liforate = liforate;
	}

	public double getRandomrate() {
		return randomrate;
	}

	public void setRandomrate(double randomrate) {
		this.randomrate = randomrate;
	}

	public int getTotworkingdays() {
		return totworkingdays;
	}

	public void setTotworkingdays(int totworkingdays) {
		this.totworkingdays = totworkingdays;
	}

	public int getWdays() {
		return wdays;
	}

	public void setWdays(int wdays) {
		this.wdays = wdays;
	}

}
