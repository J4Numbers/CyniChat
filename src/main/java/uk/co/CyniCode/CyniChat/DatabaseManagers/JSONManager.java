package uk.co.CyniCode.CyniChat.DatabaseManagers;

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

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public class JSONManager implements IDataManager {

	//Create a gson parser, only look at fields tagged with @Expose
	private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	private File channelFile = null;
	private File userFile = null;
	
	public boolean startConnection( CyniChat plugin ) {
		setChannelFile(new File( plugin.getDataFolder(),"channels.json"));
		returnChannels();
		
		setUserFile(new File( plugin.getDataFolder(),"players.json"));
		returnPlayers();
		
		return true;
	}
	
	/**
	 * Set the channel file as defined
	 * @param channelFile : Absolute path to the channel file
	 */
	public void setChannelFile(File channelFile) {
		this.channelFile = channelFile;
	}
	
	/**
	 * Set a new file for the user details
	 * @param userFile : The absolute path of a new users file
	 */
	public void setUserFile(File userFile) {
		this.userFile = userFile;
	}
	
	/**
	 * Loads the list of channels from a file 
	 * @param file channel definition file
	 * @return 
	 */
	public Map<String, Channel> returnChannels(){
		Map<String, Channel> channels = new HashMap<String, Channel>();
		try {
			channelFile.createNewFile();
			channels = gson.fromJson(new FileReader(channelFile), new TypeToken<Map<String,Channel>>(){}.getType());
			if(channels == null){channels = new HashMap<String, Channel>();}
			if(channels.size() == 0){
				//Add default channel
				CyniChat.printInfo("Creating default global channel");
				channels.put( "global", new Channel() );
			}
			return channels;
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
		return null;
	}
	
	/**
	 * Save channel configuration to file
	 * @param file
	 */
	public void saveChannels( Map<String, Channel> channels ) {
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
	
	public Map<String, UserDetails> returnPlayers() {
		
		Map<String, UserDetails> loadedUsers = new HashMap<String, UserDetails>();
		
		try {
			userFile.createNewFile();
			loadedUsers = gson.fromJson(new FileReader(userFile), new TypeToken<Map<String,UserDetails>>(){}.getType());
			if(loadedUsers == null){loadedUsers = new HashMap<String, UserDetails>();}
			
			return loadedUsers;
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
		return null;
	}
	
	public void saveUsers( Map<String, UserDetails> loadedUsers ){
		try {
			FileWriter fw = new FileWriter( userFile );
			gson.toJson( loadedUsers, fw );
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
	
}
