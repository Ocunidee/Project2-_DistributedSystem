package chat.server.handler;

import java.util.Iterator;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;


public class QuitHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "quit";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		manage(sender, currentRoomID, false);
	}
	
	public void manage(Connection sender, String currentRoomID, boolean connectionBroken){
	    if (connectionBroken && "".equals(currentRoomID)) {
	        return;
	    }
		sender.setQuit(true);
		String clientMovingToEmptyRoomJson = new JoinRoomHandler().joinRoom(sender, "", currentRoomID);
		ConnectionsSupervisor.broadcast(clientMovingToEmptyRoomJson, currentRoomID);
		sender.sendMessage(clientMovingToEmptyRoomJson, true);
		
		{
    		Iterator<ChatRoom> iterator = ConnectionsSupervisor.getChatRooms().iterator();
    		while (iterator.hasNext()){
    			ChatRoom room = iterator.next();
    			if (sender.equals(room.getOwner())){
    				room.discardOwner();
    			}
    			if (!room.isOwnedByAccount() && room.getUserList().isEmpty() && room.getOwner() == null && !room.getRoomID().equals("MainHall")){
    				iterator.remove();
    			}
    		}
		}
		ConnectionsSupervisor.removeClient(sender);
	}

	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
}