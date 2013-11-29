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
	
	/**
	 * The thing which says whether things have things or not
	 * s/things/players/permissions
	 */
	private Permission perms;
	
	/**
	 * The prefix/suffix thing
	 */
	private Chat chat;
	
	/**
	 * Get the Permission manager
	 * @return the permission manager
	 */
	public Permission getPerms() {
		return this.perms;
	}
	
	/**
	 * Set a new permission manager
	 * @param newPerms : the new permission manager
	 */
	public void setPerms( Permission newPerms ) {
		this.perms = newPerms;
	}
	
	/**
	 * Perform rocket science
	 * @return the chat manager
	 */
	public Chat getChat() {
		return this.chat;
	}
	
	/**
	 * Perform higher-class rocket science
	 * @param newChat : Set a new Chat manager
	 */
	public void setChat( Chat newChat ) {
		this.chat = newChat;
	}
	
	/**
	 * Set up all the manager bits and pieces.
	 * @param cyniChat : Use this to get the plugins we need
	 * @throws ClassNotFoundException if Vault does not exist
	 */
	public PermissionManager(CyniChat cyniChat) throws ClassNotFoundException {
		
		//Drop the console a message to say that we're going...
		CyniChat.printInfo("Starting up the permissions manager...");
		
		try {
			
			//Does the class that we are interested in exist?
			Class.forName( "net.milkbowl.vault.permission.Permission" );
			
			//Well... the catch wasn't triggered.
			//Set the things that we need for Permissions
			RegisteredServiceProvider<Permission> rsp = cyniChat.getServer()
						.getServicesManager().getRegistration(Permission.class);
			this.perms = rsp.getProvider();
			
			//And now for the Chat manager
			RegisteredServiceProvider<Chat> rsp2 = cyniChat.getServer().getServicesManager()
						.getRegistration(Chat.class);
			this.chat = rsp2.getProvider();
			
		} catch (ClassNotFoundException e) {
			
			//Okay... basically...
			// Panic.
			CyniChat.printSevere("ERROR: Could not find Vault. Shutting down...");
			throw e;
			
		}
		
	}
	
	/**
	 * Check if a player has a specific node
	 * @param player : The player we're checking against
	 * @param node : The node we have to make sure the player has
	 * @return True or false, dependent if they have it or not.
	 */
	public boolean checkPerm( Player player, String node ) {
		
		//Is the player that we're checking actually a player?
		if ( !( player instanceof Player ) ) return true;
		
		//Yes. Now drop the console a line to say what we're doing
		CyniChat.printDebug("Checking node: "+node+" for player: "+player.getDisplayName() );
		
		//And return whether or not the player has the permission
		return getPerms().has( player, node );
		
	}

	/**
	 * Create a channel, and with it, the perms
	 * @param player : The creator of the channel that we're giving the perms to
	 * @param channel : The channel we created
	 * @param protect : Whether it is completely protected or not
	 */
	public void addChannelPerms( CommandSender player, Channel channel, Boolean protect ) {
		
		//Is the player actually a player?
		if ( player instanceof Player )
			return;
		
		//Yep...
		//Print some debug associated with what we're doing
		CyniChat.printDebug("Player : "+player.getName());
		CyniChat.printDebug("Node : cynichat.mod.kick."+channel.getName().toLowerCase() );
		
		//And add the basic permissions for the moderator of the channel
		getPerms().playerAdd( (Player) player, "cynichat.mod.kick."+channel.getName().toLowerCase() );
		getPerms().playerAdd( (Player) player, "cynichat.mod.ban."+channel.getName().toLowerCase() );
		getPerms().playerAdd( (Player) player, "cynichat.mod.mute."+channel.getName().toLowerCase() );
		
		//Then, if it's protected, add more permissions
		if ( protect == true ) {
			
			CyniChat.printDebug("Node : cynichat.basic.join."+channel.getName().toLowerCase() );
			getPerms().playerAdd( (Player) player, "cynichat.basic.join."+channel.getName().toLowerCase() );
			getPerms().playerAdd( (Player) player, "cynichat.basic.talk."+channel.getName().toLowerCase() );
			getPerms().playerAdd( (Player) player, "cynichat.basic.leave."+channel.getName().toLowerCase() );
			
		}
	}
	
	/**
	 * Promote a player to a channel mod, giving them the appropriate capabilities
	 * @param player : The player we're promoting
	 * @param channel : The channel we're promoting the player in
	 */
	public void promotePlayer( String player, Channel channel ) {
		
		//This player has been promoted to channel moderater status...
		// congratulations to them
		getPerms().playerAdd( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.kick."+channel.getName().toLowerCase() );
		getPerms().playerAdd( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.ban."+channel.getName().toLowerCase() );
		getPerms().playerAdd( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.mute."+channel.getName().toLowerCase() );
		
	}
	
	/**
	 * Demote a player from channel mod, removing their perms
	 * @param player : the player we're demoting
	 * @param channel : the channel we're demoting them in
	 */
	public void demotePlayer( String player, Channel channel ) {
		
		//This player has been demoted...
		// boo!
		getPerms().playerRemove( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.kick."+channel.getName().toLowerCase() );
		getPerms().playerRemove( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.kick."+channel.getName().toLowerCase() );
		getPerms().playerRemove( DataManager.getDetails( player ).getPlayer() ,
					"cynichat.mod.kick."+channel.getName().toLowerCase() );
		
	}

	/**
	 * Currently unused
	 * The plan is to use this to completely eradicate all perms connected
	 *  with a given channel
	 * @param channel : This one to be exact
	 * @deprecated
	 */
	public void remChannelPerms( Channel channel ) {
		
	}

	/**
	 * Return a player's full name in [PREFIX]player[SUFFIX] form
	 * @param player : The player we want to check
	 * @return the full name of the player
	 */
	public String getPlayerFull(Player player) {
		
		//Initialise some strings
		String prefix;
		String suffix;
		
		//Does the player have a prefix?
		if ( getChat().getPlayerPrefix(player) == null )
			
			//Nope.
			prefix = "";
			
		else
			
			//Appparently so.
			prefix = getChat().getPlayerPrefix(player);
		
		//Do they have a suffix?
		if ( getChat().getPlayerSuffix(player) == null )
			
			//Nope.
			suffix = "";
		
		else
			
			//Apparently so.
			suffix = getChat().getPlayerSuffix(player);
		
		//Make the message out of all the parts
		String message = prefix + player.getDisplayName() + suffix;
		
		//And return the coloured message
		return message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
		
	}
	
}
