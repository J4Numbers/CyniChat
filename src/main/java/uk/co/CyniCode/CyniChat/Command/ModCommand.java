package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;

/**
 * Class for all things mod
 * So... promote, demote and set
 * 
 * @author CyniCode
 */
public class ModCommand {

	/**
	 * Promote a player in the channel to a mod
	 * @param player : The player doing the promoting
	 * @param channel : The channel that has a new mod
	 * @param newMod : The new guy to the ranks of the exhaulted
	 * @return true when complete
	 */
	public static boolean promote( CommandSender player, String channel, String newMod ) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//And do they have permission to promote someone?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.promote."+channel ) )
				//Nope... kill em
				return false;
		
		//Or promote the player... that too
		CyniChat.perms.promotePlayer( newMod, CyniChat.data.getChannel( channel ) );
		return true;
		
	}
	
	/**
	 * Demote a mod in the channel to a player
	 * @param player : The player doing the demoting
	 * @param channel : The channel that mourns
	 * @param oldMod : The ashamed exile
	 * @return true when complete
	 */
	public static boolean demote( CommandSender player, String channel, String oldMod ) {
		
		//Is the player a player?
		if ( player instanceof Player )
			//Yep... can they demote another player?
			if ( !CyniChat.perms.checkPerm( (Player) player, "cynichat.mod.demote."+channel ))
				//Nope... that should stop some feathers being rustled
				return false;
		
		//Demote the player that is being asked for
		CyniChat.perms.demotePlayer( oldMod, CyniChat.data.getChannel( channel ) );
		return true;
		
	}
	
	/**
	 * Show the player information about promoting another
	 * @param player : The player we're giving the info to
	 */
	public static void promoteInfo( CommandSender player ) {
		
		//Tell the player they've done an oopsie
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//And tell them how to solve it
		player.sendMessage( ChatColor.RED + "/ch promote "
			+ChCommand.necessary("player", ChatColor.RED)+" "
			+ChCommand.optional("channel", ChatColor.RED) );
		
	}
	
	/**
	 * Show the player information about demoting another
	 * @param player : The learned scholar
	 */
	public static void demoteInfo( CommandSender player ) {
		
		//Tell the player that they're wrong
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//And tell them how to right themselves
		player.sendMessage( ChatColor.RED + "/ch demote "
			+ChCommand.necessary("player", ChatColor.RED)+" "
			+ChCommand.optional("channel", ChatColor.RED) );
		
	}
	
	/**
	 * Set a particular aspect of a channel
	 * @param player : The player doing the setting
	 * @param args : The arguments that define what is being set and what to
	 * @return true when complete
	 */
	public static boolean set( CommandSender player, String[] args ) {
		
		//If the args given are not of length '4' or above
		if ( args.length >= 4  ) {
			
			//Then ask if arg 1 is a channel or not
			if ( ( CyniChat.data.getChannel( args[1] ) != null ) ) {
				
				//And get that channel
				Channel current = CyniChat.data.getChannel( args[1] );
				
				//Ask if the player is a player
				if ( player instanceof Player )
					//And if they have the permissions to
					// set information about a channel
					if ( !CyniChat.perms.checkPerm( (Player) player, 
							"cynichat.mod.set."+current.getName().toLowerCase() ) )
						return false;
				
				//Check to see what aspect we're setting...
				if ( args[2].equalsIgnoreCase("color") ) {
					
					//Set the colour to something else
					current.setColor( args[3] );
					player.sendMessage("Color change successful!");
					return true;
					
				} else if ( args[2].equalsIgnoreCase("password") ) {
					
					//Set the password of the channel to something
					// else (overwriting the previous one)
					current.setPassword( args[3] );
					player.sendMessage("Password change successful!");
					return true;
					
				} else if ( args[2].equalsIgnoreCase("ircchan") ) {
					
					//Ask if they want us to blank the ircName
					if ( args[3].equalsIgnoreCase( "-" ) ) {
						//And blank it if that's the chase
						current.setIrcName( "" );
					} else {
						//Otherwise... set it
						current.setIrcName( args[3] );
					}
					
					//Then tell the player that they were 
					// successful
					player.sendMessage("Name of the IRC channel successfully changed!");
					return true;
					
				} else if ( args[2].equalsIgnoreCase("ircpass") ) {
					
					//Same deal... if they want to set the
					// irc password to nil...
					if ( args[3].equalsIgnoreCase( "-" ) ) {
						
						//Do so...
						current.setIrcPass( "" );
						
					} else {
						
						//Otherwise... set it to the
						// asked value
						current.setIrcPass( args[3] );
						
					}
					
					//Tell the player they were successful
					player.sendMessage("Password for the IRC channel successfully changed!");
					return true;
					
				} else if ( args[2].equalsIgnoreCase("description") ) {
					
					//Or maybe they want to change the
					// description of the channel
					current.setDesc( stacker( args ) );
					player.sendMessage("Description change successful!");
					return true;
					
				}
				
			}
			
		}
		
		//Or maybe they don't know what they want to set.
		//Give them a hint.
		setInfo( player );
		return true;
	}

	/**
	 * Stack a string together
	 * @param stacking : The array we're turning into a sentence
	 * @return the sentence
	 */
	public static String stacker( String[] stacking ) {
		try {
			
			//Initialise a string
			String finalStack = "";
			
			//And for each part of the stack we've been given...
			// add it onto the final stack we'll return
			for ( String stack : stacking )
				finalStack += " "+stack;
			
			//Namely... here
			return finalStack;
			
		} catch ( NullPointerException e ) {
			//If the array we were given was blank...
			// return a blank sentence
			return "";
		}
	}

	/**
	 * Return the information on setting a channel aspect
	 * @param player : The player who wants to know
	 */
	public static void setInfo( CommandSender player ) {
		
		//Tell the player they were in the wrong
		player.sendMessage(ChatColor.RED+"Invalid Syntax!");
		
		//And how to set it right
		player.sendMessage( ChatColor.RED + "/ch set "
			+ChCommand.necessary("channel", ChatColor.RED )+" "
			+ChCommand.necessary("option", ChatColor.RED )+" "
			+ChCommand.necessary("new value", ChatColor.RED ) );
		
	}
}
