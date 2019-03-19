package com.resource.start;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class UserObjectInfo {

	public String firststring;
	public String secondstring;
	public String thirdstring;

	public String token;
	public long id;
	public String idprocess;
	public long userid;

	public double param1;
	public double param2;
	public double param3;

	public int p1;
	public int p2;

	public ArrayList<TaskInfo> tasks;
	public int nbtask;
	public Map<Integer, Vector<TaskInfo>> maplist;

	public ArrayList<Double> av;
	public ArrayList<Integer> inst;

	public int nbjour;

	public int sizemap;

	public boolean mode;

	public UserObjectInfo() {

	}

	public UserObjectInfo(String firststring, String secondstring, String thirdstring) {
		this.firststring = firststring;
		this.secondstring = secondstring;
		this.thirdstring = thirdstring;

	}

	public UserObjectInfo(String token, long id) {
		this.token = token;
		this.id = id;

	}

	public UserObjectInfo(String token, String id1) {
		this.token = token;
		this.idprocess = id1;

	}

	public UserObjectInfo(long id, String token, String url) {
		this.id = id;
		this.token = token;
		this.firststring = url;
	}

	public UserObjectInfo(long id, long userid, String token, String url) {
		this.id = id;
		this.userid = userid;
		this.token = token;
		this.firststring = url;
	}

	public UserObjectInfo(double param1, double param2, double param3, int task, ArrayList<TaskInfo> tasks) {
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.nbtask = task;
		this.tasks=tasks;
	}

	public UserObjectInfo(double param1, double param2, ArrayList<TaskInfo> tasks, int days) {
		this.param1 = param1;
		this.param2 = param2;
		this.tasks = tasks;
		this.sizemap = days;
	}

	public UserObjectInfo(boolean mode, ArrayList<TaskInfo> tasks, int sizemap) {
		this.mode = mode;
		this.tasks = tasks;
		this.sizemap = sizemap;

	}

	public UserObjectInfo(ArrayList<Double> av, ArrayList<Integer> inst , int nbjour) {
		this.av=av;
		this.inst=inst;
		this.nbjour=nbjour;
	}
	public UserObjectInfo(String param1, String param2 , int nbusers) {
		this.firststring=param1;
		this.secondstring=param2;
		this.nbjour=nbusers;
	}

	public ArrayList<Double> getAv() {
		return av;
	}

	public void setAv(ArrayList<Double> av) {
		this.av = av;
	}

	public ArrayList<Integer> getInst() {
		return inst;
	}

	public void setInst(ArrayList<Integer> inst) {
		this.inst = inst;
	}

	public int getNbjour() {
		return nbjour;
	}

	public void setNbjour(int nbjour) {
		this.nbjour = nbjour;
	}

	public UserObjectInfo(ArrayList<TaskInfo> tasks) {

		this.tasks = tasks;

	}

	public UserObjectInfo(int p1, int p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public UserObjectInfo(Map<Integer, Vector<TaskInfo>> maplist, boolean mode) {

		this.maplist = maplist;
		this.mode = mode;

	}

	public boolean isMode() {
		return mode;
	}

	public Map<Integer, Vector<TaskInfo>> getMaplist() {
		return maplist;
	}

	public void setMaplist(Map<Integer, Vector<TaskInfo>> maplist) {
		this.maplist = maplist;
	}

	public int getP1() {
		return p1;
	}

	public void setP1(int p1) {
		this.p1 = p1;
	}

	public int getP2() {
		return p2;
	}

	public void setP2(int p2) {
		this.p2 = p2;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public ArrayList<TaskInfo> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<TaskInfo> tasks) {
		this.tasks = tasks;
	}

	public double getParam1() {
		return param1;
	}

	public int getSizemap() {
		return sizemap;
	}

	public void setSizemap(int sizemap) {
		this.sizemap = sizemap;
	}

	public void setParam1(double param1) {
		this.param1 = param1;
	}

	public double getParam2() {
		return param2;
	}

	public void setParam2(double param2) {
		this.param2 = param2;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getfirststring() {
		return firststring;
	}

	public void setfirststring(String firststring) {
		this.firststring = firststring;
	}

	public String getsecondstring() {
		return secondstring;
	}

	public void setsecondstring(String secondstring) {
		this.secondstring = secondstring;
	}

	public String getthirdstring() {
		return thirdstring;
	}

	public void setthirdstring(String thirdstring) {
		this.thirdstring = thirdstring;
	}

	public String getIdprocess() {
		return idprocess;
	}

	public void setIdprocess(String idprocess) {
		this.idprocess = idprocess;
	}

	public int getNbtask() {
		return nbtask;
	}

	public void setNbtask(int nbtask) {
		this.nbtask = nbtask;
	}
	
	

}
