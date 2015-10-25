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
		if( account == null){
			sender.sendMessage(new MessageHandler().newMessage("Incorrect username or password", "system"));
			return;
		}
		if(checkLoggedin(account)){
			sender.sendMessage(new MessageHandler().newMessage("Someone is already logged into this account", "system"));
			return;
		}
		if( account.authenticate(password)){
			setAccountsRooms(sender, account);
			setRoomsAccount(sender, account);
			sender.sendMessage(new MessageHandler().newMessage("Login successful", "system"));
			return;
		} else {
			if(account.getFailedLoginAttempts() < account.getMAXLOGIN())
				sender.sendMessage(new MessageHandler().newMessage("Incorrect username or password. "
						+ "You have used " + account.getFailedLoginAttempts() + " out of " + account.getMAXLOGIN() 
						+ " maximum login attempts.", "system"));
			else
				sender.sendMessage(new MessageHandler().newMessage("This account is locked, "
						+ "please contact system admin to recover", "system"));
			return;
		}
	}

	/*
	 * Look for rooms owned by Connection sender and add them to the account
	 */
	private void setAccountsRooms(Connection sender, Account account){
		for (ChatRoom room: ConnectionsSupervisor.getChatRooms()){
			if (room.getOwner() != null){
				if (room.getOwner().equals(sender)){
					String roomID = room.getRoomID();
					account.addRoomOwnership(roomID);
				}
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

	private boolean checkLoggedin(Account account){
		for (Connection client: ConnectionsSupervisor.getClients()){
			if (account.equals(client.getAccount())){
				return true;
			}
		}
		return false;
	}
	
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
