package db;
import Errors.Error;
import java.sql.Connection;
import org.json.simple.JSONObject;
import Logger.LogData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/*
 * Manage all of preparing, executing and formatting SQL queries/results requested by APIs
 * @author ksonar
 */
public class DBManager {
	private Connection conn = Connect.getInstance().getConnection();
	private JSONObject obj = new JSONObject();
	private static DBManager instance;
	
	private DBManager() {}
	//singleton
	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}
	/*
	 * Set the key-value pair for when error occurred
	 */
	public ArrayList<JSONObject> buildError(String msg) {
		ArrayList<JSONObject> output = new ArrayList<>();
		obj.clear();
		obj.put("error", msg); 
		output.add(obj);
		return output;
	}
	/*
	 * Update Ticekts table
	 */
	public boolean updateTicketsTable(int param1, int param2, String col1, int val1) {
		boolean status = false;
		String sqlStmt = String.format("UPDATE tickets SET %s = %d where userID = %s and eventID = %d", col1,val1,param1, param2);
		LogData.log.info(sqlStmt);
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlStmt);
			int result = stmt.executeUpdate();
			if(result > 0) { status = true; }
		} catch (SQLException e) {

		}
		return status;
	}
	/*
	 * Update events table
	 */
	public boolean updateEventsTable(int param, String col1, int val1, String col2, int val2) {
		boolean status = false;
		String sqlStmt = String.format("UPDATE events SET %s = %d, %s = %d where eventID = %d", col1,val1,col2,val2,param);
		LogData.log.info(sqlStmt);
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlStmt);
			int result = stmt.executeUpdate();
			if(result > 0) { status = true; }
		} catch (SQLException e) {

		}
		return status;
	}
	
	/*
	 * Insert a user row data after validation
	 * @params table, userName
	 */
	public boolean insertTicketRowData(String table, int userID, int eventID, int tickets) {
		String sqlStmt = "INSERT INTO " + table + " (userID, eventID, tickets) values (?,?,?)";
		boolean flag = false;
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlStmt);
			stmt.setInt(1, userID);
			stmt.setInt(2, eventID);
			stmt.setInt(3, tickets);
			LogData.log.info(stmt.toString());
			int result = stmt.executeUpdate();
			if(result > 0) {
				LogData.log.info("SUCCESSFUL INSERT");
				flag = true;
			}

		} catch (SQLException e) {
			String msg = Error.INSERT + "[userid, eventid, tickets]";
			LogData.log.warning(msg);
			
		}
		return flag;
	}
	
	/*
	 * Insert a user row data after validation
	 * @params table, userName
	 */
	public ArrayList<JSONObject> insertUserRowData(String table, String userName) {
		String sqlStmt = "INSERT INTO " + table + " (userName) values (?)";
		ArrayList<JSONObject> output = new ArrayList<>();
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlStmt);
			stmt.setString(1, userName);
			LogData.log.info(stmt.toString());
			int result = stmt.executeUpdate();
			if(result > 0) {
				String query = "userName";  
				LogData.log.info("SUCCESSFUL INSERT");
				ArrayList<String> cols = new ArrayList<>();
				cols.add("userName");
				output = getSelectCertainParamResult(table, query, userName, true, cols);
				}

		} catch (SQLException e) {
			String msg = Error.INSERT + userName;
			LogData.log.warning(msg);
			output = buildError(msg);
		}
		return output;
	}
	/*
	 * Insert row data into events
	 */
	public ArrayList<JSONObject> insertEventRowData(String table, String eventName, int userID, int avail, int purchased) {
		String sqlStmt = "INSERT INTO " + table + " (eventName, userID, avail, purchased) values (?,?,?,?)";
		ArrayList<JSONObject> output = new ArrayList<>();
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlStmt);
			stmt.setString(1, eventName);
			stmt.setInt(2, userID);
			stmt.setInt(3, avail);
			stmt.setInt(4, purchased);
			LogData.log.info(stmt.toString());
			int result = stmt.executeUpdate();
			if(result > 0) {
				String query = "eventName";  
				LogData.log.info("SUCCESSFUL INSERT");
				ArrayList<String> cols = new ArrayList<>();
				cols.add("eventName"); cols.add("userID"); cols.add("avail"); cols.add("purchased");
				output = getSelectCertainParamResult(table, query, eventName, true, cols);
				}

		} catch (SQLException e) {
			String msg = "Could not insert :" + eventName;
			LogData.log.warning(msg);
			output = buildError(msg);
		}
		return output;
	}
	
	/*
	 * Get all columns data for given table
	 * @param table
	 */
	public ArrayList<JSONObject> getSelectAllResult(String table) {
		String sqlStmt = "SELECT * FROM " + table;
		ArrayList<JSONObject> output = new ArrayList<>();
		LogData.log.info(sqlStmt);
		output = execute(sqlStmt, table);
		return output;
	}
		
	/*
	 * Get certain columns data for each row of a table (delete certain columns)
	 * @params table, query, param, string, delCols
	 */
	public ArrayList<JSONObject> getSelectCertainParamResult(String table, String query, String param, boolean string, ArrayList<String> delCols) {
		ArrayList<JSONObject> output = new ArrayList<>();
		output = getSelectParamResult(table, query, param, string);
		LogData.log.info("REMOVING COLS : " + delCols.toString());
		
		output = delColumns(output, delCols);
		return output;
	}
	/*
	 * Remove specific fields
	 */
	public ArrayList<JSONObject> delColumns(ArrayList<JSONObject> output, ArrayList<String> delCols) {
		for(JSONObject data : output) {
			for(String delCol : delCols) {
				if(data.containsKey(delCol)) {
					data.remove(delCol);
				}
			}
		}
		return output;
	}
	/*
	 * Get data with n number of clauses with AND in between
	 */
	public ArrayList<JSONObject> getCertaindData(String table, String col, ArrayList<String> queries, ArrayList<String> params, ArrayList<String> types) {
		String sqlStmt = "SELECT " + col + " FROM " + table + " WHERE ";
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();
		for(int i = 0; i < queries.size(); i++) {
			if(i > 0 && i <= queries.size() -1) { sqlStmt += " AND "; }
			if(types.get(i).equals("str")) {
				sqlStmt += queries.get(i) + "=\"" + params.get(i) + "\"";
			}
			else {
				sqlStmt += queries.get(i) + "=" + Integer.parseInt(params.get(i));
			}
		}
		System.out.println(sqlStmt);
		LogData.log.info(sqlStmt);
		data = execute(sqlStmt,table);
		
		return data;
		
	}

	/*
	 * Get all columns data for given table with specific conidtion
	 * @params table, query, param, string
	 */
	public ArrayList<JSONObject> getSelectParamResult(String table, String query, String param, boolean string) {
		String sqlStmt;
		ArrayList<JSONObject> output = new ArrayList<>();
		if(string) {
			sqlStmt = "SELECT * FROM " + table + " WHERE " + query + "=\"" + param +"\"";
			LogData.log.info(sqlStmt);
		}
		else {
			try {
				Integer.parseInt(param);
			}
			catch (NumberFormatException i) {
				String msg = "String to integer cast problem : " + param;
				output = buildError(msg);
				LogData.log.warning(msg);
				return output;
			}
			sqlStmt = "SELECT * FROM " + table + " WHERE " + query + "=" + Integer.parseInt(param);
			LogData.log.info(sqlStmt);
		}
		output = execute(sqlStmt, table);
		return output; 
	}
	
	/*
	 * Execute the prepared statement and get users/events row data
	 * @param sql, table 
	 */
	public ArrayList<JSONObject> execute(String sql, String table) {
		PreparedStatement stmt;
		ArrayList<JSONObject> execData = new ArrayList<>();
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet result = stmt.executeQuery();
			obj.clear();
			if( result.next() == false) { 
				String msg = "No data returned from table : " + table;
				LogData.log.warning(msg); 
				execData = buildError(msg); 
				}
			else {
				
				if(table.equals("users") && sql.startsWith("SELECT")) {
					execData = getUsersRowData(result);
				}
				else if(table.equals("events") && sql.startsWith("SELECT")) {
					execData = getEventsRowData(result);
				}
				else if(table.equals("tickets") && sql.startsWith("SELECT") ) {
					execData = getTicketsRowData(result);
				}
				LogData.log.info(execData.size() + " row(s) returned");
			}

		} catch (SQLException e) {
			String msg = "SQL EXCEPTION";
			LogData.log.warning(msg);
			execData = buildError(msg);
		}
	return execData;
	}
	
	/*
	 * Pull out data from users db
	 */
	public ArrayList<JSONObject> getUsersRowData(ResultSet result) throws SQLException {
		ArrayList<JSONObject> output = new ArrayList<>();
		do{
			JSONObject temp = new JSONObject();
			temp.put("userName", result.getString("userName"));
			temp.put("userID", result.getString("userID"));
			output.add(temp);
		} while(result.next());
		return output;
	}
	/*
	 * Pull out data from events db
	 */
	public ArrayList<JSONObject> getEventsRowData(ResultSet result) throws SQLException {
		ArrayList<JSONObject> output = new ArrayList<>();
		do {
			JSONObject temp = new JSONObject();
			temp.put("eventID", result.getInt("eventID"));
			temp.put("eventName", result.getString("eventName"));
			temp.put("userID", result.getString("userID"));
			temp.put("avail", result.getInt("avail"));
			temp.put("purchased", result.getInt("purchased"));
			output.add(temp);
		} while(result.next());
		return output;
	}
	
	/*
	 * Pull out data from tickets db
	 */
	public ArrayList<JSONObject> getTicketsRowData(ResultSet result) throws SQLException {
		ArrayList<JSONObject> output = new ArrayList<>();
		do {
			JSONObject temp = new JSONObject();
			temp.put("eventID", result.getInt("eventID"));
			output.add(temp);
		} while(result.next());
		return output;
	}


}
