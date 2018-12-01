package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connect {
	private  String username  = "user29";
	private  String password  = "user29";
	private  String db  = "user29";
	public static Connection conn;
	private static Connect con;
	private Connect() {
	}
	
	public static Connect getInstance() {
		if (con == null) {
			con = new Connect();
			conn = con.getConnection();
		}
		return con;
	}
	
	
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}
		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		//note: if connecting through an ssh tunnel make sure to use 127.0.0.1 and
		//also to that the ports are set up correctly
		String urlString ="jdbc:mysql://127.0.0.1:3306/"+db;
		//String urlString = "jdbc:mysql://sql.cs.usfca.edu/"+db;
		//Must set time zone explicitly in newer versions of mySQL.
		String timeZoneSettings = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

		try {
			conn = DriverManager.getConnection(urlString+timeZoneSettings,username,password);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
}
