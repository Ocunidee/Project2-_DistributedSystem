package chat.server.handler;

import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.Account;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;



public class DeleteHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "delete";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String roomID = (String) in_message.get("roomid");
		ChatRoom chatroom = ConnectionsSupervisor.getChatRoomByID(roomID);
		if (chatroom == null){
			sender.sendMessage(new MessageHandler().newMessage(roomID + " does not exist.", "system"));
			return;
		}
		if (sender.equals(chatroom.getOwner())){
			// TODO if logout is implemented then we will have to check if the owner is authenticated, else not necessary
			// kick other clients out of deleted room
			for (Connection user : ConnectionsSupervisor.getChatRoomByID(roomID).getUserList()){
				JoinRoomHandler roomHandler = new JoinRoomHandler();
				String out_json = roomHandler.joinRoom(user, "MainHall", roomID);
				ConnectionsSupervisor.broadcast(out_json, "MainHall");
			}
			//remove room ownership and final delete room
			Account account = sender.getAccount();
			if (account != null)
				account.removeRoomOwnership(roomID);
			ConnectionsSupervisor.removeChatRoom(roomID);
			sender.sendMessage(new ListHandler().roomList());
		} else
			sender.sendMessage(new MessageHandler().newMessage("You are not the owner of " + roomID + ".", "system"));

	}

	public String getTYPE_KEY() {
		return TYPE_KEY;
	}

}
