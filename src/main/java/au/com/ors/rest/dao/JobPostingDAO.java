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

import au.com.ors.rest.bean.JobPosting;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.DAOLoadingXmlFileException;

@Component
public class JobPostingDAO {
	@Resource(name = "dataProperties")
	private Properties dataProperties;
	
	@Autowired
	private ServletContext servletContext;

	Document dom;
	String dataUrl;
	/**
	 * UserDAO initialization to read the XML into a list<br/>
	 * 
	 * @throws DAOException
	 */
	@PostConstruct
	public void init() throws DAOException {
		if (servletContext == null) {
			throw new DAOException(
					"Cannot autowire ServletContext to JobPostingDAO when injecting JobPostingDAO into JobPostingController: NullPointerException");
		}
		
		// get jobposting data file path
		String dataPath = dataProperties
				.getProperty("data.jobpostingdata.path");
		dataUrl = servletContext.getRealPath("/WEB-INF/db/" + dataPath);
		if (StringUtils.isEmpty(dataUrl)) {
			throw new DAOException(
					"Cannot find data.jobpostingdata.path in properties file.");
		}

		File jobPostingDataFile = new File(dataUrl);
		if (!jobPostingDataFile.exists()) {
			throw new DAOLoadingXmlFileException(
					"Cannot load job postings XML file from path " + dataUrl);
		}

		// open jobposting XML data via DOM parser

		// Make an instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(dataUrl);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JobPosting findByUid(String uid) {
		// TODO
		Element rootElement = dom.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("JobPosting")) {
				continue;
			}
			JobPosting jobPosting = new JobPosting(null, null, null, null, null, null, null);
			NodeList attributeList = node.getChildNodes();
			boolean idCheck = false;
			for (int j = 0; j < attributeList.getLength(); ++j) {
				Node currentNode = attributeList.item(j);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equalsIgnoreCase("_jobId")) {
						if (currentNode.getTextContent().equalsIgnoreCase(uid)) {
							jobPosting.set_jobId(currentNode.getTextContent());
							idCheck = true;
						} 
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("closingTime")) {
						jobPosting.setClosingTime(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("salaryRate")) {
						jobPosting.setSalaryRate(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("positionType")) {
						jobPosting.setPositionType(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("location")) {
						jobPosting.setLocation(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("details")) {
						jobPosting.setDetails(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("status")) {
						jobPosting.setStatus(currentNode.getTextContent());
					}
					
				}	
				
			}
			idCheck = false;
			if (jobPosting.get_jobId() != null) {
				return jobPosting;
			}
		}
		return null;
	}

	public List<JobPosting> findAll() {
		// TODO
		List<JobPosting> jobPostingList = new ArrayList<JobPosting>();
		Element rootElement = dom.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("JobPosting")) {
				continue;
			}
			NodeList attributeList = node.getChildNodes();
			JobPosting jobPosting = new JobPosting(null, null, null, null, null, null, null);
			for (int j = 0; j < attributeList.getLength(); ++j) {
				Node currentNode = attributeList.item(j);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equalsIgnoreCase("_jobId")) {
						jobPosting.set_jobId(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("closingTime")) {
						jobPosting.setClosingTime(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("salaryRate")) {
						jobPosting.setSalaryRate(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("positionType")) {
						jobPosting.setPositionType(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("location")) {
						jobPosting.setLocation(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("details")) {
						jobPosting.setDetails(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("status")) {
						jobPosting.setStatus(currentNode.getTextContent());
					}
					
				}
			}
			if (jobPosting.get_jobId() != null) {
				jobPostingList.add(jobPosting);
			}
		}
		return jobPostingList;
	}

	public JobPosting create(JobPosting jobPosting) throws TransformerException {
		// TODO Auto-generated method stub
		Element rootElement = dom.getDocumentElement();
		Element jobPostingEle = dom.createElement("jobPosting");
		Element _jobIdEle = dom.createElement("_jobId");
		Element closingTimeEle = dom.createElement("closingTime");
		Element salaryRateEle = dom.createElement("salaryRate");
		Element positionTypeEle = dom.createElement("positionType");
		Element locationEle = dom.createElement("location");
		Element detailsEle = dom.createElement("details");
		Element statusEle = dom.createElement("status");
		
		_jobIdEle.appendChild(dom.createTextNode(jobPosting.get_jobId()));
		closingTimeEle.appendChild(dom.createTextNode(jobPosting.getClosingTime()));
		salaryRateEle.appendChild(dom.createTextNode(jobPosting.getSalaryRate()));
		positionTypeEle.appendChild(dom.createTextNode(jobPosting.getPositionType()));
		locationEle.appendChild(dom.createTextNode(jobPosting.getLocation()));
		detailsEle.appendChild(dom.createTextNode(jobPosting.getDetails()));
		statusEle.appendChild(dom.createTextNode(jobPosting.getStatus()));
		
		jobPostingEle.appendChild(_jobIdEle);
		jobPostingEle.appendChild(closingTimeEle);
		jobPostingEle.appendChild(salaryRateEle);
		jobPostingEle.appendChild(positionTypeEle);
		jobPostingEle.appendChild(locationEle);
		jobPostingEle.appendChild(detailsEle);
		jobPostingEle.appendChild(statusEle);
		
		rootElement.appendChild(jobPostingEle);
		
		DOMSource source = new DOMSource(dom);
		System.out.println(dataUrl);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(dataUrl);
		transformer.transform(source, result);
		return jobPosting;
	}

	public JobPosting update(JobPosting jobPosting) throws TransformerException {
		// TODO Auto-generated method stub
		Element rootElement = dom.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("JobPosting")) {
				continue;
			}
			NodeList attributeList = node.getChildNodes();
			boolean idCheck = false;
			for (int j = 0; j < attributeList.getLength(); ++j) {
				Node currentNode = attributeList.item(j);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equalsIgnoreCase("_jobId")) {
						if (currentNode.getTextContent().equalsIgnoreCase(jobPosting.get_jobId())) {
							idCheck = true;
						}
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("closingTime")) {
						currentNode.setTextContent(jobPosting.getClosingTime());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("salaryRate")) {
						currentNode.setTextContent(jobPosting.getSalaryRate());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("positionType")) {
						currentNode.setTextContent(jobPosting.getPositionType());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("location")) {
						currentNode.setTextContent(jobPosting.getLocation());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("details")) {
						currentNode.setTextContent(jobPosting.getDetails());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("status")) {
						currentNode.setTextContent(jobPosting.getStatus());
					}
					
				}	
			}
			idCheck = false;
		}
		if (jobPosting.get_jobId() != null) {
			DOMSource source = new DOMSource(dom);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(dataUrl);
			transformer.transform(source, result);
			return jobPosting;
		}
		return null;
	}

	public JobPosting delete(String _jobId) {
		// TODO Auto-generated method stub
		return null;
	}
}
