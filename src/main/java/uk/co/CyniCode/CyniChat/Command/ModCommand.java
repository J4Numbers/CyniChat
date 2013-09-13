package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class ModCommand {

	public static boolean promote( CommandSender player, String channel, String newMod ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.promote."+channel ) )
				return false;
		
		PermissionManager.promotePlayer( newMod, DataManager.getChannel( channel ) );
		return true;
	}

	public static boolean demote( CommandSender player, String channel, String oldMod ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.demote."+channel ))
				return false;
		
		PermissionManager.demotePlayer( oldMod, DataManager.getChannel( channel ) );
		return true;
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

	public static boolean set( CommandSender player, String[] args ) {
		if ( args.length != 1 ) {
			if ( ( DataManager.getChannel( args[1] ) != null ) ) {
				Channel current = DataManager.getChannel( args[1] );
				if ( player instanceof Player )
					if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.set."+args[1] ) )
						return false;
				
				if ( args.length >= 4 ) {
					if ( args[2].equalsIgnoreCase("color") ) {
						current.setColor( args[3] );
						return true;
					} else if ( args[2].equalsIgnoreCase("password") ) {
						current.setPassword( args[3] );
						return true;
					} else if ( args[2].equalsIgnoreCase("description") ) {
						current.setDesc( stacker( args ) );
						return true;
					}
				}
			}
		}
		setInfo( player );
		return true;
	}

	public static String stacker( String[] stacking ) {
		try {
			String finalStack = "";
			for ( int i=3; i<stacking.length; i++ ) {
				finalStack += " "+stacking[i];
			}
			return finalStack;
		} catch ( NullPointerException e ) {
			return "";
		}
	}

	public static boolean setInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch set "+ChCommand.necessary("channel")+" "+ChCommand.necessary("option")+" "+ChCommand.necessary("new value") );
		return true;
	}
}
