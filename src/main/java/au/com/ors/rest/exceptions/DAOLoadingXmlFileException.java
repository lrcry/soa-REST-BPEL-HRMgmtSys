package au.com.ors.rest.exceptions;

/**
 * DAO loading xml data file exception<br/>
 * 
 * @author hansmong
 *
 */
public class DAOLoadingXmlFileException extends DAOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 497286474582128102L;

	public DAOLoadingXmlFileException() {
		super();
	}

	public DAOLoadingXmlFileException(String msg) {
		super(msg);
	}
}
