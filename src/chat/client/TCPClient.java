package chat.client;


import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import chat.client.handler.MessageHandler;
import chat.client.handler.NewIdentityHandler;
import chat.client.handler.RoomChangeHandler;
import chat.client.handler.RoomContentsHandler;
import chat.client.handler.RoomListHandler;
import chat.common.JsonHandler;


public class TCPClient {
	
	
	public static String userName = "";
	public static String roomID = "";
	public static String roomInCreation = "";
	public static boolean quit;
	public static String myHost;
	
	public static void main (String args[]) {
		
		SSLSocket s = null;
		CommandLineValuesClient values = new CommandLineValuesClient();
		CmdLineParser parser = new CmdLineParser(values);

		
		try{
			parser.parseArgument(args);
			TCPClient.setMyHost(values.getHost());
			int serverPort = values.getPort();
			String password = values.getPassword();
			String fileSep = System.getProperty("file.separator");
			File JKS = new File("Resources" + fileSep + "chatClientTrustStore.jks");
			char[] myPassword = password.toCharArray();
			System.out.println("myPassword " + myPassword.toString() + ", myHost: " + myHost + ", serverPort " + serverPort);
			s = setupSSL(getMyHost(), serverPort, myPassword, JKS);
			System.out.println(s.getSession().getProtocol());
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
			OutputStreamWriter out =new OutputStreamWriter( s.getOutputStream(),"UTF-8");
			consoleReader(out);
			while(userName == "" || roomID == "") {
				try {
					Thread.sleep(40);
					String connectData = in.readLine();
					System.out.println(doWithInJson(connectData));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			while(true){
				String in_json = in.readLine();
				System.out.println(doWithInJson(in_json));
				if (isQuit()) {
					break;
				}
				System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");	
			}
			System.out.println("Disconnected from " + TCPClient.getMyHost());
			in.close();
			out.close();
			s.close();
			System.exit(0);
			
		}catch (UnknownHostException e) {
			System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){
			System.out.println("readline:"+e.getMessage());
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(-1);
			
		}finally {
			if(s!=null){
				try {
					s.close();
				}catch (IOException e){
					System.out.println("close: "+e.getMessage());
				}
			}
		}	
	}
	


	static void consoleReader(OutputStreamWriter out){
		Scanner cmdIn = new Scanner(System.in);
		Runnable job = new Runnable(){
			public void run(){
				while (true){
					String message = cmdIn.nextLine();
					String out_json = "";
					if (!message.isEmpty()){
						if (message.charAt(0) == '#'){
							String command = message.substring(1);
							out_json = commandParser(command);
						}
						else if (message.startsWith("#quit")){
							cmdIn.close();
							break;
						}
						else out_json = messageParser(message);
					}	
					try {
						if (!out_json.isEmpty()){
							out.write(out_json + "\n");
							out.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		};
		new Thread(job).start();
	}
	
	
	
	private static String commandParser(String command){
		String delims = "[ ]+";
		String[] tokens = command.split(delims);
		String type = tokens[0];
		Map<String, Object> out_json = new LinkedHashMap<String, Object>();
		out_json.put("type", type);

		switch (type){
			case "identitychange":
				if (tokens.length <= 1){
					System.out.println("Missing argument. example : #identitychange newName");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else out_json.put("identity", tokens[1]);
				break;
			case "join":
				if (tokens.length <= 1){
					System.out.println("Missing argument. example : #join roomId");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else out_json.put("roomid", tokens[1]);
				break;
			case "who":
				if (tokens.length <= 1){
					System.out.println("Missing argument. example : #who roomId");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else out_json.put("roomid", tokens[1]);
				break;
			case "list":
				break;
			case "createroom":
				if (tokens.length <= 1 ){
					System.out.println("Missing argument. example : #createroom roomId");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else {
					out_json.put("roomid", tokens[1]);
					setRoomInCreation(tokens[1]);
				}
				break;
			case "kick":
				if (tokens.length <= 3){
					System.out.println("Missing argument. example : #kick userName roomId time");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else {
					out_json.put("identity", tokens[1]);
					out_json.put("roomid", tokens[2]);
					out_json.put("time", Integer.parseInt(tokens[3]));
				}
				break;
			case "delete":
				if (tokens.length <= 1){
					System.out.println("Missing argument. example : #delete roomId");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else out_json.put("roomid", tokens[1]);
				break;
			case "signup":
				if (tokens.length <= 2){
					System.out.println("Missing argument. example : #signup username password");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else {
					out_json.put("username", tokens[1]);
					out_json.put("password", tokens[2]);
				}
				break;
			case "login":
				if (tokens.length <= 2){
					System.out.println("Missing argument. example : #login username password");
					System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
					out_json.put("type", "invalid");
				}
				else {
					out_json.put("username", tokens[1]);
					out_json.put("password", tokens[2]);
				}
				break;
			case "quit":
			//	setQuit(true);
				break;
							
			default:
				System.out.println(("Invalid command. Valid commands are : " + "\n" + "#identitychange newName" + "\n" + "#join room" + "\n" + 
				"#who room" + "\n" + "#quit" + "\n" + "#list" + "\n" + "#createroom room"+  "\n" + "#kick room user duration_in_sec" + "\n" + "#delete room" + "\n" +"#signup username password" + "\n" + "#login username password"));
				out_json.put("type", "invalid");
				System.out.print("[" + getRoomID() + "] "+ getUserName() + "> ");
				break;
		}
		if (out_json.get("type").equals("invalid")){
			out_json.clear();
		}
		JsonHandler jsonHandler = new JsonHandler();
		return jsonHandler.marshall(out_json);		
	}

	
	
	
	
	static String messageParser(String message){
		Map<String, Object> out_message = new LinkedHashMap<String, Object>();
		out_message.put("type", "message");
		out_message.put("content", message);
		JsonHandler jsonHandler = new JsonHandler();
		return jsonHandler.marshall(out_message);
	}
	
	
	private static String doWithInJson(String in_json){
		JsonHandler jsonHandler = new JsonHandler();
		Map<String, Object> in_message = jsonHandler.unmarshall(in_json);
		String type = (String) in_message.get("type");
		String formattedMessage = "";
		switch (type){
			case "newidentity":
				formattedMessage = NewIdentityHandler.handle(in_message);
				break;
			case "roomchange":
				formattedMessage = RoomChangeHandler.handle(in_message);
				break;
			case "roomcontents":
				formattedMessage = RoomContentsHandler.handle(in_message);
				break;
			case "roomlist":
				formattedMessage = RoomListHandler.handle(in_message);
				break;
			case "message":
				formattedMessage = MessageHandler.handle(in_message);
				break;
		}	
		return formattedMessage;
	}


	public static void setUserName(String userName) {
		TCPClient.userName = userName;
	}


	public static void setRoomID(String roomID) {
		TCPClient.roomID = roomID;
	}


	public static String getUserName() {
		return userName;
	}


	public static String getRoomID() {
		return roomID;
	}


	public static String getRoomInCreation() {
		return roomInCreation;
	}


	public static void setRoomInCreation(String roomInCreation) {
		TCPClient.roomInCreation = roomInCreation;
	}


	public static void setQuit(boolean quit) {
		TCPClient.quit = quit;
	}
	
	public static String getMyHost() {
		return myHost;
	}

	
	public static void setMyHost(String myHost) {
		TCPClient.myHost = myHost;
	}


	public static boolean isQuit() {
		return quit;
	}
	
	
	private static SSLSocket setupSSL(String host, int port, char[] password, File JKS) {
		SSLSocket SSLsocket = null;	
		try {
			SSLContext sslContext = SSLContext.getInstance( "SSL" );
            KeyStore keyStore = KeyStore.getInstance("JKS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            
            keyStore.load(new FileInputStream(JKS),password);
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory f = sslContext.getSocketFactory();
            SSLsocket = (SSLSocket) f.createSocket(host,port);
		} catch (NoSuchAlgorithmException| KeyStoreException| 
				CertificateException| IOException|
				KeyManagementException e) {
			e.printStackTrace();
			System.out.println(e);
		} 
		return SSLsocket;
	}
	
}