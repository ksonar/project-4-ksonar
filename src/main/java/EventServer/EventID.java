package EventServer;

import java.io.IOException;

import java.io.PrintWriter;

import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;


//import org.json.JSONObject;
import Logger.LogData;

import db.DBManager;
/*
 * Get information of a particular eventID
 * @author ksonar
 */
public class EventID extends HttpServlet{
	private ArrayList<JSONObject> obj = new ArrayList<>();
	private String table = "events";
	private String query = "eventID";
	private DBManager db = DBManager.getInstance();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("GET: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		if(request.getPathInfo().split("/").length > 1) {
			String eventID = request.getPathInfo().split("/")[1];

			obj = db.getSelectParamResult(table, query, eventID, false);
		}
		else { String msg = "Invalid input(empty)"; obj = db.buildError(msg); LogData.log.warning(msg);}

		if((obj.size() == 1)) {
			if (obj.get(0).containsKey("error")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			out.println(obj.get(0).toString());
		}
		else {
			out.println(obj.toString());
		}

		LogData.log.info("RESPONSE STATUS : " + response.getStatus());
	}
}
