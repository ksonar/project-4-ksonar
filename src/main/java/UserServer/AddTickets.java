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
import db.DBManager;
/*
 * Add info into tickets table after a successful purchase
 * @author ksonar
 */
public class AddTickets extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private String table = "tickets";
	private int eventID;
	private int userID;
	private int purchased;
	private JSONObject json = new JSONObject();
	private DBManager db = DBManager.getInstance();
	private ArrayList<String> queries = new ArrayList<>();
	private ArrayList<String> params = new ArrayList<>();
	private ArrayList<String> types = new ArrayList<>();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		json = Read.readAndBuildJSON(request.getReader());
		setParams(request);
		String col = "eventID, tickets";

		queries.add("eventID"); queries.add("userID");
		params.add(String.valueOf(eventID)); params.add(String.valueOf(userID)); 
		types.add("int"); types.add("int");
		processed = db.getCertaindData(table, col, queries, params, types);
		boolean flag;
		if(processed.get(0).containsKey("error")) {
			System.out.println("EMPTY, adding new row");
			flag = db.insertTicketRowData(table, userID, eventID, purchased);
		}
		else {
			System.out.println("UDATING");
			flag = db.updateTicketsTable(userID, eventID, table, purchased, "+");
		}
		

		if(flag) {
			String msg = String.format("%d tickets bought by userid %d for eventid %d",purchased,userID,eventID);
			processed = db.buildSuccess(msg);
		}
		else {
			processed = db.buildError(Error.TICKETS);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		LogData.log.info(processed.toString());
		out.println(processed.get(0).toString());
	}
	
	public void setParams(HttpServletRequest request) {
		userID = (int)request.getAttribute("userid");
		eventID = Integer.parseInt(json.get("eventid").toString());
		purchased = Integer.parseInt(json.get("tickets").toString());
	}
}
