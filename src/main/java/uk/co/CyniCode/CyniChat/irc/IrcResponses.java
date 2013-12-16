package uk.co.CyniCode.CyniChat.irc;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;

/**
 * A class for all the responses that are going to be given by the IRC bot
 * 
 * @author CyniCode
 */
public class IrcResponses {
	
	/**
	 * Show all the help text
	 * 
	 * @param bot : The bot that originally got pinged
	 * @param user : The user that pinged the bot
	 */
	public static void helpOutput( PircBotX bot, User user ) {
		
		bot.setMessageDelay(10);
		bot.sendMessage( user, "You wanted some help?" );
		bot.sendMessage( user, "This is the CyniChat bot (c) CyniCode 2013" );
		bot.sendMessage( user, "All commands start with ':?'" );
		bot.sendMessage( user, "':?'                    -> Show this help screen" );
		bot.sendMessage( user, "':? help'               -> Synonymous with ':?'" );
		bot.sendMessage( user, "':? list'               -> List all the players in this channel" );
		bot.sendMessage( user, "':? list all'           -> List all the players that are online" );
		bot.sendMessage( user, "':? kick   <MCUser>'    -> Kick this player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? mute   <MCUser>'    -> Mute the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? unmute <MCUser>'    -> Unmute the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? ban    <MCUser>'    -> Ban the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? unban  <MCUser>'    -> Unban the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? restart'            -> Restart the IRC bot (You must be an OP!)" );
		bot.sendMessage( user, "':? kill'               -> Kill the IRC server (You must be an OP (and have a bloody good reason)!)" );
		bot.sendMessage( user, " " );
		bot.sendMessage( user, "If this didn't help you, then you're kinda screwed :P" );
		
	}
	
	/**
	 * List all the players on the server or in the channel
	 * 
	 * @param user : The user that originally asked
	 * @param bot : The bot that was pinged
	 * @param channel : The channel we're returning the information into
	 * @param all : A boolean of whether it's a single channel or the whole server
	 */
	public static void listOutput( User user, PircBotX bot, String channel, boolean all ) {
		
		//Let's debug all the parameters
		CyniChat.printDebug( user.getNick() + " : " + channel + " : " + all );
		
		//And if it's just this channel we're checking...
		if ( !all ) {
			
			//Let's get the channel that we're listing out
			Channel listChan = CyniChat.data.getChannel( 
					CyniChat.data.getLinkedChans().get( channel ) );
			
			//And all the users online to be in said channel
			Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();
			
			//Make a nice template for the bot's output
			String allPlayers = "Players online in "+listChan.getName()+" : ";
			
			//Then for everyone who is online...
			for ( Map.Entry< String, UserDetails > thisOne 
					: users.entrySet() ) {
				
				//Tell the console that they are online
				CyniChat.printDebug( thisOne.getKey() 
						+ " is online... checking channel : "
						+ listChan.getName() );
				
				//And what channels they are in
				CyniChat.printDebug( thisOne.getValue().getAllVerboseChannels() );
				
				//Then, if they are in this channel...
				if ( thisOne.getValue().getAllChannels().contains( listChan.getName() ) ) {
					
					//Debug that fact
					CyniChat.printDebug( "They're in the channel... adding to string" );
					
					//And add them to the final message
					allPlayers += thisOne.getValue() + ", ";
					
				}
				
			}
			
			//Once that has been done, debug what we've gathered
			// out to the console
			CyniChat.printDebug( allPlayers );
			
			//Then sent it too
			bot.sendMessage( channel, allPlayers );
			
		} else {
			
			//Otherwise, it's all the players that are on the
			// server full stop.
			Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();
			
			//Get the set of their names
			Set<String> players = users.keySet();
			
			//And, once again, make a template of the output
			String allPlayers = "Players online : ";
			
			//Then, for everyone who is online,
			for ( String thisOne : players )
				
				//Add them to that template's output
				allPlayers += thisOne + ", ";
			
			//Debug out that message
			CyniChat.printDebug( allPlayers );
			
			//Then actually send that message
			bot.sendMessage( channel, allPlayers );
			
		}
		
	}
	
