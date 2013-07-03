package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;

public class ModCommand {

	public static boolean promote( CommandSender player, String string, String newMod, CyniChat plugin ) {
		if ( player.hasPermission("cynichat.mod.promote."+string ) ) {
			Player mod = DataManager.getDetails( newMod.toLowerCase() ).getPlayer();
			//mod.addAttachment( plugin, "cynichat.mod."+channel.getName().toLowerCase(), true );
			return true;
		}
		return false;
	}

	public static boolean demote( CommandSender player, String channel, String oldMod ) {
		if (player.hasPermission( "cynichat.mod.demote."+channel )) {
			Player unMod = DataManager.getDetails( oldMod.toLowerCase() ).getPlayer();
			//unMod.removeAttachment("cynichat.mod."+channel.getName().toLowerCase() );
			return true;
		}
		return false;
	}

	public static boolean promoteInfo( CommandSender player ) {
		return true;
	}

	public static boolean demoteInfo( CommandSender player ) {
		return true;
	}
}
