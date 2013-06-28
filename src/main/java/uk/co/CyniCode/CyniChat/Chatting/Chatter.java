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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public static void joinEvent( PlayerJoinEvent event ) {
		Player player = event.getPlayer();
		CyniChat.printDebug("Player joined");
		FileHandling.loadPlayerDetails( player );
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public static void leaveEvent( PlayerQuitEvent event ) {
		CyniChat.printDebug("Player Left");
		FileHandling.dumpPlayerDetails( event.getPlayer() );
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public static void chatEvent( AsyncPlayerChatEvent event ) {
		CyniChat.printDebug( "Format ::== " + event.getFormat() );
		CyniChat.printDebug( "Recipients ::== " + looper( event.getRecipients() ) );
		
	}
	
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