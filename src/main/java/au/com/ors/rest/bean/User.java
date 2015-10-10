package au.com.ors.rest.bean;

import java.io.Serializable;

import org.springframework.util.StringUtils;

/**
 * User bean<br/>
 * 
 * @author hansmong
 *
 */
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

	public void set_uid(String _uid) {
		this._uid = _uid;
	}

	public void set_pwd(String _pwd) {
		this._pwd = _pwd;
	}

	public void setShortKey(String shortKey) {
		this.shortKey = shortKey;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String toString() {
		StringBuilder sbUser = new StringBuilder();
		if (!StringUtils.isEmpty(_uid)) {
			sbUser.append("_uid=").append(_uid).append(",");
		}

		if (!StringUtils.isEmpty(_pwd)) {
			sbUser.append("_pwd=").append(_pwd).append(",");
		}

		if (!StringUtils.isEmpty(shortKey)) {
			sbUser.append("shortKey=").append(shortKey).append(",");
		}

		if (!StringUtils.isEmpty(lastName)) {
			sbUser.append("lastName=").append(lastName).append(",");
		}

		if (!StringUtils.isEmpty(firstName)) {
			sbUser.append("firstName=").append(firstName).append(",");
		}

		if (!StringUtils.isEmpty(role)) {
			sbUser.append("role=").append(role).append(",");
		}

		if (!StringUtils.isEmpty(department)) {
			sbUser.append("department=").append(department);
		}

		return sbUser.toString();
	}
}
