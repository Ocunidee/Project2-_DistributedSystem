package chat.client;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;


public class CommandLineValuesClient {
		@Argument(index=0)
		private String host;
		
		@Option(name = "-p", aliases = {"--port"}, usage="Port Address")
		private int port = 4444;

		public int getPort() {
			return port;
		}

		public String getHost() {
			return host;
		}
	}
