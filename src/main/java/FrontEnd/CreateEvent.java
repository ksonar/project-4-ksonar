package FrontEnd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import EventServer.EventStart;
import Logger.LogData;
import ReadData.Read;
import ServiceConnection.ConnectOther;
/*
 * Front end API to create new event
 * @author ksonar
 */
public class CreateEvent extends HttpServlet{
	private ArrayList<JSONObject> processed = new ArrayList<>();
	private JSONObject json = new JSONObject();
	private int port = EventStart.port;
	private String path = "/create";
	private String method = "POST";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		
		json = Read.readAndBuildJSON(request.getReader());
		LogData.log.info("POST: " + request.getPathInfo());
		System.out.println(json.toJSONString());
		ConnectOther service = new ConnectOther(port, path, method, json.toJSONString());
		processed = service.send();
		
		out.println(processed.get(0).toString());
	}
	


}
