package chat.server.handler;


import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;



public class KickHandler extends AbstractCommandHandler{

	public static final String TYPE_KEY = "kick";

	@Override
    public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
	    String usernameToKick = (String) in_message.get("identity");
	    int time = ((Double) in_message.get("time")).intValue();
	    String roomID = (String) in_message.get("roomid");
	    ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
	    if (room == null){
	    	sender.sendMessage(new MessageHandler().newMessage("That room doesn't exist.", "system"));
	    	return;
	    }
	    Connection userToKick = ConnectionsSupervisor.getClientByUserName(usernameToKick);
	    if (userToKick == null){
	    	sender.sendMessage(new MessageHandler().newMessage("That user does not exist.", "system"));
	    	return;
	    }
	    if (room.getOwnername().equals(sender.getUserName()) && room.getUserList().contains(userToKick)){
	    	kick(roomID, userToKick, time, sender);
	    } else {
	        sender.sendMessage(new MessageHandler().newMessage("You do not own this room.", "system"));
	    }
    }
	
	public void kick(String roomID, Connection userToKick, int time, Connection sender) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, time);
		Date d = cal.getTime();
		userToKick.getBlacklistedRooms().put(roomID, d);
		
		String out_json = new JoinRoomHandler().joinRoom(userToKick, "MainHall", roomID);
		ConnectionsSupervisor.broadcast(out_json, "MainHall");
		ConnectionsSupervisor.broadcast(out_json, roomID);
		userToKick.sendMessage(new WhoHandler().roomContents("MainHall"));
		userToKick.sendMessage((new ListHandler().roomList()));
	}
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
