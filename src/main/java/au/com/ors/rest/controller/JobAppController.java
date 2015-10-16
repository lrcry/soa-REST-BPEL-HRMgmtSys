package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 * Job application resource controller<br/>
 * 
 * @author hansmong
 *
 */
@Controller
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
			@RequestParam(name = "_jobId", required = false) String _jobId,
			@RequestParam(name = "status", required = false) String status) {
		List<JobApplication> appsList = new ArrayList<>();
		System.out.println("status=" + status);
		if (!StringUtils.isEmpty(_jobId) && !StringUtils.isEmpty(status)) { // by
																			// two
																			// factors
			System.out.println("getting according to two factors, jobappdao="
					+ (jobAppDao == null));
			List<JobApplication> appsByJobIdList = jobAppDao
					.findByJobPostingId(_jobId);
			if (appsByJobIdList != null) {

				for (JobApplication app : appsByJobIdList) {
					System.out.println("found appsbyjobid size: "
							+ appsByJobIdList.size());
					System.out.println("appstatus=" + app.getStatus());
					System.out.println("hooray!");
					if (app.getStatus().equals(status)) {
						appsList.add(app);
					}
				}
			}
		} else if (!StringUtils.isEmpty(_jobId)) {
			appsList = jobAppDao.findByJobPostingId(_jobId);
		} else if (!StringUtils.isEmpty(status)) {
			appsList = jobAppDao.findByStatus(status);
		} else {
			appsList = jobAppDao.findAll();
		}

		if (appsList == null) {
			appsList = new ArrayList<>();
		}

		System.out.println("appsList size: " + appsList.size());

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
	 * @param application
	 *            application JSON object
	 * @return a HATEOAS application object with HTTP status 201 created
	 * @throws JobAppMalformatException
	 * @throws TransformerException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> createApplication(
			@RequestBody JobApplication application)
			throws JobAppMalformatException, TransformerException {
		if (application == null) {
			throw new JobAppMalformatException(
					"Cannot create null job application");
		}

		if (StringUtils.isEmpty(application.get_jobId())) {
			throw new JobAppMalformatException(
					"Job application malformed: _jobId required");
		}

		JobPosting jobFound = jobDao.findByJid(application.get_jobId());
		if (jobFound == null) {
			throw new JobAppMalformatException(
					"Job application malformed: job with _jobId not found in database");
		}

		if (StringUtils.isEmpty(application.getDriverLicenseNumber())) {
			throw new JobAppMalformatException(
					"Job application malformed: driverLicenseNumber required");
		}

		if (StringUtils.isEmpty(application.getFullName())) {
			throw new JobAppMalformatException(
					"Job application malformed: fullName required");
		}

		if (StringUtils.isEmpty(application.getPostCode())) {
			throw new JobAppMalformatException(
					"Job application malformed: postCode required");
		}

		application.set_appId(UUID.randomUUID().toString());

		if (StringUtils.isEmpty(application.getTextCoverLetter())) {
			application.setTextCoverLetter("");
		}

		if (StringUtils.isEmpty(application.getTextBriefResume())) {
			application.setTextBriefResume("");
		}

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
	 * @param application
	 *            an application JSON object
	 * @return a HATEOAS application after updated
	 * @throws JobApplicationNotFoundException
	 * @throws JobAppStatusCannotModifyException
	 * @throws JobAppMalformatException
	 * @throws TransformerException
	 */
	@RequestMapping(value = "/{_appId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> updateApplication(
			@PathVariable(value = "_appId") String _appId,
			@RequestBody JobApplication application)
			throws JobApplicationNotFoundException,
			JobAppStatusCannotModifyException, JobAppMalformatException,
			TransformerException {
		// check if the application exist
		JobApplication existApp = jobAppDao.findById(_appId);

		if (existApp == null) {
			throw new JobApplicationNotFoundException(
					"Job application with _appId=" + _appId
							+ " not found in database.");
		}

		application.set_appId(_appId);
		application.setStatus(existApp.getStatus());

		// check if the application can be updated in current status
		String status = existApp.getStatus();
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
		if (StringUtils.isEmpty(existApp.get_jobId())
				|| StringUtils.isEmpty(application.get_jobId())
				|| !existApp.get_jobId().equals(application.get_jobId())) {
			// _jobId is not permitted to be updated
			throw new JobAppMalformatException(
					"Job application malformat on _jobId: request ["
							+ application.get_jobId() + "], database ["
							+ existApp.get_jobId() + "]");
		}

		List<Boolean> needUpdateInfoList = new ArrayList<>();

		// check required information (cannot be null or empty)

		// check driver license
		String appDriverLicenseNumber = application.getDriverLicenseNumber();
		String driverLicenseNumber = existApp.getDriverLicenseNumber();
		boolean needUpdateDriverLicense = false;
		if (checkApplicationRequiredInfoEmpty(appDriverLicenseNumber,
				driverLicenseNumber)) {
			throw new JobAppMalformatException(
					"Job application malformat on driver license number: request ["
							+ appDriverLicenseNumber + "], database ["
							+ driverLicenseNumber + "]");
		} else {
			needUpdateDriverLicense = checkNeedUpdate(appDriverLicenseNumber,
					driverLicenseNumber);
			needUpdateInfoList.add(needUpdateDriverLicense);
		}

		// check full name
		String appFullName = application.getFullName();
		String fullName = existApp.getFullName();
		boolean needUpdateFullName = false;
		if (checkApplicationRequiredInfoEmpty(appFullName, fullName)) {
			throw new JobAppMalformatException(
					"Job application malformat on full name: request ["
							+ appFullName + "], database [" + fullName + "]");
		} else {
			needUpdateFullName = checkNeedUpdate(appFullName, fullName);
			needUpdateInfoList.add(needUpdateFullName);
		}

		// check post code
		String appPostCode = application.getPostCode();
		String postCode = existApp.getPostCode();
		boolean needUpdatePostCode = false;
		if (checkApplicationRequiredInfoEmpty(appPostCode, postCode)) {
			throw new JobAppMalformatException(
					"Job application malformat on post code: get from database ["
							+ appPostCode + "], parameter [" + postCode + "]");
		} else {
			needUpdatePostCode = checkNeedUpdate(appPostCode, postCode);
			needUpdateInfoList.add(needUpdatePostCode);
		}

		// check not-required information

		// check text cover letter
		String appTextCoverLetter = application.getTextCoverLetter();
		String textCoverLetter = existApp.getTextCoverLetter();
		if (StringUtils.isEmpty(appTextCoverLetter)) {
			appTextCoverLetter = "";
			application.setTextCoverLetter("");
		}

		// String appTextCoverLetter = application.getTextCoverLetter();
		boolean needUpdateTextCoverLetter = checkNeedUpdate(appTextCoverLetter,
				textCoverLetter);
		needUpdateInfoList.add(needUpdateTextCoverLetter);

		// check text brief resume
		String appTextBriefResume = application.getTextBriefResume();
		String textBriefResume = existApp.getTextBriefResume();
		if (StringUtils.isEmpty(appTextBriefResume)) {
			appTextBriefResume = "";
			application.setTextBriefResume("");
		}

		boolean needUpdateTextBriefResume = checkNeedUpdate(appTextBriefResume,
				textBriefResume);
		needUpdateInfoList.add(needUpdateTextBriefResume);

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
	 * @throws TransformerException
	 */
	@RequestMapping(value = "/status/{_appId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> updateJobStatus(
			@PathVariable(value = "_appId") String _appId,
			@RequestParam(name = "status") String status)
			throws JobApplicationNotFoundException, JobAppMalformatException,
			TransformerException {
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
	 * @throws TransformerException
	 */
	@RequestMapping(value = "/{_appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> archiveApplication(
			@PathVariable(value = "_appId") String _appId)
			throws JobApplicationNotFoundException,
			JobAppStatusCannotModifyException, TransformerException {
		// check application existence
		JobApplication app = jobAppDao.findById(_appId);
		if (app == null) {
			throw new JobApplicationNotFoundException(
					"Job application with _appId=" + _appId
							+ " not found in database, you cannot archive it");
		}

		// check application status
		String appStatus = app.getStatus();
		if (!appStatus.equals(JobAppStatus.APP_INTERVIEW_PASSED.name())
				&& !appStatus.equals(JobAppStatus.APP_INTERVIEW_FAILED.name())
				&& !appStatus.equals(JobAppStatus.APP_REJECTED_BY_CANDIDATE
						.name())
				&& !appStatus.equals(JobAppStatus.APP_NOT_SHORTLISTED.name())
				&& !appStatus.equals(JobAppStatus.APP_CANCELLED.name())) {
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
		if (e instanceof DAOException || e instanceof TransformerException) {
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
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		} else {
			error.setErrCode(RESTErrorCode.GENERAL_SERVER_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}

	/*************************************************************************************
	 * General private methods used in controller
	 *************************************************************************************/

	/**
	 * Check if info of an application is need and can be updated<br/>
	 * 
	 * @param fromRequest
	 * @param fromDatabase
	 * @return
	 */
	private boolean checkApplicationRequiredInfoEmpty(String fromRequest,
			String fromDatabase) {
		return StringUtils.isEmpty(fromDatabase) // from database not empty
				|| StringUtils.isEmpty(fromRequest); // from param not empty
	}

	/**
	 * Check if info of an application is needed to be updated<br/>
	 * 
	 * @param fromRequest
	 * @param fromDatabase
	 * @return
	 */
	private boolean checkNeedUpdate(String fromRequest, String fromDatabase) {
		return !fromDatabase.equals(fromRequest);
	}
}
