package uk.co.CyniCode.CyniChat;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.CyniCode.CyniChat.Chatting.Chatter;
import uk.co.CyniCode.CyniChat.Command.AfkCommand;
import uk.co.CyniCode.CyniChat.Command.ChCommand;
import uk.co.CyniCode.CyniChat.Command.MeCommand;
import uk.co.CyniCode.CyniChat.Command.MsgCommand;
import uk.co.CyniCode.CyniChat.Command.RCommand;

/**
 * Base class for CyniChat. Main parts are the onEnable(), onDisable(), and the print areas at the moment.
 * @author Matthew Ball
 *
 */
public class CyniChat extends JavaPlugin{
	
	public CyniChat plugin;
	public Logger log = Logger.getLogger("Minecraft");
	
	public static String version;
	public static String name;
	public static String Server;
	public static CyniChat self = null;
	public static Permission perms = null;
	
	public static Boolean JSON = false;
	public static Boolean SQL = false;
	
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
        DataManager.start( this );
        DataManager.channelTable();
        
        //Start the command
        this.getCommand("ch").setExecutor(new ChCommand(this));
        this.getCommand("afk").setExecutor(new AfkCommand() );
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

	public static void reload() {
		try {
			self.onDisable();
			self.onEnable();
		} catch (NullPointerException e) {
			printSevere("Failiure of epic proportions!");
			e.printStackTrace();
		}
	}

	public static void killPlugin() {
		printSevere("Fatal error has occured...");
		printSevere("Killing...");
		pm.disablePlugin( self );
	}
    
}