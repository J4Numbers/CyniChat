package uk.co.CyniCode.CyniChat.Chatting;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Command.GeneralCommand;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

/**
 * A listener class for everything
 * Three parts: Log in, Log out, and speak
 *
 * @author Matthew Ball
 */
public class ServerChatListener implements Listener, IChatEndpoint {
	
	public static CyniChat plugin;
	
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
		
		//Okay... so remove the slash from the event so we have a 
		// command to play with
		String comm = event.getMessage().replaceFirst("/", "");
		
		//Split it into key words and deal with them seperately
		String[] bits = comm.split(" ");
		
		//The message itself, so everything excluding the first word
		String mess = comm.substring(bits[0].length() + 1, comm.length());
		
		//Print the full command
		CyniChat.printDebug(comm);
		
		//Print the first key word
		CyniChat.printDebug(bits[0]);
		
		//Print everything else
		CyniChat.printDebug(mess);
		
		//If the command is not registered to anything...
		if (CyniChat.ifCommandExists(bits[0]) == false) {
			
			//Then execute it as a quick message
			if (CyniChat.data.getChannel(bits[0]) != null) {
				
				GeneralCommand.quickMessage((CommandSender) event.getPlayer(), bits[0], mess);
				
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
			if ( CyniChat.perms.checkPerm(player, "cynichat.basic.talk." 
						+ current.getName().toLowerCase()) == false) {
				
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
		
		//And prepare to look at everyone who is going to get the
		// message.
		Player[] all = new Player[event.getRecipients().size()];
		int count = 0;
		
		//While there is someone else that can get the message...
		for ( Player currentPlayer : event.getRecipients() ) {
			
			//Get the current player's user details
			UserDetails users = CyniChat.data.getOnlineDetails(currentPlayer);
			
			//Then debug the information about that player
			CyniChat.printDebug(currentPlayer.getName() + " : " + users.getAllVerboseChannels());
			
			//And ask if they are joined to this channel
			if (!users.getAllChannels().contains(current.getName().toLowerCase())) {
				
				//If they are not, then add the player to an array
				// of all those we're going to remove from the original
				// sending set
				all[count] = currentPlayer;
				++count;
				
			} else 
				
				//Otherwise... if the player is ignoring this person
				// and are in the channel that has a message,
				// remove them from the set too
				if ((users.getIgnoring().contains(user.getName())) && (users.getAllChannels().contains(current.getName().toLowerCase()))) {
					all[count] = currentPlayer;
					++count;
				}
			
		}
		
		//If the count has been incremented
		if (count > 0) {
			
			//Iterate through the count and strike anyone who
			// lies within
			for ( Player removal : all ) {
				CyniChat.printDebug("Removed " + removal.getDisplayName());
				event.getRecipients().remove( removal );
			}
			
		}
		
		//Make the chat event and let anyone access it for a moment or two
		ChannelChatEvent newChatter = new ChannelChatEvent(player.getDisplayName(),
				current, event.getMessage(), event.getRecipients());
		Bukkit.getServer().getPluginManager().callEvent(newChatter);
		
		//Cancel the original event
		event.setCancelled( true );
		
		//And replace it with our own
		for ( Player forAllPlayers : newChatter.getRecipients() )
			forAllPlayers.sendMessage( getCompleteMessage( newChatter ) );
		
		//Finally having done that... route the message according to any
		// given instructions
		ChatRouter.routeMessage( ChatRouter.EndpointType.PLAYER, newChatter.getSenderName(),
					newChatter.getChannel().getName(), newChatter.getMessage());
		
	}
	
	/**
	 * Make a method for returning formatted output for a chat event
	 * @param event : The event that we're taking our cues from
	 * @return the formatted string of [<channel>] <player> : <message>
	 */
	public static String getCompleteMessage( ChannelChatEvent event ) {
		//Make the format string
		return String.format( "%s %s : %s ", event.getChannel().getColour() + 
								"[" + event.getChannel().getNick() + "]",
								CyniChat.perms.getPlayerFull( event.getSender().getPlayer() ),
								event.getChannel().getColour() + event.getMessage() );
	}
	
	/**
	 * Currently used as a testing thing for the event that is registered here
	 *
	 * @param event : The event we're listening to (Only visible if debug is on)
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void testingRegister(ChannelChatEvent event) {
		CyniChat.printDebug(event.getSenderName() + " said " + event.getMessage() + " in " + event.getChannel().getName());
		CyniChat.printDebug(event.printVerboseRecip());
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
	
	/**
	 * Let's handle the message in a way that it wants to be handled
	 * @param type : The endpoint that it wants to go to
	 * @param player : The player that sent it
	 * @param channel : The channel that they sent it on
	 * @param message : The message that they sent
	 */
	public void giveMessage(ChatRouter.EndpointType type, String player, String channel, String message) {
		
		//Has the message come from IRC?
		if (type == ChatRouter.EndpointType.IRC) {
			
			//Yes it does. Hand it over to the handler for IRC
			// messages
			CyniChat.printDebug( "Handling an IRC endtype" );
			_handleIRCMessage(player, channel, message);
			
		}
		
		//Does it want to go to bungee?
		if (type == ChatRouter.EndpointType.BUNGEE) {
			
			//Yep. Send it to the bungee handler
			CyniChat.printDebug( "Handling a bungee endtype" );
			_handleBungeeMessage(player, channel, message);
			return;
			
		}
		
		//We don't handle this type of message
		CyniChat.printDebug( type.name() + " endpoint not defined" );
		
	}
	
	/**
	 * Handle a message that came from IRC
	 * @param player : The nickname of the person who sent the message
	 * @param channel : The channel that they sent the message on
	 * @param message : The message that they actually sent
	 */
	private void _handleIRCMessage(String player, String channel, String message) {
		
		//Get all the online players for the server
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		
		//And the channel object we're dealing with
		Channel chatChannel = CyniChat.data.getChannel(channel);
		
		//For each and every player on the server...
		for (Player online1 : online) {
			
			//Get their details
			UserDetails curPl = CyniChat.data.getOnlineDetails(online1);
			
			//Is the current player joined to this channel?
			if (curPl.getAllChannels().contains(channel)) {
				
				//Yep. Let's send them the message from IRC
				CyniChat.printDebug("Sending message to " + online1.getDisplayName());
				String outing = chatChannel.getColour() + "[IRC] [" + chatChannel.getNick() + "] ";
				outing += player + " : " + message;
				online1.sendMessage(outing);
				
			}
			
		}
		
	}
	
	/**
	 * Handle a message originating from bungee
	 * @param player : The player that sent the message
	 * @param channel : The channel they sent it on
	 * @param message : The actual message
	 */
	private void _handleBungeeMessage(String player, String channel, String message) {
		
		//Tell the console what's happening
		CyniChat.printDebug( "Handling a bungee message..." );
		
		//Get the channel details
		Channel chatChannel = CyniChat.data.getChannel(channel);
		
		//Then check if the channel exists on this server
		if ( chatChannel == null ){
			
			//If it doesn't, drop it and run
			CyniChat.printDebug("Dropped bungee message from unknown channel " + channel + ":: " + player + " said " + message);
			return;
			
		}
		
		//Make the message
		String formattedMsg = chatChannel.getColour() + "[" + chatChannel.getNick() + "] " 
					+ player + " : " + chatChannel.getColour() + message;
		
		//And show it in the debug along with the channel we're sending it in
		CyniChat.printDebug( "Message : " + formattedMsg );
		CyniChat.printDebug( "On : " + chatChannel.getName() );
		
		//For every player on the server...
		for (Player p : Bukkit.getOnlinePlayers()) {
			
			//Print some basic debug about the player
			CyniChat.printDebug( "Current Player: " + p.getDisplayName() );
			CyniChat.printDebug( "Checking channel: " + chatChannel.getName() );
			
			//Go and ask if they are in the channel
			if (CyniChat.data.getOnlineDetails(p).getAllChannels().contains( chatChannel.getName() )) {
				
				//They are... send them the message
				CyniChat.printDebug( "Player was in the channel... sending" );
				p.sendMessage(formattedMsg);
				
			} else {
				
				//They're not... move on
				CyniChat.printDebug( "Player was not in the channel..." );
				
			}
			
			//Finally, just print the debug about them
			CyniChat.data.getOnlineDetails(p).printAll();
			
		}
		
	}
	
}