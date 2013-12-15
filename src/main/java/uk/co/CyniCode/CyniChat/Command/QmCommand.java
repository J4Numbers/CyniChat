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

import uk.co.CyniCode.CyniChat.Chatting.ServerChatListener;
import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;

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
		
		Channel thisChan = CyniChat.data.getChannel( args[0] );
		UserDetails thisUser = CyniChat.data.getOnlineDetails( (Player) player );
		
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
		
		//Is there a message to be quicked?
		if ( args.length >= 2 ) {
			
			//Yep. Let's make it into an event...
			ChannelChatEvent newChat = new ChannelChatEvent(
					player.getName(),
					CyniChat.data.getChannel( args[0] ),
					stacker( args, 1, args.length ),
					ServerChatListener.getRecipients( args[0], player.getName() ),
					" :"
				);
			
			//Then call the event to save us a job
			Bukkit.getServer().getPluginManager().callEvent( newChat );
			
			//And then run.
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
	 * @param min : The first element of the array we'll look at
	 * @param max : The length of the array (one more than the one we'll
	 *  look at
	 * @return finalString : The sentence that is created
	 */
	public String stacker( String[] args, int min, int max ) {
		
		//Make more string bases
		String finalString = "";
		String connect = "";
		
		//And for every word in the array...
		for ( int i = min; i < max; i++ ) {
			//Add it to the string
			finalString += connect + args[i];
			connect = " ";
		}
		
		//And return it here
		return finalString;
		
	}
	
}