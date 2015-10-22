package chat.client.handler;

import java.util.ArrayList;
import java.util.Map;

public class RoomContentsHandler {

	@SuppressWarnings("unchecked")
	public static String handle(Map<String, Object> in_message){
		String userNames = "";
		
		if (((ArrayList<String>)in_message.get("identities")).isEmpty()){
			return (String) in_message.get("roomid") + " is empty.";
		}
		else
			for (String identity : (ArrayList<String>) in_message.get("identities")){
				userNames += " " + identity;
				if (identity.equals(in_message.get("owner"))){
					userNames += "*";
				}
			}
			return (String) in_message.get("roomid") + " contains" + userNames;
	}
	
}
