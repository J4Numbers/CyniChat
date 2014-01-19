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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

/**
 * A class to do something
 */
public class OnChannelChatEvent implements IChatEndpoint {


	/**
	 * Make a method for returning formatted output for a chat event
	 * @param event : The event that we're taking our cues from
	 * @return the formatted string of [<channel>] <player> : <message>
	 */
	public static String getCompleteMessage( ChannelChatEvent event ) {
		//Make the format string
		return String.format( "%s %s%s %s ", event.getChannel().getColour() +
				"[" + event.getChannel().getNick() + "]",
				CyniChat.perms.getPlayerFull( event.getSender().getPlayer() ),
				event.getConnector(),
				event.getChannel().getColour() + event.getMessage() );
	}

	/**
	 * Catch our event and play it to the server
	 *
	 * @param event : The event we're listening to
	 */
	public static void hearMessage(ChannelChatEvent event) {

		if (!event.isCancelled()) {

			CyniChat.printDebug("Event heard...");

			//Since we're listening for our own event... take nothing for
			// granted and take all the values from this
			for ( Player forAllPlayers : event.getRecipients() ) {
				CyniChat.printDebug( "Sending message to "+forAllPlayers.getDisplayName() );
				forAllPlayers.sendMessage( getCompleteMessage( event ) );
				CyniChat.printDebug( "Message sent to "+forAllPlayers.getDisplayName() );
			}

			CyniChat.printDebug( "Sent messages..." );

			//Finally having done that... route the message according to any
			// given instructions
			ChatRouter.routeMessage(ChatRouter.EndpointType.PLAYER, event.getSenderName(),
					event.getChannel().getName(), event.getMessage());

		}

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

		//ChannelChatEvent newChat = new ChannelChatEvent(
		//		player,
		//		chatChannel,
		//		message,
		//		getRecipients( channel )
		//	);

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
