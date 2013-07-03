package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;

public class LeaveCommand {
	
	/**
	 * Return the information about this specific command
	 * @param player : The player we're sending to
	 * @return true if permission is given, false if otherwise
	 */
	public static boolean info( CommandSender player ) {
		if ( player.hasPermission("cynichat.basic.leave.info") ) {
			player.sendMessage("Type /cyn leave "+ChCommand.necessary("channel") );
			return true;
		}
		return false;
	}
	
	/**
	 * Make a player leave a channel. If the channel doesn't exist, tell them so
	 * @param player : the player that we're removing
	 * @param oldChan : The channel that we're leaving
	 * @return true when complete
	 */
	public static boolean leave( CommandSender player, String oldChan ) {
		if ( DataManager.getChannel(oldChan) != null ) {
			DataManager.getDetails(player.getName()).leaveChannel( oldChan );
			return true;
		} else {
			player.sendMessage("This channel does not exist");
			return true;
		}
	}
	
}
