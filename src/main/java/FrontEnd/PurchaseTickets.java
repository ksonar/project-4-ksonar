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
import ReadData.Read;
import ServiceConnection.ConnectOther;
/*
 * Front end API to purchase a ticket for an event and given userid
 * @author ksonar
 */
public class PurchaseTickets extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private String userID;
	private String eventID;
	private int port = EventStart.port;
	private String path = "/purchase/";
	private String method = "POST";
	private JSONObject json = new JSONObject();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("POST: " + request.getPathInfo());
		json = Read.readAndBuildJSON(request.getReader());
		setDetails(request);
		System.out.println(json.toJSONString());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		ConnectOther service = new ConnectOther(port, path+eventID, method, json.toJSONString());
		processed = service.send();
		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toString());
		
	}
	/*
	 * Set fields for data sent from events/{eventid} API
	 */
	public void setDetails(HttpServletRequest request) {
		eventID = String.valueOf(request.getAttribute("eventid"));
		userID = String.valueOf(request.getAttribute("userid"));
		
		json.put("userid", userID);
		json.put("eventid", eventID);
	}

}
