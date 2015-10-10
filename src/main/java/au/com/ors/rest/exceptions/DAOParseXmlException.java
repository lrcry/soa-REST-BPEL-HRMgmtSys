package au.com.ors.rest.exceptions;

/**
 * DAO exception during parsing XMl data file<br/>
 * 
 * @author hansmong
 *
 */
public class DAOParseXmlException extends DAOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1564971628973708669L;

	public DAOParseXmlException(String msg) {
		super(msg);
	}
}
