package uk.co.CyniCode.CyniChat.Chatting;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.Channel.Channel;

public class UserDetails {
	
	private Player player;
	private String CurrentChannel;
	private Boolean Silenced = false;
	private ArrayList<String> JoinedChannels = new ArrayList<String>();
	private ArrayList<String> BannedFrom = new ArrayList<String>();
	private ArrayList<String> MutedIn = new ArrayList<String>();
	
	/**
	 * Start up a new UserDetails object for any new peeps
	 * @param player : Containing their name and whatnot
	 * @return true for when we've done all this work.
	 */
	public boolean init( Player player ) {
		this.player = player;
		CyniChat.printDebug(this.player.getName().toLowerCase());
		this.CurrentChannel = CyniChat.def_chan;
		CyniChat.printDebug( this.CurrentChannel.toLowerCase() );
		this.JoinedChannels.add( CyniChat.def_chan );
		CyniChat.printDebug( this.JoinedChannels.get(0) );
		return true;
	}
	
	/**
	 * Again, use the minor hack to check whether the config files are actually saying 'true'
	 * @param bool : The Object value of the thing we're checking
	 * @return the boolean equivalent
	 */
	private boolean loadCheck( Object bool ) {
		CyniChat.printDebug((String) bool);
		if ( bool.equals("true") ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Load in the details that we've been given from the json player files
	 * @param details : HashMap of the player config
	 * @param joinPlayer : The player who has just joined
	 * @return true for when we've loaded all of these up
	 */
	public boolean load( HashMap<String, Object> details, Player joinPlayer ) {
		this.player = joinPlayer;
		this.CurrentChannel = details.get("CurrentChannel").toString().toLowerCase();
		this.Silenced = loadCheck( details.get("Silenced") );
		this.JoinedChannels = (ArrayList<String>) details.get("JoinedChannels");
		this.BannedFrom = (ArrayList<String>) details.get("BannedChannels");
		this.MutedIn = (ArrayList<String>) details.get("MutedChannels");
		return true;
	}
	
	/**
	 * Iterate through all the values we store in here and return them as debug
	 * @return ALL THE DEBUG
	 */
	public boolean printAll() {
		CyniChat.printDebug("Name: "+ this.getName() );
		CyniChat.printDebug("Silenced: "+ this.getSilenced().toString() );
		CyniChat.printDebug("Current Channel: "+this.getCurrentChannel() );
		CyniChat.printDebug("All Channels: "+ this.getAllVerboseChannels() );
		CyniChat.printDebug("Banned Channels: "+ this.getBannedVerboseChannels() );
		CyniChat.printDebug("Muted Channels: "+ this.getMutedVerboseChannels() );
		return true;
	}
	
	/**
	 * When a player joins a channel, this is being used
	 * @param newChan : This is the new channel that we're joining
	 * @param pass : This is either "" or the password sent by the user, to be compared to the channel object.
	 * @return false if the channel is protected and the user doesn't have the perm... actually, just read the code, it's simple enough.
	 */
	public boolean joinChannel( Channel newChan, String pass ) {
		if ( newChan.isProtected() ) {
			if ( player.hasPermission("cynichat.basic.join.all") || ( player.hasPermission("cynichat.basic.join."+newChan.getName().toLowerCase() ) && newChan.equalsPass( pass ) ) ) {
				this.JoinedChannels.add( newChan.getName() );
				this.CurrentChannel = newChan.getName();
				player.sendMessage("You are now talking in "+ newChan.getName() );
				return true;
			}
			return false;
		}
		if ( newChan.equalsPass( pass ) ) {
			this.JoinedChannels.add( newChan.getName() );
			this.CurrentChannel = newChan.getName();
			player.sendMessage("You are now talking in "+ newChan.getName() );
			return true;
		}
		return false;
	}
	
	/**
	 * When a player leaves a channel, strike the channel from all the lists
	 * @param chan : This is the channel they're leaving
	 * @return true for when all the actions have been carried out.
	 */
	public boolean leaveChannel( String chan ) {
		if ( this.JoinedChannels.contains( chan )) {
			if ( player.hasPermission("cynichat.basic.remove."+chan) ) {
				this.JoinedChannels.remove( chan );
				if ( this.CurrentChannel.equals(chan) ) {
					this.CurrentChannel = JoinedChannels.get(0);
					player.sendMessage("You are now in " + JoinedChannels.get(0));
				}
				player.sendMessage("You have left "+ chan);
				return true;
			} else {
				player.sendMessage("You cannot leave "+ chan);
				return true;
			}
		} else {
			player.sendMessage("You are not in "+ chan);
			return true;
		}
	}
	
	/**
	 * Return the current channel the player is in
	 * @return CurrentChannel
	 */
	public String getCurrentChannel() {
		return CurrentChannel;
	}
	
	/**
	 * Return the name of the player
	 * @return player name
	 */
	public String getName() {
		return player.getName().toLowerCase();
	}
	
	/**
	 * Return whether they are silenced or not
	 * @return Silenced
	 */
	public Boolean getSilenced() {
		return Silenced;
	}
	
	/**
	 * Return all the channels that the player is in
	 * @return JoinedChannels
	 */
	public ArrayList<String> getAllChannels() {
		return JoinedChannels;
	}
	
	/**
	 * Turn the JoinedChannels array into a string
	 * @return JoinedChannels Verbose
	 */
	public String getAllVerboseChannels() {
		String AllChan = "";
		String c = "";
		if ( JoinedChannels.isEmpty() ) {
			return "-";
		}
		for ( int i=0; i<JoinedChannels.size(); i++ ) {
			AllChan += c + JoinedChannels.get(i);
			c = ", ";
		}
		return AllChan;
	}
	
	/**
	 * Return all the channels a player is banned from
	 * @return BannedFrom
	 */
	public ArrayList<String> getBannedChannels() {
		return BannedFrom;
	}
	
	/**
	 * Return the string of all the channels that a player is banned from
	 * @return BannedFrom Verbose
	 */
	public String getBannedVerboseChannels() {
		String BanChan = "";
		String c = "";
		if ( BannedFrom.isEmpty() ) {
			return "-";
		}
		for ( int i=0; i<BannedFrom.size(); i++ ) {
			BanChan += c + BannedFrom.get(i);
			c = ", ";
		}
		return BanChan;
	}
	
	/**
	 * Return the channels that a player is muted in
	 * @return MutedIn
	 */
	public ArrayList<String> getMutedChannels() {
		return MutedIn;
	}
	
	/**
	 * Return the strings of the MutedIn array
	 * @return MutedIn Verbose
	 */
	public String getMutedVerboseChannels() {
		String MuteChan = "";
		String c = "";
		if ( MutedIn.isEmpty() ) {
			return "-";
		}
		for ( int i=0; i<MutedIn.size(); i++ ) {
			MuteChan += c + MutedIn.get(i);
			c = ", ";
		}
		return MuteChan;
	}
}
