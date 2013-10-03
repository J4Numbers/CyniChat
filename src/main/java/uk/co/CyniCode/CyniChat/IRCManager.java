package uk.co.CyniCode.CyniChat;

import uk.co.CyniCode.CyniChat.libs.org.pircbotx.PircBotX;

public class IRCManager {

	public static boolean start( CyniChat plugin ) throws Exception {
		
		PircBotX bot = new PircBotX();
		bot.setName( plugin.getConfig().getString("CyniChat.irc.nickname") );
		try {
			bot.connect( plugin.getConfig().getString("CyniChat.irc.hostname"), plugin.getConfig().getInt("CyniChat.irc.port") );
			bot.joinChannel( plugin.getConfig().getString("CyniChat.irc.channel") );
		} catch (Exception e) {
			
			throw e;
			
		}
		
		return true;

	}

}
