package au.com.ors.rest.exceptions;


public class JobPostingNotFoundException extends ServiceException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5738988163458947147L;

	public JobPostingNotFoundException(String msg) {
		super(msg);
	}

}
