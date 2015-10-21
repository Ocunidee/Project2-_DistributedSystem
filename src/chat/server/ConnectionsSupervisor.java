package chat.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import chat.common.AbstractCommandHandler;
import chat.common.CommandHandler;
import chat.common.JsonHandler;
import chat.server.handler.CreateRoomHandler;
import chat.server.handler.DeleteHandler;
import chat.server.handler.IdentityChangeHandler;
import chat.server.handler.JoinRoomHandler;
import chat.server.handler.KickHandler;
import chat.server.handler.ListHandler;
import chat.server.handler.LoginHandler;
import chat.server.handler.MessageHandler;
import chat.server.handler.QuitHandler;
import chat.server.handler.SignupHandler;
import chat.server.handler.WhoHandler;

public class ConnectionsSupervisor {

	private static Set<Connection> clients = ConcurrentHashMap.newKeySet();
	final static Set<Long> guestsID =  Collections.synchronizedSet(new TreeSet<Long>());
	final static Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<String, ChatRoom>();
	final static Map<String, Account> accounts = new ConcurrentHashMap<String, Account>();
	
	

	private static List<AbstractCommandHandler> handlers;
	
	public static void init(){
		handlers = new ArrayList<AbstractCommandHandler>();
		handlers.add(0, new MessageHandler());
		handlers.add(1, new QuitHandler());
		handlers.add(2, new IdentityChangeHandler());
		handlers.add(3, new JoinRoomHandler());
		handlers.add(4, new KickHandler());
		handlers.add(5, new DeleteHandler());
		handlers.add(6, new ListHandler());
		handlers.add(7, new CreateRoomHandler());
		handlers.add(8, new WhoHandler());
		handlers.add(9, new SignupHandler());
		handlers.add(10, new LoginHandler());
		
		addChatRooms("MainHall", null);
	}
	
	public static void addChatRooms(String roomID, Connection owner) {
	    System.out.println("Creating room: " + roomID + ", owner: " + owner);
	    new ChatRoom(roomID, owner);
	}
	

	
	public static void doWithJson(String in_json, Connection sender, String currentRoomID){
		JsonHandler jsonHandler = new JsonHandler();
		Map<String, Object> in_message = jsonHandler.unmarshall(in_json);
		CommandHandler handler = null;
		for(CommandHandler p : getHandlers()){
			if(p.accept(in_message)){
				handler = p;
			}
		}
		if (handler != null){
			handler.handle(in_message, sender, currentRoomID);
		}
	}
	
	/**
	 * Be careful, this method is called from the constructor of ChatRoom; 
	 * @param room
	 */
	public static void addChatRoom(ChatRoom room){
		chatRooms.put(room.getRoomID(), room);
	}


	public static int count(ChatRoom room){
		return room.getUserList().size();
	}


	public static List<AbstractCommandHandler> getHandlers() {
		return handlers;
	}
	
	
	public static Collection<Connection> getClients() {
		return clients;
	}
	
	
	public static Set<Long> getGuestsID() {
		return guestsID;
	}
	
	
	public static Collection<ChatRoom> getChatRooms() {
		return chatRooms.values();
	}
	
	public static ChatRoom getChatRoomByID(String roomID){
		return chatRooms.get(roomID);
	}
	
	public static Map<String, Account> getAccounts(){
		return accounts;
	}
	
	public static Account getAccountByUsername(String username){
		return accounts.get(username);
	}
	
	
	public static Connection getClientByUserName(String userName){
		for(Connection c : ConnectionsSupervisor.getClients()) {
			if (c.getUserName().equals(userName)) {
				return c;
			}
		}
		throw new RuntimeException("Client " + userName + " connection not found.");
	}
	
	
	public static void broadcast(String msg, String roomID){
	    Collection<Connection> users = roomID == null ? clients : chatRooms.get(roomID).getUserList();
		for (Connection user : users){
		    user.sendMessage(msg); 
		}
	}

    public static void removeClient(Connection sender) {
        clients.remove(sender);
        if (sender.getUserName().matches("guest[0-9]+")) {
            Long guestId = Long.valueOf(sender.getUserName().substring(5));
            guestsID.remove(guestId);
        }
    }

    public static boolean doesChatRoomExist(String roomID) {
        return chatRooms.containsKey(roomID);
    }

    public static void removeChatRoom(String roomID) {
        chatRooms.remove(roomID);
    }

    public static Long getNextAvailableGuestId() {
        synchronized (guestsID) {
            for (long i = 1; i<= guestsID.size() + 1; i++){
                if (!guestsID.contains(i)){
                    guestsID.add(i);
                    return i;
                }
            }
        }
        throw new RuntimeException("Was not able to find a guestId.");
    }

    public static void updateGuestIdOnNameChange(String oldUserName, String newUsername) {
        if (oldUserName.matches("guest[0-9]+")) {
            guestsID.remove(Long.valueOf(oldUserName.substring(5)));
        }
        
        if (newUsername.matches("guest[0-9]+")) {
            guestsID.add(Long.valueOf(newUsername.substring(5)));
        }
    }
	
}
