package au.com.ors.rest.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Review implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4059548156199782911L;
	
	private String _reviewId;
	
	private String _appId;
	
	private String _uId;
	
	private String comments;
	
	private String decision;

	public String get_reviewId() {
		return _reviewId;
	}

	public void set_reviewId(String _reviewId) {
		this._reviewId = _reviewId;
	}

	public String get_appId() {
		return _appId;
	}

	public void set_appId(String _appId) {
		this._appId = _appId;
	}

	public String get_uId() {
		return _uId;
	}

	public void set_uId(String _uId) {
		this._uId = _uId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}
	
	@JsonCreator
	public Review(@JsonProperty("_reviewId") String _reviewId, 
			@JsonProperty("_appId") String _appId, 
			@JsonProperty("_uId") String _uId,
			@JsonProperty("comments") String comments,
			@JsonProperty("decision") String decision) {
		super();
		this._reviewId = _reviewId;
		this._appId = _appId;
		this._uId = _uId;
		this.comments = comments;
		this.decision = decision;
	} 
}
