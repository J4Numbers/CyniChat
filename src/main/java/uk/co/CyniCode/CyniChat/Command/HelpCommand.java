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
		player.sendMessage("/cyn help          -> This screen");
		player.sendMessage("/cyn help channels -> Channel help menu");
		player.sendMessage("/cyn help users    -> Users help menu");
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.mod.help")) {
			player.sendMessage("/cyn help mods     -> Mod help menu");
		}
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.help") ) {
			player.sendMessage("/cyn help admin    -> Admin help menu");
		}
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.reload")) {
			player.sendMessage("/cyn reload        -> Reload the config");
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
		player.sendMessage("/cyn create "+ChCommand.necessary("name")+" "+ChCommand.necessary("nickname")+" "+ChCommand.necessary("color")+" "+ChCommand.optional("password")+" -> Create a channel");
		player.sendMessage("/cyn remove "+ChCommand.necessary("name")+" -> Remove a channel");
		player.sendMessage("/cyn gmute "+ChCommand.necessary("player")+" -> Silence a player");
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
		player.sendMessage("/cyn kick "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" "+ChCommand.optional("reason")+" -> Kick a player from the channel");
		player.sendMessage("/cyn ban "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" "+ChCommand.optional("reason")+" -> Ban a player from the channel");
		player.sendMessage("/cyn mute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Mute the player in this channel");
		player.sendMessage("/cyn unmute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Unmute the player in this channel");
		player.sendMessage("/cyn promote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Promote the player to a chat mod");
		player.sendMessage("/cyn demote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel")+" -> Demote the player from a chat mod");
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
		player.sendMessage("/cyn join "+ChCommand.necessary("channel")+" -> Changes your current channel");
		player.sendMessage("/cyn leave "+ChCommand.optional("channel")+" -> Leave the current or defined channel");
		player.sendMessage("/cyn list "+ChCommand.optional("#page")+" -> List all available channels");
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
		player.sendMessage("/cyn who -> List all people in the current channel");
		player.sendMessage("/cyn mute "+ChCommand.necessary("player")+" -> Ignore one player across all channels");
		player.sendMessage("/cyn tell "+ChCommand.necessary("player")+" "+ChCommand.necessary("message")+" -> Send a message to one person");
		return true;
	}

}
