package db;

import java.sql.Connection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager {
	private Connection conn = Connect.getInstance().getConnection();
	private JSONObject obj = new JSONObject();
	private static DBManager instance;
	
	private DBManager() {}
	
	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}
	
	public ArrayList<JSONObject> getSelectAllResult(String table) {
		String sqlStmt = "SELECT * FROM " + table;
		ArrayList<JSONObject> output = new ArrayList<>();
		output = execute(sqlStmt, table);
		return output;
	}
	
	public ArrayList<JSONObject> getSelectParamResult(String table, String query, String param, boolean string) {
		String sqlStmt;
		ArrayList<JSONObject> output = new ArrayList<>();
		if(string) {
			sqlStmt = "SELECT * FROM " + table + " WHERE " + query + "=" + param;
		}
		else {
			sqlStmt = "SELECT * FROM " + table + " WHERE " + query + "=" + Integer.parseInt(param);
		}
		output = execute(sqlStmt, table);
		return output; 
	}
	
	public ArrayList<JSONObject> execute(String sql, String table) {
		PreparedStatement stmt;
		ArrayList<JSONObject> execData = new ArrayList<>();
		try {
			stmt = conn.prepareStatement(sql);
			ResultSet result = stmt.executeQuery();
			obj.clear();
			if( result.next() == false) { obj.put("error", "true"); execData.add(obj); }
			else {
				if(table.equals("users")) {
					execData = getUsersRowData(result, execData);
				}
				else if(table.equals("events")) {
					execData = getEventsRowData(result, execData);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	return execData;
	}
	
	
	public ArrayList<JSONObject> getUsersRowData(ResultSet result, ArrayList<JSONObject> output) throws SQLException {
		do{
			JSONObject temp = new JSONObject();
			temp.put("userName", result.getString("userName"));
			temp.put("userID", result.getString("userID"));
			temp.put("phone", result.getString("phone"));
			output.add(temp);
		} while(result.next());
		return output;
	}
	
	public ArrayList<JSONObject> getEventsRowData(ResultSet result, ArrayList<JSONObject> output) throws SQLException {
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


}
