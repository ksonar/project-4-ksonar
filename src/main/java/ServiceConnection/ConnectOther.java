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
import db.Config;
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
	private String hostname;
	private String exception = "Exception occured while connecting";
	private String url;
	
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
	//Send request from one service to another
	public ArrayList<JSONObject> send() {
		if(port == 8000) {
			url = Config.configData.getEventPath();
		}
		else {
			url = Config.configData.getUserPath();
		}
		
		String link = url + ":" + port + path;
		System.out.println(link);
		LogData.log.info("LINK : " + link);
		DBManager db = DBManager.getInstance();
		ArrayList<JSONObject> data = new ArrayList<>();
		try {
			con = setup(link);
			data = getData();
			if(con.getResponseCode() != 200) {
				String msg = "Got back 400 response";
				LogData.log.warning(msg);
			}

		} catch (MalformedURLException e) {
			data.addAll(db.buildError(exception));
			System.out.println(1);
			LogData.log.warning(exception);
		} catch (IOException e) {
			data.addAll(db.buildError(exception));
			System.out.println(2);
			LogData.log.warning(exception);
		} catch (ParseException e) {
			data.addAll(db.buildError(exception));
			System.out.println(3);
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
		if(method.equals("POST")) {
			con.setDoOutput(true);
			con.getOutputStream().write(body.getBytes());
		}
		return con;
	}
	/*
	 * Read data from input stream of HttpURLConnection
	 */
	public ArrayList<JSONObject> getData() throws IOException, ParseException {
		BufferedReader in;
		ArrayList<JSONObject> data = new ArrayList<>();
		if(con.getResponseCode() == 400) {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		}
		String message = read(in);
	
		JSONParser parser = new JSONParser();
		if(message.toString().startsWith("[")) {
			String[] split = message.toString().replace("[", "").replace("]", "").split(", ");
			for(String s : split) {
				data.add((JSONObject) parser .parse(s));
			}
		}
		else {
			data.add((JSONObject) parser.parse(message.toString()));
		}

		return data;
	}
	
	public String read(BufferedReader in) throws IOException {
		String inputLine;
		StringBuffer message = new StringBuffer();
		JSONParser parser = new JSONParser(); 
		while ((inputLine = in.readLine()) != null) {
			message.append(inputLine);
		}
		in.close();
		
		return message.toString();
	}
}
