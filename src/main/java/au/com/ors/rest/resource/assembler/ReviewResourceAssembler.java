package au.com.ors.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import au.com.ors.rest.bean.Review;
import au.com.ors.rest.controller.ReviewController;
import au.com.ors.rest.resource.ReviewResource;

@Component
public class ReviewResourceAssembler extends ResourceAssemblerSupport<Review, ReviewResource>{
	public ReviewResourceAssembler() {
		super(ReviewController.class, ReviewResource.class);
	}
	
	public ReviewResourceAssembler(Class<?> controllerClass,
			Class<ReviewResource> resourceType) {
		super(controllerClass, resourceType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ReviewResource toResource(Review review) {
		// TODO Auto-generated method stub
		ReviewResource reviewResource = instantiateResource(review);
		reviewResource.review = review;
		reviewResource.add(linkTo(ReviewController.class).slash(review.get_reviewId()).withSelfRel());
		return reviewResource;
	}


	
}
