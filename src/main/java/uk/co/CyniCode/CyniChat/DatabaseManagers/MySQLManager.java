package uk.co.CyniCode.CyniChat.DatabaseManagers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
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
	private static Map<String,Channel> channels = new HashMap<String, Channel>();
	
	private PreparedStatement InsertChannel;
	private PreparedStatement InsertPlayer;
	private PreparedStatement AddBan;
	private PreparedStatement AddMute;
	private PreparedStatement JoinChannel;
	private PreparedStatement RemBan;
	private PreparedStatement RemMute;
	private PreparedStatement LeaveChannel;
	private PreparedStatement UpdateChannel;
	private PreparedStatement UpdatePlayer;
	private PreparedStatement AddIgnoring;
	private PreparedStatement RemIgnoring;
	
	public boolean startConnection( CyniChat plugin ) {
		this.hostname = plugin.getConfig().getString("CyniChat.database.host");
		this.port = 3306; //plugin.getConfig().getInt("CyniChat.database.port");
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
		if ( generateTables( Prefix ) == false ) {
			CyniChat.SQL = false;
			CyniChat.JSON = true;
			CyniChat.printSevere("Switching to JSON data usage!");
			return false;
		}
		if ( prepareStatements() == false ) {
			CyniChat.SQL = false;
			CyniChat.JSON = true;
			CyniChat.printSevere("Switching to JSON data usage!");
			return false;
		}
		return true;
	}
	
	private boolean connect() {
		String sqlUrl = String.format("jdbc:mysql://%s:%s/%s", hostname, port, Database);
		
		Properties sqlStr = new Properties();
		sqlStr.put("user", Username);
		sqlStr.put("password", Password);
		sqlStr.put("autoReconnect", "true");
		CyniChat.printDebug("H:"+hostname+" P:"+port+" D:"+Database+" U:"+Username+" Pass:"+Password);
		try {
			conn = DriverManager.getConnection(sqlUrl, sqlStr);
		} catch (SQLException e) {
			CyniChat.printSevere("A MySQL connection could not be made!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean prepareStatements() {
		try {
			InsertPlayer = conn.prepareStatement("INSERT INTO `"+Prefix+"players` "
					+ "(`player_name`,`player_name_clean`,`active_channel`) "
					+ "VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
			InsertChannel = conn.prepareStatement("INSERT INTO `"+Prefix+"players` "
					+ "(`channel_name`,`channel_name_clean`,`channel_nickname`,`channel_pass`) "
					+ "VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			AddBan = conn.prepareStatement("INSERT INTO `"+Prefix+"banned` "
					+ "(`banner_id`,`bannee_id`,`channel_id`,`reason`) "
					+ "VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			AddMute = conn.prepareStatement("UPDATE `"+Prefix+"current_channel` "
					+ "SET `muted`='1' "
					+ "WHERE `player_id`=?", Statement.RETURN_GENERATED_KEYS);
			JoinChannel = conn.prepareStatement("INSERT INTO `"+Prefix+"current_channel` "
					+ "(`player_id`,`channel_id`) "
					+ "VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			RemBan = conn.prepareStatement("DELETE FROM `"+Prefix+"banned` "
					+ "WHERE (`player_id`=?) AND (`channel_id`=?)", Statement.RETURN_GENERATED_KEYS);
			RemMute = conn.prepareStatement("UPDATE `"+Prefix+"current_channel` "
					+ "SET `muted`='0'"
					+ "WHERE `player_id`=?", Statement.RETURN_GENERATED_KEYS);
			LeaveChannel = conn.prepareStatement("DELETE FROM `"+Prefix+"current_channel` "
					+ "WHERE (`player_id`=?) AND (`channel_id`=?)", Statement.RETURN_GENERATED_KEYS);
			UpdateChannel = conn.prepareStatement("UPDATE `"+Prefix+"channels` "
					+ "SET `channel_desc`=?, "
					+ "`channel_colour`=?, "
					+ "`channel_protected`=?", Statement.RETURN_GENERATED_KEYS);
			UpdatePlayer = conn.prepareStatement("UPDATE `"+Prefix+"players` "
					+ "SET `active_channel`=?, "
					+ "`player_silenced`=?, "
					+ "`can_ignore`=?", Statement.RETURN_GENERATED_KEYS);
			AddIgnoring = conn.prepareStatement("INSERT INTO `"+Prefix+"ignoring` "
					+ "(`ignorer_id`,`ignoree_id`) "
					+ "VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			RemIgnoring = conn.prepareStatement("DELETE FROM `"+Prefix+"ignoring` "
					+ "WHERE (`ignorer_id`=?) AND (`ignoree_id`=?)", Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			CyniChat.printSevere("Statement preparation has failed!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void saveChannels(Map<String, Channel> channels) {
		Set<String> keys = channels.keySet();
		Iterator<String> keyIterate = keys.iterator();
		while (keyIterate.hasNext()) {
			Channel current = channels.get(keyIterate.next());
			try {
				PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM `"+Prefix+"channels` WHERE `channel_id`='"+current.getID()+"'");
				ResultSet rs = ps.executeQuery();
				current.printAll();
				while (rs.next())
					if ( rs.getInt(1) == 0 ) {
						InsertChannel.setString(0, current.getName());
						InsertChannel.setString(1, current.getName().toLowerCase());
						InsertChannel.setString(2, current.getNick());
						InsertChannel.setString(3, current.getPass());
						InsertChannel.execute();
						InsertChannel.clearParameters();
					}
				UpdateChannel.setString(0, current.getDesc());
				UpdateChannel.setString(1, current.getColour().name());
				UpdateChannel.setBoolean(2, current.isProtected());
				UpdateChannel.execute();
				UpdateChannel.clearParameters();
			} catch (SQLException e) {
				CyniChat.printSevere("Saving failed on channel: "+current.getName());
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void saveUsers(Map<String, UserDetails> loadedPlayers) {
		Set<String> keys = loadedPlayers.keySet();
		Iterator<String> keyIterate = keys.iterator();
		while (keyIterate.hasNext()) {
			UserDetails current = loadedPlayers.get(keyIterate.next());
			try {
				PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM `"+Prefix+"players` WHERE `player_name`='"+current.getPlayer().getName()+"'");
				ResultSet rs = ps.executeQuery();
				while (rs.next())
					if (rs.getInt(1) == 0) {
						InsertPlayer.setString(0, current.getPlayer().getName());
						InsertPlayer.setString(1, current.getPlayer().getName().toLowerCase());
						InsertPlayer.setInt(2, DataManager.returnAllChannels().get( current.getCurrentChannel() ).getID());
						InsertPlayer.execute();
						InsertPlayer.clearParameters();
					} else {
						UpdatePlayer.setInt(0, DataManager.returnAllChannels().get( current.getCurrentChannel() ).getID());
						UpdatePlayer.setBoolean(1, current.getSilenced());
						UpdatePlayer.setBoolean(2, current.canIgnore());
						UpdatePlayer.execute();
						UpdatePlayer.clearParameters();
					}
				ps.close();
				rs.close();
				
				PreparedStatement ps2 = conn.prepareStatement("DELETE FROM `"+Prefix+"current_channels` WHERE `player_id`='"+current.getID()+"'");
				ps2.execute();
				ps2.close();
				List<String> Joined = current.getAllChannels();
				Iterator<String> joinIter = Joined.iterator();
				while (joinIter.hasNext()) {
					Channel curChan = DataManager.getChannel(joinIter.next());
					JoinChannel.setInt(0, current.getID());
					JoinChannel.setInt(1, curChan.getID());
					JoinChannel.execute();
					JoinChannel.clearParameters();
				}
				
				PreparedStatement ps4 = conn.prepareStatement("DELETE FROM `"+Prefix+"ignoring` WHERE `player_id`='"+current.getID()+"'");
				ps4.execute();
				ps4.close();
				List<String> Ignoring = current.getIgnoring();
				Iterator<String> ignoIter = Ignoring.iterator();
				while (ignoIter.hasNext()) {
					AddIgnoring.setInt(0, current.getID());
					AddIgnoring.setInt(1, DataManager.getDetails(ignoIter.next()).getID());
					AddIgnoring.execute();
					AddIgnoring.clearParameters();
				}
				
			} catch (SQLException e) {
				CyniChat.printSevere("Saving failed on player: "+current.getPlayer().getName());
				e.printStackTrace();
				return;
			}
		}
	}
	
	public Map<String, UserDetails> returnPlayers() {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM `"+Prefix+"players`");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name;
				String active;
				Boolean silenced;
				Boolean canIgnore;
				List<String> JoinedChannels = new ArrayList<String>();
				List<String> Ignoring = new ArrayList<String>();
				List<String> BannedFrom = new ArrayList<String>();
				List<String> MutedIn = new ArrayList<String>();
				name = rs.getString(2);
				active = rs.getString(4);
				silenced = rs.getBoolean(5);
				canIgnore = rs.getBoolean(6);
				
				PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM `"+Prefix+"current_channel` "
						+ "INNER JOIN `"+Prefix+"channels` ON "+Prefix+"channels.channel_id="+Prefix+"current_channel.channel_id "
						+ "WHERE "+Prefix+"current_channel.player_id='"+rs.getInt(1)+"'");
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					JoinedChannels.add( rs2.getString(7) );
					if ( rs2.getBoolean(4) == true )
						MutedIn.add( rs2.getString(7) );
				}
				rs2.close();
				ps2.close();
				
				PreparedStatement ps3 = conn.prepareStatement("SELECT * FROM `"+Prefix+"ignoring` "
						+ "INNER JOIN `players` ON "+Prefix+"players.player_id="+Prefix+"ignoring.ignoree_id "
						+ "WHERE "+Prefix+"ignoring.ignorer_id='"+rs.getInt(1)+"'");
				ResultSet rs3 = ps3.executeQuery();
				while ( rs3.next() )
					Ignoring.add( rs3.getString(5) );
				rs3.close();
				ps3.close();
				
				PreparedStatement ps4 = conn.prepareStatement("SELECT * FROM `"+Prefix+"banned` "
						+ "INNER JOIN `"+Prefix+"channels` ON "+Prefix+"channels.channel_id="+Prefix+"banned.channel_id"
						+ "WHERE "+Prefix+"banned.bannee_id='"+rs.getInt(1)+"'");
				ResultSet rs4 = ps4.executeQuery();
				while ( rs4.next() )
					BannedFrom.add(rs4.getString(4));
				ps4.close();
				rs4.close();
				UserDetails current = new UserDetails();
				current.loadData( rs.getInt(1), active, silenced, canIgnore, JoinedChannels, MutedIn, BannedFrom, Ignoring);
				Players.put(name, current);
			}
		} catch (SQLException e) {
			CyniChat.printSevere("Player loading has failed!");
			e.printStackTrace();
			CyniChat.killPlugin();
		}
		return Players;
	}
	
	public Map<String, Channel> returnChannels() {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM `"+Prefix+"channels`");
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				int ID = rs.getInt(1);
				String name = rs.getString(2);
				String nick = rs.getString(4);
				String desc = rs.getString(5);
				String pass = rs.getString(6);
				String colour = rs.getString(7);
				Boolean protect = rs.getBoolean(8);
				Channel current = new Channel();
				current.loadChannel(ID, name, nick, desc, pass, colour, protect);
				current.printAll();
				try {
					channels.put(rs.getString(2),current);
				} catch (NullPointerException e) {
					CyniChat.printSevere("Null Pointer found!");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			CyniChat.printSevere("Channel loading has failed!");
			e.printStackTrace();
			CyniChat.killPlugin();
		}
		return channels;
	}
	
	private boolean generateTables( String prefix ) {
		ResultSet rs;
		try {
			
			//Channels
			CyniChat.printInfo("Searching for channels table");
			rs = conn.getMetaData().getTables(null, null, prefix + "channels", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'channels' table found, attempting to regenerate...");
				PreparedStatement ps = conn
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "channels` ( "
								+ "`channel_id` int not null auto_increment, "
								+ "`channel_name` varchar(64) not null, "
								+ "`channel_name_clean` varchar(64) not null, "
								+ "`channel_nickname` varchar(8) not null, "
								+ "`channel_desc` varchar(128), "
								+ "`channel_pass` varchar(32) not null DEFAULT '', "
								+ "`channel_colour` VARCHAR(16) not null DEFAULT 'GRAY', "
								+ "`channel_protected` tinyint(1) null, "
								+ "PRIMARY KEY (channel_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'channels' table created!");
				PreparedStatement ps2 = conn.prepareStatement("INSERT INTO `"+prefix+"channels` "
						+ "(`channel_name`,`channel_name_clean`,`channel_nickname`,`channel_desc`) "
						+ "VALUES ('Global','global','g','The global channel for everyone!')");
				ps2.executeUpdate();
				ps2.close();
				CyniChat.printWarning("'channels' table populated!");
			} else {
				CyniChat.printInfo("Channels table found");
			}
			rs.close();
			
			//Players
			CyniChat.printInfo("Searching for players table");
			rs = conn.getMetaData().getTables(null, null, prefix + "players", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'players' table found, attempting to regenerate...");
				PreparedStatement ps = conn
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "players` ( "
								+ "`player_id` int not null auto_increment, "
								+ "`player_name` varchar(64) not null, "
								+ "`player_name_clean` varchar(64) not null, "
								+ "`active_channel` int not null DEFAULT '1', "
								+ "`player_silenced` tinyint(1) null, "
								+ "`can_ignore` tinyint(1) null, "
								+ "PRIMARY KEY (player_id), "
								+ "FOREIGN KEY (active_channel) REFERENCES "+prefix+"channels(channel_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'players' table created!");
			} else {
				CyniChat.printInfo("Players table found");
			}
			rs.close();
			
			//Current Channel
			CyniChat.printInfo("Searching for current channel table");
			rs = conn.getMetaData().getTables(null, null, prefix + "current_channel", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'current_channel' table found, attempting to regenerate...");
				PreparedStatement ps = conn
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "current_channel` ( "
								+ "`rec_id` int not null auto_increment, "
								+ "`player_id` int not null, "
								+ "`channel_id` int not null, "
								+ "`muted` tinyint(1) null, "
								+ "`mod` tinyint(1) null, "
								+ "PRIMARY KEY (rec_id), "
								+ "FOREIGN KEY (player_id) REFERENCES "+prefix+"players(player_id), "
								+ "FOREIGN KEY (channel_id) REFERENCES "+prefix+"channels(channel_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'current_channel' table created!");
			} else {
				CyniChat.printInfo("Current channel table found");
			}
			rs.close();
			
			//Banned
			CyniChat.printInfo("Searching for banned table");
			rs = conn.getMetaData().getTables(null, null, prefix + "banned", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'banned' table found, attempting to regenerate...");
				PreparedStatement ps = conn
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "banned` ( "
								+ "`ban_id` int not null auto_increment, "
								+ "`banner_id` int not null, "
								+ "`bannee_id` int not null, "
								+ "`channel_id` int not null, "
								+ "`reason` varchar(128), "
								+ "PRIMARY KEY (ban_id), "
								+ "FOREIGN KEY (banner_id) REFERENCES "+prefix+"players(player_id), "
								+ "FOREIGN KEY (bannee_id) REFERENCES "+prefix+"players(player_id), "
								+ "FOREIGN KEY (channel_id) REFERENCES "+prefix+"channels(channel_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'banned' table created!");
			} else {
				CyniChat.printInfo("Banned table found");
			}
			rs.close();
			
			//Ignoring
			CyniChat.printInfo("Searching for ignoring table");
			rs = conn.getMetaData().getTables(null, null, prefix + "ignoring", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'ignoring' table found, attempting to create one...");
				PreparedStatement ps = conn
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "ignoring` ( "
								+ "`ignore_id` int not null auto_increment, "
								+ "`ignorer_id` int not null, "
								+ "`ignoree_id` int not null, "
								+ "PRIMARY KEY (ignore_id), "
								+ "FOREIGN KEY (ignorer_id) REFERENCES "+prefix+"players(player_id), "
								+ "FOREIGN KEY (ignoree_id) REFERENCES "+prefix+"players(player_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'ignoring' table created!");
			} else {
				CyniChat.printInfo("Ignoring table found");
			}
			rs.close();
			
		} catch (SQLException e) {
			CyniChat.printSevere("The tables could not be generated!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
