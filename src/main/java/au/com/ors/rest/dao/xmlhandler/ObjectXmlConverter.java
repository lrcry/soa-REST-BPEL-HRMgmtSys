package au.com.ors.rest.dao.xmlhandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

public class ObjectXmlConverter {
	private Marshaller marshaller;
	
	private Unmarshaller unmarshaller;

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
	
	public void objectToXml(Object object, String filePath) throws XmlMappingException, IOException {
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(filePath);
			getMarshaller().marshal(object, new StreamResult(outStream));
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
	}
	
	public Object xmlToObject(String filePath) throws XmlMappingException, IOException {
		FileInputStream inStream = null;
		
		try {
			inStream = new FileInputStream(filePath);
			return getUnmarshaller().unmarshal(new StreamSource(inStream));
		} finally {
			if (inStream != null) {
				inStream.close();
			}
		}
	}
}
