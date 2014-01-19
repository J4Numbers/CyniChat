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

/**
 * Change the state of the player to afk
 * 
 * @author CyniCode
 */
public class AfkCommand implements CommandExecutor {
	
	/**
	 * When we get the command, simply switch the player from state to state
	 * @param player
	 * @param command
	 * @param key
	 * @param objects
	 * @return
	 */
	public boolean onCommand(CommandSender player, Command command, String key, String[] objects) {
		
		//If the player is not a player...
		if ( !(player instanceof Player) )
			//Return false
			return false;
		
		//Otherwise, switch the status of their afk-ness
		CyniChat.data.getDetails( player.getName() ).changeAfk();
		
		//and return.
		return true;
		
	}
	
}