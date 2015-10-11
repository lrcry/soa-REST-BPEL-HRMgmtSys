package au.com.ors.rest.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import au.com.ors.rest.bean.User;
import au.com.ors.rest.dao.xmlhandler.UserSAXHandler;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.DAOLoadingXmlFileException;
import au.com.ors.rest.exceptions.DAOParseXmlException;

/**
 * DAO for registered users<br/>
 * 
 * @author hansmong
 *
 */
@Component
public class UserDAO {
	@Resource(name = "dataProperties")
	private Properties dataProperties;
	private UserSAXHandler userXmlSaxHandler;

	private List<User> usersList = new ArrayList<>();

	@Autowired
	ServletContext servletContext;

	/**
	 * UserDAO initialization to read the XML into a list<br/>
	 * 
	 * @throws DAOException
	 */
	@PostConstruct
	public void init() throws DAOException {
		File userDataFile = null;
		String dataUrl = null;
		String dataPath = dataProperties.getProperty("data.userdata.path");
		if (servletContext == null) {
			throw new DAOException(
					"Cannot autowire ServletContext to UserDAO when injecting UserDAO into UserController: NullPointerException");
		} else {
			System.out.println("Hola!");
			dataUrl = servletContext.getRealPath("/WEB-INF/db/" + dataPath);

			if (StringUtils.isEmpty(dataUrl)) {
				throw new DAOException(
						"Cannot find data.userdata.path in properties file.");
			}

			userDataFile = new File(dataUrl);

			if (!userDataFile.exists()) {
				throw new DAOLoadingXmlFileException(
						"Cannot load user XML file from path " + dataUrl);
			}
		}

		// open user XML data via SAX parser
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = saxParserFactory.newSAXParser();
			userXmlSaxHandler = new UserSAXHandler();
			saxParser.parse(userDataFile, userXmlSaxHandler);
			usersList = userXmlSaxHandler.getUsersList();
		} catch (ParserConfigurationException | SAXException e1) {
			throw new DAOParseXmlException(
					"Initializing SAX parser exception: " + e1.getMessage());
		} catch (IOException e) {
			throw new DAOParseXmlException("Parsing XML exception: "
					+ e.getMessage());
		}

		System.out
				.println("================================== print all users ==================================");
		for (User user : usersList) {
			System.out.println(user.toString());
		}
		System.out
				.println("================================== print all users ended ==================================");
	}

	/**
	 * Find a user by its UID<br/>
	 * 
	 * @param uid
	 *            user ID
	 * @return object of user
	 */
	public User findByUid(String uid) {
		for (User user : usersList) {
			if (user.get_uid().equals(uid)) {
				return user;
			}
		}

		return null;
	}

	/**
	 * Find all users<br/>
	 * 
	 * @return list of users
	 */
	public List<User> findAll() {
		return this.usersList;
	}

	/**
	 * Find all users with whose department contains the given department
	 * keyword<br/>
	 * 
	 * @param department
	 *            department keyword
	 * @return lists of users that satisfies the criteria
	 */
	public List<User> findBySearchDepartment(String department) {
		List<User> userWithDept = new ArrayList<>();

		for (User user : usersList) {
			if (StringUtils.substringMatch(user.getDepartment(), 0, department)) {
				userWithDept.add(user);
			}
		}

		return userWithDept;
	}
}
