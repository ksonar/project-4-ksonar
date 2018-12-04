package UserServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import Logger.LogData;
/*
 * Start the Users Server and forward to appropriate APIs
 * @author ksonar
 */
public class UserStart {
	public static int port = 9000;
	private static String serverName = "users";
	
	public static void main(String[] args) throws Exception {
		LogData.createLogger(serverName);
		Server server = new Server(port);
		LogData.log.info(serverName + " server started on port " + port);
		
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		handler.addServletWithMapping(UserID.class, "/*");
		handler.addServletWithMapping(CreateUser.class, "/create");

		server.start();
		LogData.log.info("Server started");
		server.join();
		
	}
}
