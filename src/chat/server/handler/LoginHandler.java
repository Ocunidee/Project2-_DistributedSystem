package chat.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.Account;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;

public class LoginHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "login";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		if(sender.getAccount() != null){
			//TODO warn sender they are already logged in
		}
		String username = (String) in_message.get("identity");
		String password = (String) in_message.get("password");
		Account account = ConnectionsSupervisor.getAccountByUsername(username);
		if (account.isLoggedIn())
			return;//TODO message client someone is on that account
		if (account.authenticate(password)){
			//TODO do a change id for client, newID is =account.getScreenName()
			
			//TODO find all rooms owned by sender and add to account
			
			//update all rooms owned by account to have a reference to the new owner Connection object
			String[] ownedRooms = account.getOwnedRooms();
			for (int i = 0; i< ownedRooms.length; i++){
				ChatRoom room = ConnectionsSupervisor.getChatRoomByID(ownedRooms[i]);
				room.setOwner(sender);
			}
			return;//let client know they logged in successfully?
		} else {
			return;//let client know username or password is incorrect
		}
	}

	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
