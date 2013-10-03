package uk.co.CyniCode.CyniChat.irc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.ListenerAdapter;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.MessageEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public class Chatting extends ListenerAdapter {

	public void onMessage( MessageEvent event ) throws Exception {
		if ( event.getMessage().startsWith("?help") )
			event.respond("You wanted help?");
		
		CyniChat.printDebug( "Sender: " + event.getUser().getNick() );
		CyniChat.printDebug( "Channel: " + event.getChannel().getName().toLowerCase() );
		CyniChat.printDebug( "Message: " + event.getMessage() );
		
		if ( DataManager.getLinkedChannels().containsKey( event.getChannel().getName().toLowerCase() ) ) {
			Player[] online = Bukkit.getServer().getOnlinePlayers();
			String thisChannel = DataManager.getLinkedChannels().get( event.getChannel().getName().toLowerCase() );
			Channel thisChan = DataManager.getChannel( thisChannel );
			
			CyniChat.printDebug( "Match found: "+ event.getChannel().getName() + " -> " + thisChannel );
			
			for ( int i=0; i<online.length; i++ ) {
				
				UserDetails curPl = DataManager.getOnlineDetails( online[i] );
				
				if ( curPl.getAllChannels().contains( thisChannel ) ) {
					CyniChat.printDebug("Sending message to "+ online[i].getDisplayName() );
					String outing = thisChan.getColour() + "[IRC] ["+thisChan.getNick()+"] ";
					outing += event.getUser().getNick() + " : " + event.getMessage();
					
					online[i].sendMessage( outing );
				}
				
			}
		}
	}

}
