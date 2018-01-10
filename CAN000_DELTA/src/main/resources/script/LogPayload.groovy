import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.lang.Exception;

def Message processData(Message message) {

	def pmap = message.getProperties();
	String enableLogging = pmap.get("ENABLE_LOGGING");
	String count = pmap.get("count");	
	String uuid = java.util.UUID.randomUUID().toString();
	
	message.setProperty("TRACKID", uuid);
	if(enableLogging != null && enableLogging.toUpperCase().equals("TRUE")){
		def body = message.getBody(java.lang.String) as String;
		def messageLog = messageLogFactory.getMessageLog(message);
		if(messageLog != null){
			messageLog.addAttachmentAsString("Input "+count+" ("+uuid+")", body, "text/xml");
		}
	}
	   
	return message;
}