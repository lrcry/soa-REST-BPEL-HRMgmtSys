package au.com.ors.rest.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.ors.rest.bean.JobApplication;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.DAOLoadingXmlFileException;
import au.com.ors.rest.exceptions.JobApplicationNotFoundException;

@Component
public class JobAppDAO {
	@Resource(name = "dataProperties")
	private Properties dataProperties;

	@Autowired
	ServletContext servletContext;

	private Document dom;

	private String dataUrl;

	private List<JobApplication> jobAppList = new ArrayList<>();

	@PostConstruct
	public void init() throws DAOException, ParserConfigurationException,
			SAXException, IOException {
		if (servletContext == null) {
			throw new DAOException(
					"Cannot autowire ServletContext to JobAppDAO when injecting JobAppDAO into JobAppController: NullPointerException");
		}

		// get jobposting data file path
		String dataPath = dataProperties
				.getProperty("data.jobpostingdata.path");
		dataUrl = servletContext.getRealPath("/WEB-INF/db/" + dataPath);
		System.out.println("jobapp_db_path=" + dataUrl);
		if (StringUtils.isEmpty(dataUrl)) {
			throw new DAOException(
					"Cannot find data.jobpostingdata.path in properties file.");
		}

		File jobPostingDataFile = new File(dataUrl);
		if (!jobPostingDataFile.exists()) {
			throw new DAOLoadingXmlFileException(
					"Cannot load job postings XML file from path " + dataUrl);
		}

		// Make an instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		dom = db.parse(dataUrl);

		Element root = dom.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("JobApplication")) {
				continue;
			}

			JobApplication app = new JobApplication();
			NodeList jobAppInfoList = node.getChildNodes();

			for (int j = 0; j < jobAppInfoList.getLength(); ++j) {
				Node current = jobAppInfoList.item(j);

				// load list
				if (current.getNodeType() == Node.ELEMENT_NODE) {
					String content = current.getTextContent();
					if (current.getNodeName().equals("_appId")) {
						app.set_appId(content);
					} else if (current.getNodeName().equals("_jobId")) {
						app.set_jobId(content);
					} else if (current.getNodeName().equals(
							"driverLicenseNumber")) {
						app.setDriverLicenseNumber(content);
					} else if (current.getNodeName().equals("fullName")) {
						app.setFullName(content);
					} else if (current.getNodeName().equals("postCode")) {
						app.setPostCode(content);
					} else if (current.getNodeName().equals("textCoverLetter")) {
						app.setTextCoverLetter(content);
					} else if (current.getNodeName().equals("textBriefResume")) {
						app.setTextBriefResume(content);
					}
				}
			}

