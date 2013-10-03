package uk.co.CyniCode.CyniChat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.co.CyniCode.CyniChat.irc.Chatting;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.Channel;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.PircBotX;

public class IRCManager {

private PircBotX bot;
	
	public IRCManager( CyniChat plugin ) throws Exception {
		
		this.bot = new PircBotX();
		
		this.bot.getListenerManager().addListener( new Chatting() );
		
		this.bot.setName( plugin.getConfig().getString("CyniChat.irc.nickname") );
		this.bot.setLogin( "CyniChatBot" );
		try {
			this.bot.connect( plugin.getConfig().getString("CyniChat.irc.hostname"), plugin.getConfig().getInt("CyniChat.irc.port") );
			this.bot.joinChannel( plugin.getConfig().getString("CyniChat.irc.channel") );
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

}