	/**
	 * Kick a player in the MC channel from inside IRC
	 * 
	 * @param user : The user that kicked 'im
	 * @param bot : The bot that was pinged
	 * @param player : The player who got kicked
	 * @param channel : The channel the player will get kicked in
	 * @return true when complete or false if there was a problem
	 */
	public static boolean kickOutput( User user, PircBotX bot, String player, String channel ) {
		
		//Tell them that we've reached the method
		CyniChat.printDebug( "Kicking part 2..." );
		
		//And get the details of the person we're kicking
		UserDetails kicking = CyniChat.data.getDetails( player );
		
		//If they're even there that is...
		if ( kicking == null ) {
			
			//Since they're not, tell the person that no-one with
			// that name was online.
			bot.sendMessage( channel, "No-one found with the name of "+player+"..." );
			return false;
			
		}
		
		//Then get the channel that we're going to try and kick them from
		Channel kickChan = CyniChat.data.getChannel( 
				CyniChat.data.getLinkedChans().get( channel ) );
		
		//If that doesn't exist...
		if ( kickChan == null ) {
			
			//Then something is kinda fecked
			CyniChat.printDebug( "This channel of "+channel+" doesn't exist... wtf?" );
			return false;
			
		}
		
		//Is the player online?
		if ( kicking.getPlayer() != null ) {
			
			//Apparently so.
			CyniChat.printDebug( "Kicking part three..." );
			
			//Actually kick them from the channel
			if ( kicking.kick( user.getNick(), kickChan ) ) { 
				
				//Then celebrate by saying that they've been kicked
				bot.sendMessage( channel, "The player has been kicked..." );
				
			} else {
				
				//The player wasn't in the channel... crafty bugger
				bot.sendMessage( channel, "The player was not in the channel..." );
				
			}
			
		} else {
			
			//The player wasn't even online
			bot.sendMessage( channel, "This player is not online..." );
			
		}
		
		return true;
		
	}
	
	/**
	 * Un/Mute a player inside a channel from the safety of IRC
	 * 
	 * @param user : The user who's doing the un/muting
	 * @param bot : The bot who heard about the un/mute
	 * @param player : The player who will be un/muted
	 * @param channel : The channel they're being un/muted inside
	 * @param undo : Whether we're undoing a previous mute or just making a new one
	 * @return true when complete or false if an error occurred
	 */
	public static boolean muteOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {
		
		//Get the person that we're meant to be un/muting
		UserDetails muting = CyniChat.data.getDetails( player );
		
		//Then ask if they even exist
		if ( muting == null ) {
			
			//If they don't, tell the bot that they don't
			bot.sendMessage( channel, "There is no such player as "+player+"..." );
			return false;
			
		}
		
		//Then get the channel that they are being freed from
		Channel muteChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
		
		//If the channel doesn't exist...
		if ( muteChan == null )
			
			//Feck.
			return false;
		
		//Okay, then we ask if the player was online
		if ( muting.getPlayer() != null )
			
			//And if we're muting or unmuting
			if ( undo ) {
				
				//Since we're unmuting, try to remove the mute
				if ( muting.remMute( user.getNick(), muteChan ) ) {
					
					//And be successful
					bot.sendMessage( channel, "The player has been unmuted in the channel..." );
					
				} else {
					
					//Or not so
					bot.sendMessage( channel, "The player was not muted in the first place..." );
					
				}
				
			//Otherwise, they're being muted.
			} else {
				
				//And try to do so,
				if ( muting.addMute( user.getNick(), muteChan ) ) {
					
					//Succeed, and celebrate.
					bot.sendMessage( channel, "The player has been muted in this channel..." );
					
				} else {
					
					//Or fail miserably and cry.
					bot.sendMessage( channel, "The player was already muted in this channel..." );
					
				}
				
			}
		
		return true;
		
	}
	
