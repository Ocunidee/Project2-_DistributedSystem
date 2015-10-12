package chat.server.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;


public class ListHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "list";

	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String roomID) {
	    sender.sendMessage(roomList());
	}

	public String roomList() {
		List<Map<String, Object>> rooms = new ArrayList<>();
		
		for (ChatRoom room : ConnectionsSupervisor.getChatRooms()){
			Map<String, Object> roomsInfo = new LinkedHashMap<String, Object>();
			roomsInfo.put("roomid", room.getRoomID());
			roomsInfo.put("count",ConnectionsSupervisor.count(room));
			rooms.add(roomsInfo);
		}
		
		Map<String, Object> out_message = new LinkedHashMap<String, Object>();
		out_message.put("type", "roomlist");
		out_message.put("rooms", rooms);
		JsonHandler jsonHandler = new JsonHandler();
		return jsonHandler.marshall(out_message);
		}
	
	
public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
