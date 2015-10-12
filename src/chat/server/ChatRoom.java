package chat.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
	
	private String roomID;
	private Map<String, Connection> userList = new ConcurrentHashMap<>();
	private Connection owner;
	
	public ChatRoom(String roomID, Connection sender){
		this.roomID = roomID;
		this.owner = sender;
		ConnectionsSupervisor.addChatRoom(this);
	}
	
	public Connection addUser(Connection c){
		userList.put(c.getUserName(), c);
		c.setRoomID(roomID);
		return c;
	}


	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public Collection<String> getUsernameList() {
		return userList.keySet();
	}
	
	public Collection<Connection> getUserList() {
        return userList.values();
    }

	public Connection getOwner() {
        return owner;
    }
	
	public String getOwnername() {
        return owner != null ? owner.getUserName() : "";
    }

	public void discardOwner() {
		this.owner = null;
	}

    public void removeUser(Connection client) {
        userList.values().remove(client);
    }

}
