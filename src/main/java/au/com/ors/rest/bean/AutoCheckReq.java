package au.com.ors.rest.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoCheckReq implements Serializable{

	public AutoCheckReq(
			@JsonProperty("driverLicenseNumber") String driverLicenseNumber,
			@JsonProperty("fullName") String fullName,
			@JsonProperty("postCode") String postCode) {
		super();
		this.driverLicenseNumber = driverLicenseNumber;
		this.fullName = fullName;
		this.postCode = postCode;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1810324388783304085L;
	
	private String driverLicenseNumber;
	
	private String fullName;
	
	private String postCode;

	public String getDriverLicenseNumber() {
		return driverLicenseNumber;
	}

	public void setDriverLicenseNumber(String driverLicenseNumber) {
		this.driverLicenseNumber = driverLicenseNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	
}
