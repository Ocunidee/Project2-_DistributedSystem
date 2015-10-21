package chat.server.handler;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;



public class JoinRoomHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "join";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		String newRoomID = (String) in_message.get("roomid");
		synchronized (ConnectionsSupervisor.getChatRooms()) {
			if (ConnectionsSupervisor.doesChatRoomExist(newRoomID) && !isBlacklisted(sender, newRoomID) && !ConnectionsSupervisor.getChatRoomByID(newRoomID).getUserList().contains(sender)){
				String out_json = joinRoom(sender, newRoomID, currentRoomID);
				
				if (ConnectionsSupervisor.getChatRoomByID(currentRoomID) != null) {
					ConnectionsSupervisor.broadcast(out_json, currentRoomID);
				}
				if (newRoomID != ""){
					ConnectionsSupervisor.broadcast(out_json, newRoomID);
				}
			}
			else sender.sendMessage(joinRoom(sender, currentRoomID, currentRoomID));
		}
		if (newRoomID.equals("MainHall")){
		    sender.sendMessage(new WhoHandler().roomContents("MainHall"));
		    sender.sendMessage(new ListHandler().roomList());
		}
	}

	public String joinRoom(Connection client, String roomID, String currentRoomID) {
		if(!currentRoomID.equals("")){
			final ChatRoom currentChatRoom = ConnectionsSupervisor.getChatRoomByID(currentRoomID);
            currentChatRoom.removeUser(client);
			if(currentChatRoom.getUserList().isEmpty() && currentChatRoom.getOwner() == null && !"MainHall".equals(currentRoomID) && !currentChatRoom.isOwnedByAccount()) {
			    ConnectionsSupervisor.removeChatRoom(currentRoomID);
            }
		}
		if(!roomID.equals("")){
			ConnectionsSupervisor.getChatRoomByID(roomID).addUser(client);
		}
		client.setRoomID(roomID);
		
		Map<String, Object> out_message = new LinkedHashMap<String, Object>();
		out_message.put("type", "roomchange");
		out_message.put("identity", client.getUserName());
		out_message.put("former", currentRoomID);
		out_message.put("roomid", roomID);
	    JsonHandler jsonHandler = new JsonHandler();
	    return jsonHandler.marshall(out_message);
    }
	
	public boolean isBlacklisted(Connection user, String roomID) {
		return user.getBlacklistedRooms().containsKey(roomID) && user.getBlacklistedRooms().get(roomID).after(new Date());
	}

	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
