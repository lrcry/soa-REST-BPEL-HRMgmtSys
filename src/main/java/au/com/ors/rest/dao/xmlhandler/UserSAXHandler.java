package au.com.ors.rest.dao.xmlhandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import au.com.ors.rest.bean.User;

/**
 * SAX Handler for user data XML file<br/>
 * Override DefaultHandler<br/>
 * @author hansmong
 *
 */
public class UserSAXHandler extends DefaultHandler {
	private List<User> usersList = new ArrayList<>();
	private User user = null;

	public List<User> getUsersList() {
		return this.usersList;
	}

	// TODO override default handler method to read data into list of beans
	boolean bUid = false;
	boolean bPwd = false;
	boolean bShortKey = false;

	boolean bLastName = false;
	boolean bFirstName = false;
	boolean bRole = false;
	boolean bDepartment = false;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("Entry")) {
			user = new User();
		} else if (qName.equalsIgnoreCase("Login")) {
			// do nothing just an indication of entering Login tag
		} else if (qName.equalsIgnoreCase("_uid")) {
			bUid = true;
		} else if (qName.equalsIgnoreCase("_pwd")) {
			bPwd = true;
		} else if (qName.equalsIgnoreCase("ShortKey")) {
			bShortKey = true;
		} else if (qName.equalsIgnoreCase("Details")) {
			// do nothing just an indication of entering Details tag
		} else if (qName.equalsIgnoreCase("LastName")) {
			bLastName = true;
		} else if (qName.equalsIgnoreCase("FirstName")) {
			bFirstName = true;
		} else if (qName.equalsIgnoreCase("Role")) {
			bRole = true;
		} else if (qName.equalsIgnoreCase("Department")) {
			bDepartment = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("Entry")) {
			usersList.add(user);
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (bUid) {
			user.set_uid(new String(ch, start, length));
			bUid = false;
		} else if (bPwd) {
			user.set_pwd(new String(ch, start, length));
			bPwd = false;
		} else if (bShortKey) {
			user.setShortKey(new String(ch, start, length));
			bShortKey = false;
		} else if (bLastName) {
			user.setLastName(new String(ch, start, length));
			bLastName = false;
		} else if (bFirstName) {
			user.setFirstName(new String(ch, start, length));
			bFirstName = false;
		} else if (bRole) {
			user.setRole(new String(ch, start, length));
			bRole = false;
		} else if (bDepartment) {
			user.setDepartment(new String(ch, start, length));
			bDepartment = false;
		}
	}
}