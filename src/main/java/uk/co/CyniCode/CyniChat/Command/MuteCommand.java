package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class MuteCommand {
	
	public static boolean ignore( CommandSender player, String ignorer) {
		if ( DataManager.getDetails(ignorer) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		CyniChat.printDebug( player.getName() + " is now attempting to ignore " + ignorer );
		DataManager.getDetails( player.getName().toLowerCase() ).addIgnore( ignorer );
		return true;
	}

	public static boolean hear( CommandSender player, String unignorer) {
		if ( DataManager.getDetails(unignorer) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		DataManager.getDetails( player.getName().toLowerCase() ).remIgnore( unignorer );
		return true;
	}

	public static boolean ignoreInfo(CommandSender player) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch ignore "+ChCommand.necessary("player"));
		return true;
	}

	public static boolean hearInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch hear "+ChCommand.necessary("player"));
		return true;
	}

	public static boolean mute( CommandSender player, Channel channel, String mutee ) {
<<<<<<< HEAD
		if ( DataManager.getDetails( mutee ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
=======
<<<<<<< Updated upstream
=======
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
>>>>>>> Stashed changes
>>>>>>> develop
		DataManager.getDetails(mutee.toLowerCase()).addMute(player, channel);
		return true;
	}

	public static boolean gmute( CommandSender player, String mutee ) {
<<<<<<< HEAD
		if ( DataManager.getDetails( mutee ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
=======
<<<<<<< Updated upstream
=======
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.admin.silence" ) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
>>>>>>> Stashed changes
>>>>>>> develop
		DataManager.getDetails(mutee.toLowerCase()).Silence( player );
		return true;
	}

	public static boolean gmuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch gmute "+ChCommand.necessary("player"));
		return true;
	}

	public static boolean muteInfo( CommandSender player) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch mute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel") );
		return true;
	}

	public static boolean gUnMute( CommandSender player, String mutee) {
<<<<<<< HEAD
		if ( DataManager.getDetails( mutee ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
=======
<<<<<<< Updated upstream
=======
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.admin.silence") )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
>>>>>>> Stashed changes
>>>>>>> develop
		DataManager.getDetails(mutee.toLowerCase()).Listen(player);
		return true;
	}

	public static boolean gUnMuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch gunmute "+ChCommand.necessary("player"));
		return true;
	}

	public static boolean unmute( CommandSender player, Channel channel, String mutee ) {
<<<<<<< HEAD
		if ( DataManager.getDetails( mutee ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
=======
<<<<<<< Updated upstream
=======
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		if ( DataManager.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
>>>>>>> Stashed changes
>>>>>>> develop
		DataManager.getDetails( mutee.toLowerCase() ).remMute(player, channel);
		return true;
	}

	public static boolean unmuteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch unmute "+ChCommand.necessary("player")+" "+ChCommand.optional("channel") );
		return true;
	}

}
