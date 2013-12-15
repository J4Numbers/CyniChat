package uk.co.CyniCode.CyniChat;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.CyniCode.CyniChat.Chatting.ServerChatListener;
import uk.co.CyniCode.CyniChat.Command.AfkCommand;
import uk.co.CyniCode.CyniChat.Command.ChCommand;
import uk.co.CyniCode.CyniChat.Command.MeCommand;
import uk.co.CyniCode.CyniChat.Command.MsgCommand;
import uk.co.CyniCode.CyniChat.Command.QmCommand;
import uk.co.CyniCode.CyniChat.Command.RCommand;
import uk.co.CyniCode.CyniChat.bungee.BungeeChannelProxy;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;

/**
 * Base class for CyniChat. Main parts are the onEnable(), onDisable(),
 * and the print areas at the moment. Other important bits can be seen 
 * just below the declaration of the class, these being the fields that
 * do a lot of the heavy lifting around and about the plugin.
 * 
 * @author CyniCode
 */
public class CyniChat extends JavaPlugin{
	
	/**
	 * This is the object that will log everything to the console
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	
	
	
	/**
	 * This is the version of the plugin we are running
	 */
	public static String version;
	
	/**
	 * This is the name of the plugin we are running
	 */
	public static String name;
	
	/**
	 * This is the name of the server we are running the plugin on
	 */
	public static String server;
	
	
	
	/**
	 * This is an instance of the plugin itself
	 */
	public static CyniChat self = null;
	
	/**
	 * This is an instance of the DataManager class
	 */
	public static DataManager data = null;
	
	/**
	 * This is an instance of the PermissionManager class
	 */
	public static PermissionManager perms = null;
	
	/**
	 * This is an instance of the BungeeChannelProxy class
	 */
	public static BungeeChannelProxy bungeeInstance = null;
	
	
	
	/**
	 * This is the configuration option of JSON
	 */
	public static Boolean JSON = false;
	
	/**
	 * This is the configuration option of SQL
	 */
	public static Boolean SQL = false;
	
	/**
	 * This is the configuration option for bungee
	 */
	public static Boolean bungee = false;
	
	/**
	 * This is a thing... possibly to be used at one point by
	 * the bungee integration with JSON that never happened
	 */
	public static Boolean connected = false;
	
	/**
	 * This goes along with bungee as the name that this server would
	 * have for the IRC client
	 */
	public static String bungeeName;
	
	
	
	/**
	 * This is the default channel that people should join to
	 * upon arrival
	 */
	public static String def_chan;
	
	/**
	 * And this is the debug option for whether or not users want
	 * to be spammed with all things debuggy
	 */
	public static boolean debug;
	
	
	
	/**
	 * An instance of bukkit's PluginManager
	 */
	private static PluginManager pm;
	
	
	
	/**
	 * A counter originally intended for providing the ID numbers
	 * of created channels for the server
	 */
	public static int counter;
	
	/**
	 * Is this command being used by bukkit as far as it knows?
	 * @param comm : The command we're checking off
	 * @return true if it does exist, false if it doesn't
	 */
	public static boolean ifCommandExists( String comm ) {
		return self.getServer().getPluginCommand( comm ) != null;
	}
	
	/**
	 * This is the onEnable class for when the plugin starts up.
	 * Basic checks are run for the version, name and information 
	 * of the plugin, then startup occurs.
	 */
	@Override
	public void onEnable(){
		
		//Lets get the basics ready.
		version = this.getDescription().getVersion();
		name = this.getDescription().getName();
		self = this;
		log.info( name + " version " + version + " has started..." );
		
		//Start up the managers and the configs and all that
		pm = getServer().getPluginManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		//Collect config data
		
		//So, ask what the default channel is first of all
		def_chan = getConfig().getString("CyniChat.channels.default").toLowerCase();
		
		//Then check whether or not we are enabling debug
		if ( getConfig().getString("CyniChat.other.debug").equalsIgnoreCase("true") ) {
			debug = true;
			printInfo("Debugging enabled!");
		} else {
			debug = false;
			printInfo("Debugging disabled!");
		}
		
		//Then for the data management stuffs...
		if ( getConfig().getString("CyniChat.other.data").equalsIgnoreCase("mysql") ) {
			SQL = true;
			printInfo("MySQL storage enabled!");
		} else {
			JSON = true;
			printInfo("JSON storage enabled!");
		}
		
		//Finally, ask about whether we are using bungee or not
		if ( getConfig().getString( "CyniChat.other.bungee" ).equalsIgnoreCase( "true" ) ) {
			
			bungee = true;
			printInfo( "Bungee has been enabled" );
			
			//Then instantiate all things bungee
			bungeeName = getConfig().getString( "CyniChat.bungee.name" );
			bungeeInstance = new BungeeChannelProxy( this );
			ChatRouter.addRouter(ChatRouter.EndpointType.BUNGEE, bungeeInstance);
			
		} else {
			bungee = false;
			printInfo( "Bungee has been disabled" );
		}
		
		//Make ourselves a new manager for the data
		data = new DataManager( this );
		data.channelTable();
		
		//Start the commands
		this.getCommand("ch").setExecutor(new ChCommand(this));
		this.getCommand("afk").setExecutor(new AfkCommand() );
		this.getCommand("qm").setExecutor(new QmCommand() );
		this.getCommand("me").setExecutor(new MeCommand() );
		this.getCommand("msg").setExecutor(new MsgCommand() );
		this.getCommand("r").setExecutor(new RCommand() );
		
		//And set the channel counter to one
		counter = 1;
		
		try {
			
			//Now, create a new permission manager
			perms = new PermissionManager( this );
			
		} catch ( ClassNotFoundException e ) {
			
			//And hope that we have Vault accessible to us...
			killPlugin();
			
		}
		
		//Register the listeners.
		ServerChatListener listener = new ServerChatListener();
		
		//Register all the listeners to their appropriate places
		ChatRouter.addRouter( ChatRouter.EndpointType.PLAYER,listener );
		pm.registerEvents(listener, this);
		
		//And say that we've booted
		printInfo("CyniChat has been enabled!");
		
	}

	/**
	 * The routine to kill the connection cleanly and show that it has been done.
	 */
	@Override
	public void onDisable() {
		
		//Save all the data
		data.saveChannels();
		data.saveUsers();
		
		printInfo("CyniChat has been disabled!");
		
	}

	/**
	 * Prints a SEVERE warning to the console.
	 * @param line : This is the error message
	 */
	public static void printSevere(String line) {
		log.severe( "[CyniChat] " + line );
	}

	/**
	 * Prints a WARNING to the console.
	 * @param line : This is the error message
	 */
	public static void printWarning(String line) {
		log.warning( "[CyniChat] " + line );
	}

	/**
	 * Prints INFO to the console
	 * @param line : This is the information
	 */
	public static void printInfo(String line) {
		log.info( "[CyniChat] " + line );
	}

	/**
	 * Prints DEBUG info to the console
	 * @param line : This contains the information to be outputted
	 */
	public static void printDebug(String line) {
		if ( debug == true )
			log.info( "[CyniChat DEBUG] " + line );
	}

	/**
	 * Reload the plugin completely, disabling it before re-enabling it
	 */
	public static void reload() {
		
		try {
			self.onDisable();
			self.onEnable();
		} catch (NullPointerException e) {
			printSevere("Failiure of epic proportions!");
			e.printStackTrace();
		}
		
	}

	/**
	 * Kill the plugin ungracefully.
	 */
	public static void killPlugin() {
		printSevere("Fatal error has occured...");
		printSevere("Killing...");
		
		pm.disablePlugin( self );
	}
}