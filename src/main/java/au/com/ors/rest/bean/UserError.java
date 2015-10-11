package au.com.ors.rest.bean;

import java.io.Serializable;

/**
 * User resource error class<br/>
 * 
 * @author hansmong
 *
 */
public class UserError implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7454501798800795164L;

	private String errCode;

	private String errMessage;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
}
