package au.com.ors.rest.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JobPosting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String _jobId;

	String closingTime;

	String salaryRate;

	String positionType;

	String loacation;

	String details;
	
	String status;
	
	@JsonCreator
	public JobPosting(
			@JsonProperty("_jobId") String _jobId,
			@JsonProperty("closingTime") String closingTime,
			@JsonProperty("salaryRate") String salaryRate,
			@JsonProperty("positionType") String positionType,
			@JsonProperty("loacation") String loacation,
			@JsonProperty("details") String details,
			@JsonProperty("status") String status
			) {
		super();
		this._jobId = _jobId;
		this.closingTime = closingTime;
		this.salaryRate = salaryRate;
		this.positionType = positionType;
		this.loacation = loacation;
		this.details = details;
		this.status = status;
	}

	public String get_jobId() {
		return _jobId;
	}

	public void set_jobId(String _jobId) {
		this._jobId = _jobId;
	}

	public String getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(String closingTime) {
		this.closingTime = closingTime;
	}

	public String getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(String salaryRate) {
		this.salaryRate = salaryRate;
	}

	public String getPositionType() {
		return positionType;
	}

	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	public String getLoacation() {
		return loacation;
	}

	public void setLoacation(String loacation) {
		this.loacation = loacation;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
