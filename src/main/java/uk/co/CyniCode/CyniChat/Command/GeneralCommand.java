package uk.co.CyniCode.CyniChat.Command;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * Class for most of the basic commands and some not-so-basic commands
 * @author Matthew Ball
 *
 */
public class GeneralCommand {

	/**
	 * Save all the current details in circulation
	 * @param player : The player executing the save
	 * @return true when complete
	 */
	public static boolean save( CommandSender player ) {
		if ( player instanceof Player )
			if ( PermissionManager.checkPerm( (Player) player, "cynichat.admin.save") )
				return false;
		
		DataManager.saveChannelConfig();
		DataManager.saveUserDetails();
		return true;
	}
	
	/**
	 * Reload the plugin completely
	 * @param player : The player trying to reload the plugin
	 * @return true when complete
	 */
	public static boolean reload( CommandSender player ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.admin.reload") )
				return false;
		
		CyniChat.reload();
		return true;
	}
	
	/**
	 * Return the information about a channel
	 * @param player : The player asking
	 * @param channel : The channel they're asking about
	 * @return true when complete
	 */
	public static boolean info( CommandSender player, String channel ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.basic.info."+channel) )
				return false;
		
		if ( DataManager.getChannel(channel) == null ) {
			player.sendMessage("There is no such channel");
			return true;
		}
		
		Channel chan = DataManager.getChannel(channel);
		player.sendMessage( "Name: "+chan.getColour()+chan.getName() );
		player.sendMessage( "Nick: "+chan.getColour()+"["+chan.getNick()+"]" );
		player.sendMessage( "Description: "+chan.getDesc() );
		return true;
	}
	
	/**
	 * Return a complete list of all channels
	 * @param player : The player asking
	 * @param page   : The page they're currently asking about --Currently Unused--
	 * @return true when complete
	 */
	public static boolean list( CommandSender player, int page ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.basic.list" ) )
				return false;
		
		Map<String, Channel> channels = DataManager.returnAllChannels();
		Object[] keys = channels.keySet().toArray();
		for ( int i=0; i<keys.length; i++ ) {
			Channel current = channels.get(keys[i]);
			player.sendMessage( current.getColour() +"["+ current.getNick() +"] "+ current.getName() );
		}
		return true;
	}
	
	/**
	 * Get information about all those inside a channel
	 * @param player : The player who's asking
	 * @param channel : The channel they're asking about
	 * @return true when complete
	 */
	public static boolean who( CommandSender player, String channel ) {
		if ( player instanceof Player )
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.basic.who."+channel) )
				return false;
		
		if ( DataManager.getChannel(channel) == null ) {
			player.sendMessage( "There is no such channel" );
			return true;
		}
		
		String players = "";
		Map<String, UserDetails> online = DataManager.returnAllOnline();
		Object[] keys = online.keySet().toArray();
		for ( int i=0; i<keys.length; i++ ) {
			UserDetails current = online.get(keys[i]);
			if ( current.getAllChannels().contains(channel) ) {
				players = players + current.getName()+" ";
			}
		}
		player.sendMessage(players);
		return true;
	}
	
	/**
	 * Send a quick one-line-message to another channel
	 * @param player : The player who's sending
	 * @param channel : The channel that's receiving
	 * @param Message : The message that's being passed along
	 * @return true when complete
	 */
	public static boolean quickMessage( CommandSender player, String channel, String Message ) {
		if ( player instanceof Player ) {
			if ( !PermissionManager.checkPerm( (Player) player, "cynichat.basic.qm") ) {
				player.sendMessage( "You do not have the necessary permissions for this." );
				return false;
			}
			if ( DataManager.getOnlineDetails( (Player) player ).getMutedChannels().contains( DataManager.getChannel(channel).getName() ) ) {
				player.sendMessage( "You are muted in this channel... SHAAAAAME" );
				return false;
			}
		}
		
		CyniChat.printDebug( "New Quick Message..." );
		if ( DataManager.getChannel(channel) != null ) {
			UserDetails sender = DataManager.getOnlineDetails( (Player) player );
			Channel curChan = DataManager.getChannel(channel);
			
			if ( sender.getAllChannels().contains( curChan.getName() ) ) {
				Map<String, UserDetails> online = DataManager.returnAllOnline();
				Object[] keys = online.keySet().toArray();
				Set<Player> recip = new HashSet<Player>();
				
				for ( int i=0; i<keys.length; i++ ) {
					UserDetails current = online.get(keys[i]);
					CyniChat.printDebug( "Current player: "+ keys[i] );
					if ( current.getAllChannels().contains( curChan.getName() ) ) {
						CyniChat.printDebug( keys[i] + " added to the list..." );
						recip.add( current.getPlayer() );
						current.getPlayer().sendMessage(curChan.getColour()
								+"["+curChan.getNick()+"] "
								+ PermissionManager.getPlayerFull( (Player) player ) +" : "
								+ Message );
					}
				}
				
				if ( CyniChat.IRC == true ) 
					CyniChat.PBot.sendMessage( curChan.getIRC(), player.getName(), Message );
				
				ChannelChatEvent newChatter = new ChannelChatEvent( player.getName(), curChan, Message, recip );
				Bukkit.getServer().getPluginManager().callEvent(newChatter);
				
				return true;
			}
			player.sendMessage("You are not in this channel");
			return true;
		}
		player.sendMessage("There is no such channel");
		return true;
	}
	
	/**
	 * Get the information about the quick message
	 * @param player : The player we're giving the info to
	 * @return true when complete
	 */
	public static boolean qmInfo( CommandSender player ) {
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		player.sendMessage("/qm "+ChCommand.necessary("channel")+" "+ChCommand.necessary("message"));
		return true;
	}
}
