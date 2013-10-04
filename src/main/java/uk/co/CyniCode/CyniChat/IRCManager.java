package uk.co.CyniCode.CyniChat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.co.CyniCode.CyniChat.irc.Chatting;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.Channel;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.PircBotX;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.exception.IrcException;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.exception.NickAlreadyInUseException;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public class IRCManager {

private PircBotX bot;
	
	public IRCManager( CyniChat plugin ) throws Exception {
		
		this.bot = new PircBotX();
		
		this.bot.getListenerManager().addListener( new Chatting() );
		
		this.bot.setName( plugin.getConfig().getString("CyniChat.irc.nickname") );
		this.bot.setLogin( "CyniBot" );
		try {
			this.bot.connect( plugin.getConfig().getString("CyniChat.irc.hostname"), plugin.getConfig().getInt("CyniChat.irc.port") );
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	public void sendMessage( String chan, String sender, String message ) {
		if ( CyniChat.IRC == true && chan != "" ) {
			Channel sendChan = this.bot.getChannel( chan );
			this.bot.sendMessage( sendChan, sender + " : " + message );
		}
	}

	public void sendAction( String chan, UserDetails sender, String message ) {
		if ( CyniChat.IRC == true && chan != "" ) {
			Channel sendChan = this.bot.getChannel( chan );
			this.bot.sendMessage( sendChan, message );
		}
	}
	
	public void loadChannels(
			Map<String, uk.co.CyniCode.CyniChat.objects.Channel> allChans) {
		
		Map<String, String> channels = new HashMap<String, String>();
		Map<String, String> actingChans = new HashMap<String, String>();
		
		Set<String> keys = allChans.keySet();
		Iterator<String> iter = keys.iterator();
		
		try {
			while ( iter.hasNext() ) {
				
				String thisChan = iter.next();
				
				uk.co.CyniCode.CyniChat.objects.Channel current = allChans.get( thisChan );
				
				if ( current.getIRC() != "" ) {
					bot.joinChannel( current.getIRC().toLowerCase(), current.getIRCPass());
					actingChans.put( current.getIRC().toLowerCase(), thisChan );
				}
			}
			
			DataManager.setIRCChans( actingChans );
		} catch ( Exception e ) {
			
		}
	}

	public void restart() {
		CyniChat.printWarning( "Restarting the IRC bot..." );
		CyniChat.printInfo( "Stopping the IRC bot..." );
		bot.shutdown();
		CyniChat.printInfo( "Starting up the IRC bot again..." );
		try {
			bot.reconnect();
			loadChannels( DataManager.returnAllChannels() );
			CyniChat.printInfo( "Reconnected successfully" );
		} catch ( IOException e ) {
			CyniChat.printSevere( "We could not connect..." );
			e.printStackTrace();
		} catch ( NickAlreadyInUseException e ) {
			CyniChat.printSevere( "Our nickname was already in use..." );
			e.printStackTrace();
		} catch ( IrcException e ) {
			CyniChat.printSevere( "IRC has failed... call in the drones..." );
			e.printStackTrace();
		}
		return;
	}

	public void stop() {
		CyniChat.printInfo( "Shutting down IRC..." );
		this.bot.shutdown( true );
		CyniChat.printInfo( "IRC has been killed." );
	}
}
