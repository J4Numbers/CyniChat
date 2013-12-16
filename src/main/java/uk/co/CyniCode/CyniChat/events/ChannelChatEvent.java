/**
 * Copyright 2013 CyniCode (numbers@cynicode.co.uk).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.CyniCode.CyniChat.events;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.CyniCode.CyniChat.CyniChat;

import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * We're making a chat event
 * =D
 * 
 * @author CyniCode
 */
public class ChannelChatEvent extends Event {
	
	/**
	 * This is the player that originally sent the message
	 */
	private UserDetails player;
	
	/**
	 * This is the channel that the player sent the message from
	 */
	private Channel channel;
	
	/**
	 * These are those which will see the message
	 */
	private Set<Player> recipients;
	
	/**
	 * This is the message itself
	 */
	private String message;
	
	/**
	 * And just for the hell of it... this is the name of the sender
	 */
	private String senderName;
	
	/**
	 * Create a string for the possible event of it being a /me message
	 */
	private String connector;
	
	/**
	 * Needed to make bukkit play nice
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Activate and instantiate the event with the various bits and pieces
	 * @param player : The player that made the event
	 * @param channel : The channel that the event was in
	 * @param message : The message that the event contained
	 * @param recipients : The people the event will affect
	 * @param connector : The thing that joins the two halves together
	 */
	public ChannelChatEvent( String player, Channel channel, String message, 
			Set<Player> recipients, String connector ) {
		this.player = CyniChat.data.getDetails(player);
		this.channel = channel;
		this.connector = connector;
		this.message = message;
		this.senderName = player;
		this.recipients = recipients;
	}
	
	/**
	 * Get the sender of the event
	 * @return the sender of the message
	 */
	public UserDetails getSender() {
		return player;
	}
	
	/**
	 * Set the new sender to a person of some shape or form
	 * @param newSender : The new player that is sending the event
	 */
	public void setSender( Player newSender ) {
		this.player = CyniChat.data.getOnlineDetails( newSender );
	}
	
	/**
	 * Get the sender's actual name
	 * @return the name of the sender of the message
	 */
	public String getSenderName() {
		return senderName;
	}
	
	/**
	 * Give the sender a new name
	 * @param newName : The name we're giving the person
	 */
	public void setSenderName( String newName ) {
		this.senderName = newName;
	}
	
	/**
	 * Get the channel of the event
	 * @return the channel which this message is being sent on
	 */
	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * Set the channel to something else
	 * @param newChannel : such as this
	 */
	public void setChannel( Channel newChannel ) {
		this.channel = newChannel;
	}
	
	/**
	 * Get the message the event contained
	 * @return the message of the event
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * We don't like what they were originally sending...
	 * @param newMessage : change it to this
	 */
	public void setMessage( String newMessage ) {
		this.message = newMessage;
	}
	
	/**
	 * We want to see what's connecting the two...
	 * @return the connector
	 */
	public String getConnector() {
		return connector;
	}
	
	/**
	 * Okay... they didn't like what we're using...
	 * @param connector : Set it to this instead
	 */
	public void setConnector( String connector ) {
		this.connector = connector;
	}
	
	/**
	 * Retrieve all those that are going to see this message
	 * @return a list of all the players that will see this message
	 */
	public Set<Player> getRecipients() {
		return this.recipients;
	}
	
	/**
	 * Set a new list of people that will see this message
	 * @param newSet : A new set of people to see the message
	 */
	public void setRecipients( Set<Player> newSet ) {
		this.recipients = newSet;
	}
	
	/**
	 * Get a complete list of those that saw the event
	 * @return a string of the recipients
	 */
	public String printVerboseRecip() {
		
		//Get the list of those that are going to hear the event
		Iterator<Player> iterReci =  getRecipients().iterator();
		
		//Initialise the output
		String out = "";
		
		//While there is another player in the set...
		while ( iterReci.hasNext() ) {
			
			//Add their name to the output
			out += iterReci.next().getDisplayName() + " ";
			
		}
		
		//And return what we must
		return out;
		
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