package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

public class AdminCommand {

	public static boolean create( CommandSender player, String name, String nick ) {
		if ( player.hasPermission("cynichat.admin.create") ) {
			
		}
		return false;
	}

	public static boolean remove( CommandSender player, String name ) {
		if ( player.hasPermission("cynichat.admin.remove") ) {
			
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
