package EventServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import db.DBManager;

public class EventID extends HttpServlet{
	private ArrayList<JSONObject> obj = new ArrayList<>();
	private String table = "events";
	private String query = "eventID";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		System.out.println("::::" + request.getPathInfo() + " ::::");
		String eventID = request.getPathInfo().split("/")[1];
		System.out.println(eventID);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		DBManager db = DBManager.getInstance();
		
		obj = db.getSelectParamResult(table, query, eventID, true);
		System.out.println(obj.toString());
		if((obj.size() == 1) && (obj.get(0).containsKey("error"))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(obj.toString());
	}
}
