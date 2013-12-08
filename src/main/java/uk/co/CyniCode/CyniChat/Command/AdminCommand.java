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
 * 
 * @author CyniCode
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
		
		//If the player is a player
		if ( player instanceof Player )
			//Can they create a channel?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.create") )
				//Nope? Bad player!
				return false;
		
		//Ask if there is another channel by this name in existance already
		if ( CyniChat.data.getChannel(name) != null ) {
			player.sendMessage("This channel is already in existance");
			return true;
		}
		
		//If the nickname already exists, take the first two letters
		// of the channel name instead of just the first.
		// NOTE: To be changed to something that will work for making
		//  completely unique channel nicknames
		if ( CyniChat.data.hasNick( nick ) == true )
			nick = name.substring(0, 2);
		
		//Then create the new channel
		Channel newChan = new Channel( name.toLowerCase(), nick.toLowerCase(), protect );
		
		//And add it to the DataManager
		CyniChat.data.addChannel( newChan );
		
		//Tell the player that the channel has been created
		player.sendMessage( "The channel: '" + name + "' has now been created" );
		
		//And if the player is actually a player...
		if ( player instanceof Player ) {
			
			//Give them moderator permissions in that channel
			CyniChat.perms.addChannelPerms( player, newChan, protect );
			
			//And make them join the channel
			UserDetails current = CyniChat.data.getOnlineDetails( (Player) player);
			current.joinChannel( newChan, "" );
			
		}
		
		//Then return true
		return true;
		
	}

	/**
	 * Delete the channel from the plugin
	 * @param player : The player that's trying to execute the command
	 * @param name : The name of the channel we're trying to delete
	 * @return true when complete
	 */
	public static boolean remove( CommandSender player, String name ) {
		
		//If the player is actually a player...
		if ( player instanceof Player )
			//Then ask if they have the permissions to remove a channel
			if ( CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.remove") )
				//Nope? Fuck them.
				return false;
		
		//If the deletion of the channel was successful...
		if ( CyniChat.data.deleteChannel( name ) ) {
			
			//Tell the player and return
			player.sendMessage("Channel has been removed");
			return true;
		}
		
		//Otherwise, the channel wasn't there to be deleted
		// so tell the player that and return to them
		player.sendMessage("This channel doesn't exist");
		return true;
		
	}
	
	/**
	 * Return the information on how to create a channel
	 * @param player : The player we're returning the info for
	 */
	public static void createInfo( CommandSender player ) {
		
		//Tell the player that their command was invalid
		player.sendMessage( ChatColor.RED + "Incorrect Command" );
		
		//Then give them the correct syntax of the command
		player.sendMessage( ChatColor.RED + "/ch create "+ChCommand.necessary("name", ChatColor.RED )
			+" "+ChCommand.optional("nick", ChatColor.RED ) );
		
	}

	/**
	 * Return the information on how to remove a channel
	 * @param player : The player we're returning the info for
	 */
	public static void removeInfo( CommandSender player ) {
		
		//Tell the player that the command was invalid
		player.sendMessage( ChatColor.RED + "Incorrect Command" );
		
		//Then give them the correct syntax for the command
		player.sendMessage( ChatColor.RED + "/ch remove "+ChCommand.necessary("name", ChatColor.RED ) );
		
	}
}
