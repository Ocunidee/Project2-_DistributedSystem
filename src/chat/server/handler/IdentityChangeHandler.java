package chat.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import chat.common.AbstractCommandHandler;
import chat.common.JsonHandler;
import chat.server.Connection;
import chat.server.ConnectionsSupervisor;



public class IdentityChangeHandler extends AbstractCommandHandler {

	public static final String TYPE_KEY = "identitychange";
	
	
	@Override
	public void handle(Map<String, Object> in_message, Connection sender, String currentRoomID){
		String newId = (String) in_message.get("identity");
		String oldUsername = sender.getUserName();
		if (isValidUserName(newId)){
			sender.setUserName(newId);
			ConnectionsSupervisor.updateGuestIdOnNameChange(oldUsername, newId);
			ConnectionsSupervisor.getChatRoomByID(currentRoomID).removeUser(sender);
			ConnectionsSupervisor.getChatRoomByID(currentRoomID).addUser(sender);
			ConnectionsSupervisor.broadcast(changeIdentity(oldUsername, newId), null);
		}
		else {
		    sender.sendMessage(changeIdentity(sender.getUserName(), sender.getUserName()));
		}
	}

	public String changeIdentity(String in_former, String in_newId) {
	    Map<String,Object> out_message = new LinkedHashMap<>();
	    out_message.put("type", "newidentity");
	    out_message.put("former", in_former);
	    out_message.put("identity", in_newId);
	    JsonHandler jsonHandler = new JsonHandler();
	    return jsonHandler.marshall(out_message);
    }
	
	private boolean isValidUserName(String name) {
		boolean nameTaken = false;
		boolean isSystem = false;
		if (name.equals("system")){
			isSystem = true;
		}
		for (Connection c : ConnectionsSupervisor.getClients()){
			if (c.getUserName().equals(name) ){
				nameTaken = true;
				break;
			}
		}
		return name.matches("[a-zA-Z][a-zA-Z0-9]{2,15}") && !nameTaken && !isSystem;
	}
	
	public String getTYPE_KEY() {
		return TYPE_KEY;
	}
	
}
