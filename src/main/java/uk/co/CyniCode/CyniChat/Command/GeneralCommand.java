package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;

public class GeneralCommand {

	public static boolean save( CommandSender player ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.save") ) {
			
			DataManager.saveChannelConfig();
			DataManager.saveUserDetails();
			return true;
		}
		return false;
	}
	
	public static boolean reload( CommandSender player ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.reload") ) {
			save( player );
			// TODO - Insert reload block
		}
		return false;
	}
	
	public static boolean info( CommandSender player, String channel ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.basic.info."+channel) ) {
			
		}
		return false;
	}
	
	public static boolean list( CommandSender player, int page ) {
		return true;
	}
	
	public static boolean who( CommandSender player, String channel ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.basic.who."+channel) ) {
			
		}
		return false;
	}
	
	public static boolean quickMessage( CommandSender player, String channel, String Message ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.basic.qm") ) {
			
		}
		return false;
	}
	
	public static boolean qmInfo( CommandSender player ) {
		return true;
	}
}
