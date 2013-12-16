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

package uk.co.CyniCode.CyniChat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;
import uk.co.CyniCode.CyniChat.irc.IrcChatListener;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

/**
 * An instantiation of a PircBotX bot
 * 
 * @author CyniCode
 */
public class IrcManager implements IChatEndpoint {
	
	/**
	 * This is the bot that is going to be doing all the legword
	 */
	private PircBotX bot;
	
	
	
	/**
	 * This is a channel that has absolute control over 
	 * the bot. People in this channel can restart, kill
	 * and talk to the bot.
	 */
	private String ircMasterChannel;
	
	/**
	 * This goes with the previous field, allowing connection
	 * to a protected channel in IRC.
	 */
	private String ircMasterPassword;
	
	/**
	 * Constructor for making a new Bot out of barely anything
	 * 
	 * @param plugin : Used for getting the config options
	 * @throws Exception : So much that could go wrong here...
	 */
	public IrcManager(CyniChat plugin) throws Exception {
		
		//Instantiate the bot
		this.bot = new PircBotX();
		
		//And instantiate the master channel fields
		this.ircMasterChannel = plugin.getConfig().getString("CyniChat.irc.admin_channel");
		this.ircMasterPassword = plugin.getConfig().getString("CyniChat.irc.admin_password");
		
		//Then add the listener
		this.bot.getListenerManager().addListener(new IrcChatListener());
		
		//And set the names of the bot
		this.bot.setName(plugin.getConfig().getString("CyniChat.irc.nickname"));
		this.bot.setLogin("CyniBot");
		
		try {
			
			//Then connect to the server in the config
			this.bot.connect(plugin.getConfig().getString("CyniChat.irc.hostname"),
					plugin.getConfig().getInt("CyniChat.irc.port"));
			
			//And the master channel with it...
			this.bot.joinChannel( this.ircMasterChannel, this.ircMasterPassword );
			
		} catch (Exception e) {
			
			//Or try to in any case...
			throw e;
			
		}
		
	}
	
	/**
	 * Send a message through the bot in the format of : [SENDER] : [MESSAGE]
	 * 
	 * @param chan : The channel we're sending the message on
	 * @param sender : The sender who, you guessed it, sent the message
	 * @param message : The message itself
	 */
	public void sendMessage(String chan, String sender, String message) {
		
		//If the IRC is enabled and the channel is there
		if ( CyniChat.IRC == true && !chan.equals( "" ) ) {
			
			//Get the channel that we're sending out over
			Channel sendChan = this.bot.getChannel(chan);
			
			//Then send the message out over it
			this.bot.sendMessage(sendChan, sender + " : " + message);
			
		}
		
	}
	
	/**
	 * Send an action through the bot as a message in the form of [SENDER]
	 * [ACTION]
	 * 
	 * @param chan : The channel that we're transmitting stuff over
	 * @param sender : The sender who sent the thing that was to be delivered
	 * @param message : The action itself
	 */
	public void sendAction(String chan, UserDetails sender, String message) {
		
		//Ask if IRC has been enabled and whether the channel
		// is even there
		if ( CyniChat.IRC == true && !chan.equals( "" ) ) {
			
			//Set the channel we're going to be sending over
			Channel sendChan = this.bot.getChannel(chan);
			
			//Then send over that channel
			this.bot.sendMessage(sendChan, message);
			
		}
		
	}
	
