package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Chatting.UserDetails;

public class RCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		if ( PermissionManager.checkPerm( (Player) player, "cynichat.basic.msg" ) ) {
			if ( objects.length == 1 ) {
				UserDetails current = DataManager.getOnlineDetails( (Player) player );
				if ( current.getLatest() != null ) {
					String message = stacker( objects );
					try {
						UserDetails other = DataManager.returnAllOnline().get( current.getLatest() );
						player.sendMessage("To "+other.getPlayer().getName()+" :"+message);
						other.getPlayer().sendMessage("from "+player.getName()+" :"+message);
						other.changeLatest( player.getName() );
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
		return false;
	}

	private String stacker(String[] objects) {
		try {
			String finalStacked = "";
			for ( int i=0; i<objects.length; i++) {
				finalStacked += " "+objects[i];
			}
			return finalStacked;
		} catch ( NullPointerException e ) {
			return "";
		}
	}

}
