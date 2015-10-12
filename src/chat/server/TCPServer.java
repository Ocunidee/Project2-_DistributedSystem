package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class TCPServer {
	
	public static void main (String args[]) {
		CommandLineValuesServer values = new CommandLineValuesServer();
		CmdLineParser parser = new CmdLineParser(values);
		
		ServerSocket listenSocket = null;
		ConnectionsSupervisor.init();
		
		try{
			parser.parseArgument(args);
			int serverPort = values.getPort(); 
			listenSocket = new ServerSocket(serverPort);

			while(true) {
				System.out.println("Server listening for a connection");
				Socket clientSocket = listenSocket.accept();
				
				System.out.println("Server status :");
				System.out.println("===============");
				System.out.println("clients : ");
                ConnectionsSupervisor.getClients().forEach(c -> System.out.println("- " + c.getUserName()));
                System.out.println("chatRooms : ");
                ConnectionsSupervisor.getChatRooms().forEach(c -> System.out.println("- " + c.getRoomID() + " (" + ")"));
                
				new Connection(clientSocket, null, "");

			}
		} catch(IOException e) {
			System.out.println("Listen socket:"+e.getMessage());
		} catch(CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(-1);
		}
		finally{ 
			try {
			    if (listenSocket != null) {
			        listenSocket.close();
			    }
			} catch (IOException e){}
		}
	}
}