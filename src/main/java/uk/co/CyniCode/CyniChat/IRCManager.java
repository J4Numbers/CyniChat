package uk.co.CyniCode.CyniChat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.co.CyniCode.CyniChat.irc.Chatting;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

/**
 * An instantiation of a PircBotX bot
 * @author Matthew Ball
 * 
 */
public class IRCManager {

private PircBotX bot;

	/**
	 * Constructor for making a new Bot out of barely anything
	 * @param plugin : Used for getting the config options
	 * @throws Exception : So much that could go wrong here...
	 */
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
	
	/**
	 * Send a message through the bot in the format of :
	 * [SENDER] : [MESSAGE]
	 * @param chan : The channel we're sending the message on
	 * @param sender : The sender who, you guessed it, sent the message
	 * @param message : The message itself
	 */
	public void sendMessage( String chan, String sender, String message ) {
		if ( CyniChat.IRC == true && chan != "" ) {
			Channel sendChan = this.bot.getChannel( chan );
			this.bot.sendMessage( sendChan, sender + " : " + message );
		}
	}

	/**
	 * Send an action through the bot as a message in the form of
	 * [SENDER] [ACTION]
	 * @param chan : The channel that we're transmitting stuff over
	 * @param sender : The sender who sent the thing that was to be delivered
	 * @param message : The action itself
	 */
	public void sendAction( String chan, UserDetails sender, String message ) {
		if ( CyniChat.IRC == true && chan != "" ) {
			Channel sendChan = this.bot.getChannel( chan );
			this.bot.sendMessage( sendChan, message );
		}
	}
	
	/**
	 * Load all possible channels into the plugin and
	 * see which of them are actually going to be connecting
	 * to the IRC server and whatnot
	 * @param allChans : All the potential channels there are
	 */
	public void loadChannels(
			Map<String, uk.co.CyniCode.CyniChat.objects.Channel> allChans) {
		
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

	/**
	 * Restart the IRC component of the plugin via various means
	 */
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

	/**
	 * Kill the IRC component of the plugin ungracefully
	 * ...
	 * In other words, knock it over the head with a bat.
	 */
	public void stop() {
		CyniChat.printInfo( "Shutting down IRC..." );
		this.bot.shutdown( true );
		CyniChat.printInfo( "IRC has been killed." );
	}
}
