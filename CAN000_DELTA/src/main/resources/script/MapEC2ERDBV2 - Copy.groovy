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
import groovy.xml.*;

class FOObject {
	String startDate;
	String endDate;
	String description;
}

class FOTimeZone extends FOObject {
	String localName;
	String std_UTCOffsetHours
	String std_Code
	String std_Description	
	String daylight_UTCOffsetHours
	String daylight_Code
	String daylight_Description
}

class FOListOption extends FOObject {
	String externalCode;
}

class FOCompany extends FOObject {
	String country;
	String territoryCode;
	String territoryName;
}

class FOLocation extends FOObject {
	String type;
	String typeDesc;
	String subType;
	String subTypeDesc;
	String addressLine1;
	String province;
	String city;
	String country;
	String zipcode;
}

class Domain {
	HashMap FOJobs;
	HashMap FOLocations;
	HashMap FODepartments;
	HashMap FORegions;
	HashMap FODivisions;
	HashMap FODistricts;
	HashMap FOCostCenters;
	HashMap FOCompanies;
	HashMap FOPositions;
	HashMap FOWorkSchedules;
	HashMap FOEventReasons;
	HashMap FOPayscaleAreas;
	HashMap FOPayscaleTypes;
	HashMap FOPayscaleGroups;
	HashMap FOPayscaleLevels;
	HashMap FOPaycomponents;
	HashMap FOPicklists;
	HashMap FOTimeZones;
	
	
	def Domain() {
		FOJobs = new HashMap();
		FOLocations = new HashMap();
		FODepartments = new HashMap();
		FORegions = new HashMap();
		FODivisions = new HashMap();
		FODistricts = new HashMap();
		FOCostCenters = new HashMap();
		FOCompanies = new HashMap();
		FOPositions = new HashMap();
		FOWorkSchedules = new HashMap();
		FOEventReasons = new HashMap();
		FOPayscaleAreas = new HashMap();
		FOPayscaleTypes = new HashMap();
		FOPayscaleGroups = new HashMap();
		FOPayscaleLevels = new HashMap();
		FOPaycomponents = new HashMap();
		FOPicklists = new HashMap();
		FOTimeZones = new HashMap();
	}
	
	public FOObject collectData(HashMap map, String code, String startDate, String endDate, String desc) {
		FOObject o = new FOObject();
		
		o.startDate = startDate.substring(0, 10);
		o.endDate = endDate.substring(0,10);
		o.description = desc;
		ArrayList l = map.get(code);
		if (l == null) {
			l = new ArrayList();
			map.put(code, l);
		}
		l.push(o);
		return o;
	}
	
	public FOObject findDomainObject(HashMap map, String code, String startDate) {
		ArrayList l = map.get(code);
		if (l != null) {
			for (FOObject o in l) {
				if (o.startDate <= startDate && o.endDate >= startDate) return o;
			}
		}
		
		return null;
	}
	
	public FOListOption findPicklistValue(String picklistId, String code, String startDate) {
		HashMap codes = FOPicklists.get(picklistId);
		if (codes != null) {
			FOListOption op = codes.get(code);
			if (op == null) return null;
			if (op.startDate <= startDate && op.endDate >= startDate) return op;
		}
		return null;
	}
	
