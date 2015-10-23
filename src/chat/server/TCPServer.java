package chat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

// hope it gets pushed
public class TCPServer {
	
	public static void main (String args[]) {
		CommandLineValuesServer values = new CommandLineValuesServer();
		CmdLineParser parser = new CmdLineParser(values);
		
		//ServerSocket listenSocket = null;
		SSLServerSocket sslServerSocket = null;
		ConnectionsSupervisor.init();
		
		try{
			parser.parseArgument(args);
			int serverPort = values.getPort(); 
			
			String fileSep = System.getProperty("file.separator");
			File JKS = new File("Resources" + fileSep + "chatsecure.jks");
			String password = "password";
			char[] myPassword = password.toCharArray();
			//listenSocket = new ServerSocket(serverPort);
			sslServerSocket = setupSSL(serverPort, JKS, myPassword);

			while(true) {
				System.out.println("Server listening for a connection");
				Socket clientSocket = sslServerSocket.accept();
				
				System.out.println("Server status :");
				System.out.println("===============");
				System.out.println("clients : ");
                ConnectionsSupervisor.getClients().forEach(c -> System.out.println("- " + c.getUserName()));
                System.out.println("chatRooms : ");
                ConnectionsSupervisor.getChatRooms().forEach(c -> System.out.println("- " + c.getRoomID() + " (" + ")"));
                
				new Connection(clientSocket, null, "");

			}
			
		} catch(IOException e) {
			System.out.println("Listen socket: "+e.getMessage());
		} catch(CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(-1);
		}
		finally{ 
			try {
			    if (sslServerSocket != null) {
			        sslServerSocket.close();
			    }
			} catch (IOException e){}
		}
	}
	
	
	
	private static SSLServerSocket setupSSL(int serverPort, File JKS,
			char[] serverPassword) {
		SSLContext SSLcontext = null;
		KeyManagerFactory keyManagerFactory = null;
		KeyStore keyStore = null;
		SSLServerSocket SSLserverSocket = null;

		
		try {
			SSLcontext = SSLContext.getInstance("SSL");
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(JKS), serverPassword);
			keyManagerFactory.init(keyStore, serverPassword);
			SSLcontext.init(keyManagerFactory.getKeyManagers(), null, null);
			SSLServerSocketFactory SSLserverSocketFactory = SSLcontext
					.getServerSocketFactory();
			SSLserverSocket = (SSLServerSocket) SSLserverSocketFactory
					.createServerSocket(serverPort);

		} catch (NoSuchAlgorithmException| KeyStoreException| 
				CertificateException| IOException|
				KeyManagementException| UnrecoverableKeyException e) {
			e.printStackTrace();
		} 
		return SSLserverSocket;
	}
	
}