package UserServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
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
		
		ServletContextHandler handler = new ServletContextHandler();
		server.setHandler(handler);
		
		handler.addServlet(UserID.class, "/*");
		handler.addServlet(CreateUser.class, "/create");
		handler.addServlet(AddTickets.class, "/tickets/add");

		server.start();
		LogData.log.info("Server started");
		server.join();
		
	}
}
