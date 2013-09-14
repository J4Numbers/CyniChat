package uk.co.CyniCode.CyniChat.events;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public class ChannelChatEvent extends Event {
	
	private UserDetails player;
	private Channel channel;
	private Set<Player> recipients;
	
	private String message;
	private String senderName;
	
	private static final HandlerList handlers = new HandlerList();
	
	public ChannelChatEvent( String player, Channel channel, String message, Set<Player> recipients ) {
		this.player = DataManager.getDetails(player);
		this.channel = channel;
		this.message = message;
		this.senderName = player;
		this.recipients = recipients;
	}
	
	public UserDetails getSender() {
		return player;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String printVerboseRecip() {
		
		Iterator<Player> iterReci =  recipients.iterator();
		String Out = "";
		
		while ( iterReci.hasNext() ) {
			Player currentP = iterReci.next();
			Out += currentP.getDisplayName() + " ";
		}
		
		return Out;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
