package EventServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import Errors.Error;
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
 * Purchase a ticket after validating input, checking if eventID exists and if tickets are available to buy
 * @author ksonar
 */
public class PurchaseTickets extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private ArrayList<JSONObject> check = new ArrayList<>();
	private String eventid;
	private String userid;
	private String tickets;
	private String pathEventID;
	private int userID;
	private int eventID;
	private int purchase;
	private int avail;
	private int purchased;
	private DBManager db = DBManager.getInstance();
	private String table = "events";
	private String query = "eventID";
	
	private int port = UserStart.port;
	private String path = "/";
	private String method = "GET";
	private JSONObject json = new JSONObject();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		json = Read.readAndBuildJSON(request.getReader());
		setParams(request);
		LogData.log.info("POST: " + request.getPathInfo() + " userid:" + userid + " eventID:" + eventid + " tickets:" + tickets);
		
		if(validateParams()) {
			ConnectOther service = new ConnectOther(port,path+userid,method);
			check = service.methodGET();
			if(check.get(0).containsKey("error")) {
				processed = db.buildError(Error.UID+userid);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			}
			else {
				avail = (int) processed.get(0).get("avail");
				purchased = (int) processed.get(0).get("purchased");
				if(tryPurchasing()) {
					LogData.log.info("Events table updated");
					boolean flag = db.insertTicketRowData("tickets", userID, eventID, purchase);	
					if(flag) {
						buildSuccess();
						LogData.log.info("Tickets table updated\nTickets purchased!!!");
					}
				}
				else {
					processed = db.buildError(Error.PURCHASE+eventid);
					LogData.log.warning(Error.PURCHASE+eventid);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			}
			out.println(processed.get(0).toString());
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println(processed.get(0).toString());
		}
		
	}
	
	/*
	 * Check if tickets can be purchased for specific eventID
	 */
	public boolean tryPurchasing() {
		boolean flag = false;
		if (purchase <= avail && purchase > 0) {
			LogData.log.info(String.format("Can purchase tickets avail = %d, purchasing = %d\nCurrent : %s",avail,purchase,processed.get(0).toString()));
			int newAvail = avail - purchase;
			int newPurchased = purchased + purchase;
			boolean status = db.updateEventsTable(eventID, "avail", newAvail, "purchased", newPurchased);
			if(status) {
				flag = true;
			}
		}
		return flag;
	}
	
	
	/*
	 * Set params from request path and body
	 * @param request
	 */
	public void setParams(HttpServletRequest request) {
		try {
			eventid =  json.get("eventid").toString();
			userid = json.get("userid").toString();
			tickets = json.get("tickets").toString();
		}
		catch (NullPointerException i) {
			eventid = "";
			userid = "";
			tickets = "";
		}

		String[] pathInfo = request.getPathInfo().split("/");
		if(pathInfo.length == 2) {
			pathEventID = pathInfo[1];
		}
	}
	/*
	 * Validate input parameters
	 */
	public boolean validateParams() {
		boolean flags;
		flags = checkInput();
		if(flags) { 
			processed = db.getSelectParamResult(table, query, eventid, false);
			if(processed.size() == 1 && processed.get(0).containsKey("error")) {
				flags = false;
				System.out.println("FALSE");
				LogData.log.warning(processed.get(0).get("error").toString());
			}
			else { 
				flags = castInput();
			}
		}
		return flags;
	}
	/*
	 * Check for input to be of desired format
	 */
	public boolean checkInput()  {
		boolean flag = true;
		if(pathEventID == null || !pathEventID.equals(eventid)) {
			String msg = "One of eventid in request path and body do not match or is null";
			processed = db.buildError(msg);
			LogData.log.warning(msg);
			flag = false;
		}
		else if(userid == null || eventid == null || tickets == null || userid.equals("") || eventid.equals("") || tickets.equals("")) {
			processed = db.buildError(Error.NOT_MATCHING);
			LogData.log.warning(Error.NOT_MATCHING);
			flag = false;
		}
		return flag;
	}
	/*
	 * Cast string to int from input params
	 */
	public boolean castInput() {
		boolean flag;
		try {
			eventID = Integer.parseInt(eventid);
			userID = Integer.parseInt(userid);
			purchase = Integer.parseInt(tickets);
			flag = true; 
			LogData.log.info("Input params and eventID validated");
		}
		catch (NumberFormatException i) {
			String msg = "String to integer cast problem for [eventid,userid,tickets] ";
			processed = db.buildError(msg);
			LogData.log.warning(msg);
			flag = false;
		}
		return flag;
	}
	/*
	 * Build userid not found error
	 */
	public void buildError(String error, String param) {
		String msg = error + param;
		processed = db.buildError(msg);
		LogData.log.warning(msg);
	}
	
	public void buildSuccess() {
		processed.clear();
		JSONObject success = new JSONObject();
		success.put("sucess", String.format("%d tickets bought by userid %d for eventid %d",purchase,userID,eventID));
		processed.add(success);
	}

}
