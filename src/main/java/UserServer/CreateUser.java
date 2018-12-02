package UserServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import Logger.LogData;
import db.DBManager;
/*
 * Create a new user if username is unique
 * @author ksoanr
 */
public class CreateUser extends HttpServlet{
	private ArrayList<JSONObject> obj = new ArrayList<>();
	String getUser = "username";
	String table = "users";
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		String username = request.getParameter(getUser);
		LogData.log.info("POST: " + request.getPathInfo() + " " + username);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		DBManager db = DBManager.getInstance();
		
		obj = db.insertUserRowData(table, username);
		if((obj.size() == 1) && (obj.get(0).containsKey("error"))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		LogData.log.info("RESPONSE STATUS : " + response.getStatus());
		out.println(obj.toString());
		
	}

}
