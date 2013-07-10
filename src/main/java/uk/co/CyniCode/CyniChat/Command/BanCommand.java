package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class BanCommand {

	public static boolean ban(CommandSender player, Channel channel, String banee) {
		DataManager.getDetails( banee.toLowerCase() ).newBan(player, channel);
		return true;
	}

	public static boolean unban(CommandSender player, Channel channel, String banee) {
		DataManager.getDetails( banee.toLowerCase() ).remBan(player, channel);
		return true;
	}

	public static boolean banInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch ban "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

	public static boolean unbanInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch unban "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

	public static boolean kick(CommandSender player, Channel channel, String string) {
		DataManager.getDetails( string.toLowerCase() ).Kick(player, channel);
		return true;
		
	}

	public static boolean kickInfo(CommandSender player) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch kick "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

}
