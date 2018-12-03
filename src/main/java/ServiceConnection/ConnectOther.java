package ServiceConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Logger.LogData;
import db.DBManager;
/*
 * Connect to another server using HttpURLConnection. Supports GET and POST
 * @author ksonar
 */
public class ConnectOther {
	private int port;
	private String path;
	private String method;
	private String body;
	private HttpURLConnection con;
	private String exception = "Exception occured while connecting";
	
	public ConnectOther(int port, String path, String method) {
		this.port = port;
		this.path = path;
		this.method = method;
	}
	
	public ConnectOther(int port, String path, String method, String body) {
		this.port = port;
		this.path = path;
		this.method = method;
		this.body = body;
	}
	//GET
	public ArrayList<JSONObject> methodGET() {
		String link = "http://localhost:" + port + path;
		DBManager db = DBManager.getInstance();
		ArrayList<JSONObject> data = new ArrayList<>();
		try {
			con = setup(link);
			if(con.getResponseCode() == 200) {
				data = getData();
			}
			else {
				String msg = "Got back 400 response";
				data.addAll(db.buildError(msg));
				LogData.log.warning(msg);
			}

		} catch (MalformedURLException e) {
			data.addAll(db.buildError(exception));
			LogData.log.warning(exception);
		} catch (IOException e) {
			data.addAll(db.buildError(exception));
			LogData.log.warning(exception);
		} catch (ParseException e) {
			data.addAll(db.buildError(exception));
			LogData.log.warning(exception);
		}
		return data;
	}
	/*
	 * Setup a HttpURLConnection to given link
	 * @param link
	 */
	public HttpURLConnection setup(String link) throws IOException {
		HttpURLConnection con;
		URL url = new URL(link);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		return con;
	}
	/*
	 * Read data from input stream of HttpURLConnection
	 */
	public ArrayList<JSONObject> getData() throws IOException, ParseException {
		ArrayList<JSONObject> data = new ArrayList<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer message = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			message.append(inputLine);
		}
		in.close();
		
		JSONParser parser = new JSONParser(); 
		data.add((JSONObject) parser.parse(message.toString()));	

		return data;
	}
}
