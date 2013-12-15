package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * Class on help topics
 * 
 * @author CyniCode
 */
public class HelpCommand {
	
	/**
	 * Check the other augments in the help command. But only focus on the key
	 * @param player : The player to display this to
	 * @param key : The keyword we're working off
	 * @return true when complete
	 */
	public static boolean Command( CommandSender player, String key ) {
		
		//Let's ask if the key exists first
		if ( !key.equals( "" ) ) {
			
			//Then if it's asking about the channels
			if ( key.equalsIgnoreCase("channels") ) {
				HelpCommand.channels( player );
				return true;
			}
			
			//Or the users
			if ( key.equalsIgnoreCase("users") ) {
				HelpCommand.users( player );
				return true;
			}
			
			//Or admin commands
			if ( key.equalsIgnoreCase("admin") ) {
				HelpCommand.admin( player );
				return true;
			}
			
			//Or moderator commands
			if ( key.equalsIgnoreCase("mods") ) {
				HelpCommand.mods( player );
				return true;
			}
			
		}
		
		//If it's none of those, print the normal help
		// stuff anyway
		HelpCommand.info( player );
		return true;
		
	}
	
	/**
	 * Return the general help screen
	 * @param player : The player we're sending stuff to
	 * @return true when completed
	 */
	public static boolean info( CommandSender player ) {
		
		//Print out some basic headers first of all
		player.sendMessage( ChatColor.LIGHT_PURPLE + "===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch help   -> This screen");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch help channels -> Channel help menu");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch help users    -> Users help menu");
		
		//Now, if they're allowed... show them the moderator help menu option
		if ( !( player instanceof Player ) || 
				(CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.help")) )
			player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch help mods     -> Mod help menu");
		
		//Then the admin help option
		if ( !( player instanceof Player ) || 
				(CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.help") ) )
			player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch help admin    -> Admin help menu");
		
		//And finally the admin reload option...
		if ( !( player instanceof Player ) || 
				(CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.reload")) )
			player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch reload    -> Reload the config");
		
		//Now we've done... return
		return true;
		
	}
	
	/**
	 * If a player is an admin, this will be echoed
	 * @param player : This is who we're sending to
	 * @return true when completed
	 */
	public static boolean admin( CommandSender player ) {
		
		//Let's just make sure they can see this first
		if ( ( player instanceof Player ) && 
				(CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.help") == false ) )
			return false;
		
		//Show all the details
		player.sendMessage( ChatColor.LIGHT_PURPLE + "===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch create "
			+ChCommand.necessary("name", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.necessary("nickname", ChatColor.LIGHT_PURPLE)
			+" -> Create a channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch remove "
			+ChCommand.necessary("name", ChatColor.LIGHT_PURPLE)
			+" -> Remove a channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch save"
			+ " -> Saves all the objects to storage");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch gmute "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)
			+" -> Silence a player");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch gunmute "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)
			+" -> Undoes a silence on a player");
		return true;
	}
	
	/**
	 * If the player is a mod, this will be shown
	 * @param player : This is who we're sending to
	 * @return true when complete
	 */
	public static boolean mods( CommandSender player ) {
		
		//Let's make sure they can see it first...
		if ( ( player instanceof Player ) && 
				(CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.help" ) == false ) )
			return false;
		
		//Now print out all the possible commands for moderators
		player.sendMessage( ChatColor.LIGHT_PURPLE + "===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch kick "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Kick a player from the channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch ban "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Ban a player from the channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch unban "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Unban a player from the channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch mute "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Mute the player in this channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch unmute "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Unmute the player in this channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch promote "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Promote the player to a chat mod");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch demote "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Demote the player from a chat mod");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch set "
			+ChCommand.necessary("channel", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.necessary("option", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.necessary("new value", ChatColor.LIGHT_PURPLE) );
		player.sendMessage( ChatColor.LIGHT_PURPLE + " "
			+ " --> Change the color, password, description, ircchan or ircpass of a channel");
		return true;
	}
	
	/**
	 * Return the channel commands
	 * @param player : The player we're sending to
	 */
	public static void channels( CommandSender player ) {
		
		player.sendMessage( ChatColor.LIGHT_PURPLE + "===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch join "
			+ChCommand.necessary("channel", ChatColor.LIGHT_PURPLE)
			+" -> Changes your current channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch leave "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Leave the current or defined channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch qm "
			+ChCommand.necessary("channel", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.necessary("message", ChatColor.LIGHT_PURPLE)
			+" -> Sends one message to the defined channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch info "
			+ChCommand.optional("channel", ChatColor.LIGHT_PURPLE)
			+" -> Returns information about the current or defined channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch list "
			+ChCommand.optional("#page", ChatColor.LIGHT_PURPLE)
			+" -> List all available channels");
		
	}
	
	/**
	 * Return the user commands
	 * @param player : The player we're sending to
	 */
	public static void users( CommandSender player ) {
		
		player.sendMessage( ChatColor.LIGHT_PURPLE +"===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch who"
			+ " -> List all people in the current channel");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch ignore "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)
			+" -> Ignore one player across all channels");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/ch hear "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)
			+" -> Unigores a player across all channels");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/msg "
			+ChCommand.necessary("player", ChatColor.LIGHT_PURPLE)+" "
			+ChCommand.necessary("message", ChatColor.LIGHT_PURPLE)
			+" -> Send a message to one person");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/r "
			+ChCommand.necessary("message", ChatColor.LIGHT_PURPLE)
			+" -> Reply to a message from one person");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/afk "
			+ChCommand.optional("message", ChatColor.LIGHT_PURPLE)
			+" -> Sets yourself as afk (You can no-longer receive private messages)");
		player.sendMessage( ChatColor.LIGHT_PURPLE + "/me "
			+ChCommand.necessary("action", ChatColor.LIGHT_PURPLE)
			+" -> Transmits an action in the context of the player");
		
	}
	
}