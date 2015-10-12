package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import au.com.ors.rest.bean.RESTError;
import au.com.ors.rest.commons.DAOErrorCode;
import au.com.ors.rest.commons.JobAppStatus;
import au.com.ors.rest.commons.RESTErrorCode;
import au.com.ors.rest.dao.JobAppDAO;
import au.com.ors.rest.dao.JobPostingDAO;
import au.com.ors.rest.exceptions.DAOException;
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
	 * Create a new application<br/>
	 * 
	 * @param _jobId
	 *            job ID which the application for
	 * @param driverLicenseNumber
	 *            candidate's driver license number
	 * @param fullName
	 *            candidate's full name
	 * @param postCode
	 *            candidate's post code
	 * @param textCoverLetter
	 *            candidate's cv in text
	 * @param textBriefResume
	 *            candidate's resume in text
	 * @return a HATEOAS application object with HTTP status 201 created
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
		application.setStatus(JobAppStatus.APP_SUBMITTED_NOT_PROCESSED.name());

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
						.equalsIgnoreCase(JobAppStatus.APP_SUBMITTED_NOT_PROCESSED
								.name())) {
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

		// check driver license
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
				application.setDriverLicenseNumber(driverLicenseNumber);
			}
		}

		// check full name
		String appFullName = application.getFullName();
		boolean needUpdateFullName = false;
		if (checkApplicationRequiredInfoEmpty(appFullName, fullName)) {
			throw new JobAppMalformatException(
					"Job application malformat on full name: get from database ["
							+ appFullName + "], parameter [" + fullName + "]");
		} else {
			needUpdateFullName = checkNeedUpdate(appFullName, fullName);
			needUpdateInfoList.add(needUpdateFullName);
			if (needUpdateFullName) {
				application.setFullName(fullName);
			}
		}

		// check post code
		String appPostCode = application.getPostCode();
		boolean needUpdatePostCode = false;
		if (checkApplicationRequiredInfoEmpty(appPostCode, postCode)) {
			throw new JobAppMalformatException(
					"Job application malformat on post code: get from database ["
							+ appPostCode + "], parameter [" + postCode + "]");
		} else {
			needUpdatePostCode = checkNeedUpdate(appPostCode, postCode);
			needUpdateInfoList.add(needUpdatePostCode);
			if (needUpdatePostCode) {
				application.setPostCode(postCode);
			}
		}

		// check not-required information

		// check text cover letter
		if (StringUtils.isEmpty(textCoverLetter)) {
			textCoverLetter = "";
		}

		String appTextCoverLetter = application.getTextCoverLetter();
		boolean needUpdateTextCoverLetter = checkNeedUpdate(appTextCoverLetter,
				textCoverLetter);
		needUpdateInfoList.add(needUpdateTextCoverLetter);
		if (needUpdateTextCoverLetter) {
			application.setTextCoverLetter(textCoverLetter);
		}

		// check text brief resume
		if (StringUtils.isEmpty(textBriefResume)) {
			textBriefResume = "";
		}

		String appTextBriefResume = application.getTextBriefResume();
		boolean needUpdateTextBriefResume = checkNeedUpdate(appTextBriefResume,
				textBriefResume);
		needUpdateInfoList.add(needUpdateTextBriefResume);
		if (needUpdateTextBriefResume) {
			application.setTextBriefResume(textBriefResume);
		}

		// check need update flag to decide whether do writing update into XML
		boolean needWriteUpdate = false;
		for (boolean need : needUpdateInfoList) {
			if (need) {
				needWriteUpdate = true;
				break;
			}
		}

		if (needWriteUpdate) {
			jobAppDao.update(application);
		}

		JobApplicationResource updatedAppResource = appResourceAssembler
				.toResource(application);

		return new ResponseEntity<JobApplicationResource>(updatedAppResource,
				HttpStatus.OK);
	}

	/**
	 * Update application status<br/>
	 * 
	 * @param _appId
	 *            application ID
	 * @param status
	 *            status to be updated
	 * @return a HATEOAS application object
	 * @throws JobApplicationNotFoundException
	 * @throws JobAppMalformatException
	 */
	@RequestMapping(value = "/status/{_appId}")
	@ResponseBody
	public ResponseEntity<JobApplicationResource> updateJobStatus(
			@PathVariable(value = "_appId") String _appId,
			@RequestParam(name = "status") String status)
			throws JobApplicationNotFoundException, JobAppMalformatException {
		// check application existence
		JobApplication app = jobAppDao.findById(_appId);
		if (app == null) {
			throw new JobApplicationNotFoundException(
					"Job application with _appId="
							+ _appId
							+ " not found in database, you cannot update its status");
		}

		// check input status
		if (StringUtils.isEmpty(status)) {
			throw new JobAppMalformatException(
					"Job application update status: required input status");
		}

		if (!JobAppStatus.contains(status)) {
			throw new JobAppMalformatException(
					"Job application update status invalid");
		}

		if (!status.equals(app.getStatus())) { // need update
			app.setStatus(status);
			jobAppDao.update(app);
		}

		JobApplicationResource updatedAppResource = appResourceAssembler
				.toResource(app);

		return new ResponseEntity<JobApplicationResource>(updatedAppResource,
				HttpStatus.OK);
	}

	/*************************************************************************************
	 * DELETE methods
	 *************************************************************************************/
	/**
	 * Candidate archives its application<br/>
	 * 
	 * @param _appId
	 *            application ID
	 * @return a HATEOAS application object
	 * @throws JobApplicationNotFoundException
	 * @throws JobAppStatusCannotModifyException
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> archiveApplication(
			@RequestParam(name = "_appId") String _appId)
			throws JobApplicationNotFoundException,
			JobAppStatusCannotModifyException {
		// check application existence
		JobApplication app = jobAppDao.findById(_appId);
		if (app == null) {
			throw new JobApplicationNotFoundException(
					"Job application with _appId=" + _appId
							+ " not found in database, you cannot archive it");
		}

		// check application status
		String appStatus = app.getStatus();
		if (appStatus.equals(JobAppStatus.APP_INTERVIEW_PASSED.name())
				|| appStatus.equals(JobAppStatus.APP_INTERVIEW_FAILED.name())
				|| appStatus.equals(JobAppStatus.APP_REJECTED_BY_CANDIDATE
						.name())
				|| appStatus.equals(JobAppStatus.APP_NOT_SHORTLISTED.name())
				|| appStatus.equals(JobAppStatus.APP_CANCELLED)) {
			throw new JobAppStatusCannotModifyException(
					"Job application status is " + appStatus
							+ ", cannot be archived yet");
		}

		app.setStatus(JobAppStatus.APP_ARCHIVED.name());
		jobAppDao.update(app);

		JobApplicationResource updatedAppResource = appResourceAssembler
				.toResource(app);

		return new ResponseEntity<JobApplicationResource>(updatedAppResource,
				HttpStatus.OK);
	}

	/*************************************************************************************
	 * Exception handler
	 *************************************************************************************/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;

		RESTError error = new RESTError();
		if (e instanceof DAOException) {
			error.setErrCode(DAOErrorCode.DATA_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (e instanceof JobApplicationNotFoundException) {
			error.setErrCode(RESTErrorCode.JOB_APP_NOT_FOUND);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.NOT_FOUND);
		} else if (e instanceof JobAppMalformatException
				|| e instanceof JobAppStatusCannotModifyException) {
			error.setErrCode(RESTErrorCode.CLIENT_BAD_REQUEST);
			error.setErrCode(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;
	}

	/*************************************************************************************
	 * General private methods used in controller
	 *************************************************************************************/

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
