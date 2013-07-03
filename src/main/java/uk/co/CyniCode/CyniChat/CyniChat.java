package uk.co.CyniCode.CyniChat;

import java.io.File;
import java.util.logging.Logger;

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
	
	public static String host;
	public static String username;
	public static String password ;
	public static int port;
	public static String database;
	public static String prefix;
	public static String def_chan;
	public static boolean debug;

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
        PluginManager pm = getServer().getPluginManager();
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        //Collect config data
        def_chan = getConfig().getString("CyniChat.channels.default");
        if ( getConfig().getString("CyniChat.other.debug").equalsIgnoreCase("true") ) {
        	debug = true;
        	printInfo("Debugging enabled!");
        } else {
        	debug = false;
        	printInfo("Debugging disabled!");
        }
        
        //Start the command
        this.getCommand("ch").setExecutor(new ChCommand(this));
        this.getCommand("afk").setExecutor(new AfkCommand() );
        this.getCommand("me").setExecutor(new MeCommand() );
        this.getCommand("msg").setExecutor(new MsgCommand() );
        this.getCommand("r").setExecutor(new RCommand() );
        counter = 1;
        
        DataManager.setChannelFile(new File(getDataFolder(),"channels.json"));
        DataManager.loadChannelConfig();
        
        DataManager.setUserFile(new File(getDataFolder(),"players.json"));
        DataManager.loadUserDetails();
        
        printInfo("CyniChat has been enabled!");
        
        //Register the listeners.
        pm.registerEvents(new Chatter(), this);
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
    
}