package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Class dedicated to the reply command
 * @author Matthew Ball
 *
 */
public class RCommand implements CommandExecutor {
	
	/**
	 * Reply to any previous messages that were given
	 * @param player
	 * @param command
	 * @param key
	 * @param objects
	 * @return 
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		if ( player instanceof Player ) {
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.msg" ) )
				return false;
		} else {
			player.sendMessage("Console cannot reply to people");
			return true;
		}
		if ( objects.length >= 1 ) {
			UserDetails current = DataManager.getOnlineDetails( (Player) player );
			if ( current.getLatest() != null ) {
				String message = stacker( objects );
				try {
					UserDetails other = DataManager.getOnlineUsers().get( current.getLatest() );
					player.sendMessage("To "+other.getPlayer().getName()+" :"+message);
					other.getPlayer().sendMessage("From "+player.getName()+" :"+message);
					other.changeLatest( player.getName().toLowerCase() );
					return true;
				} catch ( NullPointerException e ) {
					player.sendMessage("This player is no-longer online");
					return true;
				}
			}
			player.sendMessage("You have no-one to reply to");
			return true;
		}
		player.sendMessage("Please send a message");
		return true;
	}

	/**
	 * Yet ANOTHER sentence created
	 * @param objects : The thing we're stacking
	 * @return the sentence we're returning
	 */
	private String stacker(String[] objects) {
		try {
			String finalStacked = "";
			
			for (String object : objects)
				finalStacked += " " + object;
			
			return finalStacked;
			
		} catch ( NullPointerException e ) {
			return "";
		}
	}

}
