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
			sender.sendMessage(new MessageHandler().newMessage("You are already logged in.", "system"));
			return;
		}
		String username = (String) in_message.get("username");
		String password = (String) in_message.get("password");
		Account account = ConnectionsSupervisor.getAccountByUsername(username);
		if (account != null && account.authenticate(password)){			
			setAccountsRooms(sender, account);
			setRoomsAccount(sender, account);
			sender.sendMessage(new MessageHandler().newMessage("Login successful", "system"));
			return;
		} else {
			sender.sendMessage(new MessageHandler().newMessage("Incorrect username or password", "system"));
			return;
		}
	}

	/*
	 * Look for rooms owned by Connection sender and add them to the account
	 */
	private void setAccountsRooms(Connection sender, Account account){
		for (ChatRoom room: ConnectionsSupervisor.getChatRooms()){
			if (room.getOwner().equals(sender)){
				String roomID = room.getRoomID();
				account.addRoomOwnership(roomID);
			}
		}
	}
	
	/*
	 * set the owner of all rooms listed under account to be sender
	 */
	private void setRoomsAccount(Connection sender, Account account){
		String[] ownedRooms = account.getOwnedRooms();
		for (int i = 0; i< ownedRooms.length; i++){
			ChatRoom room = ConnectionsSupervisor.getChatRoomByID(ownedRooms[i]);
			room.setOwner(sender);
		}
	}

	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