			if (!StringUtils.isEmpty(app.get_appId())) {
				jobAppList.add(app);
			}
		}

		System.out
				.println("================================== print all job applications ==================================");
		for (JobApplication app : jobAppList) {
			System.out.println(app.toString());
		}
		System.out
				.println("================================== print all job applications end ==================================");
	}

	/**
	 * Create a new job application<br/>
	 * 
	 * @param application
	 *            job application object
	 * @return created job application object
	 * @throws TransformerException 
	 */
	public JobApplication create(JobApplication application) throws TransformerException {
		Element root = dom.getDocumentElement();

		// new jobapp node
		Element jobAppNew = dom.createElement("JobApplication");

		// jobapp info
		Element _appIdNew = dom.createElement("_appId");
		_appIdNew.appendChild(dom.createTextNode(application.get_appId()));
		
		Element _jobIdNew = dom.createElement("_jobId");
		_jobIdNew.appendChild(dom.createTextNode(application.get_jobId()));
		
		Element driverLicenseNumberNew = dom
				.createElement("driverLicenseNumber");
		driverLicenseNumberNew.appendChild(dom.createTextNode(application.getDriverLicenseNumber()));
		
		Element fullNameNew = dom.createElement("fullName");
		fullNameNew.appendChild(dom.createTextNode(application.getFullName()));
		
		Element postCodeNew = dom.createElement("postCode");
		postCodeNew.appendChild(dom.createTextNode(application.getPostCode()));
		
		Element textCoverLetterNew = dom.createElement("textCoverLetter");
		textCoverLetterNew.appendChild(dom.createTextNode(application.getTextCoverLetter()));
		
		Element textBriefResumeNew = dom.createElement("textBriefResume");
		textBriefResumeNew.appendChild(dom.createTextNode(application.getTextBriefResume()));

		// append info to jobapp node
		jobAppNew.appendChild(_appIdNew);
		jobAppNew.appendChild(_jobIdNew);
		jobAppNew.appendChild(driverLicenseNumberNew);
		jobAppNew.appendChild(fullNameNew);
		jobAppNew.appendChild(postCodeNew);
		jobAppNew.appendChild(textCoverLetterNew);
		jobAppNew.appendChild(textBriefResumeNew);
		
		// append new node to root
		root.appendChild(jobAppNew);
		
		// write to XML
		DOMSource source = new DOMSource();
		TransformerFactory tfFactory = TransformerFactory.newInstance();
		Transformer tf = tfFactory.newTransformer();
		StreamResult result = new StreamResult(dataUrl);
		tf.transform(source, result);
		
		return application;
	}

	/**
	 * Update an existing job application<br/>
	 * 
	 * @param application
	 *            job application to be updated
	 * @throws JobApplicationNotFoundException
	 * @throws TransformerException
	 */
	public JobApplication update(JobApplication application)
			throws JobApplicationNotFoundException, TransformerException {
		int jobIndex = -1;
		for (int i = 0; i < jobAppList.size(); ++i) {
			if (jobAppList.get(i).get_appId().equals(application.get_appId())) {
				// found
				jobIndex = i;
				break;
			}
		}

		if (jobIndex < 0) {
			throw new JobApplicationNotFoundException(
					"Application with _appId=" + application.get_appId()
							+ " not found in database while updating.");
		}

		jobAppList.set(jobIndex, application);

		// update section to XML
		Element root = dom.getDocumentElement();
		NodeList nodeList = root.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equals("JobApplication")) {
				continue;
			}

			NodeList appInfoList = node.getChildNodes();
			for (int j = 0; j < appInfoList.getLength(); ++j) {
				Node current = appInfoList.item(j);

				// _appId, _jobId cannot be updated after the application
				// created already
				if (current.getNodeType() == Node.ELEMENT_NODE) {
					if (current.getNodeName().equals("_appId")) {
						if (!current.getTextContent().equals(
								application.get_appId())) {
							break; // not the right application ID
						}
					} else if (current.getNodeName().equals("_jobId")) {
						continue;
					} else if (current.getNodeName().equals(
							"driverLicenseNumber")) {
						current.setTextContent(application
								.getDriverLicenseNumber());
					} else if (current.getNodeName().equals("fullName")) {
						current.setTextContent(application.getFullName());
					} else if (current.getNodeName().equals("postCode")) {
						current.setTextContent(application.getPostCode());
					} else if (current.getNodeName().equals("textCoverLetter")) {
						current.setTextContent(application.getTextCoverLetter());
					} else if (current.getNodeName().equals("textBriefResume")) {
						current.setTextContent(application.getTextBriefResume());
					}
				}
			}
		}

		// write dom
		DOMSource source = new DOMSource(dom);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(dataUrl);
		transformer.transform(source, result);
		return application;
	}

	/**
	 * Delete an existing job application<br/>
	 * 
	 * @param application
	 *            job application to be deleted
	 * @throws DAOException
	 */
	public void delete(JobApplication application) throws DAOException {
		throw new DAOException("Not supported delete method in JobAppDAO");
	}

	/**
	 * Retrieve all job applications<br/>
	 * 
	 * @return list of job applications
	 */
	public List<JobApplication> findAll() {
		return jobAppList;
	}

	/**
	 * Retrieve a job application by its application ID<br/>
	 * 
	 * @param _appId
	 *            application ID
	 * @return a job application
	 */
	public JobApplication findById(String _appId) {
		for (JobApplication app : jobAppList) {
			if (app.get_appId().equals(_appId)) {
				return app;
			}
		}

		return null;
	}

	/**
	 * Retrieve job applications by which the application is for<br/>
	 * 
	 * @param _jobId
	 *            job ID
	 * @return list of job applications
	 */
	public List<JobApplication> findByJobPostingId(String _jobId) {
		List<JobApplication> appList = new ArrayList<>();
		for (JobApplication app : jobAppList) {
			if (app.get_jobId().equals(_jobId)) {
				appList.add(app);
			}
		}

		return appList;
	}
}
