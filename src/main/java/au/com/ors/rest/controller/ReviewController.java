package au.com.ors.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.bean.RESTError;
import au.com.ors.rest.bean.Review;
import au.com.ors.rest.commons.DAOErrorCode;
import au.com.ors.rest.commons.RESTErrorCode;
import au.com.ors.rest.dao.ReviewDAO;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.JobAppMalformatException;
import au.com.ors.rest.exceptions.JobAppStatusCannotModifyException;
import au.com.ors.rest.exceptions.JobApplicationNotFoundException;
import au.com.ors.rest.exceptions.JobPostingNotFoundException;
import au.com.ors.rest.exceptions.ReviewMalformatException;
import au.com.ors.rest.exceptions.ReviewNotFoundException;
import au.com.ors.rest.resource.JobPostingResource;
import au.com.ors.rest.resource.ReviewResource;
import au.com.ors.rest.resource.assembler.ReviewResourceAssembler;

@Controller
@RequestMapping(value = "/reviews")
public class ReviewController {
	@Autowired
	ReviewDAO reviewDAO;
	
	@Autowired
	ReviewResourceAssembler reviewResourceAssembler;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ReviewResource> createReview(
			@RequestBody Review review
			) throws ReviewMalformatException {
		if (review == null) {
			throw new ReviewMalformatException("Cannot create null review");
		}
		if (StringUtils.isEmpty(review.get_appId())) {
			throw new ReviewMalformatException("Review malformed: _appId required");
		}
		if (StringUtils.isEmpty(review.get_uId())) {
			throw new ReviewMalformatException("Review malformed: _uId required");
		}
		
		String _reviewId = UUID.randomUUID().toString();
		review.set_reviewId(_reviewId);
		Review reviewResult = new Review(null, null, null, null, null);
		try {
			reviewResult = reviewDAO.create(review);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReviewResource reviewResource = reviewResourceAssembler.toResource(reviewResult);
		return new ResponseEntity<ReviewResource>(reviewResource, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ReviewResource>>  getReviews(
			@RequestParam(name = "_appId", required = false) String _appId,
			@RequestParam(name = "_uId", required = false) String _uId) {
		List<Review> reviewList = reviewDAO.findAll();
		if (reviewList == null) {
			reviewList = new ArrayList<Review>();
		}
		List<Review> reviewListResult = new ArrayList<Review>();
		for (Review review : reviewList) {
			if (!StringUtils.isEmpty(_appId)
					&& StringUtils.isEmpty(review.get_appId())) {
				continue;
			} else if (!StringUtils.isEmpty(_uId)
					&& StringUtils.isEmpty(review.get_uId())){
				continue;
			} else if (!StringUtils.isEmpty(_appId) 
					&& !StringUtils.isEmpty(review.get_appId())
					&& !review.get_appId().equals(_appId)) {
				continue;
			}  else if (!StringUtils.isEmpty(_uId) 
					&& !StringUtils.isEmpty(review.get_uId())
					&& !review.get_uId().equals(_uId)) {
				continue;
			}
			reviewListResult.add(review);
		}
		List<ReviewResource> reviewResourceList = reviewResourceAssembler.toResources(reviewListResult);
		return new ResponseEntity<List<ReviewResource>>(reviewResourceList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{_reviewId}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<ReviewResource> getJobPostingById(@PathVariable(value = "_reviewId") String _reviewId) throws ReviewNotFoundException {
		Review reviewById = reviewDAO.findById(_reviewId);
		if (reviewById == null) {
			throw new ReviewNotFoundException("review with _reviewId=" + _reviewId + " not found in database.");
		}
		ReviewResource reviewResource = reviewResourceAssembler.toResource(reviewById);
		return new ResponseEntity<ReviewResource>(reviewResource, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;

		RESTError error = new RESTError();
		if (e instanceof DAOException || e instanceof TransformerException) {
			error.setErrCode(DAOErrorCode.DATA_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (e instanceof ReviewNotFoundException) {
			error.setErrCode(RESTErrorCode.REVIEW_NOT_FOUND);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.NOT_FOUND);
		} else if (e instanceof ReviewMalformatException) {
			error.setErrCode(RESTErrorCode.CLIENT_BAD_REQUEST);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		} else {
			error.setErrCode(RESTErrorCode.GENERAL_SERVER_ERROR);
			error.setErrMessage(e.getMessage());
			responseEntity = new ResponseEntity(error,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}
}
