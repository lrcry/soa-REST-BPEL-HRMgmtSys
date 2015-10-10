package au.com.ors.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.controller.JobPostingController;
import au.com.ors.rest.controller.UserController;
import au.com.ors.rest.resource.JobPostingResource;
import au.com.ors.rest.resource.UserResource;

@Component
public class JobPostingResourceAssembler extends ResourceAssemblerSupport<JobPosting, JobPostingResource>{
	public JobPostingResourceAssembler() {
		super(JobPostingController.class, JobPostingResource.class);
	}
	
	public JobPostingResourceAssembler(Class<?> controllerClass,
			Class<JobPostingResource> resourceType) {
		super(controllerClass, resourceType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public JobPostingResource toResource(JobPosting jobPosting) {
		JobPostingResource jobPostingResource = instantiateResource(jobPosting);
		jobPostingResource.jobPosting = jobPosting;
		jobPostingResource.add(linkTo(JobPostingController.class).slash(jobPosting.get_jobId()).withSelfRel());
		return jobPostingResource;
	}
	
}
