package chat.client.handler;
import java.util.Map;

import chat.client.TCPClient;
import chat.common.AbstractCommandHandler;
import chat.server.Connection;

public class MessageHandler extends AbstractCommandHandler {

	public static String handle(Map<String, Object> in_message){
		String message = in_message.get("identity") + ": " + in_message.get("content");
		if (!in_message.get("identity").equals(TCPClient.getUserName())){
			return message = "\n" + message;
		}
		else return message;
	}

    @Override
    public void handle(Map<String, Object> message, Connection sender, String currentRoomID) {
        
    }
	
}
