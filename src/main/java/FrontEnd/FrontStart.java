package FrontEnd;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import Logger.LogData;
import db.Config;
import db.DBManager;

/*
 * Start the front end server for access to public]
 * @auhtor ksonar
 */
public class FrontStart {
	public static int port = 7070;
	private static String serverName = "front";
	
	public static void main(String[] args) throws Exception {
		LogData.createLogger(serverName);
		Server server = new Server(port);
		LogData.log.info(serverName + " server started on port " + port);
		
		ServletContextHandler handler = new ServletContextHandler();
		server.setHandler(handler);
		
		handler.addServlet(ListEvents.class, "/events");
		handler.addServlet(CreateEvent.class, "/events/create");
		handler.addServlet(EventID.class, "/events/*");
		handler.addServlet(PurchaseTickets.class, "/purchase/*");
		handler.addServlet(CreateUser.class, "/users/create");
		handler.addServlet(UserID.class, "/users/*");
		handler.addServlet(Transfer.class, "/tickets/transfer");

		server.start();
		LogData.log.info("Server started");
		server.join();
	}
}
