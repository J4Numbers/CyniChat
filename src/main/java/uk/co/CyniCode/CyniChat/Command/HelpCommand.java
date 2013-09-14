package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.PermissionManager;

public class HelpCommand {
	
	/**
	 * Check the other augments in the help command. But only focus on the key
	 * @param player : The player to display this to
	 * @param key : The keyword we're working off
	 * @return true when complete
	 */
	public static boolean Command( CommandSender player, String key ) {
		if ( key != null ) {
			if ( key.equalsIgnoreCase("channels") ) {
				HelpCommand.channels( player );
				return true;
			}
			if ( key.equalsIgnoreCase("users") ) {
				HelpCommand.users( player );
				return true;
			}
			if ( key.equalsIgnoreCase("admin") ) {
				HelpCommand.admin( player );
				return true;
			}
			if ( key.equalsIgnoreCase("mods") ) {
				HelpCommand.mods( player );
				return true;
			}
		}
		HelpCommand.info( player );
		return true;
	}
	
	/**
	 * Return the general help screen
	 * @param player : The player we're sending stuff to
	 * @return true when completed
	 */
	public static boolean info( CommandSender player ) {
		player.sendMessage("===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage("/ch help          -> This screen");
		player.sendMessage("/ch help channels -> Channel help menu");
		player.sendMessage("/ch help users    -> Users help menu");
		if ( !( player instanceof Player ) || (PermissionManager.checkPerm( (Player) player, "cynichat.mod.help")) ) {
			player.sendMessage("/ch help mods     -> Mod help menu");
		}
		if ( !( player instanceof Player ) || (PermissionManager.checkPerm( (Player) player, "cynichat.admin.help") ) ) {
			player.sendMessage("/ch help admin    -> Admin help menu");
		}
		if ( !( player instanceof Player ) || (PermissionManager.checkPerm( (Player) player, "cynichat.admin.reload")) ) {
			player.sendMessage("/ch reload        -> Reload the config");
		}
		return true;
	}
	
	/**
	 * If a player is an admin, this will be echoed
	 * @param player : This is who we're sending to
	 * @return true when completed
	 */
	public static boolean admin( CommandSender player ) {
		player.sendMessage("===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage("/ch create "+ChCommand.necessary("name")+" "+ChCommand.necessary("nickname")+" -> Create a channel");
		player.sendMessage("/ch remove "+ChCommand.necessary("name")+" -> Remove a channel");
		player.sendMessage("/ch save -> Saves all the objects to storage");
		player.sendMessage("/ch gmute "+ChCommand.necessary("player")+" -> Silence a player");
		player.sendMessage("/ch gunmute "+ChCommand.necessary("player")+" -> Undoes a silence on a player");
		return true;
	}
	
	/**
	 * If the player is a mod, this will be shown
	 * @param player : This is who we're sending to
	 * @return true when complete
	 */
	public static boolean mods( CommandSender player ) {
		player.sendMessage("===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage("/ch kick "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Kick a player from the channel");
		player.sendMessage("/ch ban "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Ban a player from the channel");
		player.sendMessage("/ch unban "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Unban a player from the channel");
		player.sendMessage("/ch mute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Mute the player in this channel");
		player.sendMessage("/ch unmute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Unmute the player in this channel");
		player.sendMessage("/ch promote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Promote the player to a chat mod");
		player.sendMessage("/ch demote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Demote the player from a chat mod");
		player.sendMessage("/ch set "+ChCommand.necessary("channel")+" "+ChCommand.necessary("option")+" "+ChCommand.necessary("new value") );
		player.sendMessage("  --> Change the color, password or description of a channel");
		return true;
	}
	
	/**
	 * Return the channel commands
	 * @param player : The player we're sending to
	 * @return true when complete
	 */
	public static boolean channels( CommandSender player ) {
		player.sendMessage("===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage("/ch join "+ChCommand.necessary("channel")+" -> Changes your current channel");
		player.sendMessage("/ch leave "+ChCommand.optional("channel")+" -> Leave the current or defined channel");
		player.sendMessage("/ch qm "+ChCommand.necessary("channel")+" "+ChCommand.necessary("message")+" -> Sends one message to the defined channel");
		player.sendMessage("/ch info "+ChCommand.optional("channel")+" -> Returns information about the current or defined channel");
		player.sendMessage("/ch list "+ChCommand.optional("#page")+" -> List all available channels");
		return true;
	}
	
	/**
	 * Return the user commands
	 * @param player : The player we're sending to
	 * @return true when complete
	 */
	public static boolean users( CommandSender player ) {
		player.sendMessage("===============CyniChat===============");
		player.sendMessage("");
		player.sendMessage("/ch who -> List all people in the current channel");
		player.sendMessage("/ch ignore "+ChCommand.necessary("player")+" -> Ignore one player across all channels");
		player.sendMessage("/ch hear "+ChCommand.necessary("player")+" -> Unigores a player across all channels");
		player.sendMessage("/msg "+ChCommand.necessary("player")+" "+ChCommand.necessary("message")+" -> Send a message to one person");
		player.sendMessage("/r "+ChCommand.necessary("message")+" -> Reply to a message from one person");
		player.sendMessage("/afk "+ChCommand.optional("message")+" -> Sets yourself as afk (You can no-longer receive private messages)");
		player.sendMessage("/me "+ChCommand.necessary("action")+" -> Transmits an action in the context of the player");
		return true;
	}

}
