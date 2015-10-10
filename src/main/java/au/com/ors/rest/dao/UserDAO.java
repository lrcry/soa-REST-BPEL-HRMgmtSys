package au.com.ors.rest.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import au.com.ors.rest.bean.User;
import au.com.ors.rest.dao.xmlhandler.UserSAXHandler;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.DAOLoadingXmlFileException;
import au.com.ors.rest.exceptions.DAOParseXmlException;

@Component
public class UserDAO {
	@Resource(name = "dataProperties")
	private Properties dataProperties;
	private DefaultHandler userXmlSaxHandler;
	
	private static List<User> usersList;
	
	@PostConstruct
	public void init() throws DAOException {
		// get user data file path
		String dataPath = dataProperties.getProperty("data.userdata.path");
		String dataUrl = getClass().getResource(dataPath).getFile();
		if (StringUtils.isEmpty(dataUrl)) {
			throw new DAOException("Cannot find data.userdata.path in properties file.");
		}
		
		File userDataFile = new File(dataUrl);
		if (!userDataFile.exists()) {
			throw new DAOLoadingXmlFileException("Cannot load user XML file from path " + dataUrl);
		}
		
		// open user XML data via SAX parser
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = saxParserFactory.newSAXParser();
			userXmlSaxHandler = new UserSAXHandler();
			saxParser.parse(userDataFile, userXmlSaxHandler);
		} catch (ParserConfigurationException | SAXException e1) {
			throw new DAOParseXmlException("Initializing SAX parser exception: " + e1.getMessage());
		} catch (IOException e) {
			throw new DAOParseXmlException("Parsing XML exception: " + e.getMessage());
		}
	}
	
	public User findByUid(String uid) {
		// TODO
		
		return null;
	}
	
	public List<User> findAll() {
		// TODO
		return null;
	}
}
