package chat.client.handler;

import java.util.Map;

import chat.client.TCPClient;

public class NewIdentityHandler {

	public static String handle(Map<String, Object> in_message){
		if (in_message.get("former").equals(TCPClient.getUserName())){
			TCPClient.setUserName((String) in_message.get("identity"));
			if (in_message.get("former").equals("")){
				return "Connected to " + TCPClient.getMyHost() + " as " + in_message.get("identity") +".";
			}
			else if (in_message.get("former").equals(in_message.get("identity"))){
				return "Requested identity invalid or in use";
			}
		}
		return in_message.get("former")+ " is now " + in_message.get("identity");
	}
}
