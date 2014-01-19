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
import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * Class for most of the commands
 * (Everything beginning with /ch)
 * TODO: Comment through all of these commands
 * 
 * @author CyniCode
 */
public class ChCommand implements CommandExecutor {
	
	/**
	 * An instance of the plugin, kept around for some
	 * reason or another
	 */
	public CyniChat plugin;
	
	/**
	 * Let's make a new ChCommand
	 * @param plugin with the plugin as an argument
	 */
	public ChCommand(CyniChat plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Wrap a string in necessary tags
	 * @param option : This is what we're wrapping
	 * @param sentenceColour : This is the colour of the rest of the 
	 *  sentence after this formatted part
	 * @return the new string
	 */
	public static String necessary( String option, ChatColor sentenceColour ) {
		return ChatColor.DARK_AQUA+"<"+option+">"+sentenceColour;
	}
	
	/**
	 * Wrap a string in optional tags
	 * @param option : This is what we're wrapping
	 * @param sentenceColour : This is the colour of the rest of the
	 *  sentence after this formatted part
	 * @return the new string
	 */
	public static String optional( String option, ChatColor sentenceColour ) {
		return ChatColor.GRAY+"["+option+"]"+sentenceColour;
	}
	
	/**
	 * Iterate through all potential commands to allow a player to execute commands
	 * @param player
	 * @param comm
	 * @param Label
	 * @param args
	 * @return 
	 */
	public boolean onCommand(CommandSender player, Command comm, String Label, String[] args) {
		
		//If the length of the array is zero...
		if ( args.length == 0 ) {
			
			//Then give the player a helping hand and return
			HelpCommand.info( player );
			return true;
			
		}
		
		//Now... if the first argument is help, then give them help
		if ( args[0].equalsIgnoreCase("help") ) {
			
			//And if they have defined an area of help, give
			// them the specifics
			if ( args.length == 2 ) {
				HelpCommand.Command( player, args[1] );
				return true;
			}
			
			//Otherwise, the general help should suffice
			HelpCommand.Command( player, "" );
			return true;
			
		}
		
		//If the player is wanting to join a channel...
		if ( args[0].equalsIgnoreCase("join") ) {
			
			//Let's ask if the player is actually a player first
			if ( !( player instanceof Player ) ) 
				//Because we'll have to refuse them if they're not
				return true;
			
			//Now... if they have too many or not enough arguments, 
			// give them the help command
			if ( args.length < 2 || args.length > 3 ) {
				
				JoinCommand.info( player );
				return true;
				
			} else {
				
				//Otherwise, if they've provided a password...
				if ( args.length == 3 ) {
					
					//Then use it in the join commmand
					JoinCommand.join( player, args[1], args[2] );
					return true;
					
				} else {
					
					//Otherwise, provide no password
					JoinCommand.join( player, args[1], "");
					return true;
					
				}
				
			}
			
		}
		
		//On the other hand... they might want to leave a channel.
		if ( args[0].equalsIgnoreCase("leave") ) {
			if ( !( player instanceof Player ) ) return true;
			if ( args.length < 2 ) {
				LeaveCommand.leave( player, CyniChat.data.getOnlineDetails( 
						(Player) player ).getCurrentChannel() );
				return true;
			} else {
				LeaveCommand.leave( player, args[1] );
				return true;
			}
		}
		
		//There could be an attempted ban.
		if ( args[0].equalsIgnoreCase("ban") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					BanCommand.ban( player, CyniChat.data.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				BanCommand.ban( player, CyniChat.data.getChannel( CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			BanCommand.banInfo( player );
			return true;
		}
		
		//Or an attempted setting of data
		if ( args[0].equalsIgnoreCase("set") ) {
			ModCommand.set( player, args );
			return true;
		}
		
		//Maybe even a potential unban
		if ( args[0].equalsIgnoreCase("unban") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					BanCommand.unban( player, CyniChat.data.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				BanCommand.unban( player, CyniChat.data.getChannel( CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			BanCommand.unbanInfo( player );
			return true;
		}
		
		//Or a muting
		if ( args[0].equalsIgnoreCase("mute") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					MuteCommand.mute( player, CyniChat.data.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				MuteCommand.mute( player, CyniChat.data.getChannel( CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			MuteCommand.muteInfo( player );
			return true;
		}
		
		//Or unmuting... I suppose
		if ( args[0].equalsIgnoreCase("unmute") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					MuteCommand.unmute( player, CyniChat.data.getChannel( args[2].toLowerCase() ), args[1] );
					return true;
				}
				MuteCommand.unmute( player, CyniChat.data.getChannel( CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() ), args[1] );
				return true;
			}
			MuteCommand.unmuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("ignore") ) {
			if ( !( player instanceof Player ) ) return true;
			if ( args.length == 2 ) {
				MuteCommand.ignore( player, args[1] );
				return true;
			} else {
				MuteCommand.ignoreInfo(player);
				return true;
			}
		}
		if ( args[0].equalsIgnoreCase("hear") ) {
			if ( !( player instanceof Player ) ) return true;
			if ( args.length == 2 ) {
				MuteCommand.hear( player, args[1] );
				return true;
			} else {
				MuteCommand.hearInfo(player);
				return true;
			}
		}
		if ( args[0].equalsIgnoreCase("gmute") ) {
			if ( args.length == 2 ) {
				MuteCommand.gmute( player, args[1] );
				return true;
			}
			MuteCommand.gmuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("gunmute") ) {
			if ( args.length == 2 ) {
				MuteCommand.gUnMute(player, args[1]);
				return true;
			}
			MuteCommand.gUnMuteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("create") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) || args.length == 4 ) {
				if ( args.length == 4 ) {
					if ( args[3].equalsIgnoreCase("true") ) {
						AdminCommand.create(player, args[1], args[2], true);
						return true;
					}
					AdminCommand.createInfo(player);
					return true;
				}
				if ( args.length == 3 ) {
					if ( args[2].equalsIgnoreCase("true") ) {
						AdminCommand.create(player, args[1], String.valueOf( args[1].toLowerCase().charAt(0) ), true );
						return true;
					}
					AdminCommand.create( player, args[1], args[2], false );
					return true;
				}
				AdminCommand.create( player, args[1], String.valueOf( args[1].toLowerCase().charAt(0) ), false );
				return true;
			}
			AdminCommand.createInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("remove") ) {
			if ( args.length == 2 ) {
				AdminCommand.remove( player, args[1] );
				return true;
			} else if ( args.length == 1 ) {
				AdminCommand.remove( player, CyniChat.data
						.getOnlineDetails( (Player) player ).getCurrentChannel() );
				return true;
			}
			AdminCommand.removeInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("reload") ) {
			GeneralCommand.reload( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("promote") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					ModCommand.promote( player, args[2].toLowerCase(), args[1] );
					return true;
				}
				ModCommand.promote( player, CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel(), args[1] );
				return true;
			}
			ModCommand.promoteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("demote") ) {
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				if ( args.length == 3 ) {
					ModCommand.demote( player, args[2].toLowerCase(), args[1] );
					return true;
				}
				ModCommand.demote( player, CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel(), args[1] );
				return true;
			}
			ModCommand.demoteInfo( player );
			return true;
		}
		if ( args[0].equalsIgnoreCase("list") ) {
			if ( args.length == 2 ) {
				GeneralCommand.list( player, Integer.parseInt( args[1] ) );
				return true;
			}
			GeneralCommand.list( player, 1 );
			return true;
		}
		if ( args[0].equalsIgnoreCase("who") ) {
			if ( args.length == 2 ) {
				GeneralCommand.who( player, args[1] );
				return true;
			}
			GeneralCommand.who( player, CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() );
			return true;
		}
		if ( args[0].equalsIgnoreCase("info") ){
			if ( args.length == 2 ) {
				GeneralCommand.info( player, args[1].toLowerCase() );
				return true;
			}
			GeneralCommand.info( player, CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel() );
			return true;
		}
		
		//It could be someone trying to kick someone else
		if ( args[0].equalsIgnoreCase("kick") ){
			
			//So let's make sure they have all the arguments needed
			// for said kicking
			if ( ( args.length == 2 ) || ( args.length == 3 ) ) {
				
				if ( args.length == 3 ) {
					
					//They apparently want to kick someone
					// in a specific channel
					BanCommand.kick( player, CyniChat.data.getChannel(
						args[2].toLowerCase() ),args[1] );
					return true;
					
				}
				
				//Or a kick in this channel right here
				BanCommand.kick( player, CyniChat.data.getChannel( CyniChat.data.getDetails( player.getName().toLowerCase() ).getCurrentChannel().toLowerCase() ), args[1] );
				return true;
				
			}
			
			//Otherwise, show them the info and scarper
			BanCommand.kickInfo( player );
			return true;
			
		}
		
		//Some people might want to save
		if ( args[0].equalsIgnoreCase("save") ){
			
			//So save things
			GeneralCommand.save( player );
			return true;
			
		}
		
		//They might want to join a channel in the short-hand way
		if ( args.length < 1 || args.length > 2 ) {
			JoinCommand.info( player );
			return true;
		} else {
			
			//So give them a password or not...
			if ( args.length == 2 ) {
				JoinCommand.join( player, args[0], args[1] );
				return true;
			} else {
				JoinCommand.join( player, args[0], "");
				return true;
			}
		}
	}
	
}
