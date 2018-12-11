package FrontEnd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import Errors.Error;
import EventServer.EventStart;
import Logger.LogData;
import ServiceConnection.ConnectOther;
import db.DBManager;
/*
 * Front end API to get all details for a particular event ID
 * @auhtor ksonar
 */
public class EventID extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private int port = EventStart.port;
	private String path = "/";
	private String method = "GET";
	private DBManager db = DBManager.getInstance();
	private String eventID;
	private String userID;
	private String PURCHASE = "/purchase/";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("GET: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		if(request.getPathInfo().split("/").length > 1) {
			eventID = request.getPathInfo().split("/")[1];
		}
		else { String msg = Errors.Error.EMPTY; processed = db.buildError(msg); LogData.log.warning(msg);}
		
		ConnectOther service = new ConnectOther(port, path+eventID, method);
		processed = service.send();
		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toString());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("POST: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		System.out.println(request.getPathInfo());
		String[] split = request.getPathInfo().split("/");
		if(split.length == 4 && split[2].equals("purchase")) {

			eventID = split[1];
			userID = split[3];
			System.out.println(eventID + userID);
			try {
				request.setAttribute("eventid", eventID);
				request.setAttribute("userid", userID);
				request.getRequestDispatcher(PURCHASE+userID).forward(request, response);
			} catch (ServletException e) {
				LogData.log.warning("Could not forward");
			}

		}
		else {
			processed = db.buildError(Error.PATH);
			out.println(processed.get(0).toString());
		}
	}

}
