package uk.co.CyniCode.CyniChat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.Channel.Channel;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;

public class PermissionManager {
	
	private static DroxPermsAPI API = null;
	private static DroxPerms droxPerms = null;
	
	public static void start( DroxPerms manager ) {
		CyniChat.printInfo("Starting up the permissions manager...");
		droxPerms = manager;
		if ( droxPerms != null ) {
			API = droxPerms.getAPI();
			CyniChat.printInfo("DroxPerms API has been connected.");
			return;
		}
		CyniChat.printSevere("DroxPerms was not found! Any permission changes will not work!");
	}
	
	public static String getPrefix( Player player ) {
		if ( API.getPlayerInfo( player.getName(), "prefix") == null ) {
			return "";
		}
		return API.getPlayerInfo( player.getName(), "prefix");
	}
	
	public static String getSuffix( Player player ) {
		if ( API.getPlayerInfo( player.getName(), "suffix") == null ) {
			return "";
		}
		return API.getPlayerInfo( player.getName(), "suffix");
	}

	public static boolean addChannelPerms( CommandSender player, Channel channel, Boolean protect ) {
		API.addPlayerPermission(player.getName(), "cynichat.mod.kick."+channel.getName().toLowerCase() );
		API.addPlayerPermission(player.getName(), "cynichat.mod.ban."+channel.getName().toLowerCase() );
		API.addPlayerPermission(player.getName(), "cynichat.mod.mute."+channel.getName().toLowerCase() );
		if ( protect == true ) {
			API.addPlayerPermission(player.getName(), "cynichat.basic.join."+channel.getName().toLowerCase() );
			API.addPlayerPermission(player.getName(), "cynichat.basic.talk."+channel.getName().toLowerCase() );
			API.addPlayerPermission(player.getName(), "cynichat.basic.leave."+channel.getName().toLowerCase() );
		}
		return true;
	}
	
	public static boolean promotePlayer( String player, Channel channel ) {
		API.addPlayerPermission( player.toLowerCase(), "cynichat.mod.kick."+channel.getName().toLowerCase() );
		API.addPlayerPermission( player.toLowerCase(), "cynichat.mod.ban."+channel.getName().toLowerCase() );
		API.addPlayerPermission( player.toLowerCase(), "cynichat.mod.mute."+channel.getName().toLowerCase() );
		return true;
	}
	
	public static boolean demotePlayer( String player, Channel channel ) {
		API.removePlayerPermission( player.toLowerCase(), "cynichat.mod.kick."+channel.getName().toLowerCase() );
		API.removePlayerPermission( player.toLowerCase(), "cynichat.mod.kick."+channel.getName().toLowerCase() );
		API.removePlayerPermission( player.toLowerCase(), "cynichat.mod.kick."+channel.getName().toLowerCase() );
		return true;
	}
	
	public static boolean remChannelPerms( Channel channel ) {
		return true;
	}
}
