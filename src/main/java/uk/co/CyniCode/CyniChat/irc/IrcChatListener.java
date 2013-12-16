package uk.co.CyniCode.CyniChat.irc;

//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.CyniChat;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * Deal with all the output that the bot can see
 * 
 * @author CyniCode
 */
public class IrcChatListener extends ListenerAdapter {
	
	/**
	 * Listen for all the chatter that is going on on the
	 * IRC bot's end so that any commands given there are
	 * going to be executed while commands given from in 
	 * MC will be left alone.
	 * 
	 * @param event : The event we're listening to
	 * @throws Exception when something goes wrong
	 */
	public void onMessage(MessageEvent event) throws Exception {
		
		//Well we've been given a message. Is it a CyniBot
		// command that we're hearing?
		if (event.getMessage().startsWith(":?")) {
			
			//Yes. Let's split it into pieces
			String[] argments = event.getMessage().split(" ");
			
			//And get what channel it is
			org.pircbotx.Channel thisChan = event.getChannel();
			
			//Now... it could just be a testing ask
			// for all the things the bot can do
			if (argments.length == 1) {
				
				//In which case, send them the help
				CyniChat.printDebug("Default used...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
				
			}
			
			//Or it could be a request for help, plain 
			// and simple
			if (argments[1].equalsIgnoreCase("help")) {
				
				//So we give it to them
				CyniChat.printDebug("Help selected...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
				
			}
			
			//They could be asking for a list of players
			// that are around...
			if (argments[1].equalsIgnoreCase("list")) {
				
				CyniChat.printDebug("Listing chosen...");
				
				//And it's either asking for how many people
				// are on the whole server
				if (argments.length > 2) {
					
					//Well, we're going to have been given /something/
					// as an additional argument
					if (argments[2].equalsIgnoreCase("all")) {
						
						//And then we print out all the people
						// that are on the server
						CyniChat.printDebug("You've either got 'all' as parameter...");
						CyniChat.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
						IrcResponses.listOutput(event.getUser(), event.getBot(),
								thisChan.getName(), true);
						return;
						
					}
				
				//Or it's asking for how many people are in this 
				// specific channel
				} else {
					
					//Or we get a list of all those online that are
					// inside this channel, then print that out.
					CyniChat.printDebug("Or you don't....");
					CyniChat.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
					IrcResponses.listOutput(event.getUser(), event.getBot(),
							thisChan.getName(), false);
					return;
					
				}
				
				return;
				
			}
			
			//Well, now we get into administrative commands. 
			// For instance, a person might need to be kicked
			if (argments[1].equalsIgnoreCase("kick")
					
					//So we ask if they're an op in the channel
					// that we're trying to kick someone in.
					&& thisChan.isOp(event.getUser())) {
				
				CyniChat.printDebug("Kicking...");
				
				//And ask if they are actually trying to kick
				// anyone.
				if (argments[2] != null) {
					
					//Then kick them.
					IrcResponses.kickOutput(event.getUser(), event.getBot(),
							argments[2], thisChan.getName());
					return;
					
				}
				
				//Else we tell them that they need to kick /someone/
				event.respond("I'm sorry, you must include a person to be kicked");
				return;
				
			}
			
			//It could be a request for a ban/unban?
			if ((argments[1].equalsIgnoreCase("ban")
					|| argments[1].equalsIgnoreCase("unban"))
					
					//And once again, we need to make sure that they have
					// some sort of authority to authorize such a move.
					&& thisChan.isOp(event.getUser())) {
				
				CyniChat.printDebug("Banning...");
				
				//Are they even providing someone to un/ban
				if (argments[2] != null) {
					
					//Then we define whether it's a ban...
					if (argments[1].equalsIgnoreCase("ban")) {
						
						//And if it's a ban, we try to ban the
						// player
						IrcResponses.banOutput(event.getUser(), event.getBot(),
								argments[2], event.getChannel().getName(), false);
						
					//Or an unban...
					} else {
						
						//And since it's an unban, we try to unban
						// this player
						IrcResponses.banOutput(event.getUser(), event.getBot(),
								argments[2], event.getChannel().getName(), true);
						
					}
					
					return;
					
				}
				
				//Or we just say that we need to have
				// a player that needs to be included
				event.respond("I'm sorry, you must include a person to be un/banned");
				return;
				
			}
			
			//Someone could be trying to perform an un/mute
			if ((argments[1].equalsIgnoreCase("mute")
					|| argments[1].equalsIgnoreCase("unmute"))
					
					//So, once again, we ask if they have any sort
					// of authority in this channel...
					&& thisChan.isOp(event.getUser())) {
				
				CyniChat.printDebug("Muting...");
				
				//And whether they have provided a subject
				if (argments[2] != null) {
					
					//Then we distinctify between a mute
					if (argments[1].equalsIgnoreCase("mute")) {
						
						//We then mute said player
						IrcResponses.muteOutput(event.getUser(), event.getBot(),
								argments[2], thisChan.getName(), false);
						
					//And an unmute
					} else {
						
						//Or unmute them...
						IrcResponses.muteOutput(event.getUser(), event.getBot(),
								argments[2], thisChan.getName(), true);
						
					}
					
					return;
					
				}
				
				//Or we tell them that we need a player to be
				// muted/somesuch
				event.respond("I'm sorry, you must include a person to be un/muted");
				return;
				
			}
			
			//It could be the cheating command for
			// talking an action through the bot
			if ( argments[1].equalsIgnoreCase("talk")
					
					//As per asking... the player needs to be an
					// op to get this talking message across
					&& thisChan.isOp( event.getUser() )
					
					//Along with being in the master channel when
					// they send this command.
					&& thisChan.getName().equalsIgnoreCase( CyniChat.PBot.getIrcMasterChannel() ) ) {
				
				CyniChat.printDebug("Talking with " + argments.length + " args... ");
				
				//They have to have at least 4 argments...
				// :? talk channel message
				if (argments.length > 3) {
					
					CyniChat.printDebug("Talking...");
					
					//Then we try to talk
					if ( !IrcResponses.talkOutput(
							event.getBot(), argments[2],
							stacker(argments, 3, argments.length)) )
						
						//And if that fails, say so
						event.respond("Invalid statement. Please make sure that channel exits in the MC server.");
					
				}
				
				return;
				
			}
			
			//Someone could be asking for a bot restart
			if (argments[1].equalsIgnoreCase("restart")
					
					//That someone must have authority...
					&& thisChan.isOp(event.getUser())
					
					//In the master channel
					&& thisChan.getName().equalsIgnoreCase( CyniChat.PBot.getIrcMasterChannel() ) ) {
				
				CyniChat.printDebug("Restarting...");
				CyniChat.printDebug( String.format(
						"%s is restarting CyniBot from channel %s",
						event.getUser().getNick(),
						thisChan.getName() ) );
				
				//Then we restart it.
				CyniChat.PBot.restart();
				return;
				
			}
			
			//Or, finally, they might be wanting to kill it
			// dead.
			if (argments[1].equalsIgnoreCase("kill")
					
					//Someone of this calibre must be an op
					&& thisChan.isOp(event.getUser())
					
					//In the admin channel
					&& thisChan.getName().equalsIgnoreCase( CyniChat.PBot.getIrcMasterChannel() ) ) {
				
				CyniChat.printDebug("Murdering...");
				CyniChat.printDebug( String.format(
						"%s is killing CyniBot from channel %s",
						event.getUser().getNick(),
						thisChan.getName() ) );
				
				//Alas, poor Yorick
				CyniChat.PBot.stop();
				return;
				
			}
			
			//Otherwise, it's garbage. Debug what the argument was
			// and kill the method
			CyniChat.printDebug("\"" + argments[1] + "\"");
			
			return;
			
		}
		
		//It's not a command, therefore it's a message that
		// we have to pass on to the server. Let's get all 
		// the details debugged into the console.
		CyniChat.printDebug("Sender: " + event.getUser().getNick());
		CyniChat.printDebug("Channel: " + event.getChannel().getName().toLowerCase());
		CyniChat.printDebug("Message: " + event.getMessage());
		
		//Let's first get the channel name that we're in...
		String ircChannelName = event.getChannel().getName().toLowerCase();
		
		//Then ask if we're linked to that channel
		if (CyniChat.data.getLinkedChans().containsKey(ircChannelName)) {
			
			//Player[] online = Bukkit.getServer().getOnlinePlayers();
			
			//Since we are... grab that channel
			String channelName = CyniChat.data.getLinkedChans().get(ircChannelName);
			
			//And debug the details
			CyniChat.printDebug("Match found: " + event.getChannel().getName() + " -> " + channelName);
			
			//Then send the message onto the main listener that's hanging
			// around somewhere or other.
			ChatRouter.routeMessage(ChatRouter.EndpointType.IRC,
					event.getUser().getNick(), channelName, event.getMessage());
			
		}
		
	}
	
	/**
	 * It could be a private message from someone or another.
	 * So let's greet them warmly and give them a fuckton of 
	 * commands to see and play with while they're at it.
	 * 
	 * @param event : This is the event that we're listening
	 *  to with rapt attention
	 */
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) {
		
		//Tell the nice console what's happening
		CyniChat.printDebug("Private message called!");
		
		//Then ask if someone is trying to do a command
		if (event.getMessage().startsWith(":?")) {
			
			//And get all the substrings from it
			String[] argments = event.getMessage().split(" ");
			
			//Tell the console that someone is doing
			// something in private
			CyniChat.printDebug(":? called by " + event.getUser().getNick());
			
			//And then show them all the help they
			// could ever need
			IrcResponses.helpOutput(event.getBot(), event.getUser());
			
		}
		
	}
	
	/**
	 * Finally, it could be a stacked sentence request from
	 * the talk command. Let's get an array of words with a
	 * start and an end position, then make the sentence.
	 * 
	 * @param args : This is the array of words we've been
	 *  given to stack into a sentence
	 * @param start : This is the starting word that is the
	 *  first word in the sentence
	 * @param end : This is the end position of the last
	 *  word in the sentence
	 * @return the final sentence, from start to end.
	 */
	public String stacker(String[] args, int start, int end) {
		
		//First of all, let's make a few strings to 
		// hold all the information that we're going
		// to generate from this.
		String finalString = "";
		String connector = "";
		
		//Then, from start to finish...
		for (int i = start; i < end; i++) {
			
			//Add the word to the final sentence
			finalString += connector + args[i];
			
			//And update the connector
			connector = " ";
			
		}
		
		//Then let's return the final stacked sentence
		return finalString;
		
	}
	
}