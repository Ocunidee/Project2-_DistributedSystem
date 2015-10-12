package chat.common;

import java.util.Map;

import chat.server.Connection;

public interface CommandHandler {
	
	void handle(Map<String, Object> message, Connection sender, String currentRoomID);

	boolean accept(Map<String, Object> in_message);

	String getTYPE_KEY();

}
