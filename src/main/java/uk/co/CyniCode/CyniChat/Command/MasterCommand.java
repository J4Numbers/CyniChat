package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Command.HelpCommand;

public class MasterCommand implements CommandExecutor {

	public CyniChat plugin;
	public MasterCommand(CyniChat plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Wrap a string in necessary tags
	 * @param option : This is what we're wrapping
	 * @return the new string
	 */
	public static String necessary( String option ) {
		String coloured = ChatColor.DARK_AQUA+"<"+option+">"+ChatColor.WHITE;
		return coloured;
	}
	
	/**
	 * Wrap a string in optional tags
	 * @param option : This is what we're wrapping
	 * @return the new string
	 */
	public static String optional( String option ) {
		String coloured = ChatColor.GRAY+"["+option+"]"+ChatColor.WHITE;
		return coloured;
	}
	
	/**
	 * Iterate through all potential commands to allow a player to execute commands
	 */
	public boolean onCommand(CommandSender player, Command comm, String Label, String[] args) {
		if ( CyniChat.debug == true ) {
			CyniChat.printDebug(player.getName() + " -> " + comm.getLabel() + " -> " + Label + " -> " + args[0].toString() + " -> " + args[1].toString());
		}
		if ( args.length == 0 ) {
			HelpCommand.info( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("help") ) {
			if ( args.length == 1 ) {
				HelpCommand.info( player );
				return true;
			}
			if ( args[1].equalsIgnoreCase("channels") ) {
				HelpCommand.channels( player );
				return true;
			}
			if ( args[1].equalsIgnoreCase("users") ) {
				HelpCommand.users( player );
				return true;
			}
			if ( args[1].equalsIgnoreCase("admin") ) {
				HelpCommand.admin( player );
				return true;
			}
			if ( args[1].equalsIgnoreCase("mods") ) {
				HelpCommand.mods( player );
				return true;
			}
			HelpCommand.info( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("join") ) {
			if ( args.length < 2 || args.length > 3 ) {
				JoinCommand.info( player );
				return true;
			} else {
				if ( args.length == 3 ) {
					JoinCommand.join( player, args[1], args[2] );
					return true;
				} else {
					JoinCommand.join( player, args[1], "");
					return true;
				}
			}
		}
		if ( args[0].equalsIgnoreCase("leave") ) {
			if ( args.length != 2 ) {
				LeaveCommand.info( player );
				return true;
			} else {
				LeaveCommand.leave( player, args[1] );
			}
		}
		return true;
	}

}
