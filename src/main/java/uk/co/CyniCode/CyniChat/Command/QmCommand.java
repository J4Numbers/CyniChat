package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QmCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command command, String key, String[] args) {
		if ( args.length >= 2 ) {
			GeneralCommand.quickMessage( player, args[0], stacker( args ) );
			return true;
		}
		GeneralCommand.qmInfo( player );
		
		return true;
	}

	public String stacker( String[] args ) {
		String finalString = "";
		String connect = "";
		
		for ( int i=1; i<args.length; i++) {
			finalString += connect + args[i];
			connect = " ";
		}
		
		return finalString;
	}
	
}
