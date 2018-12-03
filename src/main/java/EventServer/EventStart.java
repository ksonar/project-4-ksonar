package EventServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import Logger.LogData;
/*
 * Start the Events Server and forward to appropriate APIs
 * @author ksonar
 */
public class EventStart {
	public static int port = 8000;
	private static String serverName = "events";
	public static void main(String[] args) throws Exception {
		LogData.createLogger(serverName);
		Server server = new Server(port);
		LogData.log.info(serverName + " server started on port " + port);
		
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		handler.addServletWithMapping(EventID.class, "/*");
		handler.addServletWithMapping(EventList.class, "/list");
		handler.addServletWithMapping(CreateEvent.class, "/create");
		server.start();
		LogData.log.info("Server started");
		//LogData.log.info("MAPPING DONE, SERVER STARTED");
		server.join();
		
	}

	

}
