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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * Class for making another player be quiet... damn,
 * That's a lot of things to shut them up.
 * 
 * @author CyniCode
 */
public class MuteCommand {
	
	/**
	 * Ignore a player
	 * @param player : The player who wants to ignore someone
	 * @param ignorer : The annoying person
	 */
	public static void ignore( CommandSender player, String ignorer) {
		
		//Ask if the person is even here
		if ( CyniChat.data.getDetails(ignorer) == null ) {
			player.sendMessage("This player does not exist");
			return;
		}
		
		//Then tell the console what is going on
		CyniChat.printDebug( player.getName() + " is now attempting to ignore " + ignorer );
		
		//And try to ignore them
		CyniChat.data.getDetails( player.getName().toLowerCase() ).addIgnore( ignorer );
		
	}
	
	/**
	 * Un-ignore (hear) a player
	 * @param player : The player who has a conscience
	 * @param unignorer : The player who wasn't as annoying as thought
	 */
	public static void hear( CommandSender player, String unignorer) {
		
		//Ask if the player exists
		if ( CyniChat.data.getDetails(unignorer) == null ) {
			player.sendMessage("This player does not exist");
			return;
		}
		
		//And if they do... unignore them
		CyniChat.data.getDetails( player.getName().toLowerCase() ).remIgnore( unignorer );
		
	}
	
	/**
	 * Return information about the ignore command
	 * @param player : The person who wants to know
	 */
	public static void ignoreInfo(CommandSender player) {
		
		//The player has a problem
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Tell the player how to solve it
		player.sendMessage( ChatColor.RED + "/ch ignore "
			+ChCommand.necessary("player", ChatColor.RED ));
		
	}
	
	/**
	 * Return information about the hear command
	 * @param player : the person who wants to know
	 */
	public static void hearInfo( CommandSender player ) {
		
		//The player failed
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Make them succeed
		player.sendMessage( ChatColor.RED + "/ch hear "
			+ChCommand.necessary("player", ChatColor.RED));
		
	}
	
	/**
	 * Mute a player in one channel
	 * @param player : The person doing the muting
	 * @param channel : The channel that the mutee is muted in
	 * @param mutee : The person who shalt not speak
	 * @return true when complete
	 */
	public static boolean mute( CommandSender player, Channel channel, String mutee ) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//And can they mute people in this channel?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		//Does the player exist?
		if ( CyniChat.data.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		
		//And can we mute them?
		if (CyniChat.data.getDetails(mutee.toLowerCase()).addMute(player.getName(), channel) ) {
			
			//Yep... we just did so
			player.sendMessage( mutee + " has been muted");
			
		} else {
			
			//Nope... they're already muted
			player.sendMessage( mutee + " was already muted");
			
		}
		
		return true;
		
	}
	
	/**
	 * Completely mute a player across all channels
	 * @param player : The annoyed person
	 * @param mutee : The unlucky sod
	 * @return true when complete
	 */
	public static boolean gmute( CommandSender player, String mutee ) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//And can they completely silence someone?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.silence" ) )
				return false;
		
		//Does the person exist?
		if ( CyniChat.data.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		
		//Silence them.
		CyniChat.data.getDetails(mutee.toLowerCase()).silence( player );
		
		return true;
		
	}

	/**
	 * Give a person information about global mutation
	 * @param player : Wait... mutation?
	 */
	public static void gmuteInfo( CommandSender player ) {
		
		//The player dun goofed
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Perform ungoof
		player.sendMessage(ChatColor.RED+"/ch gmute "
			+ChCommand.necessary("player", ChatColor.RED) );
		
	}
	
	/**
	 * Show a player information about muting
	 * @param player : The person wanting to know
	 */
	public static void muteInfo( CommandSender player) {
		
		//Incorrect
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Correct
		player.sendMessage(ChatColor.RED+"/ch mute "
			+ChCommand.necessary("player", ChatColor.RED)+" "
			+ChCommand.optional("channel", ChatColor.RED) );
		
	}
	
	/**
	 * Globally unmute someone
	 * @param player : The person who has a kind heart
	 * @param mutee : The guy with a second chance
	 * @return true when complete
	 */
	public static boolean gUnMute( CommandSender player, String mutee) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//Can they silence someone?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.admin.silence") )
				return false;
		
		//Does the player exist?
		if ( CyniChat.data.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		
		//Make them speak again!
		CyniChat.data.getDetails(mutee.toLowerCase()).listen(player);
		
		return true;
		
	}
	
	/**
	 * Show information about global unmuting
	 * @param player : The person who wants to know
	 */
	public static void gUnMuteInfo( CommandSender player ) {
		
		//Player was incorrect
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Make that a negative negative
		player.sendMessage( ChatColor.RED+"/ch gunmute "
			+ChCommand.necessary("player", ChatColor.RED) );
		
	}
	
	/**
	 * Unmute a player in one channel
	 * @param player : The player doing the unmuting
	 * @param channel : The channel that shall be graced with another voice
	 * @param mutee : The person who learnt his lesson (hopefully)
	 * @return true when complete
	 */
	public static boolean unmute( CommandSender player, Channel channel, String mutee ) {
		
		//Ask if the player is a player
		if ( player instanceof Player )
			//And if they can perform an unmute for this channel
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.mute."+channel) )
				return false;
		
		//Does the player even exist that we're unmuting?
		if ( CyniChat.data.getDetails( mutee.toLowerCase() ) == null ) {
			player.sendMessage("This player does not exist");
			return true;
		}
		
		//And are they already unmuted or not?
		if ( CyniChat.data.getDetails( mutee.toLowerCase() ).remMute(player.getName(), channel) ) {
			
			//Apparently they're now unmuted
			player.sendMessage( mutee + " has been unmuted" );
			
		} else {
			
			//Or they already were...
			player.sendMessage( mutee + " was already unmuted" );
			
		}
		
		return true;
		
	}
	
	/**
	 * Show information about the unmute command
	 * @param player : The person who wants to know
	 */
	public static void unmuteInfo( CommandSender player ) {
		
		//The player has it wrong
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//Show them how to make it right
		player.sendMessage(ChatColor.RED+"/ch unmute "
			+ChCommand.necessary("player", ChatColor.RED)+" "
			+ChCommand.optional("channel", ChatColor.RED) );
	}
	
}