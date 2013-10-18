package uk.co.CyniCode.CyniChat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DatabaseManagers.IDataManager;
import uk.co.CyniCode.CyniChat.DatabaseManagers.JSONManager;
import uk.co.CyniCode.CyniChat.DatabaseManagers.MySQLManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * A sane way to load channel and user data.
 * @author Tehbeard
 * @author Matthew Ball
 * 
 */
public class DataManager {
	//List of loaded channels
	private static Map<String,Channel> channels = null;
	private static Map<String, String> matching = new HashMap<String, String>();
	private static Map<String, String> linkedChans = null;
	private static List<String> servers = null;
	
	private static Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from sources 
	
	private static Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	private static Map<String,UserDetails> activeUsers = new HashMap<String, UserDetails>();//Users that were online
	
	private static IDataManager Connection;
	
	/**
	 * Let's start grabbing all the data we can. Check which method of storage we're using
	 * Then get the relevant details from that medium.
	 * @param cynichat : This is an instance of the plugin
	 */
	public static void start( CyniChat cynichat) {
		if ( CyniChat.bungee == true ) {
			servers = cynichat.getConfig().getStringList( "CyniChat.bungee.connected" );
			
			Iterator<String> serIter = servers.iterator();
			
			while ( serIter.hasNext() ) {
				CyniChat.printDebug( serIter.next() );
			}
		}
		
		if ( CyniChat.SQL == true ) {
			Connection = new MySQLManager();
		} else {
			Connection = new JSONManager();
		}
		
		if ( Connection.startConnection(cynichat) == true ) {
			channels = Connection.returnChannels();
			loadedUsers = Connection.returnPlayers();
		}
	}
	
	public static void saveUsers() {
		if ( CyniChat.SQL == true ) {
			Connection.saveUsers( loadedUsers );
		} else {
			Connection.saveUsers( activeUsers );
		}
	}
	
	public static void saveChannels() {
		Connection.saveChannels( channels );
	}
	
	/**
	 * Generate a map of nicknames to channel names to make nick-name joining possible
	 */
	public static void channelTable() {
		Set<String> channelKeys = channels.keySet();
		Iterator<String> chanIter = channelKeys.iterator();
		
		while ( chanIter.hasNext() ) {
			String curName = chanIter.next();
			matching.put( channels.get(curName).getNick(), curName);
		}
		return;
	}
	
	/**
	 * Add a channel
	 * @param channel
	 */
	public static void addChannel(Channel channel) {
		channels.put(channel.getName().toLowerCase(), channel);
		matching.put( channel.getNick(), channel.getName() );
	}

	/**
	 * Return a channel
	 * @param name
	 * @return
	 */
	public static Channel getChannel(String name){
		Channel cn = channels.get(name);
		if ( cn == null ) {
			cn = channels.get( matching.get(name) );
		}
		return cn;
	}
	
	/**
	 * Print all the details about every channel
	 * (Only visible if you have debug on)
	 */
	public static void printAllChannels() {
		for ( int i=0; i<channels.size(); i++ ) {
			CyniChat.printDebug( String.valueOf(i) );
			CyniChat.printDebug( String.valueOf( channels.keySet().toArray()[i] ) );
			channels.get( channels.keySet().toArray()[i] ).printAll();
		}
	}
	
	/**
	 * Set all those channels which are linked in IRC
	 * @param chans
	 */
	public static void setIRCChans( Map<String, String> chans ) {
		Set<String> setter = chans.keySet();
		Iterator<String> iter = setter.iterator();
		while ( iter.hasNext() ) {
			String thisOne = iter.next();
			CyniChat.printDebug( "IRC name : "+thisOne );
			CyniChat.printDebug( "CC name : "+chans.get(thisOne) );
		}
		
		linkedChans = chans;
	}
	
	public static Map<String, String> getLinkedChannels() {
		return linkedChans;
	}
	
	public static List<String> getServers() {
		return servers;
	}
	
	/**
	 * Get a users details
	 * user may not be online!
	 * @param player
	 * @return
	 */
	public static UserDetails getDetails(String player){
		String uName = player.toLowerCase();
		UserDetails details = loadedUsers.get(uName);
		if(details == null){
			details = new UserDetails(); 
			loadedUsers.put(uName, details);
		}
		return details;
	}
	
	/**
	 * Bind the user details of a player to a player object
	 * @param player
	 */
	public static void bindPlayer(Player player){
		String playerName = player.getName().toLowerCase();
		UserDetails details = getDetails(playerName);
		details.bindPlayer(player);
		onlineUsers.put(playerName,details);
		activeUsers.put(playerName, details);
		details.printAll();
		printAllUsers();
	}
	
	/**
	 * Unbind a player from being online
	 * @param player
	 */
	public static void unbindPlayer(Player player){
		onlineUsers.remove(player.getName().toLowerCase()).bindPlayer(null);
	}
	
	/**
	 * Get user object of an online user
	 * @param player
	 * @return
	 */
	public static UserDetails getOnlineDetails(Player player){
		return onlineUsers.get(player.getName().toLowerCase());
	}

	/**
	 * Print all the users that are loaded and all the users that are online
	 * (Only visible if you have debug on)
	 */
	public static void printAllUsers() {
		if ( onlineUsers.size() != 0 ) {
			for ( int i=0; i<onlineUsers.size(); i++ ) {
				CyniChat.printDebug( String.valueOf(i) );
				CyniChat.printDebug( String.valueOf( onlineUsers.keySet().toArray()[i] ) );
				onlineUsers.get( onlineUsers.keySet().toArray()[i] ).printAll();
			}
		} else
			CyniChat.printDebug("No online users");
		if ( loadedUsers.size() != 0 ) {
			for ( int i=0; i<loadedUsers.size(); i++ ) {
				CyniChat.printDebug( String.valueOf(i) );
				CyniChat.printDebug( String.valueOf( loadedUsers.keySet().toArray()[i] ) );
				loadedUsers.get( loadedUsers.keySet().toArray()[i] ).printAll();
			}
		} else
			CyniChat.printDebug( "No loaded users" );
	}

	/**
	 * Get the map of those online.
	 * @return onlineUsers : Everyone who is currently online
	 */
	public static Map<String, UserDetails> returnAllOnline() {
		return onlineUsers;
	}

	/**
	 * Delete an existing channel
	 * @param name : The name of the channel we're trying to delete
	 * @return : True when complete, false otherwise
	 */
	public static boolean deleteChannel(String name) {
		try {
			Iterator<String> userIter = loadedUsers.keySet().iterator();
			while ( userIter.hasNext() ) {
				String username = userIter.next();
				UserDetails current = loadedUsers.get( username );
				if ( current.clearChannel(name) == true )
					activeUsers.put(username, current);
			}
			matching.remove( getChannel( name.toLowerCase() ).getNick() );
			channels.remove( name.toLowerCase() );
			return true;
		} catch (NullPointerException e) {
			return false;
		}
		
	}

	/**
	 * Get all the channels
	 * @return channels : Every channel
	 */
	public static Map<String, Channel> returnAllChannels() {
		return channels;
	}

	/**
	 * If there is a nickname in the matching table which matches the input, say so
	 * @param nick : The nickname we're checking.
	 * @return true if it exists, false if not.
	 */
	public static boolean hasNick(String nick) {
		if ( matching.containsKey(nick) )
			return true;
		return false;
	}
}
