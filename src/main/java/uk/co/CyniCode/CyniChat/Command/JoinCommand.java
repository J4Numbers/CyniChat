package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * The class for joining a channel
 * 
 * @author CyniCode
 */
public class JoinCommand {
	
	/**
	 * When a player joins a channel, this is ran. If the channel is non-existant, the the player is notified.
	 * @param player : We need this to alter the players UserDetails file
	 * @param channel : We need this to check the password and stuffs
	 * @param pass : For use in comparison
	 */
	public static void join( CommandSender player, String channel, String pass ) {
		
		//Ask if the channel exists first
		if ( CyniChat.data.getChannel( channel.toLowerCase() ) != null ) {
			
			//Then get the details of the player so we know who
			// we're dealing with
			UserDetails user = CyniChat.data.getDetails( player.getName() );
			
			//The same goes for the channel
			Channel NewChan = CyniChat.data.getChannel(channel.toLowerCase());
			
			//Now ask whether the user is even allowed in this channel
			if ( user.getBannedChannels().contains( NewChan.getName() ) ) {
				player.sendMessage("You are banned from this channel. Tch, tch, tch.");
				return;
			}
			
			//Then let them join the channel
			user.joinChannel(NewChan, pass);
			
		} else {
			
			//They're trying to join a channel which doesn't exist
			//... wish them luck with that
			player.sendMessage("This channel does not exist");
			
		}
	}
	
	/**
	 * Return information on this specific command
	 * @param player : This is the player we're sending to
	 * @return true when complete, or false if the player doesn't have the permissions
	 */
	public static boolean info( CommandSender player ) {
		
		//Let's make sure they are allowed to see this information first
		if ( CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.join.info") ) {
			
			//Then show it to them
			player.sendMessage( ChatColor.RED + "Type /cyn join "
				+ChCommand.necessary("channel", ChatColor.RED )+" "
				+ChCommand.optional("password", ChatColor.RED ) );
			return true;
			
		}
		
		//Otherwise, they can't see it
		return false;
		
	}
	
}