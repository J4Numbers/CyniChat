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
		//CyniChat.printDebug(player.getName() + " -> " + comm.getLabel() + " -> " + Label + " -> " + args[0].toString() + " -> " + args[1].toString());
		if ( args.length == 0 ) {
			HelpCommand.info( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("help") ) {
			HelpCommand.Command( player, args[1] );
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
		if ( args[0].equalsIgnoreCase("ban") ) {
			
		}
		if ( args[0].equalsIgnoreCase("unban") ) {
			
		}
		if ( args[0].equalsIgnoreCase("mute") ) {
			
		}
		if ( args[0].equalsIgnoreCase("unmute") ) {
			
		}
		if ( args[0].equalsIgnoreCase("ignore") ) {
			
		}
		if ( args[0].equalsIgnoreCase("hear") ) {
			
		}
		if ( args[0].equalsIgnoreCase("gmute") ) {
			
		}
		if ( args[0].equalsIgnoreCase("gunmute") ) {
			
		}
		if ( args[0].equalsIgnoreCase("create") ) {
			
		}
		if ( args[0].equalsIgnoreCase("remove") ) {
			
		}
		if ( args[0].equalsIgnoreCase("reload") ) {
			
		}
		if ( args[0].equalsIgnoreCase("promote") ) {
			
		}
		if ( args[0].equalsIgnoreCase("demote") ) {
			
		}
		if ( args[0].equalsIgnoreCase("list") ) {
			
		}
		if ( args[0].equalsIgnoreCase("tell") ) {
			
		}
		if ( args[0].equalsIgnoreCase("who") ) {
			
		}
		return true;
	}

}