	public void parse(def xml) {
		def jobs = xml.'multimap:Message1'.FOJobCode.FOJobCode;
		jobs.each {
			collectData(FOJobs, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}
		
		def depts = xml.'multimap:Message1'.FODepartment.FODepartment;
		depts.each {
			collectData(FODepartments, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}

		def divisions = xml.'multimap:Message1'.FODivision.FODivision;
		divisions.each {
			collectData(FODivisions, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}

		def districts = xml.'multimap:Message1'.cust_District.cust_District;
		districts.each {
			collectData(FODistricts, (String)it.externalCode, (String)it.effectiveStartDate, (String)it.effectiveEndDate, (String)it.externalName);
		}

		def regions = xml.'multimap:Message1'.cust_Region.cust_Region;
		regions.each {
			collectData(FORegions, (String)it.externalCode, (String)it.effectiveStartDate, (String)it.effectiveEndDate, (String)it.externalName);
		}

		def companies = xml.'multimap:Message1'.FOCompany.FOCompany;
		companies.each {
			FOCompany c = new FOCompany();
			c.startDate = it.startDate;
			c.startDate = c.startDate.substring(0,10);
			c.endDate = it.endDate;
			c.endDate = c.endDate.substring(0,10);
			c.description = it.description;
			c.country = it.country;
			c.territoryCode = it.countryNav.Territory.territoryCode;
			c.territoryName = it.countryNav.Territory.territoryName;
			ArrayList l = FOCompanies.get((String)it.externalCode);
			if (l == null) {
				l = new ArrayList();
				FOCompanies.put((String)it.externalCode, l);
			}
			l.push(c);
		}

		def positions = xml.'multimap:Message1'.Position.Position;
		positions.each {
			collectData(FOPositions, (String)it.code, (String)it.effectiveStartDate, (String)it.effectiveEndDate, (String)it.externalName_defaultValue);
		}
		
		def costcenters = xml.'multimap:Message1'.FOCostCenter.FOCostCenter;
		costcenters.each {
			collectData(FOCostCenters, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}
			
		def workschedules = xml.'multimap:Message1'.WorkSchedule.WorkSchedule;
		workschedules.each {
			collectData(FOWorkSchedules, (String)it.externalCode, (String)it.mdfSystemEffectiveStartDate, (String)it.mdfSystemEffectiveEndDate, (String)it.externalName_defaultValue);
		}
		
		def reasons = xml.'multimap:Message1'.FOEventReason.FOEventReason;
		reasons.each {
			collectData(FOEventReasons, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}

		def paycomonents = xml.'multimap:Message1'.FOPayComponent.FOPayComponent;
		paycomonents.each {
			collectData(FOPaycomponents, (String)it.externalCode, (String)it.startDate, (String)it.endDate, (String)it.name);
		}

		def payscaleAreas = xml.'multimap:Message1'.PayScaleArea.PayScaleArea;
		payscaleAreas.each {
			collectData(FOPayscaleAreas, (String)it.code, (String)it.mdfSystemEffectiveStartDate, (String)it.mdfSystemEffectiveEndDate, (String)it.externalName_defaultValue);
		}

		def payscaleTypes = xml.'multimap:Message1'.PayScaleType.PayScaleType;
		payscaleTypes.each {
			collectData(FOPayscaleTypes, (String)it.code, (String)it.mdfSystemEffectiveStartDate, (String)it.mdfSystemEffectiveEndDate, (String)it.externalName_defaultValue);
		}
	
		def payScaleGroups = xml.'multimap:Message1'.PayScaleGroup.PayScaleGroup;
		payScaleGroups.each {
			collectData(FOPayscaleGroups, (String)it.code, (String)it.mdfSystemEffectiveStartDate, (String)it.mdfSystemEffectiveEndDate, (String)it.externalName_defaultValue);
		}

		def payScaleLevels = xml.'multimap:Message1'.PayScaleLevel.PayScaleLevel;
		payScaleLevels.each {
			collectData(FOPayscaleLevels, (String)it.code, (String)it.effectiveStartDate, (String)it.effectiveEndDate, (String)it.externalName_defaultValue);
		}
		
		def picklistV1 = xml.'multimap:Message1'.PicklistOption.PicklistOption;
		picklistV1.each {
			HashMap codes = null;
			String listID = it.picklist.Picklist.picklistId;
			FOListOption option = new FOListOption();
			def labels = it.picklistLabels.PicklistLabel;
			labels.each {
				if (it.locale == 'en_US') {
					option.description = it.label;
					return;
				}
			}
			option.externalCode = it.externalCode;
			option.startDate = '1900-01-01';
			option.endDate = '9999-12-31';
			codes = FOPicklists.get(listID);

			if (codes == null) {
				codes = new HashMap();
				FOPicklists.put(listID, codes)
			}
			codes.put(option.externalCode, option);
		}
		
		def picklistV2 = xml.'multimap:Message1'.PickListValueV2.PickListValueV2;
		picklistV2.each {
			HashMap codes = null;
			FOListOption option = new FOListOption();
			option.startDate = it.mdfSystemEffectiveStartDate;
			option.endDate = it.mdfSystemEffectiveEndDate;
			option.startDate = option.startDate.substring(0,10);
			option.endDate = option.endDate.substring(0,10);
			option.externalCode = it.externalCode;
			option.description = it.label_defaultValue;
			codes = FOPicklists.get((String)it.PickListV2_id);
			if (codes == null) {
				codes = new HashMap();
				FOPicklists.put((String)it.PickListV2_id, codes)
			}
			codes.put(option.externalCode, option);
		}
		
		def locations = xml.'multimap:Message1'.FOLocation.FOLocation;
		locations.each {
			FOLocation loc = new FOLocation();
			loc.startDate = it.startDate;
			loc.startDate = loc.startDate.substring(0,10);
			loc.endDate = it.endDate;
			loc.endDate = loc.endDate.substring(0,10);
			loc.description = it.description;
			loc.type = it.customString3Nav.cust_locationType.locationTypeCode;
			loc.typeDesc = it.customString3Nav.cust_locationType.locationTypeName;
			loc.subType = it.customString4Nav.externalCode;
			loc.subTypeDesc = it.customString4Nav.PicklistOption.picklistLabels.picklistLabel.label;
			loc.addressLine1 = it.addressNavDEFLT.FOCorporateAddressDEFLT.address1;
			loc.city = it.addressNavDEFLT.FOCorporateAddressDEFLT.city;
			loc.zipcode = it.addressNavDEFLT.FOCorporateAddressDEFLT.zipCode;
			loc.country = it.addressNavDEFLT.FOCorporateAddressDEFLT.country;
			loc.province = it.addressNavDEFLT.FOCorporateAddressDEFLT.province;
			FOObject country_data = findPicklistValue("csfCountry", loc.country, '2000-01-01');
			loc.country = country_data.description;
			
			ArrayList l = FOLocations.get((String)it.externalCode);
			if (l == null) {
				l = new ArrayList();
				FOLocations.put((String)it.externalCode, l);
			}
			l.push(loc);
		}

		// add gender
		HashMap codes = new HashMap();
		FOListOption option = new FOListOption();
		option.startDate = "1900-01-01";
		option.endDate = "9999-12-31";
		option.externalCode = "M";
		option.description = "Male";
		codes.put(option.externalCode, option);
		
		option = new FOListOption();
		option.startDate = "1900-01-01";
		option.endDate = "9999-12-31";
		option.externalCode = "F";
		option.description = "Female";
		codes.put(option.externalCode, option);
		
		option = new FOListOption();
		option.startDate = "1900-01-01";
		option.endDate = "9999-12-31";
		option.externalCode = "U";
		option.description = "Unknown";
		codes.put(option.externalCode, option);
		
		option = new FOListOption();
		option.startDate = "1900-01-01";
		option.endDate = "9999-12-31";
		option.externalCode = "D";
		option.description = "Undeclared";
		codes.put(option.externalCode, option);

		option = new FOListOption();
		option.startDate = "1900-01-01";
		option.endDate = "9999-12-31";
		option.externalCode = "O";
		option.description = "Others";
		codes.put(option.externalCode, option);
		FOPicklists.put("Gender", codes)
		
		//add timezone
		FOTimeZone tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-04:00";
		tz.daylight_UTCOffsetHours = "-04:00";
		tz.std_Code = "AST";
		tz.std_Description = "Atlantic Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Blanc-Sablon", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-04:00";
		tz.daylight_UTCOffsetHours = "-03:00";
		tz.std_Code = "AST";
		tz.std_Description = "Atlantic Standard Time";
		tz.daylight_Code = "ADT";
		tz.daylight_Description = "Atlantic Daylight Time";
		FOTimeZones.put("America/Glace_Bay", tz);
		FOTimeZones.put("America/Goose_Bay", tz);
		FOTimeZones.put("America/Halifax", tz);
		FOTimeZones.put("America/Moncton", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-05:00";
		tz.daylight_UTCOffsetHours = "-05:00";
		tz.std_Code = "EST";
		tz.std_Description = "Eastern Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Atikokan", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-05:00";
		tz.daylight_UTCOffsetHours = "-04:00";
		tz.std_Code = "EST";
		tz.std_Description = "Eastern Standard Time";
		tz.daylight_Code = "EDT";
		tz.daylight_Description = "Eastern Daylight Time";
		FOTimeZones.put("America/Iqaluit", tz);
		FOTimeZones.put("America/Montreal", tz);
		FOTimeZones.put("America/Nipigon", tz);
		FOTimeZones.put("America/Pangnirtung", tz);
		FOTimeZones.put("America/Thunder_Bay", tz);
		FOTimeZones.put("America/Toronto", tz);
		FOTimeZones.put("America/Detroit", tz);
		FOTimeZones.put("America/Indiana/Indianapolis", tz);
		FOTimeZones.put("America/Indiana/Marengo", tz);
		FOTimeZones.put("America/Indiana/Petersburg", tz);
		FOTimeZones.put("America/Indiana/Vevay", tz);
		FOTimeZones.put("America/Indiana/Vincennes", tz);
		FOTimeZones.put("America/Indiana/Winamac", tz);
		FOTimeZones.put("America/Kentucky/Louisville", tz);
		FOTimeZones.put("America/Kentucky/Monticello", tz);
		FOTimeZones.put("America/New_York", tz);
		FOTimeZones.put("US/Eastern", tz);
		FOTimeZones.put("US/East-Indiana", tz);
		FOTimeZones.put("US/Michigan", tz);		
		

		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-06:00";
		tz.daylight_UTCOffsetHours = "-05:00";
		tz.std_Code = "CST";
		tz.std_Description = "Central Standard Time";
		tz.daylight_Code = "CDT";
		tz.daylight_Description = "Central Daylight Time";
		FOTimeZones.put("America/Rainy_River", tz);
		FOTimeZones.put("America/Rankin_Inlet", tz);
		FOTimeZones.put("America/Resolute", tz);
		FOTimeZones.put("America/Winnipeg", tz);
		FOTimeZones.put("America/Chicago", tz);
		FOTimeZones.put("America/Indiana/Knox", tz);
		FOTimeZones.put("America/Indiana/Tell_City", tz);
		FOTimeZones.put("America/Menominee", tz);
		FOTimeZones.put("America/North_Dakota/Beulah", tz);
		FOTimeZones.put("America/North_Dakota/Center", tz);
		FOTimeZones.put("America/North_Dakota/New_Salem", tz);
		FOTimeZones.put("US/Central", tz);
		FOTimeZones.put("US/Indiana-Starke", tz);
		
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-06:00";
		tz.daylight_UTCOffsetHours = "-06:00";
		tz.std_Code = "CST";
		tz.std_Description = "Central Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Regina", tz);
		FOTimeZones.put("America/Swift_Current", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-07:00";
		tz.daylight_UTCOffsetHours = "-07:00";
		tz.std_Code = "MST";
		tz.std_Description = "Mountain Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Creston", tz);
		FOTimeZones.put("America/Dawson_Creek", tz);
		FOTimeZones.put("America/Phoenix", tz);
		FOTimeZones.put("US/Arizona", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-07:00";
		tz.daylight_UTCOffsetHours = "-06:00";
		tz.std_Code = "MST";
		tz.std_Description = "Mountain Standard Time";
		tz.daylight_Code = "MDT";
		tz.daylight_Description = "Mountain Daylight Time";
		FOTimeZones.put("America/Cambridge_Bay", tz);
		FOTimeZones.put("America/Shiprock", tz);
		FOTimeZones.put("America/Edmonton", tz);
		FOTimeZones.put("America/Inuvik", tz);
		FOTimeZones.put("America/Yellowknife", tz);
		FOTimeZones.put("America/Boise", tz);
		FOTimeZones.put("America/Denver", tz);
		FOTimeZones.put("US/Mountain", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-08:00";
		tz.daylight_UTCOffsetHours = "-08:00";
		tz.std_Code = "PST";
		tz.std_Description = "Pacific Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Metlakatla", tz);
		FOTimeZones.put("US/Pacific", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-08:00";
		tz.daylight_UTCOffsetHours = "-07:00";
		tz.std_Code = "PST";
		tz.std_Description = "Pacific Standard Time";
		tz.daylight_Code = "PDT";
		tz.daylight_Description = "Pacific Daylight Time";
		FOTimeZones.put("America/Dawson", tz);
		FOTimeZones.put("America/Vancouver", tz);
		FOTimeZones.put("America/Los_Angeles", tz);
		FOTimeZones.put("America/Whitehorse", tz);

		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-03:30";
		tz.daylight_UTCOffsetHours = "-02:30";
		tz.std_Code = "NST";
		tz.std_Description = "Newfoundland Standard Time";
		tz.daylight_Code = "NDT";
		tz.daylight_Description = "Newfoundland Daylight Time";
		FOTimeZones.put("America/St_Johns", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-09:00";
		tz.daylight_UTCOffsetHours = "-08:00";
		tz.std_Code = "AKST";
		tz.std_Description = "Alaskan Standard Time";
		tz.daylight_Code = "AKDT";
		tz.daylight_Description = "Alaskan Daylight Time";
		FOTimeZones.put("America/Anchorage", tz);
		FOTimeZones.put("America/Juneau", tz);
		FOTimeZones.put("America/Nome", tz);
		FOTimeZones.put("America/Sitka", tz);
		FOTimeZones.put("America/Yakutat", tz);
		FOTimeZones.put("US/Alaska", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-10:00";
		tz.daylight_UTCOffsetHours = "-10:00";
		tz.std_Code = "HST";
		tz.std_Description = "Hawaii-Aleutian Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("America/Honolulu", tz);
		FOTimeZones.put("America/Hawaii", tz);
		
		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-10:00";
		tz.daylight_UTCOffsetHours = "-09:00";
		tz.std_Code = "HST";
		tz.std_Description = "Hawaii-Aleutian Standard Time";
		tz.daylight_Code = "HDT";
		tz.daylight_Description = "Hawaii-Aleutian Daylight Time";
		FOTimeZones.put("America/Adak", tz);
		FOTimeZones.put("US/Aleutian", tz);

		tz = new FOTimeZone();
		tz.startDate = "1900-01-01";
		tz.endDate = "9999-12-31";
		tz.std_UTCOffsetHours = "-10:00";
		tz.daylight_UTCOffsetHours = "-10:00";
		tz.std_Code = "SST";
		tz.std_Description = "Samoa Standard Time";
		tz.daylight_Code = "";
		tz.daylight_Description = "";
		FOTimeZones.put("US/Samoa", tz);
	}
}

class DataObject {
	Boolean is_empty;
	def DataObject() {
		is_empty = true;
	}

	public void checkObject() {
		def properties = this.properties.sort()*.key;
		for (p in properties) {
			if (p in ["metaClass", "class", "is_empty"]) continue;
			def val = this.getProperty(p);

			if (val instanceof String && val != "") {
				is_empty = false;
			} else if (val instanceof ArrayList) {
				is_empty = (val.size() == 0);
			} else if (val instanceof DataObject) {
				is_empty = val.is_empty;
			} else if (val == null) {
				is_empty = true;
			}
			
			if (is_empty == false) return;
		}
	}
}

class Name extends DataObject {
	String a003_FirstName;
	String a006_MiddleName1;
	String a009_MiddleName2;
	String a012_LastName;
	String a015_AcademicTitle;
	String a018_Suffix;
	String a021_Prefix;
	String a021_FullName;
}

class Picklist extends DataObject {
	String a003_Code;
	String a006_Description;
	void fillPicklist(def code, def desc) {
		a003_Code = (code == "" ? null : code);
		a006_Description = (desc =="" ? null : desc);
	}

	void fillPicklistV2(Domain d, def code, String picklistId, String startDate) {
		a003_Code = (code == "" ? null : code);
		if (a003_Code == null) return;
		FOListOption op = d.findPicklistValue(picklistId, a003_Code, startDate);
		if (op != null) {
			a006_Description = op.description;
		}
	}
	
	void fillPicklistWithDomain(Domain d, HashMap map, def code, String startDate) {
		a003_Code = (code == "" ? null : code);
		if (a003_Code == null) return;
		FOObject data = d.findDomainObject(map, a003_Code, startDate);
		a006_Description = (data == null) ? null : data.description;
	}

}

class Country extends DataObject {
	String a003_Name;
	String a006_ShortName;
	String a009_ISO2CharacterCode;
	String a012_ISO3CharacterCode;
	String a015_ISO3DigitCode;
}

class IdentityDocument extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_Number;
	String a006_Description;
	String a009_Issuer;
	Country a012_IssuingCountry;
	String a015_IssueDate;
	String a018_EffectiveDate;
	String a021_ExpirationDate;
	String a024_ValidationDate;
	IdentityDocument a027_PreviousIdentityDocument;
}

class Person extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a004_EndDate;
	String a005_BusinessPartnerIdentifier;
	Name a006_Name;
	Name a007_PreferredName;
	Picklist a009_Gender;
	IdentityDocument a012_IdentityDocument; //IdentityValidation
	String a015_BirthDate;
	Country a018_Nationality;
	Picklist a021_MaritalStatus; //Picklist
	PreferredLanguage a022_PreferredLanguage;
	Person a024_PreviousPerson;
}

class EmploymentStatus extends DataObject {
	String a000_Action;
	String a003_Code;
	String a006_Description;
	String a009_StartDate;
	String a012_EndDate;
	EmploymentStatus a015_PreviousStatus;
}

class PreferredLanguage extends DataObject {
	String a003_Name;
	String a006_ISO2CharacterLanguageCode;
	String a009_ISO3CharacterLanguageCode;
}

class Company extends DataObject {
	String a003_Number;
	String a006_Name;
	Country a009_Country;
}

class Subdivision extends DataObject {
	String a003_abbreviatedName;
	String a006_ISOCode;
	String a009_Name;
	String a012_Category;
}

class TimeZone extends DataObject {
	Picklist a003_StandardName;
	Picklist a006_LocalName;
	String a009_UTCOffsetHours;
	String a012_DSTStartDate;
	String a015_DSTEndDate;
}

class Address extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	Picklist a009_Purpose;
	String a012_Line1;
	String a015_Line2;
	String a018_Line3;
	String a021_Line4;
	String a024_Line5;
	String a027_Line6;
	String a030_City;
	Subdivision a033_MinorSubdivision;
	Subdivision a036_MajorSubdivision;
	String a039_PostalCode;
	Country a042_Country;
	String a045_Latitude;
	String a048_Longitude;
	String a051_GeoText;
	TimeZone a054_StandardTimeZone;
	Address a057_PreviousAddress;
	TimeZone a060_DaylightSavingsTimeZone;
}

class Telephone extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	Picklist a009_Purpose;
	String a012_ITUCode;
	String a015_AreaCode;
	String a018_LocalNumber;
	String a021_ExtensionNumber;
	TimeZone a024_TimeZone;
	Country a027_Country;
	Telephone a030_PreviousTelephone;
}

class Email extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	Picklist a009_Purpose;
	String a012_Address;
	String a015_PrimaryIndicator;
	Email a018_PreviousEmail;
}

class Event extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	Picklist a009_EventCategory;
	Picklist a012_EventReason;
	Event a015_PreviousEvent;
}

class LinkedEntity extends DataObject {
	String a003_Number;
	String a006_Name;
}

class Location extends DataObject {
	String a003_Number;
	String a006_Name;
	Picklist a009_LocationMajorClassification;
	Picklist a012_LocationMinorClassification;
	Address a015_PhysicalAddress;
	Company a018_Company;
	LinkedEntity a021_CompanyTerritory;
	LinkedEntity a024_Division;
	LinkedEntity a027_Region;
	LinkedEntity a030_District;
	LinkedEntity a033_GovernanceAgreement;
}

class Position extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	String a009_Code;
	String a012_Description;
	Picklist a015_Job;
	String a018_SupervisorEmployeeNumber;
	Location a021_Location;
	Picklist a024_CostCenter;
	Picklist a027_Department;
	Picklist a030_PayScaleCategory;
	Picklist a033_PayScaleArea;
	Picklist a036_PayScaleGroup;
	Picklist a039_PayScaleLevel;
	Picklist a042_PersonnelArea;
	Picklist a045_PersonnelSubArea;
	Picklist a048_EmployeeGroup;
	Picklist a051_EmployeeSubGroup;
	AnnualSalary a052_AnnualSalary;
	String a054_WeeklyWorkHours;
	String a057_AnnualWorkHours;
	Picklist a060_WorkScheduleRule;
	Picklist a063_GovernanceAgreement;
	Position a066_PreviousPosition;
}

class Currency extends DataObject {
	String a003_Name;
	String a006_ISO3CharacterCurrencyCode;
	String a009_ISO3DigitCurrencyCode;
}

class AnnualSalary extends DataObject {
	Currency a003_Currency;
	String a006_Amount;
}

class Pay extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_StartDate;
	String a006_EndDate;
	Picklist a009_WageCategory;
	String a012_Amount;
	Currency a015_Currency;
	Pay a021_PreviousPay;
}

class Employee extends DataObject {
	String a000_Action;
	String a001_SourceDateTime;
	String a003_EmployeeNumber;
	ArrayList a006_Person; //of class Person
	Picklist a009_ActivityStatus;
	ArrayList a012_EmploymentStatus; //of class EmploymentStatus
	PreferredLanguage a015_PreferredLanguage;
	//Company a018_Company; // not from EC
	ArrayList a018_IdentityDocument;
	String a021_OriginalHireDate;
	String a024_AdjustedHireDate;
	String a025_MostRecentHireDate;
	String a027_ProbationEndDate;
	String a030_SeniorityDate;
	String a033_ServiceDate;
	String a036_TerminationDate;
	String a039_PayrollClerkIndicator;
	String a042_ProbationEndIndicator;
	String a045_FlexTimeIndicator;
	String a048_RehireIndicator;
	String a051_FirstAidQualifiedIndicator;
	String a054_CSRPIndicator;
	String a057_UGRPIndicator;
	ArrayList a060_Address;
	ArrayList a063_Telephone;
	ArrayList a066_Email;
	ArrayList a069_Event;
	ArrayList a072_Position;
	ArrayList a075_Pay;
	Employee a078_PreviousEmployee;
}

class Node {
	Object parent; //parent object
	Object tag; //xml tag of the node
	Object data; // an object contain data
	Boolean isTail;
	String nodeName;
}

class XMLCreationHelper {
	def writer = new StringWriter();
	def marker = new MarkupBuilder(writer);
	
