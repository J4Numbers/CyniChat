package uk.co.CyniCode.CyniChat.Chatting;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.FileHandling;

public class Chatter implements Listener {
	
	public static CyniChat plugin;
	
	/**
	 * Listen for any people joining the server so we can load in their configurations or generate a new one
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void joinEvent( PlayerJoinEvent event ) {
		Player player = event.getPlayer();
		CyniChat.printDebug("Player joined");
		FileHandling.loadPlayerDetails( player );
	}
	
	/**
	 * Listen for anyone leaving the server so that we can dump their UserDetails into the config and have shot of them
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void leaveEvent( PlayerQuitEvent event ) {
		CyniChat.printDebug("Player Left");
		FileHandling.dumpPlayerDetails( event.getPlayer() );
	}
	
	/**
	 * Listen for any chatter on the server so that I can print debug of it for the moment
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void chatEvent( AsyncPlayerChatEvent event ) {
		CyniChat.printDebug( "Format ::== " + event.getFormat() );
		CyniChat.printDebug( "Recipients ::== " + looper( event.getRecipients() ) );
		
	}
	
	/**
	 * Loop through a set
	 * @param item : This is what we're iterating over
	 * @return the strings within the set
	 */
	public static String looper( Set<Player> item ) {
		String recip = null;
		int j = item.size();
		String[] arrItem = null;
		item.toArray(arrItem);
		for ( int i=0; i<j; i++ ) {
			recip += arrItem[i]+", ";
		}
		return recip;
	}
}