/**
 * Copyright 2013 CyniCode (numbers@cynicode.co.uk).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import uk.co.CyniCode.CyniChat.routing.ChatRouter;

/**
 * Base class for CyniChat. Main parts are the onEnable(), onDisable(), and the print areas at the moment.
 * 
 * @author CyniCode
 */
public class CyniChat extends JavaPlugin{
	
	public Logger log = Logger.getLogger("Minecraft");
	public static IrcManager PBot;
	
	public static String version;
	public static String name;
	public static String server;
	
	public static CyniChat self = null;
	public static PermissionManager perms = null;
	public static DataManager data = null;
	
	public static Boolean JSON = false;
	public static Boolean SQL = false;
	public static Boolean IRC = false;
	
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
		
		data = new DataManager( this );
		data.channelTable();
		
		if ( getConfig().getString("CyniChat.other.irc").equalsIgnoreCase("true") ) {
			printInfo( "Starting IRC..." );
			try {
				PBot = new IrcManager( this );
				PBot.loadChannels( data.getChannels() );
				IRC = true;
				ChatRouter.addRouter(ChatRouter.EndpointType.IRC,PBot);
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
		
		try {
			perms = new PermissionManager( this );
		} catch ( ClassNotFoundException e ) {
			killPlugin();
		}
		
		//Register the listeners.
		ServerChatListener listener = new ServerChatListener();
		ChatRouter.addRouter(ChatRouter.EndpointType.PLAYER,listener);
		
		pm.registerEvents(listener, this);
		
		printInfo("CyniChat has been enabled!");
		
	}

	/**
	 * The routine to kill the connection cleanly and show that it has been done.
	 */
	@Override
	public void onDisable() {
		
		data.saveChannels();
		data.saveUsers();
		
		if ( IRC == true ) PBot.stop();
		
		printInfo("CyniChat has been disabled!");
		
	}

	/**
	 * Prints a SEVERE warning to the console.
	 * @param line : This is the error message
	 */
	public static void printSevere(String line) {
		self.log.severe( String.format( "[CyniChat] %s", line ) );
	}

	/**
	 * Prints a WARNING to the console.
	 * @param line : This is the error message
	 */
	public static void printWarning(String line) {
		self.log.warning( String.format( "[CyniChat] %s", line ) );
	}

	/**
	 * Prints INFO to the console
	 * @param line : This is the information
	 */
	public static void printInfo(String line) {
		self.log.info( String.format( "[CyniChat] %s", line ) );
	}

	/**
	 * Prints DEBUG info to the console
	 * @param line : This contains the information to be outputted
	 */
	public static void printDebug(String line) {
		if ( debug == true )
			self.log.info( String.format( "[CyniChat DEBUG] %s", line ) );
		
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