package au.com.ors.rest.commons;

/**
 * Job application status<br/>
 * 
 * @author hansmong
 *
 */
public final class JobAppStatus {
	/**
	 * Application starts
	 */
	public static final String APP_SUBMITTED_NOT_PROCESSED = "Application submitted, but not processed yet"; // can
																												// update

	// auto checked complete
	public static final String APP_AUTO_PROCESSED = "Application auto check complete"; // CANNOT
																						// update
																						// or
																						// archive

	public static final String APP_REVIEWING = "Applicatioon being reviewed by a hiring team"; // CANNOT
																								// update
																								// or
																								// archive

	public static final String APP_REVIEWED = "Application reviewed by a hiring team";

	// public static final String APP_PROCESSING =
	// "Application being processed"; // CANNOT update or archive

	/* Cannot be archived in the statuses above */

	/**
	 * Application results
	 */
	public static final String APP_SHORTLISTED = "Application is short listed";

	public static final String APP_ACCEPTED_BY_CANDIDATE = "Application accepted by candidate"; // cannot
																								// archive

	public static final String APP_INTERVIEW_PASSED = "Application interview passed"; // can
																						// archive

	public static final String APP_INTERVIEW_FAILED = "Application interview not passed"; // can
																							// archive

	public static final String APP_REJECTED_BY_CANDIDATE = "Application rejected by candidate"; // can
																								// archive

	public static final String APP_NOT_SHORTLISTED = "Application not short listed"; // can
																						// archive
	public static final String APP_CANCELLED = "Application cancelled"; // can
																		// archive

	public static final String APP_ARCHIVED = "Application archived"; // CANNOT
																		// archived
																		// or
																		// update,
																		// already
																		// finalized
}
