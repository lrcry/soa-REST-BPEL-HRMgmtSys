package au.com.ors.rest.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5252122254486693477L;

	String _uid;

	String _pwd;

	String shortKey;

	String lastName;

	String firstName;

	String role;

	String department;

	@JsonCreator
	public User(@JsonProperty("_uid") String _uid,
			@JsonProperty("_pwd") String _pwd,
			@JsonProperty("shortKey") String shortKey,
			@JsonProperty("lastName") String lastName,
			@JsonProperty("firstName") String firstName,
			@JsonProperty("role") String role,
			@JsonProperty("department") String department) {
		super();
		this._uid = _uid;
		this._pwd = _pwd;
		this.shortKey = shortKey;
		this.lastName = lastName;
		this.firstName = firstName;
		this.role = role;
		this.department = department;
	}

	public String get_uid() {
		return _uid;
	}

	public String get_pwd() {
		return _pwd;
	}

	public String getShortKey() {
		return shortKey;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getRole() {
		return role;
	}

	public String getDepartment() {
		return department;
	}
}
