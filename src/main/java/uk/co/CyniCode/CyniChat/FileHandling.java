package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

/**
 * This is the file handler class. In here are the methods for the creation and loading of files.
 * @author Matthew Ball
 */
public class FileHandling {
	
	private static Gson gson = new Gson();
	
	/**
	 * This is where we load the files. Given the path to a Json file, it will return the hashmap of all the details within.
	 * @param file : This is the path to the file that is being read/loaded
	 * @return HashMap of the details within the Json file
	 */
	private static HashMap<String, Object> loadDetailsFromJSON( File file ) {
		
		try {
			return gson.fromJson(new FileReader( file ), new TypeToken<HashMap<String, Object>>(){}.getType());
		} catch ( JsonIOException e ) {
			CyniChat.printSevere("An error occured reading " + file.toString());
			e.printStackTrace();
		} catch ( JsonSyntaxException e ) {
			CyniChat.printSevere("There is a problem with the syntax of " + file.toString() );
			e.printStackTrace();
		} catch ( FileNotFoundException e ) {
			CyniChat.printSevere(file.toString()+" not found");
			e.printStackTrace();
		} catch ( IOException e ) {
			CyniChat.printSevere("");
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * This is for when the player leaves the server. The current UserDetails class for them is dumped into the Json file.
	 * @param player : This is the player that we're editing.
	 */
	public static void dumpPlayerDetails( Player player ) {
		char initial = player.getName().toLowerCase().charAt(0);
		try {
			UserDetails left = CyniChat.user.get( player.getName() );
			File file = new File( CyniChat.self.getDataFolder(), "/players/"+initial+"/"+player.getName()+".json");
			FileWriter fw = new FileWriter( file );
			left.printAll();
			HashMap<String, Object> userArray = constructUserArray( left );
			gson.toJson( userArray, fw );
			fw.flush();
			fw.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is for when a player joins the server and needs all their details loading up.
	 * @param player : This is the player that we're trying to find.
	 */
	public static void loadPlayerDetails( Player player ) {
		
		char initial = player.getName().toLowerCase().charAt(0);
		try {
			UserDetails joined = new UserDetails();
			File dir = new File( CyniChat.self.getDataFolder(), "/players/"+initial);
			CyniChat.printDebug( CyniChat.self.getDataFolder() + "/players/"+initial+"/"+player.getName()+".json");
			File file = new File( CyniChat.self.getDataFolder(), "/players/"+initial+"/"+player.getName()+".json");
			if ( !dir.exists() ) {
				dir.mkdirs();
			}
			file.createNewFile();
			HashMap<String, Object> details = loadDetailsFromJSON( file );
			if ( details != null ) {
				CyniChat.printDebug( "name ::== " + details.get("name") );
				joined.load( details, player );
				joined.printAll();
			} else {
				joined.init( player );
				FileWriter fw = new FileWriter( file );
				CyniChat.printDebug( joined.getName() );
				CyniChat.printDebug(fw.toString() );
				HashMap<String, Object> userArray = constructUserArray( joined );
				gson.toJson( userArray, fw );
				fw.flush();
				fw.close();
			}
			CyniChat.user.put(player.getName(), joined );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Here is where we consruct the HashMap from the UserDetails class.
	 * @param user : This needs to belong to the correct user so that we can edit their file appropriately and store their data
	 * @return HashMap of the new json config file.
	 */
	public static HashMap<String, Object> constructUserArray( UserDetails user ) {
		HashMap<String, Object> newUser = new HashMap<String, Object>();
		newUser.put("Name", user.getName() );
		newUser.put("CurrentChannel", user.getCurrentChannel() );
		newUser.put("Silenced", user.getSilenced().toString() );
		newUser.put("CanIgnore", user.canIgnore().toString() );
		newUser.put("JoinedChannels", user.getAllChannels() );
		newUser.put("BannedChannels", user.getBannedChannels() );
		newUser.put("MutedChannels", user.getMutedChannels() );
		newUser.put("MutedPlayers", user.getIgnoring() );
		return newUser;
	}
	
	/**
	 * Load up the channels folders. If there are no channel folders, create a global channel.
	 * Assign them all to their HashMapped array in CyniChat root too.
	 */
	public static void loadChannels() {
		try {
			File dir = new File( CyniChat.self.getDataFolder(), "/channels/");
			if ( !dir.exists() ) {
				dir.mkdirs();
			}
			if ( dir.list().length > 0 ) {
				CyniChat.printDebug("Channels Found!");
				for ( File chan : dir.listFiles() ) {
					HashMap<String, Object> channel = loadDetailsFromJSON( chan );
					Channel iterChan = new Channel();
					iterChan.load( channel );
					CyniChat.counter++;
					iterChan.printAll();
					CyniChat.channels.put(iterChan.getName(), iterChan );
				}
			} else {
				CyniChat.printDebug("Channels Not Found!");
				Channel init = new Channel();
				init.init();
				FileWriter fw = new FileWriter( dir+"/global.json" );
				HashMap<String, Object> ChanArray = constructChannelArray( init );
				gson.toJson( ChanArray, fw );
				fw.flush();
				fw.close();
				init.printAll();
				CyniChat.channels.put(init.getName(), init);
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is the equivalent of the User construction array... except for channels of course
	 * @param chan : This is the Channel object that we're messing with and taking the data from
	 * @return HashMap of the channel data that we're going to be putting into the json
	 */
	public static HashMap<String, Object> constructChannelArray( Channel chan ) {
		HashMap<String, Object> NewChannel = new HashMap<String, Object>();
		NewChannel.put("ID", chan.getID() );
		NewChannel.put("Name", chan.getName() );
		NewChannel.put("Nickname", chan.getNick() );
		NewChannel.put("Description", chan.getDesc() );
		NewChannel.put("Password", chan.getPass() );
		NewChannel.put("Protected", chan.isProtected().toString() );
		NewChannel.put("Colour", chan.getColour().getChar() );
		return NewChannel;
	}

}
