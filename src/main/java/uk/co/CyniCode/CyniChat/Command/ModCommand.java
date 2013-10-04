package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * Class for all things mod
 * So... promote, demote and set
 * @author Matthew Ball
 *
 */
public class ModCommand {

	/**
	 * Promote a player in the channel to a mod
	 * @param player : The player doing the promoting
	 * @param channel : The channel that has a new mod
	 * @param newMod : The new guy to the ranks of the exhaulted
	 * @return true when complete
	 */
	public static boolean promote( CommandSender player, String channel, String newMod ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.promote."+channel ) )
				return false;
		
		PermissionManager.promotePlayer( newMod, DataManager.getChannel( channel ) );
		return true;
	}

	/**
	 * Demote a mod in the channel to a player
	 * @param player : The player doing the demoting
	 * @param channel : The channel that mourns
	 * @param oldMod : The ashamed exile
	 * @return true when complete
	 */
	public static boolean demote( CommandSender player, String channel, String oldMod ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.demote."+channel ))
				return false;
		
		PermissionManager.demotePlayer( oldMod, DataManager.getChannel( channel ) );
		return true;
	}

	/**
	 * Show the player information about promoting another
	 * @param player : The player we're giving the info to
	 * @return true when complete
	 */
	public static boolean promoteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch promote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

	/**
	 * Show the player information about demoting another
	 * @param player : The learned scholar
	 * @return true when complete
	 */
	public static boolean demoteInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch demote "+ChCommand.necessary("player")+" "+ChCommand.optional("channel"));
		return true;
	}

	/**
	 * Set a particular aspect of a channel
	 * @param player : The player doing the setting
	 * @param args : The arguments that define what is being set and what to
	 * @return true when complete
	 */
	public static boolean set( CommandSender player, String[] args ) {
		if ( args.length != 1 ) {
			if ( ( DataManager.getChannel( args[1] ) != null ) ) {
				Channel current = DataManager.getChannel( args[1] );
				if ( player instanceof Player )
					if ( !PermissionManager.checkPerm( (Player) player, "cynichat.mod.set."+current.getName().toLowerCase() ) )
						return false;
				
				if ( args.length >= 4 ) {
					if ( args[2].equalsIgnoreCase("color") ) {
						current.setColor( args[3] );
						player.sendMessage("Color change successful!");
						return true;
					} else if ( args[2].equalsIgnoreCase("password") ) {
						current.setPassword( args[3] );
						player.sendMessage("Password change successful!");
						return true;
					} else if ( args[2].equalsIgnoreCase("ircchan") ) {
						if ( args[3].equalsIgnoreCase( "-" ) ) {
							current.setIrcName( "" );
						} else {
							current.setIrcName( args[3] );
						}
						player.sendMessage("Name of the IRC channel successfully changed!");
						return true;
					} else if ( args[2].equalsIgnoreCase("ircpass") ) {
						if ( args[3].equalsIgnoreCase( "-" ) ) {
							current.setIrcPass( "" );
						} else {
							current.setIrcPass( args[3] );
						}
						player.sendMessage("Password for the IRC channel successfully changed!");
						return true;
					} else if ( args[2].equalsIgnoreCase("description") ) {
						current.setDesc( stacker( args ) );
						player.sendMessage("Description change successful!");
						return true;
					}
				}
			}
		}
		setInfo( player );
		return true;
	}

	/**
	 * Stack a string together
	 * @param stacking : The array we're turning into a sentence
	 * @return the sentence
	 */
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

	/**
	 * Return the information on setting a channel aspect
	 * @param player : The player who wants to know
	 * @return true when complete
	 */
	public static boolean setInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/ch set "+ChCommand.necessary("channel")+" "+ChCommand.necessary("option")+" "+ChCommand.necessary("new value") );
		return true;
	}
}
