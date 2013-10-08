package uk.co.CyniCode.CyniChat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * Class to get all the relevant information about permissions
 * and to grant/remove them where applicable
 * (Basically a vault interface class)
 * @author Matthew Ball
 *
 */
public class PermissionManager {
	
	private static Permission perms;
	private static Chat chat;
	
	/**
	 * Set up all the manager bits and pieces.
	 * @param cyniChat : Use this to get the plugins we need
	 * @return true when complete, false if there is no Vault.
	 */
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
	
	/**
	 * Check if a player has a specific node
	 * @param player : The player we're checking against
	 * @param node : The node we have to make sure the player has
	 * @return True or false, dependent if they have it or not.
	 */
	public static boolean checkPerm( Player player, String node ) {
		if ( !( player instanceof Player ) ) return true;
		CyniChat.printDebug("Checking node: "+node+" for player: "+player.getDisplayName() );
		return perms.has( player, node );
	}

	/**
	 * Create a channel, and with it, the perms
	 * @param player : The creator of the channel that we're giving the perms to
	 * @param channel : The channel we created
	 * @param protect : Whether it is completely protected or not
	 * @return True when complete
	 */
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
	
	/**
	 * Promote a player to a channel mod, giving them the appropriate capabilities
	 * @param player : The player we're promoting
	 * @param channel : The channel we're promoting the player in
	 * @return true when complete
	 */
	public static boolean promotePlayer( String player, Channel channel ) {
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.ban."+channel.getName().toLowerCase() );
		perms.playerAdd( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.mute."+channel.getName().toLowerCase() );
		return true;
	}
	
	/**
	 * Demote a player from channel mod, removing their perms
	 * @param player : the player we're demoting
	 * @param channel : the channel we're demoting them in
	 * @return true when complete
	 */
	public static boolean demotePlayer( String player, Channel channel ) {
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		perms.playerRemove( DataManager.getDetails( player ).getPlayer() , "cynichat.mod.kick."+channel.getName().toLowerCase() );
		return true;
	}

	/**
	 * Currently unused
	 * @param channel
	 * @return
	 * @deprecated
	 */
	public static boolean remChannelPerms( Channel channel ) {
		return true;
	}

	/**
	 * Return a player's full name in [PREFIX]player[SUFFIX] form
	 * @param player : The player we want to check
	 * @return the full name of the player
	 */
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
