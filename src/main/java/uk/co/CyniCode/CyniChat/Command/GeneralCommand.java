package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.DataManager;

public class GeneralCommand {

	public static boolean save( CommandSender player ) {
		if ( player.hasPermission("cynichat.admin.save") ) {
			
			DataManager.saveChannelConfig();
			DataManager.saveUserDetails();
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
