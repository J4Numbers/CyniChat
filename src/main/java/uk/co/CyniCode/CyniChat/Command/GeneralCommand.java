package uk.co.CyniCode.CyniChat.Command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.FileHandling;
import uk.co.CyniCode.CyniChat.Channel.Channel;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

public class GeneralCommand {

	public static boolean save( CommandSender player ) {
		if ( player.hasPermission("cynichat.admin.save") ) {
			
			
			return true;
		}
		return false;
	}
	
	public static boolean reload( CommandSender player ) {
		if ( player.hasPermission("cynichat.admin.reload") ) {
			save( player );
			// TODO - Insert reload block
		}
		return false;
	}
	
	public static boolean info( CommandSender player, String channel ) {
		if ( player.hasPermission("cynichat.basic.info."+channel) ) {
			
		}
		return false;
	}
	
	public static boolean list( CommandSender player, int page ) {
		return true;
	}
	
	public static boolean who( CommandSender player, String channel ) {
		if ( player.hasPermission("cynichat.basic.who."+channel) ) {
			
		}
		return false;
	}
	
	public static boolean quickMessage( CommandSender player, String channel, String Message ) {
		if ( player.hasPermission("cynichat.basic.qm") ) {
			
		}
		return false;
	}
	
	public static boolean qmInfo( CommandSender player ) {
		return true;
	}
}
