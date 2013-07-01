package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class BanCommand {

	public static boolean ban(CommandSender player, Channel channel, String banee) {
		CyniChat.user.get( banee.toLowerCase() ).newBan(player, channel);
		return true;
	}

	public static boolean unban(CommandSender player, Channel channel, String banee) {
		CyniChat.user.get( banee.toLowerCase() ).remBan(player, channel);
		return true;
	}

	public static boolean banInfo( CommandSender player ) {
		return true;
	}

	public static boolean unbanInfo( CommandSender player ) {
		return true;
	}

	public static boolean kick(CommandSender player, Channel channel, String string) {
		CyniChat.user.get( string.toLowerCase() ).Kick(player, channel);
		return true;
		
	}

	public static boolean kickInfo(CommandSender player) {
		return true;
	}

}
