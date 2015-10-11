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
import au.com.ors.rest.commons.JobAppStatus;
import au.com.ors.rest.dao.JobAppDAO;
import au.com.ors.rest.exceptions.JobApplicationNotFoundException;
import au.com.ors.rest.resource.JobApplicationResource;
import au.com.ors.rest.resource.assembler.JobApplicationResourceAssembler;

@RequestMapping("/jobapplications")
public class JobAppController {
	@Autowired
	JobAppDAO jobAppDao;

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
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> createApplication(
			@RequestParam(name = "_jobId", required = true) String _jobId,
			@RequestParam(name = "driverLicenseNumber", required = true) String driverLicenseNumber,
			@RequestParam(name = "fullName", required = true) String fullName,
			@RequestParam(name = "postCode", required = true) String postCode,
			@RequestParam(name = "textCoverLetter", required = false) String textCoverLetter,
			@RequestParam(name = "textBriefResume", required = false) String textBriefResume) {

		// set application object
		JobApplication application = new JobApplication();

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
	@RequestMapping(value = "/{_appId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobApplicationResource> updateApplication(
			@PathVariable(value = "_appId") String _appId,
			@RequestParam(name = "_jobId", required = true) String _jobId,
			@RequestParam(name = "driverLicenseNumber", required = true) String driverLicenseNumber,
			@RequestParam(name = "fullName", required = true) String fullName,
			@RequestParam(name = "postCode", required = true) String postCode,
			@RequestParam(name = "textCoverLetter", required = false) String textCoverLetter,
			@RequestParam(name = "textBriefResume", required = false) String textBriefResume) {
		// check if the application exist

		// check if the application can be updated in current status

		// check if the application has been modified, only if so do update
		// method, otherwise return the application directly

		return null;
	}
}
