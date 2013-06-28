package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

public class FileHandling {
	
	private static Gson gson = new Gson();
	
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
	
	public static HashMap<String, Object> constructUserArray( UserDetails user ) {
		HashMap<String, Object> newUser = new HashMap<String, Object>();
		newUser.put("Name", user.getName() );
		newUser.put("CurrentChannel", user.getCurrentChannel() );
		newUser.put("Silenced", user.getSilenced().toString() );
		newUser.put("JoinedChannels", user.getAllChannels() );
		newUser.put("BannedChannels", user.getBannedChannels() );
		newUser.put("MutedChannels", user.getMutedChannels() );
		return newUser;
	}
	
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
	
	public static HashMap<String, Object> constructChannelArray( Channel chan ) {
		HashMap<String, Object> NewChannel = new HashMap<String, Object>();
		NewChannel.put("ID", chan.getID() );
		NewChannel.put("Name", chan.getName() );
		NewChannel.put("Nickname", chan.getNick() );
		NewChannel.put("Description", chan.getDesc() );
		NewChannel.put("Password", chan.getPass() );
		NewChannel.put("Protected", "false");
		NewChannel.put("Colour", chan.getColour().getChar() );
		return NewChannel;
	}

}
