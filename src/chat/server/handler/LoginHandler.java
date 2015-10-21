package chat.server.handler;

import java.util.Collection;
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
			sender.sendMessage(new MessageHandler().newMessage("Already logged in", "system"));
			return;
		}
		String username = (String) in_message.get("username");
		String password = (String) in_message.get("password");
		Account account = ConnectionsSupervisor.getAccountByUsername(username);
		if (account.authenticate(password)){			
			
			Collection<ChatRoom> chatRooms = ConnectionsSupervisor.getChatRooms();
			//TODO find all rooms owned by sender and add to account
			//account.addRoomOwnership()
			
			
			//update all rooms owned by account to have a reference to the new owner Connection object
			String[] ownedRooms = account.getOwnedRooms();
			for (int i = 0; i< ownedRooms.length; i++){
				ChatRoom room = ConnectionsSupervisor.getChatRoomByID(ownedRooms[i]);
				room.setOwner(sender);
			}
			sender.sendMessage(new MessageHandler().newMessage("Login successful", "system"));
			return;
		} else {
			sender.sendMessage(new MessageHandler().newMessage("Incorrect username or password", "system"));
			return;
		}
	}

	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
