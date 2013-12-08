package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * Change the state of the player to afk
 * @author Matthew Ball
 *
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
		
		if ( !(player instanceof Player) )
			return false;
		
		CyniChat.data.getDetails( player.getName() ).changeAfk();
		return true;
	}

}
