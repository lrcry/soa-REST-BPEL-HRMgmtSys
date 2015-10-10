package au.com.ors.rest.user.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.user.bean.User;

@Controller
public class UserController {
	
	@RequestMapping(value = "/users")
	@ResponseBody
	public HttpEntity<User> user() {
		User user = new User("001", "001", "shortKey", "lastName", "firstName", "role", "dept");
		user.add(linkTo(methodOn(UserController.class).user()).withSelfRel());
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
}
