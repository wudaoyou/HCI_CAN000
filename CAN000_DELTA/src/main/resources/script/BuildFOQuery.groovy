import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.MappingContext;
import java.lang.Object;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParsePosition;

def Message processData(Message message) {
	
	def body = new XmlSlurper().parseText(message.getBody(java.lang.String));
	def properties = message.getProperties();
	def messageLog = messageLogFactory.getMessageLog(message);
	
	def MASS_CHANGE = properties.get("MASS_CHANGE");
	
	// Log Properties into MPL if enabled
	if (messageLog != null) {
	  //messageLog.setStringProperty("ENTITY_TYPE", properties.get("ENTITY_TYPE"));
	}
	
	// build external ID filters for job code, location and department
	def jobCodeIDs = "";
	def locationIDs = "";
	def deptIDs = "";
	def codeList = [];
	
	def job_codes = body.CompoundEmployee.person.employment_information.job_information.job_code;
	def locations = body.CompoundEmployee.person.employment_information.job_information.location;
	def deptartments = body.CompoundEmployee.person.employment_information.job_information.department;
	
	// collect all job_codes and remove duplicates
	codeList = [];
	job_codes.each {
		String value = it;
		if (codeList.indexOf(value) == -1 ) {
			codeList.add(value);
			if (jobCodeIDs.length() == 0) {
				jobCodeIDs = "externalCode eq '" + it + "'";
			} else {
				jobCodeIDs = jobCodeIDs + " or externalCode eq '" + it +"'";
			}
		}
	}
	
	// collect all locations and remove duplicates
	codeList = [];
	locations.each {
		String value = it;
		if (codeList.indexOf(value) == -1 ) {
			codeList.add(value);
			if (locationIDs.length() == 0) {
				locationIDs = "externalCode eq '" + it + "'";
			} else {
				locationIDs = locationIDs + " or externalCode eq '" + it +"'";
			}
		}
	}
	
	// collect all departments and remove duplicates
	codeList = [];
	deptartments.each {
		String value = it;
		if (codeList.indexOf(value) == -1 ) {
			codeList.add(value);
			if (deptIDs.length() == 0) {
				deptIDs = "externalCode eq '" + it + "'";
			} else {
				deptIDs = deptIDs + " or externalCode eq '" + it +"'";
			}
		}
	}
	
	
	// Get properties to build select clause
	def fromDate = properties.get("FULL_TRANSMISSION_START_DATE");
	
	def selectClause = "";
	def selectClauseJob = "";
	def selectClauseLocation = "";
	def selectClauseDepartment = "";
	
	def fieldsJobCode = "\$select=externalCode,name,startDate,endDate,status";
	def fieldsLocation = "\$select=externalCode,name,startDate,endDate,status";
	def fieldsDepartment = "\$select=externalCode,name,startDate,endDate,status";
	
	def selectClausePackage = "";
	
	// add from date criteria
	selectClause = "\$filter=status eq 'A'";
	
	if (jobCodeIDs.length() > 0) {
		selectClauseJob = "\$filter=" + jobCodeIDs;
	}
	if (locationIDs.length() > 0) {
		selectClauseLocation = "\$filter=" + locationIDs;
	}
	if (deptIDs.length() > 0) {
		selectClauseDepartment = "\$filter=" + deptIDs;
	}
	
	if (MASS_CHANGE == "TRUE") {
		selectClauseJob = selectClauseJob + "&fromDate=" + fromDate;
		selectClauseLocation = selectClauseLocation + "&fromDate=" + fromDate;
		selectClauseDepartment = selectClauseDepartment + "&fromDate=" + fromDate;
	} else  {
		selectClauseJob = selectClauseJob + "&" + fieldsJobCode + "&fromDate=" + fromDate;
		selectClauseLocation = selectClauseLocation + "&" + fieldsLocation + "&fromDate=" + fromDate;
		selectClauseDepartment = selectClauseDepartment + "&" + fieldsDepartment + "&fromDate=" + fromDate;
	}
	message.setProperty("HAS_MORE", "TRUE");
	message.setProperty("SELECT_CLAUSE_JOB",selectClauseJob);
	message.setProperty("SELECT_CLAUSE_LOCATION",selectClauseLocation);
	message.setProperty("SELECT_CLAUSE_DEPARTMENT",selectClauseDepartment);
	if (messageLog != null) {
	  messageLog.setStringProperty("SELECT_CLAUSE_JOB",selectClauseJob);
	  messageLog.setStringProperty("SELECT_CLAUSE_LOCATION",selectClauseLocation);
	  messageLog.setStringProperty("SELECT_CLAUSE_DEPARTMENT",selectClauseDepartment);
	}
	
	// Create property NUMBER_OF_RECORDS
	//List l = new ArrayList(1);
	//l.add(new Integer(0));
	//message.setProperty("NUMBER_OF_RECORDS", l);
	
	return message;
}