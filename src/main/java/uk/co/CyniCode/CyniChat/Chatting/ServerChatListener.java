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

package uk.co.CyniCode.CyniChat.Chatting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

/**
 * A listener class for everything
 * Three parts: Log in, Log out, and speak
 *
 * @author CyniCode
 */
public class ServerChatListener implements Listener {
	
	/**
	 * Listen for any people joining the server so we can load in their
	 * configurations or generate a new one
	 *
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void joinEvent(PlayerJoinEvent event) {
		
		//Tell the debug something happened
		CyniChat.printDebug("Player joined");
		
		//Load player details into online users.
		CyniChat.data.bindPlayer(event.getPlayer());
		
		//Debug again due to a testing error
		CyniChat.printDebug( "Player Bound" );
		
	}
	
	/**
	 * Listen for anyone leaving the server so that we can dump their
	 * UserDetails into the config and have shot of them
	 *
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void leaveEvent(PlayerQuitEvent event) {
		
		//Tell the console something left
		CyniChat.printDebug("Player Left");
		
		//And make the player leave the plugin
		CyniChat.data.unbindPlayer( event.getPlayer() );
		
		//Some more debug, just in case
		CyniChat.printDebug( "Player Unbound" );
		
	}
	
	/**
	 * A command has been fed into the server... wat do?
	 *
	 * @param event : The Command event that we're checking
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void commandEvent(PlayerCommandPreprocessEvent event) {
		
		CyniChat.printDebug( "Command event called..." );
		
		//Okay... so remove the slash from the event so we have a 
		// command to play with
		String comm = event.getMessage().replaceFirst("/", "");
		
		//Split it into key words and deal with them seperately
		String[] bits = comm.split(" ");
		
		//The message itself, so everything excluding the first word
		String mess = comm.substring(bits[0].length() + 1, comm.length());
		
		//Print the full command
		CyniChat.printDebug( "Command : " + comm);
		
		//Print the first key word
		CyniChat.printDebug( "Key word: " + bits[0]);
		
		//Print everything else
		CyniChat.printDebug( "Message: " + mess);
		
		//If the command is not registered to anything...
		if (CyniChat.ifCommandExists(bits[0]) == false) {
			
			CyniChat.printDebug( "No command existed with this name" );
			
			//Then execute it as a quick message
			if (CyniChat.data.getChannel(bits[0]) != null) {
				
				CyniChat.printDebug( "Sending a message..." );
				
				ChannelChatEvent newChat = new ChannelChatEvent(
						event.getPlayer().getDisplayName(),
						CyniChat.data.getChannel( bits[0] ),
						mess,
						getRecipients( bits[0], event.getPlayer().getDisplayName() ),
						" :"
					);
				
				//Call the event and let it be dealt with from there
				Bukkit.getServer().getPluginManager().callEvent( newChat );
				
				CyniChat.printDebug( "Cancelling the event..." );
				
				//Also... cancel the event
				event.setCancelled(true);
				
			} else {
				
				//Send a debug to say that nothing happened
				CyniChat.printDebug("No channel was found for this name");
				
			}
			
		} else {
			
			//Otherwise... the command was registered with a plugin somewhere
			CyniChat.printDebug("A command existed with this prefix of \"" + bits[0] + "\"");
			
		}
		
	}
	
	/**
	 * Listen for any chatter on the server so that I can print debug of it for
	 * the moment
	 *
	 * @param event : This is what we're listening for
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void chatEvent(AsyncPlayerChatEvent event) {
		
		//Alright... firstly, get some debug done
		CyniChat.printDebug("Format ::== " + event.getFormat());
		//CyniChat.printDebug( "Recipients ::== " + looper( event.getRecipients() ) );
		
		//Then register the basics for what we're going to use
		Player player = event.getPlayer();
		UserDetails user = CyniChat.data.getOnlineDetails(player);
		
		//If the user has no current channel...
		if ( user.getCurrentChannel().equals("") ) {
			
			//Send out a ton of debug
			user.printAll();
			
			//Tell them that they are in no channels
			player.sendMessage("You are in no channels. Join one to talk.");
			
			//Cancel the chat event and return
			event.setCancelled(true);
			return;
			
		}
		
		//Okay... look at what the channel we're sending a message on
		// is going to be.
		Channel current = CyniChat.data.getChannel(user.getCurrentChannel().toLowerCase());
		
		//Is the user silenced?
		if ( user.getSilenced() ) {
			
			//Yep... send all the debug again
			user.printAll();
			
			//Tell them so
			player.sendMessage("You have been globally muted, you cannot talk.");
			
			//And cancel the event before we exit
			event.setCancelled(true);
			return;
			
		}
		
		//Is the user muted inside the channel?
		if ( user.getMutedChannels().contains( current.getName().toLowerCase() ) ) {
			
			//Apparently so... wonder what they did
			//Again, send all the debug
			user.printAll();
			
			//Tell them that they can't talk here
			player.sendMessage("You have been muted in this channel, move to another channel to talk.");
			
			//Cancel the event and return
			event.setCancelled(true);
			return;
			
		}
		
		//Is the channel protected?
		if ( current.isProtected() ) {
			
			//Why yes. Yes it is.
			//Can our friend talk in this channel?
			if ( !CyniChat.perms.checkPerm(player, "cynichat.basic.talk."
						+ current.getName().toLowerCase()) ) {
				
				//Nope... 'fraid not
				//Print out their details
				user.printAll();
				
				//And tell them that they have been denied
				player.sendMessage("You do not have the permission to talk here.");
				
				//Then cancel the event and return
				event.setCancelled(true);
				return;
				
			}
			
		}
		
		CyniChat.printDebug( "Generating new chat event..." );
		
		//Make the chat event and let anyone access it for a moment or two
		ChannelChatEvent newChatter = new ChannelChatEvent(
				player.getDisplayName(), current, event.getMessage(), 
				getRecipients( current.getName(), player.getDisplayName() ), " :" );
		
		CyniChat.printDebug( "And sending it onwards" );
		
		Bukkit.getServer().getPluginManager().callEvent(newChatter);

		CyniChat.printDebug( "Cancelling the event..." );
		
		//Cancel the original event
		event.setCancelled( true );
		
		CyniChat.printDebug( "Event has been cancelled..." );

		CyniChat.printDebug( "Now call what we've been left with" );

		OnChannelChatEvent.hearMessage( newChatter );

		CyniChat.printDebug( "Event called" );
		
	}
	
	public static Set<Player> getRecipients( String curChan, String sender ) {
		
		Set<Player> players = new HashSet<Player>();
		
		for ( Map.Entry< String, UserDetails > entrySet : 
				CyniChat.data.getOnlineUsers().entrySet() ) {
			
			//Logging their details in the process...
			UserDetails current = entrySet.getValue();
			
			//And adding them to a list of debugs
			CyniChat.printDebug( "Current player: "+ entrySet.getKey() );
			
			//Ask if they are in this channel
			if ( current.getAllChannels().contains( curChan ) ) {
				
				//Since they are, debug that fact
				CyniChat.printDebug( entrySet.getKey() + " is in the channel..." );
				
				if ( !current.getIgnoring().contains( sender.toLowerCase() ) ) {
					
					CyniChat.printDebug( current.getName() + " was not ignoring " + sender );
					
					current.getVerboseIgnoring();
					
					//So actually add them to the list
					players.add( current.getPlayer() );
					
				}
				
			}
			
		}
		
		return players;
		
	}
	
	/**
	 * Loop through a set
	 *
	 * @param item : This is what we're iterating over
	 * @return the strings within the set
	 */
	public static String looper(Set<Player> item) {
		
		//Initialise the variable for the returned string
		String recip = "";
		
		//Iterate through all the players and add them to the
		// final string
		for ( Player arrItem : item )
			recip += arrItem.getDisplayName() + ", ";
		
		//Then return that final string
		return recip;
		
	}
	
}