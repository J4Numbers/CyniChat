package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class AdminCommand {

	public static boolean create( CommandSender player, String name, String nick, Boolean protect ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.create") ) {
			Channel newChan = new Channel();
			newChan.create( name, nick, protect );
			DataManager.addChannel( newChan );
			PermissionManager.addChannelPerms( player, newChan, protect );
		}
		return false;
	}

	public static boolean remove( CommandSender player, String name ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.remove") ) {
			
		}
		return false;
	}

	public static boolean createInfo( CommandSender player ) {
		return true;
	}

	public static boolean removeInfo( CommandSender player ) {
		return true;
	}
}