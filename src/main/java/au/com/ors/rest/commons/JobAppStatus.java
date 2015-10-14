package au.com.ors.rest.commons;

/**
 * Job application status<br/>
 * 
 * @author hansmong
 *
 */
public enum JobAppStatus {
	APP_SUBMITTED_NOT_PROCESSED, 
	APP_AUTO_PROCESSED, 
	APP_REVIEWING, 
	APP_REVIEWED, 
	APP_SHORTLISTED, 
	APP_ACCEPTED_BY_CANDIDATE,
	APP_INTERVIEW_PASSED, // can archive
	APP_INTERVIEW_FAILED, // can archive
	APP_REJECTED_BY_CANDIDATE, // can archive
	APP_NOT_SHORTLISTED, // can archive
	APP_CANCELLED, // can archive
	APP_ARCHIVED;
	
	public static boolean contains(String status) {
		for (JobAppStatus appStat : JobAppStatus.values()) {
			if (appStat.name().equals(status)) {
				return true;
			}
		}
		
		return false;
	}
}