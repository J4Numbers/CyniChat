package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;

public class ModCommand {

	public static boolean promote( CommandSender player, String channel, String newMod ) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.mod.promote."+channel ) ) {
			PermissionManager.promotePlayer( newMod, DataManager.getChannel( channel ) );
			return true;
		}
		return false;
	}

	public static boolean demote( CommandSender player, String channel, String oldMod ) {
		if (PermissionManager.checkPerm( (Player) player, "cynichat.mod.demote."+channel )) {
			PermissionManager.demotePlayer( oldMod, DataManager.getChannel( channel ) );
			return true;
		}
		return false;
	}

	public static boolean promoteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch promote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

	public static boolean demoteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch demote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}
}
