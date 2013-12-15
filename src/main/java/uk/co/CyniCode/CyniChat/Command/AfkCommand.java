package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * Change the state of the player to afk
 * 
 * @author CyniCode
 */
public class AfkCommand implements CommandExecutor {
	
	/**
	 * When we get the command, simply switch the player from state to state
	 * @param player
	 * @param command
	 * @param key
	 * @param objects
	 * @return
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		
		//If the player is not a player...
		if ( !(player instanceof Player) )
			//Return false
			return false;
		
		//Otherwise, switch the status of their afk-ness
		CyniChat.data.getDetails( player.getName() ).changeAfk();
		
		//and return.
		return true;
		
	}
	
}