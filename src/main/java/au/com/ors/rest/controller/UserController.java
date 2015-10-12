package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;

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

import au.com.ors.rest.bean.User;
import au.com.ors.rest.bean.RESTError;
import au.com.ors.rest.commons.DAOErrorCode;
import au.com.ors.rest.commons.RESTErrorCode;
import au.com.ors.rest.dao.UserDAO;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.UserNotFoundException;
import au.com.ors.rest.resource.UserResource;
import au.com.ors.rest.resource.assembler.UserResourceAssembler;

/**
 * Controller of user resources<br/>
 * 
 * @author hansmong
 *
 */
@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	UserDAO userDao;

	@Autowired
	UserResourceAssembler userResourceAssembler;

	/**
	 * Controller method mapping to URL /users[?hireTeam=some_team]<br/>
	 * If the hireTeam parameter is given, get all the users belonging to the
	 * hire team.<br/>
	 * If not, get all the users in the database<br/>
	 * 
	 * @param hireTeam
	 *            hire team parameter
	 * @return users list
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<UserResource>> users(
			@RequestParam(name = "hireTeam", required = false) String hireTeam) {
		List<User> userList = null;
		if (StringUtils.isEmpty(hireTeam)) { // if no param given, get all users
			userList = userDao.findAll();
		} else { // given param, get the users match the criterion
			userList = userDao.findBySearchDepartment(hireTeam);
		}

		if (userList == null) {
			userList = new ArrayList<>();
		}
		List<UserResource> userResourceList = userResourceAssembler
				.toResources(userList);
		return new ResponseEntity<List<UserResource>>(userResourceList,
				HttpStatus.OK);
	}

	/**
	 * Controller method mapping to URL /users/{_uid}
	 * 
	 * @param _uid
	 *            user ID
	 * @return a HATEOAS user
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "/{_uid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<UserResource> userById(
			@PathVariable(value = "_uid") String _uid)
			throws UserNotFoundException {
		User userById = userDao.findByUid(_uid);

		if (userById == null) {
			throw new UserNotFoundException("User with _uid=" + _uid
					+ " not found in database.");
		}

		UserResource userResource = userResourceAssembler.toResource(userById);
		return new ResponseEntity<UserResource>(userResource, HttpStatus.OK);
	}

	/**
	 * Handle controller exceptions<br/>
	 * 
	 * @param e
	 *            exceptions
	 * @return error response
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;

		RESTError error = new RESTError();
		if (e instanceof DAOException) {
			error.setErrCode(DAOErrorCode.DATA_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (e instanceof UserNotFoundException) {
			error.setErrCode(RESTErrorCode.USER_NOT_FOUND);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.NOT_FOUND);
		} else {
			error.setErrCode("UNKNOWN_SERVER_ERROR");
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}
}
