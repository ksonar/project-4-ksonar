package UserServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import Errors.Error;
import Logger.LogData;
import ReadData.Read;
import ServiceConnection.ConnectOther;
import db.DBManager;
/*
 * Transfer tickets if allowed to do so
 * @author ksonar
 */
public class Transfer extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private ArrayList<JSONObject> check = new ArrayList<>();
	private JSONObject json = new JSONObject();
	private DBManager db = DBManager.getInstance();
	private String col = "eventID, tickets";
	private int userID;
	private int targetUserID;
	private int tickets;
	private int eventID;
	private int port = UserStart.port;
	private String path = "/";
	private ConnectOther service;
	private String table = "events";
	private String query = "eventID";
	private ArrayList<String> queries = new ArrayList<>();
	private ArrayList<String> params = new ArrayList<>();
	private ArrayList<String> types = new ArrayList<>();
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		json = Read.readAndBuildJSON(request.getReader());
		if(setParams(request)) {
			LogData.log.info("POST: " + request.getPathInfo() + " userid:" + userID + " eventID:" + eventID + " tickets:" + tickets + " targetuser:" + targetUserID);
			if(!validateParams(response)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			else {
				LogData.log.info("Can transfer tickets, if available");
				setData(userID);
				processed = db.getCertaindData("tickets", col, queries, params, types);
				if(processed.get(0).containsKey("error")) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				else {
					if(!processTransfer()) {
						String msg = Error.TRANSFER;
						processed = db.buildError(msg);
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
				}
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toJSONString());
		
	}
	/*
	 * Try to transfer tickets after inputs have been validated
	 */
	public boolean processTransfer() {
		boolean check = true;
		int availTickets = Integer.parseInt(processed.get(0).get("tickets").toString());
		if(availTickets >= tickets && tickets > 0) {
			LogData.log.info("Tickets available");
			boolean status = db.updateTicketsTable(userID, eventID, "tickets", tickets, "-");
			if(status) {
				LogData.log.info("Tickets deducted from userid : " + userID);
				setData(targetUserID);

				processed = db.getCertaindData("tickets", col, queries, params, types);
				boolean flag;
				if(processed.get(0).containsKey("error")) {
					flag = db.insertTicketRowData("tickets", targetUserID, eventID, tickets);
				}
				else {
					flag = db.updateTicketsTable(targetUserID, eventID, "tickets", tickets, "+");
				}
				if(flag) { 
					String msg = String.format("%d tickets transferred to userid %d from userid %s for eventid %d",tickets,targetUserID,userID,eventID);
					processed = db.buildSuccess(msg);
					LogData.log.info(msg);
					}
				else {
					String msg = "Tickets were deucted from source userid but could not be transferred to target userid";
					processed = db.buildError(msg);
					LogData.log.warning(msg);
				}
			}
		}
		else {
			check = false;
		}
		return check;		
	}
	/*
	 * Set input params before querying database
	 */
	public void setData(int userID) {
		queries.clear(); params.clear(); types.clear();
		queries.add("eventID"); queries.add("userID");
		params.add(String.valueOf(eventID)); params.add(String.valueOf(userID)); 
		types.add("int"); types.add("int");
	}
	/*
	 * Set input params and data sent from other API
	 */
	public boolean setParams(HttpServletRequest request) {
		boolean status = true;
		String msg;
		try {
		userID = Integer.parseInt(request.getAttribute("userid").toString());
		targetUserID = Integer.parseInt(json.get("targetuser").toString());
		tickets = Integer.parseInt(json.get("tickets").toString());
		eventID = Integer.parseInt(json.get("eventid").toString());
		LogData.log.info("input params validated");
		}
		catch (NumberFormatException i) {
			status = false;
			userID = targetUserID = tickets = eventID = 0;
			msg = Error.CAST + " [userid, targetuser, eventid, tickets]";
			processed = db.buildError(msg);
			LogData.log.warning(msg);
		}
		catch (NullPointerException i) {
			status = false;
			userID = targetUserID = tickets = eventID = 0;
			msg = Error.EMPTY;
			processed = db.buildError(msg);
			LogData.log.warning(msg);
		}
		return status;
	}
	/*
	 * Validate format of input params
	 */
	public boolean validateParams(HttpServletResponse response) {
		boolean flag = true;
		boolean checkUser;
		checkUser = validateUser(response, userID);
		checkUser = validateUser(response, targetUserID);
		if(!checkUser) {
			flag = false;
			processed = db.buildError(Error.UID+"[userid, targetuserid]");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			LogData.log.warning(Error.UID+"[userid, targetuserid]");
			return flag;
		}
		else {
			flag = validateEventID();
		}
		
		return flag;
	}
	/*
	 * Validate user info
	 */
	public boolean validateUser(HttpServletResponse response, int user) {
		boolean flag = true;
		service = new ConnectOther(port,path+user,"GET");
		check = service.send();
		if(check.get(0).containsKey("error")) {
			flag = false;
			processed = db.buildError(Error.UID+userID);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		return flag;
	}
	/*
	 * Validate event info
	 */
	public boolean validateEventID() {
		boolean flag = true;
		processed = db.getSelectParamResult(table, query, String.valueOf(eventID), false);
		if(processed.size() == 1 && processed.get(0).containsKey("error")) {
			flag = false;
		}
		return flag;
	}

}
