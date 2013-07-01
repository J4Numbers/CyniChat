package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	//List of loaded channels
	private Map<String,Channel> channels = null;
	
	private Map<String,UserDetails> loadedUsers = new HashMap<String, UserDetails>();//User data loaded from 
	
	private Map<String,UserDetails> onlineUsers = new HashMap<String, UserDetails>();//Users who are online
	
	/**
	 * Loads the list of channels from a file 
	 * @param file channel definition file
	 */
	public void loadChannelConfig(File file){
		try {
			file.createNewFile();
			channels = gson.fromJson(new FileReader(file), new TypeToken<Map<String,Channel>>(){}.getType());
			if(channels.size() == 0){
				//Add default channel
				CyniChat.printInfo("Creating default global channel");
				addChannel(new Channel());
				saveChannelConfig(file);
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
	
	public void saveChannelConfig(File file){
		try {
			gson.toJson(channels,new FileWriter(file));
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addChannel(Channel channel) {
		channels.put(channel.getName(), channel);
	}

	public Channel getChannel(String name){
		return channels.get(name);
	}
	
	
	public void loadUserDetails(File file){
		try {
			file.createNewFile();
			loadedUsers = gson.fromJson(new FileReader(file), new TypeToken<Map<String,UserDetails>>(){}.getType());
			if(channels.size() == 0){
				//Add default channel
				CyniChat.printInfo("Creating default global channel");
				addChannel(new Channel());
				saveChannelConfig(file);
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
	
	public void saveUserDetails(File file){
		try {
			gson.toJson(loadedUsers,new FileWriter(file));
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public UserDetails getDetails(String player){
		String uName = player.toLowerCase();
		UserDetails details = loadedUsers.get(uName);
		if(details == null){
			details = new UserDetails();
			loadedUsers.put(uName, details);
		}
		
		return details;
	}
	
	public void bindPlayer(Player player){
		String playerName = player.getName().toLowerCase();
		UserDetails details = getDetails(playerName);
		onlineUsers.put(playerName,details);
	}
	
	public void unbindPlayer(Player player){
		onlineUsers.remove(player.getName().toLowerCase());
	}
	
	/**
	 * Get user object of an online user
	 * @param player
	 * @return
	 */
	public UserDetails getOnlineDetails(Player player){
		return onlineUsers.get(player.getName().toLowerCase());
	}
}