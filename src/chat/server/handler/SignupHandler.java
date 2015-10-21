package chat.server.handler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.Account;
import chat.server.ChatRoom;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;

public class SignupHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "signup";
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID) {
		if(sender.getAccount() != null){
			sender.sendMessage(new MessageHandler().newMessage("You are already logged in.", "system"));
		}
		else{
			String username = (String) in_message.get("username");
			if(isValidUserName(username) && !ConnectionsSupervisor.isUserNameTaken(username)){
				String password = (String) in_message.get("password");
				try {
					Account account = new Account(username, password);
					setAccountsRooms(sender, account);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
				sender.sendMessage(new MessageHandler().newMessage("You have signed up successfully. You can now login", "system"));
			} else {
				sender.sendMessage(new MessageHandler().newMessage("The username you requested is already in use or doesn't respect the format.", "system"));
			}
		}
	}
	
	
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

	private boolean isValidUserName(String username) {
		boolean nameTaken = false;
		if (ConnectionsSupervisor.getAccountByUsername(username) != null)
			nameTaken = true;
		//TODO are these restrictions appropriate to an account name (username)?
		return username.matches("[a-zA-Z][a-zA-Z0-9]{2,15}") && !nameTaken;
	}
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
	
	
}
