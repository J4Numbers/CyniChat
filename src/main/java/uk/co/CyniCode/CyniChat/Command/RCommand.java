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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Class dedicated to the reply command
 * 
 * @author CyniCode
 */
public class RCommand implements CommandExecutor {
	
	/**
	 * Reply to any previous messages that were given
	 * @param player
	 * @param command
	 * @param key
	 * @param objects
	 * @return 
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		
		//Is the player a player?
		if ( player instanceof Player ) {
			//Yep... can they send replies?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.msg" ) )
				return false;
		} else {
			//You should know that the console cannot send a reply
			player.sendMessage("Console cannot reply to people");
			return true;
		}
		
		//Now... if there is a message...
		if ( objects.length >= 1 ) {
			
			//Get the person who will send it
			UserDetails current = CyniChat.data.getOnlineDetails( (Player) player );
			
			//And ask if they can reply to anyone
			if ( current.getLatest() != null ) {
				
				//Since they can... make the message
				String message = stacker( objects );
				
				try {
					
					//And get the details about that player
					UserDetails other = CyniChat.data.getOnlineUsers().get( current.getLatest() );
					
					//Then send ourselves a message to say that it worked
					player.sendMessage("To "+other.getPlayer().getName()+" :"+message);
					
					//And try to send them a message...
					other.getPlayer().sendMessage("From "+player.getName()+" :"+message);
					
					//Then change their latest... just in case
					other.changeLatest( player.getName().toLowerCase() );
					
					return true;
					
				} catch ( NullPointerException e ) {
					
					//Or... the user might not even be there...
					player.sendMessage("This player is no-longer online");
					return true;
					
				}
				
			}
			
			//Tell the player that
			player.sendMessage("You have no-one to reply to");
			return true;
			
		}
		
		//Tell the player this too
		player.sendMessage("Please send a message");
		return true;
		
	}
	
	/**
	 * Yet ANOTHER sentence created
	 * @param objects : The thing we're stacking
	 * @return the sentence we're returning
	 */
	private String stacker(String[] objects) {
		try {
			
			//Create a string to stack together
			String finalStacked = "";
			
			//And create the stack
			for (String object : objects)
				finalStacked += " " + object;
			
			//Before returning it
			return finalStacked;
			
		} catch ( NullPointerException e ) {
			
			//And hoping we have actually been given /something/
			// to stack
			return "";
			
		}
		
	}
	
}