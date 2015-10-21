package chat.server.handler;

import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.server.Account;
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
			if(isValidUserName(username)){
				String password = (String) in_message.get("password");
				Account account = new Account(username, password, sender);
				//TODO find all rooms owned by sender and add to account
				//update screenname
			} else {
				//warn user username is taken
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
