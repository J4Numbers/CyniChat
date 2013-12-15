package uk.co.CyniCode.CyniChat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DatabaseManagers.IDataManager;
import uk.co.CyniCode.CyniChat.DatabaseManagers.JSONManager;
import uk.co.CyniCode.CyniChat.DatabaseManagers.MySQLManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * A sane way to load channel and user data.
 * 
 * @author Tehbeard
 * @author CyniCode
 */
public class DataManager {
	
	/**
	 * The list of all channels that are loaded
	 */
	private Map<String,Channel> channels = null;
	
	/**
	 * The nickname-to-name matching table... I think
	 */
	private Map<String, String> matching = new HashMap<String, String>();
	
	/**
	 * Any channels that are also linked to IRC
	 */
	private Map<String, String> linkedChans = null;
	
	
	
	/**
	 * The list of everyone that is in the data structure,
	 * regardless of whether they're online or not
	 */
	private Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from sources 
	
	/**
	 * The list of every online user that is currently
	 *  ... well... online
	 */
	private Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	/**
	 * The active users. These are people that have been online since
	 * the last reload and save so we know who to update
	 */
	private Map<String,UserDetails> activeUsers = new HashMap<String, UserDetails>();//Users that were online
	
	/**
	 * The connection bit that lets us access the data
	 */
	private IDataManager connection;
	
	/**
	 * Let's start grabbing all the data we can. Check which method of storage we're using
	 * Then get the relevant details from that medium.
	 * @param cynichat : This is an instance of the plugin
	 */
	public DataManager( CyniChat cynichat) {
		
		//If we're using SQL as a data type...
		if ( CyniChat.SQL == true ) {
			
			//Use the MySQL manager
			this.connection = new MySQLManager();
			
		} else {
			
			//Otherwise, we're using JSON
			this.connection = new JSONManager();
			
		}
		
		//Start the connection and make sure we /are/ connected
		if ( this.connection.startConnection(cynichat) == true ) {
			
			//Get the channels
			this.channels = this.connection.returnChannels();
			
			//Set the linked channels
			setIRCChans( this.channels );
			
			//And load all the users in
			this.loadedUsers  = this.connection.returnPlayers();
			
		}
		
		cynichat.getServer().getScheduler()
			.scheduleSyncRepeatingTask( cynichat,
				this.connection.getBooster(),
				6000L,
				6000L);
	}
	
	/**
	 * Clear the active users map to those that are still using it
	 */
	public void flushData() {
		
		setActiveUsers( getOnlineUsers() );
		CyniChat.printInfo( "CyniChat has saved..." );
		
	}
	
	/**
	 * Reload all the players on the server into the plugin
	 * just in case we had a derp at some point or another.
	 */
	public void reloadPlayers() {
		
		for ( Player thisPlayer : CyniChat.self.getServer().getOnlinePlayers() ) {
			bindPlayer( thisPlayer );
			CyniChat.printDebug( "Rebound player " + thisPlayer.getDisplayName() );
		}
		
	}
	
