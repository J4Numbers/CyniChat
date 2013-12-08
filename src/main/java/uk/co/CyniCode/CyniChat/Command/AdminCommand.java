package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Class for all the admin commands
 * (Create and delete)
 * @author Matthew Ball
 *
 */
public class AdminCommand {

	/**
	 * Create a channel
	 * @param player : The player attempting to create the channel
	 * @param name : The name of the channel
	 * @param nick : The nickname of the channel
	 * @param protect : Whether the channel is protected or not
	 * @return true when complete
	 */
	public static boolean create( CommandSender player, String name, String nick, Boolean protect ) {
		if ( player instanceof Player )
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.create") )
				return false;
		
		if ( CyniChat.data.getChannel(name) != null ) {
			player.sendMessage("This channel is already in existance");
			return true;
		}
		
		if ( CyniChat.data.hasNick( nick ) == true )
			nick = name.substring(0, 2);
		
		Channel newChan = new Channel( name.toLowerCase(), nick.toLowerCase(), protect );
		
		CyniChat.data.addChannel( newChan );
		player.sendMessage( "The channel: " + name + " has now been created" );
		
		if ( player instanceof Player ) {
			CyniChat.perms.addChannelPerms( player, newChan, protect );
			UserDetails current = CyniChat.data.getOnlineDetails( (Player) player);
			current.joinChannel(newChan, "");
		}
		
		return true;
	}

	/**
	 * Delete the channel from the plugin
	 * @param player : The player that's trying to execute the command
	 * @param name : The name of the channel we're trying to delete
	 * @return true when complete
	 */
	public static boolean remove( CommandSender player, String name ) {
		if ( player instanceof Player )
			if ( CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.remove") )
				return false;
		
		if ( CyniChat.data.deleteChannel( name ) == true ) {
			player.sendMessage("Channel has been removed");
			return true;
		}
		player.sendMessage("This channel doesn't exist");
		return true;
	}

	/**
	 * Return the information on how to create a channel
	 * @param player : The player we're returning the info for
	 * @return true when complete
	 */
	public static boolean createInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED + "Incorrect Command");
		player.sendMessage( "/ch create "+ChCommand.necessary("name")+" "+ChCommand.optional("nick") );
		return true;
	}

	/**
	 * Return the information on how to remove a channel
	 * @param player : The player we're returning the info for
	 * @return true when complete
	 */
	public static boolean removeInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED + "Incorrect Command");
		player.sendMessage( "/ch remove "+ChCommand.necessary("name") );
		return true;
	}
}
