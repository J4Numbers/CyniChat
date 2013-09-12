package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

public class JoinCommand {
	
	/**
	 * When a player joins a channel, this is ran. If the channel is non-existant, the the player is notified.
	 * @param player : We need this to alter the players UserDetails file
	 * @param channel : We need this to check the password and stuffs
	 * @param pass : For use in comparison
	 * @return true when complete
	 */
	public static boolean join( CommandSender player, String channel, String pass ) {
		if ( DataManager.getChannel(channel) != null ) {
			UserDetails user = DataManager.getDetails( player.getName() );
			Channel NewChan = DataManager.getChannel(channel);
			if ( user.getBannedChannels().contains( NewChan.getName() ) ) {
				player.sendMessage("You are banned from this channel. Tch, tch, tch.");
				return true;
			}
			DataManager.getDetails(player.getName()).joinChannel(NewChan, pass);
			return true;
		} else {
			player.sendMessage("This channel does not exist");
			return true;
		}
	}
	
	/**
	 * Return information on this specific command
	 * @param player : This is the player we're sending to
	 * @return true when complete, or false if the player doesn't have the permissions
	 */
	public static boolean info( CommandSender player ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.basic.join.info") ) {
			player.sendMessage("Type /cyn join "+ChCommand.necessary("channel")+" "+ChCommand.optional("password") );
			return true;
		}
		return false;
	}
	
}
