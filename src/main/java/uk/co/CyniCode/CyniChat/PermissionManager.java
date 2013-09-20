package uk.co.CyniCode.CyniChat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import uk.co.CyniCode.CyniChat.objects.Channel;

public class PermissionManager {
	
	private static Permission perms;
	private static Chat chat;
	
	public static boolean setupPermissions(CyniChat cyniChat) {
		CyniChat.printInfo("Starting up the permissions manager...");
		try {
			Class.forName( "net.milkbowl.vault.permission.Permission" );
			RegisteredServiceProvider<Permission> rsp = cyniChat.getServer().getServicesManager().getRegistration(Permission.class);
			perms = rsp.getProvider();
			
			RegisteredServiceProvider<Chat> rsp2 = cyniChat.getServer().getServicesManager().getRegistration(Chat.class);
			chat = rsp2.getProvider();
			
			return true;
		} catch (ClassNotFoundException e) {
			CyniChat.printSevere("ERROR: Could not find Vault. Shutting down...");
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean checkPerm( Player player, String node ) {
		if ( !( player instanceof Player ) ) return true;
		CyniChat.printDebug("Checking node: "+node+" for player: "+player.getDisplayName() );
		return perms.has( player, node );
	}

	public static boolean addChannelPerms( CommandSender player, Channel channel, Boolean protect ) {
		if ( player instanceof Player )
			return true;
		CyniChat.printDebug("Player : "+player.getName());
		CyniChat.printDebug("Node : cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.ban."+channel.getName().toLowerCase() );
		perms.playerAdd( (Player) player, "cynichat.mod.mute."+channel.getName().toLowerCase() );
		if ( protect == true ) {
			CyniChat.printDebug("Node : cynichat.basic.join."+channel.getName().toLowerCase() );
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

	public static String getPlayerFull(Player player) {
		String prefix;
		String suffix;
		
		if ( chat.getPlayerPrefix(player) == null )
			prefix = "";
		else
			prefix = chat.getPlayerPrefix(player);
		
		if ( chat.getPlayerSuffix(player) == null )
			suffix = "";
		else
			suffix = chat.getPlayerSuffix(player);
		
		String message = prefix + player.getDisplayName() + suffix;
		
		return message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
	}
}