	/**
	 * Load all possible channels into the plugin and see which of them are
	 * actually going to be connecting to the IRC server and whatnot
	 * 
	 * @param allChans : All the potential channels there are
	 */
	public void loadChannels(
			Map<String, uk.co.CyniCode.CyniChat.objects.Channel> allChans) {
		
		//Initialise a new map for all the channels that are linked
		// to IRC
		Map<String, String> actingChans = new HashMap<String, String>();
		
		try {
			
			//Join the master channel
			bot.joinChannel( getIrcMasterChannel(), getIrcMasterPassword() );
			
			//Then, for all of them...
			for ( Map.Entry<String, uk.co.CyniCode.CyniChat.objects.Channel> thisChan
					: allChans.entrySet() ) {
				
				//If the IRC channel does exist...
				if ( !thisChan.getValue().getIRC().equals( "" ) ) {
					
					//Join the channel with the bot
					bot.joinChannel(thisChan.getValue().getIRC().toLowerCase(),
							thisChan.getValue().getIRCPass());
					
					//And put the channel into the map we prepared earlier
					actingChans.put(thisChan.getValue().getIRC().toLowerCase(),
							thisChan.getKey() );
					
				}
				
			}
			
			//Then set the map
			CyniChat.data.setLinkedChans(actingChans);
			
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * Restart the IRC component of the plugin via various means
	 */
	public void restart() {
		
		//Tell people that things are happening
		CyniChat.printWarning("Restarting the IRC bot...");
		CyniChat.printInfo("Stopping the IRC bot...");
		
		//And shut the bot down
		bot.shutdown();
		
		//Then announce that we're going to start it up again
		CyniChat.printInfo("Starting up the IRC bot again...");
		
		try {
			
			//And reconnect.
			bot.reconnect();
			
			//Then load in all the channels again
			loadChannels(CyniChat.data.getChannels());
			
			//And say that we were successful!
			CyniChat.printInfo("Reconnected successfully");
			
		//Or fail miserably...
		} catch (IOException e) {
			CyniChat.printSevere("We could not connect...");
			e.printStackTrace();
		} catch (NickAlreadyInUseException e) {
			CyniChat.printSevere("Our nickname was already in use...");
			e.printStackTrace();
		} catch (IrcException e) {
			CyniChat.printSevere("IRC has failed... call in the drones...");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Given the name of a channel... get the channel object 
	 *  associated with it
	 * 
	 * @param chanName : The channel name we're searching for
	 * @return the Channel that belongs to that name
	 */
	public Channel getChannel( String chanName ) {
		return bot.getChannel( chanName );
	}
	
	/**
	 * Kill the IRC component of the plugin ungracefully ... In other words,
	 * knock it over the head with a bat.
	 */
	public void stop() {
		
		//Tell the console that stuff is happening
		CyniChat.printInfo("Shutting down IRC...");
		
		//And kill the bot with no chance of reconnection
		bot.shutdown(true);
		
		//Then tell the people that the murder happened
		CyniChat.printInfo("IRC has been killed.");
		
	}
	
	/**
	 * As per interfacing...
	 * 
	 * @param type : The endpoint of the message
	 * @param player : The player that sent the message
	 * @param channel : The channel that the message was sent on
	 * @param message : The message contents themselves
	 */
	public void giveMessage(ChatRouter.EndpointType type, String player,
			String channel, String message) {
		
		//Is the endpoint a player?
		if ( type == ChatRouter.EndpointType.PLAYER ) {
			
			//If so, get the channel from the package
			String ircChannel = CyniChat.data.getChannel(channel).getIRC();
			
			//And send a message out over that channel
			sendMessage(ircChannel, player, message);
			
		}
		
	}
	
	/**
	 * Get the master channel name from the data.
	 * @return the ircMasterChannel
	 */
	public String getIrcMasterChannel() {
		return ircMasterChannel;
	}
	
	/**
	 * Get the password associated with the ircMasterChannel.
	 * @return the ircMasterPassword
	 */
	public String getIrcMasterPassword() {
		return ircMasterPassword;
	}
	
	/**
	 * Set the Master Channel to something else.
	 * @param ircMasterChannel : The ircMasterChannel to set
	 */
	public void setIrcMasterChannel( String ircMasterChannel ) {
		this.ircMasterChannel = ircMasterChannel;
	}
	
	/**
	 * Set the Master Channel's password to something else.
	 * @param ircMasterPassword : The ircMasterPassword to set
	 */
	public void setIrcMasterPassword( String ircMasterPassword ) {
		this.ircMasterPassword = ircMasterPassword;
	}
}