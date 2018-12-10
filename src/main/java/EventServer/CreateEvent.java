package EventServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import Logger.LogData;
import ReadData.Read;
import ServiceConnection.ConnectOther;
import UserServer.UserStart;
import db.DBManager;
/*
 * Create a new event after validating input params and user (from User Service API)
 * @author ksonar
 */
public class CreateEvent extends HttpServlet {
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private String table = "events";
	private String userid;
	private String eventName;
	private String tickets;
	private int port = UserStart.port;
	private String path = "/";
	private String method = "GET";
	private DBManager db = DBManager.getInstance();
	private JSONObject json = new JSONObject();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		json = Read.readAndBuildJSON(request.getReader());
		setParams(request);
		LogData.log.info("POST: " + request.getPathInfo() + " userid:" + userid + " eventName:" + eventName + " tickets:" + tickets);
		
		if(validateParams()) {
			ConnectOther service = new ConnectOther(port,path+userid,method);
			processed = service.send();
			if(processed.get(0).containsKey("error")) {
				buildError();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			else {
				LogData.log.info("UserID validated, allowing to create new event");
				insertRow();
				if(processed.get(0).containsKey("error")) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			}
		}
		else { response.setStatus(HttpServletResponse.SC_BAD_REQUEST); }
		out.println(processed.get(0).toString());
	}
	/*
	 * Set input parameters
	 * @param request
	 */
	public void setParams(HttpServletRequest request) {
		try {
			userid = json.get("userid").toString();
			eventName = json.get("eventname").toString();
			tickets = json.get("numtickets").toString();
		}
		catch(NullPointerException i) {
		}

	}
	/*
	 * Validate input parameters
	 */
	public boolean validateParams() {
		boolean flag;
		if(userid == null || eventName == null || tickets == null || userid.equals("") || eventName.equals("") || tickets.equals("")) {
			String msg = "One of [userid, eventname, tickets] not present or empty";
			processed = db.buildError(msg);
			LogData.log.warning(msg);
			flag = false;
		}
		else { flag = true; }
		return flag;
	}
	/*
	 * Insert row into SQL
	 */
	public void insertRow() {
		try {
			int userID = Integer.parseInt(userid);
			int avail = Integer.parseInt(tickets);
			LogData.log.info("New Event row fields validated, inserting new event");
			processed = db.insertEventRowData(table, eventName, userID, avail, 0);

		}
		catch (NumberFormatException i) {
			String msg = "String to integer cast problem @ numtickets :" + tickets;
			processed = db.buildError(msg);
			LogData.log.warning(msg);
		}
	}
	/*
	 * Build error if userid not found
	 */
	public void buildError() {
		String msg = "Invalid userid : " + userid;
		processed = db.buildError(msg);
		LogData.log.warning(msg);
	}
}
