package au.com.ors.rest.dao.xmlhandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

import au.com.ors.rest.bean.User;

public class UserSAXHandler extends DefaultHandler {
	private List<User> usersList = new ArrayList<>();
	private User user = null;
	
	public List<User> getUsersList() {
		return this.usersList;
	}
	
	// TODO override default handler method to read data into list of beans
}
