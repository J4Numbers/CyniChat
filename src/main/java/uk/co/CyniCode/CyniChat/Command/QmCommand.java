package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Quick Message class to deal with all commands that start with /qm
 * 
 * @author CyniCode
 */
public class QmCommand implements CommandExecutor {
	
	/**
	 * So we have the command and the bits that follow it, let's transmit
	 * it to the right channel and the right people
	 * @param player
	 * @param command
	 * @param key
	 * @param args
	 * @return boolean as per the damn interface
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] args) {
		
		//Is there a message to be quicked?
		if ( args.length >= 2 ) {
			//Yep, pass it on
			GeneralCommand.quickMessage( player, args[0], stacker( args ) );
			return true;
		}
		
		//Nope... tell them off
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
		
		//TODO: Make sure that the first array value is not
		// the channel.
		
		//Make more string bases
		String finalString = "";
		String connect = "";
		
		//And for every word in the array...
		for ( String arg : args ) {
			//Add it to the string
			finalString += connect + arg;
			connect = " ";
		}
		
		//And return it here
		return finalString;
		
	}
	
}
