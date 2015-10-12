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

//public final class JobAppStatus {
//	/**
//	 * Application starts
//	 */
//	public static final String APP_SUBMITTED_NOT_PROCESSED = "APP_SUBMITTED_NOT_PROCESSED"; // can
//																							// update
//
//	// auto checked complete
//	public static final String APP_AUTO_PROCESSED = "APP_AUTO_PROCESSED"; // CANNOT
//																			// update
//																			// or
//																			// archive
//
//	public static final String APP_REVIEWING = "APP_REVIEWING"; // CANNOT
//																// update
//																// or
//																// archive
//
//	public static final String APP_REVIEWED = "APP_REVIEWED";
//
//	// public static final String APP_PROCESSING =
//	// "Application being processed"; // CANNOT update or archive
//
//	/* Cannot be archived in the statuses above */
//
//	/**
//	 * Application results
//	 */
//	public static final String APP_SHORTLISTED = "APP_SHORTLISTED";
//
//	public static final String APP_ACCEPTED_BY_CANDIDATE = "APP_ACCEPTED_BY_CANDIDATE"; // cannot
//																						// archive
//
//	public static final String APP_INTERVIEW_PASSED = "APP_INTERVIEW_PASSED"; // can
//																				// archive
//
//	public static final String APP_INTERVIEW_FAILED = "APP_INTERVIEW_FAILED"; // can
//																				// archive
//
//	public static final String APP_REJECTED_BY_CANDIDATE = "APP_REJECTED_BY_CANDIDATE"; // can
//																						// archive
//
//	public static final String APP_NOT_SHORTLISTED = "APP_NOT_SHORTLISTED"; // can
//																			// archive
//	public static final String APP_CANCELLED = "APP_CANCELLED"; // can
//																// archive
//
//	public static final String APP_ARCHIVED = "APP_ARCHIVED"; // CANNOT
//																// archived
//																// or
//																// update,
//																// already
//																// finalized
//}
