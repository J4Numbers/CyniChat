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
 * The class for privately messaging another player
 * 
 * @author CyniCode
 */
public class MsgCommand implements CommandExecutor {

	/**
	 * Shh... we're sending a message between 2 people
	 * No, you can't see what it is, it's private!
	 * @param player
	 * @param command
	 * @param key
	 * @param objects
	 * @return 
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		
		//Let's make sure the player is a player
		if ( player instanceof Player )
			//And whether they can send a message or not
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.basic.msg") )
				return false;
		
		//Now... have they provided a message to send?
		if ( objects.length >= 2 ) {
			try {
				//Yep? Good.
				//Print out who they're sending it to
				CyniChat.printDebug(objects[0]);
				
				//And then get them from the data
				UserDetails person = CyniChat.data.getOnlineUsers()
					.get( objects[0].toLowerCase() );
				
				UserDetails user = CyniChat.data.
						getOnlineDetails( (Player) player );
				
				if ( person == user ) {
					player.sendMessage( "You cannot send a message to yourself" );
					return false;
				}
				
				//Print out a bit of debug
				person.printAll();
				
				//Then construct the message we're going to send
				String Message = stacker( objects );
				
				//And debug that too
				CyniChat.printDebug( Message );
				
				//Send the message to both of them to show that
				// it worked
				player.sendMessage("To "+person.getPlayer().getName()+" :"+Message);
				person.getPlayer().sendMessage( "From "+player.getName()+" :"+Message );
				
				//Change the latest of that player to this player
				person.changeLatest( user.getName() );
				
				//And show that it worked
				CyniChat.printDebug( person.getName() + " will reply to " + person.getLatest() );
				
				//Now... if we're a player...
				if ( player instanceof Player ) {
					
					//Then set our own latest to them
					user.changeLatest( person.getName() );
					
					//And show it worked
					CyniChat.printDebug( user.getName() + " will reply to " + user.getLatest() );
					
				}
				
				//Then flee
				return true;
				
			} catch ( NullPointerException e ) {
				
				//Or maybe the person does not exist
				player.sendMessage("This player is not online");
				e.printStackTrace();
				return true;
				
			}
			
		}
		
		//Or they haven't provided a message to send to someone
		player.sendMessage("Please enter a message to send");
		return true;
		
	}
	
	/**
	 * Yada, yada, string array to sentence, yada, yada
	 * @param stacking : The string array
	 * @return the sentence
	 */
	public String stacker( String[] stacking ) {
		try {
			
			//Create a new string
			String finalStack = "";
			
			//And for each word in the array, add it to the
			// string we'll return
			for ( int i = 1; i < stacking.length; i++ ) 
				finalStack += " "+stacking[i];
			
			//Right now...
			return finalStack;
			
		} catch ( NullPointerException e ) {
			
			//Don't you just love being given empty arrays
			return "";
			
		}
		
	}
	
}