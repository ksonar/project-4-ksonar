package ReadData;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Logger.LogData;
/*
 * Read data from BufferedReader of HttPServletRequest 
 * @author ksonar
 */
public class Read {
	/*
	 * Convert string to JSON
	 */
	public static JSONObject readAndBuildJSON(BufferedReader read) {
		String data = Read.read(read);
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser(); 
		try {
			json = (JSONObject) parser.parse(data);
		} catch (ParseException e) {
			LogData.log.warning("Parsing error");
		}
		return json;
	}
	/*
	 * Read all data into a string
	 */
	public static String read(BufferedReader in) {
		String data = "";
		String line;
		try {
			while((line = in.readLine()) != null) {
				data += line;
			}
			in.close();
		} catch (IOException e) {
			LogData.log.warning("IO EXCEPTION");
		}
		
		return data;
	}

}
