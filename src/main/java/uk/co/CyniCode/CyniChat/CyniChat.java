package uk.co.CyniCode.CyniChat;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import uk.co.CyniCode.CyniChat.Chatting.Chatter;
import uk.co.CyniCode.CyniChat.Command.AfkCommand;
import uk.co.CyniCode.CyniChat.Command.ChCommand;
import uk.co.CyniCode.CyniChat.Command.MeCommand;
import uk.co.CyniCode.CyniChat.Command.MsgCommand;
import uk.co.CyniCode.CyniChat.Command.QmCommand;
import uk.co.CyniCode.CyniChat.Command.RCommand;
import uk.co.CyniCode.CyniChat.bungee.Bungee;

/**
 * Base class for CyniChat. Main parts are the onEnable(), onDisable(), and the print areas at the moment.
 * @author Matthew Ball
 *
 */
public class CyniChat extends JavaPlugin{
	
	public Logger log = Logger.getLogger("Minecraft");
	public static IRCManager PBot;
	
	public static String version;
	public static String name;
	public static String Server;
	public static CyniChat self = null;
	public static Permission perms = null;
	
	public static Boolean JSON = false;
	public static Boolean SQL = false;
	public static Boolean IRC = false;
	public static Boolean bungee = false;
	public static Bungee bungeeInstance = null;
	
	public static String host;
	public static String username;
	public static String password ;
	public static int port;
	public static String database;
	public static String prefix;
	public static String def_chan;
	public static boolean debug;
	
	private static PluginManager pm;

	public static int counter;
	
	/**
	 * Is this command being used by bukkit as far as it knows?
	 * @param comm : The command we're checking off
	 * @return true if it does exist, false if it doesn't
	 */
	public static boolean ifCommandExists( String comm ) {
		if ( self.getServer().getPluginCommand( comm ) == null ) 
			return false;
		return true;
	}
	
	/**
	 * This is the onEnable class for when the plugin starts up. Basic checks are run for the version, name and information of the plugin, then startup occurs.
	 */
	@Override
	public void onEnable(){
		
		//Lets get the basics ready.
		version = this.getDescription().getVersion();
		name = this.getDescription().getName();
		self = this;
		log.info(name + " version " + version + " has started...");
		
		//Start up the managers and the configs and all that
		pm = getServer().getPluginManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		//Collect config data
		def_chan = getConfig().getString("CyniChat.channels.default").toLowerCase();
		if ( getConfig().getString("CyniChat.other.debug").equalsIgnoreCase("true") ) {
			debug = true;
			printInfo("Debugging enabled!");
		} else {
			debug = false;
			printInfo("Debugging disabled!");
		}
		if ( getConfig().getString("CyniChat.other.data").equalsIgnoreCase("mysql") ) {
			SQL = true;
			printInfo("MySQL storage enabled!");
		} else {
			JSON = true;
			printInfo("JSON storage enabled!");
		}
		if ( getConfig().getString( "CyniChat.other.bungee" ).equalsIgnoreCase( "true" ) ) {
			bungee = true;
			printInfo( "Bungee has been enabled" );
			bungeeInstance = new Bungee( this );
		} else {
			bungee = false;
			printInfo( "Bungee has been disabled" );
		}
		DataManager.start( this );
		DataManager.channelTable();
		
		if ( getConfig().getString("CyniChat.other.irc").equalsIgnoreCase("true") ) {
			printInfo( "Starting IRC..." );
			try {
				PBot = new IRCManager( this );
				PBot.loadChannels( DataManager.returnAllChannels() );
				IRC = true;
				printInfo( "IRC has started." );
			} catch ( Exception e ) {
				printSevere( "IRC has failed. Switching off..." );
				e.printStackTrace();
			}
		}
		
		//Start the command
		this.getCommand("ch").setExecutor(new ChCommand(this));
		this.getCommand("afk").setExecutor(new AfkCommand() );
		this.getCommand("qm").setExecutor(new QmCommand() );
		this.getCommand("me").setExecutor(new MeCommand() );
		this.getCommand("msg").setExecutor(new MsgCommand() );
		this.getCommand("r").setExecutor(new RCommand() );
		counter = 1;
		
		if ( PermissionManager.setupPermissions( this ) == false ) {
			killPlugin();
			return;
		}
		
		//Register the listeners.
		pm.registerEvents(new Chatter(), this);
		
		printInfo("CyniChat has been enabled!");
		
	}

	/**
	 * The routine to kill the connection cleanly and show that it has been done.
	 */
	@Override
	public void onDisable() {
		DataManager.saveChannelConfig();
		DataManager.saveUserDetails();
		if ( IRC == true ) PBot.stop();
		printInfo("CyniChat has been disabled!");
	}

	/**
	 * Prints a SEVERE warning to the console.
	 * @param line : This is the error message
	 */
	public static void printSevere(String line) {
		self.log.severe("[CyniChat] " + line);
	}

	/**
	 * Prints a WARNING to the console.
	 * @param line : This is the error message
	 */
	public static void printWarning(String line) {
		self.log.warning("[CyniChat] " + line);
	}

	/**
	 * Prints INFO to the console
	 * @param line : This is the information
	 */
	public static void printInfo(String line) {
		self.log.info("[CyniChat] " + line);
	}

	/**
	 * Prints DEBUG info to the console
	 * @param line : This contains the information to be outputted
	 */
	public static void printDebug(String line) {
		if ( debug == true ) {
			self.log.info("[CyniChat DEBUG] " + line);
		}
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
		if ( IRC == true ) PBot.stop();
		pm.disablePlugin( self );
	}
}