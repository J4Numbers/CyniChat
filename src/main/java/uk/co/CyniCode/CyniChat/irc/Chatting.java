package uk.co.CyniCode.CyniChat.irc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.IRCManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Deal with all the output that the bot can see
 * @author Matthew Ball
 *
 */
public class Chatting extends ListenerAdapter {

	/**
	 * Listen for all the chatter that is going on on the IRC bot's end
	 * so that any commands given there are going to be executed while
	 * commands given from inside MC will be left alone.
	 */
	public void onMessage( MessageEvent event ) throws Exception {
		if ( event.getMessage().startsWith(":?") ) {
			String[] argments = event.getMessage().split( " " );
			org.pircbotx.Channel thisChan = event.getChannel();
			
			if ( argments.length == 1 ) {
				CyniChat.printDebug( "Default used..." );
				ircResponses.helpOutput( event.getBot(), event.getUser() );
				return;
			}
			
			if ( argments[1].equalsIgnoreCase("help") ) {
				CyniChat.printDebug( "Help selected..." );
				ircResponses.helpOutput( event.getBot(), event.getUser() );
				return;
			}
			
			if ( argments[1].equalsIgnoreCase( "list" ) ) {
				CyniChat.printDebug( "Listing chosen..." );
				if ( argments.length > 2 ) {
					if ( argments[2].equalsIgnoreCase( "all" ) ) {
						CyniChat.printDebug( "You've either got 'all' as parameter..." );
						CyniChat.printDebug( event.getUser().getNick()+" : "+thisChan.getName() );
						ircResponses.listOutput( event.getUser(), event.getBot(), thisChan.getName(), true );
						return;
					}
				} else {
					CyniChat.printDebug( "Or you don't...." );
					CyniChat.printDebug( event.getUser().getNick()+" : "+thisChan.getName() );
					ircResponses.listOutput( event.getUser(), event.getBot(), thisChan.getName(), false );
					return;
				}
				return;
			}
			
			if ( argments[1].equalsIgnoreCase("kick") 
					&& thisChan.isOp( event.getUser() ) ) {
				CyniChat.printDebug( "Kicking..." );
				if ( argments[2] != null ) {
					ircResponses.kickOutput( event.getUser(), event.getBot(), argments[2], thisChan.getName() );
					return;
				}
				event.respond( "I'm sorry, you must include a person to be kicked" );
				return;
			}
			
			if ( ( argments[1].equalsIgnoreCase("ban") 
						|| argments[1].equalsIgnoreCase("unban") ) 
					&& thisChan.isOp( event.getUser() ) ) {
				CyniChat.printDebug( "Banning..." );
				if ( argments[2] != null ) {
					
					if ( argments[1].equalsIgnoreCase("ban") ){
						ircResponses.banOutput( event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), false );
					} else {
						ircResponses.banOutput( event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), true );
					}
					return;
				}
				event.respond( "I'm sorry, you must include a person to be un/banned" );
				return;
			}
			
			if ( ( argments[1].equalsIgnoreCase("mute")
						|| argments[1].equalsIgnoreCase("unmute") )
					&& thisChan.isOp( event.getUser() ) ) {
				CyniChat.printDebug( "Muting..." );
				if ( argments[2] != null ) {
					
					if ( argments[1].equalsIgnoreCase("mute") ) {
						ircResponses.muteOutput( event.getUser(), event.getBot(), argments[2], thisChan.getName(), false );
					} else {
						ircResponses.muteOutput( event.getUser(), event.getBot(), argments[2], thisChan.getName(), true );
					}
					return;
					
				}
				event.respond( "I'm sorry, you must include a person to be un/muted" );
				return;
			}
			
			if ( argments[1].equalsIgnoreCase("restart") 
					&& thisChan.isOp( event.getUser() ) ) {
				CyniChat.printDebug( "Restarting..." );
				CyniChat.PBot.restart();
				return;
			}
			
			if ( argments[1].equalsIgnoreCase("kill")
					&& thisChan.isOp( event.getUser() ) ) {
				CyniChat.printDebug( "Murdering..." );
				CyniChat.PBot.stop();
				return;
			}
			
			CyniChat.printDebug( "\""+argments[1]+"\"" );
			
			return;
		}
		
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

	@Override
	public void onPrivateMessage( PrivateMessageEvent event ) {
		CyniChat.printDebug( "Private message called!" );
		if ( event.getMessage().startsWith( ":?" ) ) {
			String[] argments = event.getMessage().split( " " );
			CyniChat.printDebug( ":? called by " + event.getUser().getNick() );
			
			if ( argments[1].equalsIgnoreCase( "talk" ) ) {
				CyniChat.printDebug( "Talking with " +argments.length+ " args... " );
				if ( argments.length > 3 ) {
					
					CyniChat.printDebug( "Talking..." );
					if ( ircResponses.talkOutput( event.getBot(), argments[2], stacker( argments, 3, argments.length ) ) == false ) 
						event.respond( "Invalid statement. Please make sure that channel exits in the MC server." );
				}
				
				return;
			}
			
			ircResponses.helpOutput( event.getBot(), event.getUser() );
		}
	}
	
	public String stacker( String[] args, int start, int end ) {
		
		String finalString = "";
		String connector = "";
		
		for ( int i = start; i < end; i++ ) {
			finalString += connector + args[i];
			connector = " ";
		}
		
		return finalString;
	}
	
}
