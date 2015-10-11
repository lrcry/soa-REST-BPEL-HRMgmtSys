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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.dao.JobPostingDAO;
import au.com.ors.rest.exceptions.JobPostingNotFoundException;
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
			@RequestParam(name = "closingTime") String closingTime,
			@RequestParam(name = "salaryRate") String salaryRate,
			@RequestParam(name = "positionType") String positionType,
			@RequestParam(name = "location") String location,
			@RequestParam(name = "details") String details,
			@RequestParam(name = "status") String status
			) {
		String _jobId = UUID.randomUUID().toString();
		JobPosting jobPosting = new JobPosting(_jobId, closingTime, salaryRate, positionType, location, details, status);
		JobPosting jobPostingResult = new JobPosting(null, null, null, null, null, null, null);
		try {
			jobPostingResult = jobPostingDAO.create(jobPosting);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			System.out.println("exception");
		}
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingResult);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<JobPostingResource> updateJobPosting(
			@RequestParam(name = "_jobId") String _jobId,
			@RequestParam(name = "closingTime") String closingTime,
			@RequestParam(name = "salaryRate") String salaryRate,
			@RequestParam(name = "positionType") String positionType,
			@RequestParam(name = "location") String location,
			@RequestParam(name = "details") String details,
			@RequestParam(name = "status") String status) throws JobPostingNotFoundException, TransformerException {
		JobPosting jobPosting = new JobPosting(_jobId, closingTime, salaryRate, positionType, location, details, status);
		JobPosting jobPostingResult = jobPostingDAO.update(jobPosting);
		if (jobPostingResult == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + jobPosting.get_jobId() + " not found in database.");
		}
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingResult);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<JobPostingResource>>  getJobPostings(
			@RequestParam(name = "_jobId" , required = false) String _jobId,
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
		System.out.println(jobPostingList);
		System.out.println(jobPostingListResult);
		List<JobPostingResource> jobPostingResourceList = jobPostingResourceAssembler.toResources(jobPostingListResult);
		return new ResponseEntity<List<JobPostingResource>>(jobPostingResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_jobId}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<JobPostingResource> getJobPostingById(@PathVariable(value = "_jobId") String _jobId) throws JobPostingNotFoundException {
		JobPosting jobPostingById = jobPostingDAO.findByUid(_jobId);
		if (jobPostingById == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + _jobId + " not found in database.");
		}
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingById);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	 
	@RequestMapping(value = "/{_jobId}", method = RequestMethod.DELETE)
	@ResponseBody
	public HttpEntity<JobPosting> deleteJobPosting(@PathVariable(value = "_jobId") String _jobId) throws JobPostingNotFoundException {
		JobPosting jobPostingById = jobPostingDAO.delete(_jobId);
		if (jobPostingById == null) {
			throw new JobPostingNotFoundException("jobPosting with _jobid=" + _jobId + " not found in database.");
		}
		return new ResponseEntity<JobPosting>(jobPostingById, HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;
		
		// TODO handle exceptions of this controller
		
		return responseEntity;
	}
}
