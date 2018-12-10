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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		json = Read.readAndBuildJSON(request.getReader());
		setParams(request);
		boolean flag = db.insertTicketRowData(table, userID, eventID, purchased);
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
