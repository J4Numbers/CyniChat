package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Quick Message class to deal with all commands that start with /qm
 * @author Cynical
 *
 */
public class QmCommand implements CommandExecutor {

	/**
	 * So we have the command and the bits that follow it, let's transmit
	 * it to the right channel and the right people
	 * @param player
	 * @param command
	 * @param key
	 * @param args
	 * @return 
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] args) {
		if ( args.length >= 2 ) {
			GeneralCommand.quickMessage( player, args[0], stacker( args ) );
			return true;
		}
		
		GeneralCommand.qmInfo( player );
		
		return true;
	}

	/**
	 * You again? I should really just get a single instance of you for all the
	 * methods that need it.
	 * @param args : The set of words we're putting into a sentence
	 * @return finalString : The sentence that is created
	 */
	public String stacker( String[] args ) {
		String finalString = "";
		String connect = "";
		
		for ( String arg : args ) {
			finalString += connect + arg;
			connect = " ";
		}
		
		return finalString;
	}
	
}
