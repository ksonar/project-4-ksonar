package FrontEnd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Errors.Error;
import EventServer.EventStart;
import Logger.LogData;
import ServiceConnection.ConnectOther;
import UserServer.UserStart;
import db.DBManager;
/*
 * Front end API to get detailed information for a particular user.
 * @auhtor ksonar
 */
public class UserID extends HttpServlet{
	private String userID;
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private int portU = UserStart.port;
	private String pathU = "/";
	private String method = "GET";
	private int portE = EventStart.port;
	private String pathE = "/";
	private DBManager db = DBManager.getInstance();
	private ConnectOther service;
	private String TRANSFER = "/tickets/transfer";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("GET: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		if(request.getPathInfo().split("/").length > 1) {
			userID = request.getPathInfo().split("/")[1];
			service = new ConnectOther(portU, pathU+userID, method);
			processed = service.send();
		}
		else {
			processed = db.buildError(Error.EMPTY);
		}

		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		else {
			processed.get(0).put("tickets", getEventInfo());
		}
		
		out.println(processed.get(0).toString());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("POST: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		String[] split = request.getPathInfo().split("/");

		if(split.length == 4) {
			try {
					request.setAttribute("userid", split[1]);
					request.getRequestDispatcher(TRANSFER).forward(request, response);
			} catch (ServletException e) {
				LogData.log.warning("Could not forward");
			}
		}
	}

	
	/*
	 * Get all event details for the various tickets bought by the user
	 */
	public ArrayList<JSONObject> getEventInfo() {
		JSONParser parser = new JSONParser(); 
		ArrayList<JSONObject> data = new ArrayList<>();
		String ticketInfo = processed.get(0).get("tickets").toString();

		String[] split = ticketInfo.toString().replace("[", "").replace("]", "").split(",");
		JSONObject event = new JSONObject();
		for(String s : split) {
			
			try {
				event = (JSONObject) parser.parse(s);

				service = new ConnectOther(portE, pathE+event.get("eventid").toString(),method);
				data.add(service.send().get(0));
			} catch (ParseException e) {
				LogData.log.warning("PARSING EXCEPTION");
			}
		}
		return data;
	}
}
