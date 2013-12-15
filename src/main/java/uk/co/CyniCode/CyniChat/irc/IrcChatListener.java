package uk.co.CyniCode.CyniChat.irc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
	 * Listen for all the chatter that is going on on the IRC bot's end so that
	 * any commands given there are going to be executed while commands given
	 * from inside MC will be left alone.
	 */
	public void onMessage(MessageEvent event) throws Exception {
		if (event.getMessage().startsWith(":?")) {
			String[] argments = event.getMessage().split(" ");
			org.pircbotx.Channel thisChan = event.getChannel();
			
			if (argments.length == 1) {
				CyniChat.printDebug("Default used...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
			}
			
			if (argments[1].equalsIgnoreCase("help")) {
				CyniChat.printDebug("Help selected...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
			}
			
			if (argments[1].equalsIgnoreCase("list")) {
				CyniChat.printDebug("Listing chosen...");
				if (argments.length > 2) {
					if (argments[2].equalsIgnoreCase("all")) {
						CyniChat.printDebug("You've either got 'all' as parameter...");
						CyniChat.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
						IrcResponses.listOutput(event.getUser(), event.getBot(), thisChan.getName(), true);
						return;
					}
				} else {
					CyniChat.printDebug("Or you don't....");
					CyniChat.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
					IrcResponses.listOutput(event.getUser(), event.getBot(), thisChan.getName(), false);
					return;
				}
				return;
			}
			
			if (argments[1].equalsIgnoreCase("kick")
					&& thisChan.isOp(event.getUser())) {
				CyniChat.printDebug("Kicking...");
				if (argments[2] != null) {
					IrcResponses.kickOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName());
					return;
				}
				event.respond("I'm sorry, you must include a person to be kicked");
				return;
			}
			
			if ((argments[1].equalsIgnoreCase("ban")
					|| argments[1].equalsIgnoreCase("unban"))
					&& thisChan.isOp(event.getUser())) {
				CyniChat.printDebug("Banning...");
				if (argments[2] != null) {
					
					if (argments[1].equalsIgnoreCase("ban")) {
						IrcResponses.banOutput(event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), false);
					} else {
						IrcResponses.banOutput(event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), true);
					}
					return;
				}
				event.respond("I'm sorry, you must include a person to be un/banned");
				return;
			}
			
			if ((argments[1].equalsIgnoreCase("mute")
					|| argments[1].equalsIgnoreCase("unmute"))
					&& thisChan.isOp(event.getUser())) {
				CyniChat.printDebug("Muting...");
				if (argments[2] != null) {
					
					if (argments[1].equalsIgnoreCase("mute")) {
						IrcResponses.muteOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName(), false);
					} else {
						IrcResponses.muteOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName(), true);
					}
					return;
					
				}
				event.respond("I'm sorry, you must include a person to be un/muted");
				return;
			}
			
			if (argments[1].equalsIgnoreCase("restart")
					&& thisChan.isOp(event.getUser())) {
				CyniChat.printDebug("Restarting...");
				CyniChat.PBot.restart();
				return;
			}
			
			if (argments[1].equalsIgnoreCase("kill")
					&& thisChan.isOp(event.getUser())) {
				CyniChat.printDebug("Murdering...");
				CyniChat.PBot.stop();
				return;
			}
			
			CyniChat.printDebug("\"" + argments[1] + "\"");
			
			return;
		}
		
		CyniChat.printDebug("Sender: " + event.getUser().getNick());
		CyniChat.printDebug("Channel: " + event.getChannel().getName().toLowerCase());
		CyniChat.printDebug("Message: " + event.getMessage());
		
		//Check if we are linked to this channel, and if so forward 
		String ircChannelName = event.getChannel().getName().toLowerCase();
		if (CyniChat.data.getLinkedChans().containsKey(ircChannelName)) {
			Player[] online = Bukkit.getServer().getOnlinePlayers();
			String channelName = CyniChat.data.getLinkedChans().get(ircChannelName);
			
			CyniChat.printDebug("Match found: " + event.getChannel().getName() + " -> " + channelName);
			
			//Route message
			ChatRouter.routeMessage(ChatRouter.EndpointType.IRC,event.getUser().getNick(), channelName, event.getMessage());
		}
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) {
		CyniChat.printDebug("Private message called!");
		if (event.getMessage().startsWith(":?")) {
			String[] argments = event.getMessage().split(" ");
			CyniChat.printDebug(":? called by " + event.getUser().getNick());
			
			if (argments[1].equalsIgnoreCase("talk")) {
				CyniChat.printDebug("Talking with " + argments.length + " args... ");
				if (argments.length > 3) {
					
					CyniChat.printDebug("Talking...");
					if (IrcResponses.talkOutput(event.getBot(), argments[2], stacker(argments, 3, argments.length)) == false) {
						event.respond("Invalid statement. Please make sure that channel exits in the MC server.");
					}
				}
				
				return;
			}
			
			IrcResponses.helpOutput(event.getBot(), event.getUser());
		}
	}
	
	public String stacker(String[] args, int start, int end) {
		
		String finalString = "";
		String connector = "";
		
		for (int i = start; i < end; i++) {
			finalString += connector + args[i];
			connector = " ";
		}
		
		return finalString;
	}
}