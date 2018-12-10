package FrontEnd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import ReadData.Read;
import ServiceConnection.ConnectOther;
import UserServer.UserStart;
/*
 * Front end API to create new user
 * @auhtor ksonar
 */
public class CreateUser extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private JSONObject json = new JSONObject();
	private int portU = UserStart.port;
	private String pathU = "/create";
	private String method = "POST";
	private ConnectOther service;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		
		json = Read.readAndBuildJSON(request.getReader());
		service = new ConnectOther(portU, pathU, method, json.toJSONString());
		processed = service.send();
		if((processed.size() == 1) && processed.get(0).containsKey("error")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		out.println(processed.get(0).toJSONString());
	}
}
