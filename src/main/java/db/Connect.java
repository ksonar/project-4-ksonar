package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Create a single instance of the DB connection, read credentials from a config file
 * @author ksonar
 */
public class Connect {
	private String cFile = "config.json";
	private  String username;
	private  String password;
	private  String db;
	private static Connection conn;
	private static Connect con;
	private Connect() {
		Config.readConfig(cFile);
		this.username = Config.configData.getUserName();
		this.password = Config.configData.getPassword();
		this.db = Config.configData.getDB();
	}
	//singleton
	public static Connect getInstance() {
		if (con == null) {
			con = new Connect();
		}
		return con;
	}
	
	//setup the connection to mysql
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		String urlString ="jdbc:mysql://127.0.0.1:3306/"+db;

		String timeZoneSettings = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

		try {
			conn = DriverManager.getConnection(urlString+timeZoneSettings,username,password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
