package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.CommandSender;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class MuteCommand {
	
	public static boolean ignore( CommandSender player, String ignorer) {
		CyniChat.user.get( player.getName().toLowerCase() ).addIgnore( ignorer );
		return true;
	}

	public static boolean hear( CommandSender player, String unignorer) {
		CyniChat.user.get( player.getName().toLowerCase() ).remIgnore( unignorer );
		return true;
	}

	public static boolean ignoreInfo(CommandSender player) {
		// TODO Auto-generated method stub
		return true;
	}

	public static boolean hearInfo( CommandSender player ) {
		return true;
	}

	public static boolean mute( CommandSender player, Channel channel, String mutee ) {
		CyniChat.user.get(mutee.toLowerCase()).addMute(player, channel);
		return true;
	}

	public static boolean gmute( CommandSender player, String mutee ) {
		CyniChat.user.get(mutee.toLowerCase()).Silence( player );
		return true;
	}

	public static boolean gmuteInfo( CommandSender player ) {
		return true;
	}

	public static boolean muteInfo( CommandSender player) {
		return true;
	}

	public static boolean gUnMute( CommandSender player, String mutee) {
		CyniChat.user.get(mutee.toLowerCase()).Listen(player);
		return true;
	}

	public static boolean gUnMuteInfo( CommandSender player ) {
		return true;
	}

	public static boolean unmute( CommandSender player, Channel channel, String mutee ) {
		CyniChat.user.get( mutee.toLowerCase() ).remMute(player, channel);
		return true;
	}

	public static boolean unmuteInfo( CommandSender player ) {
		return true;
	}

}
