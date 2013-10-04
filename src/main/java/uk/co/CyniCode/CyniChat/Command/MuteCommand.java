package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * Class for making another player be quiet... damn,
 * That's a lot of things to shut them up.
 * @author Matthew Ball
 *
 */
public class MuteCommand {
	
	/**
	 * Ignore a player
	 * @param player : The player who wants to ignore someone
	 * @param ignorer : The annoying person
	 * @return true when complete
	 */
	public static boolean ignore( CommandSender player, String ignorer) {
		if ( DataManager.getDetails(ignorer) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		CyniChat.printDebug( player.getName() + " is now attempting to ignore " + ignorer );
		DataManager.getDetails( player.getName().toLowerCase() ).addIgnore( ignorer );
		return true;
	}

	/**
	 * Un-ignore (hear) a player
	 * @param player : The player who has a conscience
	 * @param unignorer : The player who wasn't as annoying as thought
	 * @return true when complete
	 */
	public static boolean hear( CommandSender player, String unignorer) {
		if ( DataManager.getDetails(unignorer) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		DataManager.getDetails( player.getName().toLowerCase() ).remIgnore( unignorer );
		return true;
	}

	/**
	 * Return information about the ignore command
	 * @param player : The person who wants to know
	 * @return true when complete
	 */
	public static boolean ignoreInfo(CommandSender player) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch ignore "+ChCommand.necessary("player"));
		return true;
	}

	/**
	 * Return information about the hear command
	 * @param player : the person who wants to know
	 * @return true when complete
	 */
	public static boolean hearInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch hear "+ChCommand.necessary("player"));
		return true;
	}

	/**
	 * Mute a player in one channel
	 * @param player : The person doing the muting
	 * @param channel : The channel that the mutee is muted in
	 * @param mutee : The person who shalt not speak
	 * @return true when complete
	 */
	public static boolean mute( CommandSender player, Channel channel, String mutee ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		if (DataManager.getDetails(mutee.toLowerCase()).addMute(player.getName(), channel) == true ) {
			player.sendMessage( mutee + " has been muted");
		} else {
			player.sendMessage( mutee + " was already muted");
		}
		return true;
	}

	/**
	 * Completely mute a player across all channels
	 * @param player : The annoyed person
	 * @param mutee : The unlucky sod
	 * @return true when complete
	 */
	public static boolean gmute( CommandSender player, String mutee ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.admin.silence" ) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		DataManager.getDetails(mutee.toLowerCase()).Silence( player );
		return true;
	}

	/**
	 * Give a person infomation about global mutation
	 * @param player : Wait... mutation?
	 * @return No, no! Muting!
	 */
	public static boolean gmuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch gmute "+ChCommand.necessary("player"));
		return true;
	}

	/**
	 * Show a player information about muting
	 * @param player : The person wanting to know
	 * @return true when complete
	 */
	public static boolean muteInfo( CommandSender player) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch mute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel") );
		return true;
	}

	/**
	 * Globally unmute someone
	 * @param player : The person who has a kind heart
	 * @param mutee : The guy with a second chance
	 * @return true when complete
	 */
	public static boolean gUnMute( CommandSender player, String mutee) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.admin.silence") )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		DataManager.getDetails(mutee.toLowerCase()).Listen(player);
		return true;
	}

	/**
	 * Show information about global unmuting
	 * @param player : The person who wants to know
	 * @return true when complete
	 */
	public static boolean gUnMuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch gunmute "+ChCommand.necessary("player"));
		return true;
	}

	/**
	 * Unmute a player in one channel
	 * @param player : The player doing the unmuting
	 * @param channel : The channel that shall be graced with another voice
	 * @param mutee : The person who learnt his lesson (hopefully)
	 * @return true when complete
	 */
	public static boolean unmute( CommandSender player, Channel channel, String mutee ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		if ( DataManager.getDetails( mutee.toLowerCase() ).remMute(player.getName(), channel) == true ) {
			player.sendMessage( mutee + " has been unmuted" );
		} else {
			player.sendMessage( mutee + " was already unmuted" );
		}
		return true;
	}

	/**
	 * Show information about the unmute command
	 * @param player : The person who wants to know
	 * @return true when complete
	 */
	public static boolean unmuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch unmute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel") );
		return true;
	}

}
