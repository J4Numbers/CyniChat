package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DatabaseManagers.MySQLManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * A sane way to load channel and user data.
 * @author Tehbeard
 *
 */
public class DataManager {
	
	//Create a gson parser, only look at fields tagged with @Expose
	private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	//List of loaded channels
	private static Map<String,Channel> channels = null;
	private static Map<String, String> matching = new HashMap<String, String>();
	
	private static Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from sources 
	
	private static Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	private static Map<String,UserDetails> activeUsers = new HashMap<String, UserDetails>();//Users that were online
	
	private static File channelFile = null;
	private static File userFile = null;
	private static MySQLManager Connection;
	
	public static void start( CyniChat cynichat) {
		if ( CyniChat.SQL == true ) {
			Connection = new MySQLManager();
			if ( Connection.startConnection( cynichat ) == true ) {
				loadedUsers = Connection.returnPlayers();
				channels = Connection.returnChannels();
				return;
			}
		}
		setChannelFile(new File( cynichat.getDataFolder(),"channels.json"));
		loadChannelConfig();
		printAllChannels();
		
		setUserFile(new File( cynichat.getDataFolder(),"players.json"));
		loadUserDetails();
		printAllUsers();
	}
	
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
	 * Loads the list of channels from a file 
	 * @param file channel definition file
	 */
	public static void loadChannelConfig(){
		try {
			channelFile.createNewFile();
			channels = gson.fromJson(new FileReader(channelFile), new TypeToken<Map<String,Channel>>(){}.getType());
			if(channels == null){channels = new HashMap<String, Channel>();}
			if(channels.size() == 0){
				//Add default channel
				CyniChat.printInfo("Creating default global channel");
				addChannel(new Channel());
				saveChannelConfig();
			}
		} catch (JsonIOException e) {
			CyniChat.printSevere("IO error occured reading channel file");
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			CyniChat.printSevere("JSON is invalid");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			CyniChat.printSevere("File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save channel configuration to file
	 * @param file
	 */
	public static void saveChannelConfig(){
		if (CyniChat.SQL == true) {
			Connection.saveChannels(channels);
			return;
		}
		try {
			CyniChat.printDebug( channelFile.getAbsolutePath() );
			channels.get(channels.keySet().toArray()[0]).printAll();
			FileWriter fw = new FileWriter( channelFile );
			gson.toJson(channels, fw);
			fw.flush();
			fw.close();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static void printAllChannels() {
		for ( int i=0; i<channels.size(); i++ ) {
			CyniChat.printDebug( String.valueOf(i) );
			CyniChat.printDebug( String.valueOf( channels.keySet().toArray()[i] ) );
			channels.get( channels.keySet().toArray()[i] ).printAll();
		}
	}
	
	/**
	 * Load user details from file
	 * @param file
	 */
	public static void loadUserDetails(){
		try {
			userFile.createNewFile();
			loadedUsers = gson.fromJson(new FileReader(userFile), new TypeToken<Map<String,UserDetails>>(){}.getType());
			if(loadedUsers == null){loadedUsers = new HashMap<String, UserDetails>();}
		} catch (JsonIOException e) {
			CyniChat.printSevere("IO error occured reading channel file");
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			CyniChat.printSevere("JSON is invalid");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			CyniChat.printSevere("File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Save all user details to file 
	 * @param file
	 */
	public static void saveUserDetails(){
		if ( CyniChat.SQL == true ) {
			printAllUsers();
			Connection.saveUsers(activeUsers);
			activeUsers.clear();
			return;
		}
		try {
			FileWriter fw = new FileWriter( userFile );
			gson.toJson(loadedUsers, fw);
			fw.flush();
			fw.close();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public static void setChannelFile(File channelFile) {
		DataManager.channelFile = channelFile;
	}

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

	public static void setUserFile(File userFile) {
		DataManager.userFile = userFile;
	}

	public static Map<String, UserDetails> returnAllOnline() {
		return onlineUsers;
	}

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

	public static Map<String, Channel> returnAllChannels() {
		return channels;
	}

	public static boolean hasNick(String nick) {
		if ( matching.containsKey(nick) )
			return true;
		return false;
	}
}
