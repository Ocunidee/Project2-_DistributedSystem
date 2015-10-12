package chat.server;


	import org.kohsuke.args4j.Option;


public class CommandLineValuesServer {
			
	// Give it a default value of 4444 
	@Option(required = false, name = "-p", aliases = {"--port"}, usage="Port Address")
	private int port = 4444;

	public int getPort() {
		return port;
	}

	
}
