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

package uk.co.CyniCode.CyniChat.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.CyniCode.CyniChat.Chatting.ServerChatListener;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Class for most of the basic commands and some not-so-basic commands
 * 
 * @author CyniCode
 */
public class GeneralCommand {
	
	/**
	 * Save all the current details in circulation
	 * @param player : The player executing the save
	 * @return true when complete
	 */
	public static boolean save( CommandSender player ) {
		
		//Now... is the player a player?
		if ( player instanceof Player )
			//And do they have the permissions to save?
			if ( CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.save") )
				//Nope... kill em
				return false;
		
		//They do... so save it all!
		CyniChat.data.saveChannels();
		CyniChat.data.saveUsers();
		
		//Flush the data since we've saved things
		CyniChat.data.flushData();
		
		//And return
		return true;
		
	}
	
	/**
	 * Reload the plugin completely
	 * @param player : The player trying to reload the plugin
	 * @return true when complete
	 */
	public static boolean reload( CommandSender player ) {
		
		//Is the player really a player again...
		if ( player instanceof Player )
			//And do they have permissions to do a reload?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.reload") )
				//Phew... just as well we checked.
				return false;
		
		CyniChat.printDebug( "Performing a basic reload..." );
		
		//Reload it all!
		CyniChat.reload();
		
		CyniChat.printDebug( "Basic reload completed... moving onto the players." );
		
		CyniChat.data.reloadPlayers();
		
		CyniChat.printDebug( "Player reload completed" );
		
		return true;
		
	}
	
	/**
	 * Return the information about a channel
	 * @param player : The player asking
	 * @param channel : The channel they're asking about
	 * @return true when complete
	 */
	public static boolean info( CommandSender player, String channel ) {
		
		//Is the player actually a player?
		if ( player instanceof Player ) {
			
			if ( CyniChat.data.getOnlineDetails( (Player) player )
					.getCurrentChannel().equals("") ) {
				
				player.sendMessage( "You are not in a channel." );
				return false;
				
			}
			
			if ( !CyniChat.perms.checkPerm( (Player) player,
					"cynichat.basic.info" ) )
				
				return false;
				
			
			//And do they have permission to ask about the channel?
			if ( CyniChat.data.getChannel(channel).isProtected() && 
					!CyniChat.perms.checkPerm(
							(Player) player, "cynichat.basic.info."+channel) )
				
				//Yeeah... no
				return false;
		
		}
		
		//Let's make sure such a channel exists
		if ( CyniChat.data.getChannel(channel) == null ) {
			player.sendMessage("There is no such channel");
			return true;
		}
		
		//Then print out the information to the player
		Channel chan = CyniChat.data.getChannel(channel);
		player.sendMessage( "Name: "+chan.getColour()+chan.getName() );
		player.sendMessage( "Nick: "+chan.getColour()+"["+chan.getNick()+"]" );
		player.sendMessage( "Description: "+chan.getDesc() );
		
		return true;
		
	}
	
	/**
	 * Return a complete list of all channels
	 * @param player : The player asking
	 * @param page   : The page they're currently asking about --Currently Unused--
	 * @return true when complete
	 */
	public static boolean list( CommandSender player, int page ) {
		
		//Is the player a Player?
		if ( player instanceof Player )
			//And do they have permission to be snooping?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.list" ) )
				//lolno
				return false;
		
		//Initialise a list and a position for later
		List<Channel> allChans = new ArrayList<Channel>();
		int pos = 0;
		
		//Now... for all the channels on the server...
		for ( Map.Entry<String, Channel> entrySet : CyniChat.data.getChannels().entrySet() ) {
			
			//Add the channel to a list of channels
			allChans.add( pos, entrySet.getValue() );
			//And increment the positional index
			pos++;
			
		}
		
		//Now... up to the size of the list, get two items at a time
		for ( int i = 0; i < allChans.size(); i = i + 2 )
			
			if ( i == allChans.size() - 1 ) {
				
				player.sendMessage( String.format( "%s",
					allChans.get(i).getColour()+"["+allChans.get(i).getNick()+"] "
						+allChans.get(i).getName() ) );
				
			} else {
				//And print them, side-by-side in a message
				player.sendMessage( String.format( "%s\t\t%s", 
					allChans.get(i).getColour()+"["+allChans.get(i).getNick()+"] "
						+allChans.get(i).getName(),
					allChans.get(i+1).getColour()+"["+allChans.get(i+1).getNick()+"] "
						+allChans.get(i+1).getName()) );
			}
		
		//Then return
		return true;
		
	}
	
	/**
	 * Get information about all those inside a channel
	 * @param player : The player who's asking
	 * @param channel : The channel they're asking about
	 * @return true when complete
	 */
	public static boolean who( CommandSender player, String channel ) {
		
		//Is there a channel by this name?
		if ( CyniChat.data.getChannel(channel) == null ) {
			player.sendMessage( "There is no such channel" );
			return true;
		}
		
		Channel thisChan = CyniChat.data.getChannel( channel );
		
		//Let's just make sure they are a player first
		if ( player instanceof Player )
			//Then if they have the permission to do this
			if ( thisChan.isProtected() && 
					!CyniChat.perms.checkPerm( (Player) player,
							"cynichat.basic.who."+channel) )
				//And if not, kill em
				return false;
			else if ( !CyniChat.perms.checkPerm( (Player) player,
					"cyniChat.basic.who" ) )
				//And if not, kill em
				return false;
		
		//Initiate a string by this name
		String players = "";
		
		//And go through all those online...
		for ( Map.Entry<String, UserDetails> entrySet : CyniChat.data.getOnlineUsers().entrySet() ) {
			
			//Assign the values
			UserDetails current = entrySet.getValue();
			
			//And ask if they're in the channel we're enquiring about
			if ( current.getAllChannels().contains(channel) )
				
				//If they are... add them to the list.
				players = players + CyniChat.perms.getPlayerFull( current.getPlayer() )+" ";
			
		}
		
		//Then sent the player the list of who is in the channel
		player.sendMessage(players);
		
		//Before we return
		return true;
		
	}
	
	/**
	 * Send a quick one-line-message to another channel
	 * @param player : The player who's sending
	 * @param channel : The channel that's receiving
	 * @param message : The message that's being passed along
	 * @return true when complete
	 */
	public static boolean quickMessage( CommandSender player, String channel, String message ) {
		
		//Is the player a player?
		if ( player instanceof Player ) {
			
			//And do they have the basic permission to transmit
			// a general quick-message?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.qm" ) ) {
				player.sendMessage( "You do not have the necessary permissions for this." );
				return false;
			}
			
			//TODO: Needs more checking to see if the person
			// is allowed to speak in this channel
			
			//Now... let's get more specific.
			//Are they muted in this channel?
			if ( CyniChat.data.getOnlineDetails( (Player) player )
					.getMutedChannels().contains( CyniChat.data.getChannel(channel).getName() ) ) {
				player.sendMessage( "You are muted in this channel... SHAAAAAME" );
				return false;
			}
			
		}
		
		//Tell the console what's happening
		CyniChat.printDebug( "New Quick Message..." );
		
		//And check if the channel exists
		if ( CyniChat.data.getChannel(channel) != null ) {
			
			//Then put the information we need into local vars
			UserDetails sender = CyniChat.data.getOnlineDetails( (Player) player );
			Channel curChan = CyniChat.data.getChannel(channel);
			
			//And ask if the sender is already in this channel
			if ( sender.getAllChannels().contains( curChan.getName() ) ) {
				
				//Make an event with all the details inside
				ChannelChatEvent newChat = new ChannelChatEvent(
						sender.getName(),
						curChan,
						message,
						ServerChatListener
							.getRecipients( curChan.getName(), sender.getName() ),
						" :"
					);
				
				//And call it so we don't have to do anything
				Bukkit.getServer().getPluginManager().callEvent( newChat );
				
				//Finally, return after this
				return true;
				
			}
			
			//Huh, you're not in the channel
			player.sendMessage("You are not in this channel");
			return true;
			
		}
		
		//And the channel doesn't exist
		player.sendMessage("There is no such channel");
		return true;
		
	}
	
	/**
	 * Get the information about the quick message
	 * @param player : The player we're giving the info to
	 */
	public static void qmInfo( CommandSender player ) {
		
		//Tell the player that they're wrong
		player.sendMessage( ChatColor.RED+"Invalid Syntax!" );
		
		//Tell the player the correct syntax then
		player.sendMessage( ChatColor.RED + "/qm "+ChCommand.necessary( "channel", ChatColor.RED )
			+" "+ChCommand.necessary( "message", ChatColor.RED ) );
		
	}
}
