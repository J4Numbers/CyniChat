package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.Chatting.ServerChatListener;
import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * As much as this is playing with fire... this is for the /me command
 * so a player can write in the context of themselves
 * i.e. [g] Steve does this rather than [g] Steve : does that
 * 
 * @author CyniCode
 */
public class MeCommand implements CommandExecutor {
	
	/**
	 * On the command...
	 * @param player : The player that is doing the command
	 * @param command : The command they're executing
	 * @param key : A keyword of sorts
	 * @param objects : All the arguments afterwards
	 * @return true when complete (as per interface)
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		
		//Tell the console that shit is happening
		CyniChat.printDebug("Initialised a /me command");
		
		UserDetails thisUser = CyniChat.data.getOnlineDetails( (Player) player );
		Channel thisChan = CyniChat.data.getChannel( thisUser.getCurrentChannel() );
		
		if ( thisUser.getSilenced() ) {
			
			player.sendMessage( "You are silenced. You cannot speak." );
			return true;
			
		}
		
		if ( !thisUser.getAllChannels().contains( thisChan.getName() ) ) {
			
			player.sendMessage( "You are not in this channel." );
			return true;
			
		}
		
		if ( thisUser.getMutedChannels().contains( thisChan.getName() ) ) {
			
			player.sendMessage( "You are muted in this channel." );
			return true;
			
		}
		
		//And check to see if there is anything to put in the syntax of
		if ( objects[0] != null ) {
			
			//Now... create a message from the parts
			ChannelChatEvent newChat = new ChannelChatEvent(
					player.getName(),
					CyniChat.data.getChannel( CyniChat.data
						.getOnlineDetails( (Player) player )
						.getCurrentChannel() ),
					stacker( objects ),
					ServerChatListener.getRecipients( 
						CyniChat.data.getOnlineDetails( (Player) player )
						.getCurrentChannel(), player.getName() ),
					""
				);
			
			Bukkit.getServer().getPluginManager().callEvent( newChat );
			
			//String linkedChan = CyniChat.data.getChannel(curChan).getIRC();
			
			//Tell the console that it's over
			CyniChat.printDebug("End of a /me command");
			return true;
		}
		
		//Or tell the player that they can't do this impossible acction
		player.sendMessage("Please provide an action to go with the /me command");
		return true;
		
	}
	
	/**
	 * Stack a message together into a sentence
	 * @param message : An array of words to be formed into a sentence
	 * @return the sentence produced from the words
	 */
	public String stacker( String[] message ) {
		
		//Initiailise the string
		String stackedMessage = "";
		
		//And for each object, add it to that string
		for (String message1 : message)
			stackedMessage +=  message1 + " ";
		
		//Then return the final string
		return stackedMessage;
		
	}
	
}