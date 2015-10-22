package chat.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;

public class WhoHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "who";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String roomID = (String) in_message.get("roomid");
		sender.sendMessage(roomContents(roomID));
	}

	public String roomContents(String roomID) {
		Map<String, Object> out_message = new LinkedHashMap<String, Object>();
		ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
    	if (room != null) {
    		out_message.put("identities", room.getUsernameList());
    		out_message.put("owner", room.getOwnername());
    	} else {
    		return new MessageHandler().newMessage("Room " + roomID + " does not exist.", "system");
    	}
	    out_message.put("type", "roomcontents");
	    out_message.put("roomid", roomID);
	    JsonHandler jsonHandler = new JsonHandler();
	    return jsonHandler.marshall(out_message);

    }
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
