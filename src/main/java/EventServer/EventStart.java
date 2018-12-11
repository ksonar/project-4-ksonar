package EventServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import Logger.LogData;
import db.Config;
import db.DBManager;
/*
 * Start the Events Server and forward to appropriate APIs
 * @author ksonar
 */
public class EventStart {
	public static int port = 8000;
	private static String serverName = "events";
	
	public static void main(String[] args) throws Exception {
		LogData.createLogger(serverName);
		Config.readConfig("config.json");
		Server server = new Server(port);
		LogData.log.info(serverName + " server started on port " + port);
		
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		handler.addServletWithMapping(EventID.class, "/*");
		handler.addServletWithMapping(EventList.class, "/list");
		handler.addServletWithMapping(CreateEvent.class, "/create");
		handler.addServletWithMapping(PurchaseTickets.class, "/purchase/*");
		server.start();
		LogData.log.info("Server started");
		server.join();
		
	}

	

}
