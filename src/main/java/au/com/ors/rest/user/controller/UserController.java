package au.com.ors.rest.user.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.user.bean.User;
import au.com.ors.rest.user.dao.UserDAO;

@Controller
public class UserController {
	@Autowired
	UserDAO userDao;
	
	@RequestMapping(value = "/users")
	@ResponseBody
	public HttpEntity<User> users(@RequestParam(name = "_uid", required = false) String _uid) {
		User userById = userDao.findByUid(_uid);
		userById.add(linkTo(methodOn(UserController.class).users(_uid)).withSelfRel());
		return new ResponseEntity<User>(userById, HttpStatus.OK);
	}
}
