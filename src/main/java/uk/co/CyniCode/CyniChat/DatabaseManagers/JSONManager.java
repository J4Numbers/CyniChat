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
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * A class for dealing with the JSON type of data management
 * 
 * @author CyniCode
 */
public class JSONManager implements IDataManager {

	/**
	 * Create a gson parser, 
	 * only look at fields tagged with @Expose
	 */
	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
	/**
	 * @return the gson
	 */
	public Gson getGson() {
		return gson;
	}
	
	/**
	 * @param gson the gson to set
	 */
	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
	
	/**
	 * The file that belongs to the channels
	 */
	private File channelFile = null;
	
	/**
	 * The file that belongs to the users
	 */
	private File userFile = null;
	
	
	
	/**
	 * Let's get the connection going and make all the new files/
	 * get all the old ones.
	 * @param plugin : The instance of the plugin we need to get
	 *  the folder information
	 * @return true when we're done
	 */
	public boolean startConnection( CyniChat plugin ) {
		
		//Alright, let's get the channel file and the
		// channels contained within
		setChannelFile(new File( plugin.getDataFolder(),"channels.json"));
		returnChannels();
		
		//Then let's do the same with the players file
		// and all those contained within again
		setUserFile(new File( plugin.getDataFolder(),"players.json"));
		returnPlayers();
		
		//Return true because the interface tells us to
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
	 * @return the map of all channels
	 */
	public Map<String, Channel> returnChannels(){
		
		//Let's initialise it first
		Map<String, Channel> channels;
		
		try {
			
			//Then try to make a new file...
			// if one is already there, this will have
			// no effect whatsoever
			getChannelFile().createNewFile();
			
			//Get all the channels from the file
			channels = getGson().fromJson(new FileReader(getChannelFile()), 
				new TypeToken<Map<String,Channel>>(){}.getType());
			
			//If there were no channels... make a new map of them
			if ( channels == null )
				channels = new HashMap<String, Channel>();
			
			//And if the map is completely empty...
			if( channels.isEmpty() ){
				
				//Add a default channel
				CyniChat.printInfo("Creating default global channel");
				channels.put( "global", new Channel() );
				
			}
			
			//Then return the map of all the channels
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
		
		//If we had an error, then we're going to have to
		// return nothing to show that there was such an 
		// error
		return null;
		
	}
	
	/**
	 * Save channel configuration to file
	 * @param channels
	 */
	public void saveChannels( Map<String, Channel> channels ) {
		try {
			
			//Now save all the channels
			
			//First print out the file path we're saving to as
			// debug
			CyniChat.printDebug( getChannelFile().getAbsolutePath() );
			
			//Then print out the first channel as debug too
			channels.get(channels.keySet().toArray()[0]).printAll();
			
			//Create a new writer
			FileWriter fw = new FileWriter( getChannelFile());
			
			//And write all the channels to the file
			getGson().toJson(channels, fw);
			
			//Flush the change and close the writer
			fw.flush();
			fw.close();
			
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Return all the players that are accessible within the 
	 * player configuration file
	 * @return the map of all players available
	 */
	public Map<String, UserDetails> returnPlayers() {
		
		//Make a new map first of all
		Map<String, UserDetails> loadedUsers;
		
		try {
			
			//Then create/access a new file
			getUserFile().createNewFile();
			
			//And get all the player information from there
			loadedUsers = getGson().fromJson(new FileReader(getUserFile()),
				new TypeToken<Map<String,UserDetails>>(){}.getType());
			
			//If there were no users to be loaded...
			if ( loadedUsers == null )
				
				//Then create a new map for all the users anyway
				loadedUsers = new HashMap<String, UserDetails>();
			
			//Finally, return all those users
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
		
		//If an error occured, then we're going to have to  
		// return nothing as a result of having nothing else
		// to return instead.
		return null;
		
	}
	
	/**
	 * Save all the users to the file again
	 * @param loadedUsers : Those that we're saving to
	 *  the file
	 */
	public void saveUsers( Map<String, UserDetails> loadedUsers ){
		try {
			
			//Create a new writer for the file
			FileWriter fw = new FileWriter( getUserFile());
			
			//Write all the users to said file
			getGson().toJson( loadedUsers, fw );
			
			//Flush and close the writer
			fw.flush();
			fw.close();
			
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Provide the thing that will allow us to save stuff every now
	 * and again.
	 * @return The runnable for the job
	 */
	public Runnable getBooster() {
		return new fileBooster();
	}
	
	/**
	 * We need something to boost the file every now and again...
	 * This is that
	 */
	public class fileBooster implements Runnable {
		
		/**
		 * Run the methods we need to run in order
		 * to save the files and flush the data
		 */
		public void run() {
			
			saveChannels( CyniChat.data.getChannels() );
			saveUsers( CyniChat.data.getLoadedUsers() );
			
			CyniChat.data.flushData();
			
		}
		
	};
	
	/**
	 * @return the channelFile
	 */
	public File getChannelFile() {
		return channelFile;
	}

	/**
	 * @return the userFile
	 */
	public File getUserFile() {
		return userFile;
	}
	
}