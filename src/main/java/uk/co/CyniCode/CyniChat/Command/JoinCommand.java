package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class JoinCommand {
	
	public static boolean join( CommandSender player, String channel, String pass ) {
		if ( CyniChat.channels.get(channel) != null ) {
			Channel NewChan = CyniChat.channels.get(channel);
			CyniChat.user.get(player.getName()).joinChannel(NewChan, pass);
			return true;
		} else {
			player.sendMessage("This channel does not exist");
			return true;
		}
	}
	
	public static boolean info( CommandSender player ) {
		if ( player.hasPermission("cynichat.basic.join.info") ) {
			player.sendMessage("Type /cyn join "+MasterCommand.necessary("channel")+" "+MasterCommand.optional("password") );
			return true;
		}
		return false;
	}
	
}
