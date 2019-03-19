package com.resource.start;

public class CaseInfo {

	public String caseId;
	public String processName;
	public String startDate;
	public String endDate;
	public String archiveDate;
	public String startedBy;

	public CaseInfo(String caseId, String processName, String startDate, String endDate, String archiveDate,
			String startBy) {

		this.caseId = caseId;
		this.processName = processName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.archiveDate = archiveDate;
		this.startedBy = startBy;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

}
