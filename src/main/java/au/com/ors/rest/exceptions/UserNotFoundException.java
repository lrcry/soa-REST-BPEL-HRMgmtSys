package au.com.ors.rest.exceptions;

public class UserNotFoundException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3726547604336405215L;

	public UserNotFoundException(String msg) {
		super(msg);
	}

}
