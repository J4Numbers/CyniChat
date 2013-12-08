package uk.co.CyniCode.CyniChat.DatabaseManagers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Deal with all things MySQL'y
 * @author Matthew Ball
 *
 */
public class MySQLManager implements IDataManager {

	/**
	 * @return the Players
	 */
	public static Map<String,UserDetails> getPlayers() {
		return Players;
	}

	/**
	 * @param aPlayers the Players to set
	 */
	public static void setPlayers(Map<String,UserDetails> aPlayers) {
		Players = aPlayers;
	}

	/**
	 * @return the channels
	 */
	public static Map<String,Channel> getChannels() {
		return channels;
	}

	/**
	 * @param aChannels the channels to set
	 */
	public static void setChannels(Map<String,Channel> aChannels) {
		channels = aChannels;
	}
	
	/**
	 * The hostname of the mysql server
	 */
	private String hostname;
	
	/**
	 * What port the mysql server is running on
	 */
	private int port;
	
	/**
	 * The username we're using to connect to the mysql server
	 */
	private String Username;
	
	/**
	 * The password of the username
	 */
	private String Password;
	
	/**
	 * The database name we're connecting to
	 */
	private String Database;
	
	/**
	 * The prefix of the tables
	 */
	private String Prefix;
	
	/**
	 * The connection instance
	 */
	private Connection conn;
	
	private static Map<String,UserDetails> Players = new HashMap<String, UserDetails>();
	private static Map<String,Channel> channels = new HashMap<String, Channel>();
	
	private PreparedStatement InsertChannel;
	private PreparedStatement InsertPlayer;
	private PreparedStatement AddBan;
	private PreparedStatement AddMute;
	private PreparedStatement JoinChannel;
	private PreparedStatement UpdateChannel;
	private PreparedStatement UpdatePlayer;
	private PreparedStatement AddIgnoring;
	