	/**
	 * A method to un/ban a player in the MC channels from IRC
	 * 
	 * @param user : The user that's doing the un/banning
	 * @param bot : The bot that heard about it
	 * @param player : The player that's being un/banned
	 * @param channel : The channel that's about to have one more un/banned player
	 * @param undo : Whether or not it's undoing a previous ban or just making a new one
	 * @return true when complete or false if an error occurs
	 */
	public static boolean banOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {
		
		//Get the person that this is happening to
		UserDetails banning = CyniChat.data.getDetails( player );
		
		//And if they even exist
		if ( banning == null ) {
			
			//Since they don't say so
			bot.sendMessage( channel, "There is no such person as "+player+"..." );
			return false;
			
		}
		
		//Then get the channel we're banning in
		Channel banChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
		
		//And ask if that is there
		if ( banChan == null )
			//If not... cry
			return false;
		
		//Then we ask if they are online
		if ( banning.getPlayer() != null )
			
			//And if we're banning or unbanning
			if ( undo ) {
				
				//Since we're unbanning, remove the ban
				// in this channel
				if ( banning.remBan( user.getNick(), banChan ) ) {
					
					//And let the player rejoice
					bot.sendMessage( channel, "The player has been unbanned from the channel..." );
					
				} else {
					
					//Or fail miserably and cry
					bot.sendMessage( channel, "The player was already unbanned in the channel..." );
					
				}
				
			} else {
				
				//Well... sorry mate, you're being banned
				if ( banning.newBan( user.getNick(), banChan ) ) {
					
					//Yeah... sorry.
					bot.sendMessage( channel, "The player has been banned in the channel..." );
					
				} else {
					
					//Or not... apparently you get to speak another day
					bot.sendMessage( channel, "The player was aready banned in the channel..." );
					
				}
				
			}
		
		return true;
		
	}
	
	/**
	 * Direct a single message to this bot and relay it to a given channel
	 * in terms of the bot.
	 * 
	 * @param bot : This is who is going to send the message
	 * @param chan : This is the channel we're going to send it over
	 * @param message : This is the message we're carrying
	 * @return true if the channel exists and we were successful...
	 *  false if the channel didn't actually exist in the first place.
	 */
	public static boolean talkOutput( PircBotX bot, String chan, String message ) {
		
		//Tell the console what's happening
		CyniChat.printDebug( "Trying to TALK in: " + chan );
		
		//Then ask if the channel exists
		if ( CyniChat.data.getChannel( chan ) != null ) {
			
			//Since it does...
			CyniChat.printDebug( chan + " exists" );
			
			//Get this channel
			Channel thisChan = CyniChat.data.getChannel( chan );
			
			//And all those online
			Map<String, UserDetails> online = CyniChat.data.getOnlineUsers();
			
			//Then, for all those people...
			for ( Map.Entry<String,UserDetails> thisPlayer
					: online.entrySet() ) {
				
				//Get each of them
				CyniChat.printDebug( thisPlayer.getKey() + " is the next player in line" );
				
				//And ask if they are in this channel
				if ( thisPlayer.getValue().getAllChannels().contains( thisChan.getName() ) ) {
					
					try {
						
						//If they are...
						CyniChat.printDebug( "Player found... trying to send message" );
						
						//Send the message to them
						thisPlayer.getValue().getPlayer().sendMessage( 
								thisChan.getColour()+"[IRC] ["
										+thisChan.getNick()+"] "
										+bot.getNick()+" : "+message );
						
					} catch ( NullPointerException e ) {
						
						//Or fail miserably and cry...
						CyniChat.printDebug( "Player not found... erroring" );
						e.printStackTrace();
						
					}
					
				}
				
			}
			
			//Once we've done that, send the message into IRC to echo
			// things
			CyniChat.printDebug( "Trying to send message to IRC..." );
			bot.sendMessage( thisChan.getIRC(), message );
			
			return true;
			
		}
		
		//Or the channel simply doesn't exist...
		CyniChat.printDebug( "Channel doesn't exist." );
		return false;
		
	}
	
}