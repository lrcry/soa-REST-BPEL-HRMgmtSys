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
import au.com.ors.rest.bean.Review;
import au.com.ors.rest.exceptions.DAOException;
import au.com.ors.rest.exceptions.DAOLoadingXmlFileException;

@Component
public class ReviewDAO {
	@Resource(name = "dataProperties")
	private Properties dataProperties;

	@Autowired
	private ServletContext servletContext;

	Document dom;
	String dataUrl;

	@PostConstruct
	public void init() throws DAOException {
		if (servletContext == null) {
			throw new DAOException(
					"Cannot autowire ServletContext to JobPostingDAO when injecting JobPostingDAO into JobPostingController: NullPointerException");
		}

		// get the file path
		String dataPath = dataProperties.getProperty("data.reviewdata.path");
		dataUrl = servletContext.getRealPath("/WEB-INF/db/" + dataPath);
		if (StringUtils.isEmpty(dataUrl)) {
			throw new DAOException(
					"Cannot find data.reviewdata.path in properties file.");
		}

		File reviewFile = new File(dataUrl);
		if (!reviewFile.exists()) {
			throw new DAOLoadingXmlFileException(
					"Cannot load review XML file from path " + dataUrl);
		}

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
	
	public Review findById(String id) {
		// TODO
		Element rootElement = dom.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("Review")) {
				continue;
			}
			Review review = new Review(null, null, null, null, null);
			NodeList attributeList = node.getChildNodes();
			boolean idCheck = false;
			for (int j = 0; j < attributeList.getLength(); ++j) {
				Node currentNode = attributeList.item(j);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equalsIgnoreCase("_reviewId")) {
						if (currentNode.getTextContent().equalsIgnoreCase(id)) {
							review.set_reviewId(currentNode.getTextContent());
							idCheck = true;
						} 
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("_appId")) {
						review.set_appId(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("_uId")) {
						review.set_uId(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("comments")) {
						review.setComments(currentNode.getTextContent());
					} else if (idCheck && currentNode.getNodeName().equalsIgnoreCase("decision")) {
						review.setDecision(currentNode.getTextContent());
					}
					
				}	
				
			}
			idCheck = false;
			if (!StringUtils.isEmpty(review.get_reviewId())) {
				return review;
			}
		}
		return null;
	}
	
	public List<Review> findAll() {
		List<Review> reviewList = new ArrayList<Review>();
		Element rootElement = dom.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (!node.getNodeName().equalsIgnoreCase("Review")) {
				continue;
			}
			NodeList attributeList = node.getChildNodes();
			Review review = new Review(null, null, null, null, null);
			for (int j = 0; j < attributeList.getLength(); ++j) {
				Node currentNode = attributeList.item(j);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equalsIgnoreCase("_reviewId")) {
						review.set_reviewId(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("_appId")) {
						review.set_appId(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("_uId")) {
						review.set_uId(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("comments")) {
						review.setComments(currentNode.getTextContent());
					} else if (currentNode.getNodeName().equalsIgnoreCase("decision")) {
						review.setDecision(currentNode.getTextContent());
					}
				}
			}
			if (!StringUtils.isEmpty(review.get_reviewId())) {
				reviewList.add(review);
			}
		}
		return reviewList;
	}

	public Review create(Review review) throws TransformerException {
		// TODO Auto-generated method stub
		Element rootElement = dom.getDocumentElement();
		Element reviewEle = dom.createElement("review");
		Element _reviewIdEle = dom.createElement("_reviewId");
		Element _appIdEle = dom.createElement("_appId");
		Element _uIdEle = dom.createElement("_uId");
		Element commentsEle = dom.createElement("comments");
		Element decisionEle = dom.createElement("decision");
		
		_reviewIdEle.appendChild(dom.createTextNode(review.get_reviewId()));
		_appIdEle.appendChild(dom.createTextNode(review.get_appId()));
		_uIdEle.appendChild(dom.createTextNode(review.get_uId()));
		commentsEle.appendChild(dom.createTextNode(review.getComments()));
		decisionEle.appendChild(dom.createTextNode(review.getDecision()));

		
		reviewEle.appendChild(_reviewIdEle);
		reviewEle.appendChild(_appIdEle);
		reviewEle.appendChild(_uIdEle);
		reviewEle.appendChild(commentsEle);
		reviewEle.appendChild(decisionEle);
		
		rootElement.appendChild(reviewEle);
		
		DOMSource source = new DOMSource(dom);
		System.out.println(dataUrl);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(dataUrl);
		transformer.transform(source, result);
		return review;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
