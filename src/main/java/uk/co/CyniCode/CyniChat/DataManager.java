package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

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
	
	private static Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from json 
	
	private static Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	private static File channelFile = null;
	private static File userFile = null;
	
	
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
		try {
			gson.toJson(channels,new FileWriter(channelFile));
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
		channels.put(channel.getName(), channel);
	}

	/**
	 * Return a channel
	 * @param name
	 * @return
	 */
	public static Channel getChannel(String name){
		return channels.get(name);
	}
	
	
	/**
	 * Load user details from file
	 * @param file
	 */
	public static void loadUserDetails(){
		try {
			userFile.createNewFile();
			loadedUsers = gson.fromJson(new FileReader(userFile), new TypeToken<Map<String,UserDetails>>(){}.getType());
			if(loadedUsers == null){loadedUsers = new HashMap<String, UserDetails>();
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
		try {
			gson.toJson(loadedUsers,new FileWriter(userFile));
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


	public static void setUserFile(File userFile) {
		DataManager.userFile = userFile;
	}
}