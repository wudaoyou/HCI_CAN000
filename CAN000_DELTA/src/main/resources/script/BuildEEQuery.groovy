import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.lang.Exception;


def Message processData(Message message) {
	
	//Properties
	def pmap = message.getProperties();
	def messageLog = messageLogFactory.getMessageLog(message);

	String enableLogging = pmap.get("ENABLE_LOGGING");
	String fullTransmissionStartDate = pmap.get("FULL_TRANSMISSION_START_DATE");
	String lastModifiedDate = pmap.get("LAST_MODIFIED_DATE");
	String company = pmap.get("COMPANY");
	String employeeClass = pmap.get("EMPLOYEE_CLASS");
	String personIdExternal = pmap.get("PERSON_ID_EXTERNAL");
	String customMDFObjects = pmap.get("CUSTOM_MDF_OBJECTS");
	String userSetLastModifiedDateTime = pmap.get("USER_SET_LAST_MODIFIED_DATE_TIME");
	String changedSegmentOnly = pmap.get("CHANGED_SEGMENT_ONLY");
	String multipleEvents = pmap.get("MULTIPLE_JOB_EVENTS");
	String count = pmap.get("count");
	String days = pmap.get("PREIOD_DAY");
	
	
	if (count == null) {
		count = "1";
	}  else {
		int i = Integer.parseInt(count) + 1;
		count = Integer.toString(i);
	}
	message.setProperty("count", count);
	
	//Log externalized parameters
	if(messageLog != null){
		messageLog.setStringProperty("ENABLE_LOGGING ", enableLogging);
		messageLog.setStringProperty("FULL_TRANSMISSION_START_DATE ", fullTransmissionStartDate);
		messageLog.setStringProperty("COMPANY ", company);
		messageLog.setStringProperty("EMPLOYEE_CLASS ", employeeClass);
		messageLog.setStringProperty("PERSON_ID_EXTERNAL ", personIdExternal);
		messageLog.setStringProperty("CUSTOM_MDF_OBJECTS ", customMDFObjects);
		messageLog.setStringProperty("USER_SET_LAST_MODIFIED_DATE_TIME ", userSetLastModifiedDateTime);
		messageLog.setStringProperty("CHANGED_SEGMENT_ONLY ", changedSegmentOnly);
		messageLog.setStringProperty("MULTIPLE_JOB_EVENTS ", multipleEvents);
	}
	
	//Error handling for external parameters
	if(enableLogging == null || (!enableLogging.equals("TRUE") && !enableLogging.equals("FALSE"))){
		throw new Exception("Configuration Error: Please enter either TRUE or FALSE in the parameter ENABLE_LOGGING.   ");
	}
	if(fullTransmissionStartDate == null || fullTransmissionStartDate.length() < 10){
		throw new Exception("Configuration Error: Please enter FULL_TRANSMISSION_START_DATE in the format yyyy-MM-dd.   ");
	}
	if(changedSegmentOnly == null || (!changedSegmentOnly.equals("TRUE") && !changedSegmentOnly.equals("FALSE"))){
		throw new Exception("Configuration Error: Please enter either TRUE or FALSE in the parameter CHANGED_SEGMENT_ONLY.   ");
	}
	if(multipleEvents == null || (!multipleEvents.equals("TRUE") && !multipleEvents.equals("FALSE"))){
		throw new Exception("Configuration Error: Please enter either TRUE or FALSE in the parameter MULTIPLE_JOB_EVENTS.   ");
	}
	if(multipleEvents.equals("TRUE") && changedSegmentOnly.equals("TRUE")) {
		throw new Exception("Configuration Error: parameter MULTIPLE_JOB_EVENTS and CHANGED_SEGMENT_ONLY cannot both be TRUE at the same time.   ");
	}

	//Set general object list for select statement
	String objectsSelectStatement = " personal_documents_information, person, personal_information, address_information, phone_information, email_information, employment_information, job_information, compensation_information, paycompensation_recurring, paycompensation_non_recurring, national_id_card, deduction_recurring, global_assignment_information ";
	message.setProperty("OBJECTS_SELECT_STATEMENT", objectsSelectStatement);
	message.setProperty("HAS_MORE", "FALSE");
	
	//format custom MDF objects parameter
	  if(customMDFObjects != null && !customMDFObjects.equals("") && customMDFObjects.charAt(customMDFObjects.length() - 1) != ','){
		  customMDFObjects += ",";
	  }
	  message.setProperty("CUSTOM_MDF_OBJECTS", customMDFObjects);
	  
	  String query = "";
		  
	//Set ECERP_LAST_MODIFIED_DATE
	//if(lastModifiedDate == null || lastModifiedDate.equals("") || lastModifiedDate.equals("null")){
	if (userSetLastModifiedDateTime != null && !userSetLastModifiedDateTime.equals("") && !userSetLastModifiedDateTime.equals("null")) {
		//Get USER_SET_LAST_MODIFIED_DATE_TIME
		lastModifiedDate = userSetLastModifiedDateTime;
		//no_upd_lm = "TRUE";
	}
	if(lastModifiedDate == null || lastModifiedDate.equals("") || lastModifiedDate.equals("null") || lastModifiedDate.length() < 20){
		//throw new Exception("Configuration Error: Please enter a valid value for USER_SET_LAST_MODIFIED_DATE_TIME in the format: yyyy-MM-dd'T'HH:mm:ss'Z'.   ");
		lastModifiedDate = "2016-01-01T00:00:00Z";
	}else{
		if(messageLog != null){
			messageLog.setStringProperty("LAST_MODIFIED_DATE: ", lastModifiedDate);
		}
	}
	
	//Build dynamic where statement
	String queryPersonID = "";
	String queryLastModifiedDate = "";
	String no_upd_lm = "FALSE";
	
	//If person_id_external filter parameter is set use it
	if(personIdExternal != null && !personIdExternal.equals("")){
		
		// add multi-valued selection parameter for person id to where clause
		personIdExternal = personIdExternal.replaceAll(",", "', '");
		queryPersonID = "person_id_external IN ('" + personIdExternal + "')";
	
		query = queryPersonID;
		//no_upd_lm = "TRUE";
	}
	message.setProperty("NO_UPD_LM", no_upd_lm);
	
	if (lastModifiedDate != null && !lastModifiedDate.equals("")){
		Calendar now = Calendar.getInstance();
		 //Instant instant = Instant.now();
		//String nowDateStr = instant.toString().substring(0,10);
		String nowDateStr= now.get(Calendar.YEAR)+"-"+ (now.get(Calendar.MONTH) +1) + "-"+ now.get(Calendar.DATE);
		int d = 0;
		if(days==null || days.isEmpty()){
		       d = -30;              
		 }else{
		     d = 0- Integer.parseInt(days);
		 }
		 now.add(Calendar.DATE, d);
		 String fromDateStr = now.get(Calendar.YEAR)+"-"+ (now.get(Calendar.MONTH) +1) + "-"+ now.get(Calendar.DATE);
		
		
		//add selection parameter for last modified date
		queryLastModifiedDate = "last_modified_on > to_datetime('" + lastModifiedDate.toString().substring(0,19) + "Z" + "')";
		queryPeriodFromTo = " AND fromDate = to_date('"+fromDateStr+"','yyyy-MM-dd') AND toDate = to_date('"+nowDateStr+"','yyyy-MM-dd')"
		if (!query.equals("")) {
			query = query + " AND ";
		}
		 if (changedSegmentOnly.toUpperCase() == "TRUE") {
		 	query = query + queryLastModifiedDate;
		 }else{
		     query = query + queryLastModifiedDate + queryPeriodFromTo;
		 }
		
	}

	
	//Set PERSON_ID_EXTERNAL_PARAMETER
	message.setProperty("PERSON_ID_EXTERNAL_PARAMETER", queryPersonID);
	if(messageLog != null){
		messageLog.setStringProperty("PERSON_ID_EXTERNAL_PARAMETER ", queryPersonID);
	}
	
	//Set LAST_MODIFIED_DATE_PARAMETER
	message.setProperty("LAST_MODIFIED_DATE_PARAMETER", queryLastModifiedDate);
	if(messageLog != null){
		messageLog.setStringProperty("LAST_MODIFIED_DATE_PARAMETER ", queryLastModifiedDate);
	}
		  
	// add multi-valued selection parameter for company to where clause
	// format: ... IN ('Company1','Company2')
	if (company != null && !company.equals("") && !company.equals("<company>")) {
		company_split = company.split(",");
		for (int j=0; j < company_split.length; j++) {
			if (j==0)
				company = "'" + company_split[j].trim() + "'";
			else
				company = company + ",'" + company_split[j].trim() + "'";
		}
		if (!query.equals("")) {
			query = query + " AND ";
		}
		query = query + "company IN (" + company + ")";
	}
	
	// add multi-valued selection parameter for employee class to where clause
	// format: ... IN ('employeeClass1','employeeClass2')
	if(employeeClass != null && !employeeClass.equals("") && !employeeClass.equals("<employee_class>")) {
		employeeClass_split = employeeClass.split(",");
		for (int k=0; k < employeeClass_split.length; k++) {
			if (k==0)
				employeeClass = "'" + employeeClass_split[k].trim() + "'";
			else
				employeeClass = employeeClass + ",'" + employeeClass_split[k].trim() + "'";
		}
		if (!query.equals("")) {
			query = query + " AND ";
		}
		query = query + "employee_class IN (" + employeeClass + ")";
	}
		
	// add selection parameter for full transmission start date
//	if(fullTransmissionStartDate != null && fullTransmissionStartDate != "" && changedSegmentOnly.equals("FALSE") ) {
//		if (!query.equals("")) {
//			query = query + " AND ";
//		}
//		query = query + "effective_end_date >= to_date('" + fullTransmissionStartDate +"')";
//	}

	//Set FILTER_PARAMETERS
	message.setProperty("FILTER_PARAMETERS", query);
	
		messageLog.setStringProperty("FILTER_PARAMETERS: ", query);
	

	//*** Set SFAPI Parameters ***	 
	 String SFAPIParameters = "";//"agent=ERPPayrollHCI";
	 
	 if (changedSegmentOnly.toUpperCase() == "TRUE") {
		 SFAPIParameters = "queryMode=delta;resultOptions=changedSegmentsOnly";
	 } else {
	     //SFAPIParameters = "queryMode=periodDelta;resultOptions=isNotFirstQuery";
	     SFAPIParameters = "queryMode=periodDelta";
	 }

	 if (multipleEvents.toUpperCase() == "TRUE") {
		 SFAPIParameters = "resultOptions=allJobChangesPerDay";
	 }

	 message.setProperty("SFAPI_PARAMETERS", SFAPIParameters);
	
	return message;
}

