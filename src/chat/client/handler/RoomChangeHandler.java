package chat.client.handler;

import java.util.Map;

import chat.client.TCPClient;

public class RoomChangeHandler {

	public static String handle(Map<String, Object> in_message){
		if (in_message.get("identity").equals(TCPClient.getUserName())){
			TCPClient.setRoomID((String) in_message.get("roomid")); 
			if (in_message.get("roomid").equals("")){
				TCPClient.setQuit(true);
			}
		}
		if (in_message.get("former").equals("")){
			return in_message.get("identity") + " moves to MainHall";
		}
		else if (in_message.get("roomid").equals("")){
			return in_message.get("identity") + " leaves " + in_message.get("former");
		}
		else if (in_message.get("roomid").equals(in_message.get("former"))){
			return "The requested room is invalid or non existent.";
		}
		else return in_message.get("identity") + " moved from " + in_message.get("former") + " to " + in_message.get("roomid");
	}
	
}
