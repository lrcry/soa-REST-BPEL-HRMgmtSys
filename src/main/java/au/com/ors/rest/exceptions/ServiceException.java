package au.com.ors.rest.exceptions;

public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8202822154326556458L;
	
	public ServiceException(String msg) {
		super(msg);
	}
}
