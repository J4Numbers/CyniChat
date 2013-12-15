package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * All the relevant snippits on banning/kicking etc.
 * 
 * @author CyniCode
 */
public class BanCommand {
	
	/**
	 * Ban a player from a channel
	 * @param player : The player doing the banning
	 * @param channel : The channel the other player is being banned in
	 * @param banee : The naughty person being banned
	 * @return true when complete
	 */
	public static boolean ban(CommandSender player, Channel channel, String banee) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//Do they have the permissions to ban a person in this channel?
			if ( !CyniChat.perms.checkPerm( (Player) player, 
						"cynichat.mod.ban."+channel.getName().toLowerCase() ) )
				//Nope? Boom!
				return false;
		
		//Now try to ban the player
		if ( CyniChat.data.getDetails( banee.toLowerCase() )
				.newBan(player.getName(), channel) )
			
			//If it was successful, tell the player so
			player.sendMessage( banee + " has been banned.");
			
		else 
			
			//Otherwise, tell the player so, just so they know
			player.sendMessage( banee + " is already banned.");
			
		
		
		//Then return
		return true;
		
	}
	
	/**
	 * Unban a player from a channel
	 * @param player : The player doing the unbanning
	 * @param channel : The channel the other player is being unbanned in
	 * @param banee : The lucky player who has a second chance
	 * @return true when complete
	 */
	public static boolean unban(CommandSender player, Channel channel, String banee) {
		
		//Is the player actually a player?
		if ( player instanceof Player )
			//Yep. Can they unban the person?
			if ( !CyniChat.perms.checkPerm( (Player) player, 
						"cynichat.mod.ban."+channel.getName().toLowerCase() ) )
				//Nooooo... kill it!
				return false;
		
		//Try to unban the player...
		if ( CyniChat.data.getDetails( banee.toLowerCase() ).remBan( player.getName(), channel) )
			
			//Now they're unbanned, celebrate!
			player.sendMessage("The player has been unbanned.");
			
		else
			
			//Tell the player that we were unsuccessful
			player.sendMessage("This player was not banned.");
		
		//And return to wherever we came from
		return true;
	}
	
	/**
	 * Return the information about the ban command
	 * @param player : The person we're giving the information
	 */
	public static void banInfo( CommandSender player ) {
		
		//Tell the player that their command was invalid
		player.sendMessage( ChatColor.RED+"Invalid Syntax!" );
		
		//And give them the correct one to work with
		player.sendMessage( ChatColor.RED + "/ch ban "+ChCommand.necessary( "player", ChatColor.RED )
			+" "+ChCommand.optional( "channel", ChatColor.RED ) );
		
	}
	
	/**
	 * Return the information about the unban command
	 * @param player : The player we're giving the information to
	 */
	public static void unbanInfo( CommandSender player ) {
		
		//Tell the player their command was wrong
		player.sendMessage( ChatColor.RED+"Invalid Syntax!" );
		
		//And give them the right command instead
		player.sendMessage( ChatColor.RED + "/ch unban "+ChCommand.necessary( "player", ChatColor.RED )
			+" "+ChCommand.optional( "channel", ChatColor.RED ) );
		
	}
	
	/**
	 * Kick the player from the channel
	 * @param player : The player trying to kick someone else
	 * @param channel : The channel the other player is being kicked from
	 * @param kickee : And stay out!
	 * @return true when complete
	 */
	public static boolean kick(CommandSender player, Channel channel, String kickee) {
		
		//If the player is a player...
		if ( player instanceof Player )
			
			//If they do not have the permission to kick globally or kick
			// locally, then laugh at them.
			if ( !( CyniChat.perms.checkPerm( (Player) player,
						"cynichat.mod.kick" ) ) && 
					!( CyniChat.perms.checkPerm( (Player) player, 
						"cynichat.mod.kick."+channel.getName().toLowerCase() ) ) )
				
				//Apparently not...
				return false;
		
		//Try to kick the player from the channel
		if ( CyniChat.data.getDetails( kickee.toLowerCase() )
			.kick(player.getName(), channel ) )
			
			//If we kicked em, tell the player
			player.sendMessage( kickee + " has been kicked from the channel." );
			
		else 
			
			//And if we didn't, tell 'em that too
			player.sendMessage( kickee + " was not in the channel" );
		
		//Now return
		return true;
		
	}
	
	/**
	 * Return the information on how to kick a player
	 * @param player : Firstly, you should pick them up
	 */
	public static void kickInfo(CommandSender player) {
		
		//Tell the player their command was wrong
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Then give them the right command
		player.sendMessage( ChatColor.RED + "/ch kick "+ChCommand.necessary("player", ChatColor.RED)+
			" "+ChCommand.optional("channel", ChatColor.RED) );
		
	}
	
}