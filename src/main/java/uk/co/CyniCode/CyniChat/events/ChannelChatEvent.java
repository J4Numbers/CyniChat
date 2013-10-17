package uk.co.CyniCode.CyniChat.events;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * We're making a chat event
 * =D
 * @author Matthew Ball
 * 
 */
public class ChannelChatEvent extends Event {
	
	private UserDetails player;
	private Channel channel;
	private Set<Player> recipients;
	
	private String message;
	private String senderName;
	
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Activate and instantiate the event with the various bits and pieces
	 * @param player : The player that made the event
	 * @param channel : The channel that the event was in
	 * @param message : The message that the event contained
	 * @param recipients : The people the event will affect
	 */
	public ChannelChatEvent( String player, Channel channel, String message, Set<Player> recipients ) {
		this.player = DataManager.getDetails(player);
		this.channel = channel;
		this.message = message;
		this.senderName = player;
		this.recipients = recipients;
	}
	
	/**
	 * Get the sender of the event
	 * @return
	 */
	public UserDetails getSender() {
		return player;
	}
	
	/**
	 * Get the sender's actual name
	 * @return
	 */
	public String getSenderName() {
		return senderName;
	}
	
	/**
	 * Get the channel of the event
	 * @return
	 */
	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * Get the message the event contained
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Get a complete list of those that saw the event
	 * @return
	 */
	public String printVerboseRecip() {
		
		Iterator<Player> iterReci =  recipients.iterator();
		String Out = "";
		
		while ( iterReci.hasNext() ) {
			Player currentP = iterReci.next();
			Out += currentP.getDisplayName() + " ";
		}
		
		return Out;
	}
	
	/**
	 * Bukkit requirements
	 * @return
	 */
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Bukkit requirements
	 * @return
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
