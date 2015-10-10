package au.com.ors.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import au.com.ors.rest.bean.User;
import au.com.ors.rest.controller.UserController;
import au.com.ors.rest.resource.UserResource;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {
	public UserResourceAssembler() {
		super(UserController.class, UserResource.class);
	}

	public UserResourceAssembler(Class<?> controllerClass,
			Class<UserResource> resourceType) {
		super(controllerClass, resourceType);
	}

	@Override
	public UserResource toResource(User user) {
		UserResource userResource = instantiateResource(user);
		userResource.user = user;
		userResource.add(linkTo(UserController.class).slash(user.get_uid()).withSelfRel());
		return userResource;
	}

}
