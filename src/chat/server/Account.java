package chat.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Account {
	
	private String userName;
	private Connection currentUser;
	private String screenName;
//	private encPass;
//	private salt;
	private ArrayList<String> ownedRooms = new ArrayList<String>();
	
	public Account(String userName, String password, Connection clientConnection){
		this.userName = userName;
		this.setPassword(password);
		this.currentUser = clientConnection;
		//TODO find rooms owned by connection and add to ownedRooms
	}
	
	private void setPassword(String password){
		//TODO
		//generate salt
		//save salt to private variable salt
		//add salt to password
		//encrypt salted password
		//save encryption to encPass
		return;
	}
	
	public boolean authenticate(String password){
		//TODO
		//add salt to password
		//encrypt
		//check result matches 'encPass'
		return true;
	}
	
	public void addRoomOwnership(String roomID){
		ownedRooms.add(roomID);
	}
	
	public void removeRoomOwnership(String roomID){
		ownedRooms.remove(roomID);
	}
	
	public void logout(){
		currentUser = null;
	}

	public String getUsername() {
		return userName;
	}
	
	public boolean isLoggedIn() {
		return (currentUser == null);
	}
	
	public String getScreenName() {
		return screenName;
	}
	
	public void setScreenName(String newScreenName) {
		screenName = newScreenName;
	}
	
	public String[] getOwnedRooms(){
		String[] tmp = new String[ownedRooms.size()];
		return ownedRooms.toArray(tmp);
	}

}
