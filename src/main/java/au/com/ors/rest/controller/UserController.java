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

import au.com.ors.rest.bean.User;
import au.com.ors.rest.dao.UserDAO;
import au.com.ors.rest.resource.UserResource;
import au.com.ors.rest.resource.assembler.UserResourceAssembler;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	UserDAO userDao;
	
	@Autowired
	UserResourceAssembler userResourceAssembler;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<UserResource>> users() {
		// TODO DAO and test if DAO and this work
		List<User> userList = userDao.findAll();
		List<UserResource> userResourceList = userResourceAssembler.toResources(userList);
		return new ResponseEntity<List<UserResource>>(userResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_uid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<UserResource> usersById(@PathVariable(value = "_uid") String _uid) {
		User userById = userDao.findByUid(_uid);
		UserResource userResource = userResourceAssembler.toResource(userById);
		return new ResponseEntity<UserResource>(userResource, HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;
		
		// TODO handle exceptions of this controller
		
		return responseEntity;
	}
}
