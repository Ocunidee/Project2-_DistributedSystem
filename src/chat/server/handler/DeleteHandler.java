package chat.server.handler;

import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;



public class DeleteHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "delete";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String roomID = (String) in_message.get("roomid");
		ChatRoom chatroom = ConnectionsSupervisor.getChatRoomByID(roomID);
		if (chatroom.getOwner().equals(sender)){
			if (){
				for (Connection user : ConnectionsSupervisor.getChatRoomByID(roomID).getUserList()){
					JoinRoomHandler roomHandler = new JoinRoomHandler();
					String out_json = roomHandler.joinRoom(user, "MainHall", roomID);
					ConnectionsSupervisor.broadcast(out_json, "MainHall");
				}
				ConnectionsSupervisor.removeChatRoom(roomID);
				sender.sendMessage(new ListHandler().roomList());
			}
		}
	}

	public String getTYPE_KEY() {
		return TYPE_KEY;
	}

}
