package au.com.ors.rest.exceptions;

/**
 * DAO general exception<br/>
 * 
 * @author hansmong
 *
 */
public class DAOException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1935878392512450551L;

	public DAOException() {
		super();
	}

	public DAOException(String msg) {
		super(msg);
	}
}
