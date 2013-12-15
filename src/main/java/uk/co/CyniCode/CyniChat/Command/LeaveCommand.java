package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * The class for leaving a channel
 * 
 * @author CyniCode
 */
public class LeaveCommand {
	
	/**
	 * Return the information about this specific command
	 * @param player : The player we're sending to
	 * @return true if permission is given, false if otherwise
	 */
	public static boolean info( CommandSender player ) {
		
		//Is the player allowed to see the command information?
		if ( CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.leave.info") ) {
			
			//Apparenly so. Show it to them
			player.sendMessage( ChatColor.RED + "Type /cyn leave "
				+ChCommand.necessary("channel", ChatColor.RED) );
			return true;
		}
		
		//And they're apparently not allowed to see it.. boo hoo
		return false;
		
	}
	
	/**
	 * Make a player leave a channel. If the channel doesn't exist, tell them so
	 * @param player : the player that we're removing
	 * @param oldChan : The channel that we're leaving
	 */
	public static void leave( CommandSender player, String oldChan ) {
		
		CyniChat.printDebug( String.format( "%s is trying to leave %s",
				player.getName(),
				oldChan ) );
		
		//Does the channel exist first of all?
		if ( CyniChat.data.getChannel(oldChan) != null ) {
			
			//Yes? Leave it.
			CyniChat.printDebug( String.format( "%s exists... leaving.",
					oldChan ) );
			CyniChat.data.getDetails(player.getName()).leaveChannel( oldChan );
			
		} else {
			
			//No... try anyway.
			CyniChat.printDebug( String.format( "%s did not exist.",
					oldChan ) );
			player.sendMessage("This channel does not exist");
			
		}
	}
	
}