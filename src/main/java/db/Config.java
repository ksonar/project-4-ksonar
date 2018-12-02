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
	public static Config configData;
	//getters
	public String getUserName() { return username; }
	public String getPassword() { return password; }
	public String getDB() { return db; }

	private Config() {}
	public String toString() { 
		return username + "\t" + password + '\t' + db;
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
		System.out.println(configData.toString() + '\n');
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