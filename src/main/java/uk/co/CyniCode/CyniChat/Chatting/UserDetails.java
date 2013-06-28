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
	
	public boolean init( Player player ) {
		this.player = player;
		CyniChat.printDebug(this.player.getName().toLowerCase());
		this.CurrentChannel = CyniChat.def_chan;
		CyniChat.printDebug( this.CurrentChannel.toLowerCase() );
		this.JoinedChannels.add( CyniChat.def_chan );
		CyniChat.printDebug( this.JoinedChannels.get(0) );
		return true;
	}
	
	private boolean loadCheck( Object bool ) {
		CyniChat.printDebug((String) bool);
		if ( bool.equals("true") ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean load( HashMap<String, Object> details, Player joinPlayer ) {
		this.player = joinPlayer;
		this.CurrentChannel = details.get("CurrentChannel").toString().toLowerCase();
		this.Silenced = loadCheck( details.get("Silenced") );
		this.JoinedChannels = (ArrayList<String>) details.get("JoinedChannels");
		this.BannedFrom = (ArrayList<String>) details.get("BannedChannels");
		this.MutedIn = (ArrayList<String>) details.get("MutedChannels");
		return true;
	}
	
	public boolean printAll() {
		CyniChat.printDebug("Name: "+ this.getName() );
		CyniChat.printDebug("Silenced: "+ this.getSilenced().toString() );
		CyniChat.printDebug("Current Channel: "+this.getCurrentChannel() );
		CyniChat.printDebug("All Channels: "+ this.getAllVerboseChannels() );
		CyniChat.printDebug("Banned Channels: "+ this.getBannedVerboseChannels() );
		CyniChat.printDebug("Muted Channels: "+ this.getMutedVerboseChannels() );
		return true;
	}
	
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
		this.JoinedChannels.add( newChan.getName() );
		this.CurrentChannel = newChan.getName();
		player.sendMessage("You are now talking in "+ newChan.getName() );
		return true;
	}
	
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
	
	public String getCurrentChannel() {
		return CurrentChannel;
	}
	
	public String getName() {
		return player.getName().toLowerCase();
	}
	
	public Boolean getSilenced() {
		return Silenced;
	}
	
	public ArrayList<String> getAllChannels() {
		return JoinedChannels;
	}
	
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
	
	public ArrayList<String> getBannedChannels() {
		return BannedFrom;
	}
	
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
	
	public ArrayList<String> getMutedChannels() {
		return MutedIn;
	}
	
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
