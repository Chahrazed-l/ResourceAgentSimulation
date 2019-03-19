package com.resource.start;

public class TaskInfo {

	public long id;
	public String name;
	public String state;
	public String timestamp;
	public String rootcaseId;
	public String executedBy;
	public String assigneddate;

	public TaskInfo(long id, String name, String state, String timestamp, String rootcaseId, String executedBy,
			String assigneddate) {
		this.id = id;
		this.name = name;
		this.state = state;
		this.timestamp = timestamp;
		this.rootcaseId = rootcaseId;
		this.executedBy = executedBy;
		this.assigneddate = assigneddate;
	}

	public String getAssigneddate() {
		return assigneddate;
	}

	public void setAssigneddate(String assigneddate) {
		this.assigneddate = assigneddate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getRootcaseId() {
		return rootcaseId;
	}

	public void setRootcaseId(String rootcaseId) {
		this.rootcaseId = rootcaseId;
	}

	public String getExecutedBy() {
		return executedBy;
	}

	public void setExecutedBy(String executedBy) {
		this.executedBy = executedBy;
	}

}
