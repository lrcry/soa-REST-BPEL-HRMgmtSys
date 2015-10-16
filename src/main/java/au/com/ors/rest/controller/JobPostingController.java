package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.bean.RESTError;
import au.com.ors.rest.commons.DAOErrorCode;
import au.com.ors.rest.commons.JobStatus;
import au.com.ors.rest.commons.RESTErrorCode;
import au.com.ors.rest.dao.JobPostingDAO;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.JobAppMalformatException;
import au.com.ors.rest.exceptions.JobAppStatusCannotModifyException;
import au.com.ors.rest.exceptions.JobApplicationNotFoundException;
import au.com.ors.rest.exceptions.JobPostingMalformatException;
import au.com.ors.rest.exceptions.JobPostingNotFoundException;
import au.com.ors.rest.exceptions.JobPostingStatusCannotModifyException;
import au.com.ors.rest.resource.JobPostingResource;
import au.com.ors.rest.resource.assembler.JobPostingResourceAssembler;

@Controller
@RequestMapping(value = "/jobPostings")
public class JobPostingController {
	@Autowired
	JobPostingDAO jobPostingDAO;
	
	@Autowired
	JobPostingResourceAssembler jobPostingResourceAssembler;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JobPostingResource> createJobPosting(
			@RequestBody JobPosting jobPosting) throws JobPostingMalformatException {
		
		if (jobPosting == null) {
			throw new JobPostingMalformatException("Cannot create null job posting");
		}
		if (StringUtils.isEmpty(jobPosting.get_uId())) {
			throw new JobPostingMalformatException("Job posting malformed: uid required");
		}
		if (StringUtils.isEmpty(jobPosting.getTitle())) {
			throw new JobPostingMalformatException("Job posting malformed: title required");
		}
		if (StringUtils.isEmpty(jobPosting.getClosingTime())) {
			throw new JobPostingMalformatException("Job posting malformed: closingTime required");
		}
		if (StringUtils.isEmpty(jobPosting.getSalaryRate())) {
			throw new JobPostingMalformatException("Job posting malformed: salaryRate required");
		}
		if (StringUtils.isEmpty(jobPosting.getPositionType())) {
			throw new JobPostingMalformatException("Job posting malformed: positionType required");
		}
		if (StringUtils.isEmpty(jobPosting.getLocation())) {
			throw new JobPostingMalformatException("Job posting malformed: location required");
		}
		if (StringUtils.isEmpty(jobPosting.getDetails())) {
			throw new JobPostingMalformatException("Job posting malformed: details required");
		}
		jobPosting.setStatus(JobStatus.CREATED);
		String _jobId = UUID.randomUUID().toString();
		jobPosting.set_jobId(_jobId);
		JobPosting jobPostingResult = new JobPosting(null, null, null, null, null, null, null, null, null);
		try {
			jobPostingResult = jobPostingDAO.create(jobPosting);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingResult);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(value = "{_jobId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobPostingResource> updateJobPosting(
			@PathVariable(value = "_jobId") String _jobId,
			@RequestBody JobPosting jobPosting) throws JobPostingNotFoundException, TransformerException, JobPostingStatusCannotModifyException {
		JobPosting existJP = jobPostingDAO.findByJid(_jobId);
		if (existJP == null) {
			throw new JobPostingNotFoundException("Job Posting with _jobId = " + _jobId + " not found in database.");
		}
		jobPosting.set_jobId(_jobId);
		String status = existJP.getStatus();
		if (!status.equals(JobStatus.CREATED)) {
			throw new JobPostingStatusCannotModifyException("Cannot modify the JobPosting on status = " + status + ".");
		}
		jobPosting.setStatus(status);
		
		JobPosting jobPostingResult = jobPostingDAO.update(jobPosting);
		if (jobPostingResult == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + jobPosting.get_jobId() + " not found in database.");
		}
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingResult);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/status/{_jobId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobPostingResource> updateJobPostingStatus(
			@PathVariable(value = "_jobId") String _jobId,
			@RequestParam(name= "status") String status) throws JobPostingNotFoundException, JobPostingMalformatException, TransformerException {
		JobPosting jobPosting = jobPostingDAO.findByJid(_jobId);
		if (jobPosting == null) {
			throw new JobPostingNotFoundException("Job Posting with _jobId = " + _jobId + " not found in database.");
		}
		if (StringUtils.isEmpty(status)) {
			throw new JobPostingMalformatException("Job Posting update status: required input status");
		}
		if (!status.equals(jobPosting.getStatus())) {
			jobPosting.setStatus(status);
			jobPosting = jobPostingDAO.update(jobPosting);
		}
		
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPosting);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<JobPostingResource>>  getJobPostings(
			@RequestParam(name = "_jobId" , required = false) String _jobId,
			@RequestParam(name = "_uId" , required = false) String _uId,
			@RequestParam(name = "title" , required = false) String title,
			@RequestParam(name = "closingTime" , required = false) String closingTime,
			@RequestParam(name = "salaryRate" , required = false) String salaryRate,
			@RequestParam(name = "positionType" , required = false) String positionType,
			@RequestParam(name = "location" , required = false) String location,
			@RequestParam(name = "details" , required = false) String details,
			@RequestParam(name = "status" , required = false) String status) {
		List<JobPosting> jobPostingList = jobPostingDAO.findAll();
		List<JobPosting> jobPostingListResult = new ArrayList<JobPosting>();
		if (jobPostingList == null) {
			jobPostingList = new ArrayList<JobPosting>();
		}
		
		for (JobPosting jobPosting : jobPostingList) {
			if (!StringUtils.isEmpty(_jobId) && StringUtils.isEmpty(jobPosting.get_jobId())) {
				continue;
			} else if (!StringUtils.isEmpty(_uId) && StringUtils.isEmpty(jobPosting.get_uId())) {
				continue;
			} else if (!StringUtils.isEmpty(title) && StringUtils.isEmpty(jobPosting.getTitle())) {
				continue;
			} else if (!StringUtils.isEmpty(closingTime) && StringUtils.isEmpty(jobPosting.getClosingTime())) {
				continue;
			} else if (!StringUtils.isEmpty(salaryRate) && StringUtils.isEmpty(jobPosting.getSalaryRate())) {
				continue;
			} else if (!StringUtils.isEmpty(positionType) && StringUtils.isEmpty(jobPosting.getPositionType())) {
				continue;
			} else if (!StringUtils.isEmpty(location) && StringUtils.isEmpty(jobPosting.getLocation())) {
				continue;
			} else if (!StringUtils.isEmpty(details) && StringUtils.isEmpty(jobPosting.getDetails())) {
				continue;
			} else if (!StringUtils.isEmpty(status) && StringUtils.isEmpty(jobPosting.getStatus())) {
				continue;
			} else if (!StringUtils.isEmpty(_jobId) && !StringUtils.isEmpty(jobPosting.get_jobId()) && !jobPosting.get_jobId().equalsIgnoreCase(_jobId)) {
				continue;
			} else if (!StringUtils.isEmpty(_uId) && !StringUtils.isEmpty(jobPosting.get_uId()) && !jobPosting.get_uId().equalsIgnoreCase(_uId)) {
				continue;
			} else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(jobPosting.getTitle()) && !jobPosting.getTitle().equalsIgnoreCase(title)) {
				continue;
			} else if (!StringUtils.isEmpty(closingTime) && !StringUtils.isEmpty(jobPosting.getClosingTime()) && !jobPosting.getClosingTime().equalsIgnoreCase(closingTime)) {
				continue;
			} else if (!StringUtils.isEmpty(salaryRate) && !StringUtils.isEmpty(jobPosting.getSalaryRate()) && !jobPosting.getSalaryRate().equalsIgnoreCase(salaryRate)) {
				continue;
			} else if (!StringUtils.isEmpty(positionType) && !StringUtils.isEmpty(jobPosting.getPositionType()) && !jobPosting.getPositionType().equalsIgnoreCase(positionType)) {
				continue;
			} else if (!StringUtils.isEmpty(location) && !StringUtils.isEmpty(jobPosting.getLocation()) && !jobPosting.getLocation().equalsIgnoreCase(location)) {
				continue;
			} else if (!StringUtils.isEmpty(details) && !StringUtils.isEmpty(jobPosting.getDetails()) && !jobPosting.getDetails().equalsIgnoreCase(details)) {
				continue;
			} else if (!StringUtils.isEmpty(status) && !StringUtils.isEmpty(jobPosting.getStatus()) && !jobPosting.getStatus().equalsIgnoreCase(status)) {
				continue;
			}
			jobPostingListResult.add(jobPosting);
		}
		List<JobPostingResource> jobPostingResourceList = jobPostingResourceAssembler.toResources(jobPostingListResult);
		return new ResponseEntity<List<JobPostingResource>>(jobPostingResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_jobId}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<JobPostingResource> getJobPostingById(@PathVariable(value = "_jobId") String _jobId) throws JobPostingNotFoundException {
		JobPosting jobPostingById = jobPostingDAO.findByJid(_jobId);
		if (jobPostingById == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + _jobId + " not found in database.");
		}
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingById);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	 
	@RequestMapping(value = "/{_jobId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<JobPostingResource> deleteJobPosting(@PathVariable(value = "_jobId") String _jobId) throws JobPostingNotFoundException, JobAppStatusCannotModifyException, TransformerException {
		JobPosting jobPostingById = jobPostingDAO.findByJid(_jobId);
		if (jobPostingById == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + _jobId + " not found in database.");
		}
		String status = jobPostingById.getStatus();
		if (status.equals(JobStatus.ARCHIVED)) {
			throw new JobAppStatusCannotModifyException(
					"Job application status is already ARCHIVED");
		}
		jobPostingById.setStatus(JobStatus.ARCHIVED);
		jobPostingById = jobPostingDAO.update(jobPostingById);
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingById);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/_uId/{_uId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<JobPostingResource>> getJobPostingsByUid(
			@PathVariable(value = "_uId") String _uId) {
		List<JobPosting> jobPostingList = jobPostingDAO.findByUid(_uId);
		if (jobPostingList == null) {
			jobPostingList = new ArrayList<JobPosting>();
		}
		List<JobPostingResource> jobPostingResourceList = jobPostingResourceAssembler.toResources(jobPostingList);
		return new ResponseEntity<List<JobPostingResource>>(jobPostingResourceList, HttpStatus.OK);
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;
		
		// TODO handle exceptions of this controller
		RESTError error = new RESTError();
		if (e instanceof DAOException || e instanceof TransformerException) {
			error.setErrCode(DAOErrorCode.DATA_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (e instanceof JobPostingNotFoundException) {
			error.setErrCode(RESTErrorCode.JOB_POSTING_NOT_FOUND);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.NOT_FOUND);
		} else if (e instanceof JobPostingMalformatException
				|| e instanceof JobPostingStatusCannotModifyException) {
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
}
