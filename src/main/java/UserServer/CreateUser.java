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
 * Create a new user if username is unique
 * @author ksoanr
 */
public class CreateUser extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private String getUser = "username";
	private String table = "users";
	private DBManager db = DBManager.getInstance();
	private JSONObject json = new JSONObject();
	private String username;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		json.clear();
		json = Read.readAndBuildJSON(request.getReader());

		if(checkInput()) { 			
			username = json.get(getUser).toString();
			processed = db.insertUserRowData(table, username);
			LogData.log.info("POST: " + request.getPathInfo() + " " + username);
		}
		
		if((processed.size() == 1 && processed.get(0).containsKey("error"))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toString());
		LogData.log.info("RESPONSE STATUS : " + response.getStatus());
	}
	/*
	 * Check if input is null/empty
	 */
	public boolean checkInput() {
		boolean flag = true;
		if(json.get(getUser) == null || json.get(getUser).toString().equals("")) {
			String msg = "Invalid input (empty)";
			processed = db.buildError(msg);
			LogData.log.warning(msg);
			flag = false;
		}
		return flag;
	}

}
