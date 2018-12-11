package FrontEnd;

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
/*
 * Front end API to to transfer tickets to another user
 * @author ksonar
 */
public class Transfer extends HttpServlet{
	private String userID;
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private int port = UserStart.port;
	private String path = "/tickets/transfer";
	private String method = "POST";
	private JSONObject json = new JSONObject();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogData.log.info("POST: " + request.getPathInfo());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		json = Read.readAndBuildJSON(request.getReader());
		setDetails(request);
		ConnectOther service = new ConnectOther(port, "/"+userID+path, method, json.toJSONString());
		processed = service.send();
		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toString());
		
	}
	
	/*
	 * Set fields for data sent from users/{userid} API
	 */
	public void setDetails(HttpServletRequest request) {
		userID = String.valueOf(request.getAttribute("userid"));
	}

}
