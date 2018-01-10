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

def Message processData(Message message) {
	def messageLog = messageLogFactory.getMessageLog(message);
	def pmap = message.getProperties();

	String fullTransmissionStartDate = pmap.get("FULL_TRANSMISSION_START_DATE");
	String lastModifiedDate = pmap.get("LAST_MODIFIED_DATE");
	String company = pmap.get("COMPANY");
	String employeeClass = pmap.get("EMPLOYEE_CLASS");
	String personIdExternal = pmap.get("PERSON_ID_EXTERNAL");
	String customMDFObjects = pmap.get("CUSTOM_MDF_OBJECTS");
	String userSetLastModifiedDateTime = pmap.get("USER_SET_LAST_MODIFIED_DATE_TIME");
	String changedSegmentOnly = pmap.get("CHANGED_SEGMENT_ONLY");
	String multipleEvents = pmap.get("MULTIPLE_JOB_EVENTS");
	
	messageLog.setStringProperty("PARAM 1", "Last Modified Date: " + lastModifiedDate);
	messageLog.setStringProperty("PARAM 2","Company: " + company);
	messageLog.setStringProperty("PARAM 3","Employee Calss: " + employeeClass);
	messageLog.setStringProperty("PARAM 4","Employee IDs: " + personIdExternal);
	messageLog.setStringProperty("PARAM 5","User Set Last Modified Date: " + userSetLastModifiedDateTime);
	messageLog.setStringProperty("PARAM 6","CHANGED_SEGMENT_ONLY: " + changedSegmentOnly);
	messageLog.setStringProperty("PARAM 7","MULTIPLE_JOB_EVENTS: " + multipleEvents);
	
	message.setProperty("count", "0");
	
	return message;
}

