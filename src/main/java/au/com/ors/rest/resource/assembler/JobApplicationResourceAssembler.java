package au.com.ors.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import au.com.ors.rest.bean.JobApplication;
import au.com.ors.rest.controller.JobAppController;
import au.com.ors.rest.resource.JobApplicationResource;

@Component
public class JobApplicationResourceAssembler extends
		ResourceAssemblerSupport<JobApplication, JobApplicationResource> {
	public JobApplicationResourceAssembler() {
		super(JobAppController.class, JobApplicationResource.class);
	}

	public JobApplicationResourceAssembler(Class<?> controllerClass,
			Class<JobApplicationResource> resourceType) {
		super(controllerClass, resourceType);
	}

	@Override
	public JobApplicationResource toResource(JobApplication application) {
		JobApplicationResource applicationResource = instantiateResource(application);
		applicationResource.application = application;
		applicationResource.add(linkTo(JobAppController.class).slash(
				application.get_appId()).withSelfRel());

		return applicationResource;
	}
}
