package uk.co.CyniCode.CyniChat;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import uk.co.CyniCode.CyniChat.Channel.Channel;

public class PermissionManager {
	
	private static Permission perms;
	
	public static boolean setupPermissions(CyniChat cyniChat) {
		CyniChat.printInfo("Starting up the permissions manager...");
		RegisteredServiceProvider<Permission> rsp = cyniChat.getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
	
	public static boolean checkPerm( Player player, String node ) {
		return perms.has( player, node );
	}

	public static boolean addChannelPerms( CommandSender player, Channel channel, Boolean protect ) {
		CyniChat.printDebug("Player : "+player.getName());
		CyniChat.printDebug("Node : cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.ban."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.mute."+channel.getName().toLowerCase() );
		if ( protect == true ) {
			perms.playerAdd( (Player) player, "cynichat.basic.join."+channel.getName().toLowerCase() );
			perms.playerAdd( (Player) player, "cynichat.basic.talk."+channel.getName().toLowerCase() );
			perms.playerAdd( (Player) player, "cynichat.basic.leave."+channel.getName().toLowerCase() );
		}
		return true;
	}
	
	public static boolean promotePlayer( String player, Channel channel ) {
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.ban."+channel.getName().toLowerCase() );
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.mute."+channel.getName().toLowerCase() );
		return true;
	}
	
	public static boolean demotePlayer( String player, Channel channel ) {
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		return true;
	}

	public static boolean remChannelPerms( Channel channel ) {
		return true;
	}
}
