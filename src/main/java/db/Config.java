package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import Logger.LogData;

/*
 * Store data from config file
 * @author ksonar
 */
public class Config {
	private String username;
	private String password;
	private String db;
	private String dbHostname;
	private String frontPath;
	private String eventPath;
	private String userPath;
	private int frontPort;
	private int eventPort;
	private int userPort;
	
	public static Config configData;
	
	//getters
	public String getUserName() { return username; }
	public String getPassword() { return password; }
	public String getDB() { return db; }
	public String getHostname() { return dbHostname; }
	public String getFrontPath() { return frontPath; }
	public String getEventPath() { return eventPath; }
	public String getUserPath() { return userPath; }
	public int getFrontPort() { return frontPort; }
	public int getEventPort() { return eventPort; }
	public int getUserPort() { return userPort; }
	
	private Config() {}
	public String toString() { 
		return username + "\t" + password + '\t' + db + '\t' + dbHostname;
	}

	/*
	 * Read from config file
	 * @params cFile
	 */
	public static void readConfig(String cFile) {
		Gson gson = new GsonBuilder().create();
		try {
		BufferedReader f = Files.newBufferedReader(Paths.get(cFile));
		configData = gson.fromJson(f, Config.class);
		LogData.log.info(configData.toString());
		}
		catch (IOException | NullPointerException i) {
			LogData.log.warning("NO SUCH FILE");
			System.exit(1);
		}
		catch (JsonSyntaxException i) {
			LogData.log.warning("JSON EXCEPTION");
		}		
	}

}