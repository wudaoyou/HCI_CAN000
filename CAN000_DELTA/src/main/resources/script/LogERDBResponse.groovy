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
 import groovy.xml.*;
 
 def Message processData(Message message) {
				 
	 def map = message.getProperties();
	 def messageLog = messageLogFactory.getMessageLog(message);
	 def uuid = map.get("TRACKID");

	 messageLog.setStringProperty("ERDB error TrackID: ", uuid);

	 // get an exception java class instance
	 def ex = map.get("CamelExceptionCaught");
	 if (ex!=null) {
		// an http adapter throws an instance of org.apache.camel.component.ahc.AhcOperationFailedException
		if (ex.getClass().getCanonicalName().equals("org.apache.camel.component.ahc.AhcOperationFailedException")) {
			// save the http error response as a message attachment
			String errorMsg = ex.getResponseBody();
			def errorBody = new XmlSlurper().parseText(errorMsg).declareNamespace(env:"http://www.w3.org/2003/05/soap-envelope");

			def detailNode = errorBody.'env:Body'.'env:Fault'.'env:Detail';
			for (def node in detailNode) {
				def inBound = node.getAt(0).children.getAt(0);
				for (child in inBound.children) {
					for (error in child.children) { //rule
						String value = null;
						for (info in error.children) {
							value = info;
							break;
						}
						if(messageLog != null){
							messageLog.setStringProperty("ERDB "+error.name+" :", value);
						}
					}
					break;
				}
				break;
			}
		 }
	 }
 
	 return message;
}
