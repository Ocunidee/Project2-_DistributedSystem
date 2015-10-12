package chat.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.client.TCPClient;

public class RoomListHandler {

	@SuppressWarnings("unchecked")
	public static String handle(Map<String, Object> in_message){
		String roomList = "";
		String myRoom = TCPClient.getRoomInCreation();
		List<Map<String, Object>> rooms = (List<Map<String, Object>>) in_message.get("rooms");
		if(!TCPClient.getRoomInCreation().isEmpty()){
			for (Map<String, Object> room : rooms){
				if (room.get("roomid").equals(myRoom)){
					TCPClient.setRoomInCreation("");
					return "Room " + room.get("roomid") + " created.";
				}
			}
			TCPClient.setRoomInCreation("");
			return "Room " + myRoom + " is invalid or already in use.";
			
		}	
		else 
			for (Map<String, Object> roomsInfo : (ArrayList<Map<String, Object>>) in_message.get("rooms")){
			roomList += roomsInfo.get("roomid") + ": " + ((Double) roomsInfo.get("count")).intValue() + " guests" + "\n" ;
			}
			return roomList.substring(0, roomList.length()-1);
	}
	
}