	/**
	 * Save all the data
	 */
	public void saveUsers() {
		
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
	public void saveChannels() {
		getConnection().saveChannels( getChannels() );
	}
	
	/**
	 * Generate a map of nicknames to channel names to make nick-name joining possible
	 */
	public void channelTable() {
		
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
	public void addChannel(Channel channel) {
		getChannels().put(channel.getName().toLowerCase(), channel);
		getMatching().put( channel.getNick(), channel.getName() );
	}

	/**
	 * Return a channel
	 * @param name
	 * @return the asked channel if it exists
	 */
	public Channel getChannel(String name){
		
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
	public void printAllChannels() {
		
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
	public final void setIRCChans( Map<String, Channel> chans ) {
		
		//Let's start up a new map for all the channels
		Map<String, String> linkedChannels = new HashMap<String, String>();
		
		//Then for all the channels that we've been given...
		for ( Map.Entry< String, Channel > entrySet : chans.entrySet() ) {
			
			//debug out the basic channel and the irc channel
			CyniChat.printDebug( "IRC name : "+ entrySet.getValue().getIRC() );
			CyniChat.printDebug( "CC name : "+ entrySet.getKey() );
			
			//then put it into the map as <irc>:<cynichat>
			linkedChannels.put( entrySet.getValue().getIRC(), entrySet.getKey() );
			
		}
		
		//Finally, set the linked channels as this
		setLinkedChans(linkedChannels);
		
	}
	
	/**
	 * Get a users details
	 * user may not be online!
	 * @param player
	 * @return
	 */
	public UserDetails getDetails(String player){
		
		//Alright... let's firstly put the name to lowercase
		String uName = player.toLowerCase();
		
		//before we check it against the loaded users
		UserDetails details = getLoadedUsers().get(uName);
		
		//If they're not in the loaded users...
		if(details == null){
			
			//Then make a new UserDetails thing
			details = new UserDetails(); 
			
			//And put it as a new entry into the loaded users
			getLoadedUsers().put( uName, details );
			
		}
		
		//Finally, return whatever details there are to collect
		return details;
		
	}
	
	/**
	 * Bind the user details of a player to a player object
	 * @param player
	 */
	public void bindPlayer(Player player){
		
		//Firstly, get the lowercase name of the player from the object
		String playerName = player.getName().toLowerCase();
		
		//then get the player from all our details
		UserDetails details = getDetails(playerName);
		
		//Bind the player to the object
		details.bindPlayer(player);
		
		//Then bind them to the maps of players
		getOnlineUsers().put(playerName, details);
		getActiveUsers().put(playerName, details);
		
		//And put out some debug just in case
		details.printAll();
		printAllUsers();
		
	}
	
	/**
	 * Unbind a player from being online
	 * @param player
	 */
	public void unbindPlayer(Player player){
		
		//Get the details of the player in question
		UserDetails details = getOnlineUsers().get( player.getName().toLowerCase() );
		
		//Unbind the player object from the details
		details.unbindPlayer();
		
		//Then remove the player from our list of people on the server
		// at the current time.
		getOnlineUsers().remove( player.getName().toLowerCase() );
		
	}
	
	/**
	 * Get user object of an online user
	 * @param player
	 * @return
	 */
	public UserDetails getOnlineDetails(Player player){
		return getOnlineUsers().get( player.getName().toLowerCase() );
	}

	/**
	 * Print all the users that are loaded and all the users that are online
	 * (Only visible if you have debug on)
	 */
	public void printAllUsers() {
		
		//Alright, let's check the online user map first
		if ( !getOnlineUsers().isEmpty() ) {
			
			//For everyone who is online...
			for ( Map.Entry< String, UserDetails > entrySet : getOnlineUsers().entrySet() ) {
				
				//Print out all the debug!
				CyniChat.printDebug( "Person : " + entrySet.getKey() );
				entrySet.getValue().printAll();
				
			}
			
		} else
			//Otherwise, announce that no-one is online
			CyniChat.printDebug("No online users");
		
		//Now check the loaded users map...
		if ( !getLoadedUsers().isEmpty() ) {
			
			//And for every one of them...
			for ( Map.Entry< String, UserDetails> entrySet : getLoadedUsers().entrySet() ) {
				
				//Add them to the debug pile!
				CyniChat.printDebug( "Person : " + entrySet.getKey() );
				entrySet.getValue().printAll();
				
			}
			
		} else
			//And otherwise, say that no-one is loaded in
			CyniChat.printDebug( "No loaded users" );
		
	}
	
	/**
	 * Delete an existing channel
	 * @param name : The name of the channel we're trying to delete
	 * @return : True when complete, false if the channel did not exist
	 */
	public boolean deleteChannel(String name) {
		
		try {
			
			//Iterate through everyone on the server...
			for ( Map.Entry< String, UserDetails > entrySet : getLoadedUsers().entrySet() )
				
				//And wipe the channel from their records
				entrySet.getValue().clearChannel(name);
			
			//Then remove the channel itself from the matching
			// map and then again from the usual channel map  
			getMatching().remove( getChannel( name.toLowerCase() ).getNick() );
			getChannels().remove( name.toLowerCase() );
			
			//Then return true to say we were successful
			return true;
			
		} catch (NullPointerException e) {
			
			//Or false if we were not...
			return false;
			
		}
		
	}
	
	/**
	 * If there is a nickname in the matching table which matches the input, say so
	 * @param nick : The nickname we're checking.
	 * @return true if it exists, false if not.
	 */
	public boolean hasNick(String nick) {
		return getChannels().containsKey(nick);
	}
	
	/**
	 * @return the channels
	 */
	public Map<String,Channel> getChannels() {
		return channels;
	}
	
	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Map<String,Channel> channels) {
		this.channels = channels;
	}
	
	/**
	 * @return the matching
	 */
	public Map<String, String> getMatching() {
		return matching;
	}
	
	/**
	 * @param matching the matching to set
	 */
	public void setMatching(Map<String, String> matching) {
		this.matching = matching;
	}
	
	/**
	 * @return the linkedChans
	 */
	public Map<String, String> getLinkedChans() {
		return linkedChans;
	}
	
	/**
	 * @param linkedChans the linkedChans to set
	 */
	public void setLinkedChans(Map<String, String> linkedChans) {
		this.linkedChans = linkedChans;
	}
	
	/**
	 * @return the loadedUsers
	 */
	public Map<String,UserDetails> getLoadedUsers() {
		return loadedUsers;
	}
	
	/**
	 * @param loadedUsers the loadedUsers to set
	 */
	public void setLoadedUsers(Map<String,UserDetails> loadedUsers) {
		this.loadedUsers = loadedUsers;
	}
	
	/**
	 * @return the onlineUsers
	 */
	public Map<String,UserDetails> getOnlineUsers() {
		return onlineUsers;
	}
	
	/**
	 * @param onlineUsers the onlineUsers to set
	 */
	public void setOnlineUsers(Map<String,UserDetails> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}
	
	/**
	 * @return the activeUsers
	 */
	public Map<String,UserDetails> getActiveUsers() {
		return activeUsers;
	}
	
	/**
	 * @param activeUsers the activeUsers to set
	 */
	public void setActiveUsers(Map<String,UserDetails> activeUsers) {
		this.activeUsers = activeUsers;
	}
	
	/**
	 * @return the Connection
	 */
	public IDataManager getConnection() {
		return connection;
	}
	
	/**
	 * @param connection the Connection to set
	 */
	public void setConnection(IDataManager connection) {
		this.connection = connection;
	}
	
}