	public String getCurrent(Object xmlNode) {
		String result = null;
		for (node in xmlNode) {
			def children = node.getAt(0).children;
			for (child in children) {
				result = child;
				result = result.trim();
				return result;
			}
		}
		return result;
	}
	
	public String getPrevious(Object xmlNode) {
		String result = null;
		for (node in xmlNode) {
			def children = node.getAt(0).children;
			for (child in children) {
				if (child instanceof java.lang.String) continue;
				result = child.text();
				result = result.trim();
				
			}
		}
		return result;
		
	}
	
	public String formatSourceTime(def input) {
		String result = input;
		if (result.length() == 0) return null;
		result = result.substring(0, 19) + "Z";
		return result;
	}
	
	public void generateXML(Object data) {
		def tag;
		def toProcessNodeStack = new ArrayList(); //a FIFO queue
		def openingNodeStack = new ArrayList(); // a FILO queue
		
		def node = new Node();
		node.parent = null;
		node.tag = null;
		node.data = data;
		node.nodeName = data.getClass().name;
		node.isTail = true;
		
		toProcessNodeStack.push(node);
		// using deep first search on all (nested) attributes of the data to build the XML tree
		while (toProcessNodeStack.size() > 0) {
			Node current = toProcessNodeStack.remove(0);
			
			if (current.data == null) {
				//tag = marker.createNode(current.nodeName);
				//marker.nodeCompleted(null, tag);
				while (current.isTail && openingNodeStack.size() > 0) {//close the opening tag
					node = openingNodeStack.pop();
					//print "node poped out "; println node.nodeName;
					marker.nodeCompleted(null, node.tag);
					current = node;
				}
			} else if (current.data instanceof String ) { //simple string element, generate tag
				//print "create tag:"; println current.nodeName;
				if (!current.data.equals("")) {
					tag = marker.createNode(current.nodeName, current.data);
					marker.nodeCompleted(null, tag);
				}
				while (current.isTail && openingNodeStack.size() > 0) {//close the opening tag
					node = openingNodeStack.pop();
					//print "node poped out "; println node.nodeName;
					marker.nodeCompleted(null, node.tag);
					current = node;
				}
			} else if (current.data instanceof ArrayList){ // list of elements, add to stack
				def list = new ArrayList();
				current.data.each {
					def parent = openingNodeStack.last();
					node = new Node();
					node.parent = parent;
					node.data = it;
					node.tag = null;
					node.nodeName = it.getClass().name;
					node.isTail = false;
					list.push(node);
				}
				if (list.size() > 0) {
					if (current.isTail) {
						current.isTail = false;
						node.isTail = true;
					}
					toProcessNodeStack.addAll(0, list);
				} else { // empty list, the re-process it next time so that parent node can be poped
					current.data = null;
					toProcessNodeStack.add(0, current);
				}
			} else { //an object element, push all properties into the stack
				def properties = current.data.properties.sort()*.key;
				def list = new ArrayList();
 

				properties.each {
					if (it in ["metaClass", "class", "is_empty"]) return;
					node = new Node();
					node.parent = current;
					node.data = current.data.getProperty(it);
					node.nodeName = (String)it.substring(5);
					node.tag = null; // no tag has been created as this node has not been processed yet
					node.isTail = false;
					list.push(node);
				}
				node.isTail = true;
				//println current.nodeName;
				if (!current.data.is_empty) {
					// create opening tag of the object
					tag = marker.createNode(current.nodeName);
					current.tag = tag;
					toProcessNodeStack.addAll(0, list);
					openingNodeStack.push(current);
				} else {
					current.data = null;
					toProcessNodeStack.add(0, current);
				}
			}
		}
	}
}


def Message processData(Message message) {
	
	//Body
	String inXML = message.getBody(java.lang.String) as String;
	def body = new XmlSlurper().parseText(inXML).declareNamespace(multimap:'http://sap.com/xi/XI/SplitAndMerge');
	
	//Headers
	def map = message.getHeaders();
	
	//Properties
	map = message.getProperties();
	def mass_change = map.get("MASS_CHANGE");
	String FTSD = map.get("FULL_TRANSMISSION_START_DATE");
	//message.setProperty("oldProperty", value + "modified");

	def XMLCreationHelper helper = new XMLCreationHelper();
	def Domain domain = new Domain();
	def tmpEE = new Employee();
	 
	//parse domain values
	domain.parse(body);
	ArrayList employees = new ArrayList();
	
	//parse compound employees
	def compoundEEs = body.'multimap:Message1'.queryCompoundEmployeeResponse.CompoundEmployee;
	compoundEEs.each { //compoundEEs
		
		def eeOut = new Employee();
		String pernr = it.person.logon_user_id;
		
		employees.add(eeOut);
		
		eeOut.a078_PreviousEmployee = new Employee();
		
		// extract person related information
		String birthDate = helper.getCurrent(it.person.date_of_birth);
		String prevBirthDate = it.person.date_of_birth.previous;
		
		// extract address
		eeOut.a060_Address = new ArrayList();
		for (def a in it.person.address_information) {
			Address addr = new Address();
			Address prev_addr = new Address();
			addr.a000_Action = a.action;
			addr.a001_SourceDateTime = helper.formatSourceTime(a.last_modified_on);
			addr.a003_StartDate = a.start_date;
			addr.a006_EndDate = a.end_date;
			if (addr.a006_EndDate < FTSD) continue;
			prev_addr.a009_Purpose = new Picklist();
			prev_addr.a009_Purpose.checkObject();
			prev_addr.a009_Purpose.fillPicklistV2(domain, a.address_type.previous, "addressType", addr.a003_StartDate);
			prev_addr.a009_Purpose.checkObject()
			prev_addr.a012_Line1 = a.address1.previous;
			prev_addr.a015_Line2 = a.address2.previous;
			prev_addr.a018_Line3 = a.address3.previous;
			prev_addr.a021_Line4 = a.address4.previous;
			prev_addr.a024_Line5 = a.address5.previous;
			prev_addr.a027_Line6 = a.address6.previous;
			prev_addr.a030_City = a.city.previous;
			prev_addr.a039_PostalCode = a.zip_code.previous;
			prev_addr.a042_Country = new Country();
			prev_addr.a042_Country.a012_ISO3CharacterCode = a.country.previous;
			FOObject country_data = domain.findPicklistValue("csfCountry", prev_addr.a042_Country.a012_ISO3CharacterCode, addr.a003_StartDate);
			prev_addr.a042_Country.a003_Name = (country_data == null) ? null : country_data.description;

			prev_addr.a042_Country.checkObject();
			prev_addr.a036_MajorSubdivision = new Subdivision();
			prev_addr.a036_MajorSubdivision.a006_ISOCode = a.state.previous;
			if (prev_addr.a042_Country.a012_ISO3CharacterCode == "CAN") {
				prev_addr.a036_MajorSubdivision.a012_Category = "Province";
			}
			prev_addr.a036_MajorSubdivision.checkObject();
			prev_addr.checkObject();
			addr.a057_PreviousAddress = prev_addr;
			
			addr.a009_Purpose = new Picklist();
			addr.a009_Purpose.checkObject();
			addr.a009_Purpose.fillPicklistV2(domain, helper.getCurrent(a.address_type), "addressType", addr.a003_StartDate);
			addr.a009_Purpose.checkObject()
			addr.a012_Line1 = helper.getCurrent(a.address1);
			addr.a015_Line2 = helper.getCurrent(a.address2);
			addr.a018_Line3 = helper.getCurrent(a.address3);
			addr.a021_Line4 = helper.getCurrent(a.address4);
			addr.a024_Line5 = helper.getCurrent(a.address5);
			addr.a027_Line6 = helper.getCurrent(a.address6);
			addr.a030_City = helper.getCurrent(a.city);
			addr.a039_PostalCode = helper.getCurrent(a.zip_code);
			addr.a042_Country = new Country();
			addr.a042_Country.a012_ISO3CharacterCode = helper.getCurrent(a.country);
			country_data = domain.findPicklistValue("csfCountry", addr.a042_Country.a012_ISO3CharacterCode, addr.a003_StartDate);
			addr.a042_Country.a003_Name = (country_data == null) ? null : country_data.description;
			addr.a042_Country.checkObject();
			addr.a036_MajorSubdivision = new Subdivision();
			addr.a036_MajorSubdivision.a006_ISOCode = helper.getCurrent(a.state);
			if (addr.a042_Country.a012_ISO3CharacterCode == "CAN") {
				addr.a036_MajorSubdivision.a012_Category = "Province";
			}
			addr.a036_MajorSubdivision.checkObject();
			addr.a036_MajorSubdivision.a012_Category 
			addr.checkObject();
			
			if (prev_addr.is_empty && addr.a000_Action == "CHANGE") addr.is_empty = true;
			
			if (!addr.is_empty)
				eeOut.a060_Address.push(addr);
		} // end of person.address_information
		
		//extract telephone
		eeOut.a063_Telephone = new ArrayList();
		for (def ph in it.person.phone_information) {
			String rawNumber = null;
			Telephone p = new Telephone();
			prev_phone = new Telephone();
			prev_phone.a009_Purpose = new Picklist();
			prev_phone.a009_Purpose.fillPicklistV2(domain, ph.phone_type.previous, "ecPhoneType", "2001-01-01");
			prev_phone.a009_Purpose.checkObject();
			rawNumber = ph.phone_number.previous;
			rawNumber = rawNumber.replaceAll( "[^\\d]", "" );
			if (rawNumber.length() > 10) rawNumber = rawNumber.substring(0,10);
			
			if (rawNumber.length() == 7) {
				prev_phone.a018_LocalNumber = rawNumber;
			} else if (rawNumber.length() == 10) {
				prev_phone.a018_LocalNumber = rawNumber.substring(3);
				prev_phone.a015_AreaCode = rawNumber.substring(0,3);
			}
			prev_phone.checkObject();
			p.a030_PreviousTelephone = prev_phone;
			
			p.a000_Action = ph.action;
			p.a001_SourceDateTime = helper.formatSourceTime(ph.last_modified_on);
			p.a009_Purpose = new Picklist();
			p.a009_Purpose.fillPicklistV2(domain, helper.getCurrent(ph.phone_type), "ecPhoneType", "2001-01-01");
			p.a009_Purpose.checkObject();
			rawNumber = helper.getCurrent(ph.phone_number);
			rawNumber = rawNumber.replaceAll( "[^\\d]", "" );
			if (rawNumber.length() > 10) rawNumber = rawNumber.substring(0,10);
			
			if (rawNumber.length() == 7) {
				p.a018_LocalNumber = rawNumber;
			} else if (rawNumber.length() == 10) {
				p.a018_LocalNumber = rawNumber.substring(3);
				p.a015_AreaCode = rawNumber.substring(0,3);
			}

			p.checkObject();

			if (prev_phone.is_empty && p.a000_Action == "CHANGE") p.is_empty = true;
			
			if (!p.is_empty)
				eeOut.a063_Telephone.push(p);
		} // end of person.phone_information
		
		//extract email
		eeOut.a066_Email = new ArrayList();
		for (def e in it.person.email_information) {
			Email email = new Email();
			Email prev_email = new Email();
			prev_email.a009_Purpose = new Picklist();
			prev_email.a009_Purpose.fillPicklistV2(domain, e.email_type.previous, "ecEmailType", "2001-01-01")
			prev_email.a009_Purpose.checkObject();
			prev_email.a012_Address = e.email_address.previous;
			prev_email.a015_PrimaryIndicator = e.isPrimary.previous;
			prev_email.checkObject();
			email.a018_PreviousEmail = prev_email;

			email.a000_Action = e.action;
			email.a001_SourceDateTime = helper.formatSourceTime(e.last_modified_on);
			email.a009_Purpose = new Picklist();
			email.a009_Purpose.fillPicklistV2(domain, helper.getCurrent(e.email_type), "ecEmailType", "2001-01-01");
			email.a009_Purpose.checkObject();
			email.a012_Address = helper.getCurrent(e.email_address);
			email.a015_PrimaryIndicator = helper.getCurrent(e.isPrimary);
			email.checkObject();

			if (prev_email.is_empty && email.a000_Action == "CHANGE") email.is_empty = true;
			
			if (!email.is_empty) {
				eeOut.a066_Email.push(email);
			}
		} // end of person.email_information
				
		// extract person information
		eeOut.a006_Person = new ArrayList();
		PreferredLanguage curr_lang = null;
		PreferredLanguage prev_lang = null;
		for (def p in it.person.personal_information) {
			// only extract the top most record
			FOObject domain_data;
			Person pOut = new Person();
			pOut.a006_Name = new Name();
			pOut.a007_PreferredName = new Name();
			pOut.a021_MaritalStatus = new Picklist();
			pOut.a009_Gender = new Picklist();
			pOut.a018_Nationality = new Country();
			//pOut.a022_PreferredLanguage = new PreferredLanguage();
			pOut.a000_Action = p.action;
			pOut.a001_SourceDateTime = helper.formatSourceTime(p.last_modified_on);
			pOut.a003_StartDate = p.start_date;
			pOut.a004_EndDate = p.end_date;
			if (pOut.a004_EndDate < FTSD) continue;	
			
			if (pOut.a004_EndDate == '9999-12-31') {
				curr_lang = new PreferredLanguage();
				curr_lang.a006_ISO2CharacterLanguageCode = helper.getCurrent(p.native_preferred_lang);
				domain_data = domain.findPicklistValue("prefLanguage", curr_lang.a006_ISO2CharacterLanguageCode, '2016-01-01');
				curr_lang.a003_Name = (domain_data == null) ? null : domain_data.description;
				curr_lang.checkObject();
				//pOut.a022_PreferredLanguage = curr_lang;	
			}		
			pOut.a005_BusinessPartnerIdentifier = pernr;
			pOut.a015_BirthDate = birthDate;
			pOut.a006_Name.a021_FullName = helper.getCurrent(p.formal_name);
			pOut.a006_Name.a003_FirstName = helper.getCurrent(p.first_name);
			pOut.a006_Name.a012_LastName = helper.getCurrent(p.last_name);
			pOut.a006_Name.a006_MiddleName1 = helper.getCurrent(p.middle_name);
			pOut.a006_Name.a015_AcademicTitle = helper.getCurrent(p.name_prefix);
			pOut.a006_Name.checkObject();
			pOut.a007_PreferredName.a012_LastName = helper.getCurrent(p.second_last_name);
			pOut.a007_PreferredName.checkObject();
			pOut.a021_MaritalStatus.fillPicklistV2(domain, helper.getCurrent(p.marital_status), "ecMaritalStatus", pOut.a003_StartDate);
			pOut.a021_MaritalStatus.checkObject();
			pOut.a009_Gender.fillPicklistV2(domain, helper.getCurrent(p.gender), "Gender", pOut.a003_StartDate);
			pOut.a009_Gender.checkObject();
			pOut.a018_Nationality.a012_ISO3CharacterCode = helper.getCurrent(p.nationality);
			domain_data = domain.findPicklistValue("csfCountry", pOut.a018_Nationality.a012_ISO3CharacterCode, pOut.a003_StartDate);
			pOut.a018_Nationality.a003_Name = (domain_data == null) ? null : domain_data.description;
			pOut.a018_Nationality.checkObject();
			//pOut.a012_IdentityDocument = doc;
			// extract previous values
			Person prevPerson = new Person();
			prevPerson.a006_Name = new Name();
			prevPerson.a007_PreferredName = new Name();
			prevPerson.a021_MaritalStatus = new Picklist();
			prevPerson.a009_Gender = new Picklist();
			prevPerson.a018_Nationality = new Country();
			
			if (pOut.a004_EndDate == '9999-12-31') {
				prev_lang = new PreferredLanguage();
				prev_lang.a006_ISO2CharacterLanguageCode = p.native_preferred_lang.previous;
				domain_data = domain.findPicklistValue("prefLanguage", prev_lang.a006_ISO2CharacterLanguageCode, '2016-01-01');
				prev_lang.a003_Name = (domain_data == null) ? null : domain_data.description;
				prev_lang.checkObject();
				//prevPerson.a022_PreferredLanguage = prev_lang;
			}
			prevPerson.a015_BirthDate = prevBirthDate;
			prevPerson.a006_Name.a021_FullName = p.formal_name.previous;
			prevPerson.a006_Name.a003_FirstName = p.first_name.previous;
			prevPerson.a006_Name.a012_LastName = p.last_name.previous;
			prevPerson.a006_Name.a006_MiddleName1 = p.middle_name.previous;
			prevPerson.a006_Name.a015_AcademicTitle = p.name_prefix.previous;
			prevPerson.a006_Name.checkObject();
			prevPerson.a007_PreferredName.a012_LastName = p.second_last_name.previous;
			prevPerson.a007_PreferredName.checkObject();
			prevPerson.a021_MaritalStatus.fillPicklistV2(domain, p.marital_status.previous, "ecMaritalStatus", pOut.a003_StartDate);
			prevPerson.a021_MaritalStatus.checkObject();
			prevPerson.a009_Gender.fillPicklistV2(domain, p.gender.previous, "Gender", pOut.a003_StartDate);
			prevPerson.a009_Gender.checkObject();
			prevPerson.a018_Nationality.a012_ISO3CharacterCode = p.nationality.previous;
			domain_data = domain.findPicklistValue("csfCountry", prevPerson.a018_Nationality.a012_ISO3CharacterCode, pOut.a003_StartDate);
			prevPerson.a018_Nationality.a003_Name = (domain_data == null) ? null : domain_data.description;
			prevPerson.a018_Nationality.checkObject();
			prevPerson.checkObject();
			pOut.a024_PreviousPerson = prevPerson;
			pOut.checkObject();
			
			if (prevPerson.is_empty && pOut.a000_Action == "CHANGE") pOut.is_empty = true;
					
			eeOut.a006_Person.push(pOut);
		} // end of person.personal_information
		
		// extract national id
		IdentityDocument doc = new IdentityDocument();
		IdentityDocument prev_doc = new IdentityDocument();
		for (def id in it.person.national_id_card) {
			doc.a000_Action = id.action;
			doc.a001_SourceDateTime = helper.formatSourceTime(id.last_modified_on);
			doc.a003_Number = helper.getCurrent(id.national_id);
			if (helper.getCurrent(id.country) == "CAN") {
				doc.a006_Description = "SocialInsuranceNumber";//helper.getCurrent(id.card_type);
			}
		
			prev_doc.a003_Number = id.national_id.previous;
			prev_doc.checkObject();
			if (!prev_doc.is_empty) {
				prev_doc.a006_Description = "Social Insurance Number";				
			}
			doc.a027_PreviousIdentityDocument = prev_doc;
			
			doc.checkObject();
			
			if (prev_doc.is_empty && doc.a000_Action == "CHANGE") doc.is_empty = true;
			
			// create an empty Person node just as the parent of IdentityDocument node
			if (!doc.is_empty) {
				Person emptyP = new Person();
				if (doc.a000_Action == "INSERT" ) {
					if (eeOut.a006_Person.size() > 0) {
						Person top = eeOut.a006_Person.get(0);
						if (top.a000_Action == "INSERT") emptyP = top;
					}
				}
				emptyP.a012_IdentityDocument = doc;
				emptyP.checkObject();
				if (emptyP.a000_Action == null)
					eeOut.a006_Person.push(emptyP);
			}
		}
		
		eeOut.a072_Position = new ArrayList();
		eeOut.a012_EmploymentStatus = new ArrayList();
		eeOut.a069_Event = new ArrayList();
		//ArrayList docs = new ArrayList();
		def no_emp_info = true;
		for (def emp in it.person.employment_information) {
			//extract flags
			no_emp_info = false;
			//for (def flag in emp.cust_employeeFlags.cust_employeeFlagsItems) {
			//	if (flag.flagName == "CSRP") eeOut.a054_CSRPIndicator = "true";
			//}
		
			for (def job_event in emp.job_event_information) {
				FOObject domain_data;
				// extract events
				Event event = new Event();
				Event prev_event = new Event();
				event.a000_Action = job_event.action;
				event.a001_SourceDateTime = helper.formatSourceTime(job_event.created_on_timestamp);
				event.a003_StartDate = job_event.event_date;
				
				prev_event.a009_EventCategory = new Picklist();
				prev_event.a009_EventCategory.fillPicklistV2(domain, job_event.event.previous, "event", event.a003_StartDate);
				prev_event.a009_EventCategory.checkObject();
				prev_event.a012_EventReason = new Picklist();
				prev_event.a012_EventReason.fillPicklistWithDomain(domain, domain.FOEventReasons, job_event.event_reason.previous, event.a003_StartDate);
				prev_event.a012_EventReason.checkObject();
				prev_event.checkObject();
				event.a015_PreviousEvent = prev_event;
		
				event.a009_EventCategory = new Picklist();
				event.a009_EventCategory.fillPicklistV2(domain, helper.getCurrent(job_event.event), "event", event.a003_StartDate);
				event.a009_EventCategory.checkObject();
				event.a012_EventReason = new Picklist();
				event.a012_EventReason.fillPicklistWithDomain(domain, domain.FOEventReasons, helper.getCurrent(job_event.event_reason), event.a003_StartDate);
				event.a012_EventReason.checkObject();
				event.checkObject();
				
				if (!event.is_empty)
					eeOut.a069_Event.push(event);
			}
			
			//eeOut.a030_SeniorityDate = emp
			String tmp_country = null;
			
			for (def job in emp.job_information) {
				FOObject domain_data;
				
				tmp_country = null;
				for (Event ev in eeOut.a069_Event) {
					String job_start_date = job.start_date;
					String curr_event = helper.getCurrent(job.event);
					String curr_reason = helper.getCurrent(job.event_reason);
					if (ev.a003_StartDate == job_start_date && ev.a009_EventCategory.a003_Code == curr_event &&
						ev.a012_EventReason.a003_Code == curr_reason ) {
						ev.a006_EndDate = job.end_date;
					}
				}
				// extract employment status
				EmploymentStatus status = new EmploymentStatus();
				EmploymentStatus prev_status = new EmploymentStatus();
				
				status.a009_StartDate = job.start_date;
				status.a012_EndDate = job.end_date;
				
				if (job.end_date == '9999-12-31') {
					eeOut.a054_CSRPIndicator = helper.getCurrent(job.custom_string17);
					if (eeOut.a054_CSRPIndicator == "N") {
						eeOut.a054_CSRPIndicator = "false";
					} else {
						eeOut.a054_CSRPIndicator = "true";
					}
					eeOut.a078_PreviousEmployee.a054_CSRPIndicator = job.custom_string17.previous;
					if (eeOut.a078_PreviousEmployee.a054_CSRPIndicator == "N") {
						eeOut.a078_PreviousEmployee.a054_CSRPIndicator = "false";						
					} else {
						eeOut.a078_PreviousEmployee.a054_CSRPIndicator = "true";
					}
				}
				
				prev_status.a003_Code = job.emplStatus.previous;
				domain_data = domain.findPicklistValue("employee-status", prev_status.a003_Code, status.a009_StartDate);
				prev_status.a006_Description = domain_data == null ? null : domain_data.description;
				prev_status.checkObject();
				status.a015_PreviousStatus = prev_status;
				
				status.a000_Action = job.action;
				status.a003_Code = helper.getCurrent(job.emplStatus);
				domain_data = domain.findPicklistValue("employee-status", status.a003_Code, status.a009_StartDate);
				status.a006_Description = domain_data == null ? null : domain_data.description;
				status.checkObject();
				if (prev_status.is_empty && status.a000_Action.equals("CHANGE")) {
					status.is_empty = true;
					status.a000_Action = 'NO_CHANGE';
				}
				
				if (!status.is_empty)
					eeOut.a012_EmploymentStatus.push(status);
		
				// extract position information
				Position pos = new Position();
				pos.a000_Action = job.action;
				pos.a001_SourceDateTime = helper.formatSourceTime(job.last_modified_on);
				pos.a003_StartDate = job.start_date;
				pos.a006_EndDate = job.end_date;
				//get position
				pos.a009_Code = helper.getCurrent(job.position);
				domain_data = domain.findDomainObject(domain.FOPositions, pos.a009_Code, pos.a003_StartDate);
				pos.a012_Description = domain_data == null ? null : domain_data.description;
				pos.a054_WeeklyWorkHours = helper.getCurrent(job.standard_hours);
				//get job
				pos.a015_Job = new Picklist();
				pos.a015_Job.fillPicklistWithDomain(domain, domain.FOJobs, helper.getCurrent(job.job_code), pos.a003_StartDate);
				pos.a015_Job.a006_Description = domain_data == null ? null : domain_data.description;
				pos.a015_Job.checkObject();
				pos.a018_SupervisorEmployeeNumber = helper.getCurrent(job.manager_id);
				//extract location
				Location loc = new Location();
				pos.a021_Location = loc;
				loc.a003_Number = helper.getCurrent(job.location);
				domain_data = domain.findDomainObject(domain.FOLocations, loc.a003_Number, pos.a003_StartDate);
				loc.a006_Name = domain_data == null ? null : domain_data.description;
				loc.a012_LocationMinorClassification = new Picklist();
				loc.a012_LocationMinorClassification.a003_Code = domain_data == null ? null : domain_data.subType;
				loc.a012_LocationMinorClassification.a006_Description = domain_data == null ? null : domain_data.subTypeDesc;
				loc.a012_LocationMinorClassification.checkObject()
				loc.a009_LocationMajorClassification = new Picklist();
				loc.a009_LocationMajorClassification.a003_Code = domain_data == null ? null : domain_data.type;
				loc.a009_LocationMajorClassification.a006_Description = domain_data == null ? null : domain_data.typeDesc;
				loc.a009_LocationMajorClassification.checkObject();
				// location address
				loc.a015_PhysicalAddress = new Address();				
				if (domain_data != null) {
					FOLocation fo_loc = domain_data;
					loc.a015_PhysicalAddress.a042_Country = new Country();
					loc.a015_PhysicalAddress.a042_Country.a003_Name = fo_loc.country;
					loc.a015_PhysicalAddress.a042_Country.checkObject();
					loc.a015_PhysicalAddress.a012_Line1 = fo_loc.addressLine1;
					loc.a015_PhysicalAddress.a015_Line2 = fo_loc.city;
					loc.a015_PhysicalAddress.a039_PostalCode = fo_loc.zipcode;
					if (fo_loc.country.toUpperCase() == 'CANADA') {
						loc.a015_PhysicalAddress.a036_MajorSubdivision = new Subdivision();
						loc.a015_PhysicalAddress.a036_MajorSubdivision.a012_Category = "Province";
						loc.a015_PhysicalAddress.a036_MajorSubdivision.a009_Name = fo_loc.province;
						loc.a015_PhysicalAddress.a036_MajorSubdivision.checkObject();
					}
				}
				//standard time zone
				loc.a015_PhysicalAddress.a054_StandardTimeZone = new TimeZone();
				loc.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName = new Picklist();
				loc.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.a006_Description = helper.getCurrent(job.timezone);
				loc.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.checkObject();
				FOTimeZone tzone = domain.FOTimeZones.get(loc.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.a006_Description);
				loc.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName = new Picklist();
				if (tzone != null) {
					loc.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.a003_Code = tzone.std_Code;
					loc.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.a006_Description = tzone.std_Description;
					loc.a015_PhysicalAddress.a054_StandardTimeZone.a009_UTCOffsetHours = tzone.std_UTCOffsetHours;	
				}					
				loc.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.checkObject();
				loc.a015_PhysicalAddress.a054_StandardTimeZone.checkObject();
				//daylight timezone
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone = new TimeZone();
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName = new Picklist();
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName.a003_Code = helper.getCurrent(job.timezone);
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName.checkObject();
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName = new Picklist();
				if (tzone != null) {
					loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.a003_Code = tzone.daylight_Code;
					loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.a006_Description = tzone.daylight_Description;
					loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a009_UTCOffsetHours = tzone.daylight_UTCOffsetHours;
				}
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.checkObject();
				loc.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.checkObject();
				loc.a015_PhysicalAddress.checkObject();				
				// get company
				loc.a018_Company = new Company();
				loc.a018_Company.a003_Number = helper.getCurrent(job.company);
				domain_data = domain.findDomainObject(domain.FOCompanies, loc.a018_Company.a003_Number, pos.a003_StartDate);
				loc.a018_Company.a006_Name = domain_data == null ? null : domain_data.description;
				loc.a018_Company.a009_Country = new Country();
				//loc.a018_Company.a009_Country.a012_ISO3CharacterCode = helper.getCurrent(job.company_territory_code);
				//domain_data = domain.findPicklistValue("csfCountry", loc.a018_Company.a009_Country.a012_ISO3CharacterCode, pos.a003_StartDate);
				loc.a018_Company.a009_Country.a003_Name = (domain_data == null) ? null : domain_data.territoryName;	
				loc.a018_Company.a009_Country.a012_ISO3CharacterCode = (domain_data == null) ? null : domain_data.territoryCode;
				loc.a018_Company.a009_Country.checkObject();
				loc.a018_Company.checkObject();
				loc.a021_CompanyTerritory = new LinkedEntity();
				loc.a021_CompanyTerritory.a003_Number = helper.getCurrent(job.company_territory_code);
				tmp_country = loc.a021_CompanyTerritory.a003_Number;
				domain_data = domain.findPicklistValue("csfCountry", loc.a021_CompanyTerritory.a003_Number, pos.a003_StartDate);
				loc.a021_CompanyTerritory.a006_Name = (domain_data == null) ? null : domain_data.description;
				loc.a021_CompanyTerritory.checkObject();
				// get division
				loc.a024_Division = new LinkedEntity();
				loc.a024_Division.a003_Number = helper.getCurrent(job.division);
				domain_data = domain.findDomainObject(domain.FODivisions, loc.a024_Division.a003_Number, pos.a003_StartDate);
				loc.a024_Division.a006_Name = domain_data == null ? null : domain_data.description;
				loc.a024_Division.checkObject();
				// get region
				loc.a027_Region = new LinkedEntity();
				loc.a027_Region.a003_Number = helper.getCurrent(job.custom_string2);
				domain_data = domain.findDomainObject(domain.FORegions, loc.a027_Region.a003_Number, pos.a003_StartDate);
				loc.a027_Region.a006_Name = domain_data == null ? null : domain_data.description;
				loc.a027_Region.checkObject();
				// get district
				loc.a030_District = new LinkedEntity();
				loc.a030_District.a003_Number = helper.getCurrent(job.custom_string3);
				domain_data = domain.findDomainObject(domain.FODistricts, loc.a030_District.a003_Number, pos.a003_StartDate);
				loc.a030_District.a006_Name = domain_data == null ? null : domain_data.description;
				loc.a030_District.checkObject();
				// get cost center
				loc.checkObject();
				pos.a024_CostCenter = new Picklist();
				pos.a024_CostCenter.fillPicklistWithDomain(domain, domain.FOCostCenters, helper.getCurrent(job.cost_center), pos.a003_StartDate);
				pos.a024_CostCenter.checkObject();
				// get department
				pos.a027_Department = new Picklist();
				pos.a027_Department.fillPicklistWithDomain(domain, domain.FODepartments, helper.getCurrent(job.department), pos.a003_StartDate);
				pos.a027_Department.checkObject();
				pos.a030_PayScaleCategory = new Picklist();
				pos.a030_PayScaleCategory.fillPicklistWithDomain(domain, domain.FOPayscaleTypes, helper.getCurrent(job.payScaleType), pos.a003_StartDate);
				pos.a030_PayScaleCategory.checkObject();
				pos.a033_PayScaleArea = new Picklist();
				pos.a033_PayScaleArea.fillPicklistWithDomain(domain, domain.FOPayscaleAreas, helper.getCurrent(job.payScaleArea), pos.a003_StartDate);
				pos.a033_PayScaleArea.checkObject();
				pos.a036_PayScaleGroup = new Picklist();
				pos.a036_PayScaleGroup.fillPicklistWithDomain(domain, domain.FOPayscaleGroups, helper.getCurrent(job.payScaleGroup), pos.a003_StartDate);
				pos.a036_PayScaleGroup.checkObject();
				pos.a039_PayScaleLevel = new Picklist();
				pos.a039_PayScaleLevel.fillPicklistWithDomain(domain, domain.FOPayscaleLevels, helper.getCurrent(job.payScaleLevel), pos.a003_StartDate);
				pos.a039_PayScaleLevel.checkObject();
				pos.a042_PersonnelArea = new Picklist();
				pos.a042_PersonnelArea.fillPicklistV2(domain, helper.getCurrent(job.custom_string15), "personnelArea", pos.a003_StartDate);
				pos.a042_PersonnelArea.checkObject();
				pos.a045_PersonnelSubArea = new Picklist();
				pos.a045_PersonnelSubArea.fillPicklistV2(domain, helper.getCurrent(job.custom_string16), "personnelSubarea", pos.a003_StartDate);
				pos.a045_PersonnelSubArea.checkObject();
				pos.a048_EmployeeGroup = new Picklist();
				pos.a048_EmployeeGroup.fillPicklistV2(domain, helper.getCurrent(job.employee_class), "EMPLOYEECLASS", pos.a003_StartDate);
				pos.a048_EmployeeGroup.checkObject();
				pos.a051_EmployeeSubGroup = new Picklist();
				pos.a051_EmployeeSubGroup.fillPicklistV2(domain, helper.getCurrent(job.employment_type), "employmentType", pos.a003_StartDate);
				pos.a051_EmployeeSubGroup.checkObject();
				pos.a052_AnnualSalary = new AnnualSalary();
				pos.a052_AnnualSalary.a006_Amount = helper.getCurrent(job.custom_double2);
				pos.a052_AnnualSalary.a003_Currency = new Currency();
				if (tmp_country == "CAN") {
					pos.a052_AnnualSalary.a003_Currency.a006_ISO3CharacterCurrencyCode = "CAD";
					domain_data = domain.findPicklistValue("currency", pos.a052_AnnualSalary.a003_Currency.a006_ISO3CharacterCurrencyCode, pos.a003_StartDate);
					pos.a052_AnnualSalary.a003_Currency.a003_Name = domain_data.description;
					pos.a052_AnnualSalary.a003_Currency.is_empty = false;
				}
				tmp_country = null;
				pos.a052_AnnualSalary.checkObject();
				pos.a060_WorkScheduleRule = new Picklist();
				pos.a060_WorkScheduleRule.fillPicklistWithDomain(domain, domain.FOWorkSchedules, helper.getCurrent(job.workschedule_code), pos.a003_StartDate);
				pos.a060_WorkScheduleRule.checkObject();
		
				// extract prev_position information
				Position prev_pos = new Position();
				//prev_pos.a003_StartDate = job.start_date;
				//prev_pos.a006_EndDate = job.end_date;
				prev_pos.a009_Code = job.position.previous;
				domain_data = domain.findDomainObject(domain.FOPositions, prev_pos.a009_Code, pos.a003_StartDate);
				prev_pos.a012_Description = domain_data == null ? null : domain_data.description;
				prev_pos.a054_WeeklyWorkHours = job.standard_hours.previous;				
				//prev job
				prev_pos.a015_Job = new Picklist();
				prev_pos.a015_Job.fillPicklistWithDomain(domain, domain.FOJobs, job.job_code.previous, pos.a003_StartDate);
				prev_pos.a015_Job.checkObject();
				prev_pos.a018_SupervisorEmployeeNumber = job.manager_id.previous;
				//get location
				prev_pos.a021_Location = new Location();
				prev_pos.a021_Location.a003_Number = job.location.previous;
				domain_data = domain.findDomainObject(domain.FOLocations, prev_pos.a021_Location.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a006_Name = domain_data == null ? null : domain_data.description;
				//location type/subtype
				prev_pos.a021_Location.a012_LocationMinorClassification = new Picklist();
				prev_pos.a021_Location.a012_LocationMinorClassification.a003_Code = domain_data == null ? null : domain_data.subType;
				prev_pos.a021_Location.a012_LocationMinorClassification.a006_Description = domain_data == null ? null : domain_data.subTypeDesc;
				prev_pos.a021_Location.a012_LocationMinorClassification.checkObject();
				prev_pos.a021_Location.a009_LocationMajorClassification = new Picklist();
				prev_pos.a021_Location.a009_LocationMajorClassification.a003_Code = domain_data == null ? null : domain_data.type;
				prev_pos.a021_Location.a009_LocationMajorClassification.a006_Description = domain_data == null ? null : domain_data.typeDesc;
				prev_pos.a021_Location.a009_LocationMajorClassification.checkObject();
				// location address
				prev_pos.a021_Location.a015_PhysicalAddress = new Address();
				if (domain_data != null) {
					FOLocation fo_loc = domain_data;
					prev_pos.a021_Location.a015_PhysicalAddress.a042_Country = new Country();
					prev_pos.a021_Location.a015_PhysicalAddress.a042_Country.a003_Name = fo_loc.country;
					prev_pos.a021_Location.a015_PhysicalAddress.a042_Country.checkObject();
					prev_pos.a021_Location.a015_PhysicalAddress.a012_Line1 = fo_loc.addressLine1;
					prev_pos.a021_Location.a015_PhysicalAddress.a015_Line2 = fo_loc.city;
					prev_pos.a021_Location.a015_PhysicalAddress.a039_PostalCode = fo_loc.zipcode;
					if (fo_loc.country.toUpperCase() == 'CANADA') {
						prev_pos.a021_Location.a015_PhysicalAddress.a036_MajorSubdivision = new Subdivision();
						prev_pos.a021_Location.a015_PhysicalAddress.a036_MajorSubdivision.a012_Category = "Province";
						prev_pos.a021_Location.a015_PhysicalAddress.a036_MajorSubdivision.a009_Name = fo_loc.province;
						prev_pos.a021_Location.a015_PhysicalAddress.a036_MajorSubdivision.checkObject();
					}
				}
				
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone = new TimeZone();
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName = new Picklist();
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.a006_Description = job.timezone.previous;
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.checkObject();

				tzone = domain.FOTimeZones.get(prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a006_LocalName.a006_Description);
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName = new Picklist();
				if (tzone != null) {
					prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.a003_Code = tzone.std_Code;
					prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.a006_Description = tzone.std_Description;
					prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a009_UTCOffsetHours = tzone.std_UTCOffsetHours;
				}
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.a003_StandardName.checkObject();
				prev_pos.a021_Location.a015_PhysicalAddress.a054_StandardTimeZone.checkObject();
				//daylight timezone
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone = new TimeZone();
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName = new Picklist();
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName.a003_Code = job.timezone.previous;
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a006_LocalName.checkObject();
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName = new Picklist();
				if (tzone != null) {
					prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.a003_Code = tzone.daylight_Code;
					prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.a006_Description = tzone.daylight_Description;
					prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a009_UTCOffsetHours = tzone.daylight_UTCOffsetHours;
				}
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.a003_StandardName.checkObject();
				prev_pos.a021_Location.a015_PhysicalAddress.a060_DaylightSavingsTimeZone.checkObject();
				prev_pos.a021_Location.a015_PhysicalAddress.checkObject();
				//get company
				prev_pos.a021_Location.a018_Company = new Company();
				prev_pos.a021_Location.a018_Company.a003_Number = job.company.previous;
				domain_data = domain.findDomainObject(domain.FOCompanies, prev_pos.a021_Location.a018_Company.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a018_Company.a006_Name = domain_data == null ? null : domain_data.description;
				prev_pos.a021_Location.a018_Company.a009_Country = new Country();
				prev_pos.a021_Location.a018_Company.a009_Country.a012_ISO3CharacterCode = domain_data == null ? null : domain.territoryCode;
				prev_pos.a021_Location.a018_Company.a009_Country.a003_Name = domain_data == null ? null : domain.territoryName;
				prev_pos.a021_Location.a018_Company.a009_Country.checkObject();
				prev_pos.a021_Location.a018_Company.checkObject();
				prev_pos.a021_Location.a021_CompanyTerritory = new LinkedEntity();
				prev_pos.a021_Location.a021_CompanyTerritory.a003_Number = job.company_territory_code.previous;
				tmp_country = prev_pos.a021_Location.a021_CompanyTerritory.a003_Number;
				domain_data = domain.findPicklistValue("csfCountry", prev_pos.a021_Location.a021_CompanyTerritory.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a021_CompanyTerritory.a006_Name = (domain_data == null) ? null : domain_data.description;
				prev_pos.a021_Location.a021_CompanyTerritory.checkObject();
				// get division
				prev_pos.a021_Location.a024_Division = new LinkedEntity();
				prev_pos.a021_Location.a024_Division.a003_Number = job.division.previous;
				domain_data = domain.findDomainObject(domain.FODivisions, prev_pos.a021_Location.a024_Division.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a024_Division.a006_Name = domain_data == null ? null : domain_data.description;
				prev_pos.a021_Location.a024_Division.checkObject();
				// get region
				prev_pos.a021_Location.a027_Region = new LinkedEntity();
				prev_pos.a021_Location.a027_Region.a003_Number = job.custom_string2.previous;
				domain_data = domain.findDomainObject(domain.FORegions, prev_pos.a021_Location.a027_Region.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a027_Region.a006_Name = domain_data == null ? null : domain_data.description;
				prev_pos.a021_Location.a027_Region.checkObject();
				//get district
				prev_pos.a021_Location.a030_District = new LinkedEntity();
				prev_pos.a021_Location.a030_District.a003_Number = job.custom_string3.previous;
				domain_data = domain.findDomainObject(domain.FODistricts, prev_pos.a021_Location.a030_District.a003_Number, pos.a003_StartDate);
				prev_pos.a021_Location.a030_District.a006_Name = domain_data == null ? null : domain_data.description;
				prev_pos.a021_Location.a030_District.checkObject();
				prev_pos.a021_Location.checkObject();
				//get cost center
				prev_pos.a024_CostCenter = new Picklist();
				prev_pos.a024_CostCenter.fillPicklistWithDomain(domain, domain.FOCostCenters, job.cost_center.previous, pos.a003_StartDate);
				prev_pos.a024_CostCenter.checkObject();
				//get department
				prev_pos.a027_Department = new Picklist();
				prev_pos.a027_Department.fillPicklistWithDomain(domain, domain.FODepartments, job.department.previous, pos.a003_StartDate);
				prev_pos.a027_Department.checkObject();
				
				prev_pos.a030_PayScaleCategory = new Picklist();
				prev_pos.a030_PayScaleCategory.fillPicklistWithDomain(domain, domain.FOPayscaleTypes, job.payScaleType.previous, pos.a003_StartDate);
				prev_pos.a030_PayScaleCategory.checkObject();
				prev_pos.a033_PayScaleArea = new Picklist();
				prev_pos.a033_PayScaleArea.fillPicklistWithDomain(domain, domain.FOPayscaleAreas, job.payScaleArea.previous, pos.a003_StartDate);
				prev_pos.a033_PayScaleArea.checkObject();
				prev_pos.a036_PayScaleGroup = new Picklist();
				prev_pos.a036_PayScaleGroup.fillPicklistWithDomain(domain, domain.FOPayscaleGroups, job.payScaleGroup.previous, pos.a003_StartDate);
				prev_pos.a036_PayScaleGroup.checkObject();
				prev_pos.a039_PayScaleLevel = new Picklist();
				prev_pos.a039_PayScaleLevel.fillPicklistWithDomain(domain, domain.FOPayscaleLevels, job.payScaleLevel.previous, pos.a003_StartDate);
				prev_pos.a039_PayScaleLevel.checkObject();
				prev_pos.a042_PersonnelArea = new Picklist();
				prev_pos.a042_PersonnelArea.fillPicklistV2(domain, job.custom_string15.previous, "personnelArea", pos.a003_StartDate);
				prev_pos.a042_PersonnelArea.checkObject();
				prev_pos.a045_PersonnelSubArea = new Picklist();
				prev_pos.a045_PersonnelSubArea.fillPicklistV2(domain, job.custom_string16.previous, "personnelSubarea", pos.a003_StartDate);
				prev_pos.a045_PersonnelSubArea.checkObject();
				prev_pos.a048_EmployeeGroup = new Picklist();
				prev_pos.a048_EmployeeGroup.fillPicklistV2(domain, job.employee_class.previous, "EMPLOYEECLASS", pos.a003_StartDate);
				prev_pos.a048_EmployeeGroup.checkObject();
				prev_pos.a051_EmployeeSubGroup = new Picklist();
				prev_pos.a051_EmployeeSubGroup.fillPicklistV2(domain, job.employment_type.previous, "employmentType", pos.a003_StartDate);
				prev_pos.a051_EmployeeSubGroup.checkObject();
				prev_pos.a052_AnnualSalary = new AnnualSalary();
				prev_pos.a052_AnnualSalary.a006_Amount = job.custom_double2.previous;
				prev_pos.a052_AnnualSalary.a003_Currency = new Currency();
				if (tmp_country == "CAN") {
					prev_pos.a052_AnnualSalary.a003_Currency.a006_ISO3CharacterCurrencyCode = "CAD";
					domain_data = domain.findPicklistValue("currency", prev_pos.a052_AnnualSalary.a003_Currency.a006_ISO3CharacterCurrencyCode, pos.a003_StartDate);
					prev_pos.a052_AnnualSalary.a003_Currency.a003_Name = domain_data.description;
					prev_pos.a052_AnnualSalary.a003_Currency.is_empty = false;
				}
				prev_pos.a052_AnnualSalary.checkObject();
				prev_pos.a060_WorkScheduleRule = new Picklist();
				prev_pos.a060_WorkScheduleRule.fillPicklistWithDomain(domain, domain.FOWorkSchedules, job.workschedule_code.previous, pos.a003_StartDate);
				prev_pos.a060_WorkScheduleRule.checkObject();
				prev_pos.checkObject();
				pos.a066_PreviousPosition = prev_pos;
				pos.checkObject();

				if (prev_pos.is_empty && pos.a000_Action == "CHANGE") pos.is_empty = true;
				if (!pos.is_empty)
					eeOut.a072_Position.push(pos);
			} // end of emp.job_information
			
			//extract pay information
			eeOut.a075_Pay = new ArrayList();
			for (def comp in emp.compensation_information) {
				FOObject domain_data;
				for (def recur in comp.paycompensation_recurring) {
					Pay pay = new Pay();
					pay.a000_Action = recur.action;
					pay.a001_SourceDateTime = helper.formatSourceTime(recur.last_modified_on);
					pay.a003_StartDate = recur.start_date;
					pay.a006_EndDate = recur.end_date;
					if (pay.a006_EndDate < FTSD) continue;
					pay.a009_WageCategory = new Picklist();
					pay.a009_WageCategory.fillPicklistWithDomain(domain, domain.FOPaycomponents,helper.getCurrent(recur.pay_component), pay.a003_StartDate);
					pay.a009_WageCategory.checkObject();
					pay.a015_Currency = new Currency();
					pay.a015_Currency.a006_ISO3CharacterCurrencyCode = helper.getCurrent(recur.currency_code);
					domain_data = domain.findPicklistValue("currency", pay.a015_Currency.a006_ISO3CharacterCurrencyCode, pay.a003_StartDate);
					pay.a015_Currency.a003_Name = (domain_data == null) ? null : domain_data.description;
					pay.a015_Currency.checkObject();
					pay.a012_Amount = helper.getCurrent(recur.paycompvalue);
					
					Pay prev_pay = new Pay();
					prev_pay.a009_WageCategory = new Picklist();
					prev_pay.a009_WageCategory.fillPicklistWithDomain(domain, domain.FOPaycomponents,recur.pay_component.previous, pay.a003_StartDate);
					prev_pay.a009_WageCategory.checkObject();
					prev_pay.a015_Currency = new Currency();
					prev_pay.a015_Currency.a006_ISO3CharacterCurrencyCode = recur.currency_code.previous;
					domain_data = domain.findPicklistValue("currency", prev_pay.a015_Currency.a006_ISO3CharacterCurrencyCode, pay.a003_StartDate);
					prev_pay.a015_Currency.a003_Name = (domain_data == null) ? null : domain_data.description;					
					prev_pay.a015_Currency.checkObject();
					prev_pay.a012_Amount = recur.paycompvalue.previous;
					prev_pay.checkObject();
					pay.a021_PreviousPay = prev_pay;
					
					pay.checkObject();
					//if (prev_pay.is_empty && pay.a000_Action == "CHANGE") pay.is_empty = true;
					if (!pay.is_empty)
						eeOut.a075_Pay.push(pay);
				} // end of comp.paycompensation_recurring
			} // end of emp.payment_information
			
			//eeOut.a075_Pay, eeOut.a072_Position
			for (Pay pay in eeOut.a075_Pay) {
				for (Position pos in eeOut.a072_Position) {
					if (pay.a003_StartDate <= pos.a003_StartDate && pay.a006_EndDate >= pos.a006_EndDate) {
						pos.a052_AnnualSalary.a003_Currency = pay.a015_Currency;
						pos.a066_PreviousPosition.a052_AnnualSalary.a003_Currency = pay.a021_PreviousPay.a015_Currency;
					}
				}
				pay.a015_Currency = null;
			}
						
			eeOut.a078_PreviousEmployee.a021_OriginalHireDate = emp.originalStartDate.previous;
			eeOut.a078_PreviousEmployee.a024_AdjustedHireDate = emp.custom_date4.previous;
			eeOut.a078_PreviousEmployee.a025_MostRecentHireDate = emp.start_date.previous;
			eeOut.a078_PreviousEmployee.a027_ProbationEndDate = emp.custom_date23.previous;
			eeOut.a078_PreviousEmployee.a030_SeniorityDate = emp.seniorityDate.previous;
			eeOut.a078_PreviousEmployee.a015_PreferredLanguage = prev_lang;
			eeOut.a078_PreviousEmployee.a048_RehireIndicator = emp.okToRehire.previous;
			eeOut.a015_PreferredLanguage = curr_lang;
			eeOut.a048_RehireIndicator = helper.getCurrent(emp.okToRehire);
			eeOut.a078_PreviousEmployee.checkObject();
		
			//eeOut.checkObject();
			tmpEE.a021_OriginalHireDate = helper.getCurrent(emp.originalStartDate);
			tmpEE.a030_SeniorityDate = helper.getCurrent(emp.seniorityDate);
			tmpEE.a024_AdjustedHireDate = helper.getCurrent(emp.custom_date4);
			tmpEE.a025_MostRecentHireDate = helper.getCurrent(emp.start_date);
			tmpEE.a027_ProbationEndDate = helper.getCurrent(emp.custom_date23);
			tmpEE.a000_Action = helper.getCurrent(emp.action);
		} // end of person.employment_information
		
		eeOut.checkObject();
		eeOut.a003_EmployeeNumber = it.person.person_id_external;		
		if (no_emp_info == false) {
			eeOut.a021_OriginalHireDate = tmpEE.a021_OriginalHireDate;
			eeOut.a030_SeniorityDate = tmpEE.a030_SeniorityDate
			eeOut.a024_AdjustedHireDate = tmpEE.a024_AdjustedHireDate
			eeOut.a025_MostRecentHireDate = tmpEE.a025_MostRecentHireDate;
			eeOut.a027_ProbationEndDate = tmpEE.a027_ProbationEndDate;
			eeOut.a000_Action = tmpEE.a000_Action;
		} else {
			eeOut.a000_Action = helper.getCurrent(it.person.action);
		}
		if (helper.getCurrent(it.person.action) != "NO CHANGE" && eeOut.a00_Action == "NO CHANGE")
			eeOut.a000_Action = helper.getCurrent(it.person.action);
		eeOut.a001_SourceDateTime = helper.formatSourceTime(it.person.last_modified_on);
	} // end of CompoundEE
	
	// generate transformed XML
	employees.each {
		helper.generateXML(it);
	}
	if (helper.writer.toString().length() == 0) {
		message.setBody("<NODELTA></NODELTA>");
	} else {
		message.setBody("<Request>"+helper.writer.toString()+"</Request>");
	}
	
	return message;
}
