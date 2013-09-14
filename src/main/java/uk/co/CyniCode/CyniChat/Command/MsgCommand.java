package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public class MsgCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.basic.msg") )
				return false;
		if ( objects.length >= 2 ) {
			try {
				CyniChat.printDebug(objects[0]);
				UserDetails person = DataManager.returnAllOnline().get( objects[0].toLowerCase() );
				person.printAll();
				String Message = stacker( objects );
				CyniChat.printDebug( Message );
				player.sendMessage("To "+person.getPlayer().getName()+" :"+Message);
				person.getPlayer().sendMessage( "From "+player.getName()+" :"+Message );
				person.changeLatest( player.getName() );
				CyniChat.printDebug( person.getLatest() );
				if ( player instanceof Player ) {
					UserDetails user = DataManager.getOnlineDetails( (Player) player );
					user.changeLatest( person.getName() );
					CyniChat.printDebug( user.getLatest() );
				}
				return true;
			} catch ( NullPointerException e ) {
				player.sendMessage("This player is not online");
				e.printStackTrace();
				return true;
			}
		}
		player.sendMessage("Please enter a message to send");
		return true;
	}
	
	public String stacker( String[] stacking ) {
		try {
			String finalStack = "";
			for ( int i=1; i<stacking.length; i++ ) {
				finalStack += " "+stacking[i];
			}
			return finalStack;
		} catch ( NullPointerException e ) {
			return "";
		}
	}

}
