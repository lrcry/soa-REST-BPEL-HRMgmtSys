package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
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
import au.com.ors.rest.bean.UserError;
import au.com.ors.rest.commons.DAOErrorCode;
import au.com.ors.rest.commons.UserErrorCode;
import au.com.ors.rest.dao.UserDAO;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.UserNotFoundException;
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
		List<User> userList = userDao.findAll();
		if (userList == null) {
			userList = new ArrayList<>();
		}
		List<UserResource> userResourceList = userResourceAssembler.toResources(userList);
		return new ResponseEntity<List<UserResource>>(userResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_uid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<ResourceSupport> usersById(@PathVariable(value = "_uid") String _uid) throws UserNotFoundException {
		User userById = userDao.findByUid(_uid);
		
		if (userById == null) {
			throw new UserNotFoundException("User with _uid=" + _uid + " not found in database.");
		}
		
		UserResource userResource = userResourceAssembler.toResource(userById);
		return new ResponseEntity<ResourceSupport>(userResource, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;
		
		UserError error = new UserError();
		if (e instanceof DAOException) {
			error.setErrCode(DAOErrorCode.DATA_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (e instanceof UserNotFoundException) {
			error.setErrCode(UserErrorCode.USER_NOT_FOUND);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.NOT_FOUND);
		} else {
			error.setErrCode("UNKNOWN_SERVER_ERROR");
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return responseEntity;
	}
}
