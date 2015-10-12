package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.bean.JobApplication;
import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.commons.JobAppStatus;
import au.com.ors.rest.dao.JobAppDAO;
import au.com.ors.rest.dao.JobPostingDAO;
import au.com.ors.rest.exceptions.JobAppMalformatException;
import au.com.ors.rest.exceptions.JobAppStatusCannotModifyException;
import au.com.ors.rest.exceptions.JobApplicationNotFoundException;
import au.com.ors.rest.resource.JobApplicationResource;
import au.com.ors.rest.resource.assembler.JobApplicationResourceAssembler;

@RequestMapping("/jobapplications")
public class JobAppController {
	@Autowired
	JobAppDAO jobAppDao;

	@Autowired
	JobPostingDAO jobDao;

	@Autowired
	JobApplicationResourceAssembler appResourceAssembler;

	@SuppressWarnings("rawtypes")
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		return null;
	}

	/*************************************************************************************
	 * GET methods
	 *************************************************************************************/

	/**
	 * Get job applications<br/>
	 * If a job ID is not given, the method will get all applications<br/>
	 * Otherwise it will get the applications which point to the job with job ID<br/>
	 * 
	 * @param _jobId
	 *            job ID
	 * @return applications list
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<JobApplicationResource>> jobApplications(
			@RequestParam(name = "_jobId", required = false) String _jobId) {
		List<JobApplication> appsList = null;

		if (StringUtils.isEmpty(_jobId)) { // no param
			appsList = jobAppDao.findAll();
		} else { // with param
			appsList = jobAppDao.findByJobPostingId(_jobId);
		}

		if (appsList == null) {
			appsList = new ArrayList<>();
		}

		List<JobApplicationResource> appListResource = appResourceAssembler
				.toResources(appsList);
		return new ResponseEntity<List<JobApplicationResource>>(
				appListResource, HttpStatus.OK);
	}

	/**
	 * Get application by _appId<br/>
	 * 
	 * @param _appId
	 *            application Id
	 * @return a HATEOAS job application
	 * @throws JobApplicationNotFoundException
	 */
	@RequestMapping(value = "/{_appId}", method = RequestMethod.GET)
	public ResponseEntity<JobApplicationResource> jobAppById(
			@PathVariable(value = "_appId") String _appId)
			throws JobApplicationNotFoundException {
		JobApplication application = jobAppDao.findById(_appId);

		if (application == null) {
			throw new JobApplicationNotFoundException(
					"Application with _appid=" + _appId
							+ " not found in database.");
		}

		JobApplicationResource appResource = appResourceAssembler
				.toResource(application);

		return new ResponseEntity<JobApplicationResource>(appResource,
				HttpStatus.OK);
	}

	/*************************************************************************************
	 * POST methods
	 *************************************************************************************/
	/**
	 * 
	 * @param _jobId
	 * @param driverLicenseNumber
	 * @param fullName
	 * @param postCode
	 * @param textCoverLetter
	 * @param textBriefResume
	 * @return
	 * @throws JobAppMalformatException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> createApplication(
			@RequestParam(name = "_jobId", required = true) String _jobId,
			@RequestParam(name = "driverLicenseNumber", required = true) String driverLicenseNumber,
			@RequestParam(name = "fullName", required = true) String fullName,
			@RequestParam(name = "postCode", required = true) String postCode,
			@RequestParam(name = "textCoverLetter", required = false) String textCoverLetter,
			@RequestParam(name = "textBriefResume", required = false) String textBriefResume)
			throws JobAppMalformatException {

		// set application object
		JobApplication application = new JobApplication();

		// check job application format
		if (StringUtils.isEmpty(_jobId)) {
			throw new JobAppMalformatException(
					"Job application malformed: _jobId required");
		}
		
		JobPosting jobFound = jobDao.findByUid(_jobId);
		if (jobFound == null) {
			throw new JobAppMalformatException(
					"Job application malformed: job with _jobId not found in database");
		}

		if (StringUtils.isEmpty(driverLicenseNumber)) {
			throw new JobAppMalformatException(
					"Job application malformed: driverLicenseNumber required");
		}
		
		if (StringUtils.isEmpty(fullName)) {
			throw new JobAppMalformatException(
					"Job application malformed: fullName required");
		}
		
		if (StringUtils.isEmpty(postCode)) {
			throw new JobAppMalformatException(
					"Job application malformed: postCode required");
		}

		application.set_appId(UUID.randomUUID().toString());
		application.set_jobId(_jobId);
		application.setDriverLicenseNumber(driverLicenseNumber);
		application.setFullName(fullName);
		application.setPostCode(postCode);

		if (StringUtils.isEmpty(textCoverLetter)) {
			textCoverLetter = "";
		}
		application.setTextCoverLetter(textCoverLetter);

		if (StringUtils.isEmpty(textBriefResume)) {
			textBriefResume = "";
		}
		application.setTextBriefResume(textBriefResume);

		// after the create, status becomes submit but not processed yet
		application.setStatus(JobAppStatus.APP_SUBMITTED_NOT_PROCESSED);

		JobApplication createdApp = jobAppDao.create(application);

		JobApplicationResource createdAppResource = appResourceAssembler
				.toResource(createdApp);

		return new ResponseEntity<JobApplicationResource>(createdAppResource,
				HttpStatus.CREATED);
	}

	/*************************************************************************************
	 * PUT methods
	 *************************************************************************************/

	/**
	 * Update an application by its candidate<br/>
	 * 
	 * @param _appId
	 *            application ID
	 * @param _jobId
	 *            job ID this application is for
	 * @param driverLicenseNumber
	 *            candidate's driver license
	 * @param fullName
	 *            candidate's full name
	 * @param postCode
	 *            candidate's post code of address
	 * @param textCoverLetter
	 *            candidate's cover letter in text (not required)
	 * @param textBriefResume
	 *            candidate's brief resume in text (not required)
	 * @return a HATEOAS application after updated
	 * @throws JobApplicationNotFoundException
	 * @throws JobAppStatusCannotModifyException
	 * @throws JobAppMalformatException
	 */
	@RequestMapping(value = "/{_appId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> updateApplication(
			@PathVariable(value = "_appId") String _appId,
			@RequestParam(name = "_jobId", required = true) String _jobId,
			@RequestParam(name = "driverLicenseNumber", required = true) String driverLicenseNumber,
			@RequestParam(name = "fullName", required = true) String fullName,
			@RequestParam(name = "postCode", required = true) String postCode,
			@RequestParam(name = "textCoverLetter", required = false) String textCoverLetter,
			@RequestParam(name = "textBriefResume", required = false) String textBriefResume)
			throws JobApplicationNotFoundException,
			JobAppStatusCannotModifyException, JobAppMalformatException {
		// check if the application exist
		JobApplication application = jobAppDao.findById(_appId);

		if (application == null) {
			throw new JobApplicationNotFoundException(
					"Job application with _appId=" + _appId
							+ " not found in database.");
		}

		// check if the application can be updated in current status
		String status = application.getStatus();
		if (StringUtils.isEmpty(status)
				|| !status
						.equalsIgnoreCase(JobAppStatus.APP_SUBMITTED_NOT_PROCESSED)) {
			// only in submitted_but_not_processed status the application can be
			// updated
			throw new JobAppStatusCannotModifyException(
					"Job application with _appId=" + _appId
							+ " is in the status of: '" + status
							+ "', and cannot be modified currently.");
		}

		// check if the application has been modified, only if so do update
		// method, otherwise return the application directly
		if (StringUtils.isEmpty(_jobId)
				|| StringUtils.isEmpty(application.get_jobId())
				|| !_jobId.equals(application.get_jobId())) {
			// _jobId is not permitted to be updated
			throw new JobAppMalformatException(
					"Job application malformat on _jobId: get from database ["
							+ application.get_jobId() + "], parameter ["
							+ _jobId + "]");
		}

		List<Boolean> needUpdateInfoList = new ArrayList<>();

		// check required information (cannot be null or empty)
		String appDriverLicenseNumber = application.getDriverLicenseNumber();
		boolean needUpdateDriverLicense = false;
		if (checkApplicationRequiredInfoEmpty(appDriverLicenseNumber,
				driverLicenseNumber)) {
			throw new JobAppMalformatException(
					"Job application malformat on driver license number: get from database ["
							+ appDriverLicenseNumber + "], parameter ["
							+ driverLicenseNumber + "]");
		} else {
			needUpdateDriverLicense = checkNeedUpdate(appDriverLicenseNumber,
					driverLicenseNumber);
			needUpdateInfoList.add(needUpdateDriverLicense);
			if (needUpdateDriverLicense) {
				application.setDriverLicenseNumber(appDriverLicenseNumber);
			}
		}

		// TODO complete check of required info

		// TODO check not-required information

		return null;
	}

	/**
	 * Check if info of an application is need and can be updated<br/>
	 * 
	 * @param fromDatabase
	 * @param fromParam
	 * @return
	 */
	private boolean checkApplicationRequiredInfoEmpty(String fromDatabase,
			String fromParam) {
		return StringUtils.isEmpty(fromDatabase) // from database not empty
				|| StringUtils.isEmpty(fromParam); // from param not empty
	}

	/**
	 * Check if info of an application is needed to be updated<br/>
	 * 
	 * @param fromDatabase
	 * @param fromParam
	 * @return
	 */
	private boolean checkNeedUpdate(String fromDatabase, String fromParam) {
		return !fromDatabase.equals(fromParam);
	}
}
