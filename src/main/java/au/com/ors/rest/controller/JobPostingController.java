package au.com.ors.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.dao.JobPostingDAO;
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
	public ResponseEntity<JobPostingResource> createJobPosting() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<JobPostingResource>>  jobPostings() {
		List<JobPosting> jobPostingList = jobPostingDAO.findAll();
		List<JobPostingResource> jobPostingResourceList = jobPostingResourceAssembler.toResources(jobPostingList);
		return new ResponseEntity<List<JobPostingResource>>(jobPostingResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_jobId}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<JobPostingResource> jobPostingById(@PathVariable(value = "_jobId") String _jobId) {
		JobPosting jobPostingById = jobPostingDAO.findByUid(_jobId);
		JobPostingResource jobPostingResource = jobPostingResourceAssembler.toResource(jobPostingById);
		return new ResponseEntity<JobPostingResource>(jobPostingResource, HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;
		
		// TODO handle exceptions of this controller
		
		return responseEntity;
	}
}