	/**
	 * Boot up the plugin and do all the connecting bullshit.
	 * Basically... connect, generate tables, make statements
	 * if @throws error then we connect to JSON instead 
	 * @param plugin : The thing we use to get the config options
	 * @return true when complete
	 */
	public boolean startConnection( CyniChat plugin ) {
		
		setHostname(plugin.getConfig().getString("CyniChat.database.host"));
		setPort(plugin.getConfig().getInt("CyniChat.database.port"));
		setUsername(plugin.getConfig().getString("CyniChat.database.username"));
		setPassword(plugin.getConfig().getString("CyniChat.database.password"));
		setDatabase(plugin.getConfig().getString("CyniChat.database.database"));
		setPrefix(plugin.getConfig().getString("CyniChat.database.prefix"));
		
		if ( connect() == false ) {
			CyniChat.SQL = false;
			CyniChat.JSON = true;
			CyniChat.printSevere("Switching to JSON data usage!");
			return false;
		}
		
		if ( generateTables( getPrefix()) == false ) {
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
	
	/**
	 * Actually connect to the database with the information we've been given
	 * @return true upon completion
	 */
	private boolean connect() {
		
		String sqlUrl = String.format("jdbc:mysql://%s:%s/%s", getHostname(), getPort(), getDatabase());
		
		Properties sqlStr = new Properties();
		sqlStr.put("user", getUsername());
		sqlStr.put("password", getPassword());
		sqlStr.put("autoReconnect", "true");
		CyniChat.printDebug("H:"+getHostname()+" P:"+getPort()+" D:"+getDatabase()
				+" U:"+getUsername()+" Pass:"+getPassword());
		
		try {
			setConn(DriverManager.getConnection(sqlUrl, sqlStr));
		} catch (SQLException e) {
			CyniChat.printSevere("A MySQL connection could not be made!");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Make the statements that we're going to use a fuckton of times
	 * @return true when complete
	 */
	private boolean prepareStatements() {
		try {
			setInsertPlayer(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "players` " + "(`player_name`,`player_name_clean`,`active_channel`,`can_ignore`) " + "VALUES (?,?,?,'1')", Statement.RETURN_GENERATED_KEYS));
			setInsertChannel(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "channels` " + "(`channel_name`,`channel_name_clean`,`channel_nickname`,`channel_pass`) " + "VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS));
			setAddBan(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "banned` " + "(`bannee_id`,`channel_id`) VALUES (?,?) " + "ON DUPLICATE KEY UPDATE `bannee_id`=`bannee_id`", Statement.RETURN_GENERATED_KEYS));
			setAddMute(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "muted` " + "(`mutee_id`,`channel_id`) VALUES (?,?) " + "ON DUPLICATE KEY UPDATE `mutee_id`=`mutee_id`", Statement.RETURN_GENERATED_KEYS));
			setJoinChannel(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "current_channel` " + "(`player_id`,`channel_id`) " + "VALUES (?,?)", Statement.RETURN_GENERATED_KEYS));
			setUpdateChannel(getConn().prepareStatement("UPDATE `" + getPrefix() + "channels` " + "SET `channel_desc`=?, " + "`channel_irc_name`=?, " + "`channel_irc_pass`=?, " + "`channel_colour`=?, " + "`channel_protected`=? " + "WHERE `channel_id`=?", Statement.RETURN_GENERATED_KEYS));
			setUpdatePlayer(getConn().prepareStatement("UPDATE `" + getPrefix() + "players` " + "SET `active_channel`=?, " + "`player_silenced`=?, " + "`can_ignore`=? " + "WHERE `player_id`=?", Statement.RETURN_GENERATED_KEYS));
			setAddIgnoring(getConn().prepareStatement("INSERT INTO `" + getPrefix() + "ignoring` " + "(`ignorer_id`,`ignoree_id`) VALUES (?,?) " + "ON DUPLICATE KEY UPDATE `ignorer_id`=`ignorer_id`", Statement.RETURN_GENERATED_KEYS));
		} catch (SQLException e) {
			CyniChat.printSevere("Statement preparation has failed!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save the channel data
	 * @param channels : Everything we're going to save
	 * 
	 * @WARNING This is an inefficient and shitty method of updating no-matter-what. Even if nothing has changed.
	 * Forgive me...
	 */
	public void saveChannels(Map<String, Channel> channels) {
		
		for ( Map.Entry<String, Channel> entrySet : channels.entrySet() ) {
			Channel current = entrySet.getValue();
			try {
				PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM `"+getPrefix()+"channels` WHERE `channel_id`='"+current.getID()+"'");
				ResultSet rs = ps.executeQuery();
				current.printAll();
				while (rs.next())
					if ( rs.getInt(1) == 0 ) {
						getInsertChannel().setString(1, current.getName());
						getInsertChannel().setString(2, current.getName().toLowerCase());
						getInsertChannel().setString(3, current.getNick());
						getInsertChannel().setString(4, current.getPass());
						getInsertChannel().execute();
						
						ResultSet generatedKeys = getInsertChannel().getGeneratedKeys();
						if (generatedKeys.next()) {
							current.setId( generatedKeys.getInt(1) );
						}
						
						getInsertChannel().clearParameters();
					}
				getUpdateChannel().setString(1, current.getDesc());
				getUpdateChannel().setString(2, current.getIRC());
				getUpdateChannel().setString(3, current.getIRCPass());
				getUpdateChannel().setString(4, current.getColour().name());
				getUpdateChannel().setBoolean(5, current.isProtected());
				getUpdateChannel().setInt( 6, current.getID() );
				getUpdateChannel().execute();
				getUpdateChannel().clearParameters();
			} catch (SQLException e) {
				CyniChat.printSevere("Saving failed on channel: "+current.getName());
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Save users that have been online at some point since the last save
	 * @param loadedPlayers : The active players that we're saving
	 * 
	 * @WARNING : See last @WARNING.
	 */
	public void saveUsers(Map<String, UserDetails> loadedPlayers) {
		for ( Map.Entry<String, UserDetails> entrySet : loadedPlayers.entrySet() ) {
			String username = entrySet.getKey();
			UserDetails current = entrySet.getValue();
			try {
				PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM `"
					+getPrefix()+"players` WHERE `player_id`='"+current.getID()+"'");
				ResultSet rs = ps.executeQuery();
				while (rs.next())
					if (rs.getInt(1) == 0) {
						getInsertPlayer().setString(1, username );
						getInsertPlayer().setString(2, username.toLowerCase());
						getInsertPlayer().setInt(3, CyniChat.data.getChannels()
							.get( current.getCurrentChannel() ).getID());
						getInsertPlayer().execute();
						
						ResultSet generatedKeys = getInsertPlayer().getGeneratedKeys();
						if (generatedKeys.next()) {
							current.setId( generatedKeys.getInt(1) );
						}
						
						getInsertPlayer().clearParameters();
					} else {
						getUpdatePlayer().setInt(1, CyniChat.data.getChannels()
							.get( current.getCurrentChannel() ).getID());
						getUpdatePlayer().setBoolean(2, current.getSilenced());
						getUpdatePlayer().setBoolean(3, current.canIgnore());
						getUpdatePlayer().setInt(4, current.getID() );
						getUpdatePlayer().execute();
						getUpdatePlayer().clearParameters();
					}
				ps.close();
				rs.close();
			} catch (SQLException e) {
			}
		}
		
		for ( Map.Entry<String, UserDetails> entrySet : loadedPlayers.entrySet() ) {
			String username = entrySet.getKey();
			UserDetails current = entrySet.getValue();
			try {
				PreparedStatement ps2 = getConn().prepareStatement("DELETE FROM `"
					+getPrefix()+"current_channel` WHERE `player_id`='"+current.getID()+"'");
				ps2.execute();
				ps2.close();
				for ( String curString : current.getAllChannels() ) {
					Channel curChan = CyniChat.data.getChannel( curString );
					getJoinChannel().setInt(1, current.getID());
					getJoinChannel().setInt(2, curChan.getID());
					getJoinChannel().execute();
					getJoinChannel().clearParameters();
				}
				
				PreparedStatement ps4 = getConn().prepareStatement("DELETE FROM `"
					+getPrefix()+"ignoring` WHERE `ignorer_id`='"+current.getID()+"'");
				ps4.execute();
				ps4.close();
				for ( String curString : current.getIgnoring() ) {
					getAddIgnoring().setInt(1, current.getID());
					getAddIgnoring().setInt(2, CyniChat.data.getDetails(curString).getID());
					getAddIgnoring().execute();
					getAddIgnoring().clearParameters();
				}
				
				PreparedStatement ps5 = getConn().prepareStatement("DELETE FROM `"
					+getPrefix()+"muted` WHERE `mutee_id`='"+current.getID()+"'");
				ps5.execute();
				ps5.close();
				for ( String curString : current.getMutedChannels() ) {
					getAddMute().setInt( 1, current.getID() );
					getAddMute().setInt( 2, CyniChat.data.getChannel( curString ).getID() );
					getAddMute().execute();
					getAddMute().clearParameters();
				}
				
				PreparedStatement ps6 = getConn().prepareStatement("DELETE FROM `"
					+getPrefix()+"banned` WHERE `bannee_id`='"+current.getID()+"'");
				ps6.execute();
				ps6.close();
				for ( String curString : current.getBannedChannels() ) {
					getAddBan().setInt( 1, current.getID() );
					getAddBan().setInt( 2, CyniChat.data.getChannel( curString ).getID() );
					getAddBan().execute();
					getAddBan().clearParameters();
				}
				
			} catch (SQLException e) {
				CyniChat.printSevere("Saving failed on player: "+username);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get all the players and their information from the database
	 * @return a Map of all the players and their details
	 */
	public Map<String, UserDetails> returnPlayers() {
		try {
			PreparedStatement ps = getConn().prepareStatement(
					  "SELECT `player_id`,`player_name`,`channel_name`,"
						  + "`player_silenced`,`can_ignore` FROM `"
						  +getPrefix()+"players` INNER JOIN `"
						  +getPrefix()+"channels` ON `"+getPrefix()
						  +"channels`.`channel_id`=`"+getPrefix()
						  +"players`.`active_channel`" );
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
				active = rs.getString(3);
				silenced = rs.getBoolean(4);
				canIgnore = rs.getBoolean(5);
				
				PreparedStatement ps2 = getConn().prepareStatement(
					  "SELECT `channel_name` FROM `"+getPrefix()+"current_channel` "
						  + "INNER JOIN `"+getPrefix()+"channels` ON "+getPrefix()
						  +"channels.channel_id="+getPrefix()+"current_channel.channel_id "
						  + "WHERE "+getPrefix()+"current_channel.player_id='"+rs.getInt(1)
						  +"'");
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					JoinedChannels.add( rs2.getString(1) );
				}
				rs2.close();
				ps2.close();
				
				PreparedStatement ps3 = getConn().prepareStatement(
					  "SELECT `player_name` FROM `"+getPrefix()+"ignoring` "
						  + "INNER JOIN `"+getPrefix()+"players` ON "+getPrefix()
						  +"players.player_id="+getPrefix()+"ignoring.ignoree_id "
						  + "WHERE "+getPrefix()+"ignoring.ignorer_id='"+rs.getInt(1)+"'");
				ResultSet rs3 = ps3.executeQuery();
				while ( rs3.next() )
					Ignoring.add( rs3.getString(1) );
				rs3.close();
				ps3.close();
				
				PreparedStatement ps4 = getConn().prepareStatement(
					  "SELECT `channel_name` FROM `"+getPrefix()+"banned` "
						  + "INNER JOIN `"+getPrefix()+"channels` ON "+getPrefix()
						  + "channels.`channel_id`="+getPrefix()+"banned.`channel_id` "
						  + "WHERE "+getPrefix()+"banned.bannee_id='"+rs.getInt(1)+"'");
				ResultSet rs4 = ps4.executeQuery();
				while ( rs4.next() )
					BannedFrom.add( rs4.getString(1) );
				ps4.close();
				rs4.close();
				
				PreparedStatement ps5 = getConn().prepareStatement(
					  "SELECT `channel_name` FROM `"+getPrefix()+"muted` "
						  + "INNER JOIN `"+getPrefix()+"channels` ON "+getPrefix()
						  +"channels.channel_id="+getPrefix()+"muted.channel_id "
						  + "WHERE "+getPrefix()+"muted.`mutee_id`='"+rs.getInt(1)+"'");
				ResultSet rs5 = ps5.executeQuery();
				while ( rs5.next() )
					MutedIn.add( rs5.getString(1) );
				
				UserDetails current = new UserDetails();
				current.loadData( rs.getInt(1), active, silenced, canIgnore, JoinedChannels, MutedIn, BannedFrom, Ignoring);
				getPlayers().put(name, current);
			}
		} catch (SQLException e) {
			CyniChat.printSevere("Player loading has failed!");
			e.printStackTrace();
			CyniChat.killPlugin();
		}
		return getPlayers();
	}
	
	/**
	 * Generate a map of all the channels in the various tables
	 * @return the map of all the channels along with their information
	 */
	public Map<String, Channel> returnChannels() {
		try {
			PreparedStatement ps = getConn().prepareStatement(
				"SELECT * FROM `"+getPrefix()+"channels`");
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				int ID = rs.getInt(1);
				String name = rs.getString(2);
				String nick = rs.getString(4);
				String irc = rs.getString(5);
				String ircPass = rs.getString(6);
				String desc = rs.getString(7);
				String pass = rs.getString(8);
				String colour = rs.getString(9);
				Boolean protect = rs.getBoolean(10);
				Channel current = new Channel();
				current.loadChannel(ID, name, nick, irc, ircPass, desc, pass, colour, protect);
				current.printAll();
				try {
					getChannels().put(rs.getString(2).toLowerCase(),current);
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
		return getChannels();
	}
	
	public Runnable getBooster() {
		
		return new boostConnection();
		
	}
	
	public class boostConnection implements Runnable {
		
		public void run() {
			
			CyniChat.data.flushData();
			
		}
		
	};
	
	/**
	 * Check and make all the tables required for this plugin
	 * @param prefix : The prefix of the tables that you want
	 * @return true when complete
	 */
	private boolean generateTables( String prefix ) {
		ResultSet rs;
		try {
			
			//Channels
			CyniChat.printInfo("Searching for channels table");
			rs = getConn().getMetaData().getTables(null, null, prefix + "channels", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'channels' table found, attempting to regenerate...");
				PreparedStatement ps = getConn()
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "channels` ( "
								+ "`channel_id` int not null auto_increment, "
								+ "`channel_name` varchar(64) not null, "
								+ "`channel_name_clean` varchar(64) not null, "
								+ "`channel_nickname` varchar(8) not null, "
								+ "`channel_irc_name` varchar(32) default '', "
								+ "`channel_irc_pass` varchar(32) default '', "
								+ "`channel_desc` varchar(128), "
								+ "`channel_pass` varchar(32) not null DEFAULT '', "
								+ "`channel_colour` VARCHAR(16) not null DEFAULT 'GRAY', "
								+ "`channel_protected` tinyint(1) null, "
								+ "PRIMARY KEY (channel_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'channels' table created!");
				PreparedStatement ps2 = getConn().prepareStatement("INSERT INTO `"+prefix+"channels` "
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
			rs = getConn().getMetaData().getTables(null, null, prefix + "players", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'players' table found, attempting to regenerate...");
				PreparedStatement ps = getConn()
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
			rs = getConn().getMetaData().getTables(null, null, prefix + "current_channel", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'current_channel' table found, attempting to regenerate...");
				PreparedStatement ps = getConn()
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "current_channel` ( "
								+ "`player_id` int not null, "
								+ "`channel_id` int not null, "
								+ "`mod` tinyint(1) null, "
								+ "UNIQUE KEY `current_chan` (`player_id`,`channel_id`), "
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
			rs = getConn().getMetaData().getTables(null, null, prefix + "banned", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'banned' table found, attempting to regenerate...");
				PreparedStatement ps = getConn()
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "banned` ( "
								+ "`bannee_id` int not null, "
								+ "`channel_id` int not null, "
								+ "UNIQUE KEY `banned_chan` (`bannee_id`,`channel_id`), "
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
			rs = getConn().getMetaData().getTables(null, null, prefix + "ignoring", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'ignoring' table found, attempting to create one...");
				PreparedStatement ps = getConn()
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "ignoring` ( "
								+ "`ignorer_id` int not null, "
								+ "`ignoree_id` int not null, "
								+ "UNIQUE KEY `ignoring_chan` (`ignorer_id`,`ignoree_id`), "
								+ "FOREIGN KEY (ignorer_id) REFERENCES "+prefix+"players(player_id), "
								+ "FOREIGN KEY (ignoree_id) REFERENCES "+prefix+"players(player_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'ignoring' table created!");
			} else {
				CyniChat.printInfo("Ignoring table found");
			}
			
			//Muted
			CyniChat.printInfo("Searching for muted table");
			rs = getConn().getMetaData().getTables(null, null, prefix + "muted", null);
			if (!rs.next()) {
				CyniChat.printWarning("No 'muted' table found, attempting to create one...");
				PreparedStatement ps = getConn()
						.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "muted` ( "
								+ "`channel_id` int not null, "
								+ "`mutee_id` int not null, "
								+ "UNIQUE KEY `muted_chan` (`channel_id`,`mutee_id`), "
								+ "FOREIGN KEY (channel_id) REFERENCES "+prefix+"channels(channel_id), "
								+ "FOREIGN KEY (mutee_id) REFERENCES "+prefix+"players(player_id) "
								+ ");" );
				ps.executeUpdate();
				ps.close();
				CyniChat.printWarning("'muted' table created!");
			} else {
				CyniChat.printInfo("Muted table found");
			}
			
			rs.close();
			
		} catch (SQLException e) {
			CyniChat.printSevere("The tables could not be generated!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the Username
	 */
	public String getUsername() {
		return Username;
	}

	/**
	 * @param Username the Username to set
	 */
	public void setUsername(String Username) {
		this.Username = Username;
	}

	/**
	 * @return the Password
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * @param Password the Password to set
	 */
	public void setPassword(String Password) {
		this.Password = Password;
	}

	/**
	 * @return the Database
	 */
	public String getDatabase() {
		return Database;
	}

	/**
	 * @param Database the Database to set
	 */
	public void setDatabase(String Database) {
		this.Database = Database;
	}

	/**
	 * @return the Prefix
	 */
	public String getPrefix() {
		return Prefix;
	}

	/**
	 * @param Prefix the Prefix to set
	 */
	public void setPrefix(String Prefix) {
		this.Prefix = Prefix;
	}

	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * @param conn the conn to set
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * @return the InsertChannel
	 */
	public PreparedStatement getInsertChannel() {
		return InsertChannel;
	}

	/**
	 * @param InsertChannel the InsertChannel to set
	 */
	public void setInsertChannel(PreparedStatement InsertChannel) {
		this.InsertChannel = InsertChannel;
	}

	/**
	 * @return the InsertPlayer
	 */
	public PreparedStatement getInsertPlayer() {
		return InsertPlayer;
	}

	/**
	 * @param InsertPlayer the InsertPlayer to set
	 */
	public void setInsertPlayer(PreparedStatement InsertPlayer) {
		this.InsertPlayer = InsertPlayer;
	}

	/**
	 * @return the AddBan
	 */
	public PreparedStatement getAddBan() {
		return AddBan;
	}

	/**
	 * @param AddBan the AddBan to set
	 */
	public void setAddBan(PreparedStatement AddBan) {
		this.AddBan = AddBan;
	}

	/**
	 * @return the AddMute
	 */
	public PreparedStatement getAddMute() {
		return AddMute;
	}
	
	/**
	 * @param AddMute the AddMute to set
	 */
	public void setAddMute(PreparedStatement AddMute) {
		this.AddMute = AddMute;
	}
	
	/**
	 * @return the JoinChannel
	 */
	public PreparedStatement getJoinChannel() {
		return JoinChannel;
	}
	
	/**
	 * @param JoinChannel the JoinChannel to set
	 */
	public void setJoinChannel(PreparedStatement JoinChannel) {
		this.JoinChannel = JoinChannel;
	}
	
	/**
	 * @return the UpdateChannel
	 */
	public PreparedStatement getUpdateChannel() {
		return UpdateChannel;
	}
	
	/**
	 * @param UpdateChannel the UpdateChannel to set
	 */
	public void setUpdateChannel(PreparedStatement UpdateChannel) {
		this.UpdateChannel = UpdateChannel;
	}
	
	/**
	 * @return the UpdatePlayer
	 */
	public PreparedStatement getUpdatePlayer() {
		return UpdatePlayer;
	}
	
	/**
	 * @param UpdatePlayer the UpdatePlayer to set
	 */
	public void setUpdatePlayer(PreparedStatement UpdatePlayer) {
		this.UpdatePlayer = UpdatePlayer;
	}
	
	/**
	 * @return the AddIgnoring
	 */
	public PreparedStatement getAddIgnoring() {
		return AddIgnoring;
	}
	
	/**
	 * @param AddIgnoring the AddIgnoring to set
	 */
	public void setAddIgnoring(PreparedStatement AddIgnoring) {
		this.AddIgnoring = AddIgnoring;
	}
	
}
