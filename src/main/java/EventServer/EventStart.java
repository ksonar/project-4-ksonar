package EventServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import Logger.LogData;

public class EventStart {
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8000);
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		handler.addServletWithMapping(EventID.class, "/*");
		handler.addServletWithMapping(EventList.class, "/list");
		//handler.addServletWithMapping(InvertedIndexAPI.class, "/InvertedIndexAPI");
		server.start();
		//LogData.log.info("MAPPING DONE, SERVER STARTED");
		server.join();
		
	}

	

}
