package au.com.ors.rest.bean;

import java.io.Serializable;

import org.springframework.util.StringUtils;

/**
 * Job application bean<br/>
 * 
 * @author hansmong
 *
 */
public class JobApplication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 392832954516730540L;

	private String _appId; // primary key

	private String _jobId; // foreign key to JobPosting

	private String driverLicenseNumber;

	private String fullName;

	private String postCode;

	private String textCoverLetter;

	private String textBriefResume;

	private String status;

	public String get_appId() {
		return _appId;
	}

	public void set_appId(String _appId) {
		this._appId = _appId;
	}

	public String get_jobId() {
		return _jobId;
	}

	public void set_jobId(String _jobId) {
		this._jobId = _jobId;
	}

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

	public String getTextCoverLetter() {
		return textCoverLetter;
	}

	public void setTextCoverLetter(String textCoverLetter) {
		this.textCoverLetter = textCoverLetter;
	}

	public String getTextBriefResume() {
		return textBriefResume;
	}

	public void setTextBriefResume(String textBriefResume) {
		this.textBriefResume = textBriefResume;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		StringBuilder sbUser = new StringBuilder();
		if (!StringUtils.isEmpty(_appId)) {
			sbUser.append("_appId=").append(_appId).append(",");
		}

		if (!StringUtils.isEmpty(_jobId)) {
			sbUser.append("_jobId=").append(_jobId).append(",");
		}

		if (!StringUtils.isEmpty(driverLicenseNumber)) {
			sbUser.append("driverLicenseNumber=").append(driverLicenseNumber).append(",");
		}

		if (!StringUtils.isEmpty(fullName)) {
			sbUser.append("fullName=").append(fullName).append(",");
		}

		if (!StringUtils.isEmpty(postCode)) {
			sbUser.append("postCode=").append(postCode).append(",");
		}

		if (!StringUtils.isEmpty(textCoverLetter)) {
			sbUser.append("textCoverLetter=true").append(",");
		} else {
			sbUser.append("textCoverLetter=false").append(",");
		}

		if (!StringUtils.isEmpty(textBriefResume)) {
			sbUser.append("textBriefResume=true");
		} else {
			sbUser.append("textBriefResume=false");
		}

		return sbUser.toString();
	}
}
