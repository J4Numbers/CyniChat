package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class LeaveCommand {
	
	public static boolean info( CommandSender player ) {
		if ( player.hasPermission("cynichat.basic.leave.info") ) {
			player.sendMessage("Type /cyn leave "+MasterCommand.necessary("channel") );
			return true;
		}
		return false;
	}
	
	public static boolean leave( CommandSender player, String oldChan ) {
		if ( CyniChat.channels.get(oldChan) != null ) {
			CyniChat.user.get(player.getName()).leaveChannel( oldChan );
			return true;
		} else {
			player.sendMessage("This channel does not exist");
			return true;
		}
	}
	
}
