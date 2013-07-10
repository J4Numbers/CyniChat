package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.Command.HelpCommand;

public class ChCommand implements CommandExecutor {

	public CyniChat plugin;
	public ChCommand(CyniChat plugin) {
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
			if ( args[1] != null )
				HelpCommand.Command( player, args[1] );
			HelpCommand.Command( player, "" );
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
				return true;
			}
		}
		if ( args[0].equalsIgnoreCase("ban") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					BanCommand.ban( player, DataManager.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				BanCommand.ban( player, DataManager.getChannel( DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			BanCommand.banInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("unban") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					BanCommand.unban( player, DataManager.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				BanCommand.unban( player, DataManager.getChannel( DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			BanCommand.unbanInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("mute") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					MuteCommand.mute( player, DataManager.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				MuteCommand.mute( player, DataManager.getChannel( DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			MuteCommand.muteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("unmute") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					MuteCommand.unmute( player, DataManager.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				MuteCommand.unmute( player, DataManager.getChannel( DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			MuteCommand.unmuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("ignore") ) {
			if ( args[1] != null ) {
				MuteCommand.ignore( player, args[1] );
			} else {
				MuteCommand.ignoreInfo(player);
				return true;
			}
		}
		if ( args[0].equalsIgnoreCase("hear") ) {
			if ( args[1] != null ) {
				MuteCommand.hear( player, args[1] );
			} else {
				MuteCommand.hearInfo(player);
				return true;
			}
		}
		if ( args[0].equalsIgnoreCase("gmute") ) {
			if ( args[1] != null ) {
				MuteCommand.gmute( player, args[1] );
				return true;
			}
			MuteCommand.gmuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("gunmute") ) {
			if ( args[1] != null ) {
				MuteCommand.gUnMute(player, args[1]);
				return true;
			}
			MuteCommand.gUnMuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("create") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					AdminCommand.create( player, args[1], args[2], false );
					return true;
				}
				AdminCommand.create( player, args[1], String.valueOf( args[1].toLowerCase().charAt(0) ), false );
				return true;
			}
			AdminCommand.createInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("remove") ) {
			if ( args[1] != null ) {
				AdminCommand.remove( player, args[1] );
			}
			AdminCommand.removeInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("reload") ) {
			GeneralCommand.reload( player );
		}
		if ( args[0].equalsIgnoreCase("promote") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					ModCommand.promote( player, args[2].toLowerCase(), args[1] );
					return true;
				}
				ModCommand.promote( player, DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel(), args[1] );
				return true;
			}
			ModCommand.promoteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("demote") ) {
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					ModCommand.demote( player, args[2].toLowerCase(), args[1] );
					return true;
				}
				ModCommand.demote( player, DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel(), args[1] );
				return true;
			}
			ModCommand.demoteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("list") ) {
			if ( args[1] != null ) {
				GeneralCommand.list( player, Integer.parseInt( args[1] ) );
				return true;
			}
			GeneralCommand.list( player, 1 );
			return true;
		}
		if ( args[0].equalsIgnoreCase("who") ) {
			if ( args[1] != null ) {
				GeneralCommand.who( player, args[1] );
				return true;
			}
			GeneralCommand.who( player, DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() );
			return true;
		}
		if ( args[0].equalsIgnoreCase("qm") ){
			if ( args[2] != null ) {
				GeneralCommand.quickMessage( player, args[1], args[2] );
				return true;
			}
			GeneralCommand.qmInfo( player );
		}
		if ( args[0].equalsIgnoreCase("info") ){
			if ( args[1] != null ) {
				GeneralCommand.info( player, args[1] );
				return true;
			}
			GeneralCommand.info( player, DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() );
			return true;
		}
		if ( args[0].equalsIgnoreCase("kick") ){
			if ( args[1] != null ) {
				if ( args[2] != null ) {
					BanCommand.kick( player, DataManager.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				BanCommand.kick( player, DataManager.getChannel( DataManager.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			BanCommand.kickInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("save") ){
			GeneralCommand.save( player );
			return true;
		}
		if ( args.length < 1 || args.length > 2 ) {
			JoinCommand.info( player );
			return true;
		} else {
			if ( args.length == 2 ) {
				JoinCommand.join( player, args[0], args[1] );
				return true;
			} else {
				JoinCommand.join( player, args[0], "");
				return true;
			}
		}
	}

}
