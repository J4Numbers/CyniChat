package uk.co.CyniCode.CyniChat.DatabaseManagers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

public class MySQLManager {

	private String hostname;
	private int port;
	private String Username;
	private String Password;
	private String Database;
	private String Prefix;
	private Connection conn;
	
	private static Map<String,UserDetails> Players = new HashMap<String, UserDetails>();
	private static Map<String,Channel> channels = null;
	
	private PreparedStatement InsertChannel;
	private PreparedStatement InsertPlayer;
	private PreparedStatement LoadChannel;
	private PreparedStatement LoadPlayer;
	private PreparedStatement AddBan;
	private PreparedStatement AddMute;
	private PreparedStatement JoinChannel;
	private PreparedStatement RemBan;
	private PreparedStatement RemMute;
	private PreparedStatement LeaveChannel;
	private PreparedStatement UpdateChannel;
	private PreparedStatement UpdatePlayer;
	
	public boolean startConnection( CyniChat plugin ) {
		this.hostname = plugin.getConfig().getString("CyniChat.database.host");
		this.port = plugin.getConfig().getInt("CyniChat.database.port");
		this.Username = plugin.getConfig().getString("CyniChat.database.username");
		this.Password = plugin.getConfig().getString("CyniChat.database.password");
		this.Database = plugin.getConfig().getString("CyniChat.database.database");
		this.Prefix = plugin.getConfig().getString("CyniChat.database.prefix");
		
		if ( connect() == false ) {
			CyniChat.SQL = false;
			CyniChat.JSON = true;
			CyniChat.printSevere("Switching to JSON data usage!");
			return false;
		}
		return true;
	}
	
	public boolean connect() {
		String sqlUrl = String.format("jdbc:mysql://%s:%s/%s", hostname, port, Database);
		
		Properties sqlStr = new Properties();
		sqlStr.put("user", Username);
		sqlStr.put("password", Password);
		sqlStr.put("autoReconnect", "true");
		try {
			conn = DriverManager.getConnection(sqlUrl, sqlStr);
		} catch (SQLException e) {
			CyniChat.printSevere("A MySQL connection could not be made!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Map<String, UserDetails> returnPlayers() {
		return Players;
	}
	
	public Map<String, Channel> returnChannels() {
		return channels;
	}
}
