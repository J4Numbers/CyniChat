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
	
	/**
	 * The list of all channels that are loaded
	 */
	private static Map<String,Channel> channels = null;
	
	/**
	 * The nickname-to-name matching table... I think
	 */
	private static Map<String, String> matching = new HashMap<String, String>();
	
	/**
	 * Any channels that are also linked to IRC
	 */
	private static Map<String, String> linkedChans = null;
	
	
	
	/**
	 * The list of everyone that is in the data structure,
	 * regardless of whether they're online or not
	 */
	private static Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from sources 
	
	/**
	 * The list of every online user that is currently
	 *  ... well... online
	 */
	private static Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	/**
	 * The active users. These are people that have been online since
	 * the last reload and save so we know who to update
	 */
	private static Map<String,UserDetails> activeUsers = new HashMap<String, UserDetails>();//Users that were online
	
	/**
	 * The connection bit that lets us access the data
	 */
	private static IDataManager Connection;
	
	/**
	 * Let's start grabbing all the data we can. Check which method of storage we're using
	 * Then get the relevant details from that medium.
	 * @param cynichat : This is an instance of the plugin
	 */
	public static void start( CyniChat cynichat) {
		
		//If we're using SQL as a data type...
		if ( CyniChat.SQL == true ) {
			
			//Use the MySQL manager
			setConnection(new MySQLManager());
			
		} else {
			
			//Otherwise, we're using JSON
			setConnection(new JSONManager());
			
		}
		
		//Start the connection and make sure we /are/ connected
		if ( getConnection().startConnection(cynichat) == true ) {
			
			//Get the channels
			setChannels( getConnection().returnChannels() );
			
			//Set the linked channels
			setIRCChans( getChannels() );
			
			//And load all the users in
			setLoadedUsers(getConnection().returnPlayers());
			
		}
	}
	
	/**
	 * Save all the data
	 */
	public static void saveUsers() {
		
		//If we're using the SQL data type...
		if ( CyniChat.SQL == true ) {
			
			//Then save everyone
			getConnection().saveUsers( getLoadedUsers());
			
		} else {
			
			//Save only those that have been active
			getConnection().saveUsers( getActiveUsers());
			
		}
	}
	
	/**
	 * Save all the channels
	 */
	public static void saveChannels() {
		getConnection().saveChannels( getChannels() );
	}
	
	/**
	 * Generate a map of nicknames to channel names to make nick-name joining possible
	 */
	public static void channelTable() {
		
		//Iterate through the map of all channels
		for ( Map.Entry< String, Channel > channelSet : getChannels().entrySet() )
			
			//And put in the format <nick, name>
			getMatching().put( channelSet.getValue().getNick(),
					channelSet.getKey() );
		
	}
	
	/**
	 * Add a channel to all our items
	 * @param channel
	 */
	public static void addChannel(Channel channel) {
		getChannels().put(channel.getName().toLowerCase(), channel);
		getMatching().put( channel.getNick(), channel.getName() );
	}

	/**
	 * Return a channel
	 * @param name
	 * @return the asked channel if it exists
	 */
	public static Channel getChannel(String name){
		
		//Do a basic ask on the all channels
		Channel cn = getChannels().get(name);
		
		//If it's still null...
		if ( cn == null )
			//Check the nickname
			cn = getChannels().get( getMatching().get(name) );
		
		//Return what is left
		return cn;
		
	}
	
	/**
	 * Print all the details about every channel
	 * (Only visible if you have debug on)
	 */
	public static void printAllChannels() {
		
		//Iterate over all the channels
		for ( Map.Entry< String, Channel > entrySet : getChannels().entrySet() ) {
			
			//And debug everything
			CyniChat.printDebug( "Channel name : " + entrySet.getKey() );
			entrySet.getValue().printAll();
			
		}
		
	}
	
	/**
	 * Set all those channels which are linked in IRC
	 * @param chans : The channels that we're putting into our links
	 */
	public static void setIRCChans( Map<String, Channel> chans ) {
		
		Set<String> setter = chans.keySet();
		Iterator<String> iter = setter.iterator();
		
		Map<String, String> linkedChannels = new HashMap<String, String>();
		
		for ( Map.Entry< String, Channel > entrySet : chans.entrySet() ) {
			CyniChat.printDebug( "IRC name : "+ entrySet.getValue().getIRC() );
			CyniChat.printDebug( "CC name : "+ entrySet.getKey() );
			linkedChannels.put( entrySet.getValue().getIRC(), entrySet.getKey() );
		}
		
		setLinkedChans(linkedChannels);
	}
	
	/**
	 * Get a users details
	 * user may not be online!
	 * @param player
	 * @return
	 */
	public static UserDetails getDetails(String player){
		String uName = player.toLowerCase();
		UserDetails details = getLoadedUsers().get(uName);
		if(details == null){
			details = new UserDetails(); 
			getLoadedUsers().put(uName, details);
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
		getOnlineUsers().put(playerName,details);
		getActiveUsers().put(playerName, details);
		details.printAll();
		printAllUsers();
	}
	
	/**
	 * Unbind a player from being online
	 * @param player
	 */
	public static void unbindPlayer(Player player){
		getOnlineUsers().remove(player.getName().toLowerCase()).bindPlayer(null);
	}
	
	/**
	 * Get user object of an online user
	 * @param player
	 * @return
	 */
	public static UserDetails getOnlineDetails(Player player){
		return getOnlineUsers().get( player.getName().toLowerCase() );
	}

	/**
	 * Print all the users that are loaded and all the users that are online
	 * (Only visible if you have debug on)
	 */
	public static void printAllUsers() {
		if ( !getOnlineUsers().isEmpty() ) {
			for ( Map.Entry< String, UserDetails > entrySet : getOnlineUsers().entrySet() ) {
				CyniChat.printDebug( "Person : " + entrySet.getKey() );
				entrySet.getValue().printAll();
			}
		} else
			CyniChat.printDebug("No online users");
		
		if ( !getLoadedUsers().isEmpty() ) {
			for ( Map.Entry< String, UserDetails> entrySet : getLoadedUsers().entrySet() ) {
				CyniChat.printDebug( "Person : " + entrySet.getKey() );
				entrySet.getValue().printAll();
			}
		} else
			CyniChat.printDebug( "No loaded users" );
	}

	/**
	 * Delete an existing channel
	 * @param name : The name of the channel we're trying to delete
	 * @return : True when complete, false otherwise
	 */
	public static boolean deleteChannel(String name) {
		try {
			
			for ( Map.Entry< String, UserDetails > entrySet : getLoadedUsers().entrySet() ) {
				UserDetails current = entrySet.getValue();
				if ( current.clearChannel(name) == true )
					getActiveUsers().put(entrySet.getKey(), current);
			}
			
			getMatching().remove( getChannel( name.toLowerCase() ).getNick() );
			getChannels().remove( name.toLowerCase() );
			return true;
			
		} catch (NullPointerException e) {
			return false;
		}
		
	}
	
	/**
	 * If there is a nickname in the matching table which matches the input, say so
	 * @param nick : The nickname we're checking.
	 * @return true if it exists, false if not.
	 */
	public static boolean hasNick(String nick) {
		return getChannels().containsKey(nick);
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
	 * @return the matching
	 */
	public static Map<String, String> getMatching() {
		return matching;
	}
	
	/**
	 * @param aMatching the matching to set
	 */
	public static void setMatching(Map<String, String> aMatching) {
		matching = aMatching;
	}
	
	/**
	 * @return the linkedChans
	 */
	public static Map<String, String> getLinkedChans() {
		return linkedChans;
	}
	
	/**
	 * @param aLinkedChans the linkedChans to set
	 */
	public static void setLinkedChans(Map<String, String> aLinkedChans) {
		linkedChans = aLinkedChans;
	}
	
	/**
	 * @return the loadedUsers
	 */
	public static Map<String,UserDetails> getLoadedUsers() {
		return loadedUsers;
	}
	
	/**
	 * @param aLoadedUsers the loadedUsers to set
	 */
	public static void setLoadedUsers(Map<String,UserDetails> aLoadedUsers) {
		loadedUsers = aLoadedUsers;
	}
	
	/**
	 * @return the onlineUsers
	 */
	public static Map<String,UserDetails> getOnlineUsers() {
		return onlineUsers;
	}
	
	/**
	 * @param aOnlineUsers the onlineUsers to set
	 */
	public static void setOnlineUsers(Map<String,UserDetails> aOnlineUsers) {
		onlineUsers = aOnlineUsers;
	}
	
	/**
	 * @return the activeUsers
	 */
	public static Map<String,UserDetails> getActiveUsers() {
		return activeUsers;
	}
	
	/**
	 * @param aActiveUsers the activeUsers to set
	 */
	public static void setActiveUsers(Map<String,UserDetails> aActiveUsers) {
		activeUsers = aActiveUsers;
	}
	
	/**
	 * @return the Connection
	 */
	public static IDataManager getConnection() {
		return Connection;
	}
	
	/**
	 * @param aConnection the Connection to set
	 */
	public static void setConnection(IDataManager aConnection) {
		Connection = aConnection;
	}
	
}
