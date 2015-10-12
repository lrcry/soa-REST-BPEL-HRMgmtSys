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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.bean.Review;
import au.com.ors.rest.dao.ReviewDAO;
import au.com.ors.rest.exceptions.JobPostingNotFoundException;
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
			@RequestParam(name = "_appId") String _appId,
			@RequestParam(name = "_uId") String _uId,
			@RequestParam(name = "comments") String comments,
			@RequestParam(name = "decision") String decision
			) {
		String _reviewId = UUID.randomUUID().toString();
		Review review = new Review(_reviewId, _appId, _uId, comments, decision);
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
	public ResponseEntity<List<ReviewResource>>  getReviews() {
		List<Review> reviewList = reviewDAO.findAll();
		if (reviewList == null) {
			reviewList = new ArrayList<Review>();
		}
		List<ReviewResource> reviewResourceList = reviewResourceAssembler.toResources(reviewList);
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
}
