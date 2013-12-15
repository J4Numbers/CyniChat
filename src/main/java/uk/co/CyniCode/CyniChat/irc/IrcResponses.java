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
	 * @param bot : The bot that originally got pinged
	 * @param user : The user that pinged the bot
	 * @return true when complete
	 */
	public static boolean helpOutput( PircBotX bot, User user ) {
		
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
		
		return true;
	}
	
	/**
	 * List all the players on the server or in the channel
	 * @param user : The user that originally asked
	 * @param bot : The bot that was pinged
	 * @param channel : The channel we're returning the information into
	 * @param all : A boolean of whether it's a single channel or the whole server
	 * @return true when complete
	 */
	public static boolean listOutput( User user, PircBotX bot, String channel, boolean all ) {
		
		CyniChat.printDebug( user.getNick() + " : " + channel + " : " + all );
		
		if ( all != true ) {
			
			Channel listChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
			
			Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();
			
			Set<String> keys = users.keySet();
			Iterator<String> iter = keys.iterator();
			
			String allPlayers = "Players online in "+listChan.getName()+" : ";
			
			while ( iter.hasNext() ) {
				String thisOne = iter.next();
				
				CyniChat.printDebug( thisOne + " is online... checking channel : "+listChan.getName() );
				
				UserDetails thisPlayer = users.get( thisOne );
				CyniChat.printDebug( thisPlayer.getAllVerboseChannels() );
				
				if ( thisPlayer.getAllChannels().contains( listChan.getName() ) ) {
					CyniChat.printDebug( "They're in the channel... adding to string" );
					allPlayers += thisOne + ", ";
				}
			}
			
			CyniChat.printDebug( allPlayers );
			
			bot.sendMessage( channel, allPlayers );
			
		} else {
			
			Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();
			Set<String> players = users.keySet();
			Iterator<String> everyone = players.iterator();
			String allPlayers = "Players online : ";
			
			while ( everyone.hasNext() ) {
				allPlayers += everyone.next() + ", ";
			}
			
			bot.sendMessage( channel, allPlayers );
			
		}
		
		return true;
	}

	/**
	 * Kick a player in the MC channel from inside IRC
	 * @param user : The user that kicked 'im
	 * @param bot : The bot that was pinged
	 * @param player : The player who got kicked
	 * @param channel : The channel the player will get kicked in
	 * @return true when complete or false if there was a problem
	 */
	public static boolean kickOutput( User user, PircBotX bot, String player, String channel ) {
		
		CyniChat.printDebug( "Kicking part 2..." );
		
		UserDetails kicking = CyniChat.data.getDetails( player );
		
		if ( kicking == null ) {
			bot.sendMessage( channel, "No-one found with the name of "+player+"..." );
			return false;
		}
		
		Channel kickChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
		
		if ( kickChan == null ) {
			CyniChat.printDebug( "This channel of "+channel+" doesn't exist... wtf?" );
			return false;
		}
		
		if ( kicking.getPlayer() != null ) {
			CyniChat.printDebug( "Kicking part three..." );
			if ( kicking.kick( user.getNick(), kickChan ) == true ) { 
				bot.sendMessage( channel, "The player has been kicked..." );
			} else {
				bot.sendMessage( channel, "The player was not in the channel..." );
			}
		} else {
			bot.sendMessage( channel, "This player is not online..." );
		}
		
		return true;
	}
	
	/**
	 * Un/Mute a player inside a channel from the safety of IRC
	 * @param user : The user who's doing the un/muting
	 * @param bot : The bot who heard about the un/mute
	 * @param player : The player who will be un/muted
	 * @param channel : The channel they're being un/muted inside
	 * @param undo : Whether we're undoing a previous mute or just making a new one
	 * @return true when complete or false if an error occurred
	 */
	public static boolean muteOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {
		
		UserDetails muting = CyniChat.data.getDetails( player );
		
		if ( muting == null ) {
			bot.sendMessage( channel, "There is no such player as "+player+"..." );
			return false;
		}
		
		Channel muteChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
		
		if ( muteChan == null )
			return false;
		
		if ( muting.getPlayer() != null )
			if ( undo == true ) {
				if ( muting.remMute( user.getNick(), muteChan ) == true ) {
					bot.sendMessage( channel, "The player has been unmuted in the channel..." );
				} else {
					bot.sendMessage( channel, "The player was not muted in the first place..." );
				}
			} else {
				if ( muting.addMute( user.getNick(), muteChan ) == true ) {
					bot.sendMessage( channel, "The player has been muted in this channel..." );
				} else {
					bot.sendMessage( channel, "The player was already muted in this channel..." );
				}
			}
		
		return true;
	}
	
	/**
	 * A method to un/ban a player in the MC channels from IRC
	 * @param user : The user that's doing the un/banning
	 * @param bot : The bot that heard about it
	 * @param player : The player that's being un/banned
	 * @param channel : The channel that's about to have one more un/banned player
	 * @param undo : Whether or not it's undoing a previous ban or just making a new one
	 * @return true when complete or false if an error occurs
	 */
	public static boolean banOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {
		
		UserDetails banning = CyniChat.data.getDetails( player );
		
		if ( banning == null ) {
			bot.sendMessage( channel, "There is no such person as "+player+"..." );
			return false;
		}
		
		Channel banChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );
		
		if ( banChan == null )
			return false;
		
		if ( banning.getPlayer() != null )
			if ( undo == true ) {
				if ( banning.remBan( user.getNick(), banChan ) == true ) {
					bot.sendMessage( channel, "The player has been unbanned from the channel..." );
				} else {
					bot.sendMessage( channel, "The player was already unbanned in the channel..." );
				}
			} else {
				if ( banning.newBan( user.getNick(), banChan ) == true ) {
					bot.sendMessage( channel, "The player has been banned in the channel..." );
				} else {
					bot.sendMessage( channel, "The player was aready banned in the channel..." );
				}
			}
		
		return true;
	}
	
	/**
	 * Direct a single message to this bot and relay it to a given channel
	 * in terms of the bot.
	 * @param bot : This is who is going to send the message
	 * @param chan : This is the channel we're going to send it over
	 * @param message : This is the message we're carrying
	 * @return true if the channel exists and we were successful...
	 * False if the channel didn't actually exist in the first place.
	 */
	public static boolean talkOutput( PircBotX bot, String chan, String message ) {
		
		CyniChat.printDebug( "Trying to TALK in: " + chan );
		
		if ( CyniChat.data.getChannel( chan ) != null ) {
			
			CyniChat.printDebug( chan + " exists" );
			
			Channel thisChan = CyniChat.data.getChannel( chan );
			
			Map<String, UserDetails> online = CyniChat.data.getOnlineUsers();
			
			Set<String> onSet = online.keySet();
			Iterator<String> onIter = onSet.iterator();
			
			while ( onIter.hasNext() ) {
				String cur = onIter.next();
				
				CyniChat.printDebug( cur + " is the next player in line" );
				
				UserDetails thisPlayer = online.get( cur );
				if ( thisPlayer.getAllChannels().contains( thisChan.getName() ) ) {
					try {
						CyniChat.printDebug( "Player found... trying to send message" );
						thisPlayer.getPlayer().sendMessage( thisChan.getColour()+"[IRC] ["+thisChan.getNick()+"] "+bot.getNick()+" : "+message );
					} catch ( NullPointerException e ) {
						CyniChat.printDebug( "Player not found... erroring" );
						e.printStackTrace();
					}
				}
			}
			
			CyniChat.printDebug( "Trying to send message to IRC..." );
			bot.sendMessage( thisChan.getIRC(), message );
			
			return true;
		}
		
		CyniChat.printDebug( "Channel doesn't exist." );
		return false;
	}
}
