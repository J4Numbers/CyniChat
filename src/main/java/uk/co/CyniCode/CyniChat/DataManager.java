package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import uk.co.CyniCode.CyniChat.Channel.Channel;

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
	
	/**
	 * Loads the list of channels from a file 
	 * @param file channel definition file
	 */
	public void loadChannelConfig(File file){
		try {
			channels = gson.fromJson(new FileReader(file), new TypeToken<Map<String,Channel>>(){}.getType());
		} catch (JsonIOException e) {
			CyniChat.printSevere("IO error occured reading channel file");
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			CyniChat.printSevere("JSON is invalid");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			CyniChat.printSevere("File not found!");
			e.printStackTrace();
		}
	}
	
	public Channel getChannel(String name){
		return channels.get(name);
	}
}
