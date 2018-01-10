/*
 * The integration developer needs to create the method processData 
 * This method takes Message object of package com.sap.gateway.ip.core.customdev.util
 * which includes helper methods useful for the content developer:
 * 
 * The methods available are:
    public java.lang.Object getBody()
    
    //This method helps User to retrieve message body as specific type ( InputStream , String , byte[] ) - e.g. message.getBody(java.io.InputStream)
    public java.lang.Object getBody(java.lang.String fullyQualifiedClassName)

    public void setBody(java.lang.Object exchangeBody)

    public java.util.Map<java.lang.String,java.lang.Object> getHeaders()

    public void setHeaders(java.util.Map<java.lang.String,java.lang.Object> exchangeHeaders)

    public void setHeader(java.lang.String name, java.lang.Object value)

    public java.util.Map<java.lang.String,java.lang.Object> getProperties()

    public void setProperties(java.util.Map<java.lang.String,java.lang.Object> exchangeProperties) 

	public void setProperty(java.lang.String name, java.lang.Object value)
 * 
 */
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.cxf.binding.soap.SoapHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.securestore.SecureStoreService;
import com.sap.it.api.securestore.UserCredential;
import groovy.util.XmlSlurper;

def Message processData(Message message) {

   String sessionID = java.util.UUID.randomUUID().toString();
  
   // Set SOAP Heeader sessionID
   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
   dbf.setNamespaceAware(true);
   dbf.setIgnoringElementContentWhitespace(true);
   dbf.setValidating(false);
   DocumentBuilder db = dbf.newDocumentBuilder();
   Document doc = db.newDocument();
   Element authHeader = doc.createElementNS("urn:enterprise.soap.com", "SessionHeader");
   doc.appendChild(authHeader);
   Element clientId = doc.createElement("sessionId");
   clientId.setTextContent(sessionID);
   authHeader.appendChild(clientId);
   SoapHeader header = new SoapHeader(new QName(authHeader.getNamespaceURI(), authHeader.getLocalName()), authHeader);
   List  headersList  = new ArrayList<SoapHeader>();
   headersList.add(header);
   message.setHeader("org.apache.cxf.headers.Header.list", headersList);
   
   return message;
}
