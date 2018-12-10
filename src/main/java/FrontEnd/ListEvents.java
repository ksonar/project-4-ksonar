package FrontEnd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import EventServer.EventStart;
import Logger.LogData;
import ServiceConnection.ConnectOther;
/*
 * Front end API to list all events from database
 * @author ksonar
 */
public class ListEvents extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private int port = EventStart.port;
	private String path = "/list";
	private String method = "GET";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("GET: " + request.getPathInfo());
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		
		ConnectOther service = new ConnectOther(port, path, method);
		processed = service.send();
		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.toString());
	}
}
