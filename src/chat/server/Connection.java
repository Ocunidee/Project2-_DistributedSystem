package chat.server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import chat.server.handler.*;



public class Connection extends Thread {
	
	private BufferedReader in;
	private OutputStreamWriter out;
	private Socket clientSocket;
	private String userName;
	private String roomID;
	private Map<String, Date> blacklistedRooms = new HashMap<String, Date>();
	private boolean quit;
	
	
	public Connection (Socket aClientSocket, String userName, String roomID) {
		try {
			clientSocket = aClientSocket;
			setUserName(setFirstUserName());
			this.roomID = roomID;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));  
			out = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
			synchronized (ConnectionsSupervisor.getClients()) {
				ConnectionsSupervisor.getClients().add(this);
			}
			this.start();
		} catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		}
	}


	public void run(){
		try {        
			String id = ((IdentityChangeHandler) ConnectionsSupervisor.getHandlers().get(2)).changeIdentity("", this.getUserName());
			out.write(id + "\n");
			String room = ((JoinRoomHandler) ConnectionsSupervisor.getHandlers().get(3)).joinRoom(this, "MainHall", "");
			ConnectionsSupervisor.broadcast(room, "MainHall");
			String content = ((WhoHandler) ConnectionsSupervisor.getHandlers().get(8)).roomContents(this.getRoomID());
			String roomsList = ((ListHandler) ConnectionsSupervisor.getHandlers().get(6)).roomList();
			
			out.write(content + "\n");
			out.write(roomsList + "\n");
			out.flush();

			while(!isQuit()){
				String json = in.readLine();
				System.out.println("Receiving from " + getUserName() + " this message : " + json);
				ConnectionsSupervisor.doWithJson(json, this, this.getRoomID()); 
				out.flush();
			}
			
		} catch(EOFException e) {
			System.out.println("EOF:" + e.getMessage());
			new QuitHandler().manage(this, this.getRoomID(), true);
		} catch(IOException e) {
			System.out.println("readline:" + e.getMessage());
			new QuitHandler().manage(this, this.getRoomID(), true);
		} finally { 
			try {
				in.close();
				out.close();
				clientSocket.close();
			} catch (IOException e) {System.out.println("close failed");}
		}
	}
	
	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public String getUserName() {
		return userName;
	}

	public Map<String, Date> getBlacklistedRooms() {
		return blacklistedRooms;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		this.setName(userName);
	}
	
	public String setFirstUserName(){
		return "guest" + ConnectionsSupervisor.getNextAvailableGuestId();
	}
	
	public void sendMessage(String msg) {
		sendMessage(msg, false);
	}
	
	public synchronized void sendMessage(String msg, boolean overrideQuitStatus) {
		if (isQuit() && !overrideQuitStatus) {
			return;
		}
		try {
		    System.out.println("Writing to " + getUserName() + " this message : " + msg);
			out.write(msg +"\n");
			out.flush();
		} catch (IOException e) {
			System.out.println("Send message to " + getUserName() + " failed (IOException-" + e.getMessage() + ") (" + msg + "), (override: " + overrideQuitStatus + ")");
			new QuitHandler().manage(this, this.getRoomID(), true);
		}finally{ 
			
		}
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
}
