package au.com.ors.rest.controller;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.com.ors.rest.bean.AutoCheckReq;
import au.com.ors.rest.bean.AutoCheckRes;
import au.com.ors.rest.bean.RESTError;

@Controller
@RequestMapping("/autocheck")
public class AutoCheckController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AutoCheckRes> autoCheck(@RequestBody AutoCheckReq req)
			throws Exception {
		AutoCheckRes autoCheckRes = new AutoCheckRes(null, null);

		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
				.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory
				.createConnection();

		String url = "http://localhost:6060/ode/processes/AutoCheck";
		SOAPMessage soapResponse = soapConnection.call(
				createSOAPRequest(req.getDriverLicenseNumber(),
						req.getFullName(), req.getPostCode()), url);

		soapConnection.close();

		SOAPBody body = soapResponse.getSOAPBody();
		Node rootnode = body.getChildNodes().item(0);
		NodeList nodeList = rootnode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeName().equals("ns:pdvResult")) {
				autoCheckRes.setPdvResult(currentNode.getTextContent());
			} else if (currentNode.getNodeName().equals("ns:crvResult")) {
				autoCheckRes.setCrvResult(currentNode.getTextContent());
			}
		}
		return new ResponseEntity<AutoCheckRes>(autoCheckRes, HttpStatus.OK);
	}

	private SOAPMessage createSOAPRequest(String driverLicenseNumber,
			String fullName, String postCode) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String serverURI = "http://soap.ors.com.au/pdv";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("pdv", serverURI);

		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapElement = soapBody.addChildElement(
				"PDVCheckRequestMsg", "pdv");
		SOAPElement soapElementChild1 = soapElement.addChildElement(
				"driverLicenseNumber", "pdv");
		soapElementChild1.addTextNode(driverLicenseNumber);
		SOAPElement soapElementChild2 = soapElement.addChildElement("fullName",
				"pdv");
		soapElementChild2.addTextNode(fullName);
		SOAPElement soapElementChild3 = soapElement.addChildElement("postCode",
				"pdv");
		soapElementChild3.addTextNode(postCode);

		// MimeHeaders headers = soapMessage.getMimeHeaders();
		// headers.addHeader(S, value);
		soapMessage.saveChanges();

		System.out.println("Request SOAP Message:");
		soapMessage.writeTo(System.out);
		return soapMessage;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler
	ResponseEntity handleExceptions(Exception e) {
		ResponseEntity responseEntity = null;

		// TODO handle exceptions of this controller
		RESTError error = new RESTError();

		error.setErrCode("INVALID_INPUT");
		error.setErrMessage("Your INPUT IS WRONG");
		responseEntity = new ResponseEntity(error,
				HttpStatus.INTERNAL_SERVER_ERROR);

		return responseEntity;
	}
}
