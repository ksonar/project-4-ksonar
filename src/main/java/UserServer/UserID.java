package UserServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import Logger.LogData;
import ReadData.Read;
import db.DBManager;
/*
 * Get data on specific userID from db
 * @author ksonar
 */
public class UserID extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private ArrayList<JSONObject> ticketsObj = new ArrayList<>();
	private String userTable = "users";
	private String ticketsTable = "tickets";
	private String userQuery = "userID";
	private DBManager db = DBManager.getInstance();
	private String userID;
	private String col = "eventID";
	private ArrayList<String> queries = new ArrayList<>();
	private ArrayList<String> params = new ArrayList<>();
	private ArrayList<String> types = new ArrayList<>();
	private JSONObject json = new JSONObject();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("GET: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		json = Read.readAndBuildJSON(request.getReader());
		
		if(request.getPathInfo().split("/").length > 1) {
			userID = request.getPathInfo().split("/")[1];
			processed = db.getSelectParamResult(userTable, userQuery, userID, false);
		}
		else { String msg = "Invalid input(empty)"; processed = db.buildError(msg); LogData.log.warning(msg);}

		if((processed.size() == 1)) {
			if (processed.get(0).containsKey("error")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			else {
				processed = getAllUserInfo();
			}
			out.println(processed.get(0).toString());
			LogData.log.info("RETURNED USERID INFO");
		}
		else {
			out.println(processed.toString());
		}
		LogData.log.info("RESPONSE STATUS : " + response.getStatus());
	}
	
	/*
	 * Get all eventIDs for particular userID
	 */
	public ArrayList<JSONObject> getAllUserInfo() {
		queries.clear(); params.clear(); types.clear();
		queries.add("userID");
		params.add(userID); 
		types.add("int"); 
		ticketsObj = db.getCertaindData(ticketsTable, col, queries, params, types);
		processed.get(0).put("tickets", ticketsObj);
		return processed;
	}
	
}
