package chat.server.handler;


import java.util.LinkedHashMap;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;


public class MessageHandler extends AbstractCommandHandler{
	
	public static final String TYPE_KEY = "message";

	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String content = (String) in_message.get("content");
		ConnectionsSupervisor.broadcast(newMessage(content, sender.getUserName()), currentRoomID);
	}

    public String newMessage(String in_content, String in_identity) {
    	Map<String, Object> out_message = new LinkedHashMap<String, Object>();
		out_message.put("type", TYPE_KEY);
		out_message.put("identity", in_identity);
		out_message.put("content", in_content);
		JsonHandler jsonHandler = new JsonHandler();
		return jsonHandler.marshall(out_message);
    }

	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
}