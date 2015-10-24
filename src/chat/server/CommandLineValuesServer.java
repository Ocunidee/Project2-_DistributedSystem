package chat.server;


	import org.kohsuke.args4j.Option;


public class CommandLineValuesServer {
			
	// Give it a default value of 4444 
	@Option(required = false, name = "-p", aliases = {"--port"}, usage="Port Address")
	private int port = 4444;

	public int getPort() {
		return port;
	}
	
	@Option(required = true, name = "-k", aliases = {"--keystore"}, usage="Keystore password, check README")
	private String password;

	public String getPassword() {
		return password;
	}

	
}
