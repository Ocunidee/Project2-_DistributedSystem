package chat.server.handler;


import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;


public class CreateRoomHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "createroom";
	

	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String new_roomID = (String) in_message.get("roomid");
		if (isValidRoomID(new_roomID)){
			new ChatRoom(new_roomID, sender);
			if(sender.getAccount() != null){
				sender.getAccount().addRoomOwnership(new_roomID);
			}
		}
		sender.sendMessage(createRoom());
	}
	
	private String createRoom(){
		return new ListHandler().roomList();
	}
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}

	private boolean isValidRoomID(String roomID) {
		return roomID.matches("[a-zA-Z]\\w{2,31}") && !ConnectionsSupervisor.doesChatRoomExist(roomID);
	}
	
}
