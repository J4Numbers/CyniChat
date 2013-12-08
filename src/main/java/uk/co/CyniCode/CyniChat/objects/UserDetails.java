package uk.co.CyniCode.CyniChat.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * UserDetails class and object which stores all of the relevant information for each and every player
 * @author Matthew Ball
 *
 */
public class UserDetails {
	
	/**
	 * The ID is for use when we are using MySQL data storage
	 */
	private int ID = 0;
	
	/**
	 * Every user has a player object...
	 * They just don't know it yet
	 */
	private Player player;
	
	/**
	 * When a user joins the server, they have the default
	 * channel assigned to them, it is also stored inside
	 * whatever method of storage we choose to use
	 */
	@Expose
	private String CurrentChannel = CyniChat.def_chan;
	
	/**
	 * This is for use with the /msg and /r commands,
	 * meaning that whenever the /r command is used, it checks this
	 * and when the /msg command is used, it sets this
	 */
	private String LastMessage = null;
	
	/**
	 * Is the player away from the keyboard?
	 * ****NOT IMPLEMENTED****
	 */
	private Boolean afk = false;
	
	/**
	 * If players are being annoying then we have a mechanism to completely
	 * silence them... this carries across restarting the server
	 */
	@Expose
	private Boolean Silenced = false;
	
	/**
	 * Can a player type /ignore [this player] ?
	 * We don't know yet... this is to be set via the web interface
	 * I think... or manually... that works too
	 */
	@Expose
	private Boolean CanIgnore = true;
	
	/**
	 * This is a list of all the channels that the player has joined,
	 * be them the small channels, no channels or all channels
	 */
	@Expose
	private List<String> JoinedChannels = new ArrayList<String>();
	
	/**
	 * If the player has made enemies, they will have been banned from
	 * channels... we have to keep a record of that sort of thing
	 * amazingly enough, so we keep it here
	 */
	@Expose
	private List<String> BannedFrom = new ArrayList<String>();
	
	/**
	 * Although, maybe the enemies are nice people and only muted the
	 * player... Although that's hardly nice... just slightly more cruel...
	 * It's almost like Chinese water torture
	 */
	@Expose
	private List<String> MutedIn = new ArrayList<String>();
	
	/**
	 * And if the player just doesn't like someone, then they have the 
	 * option to straight-up ignore the person they don't like
	 */
	@Expose
	private List<String> Ignoring = new ArrayList<String>();
	
	/**
	 * We have a new user!
	 * 
	 * Let's celebrate by adding them to the default channel!
	 */
	public UserDetails() {
		this.JoinedChannels.add( CyniChat.def_chan.toLowerCase() );
	}
	
	/**
	 * Adds an ignored player to the list
	 * @param ignoree : The player we're going to ignore 
	 * @return true when complete or false with insufficient perms.
	 */
	public boolean addIgnore( String ignoree ) {
		
		//Follow the procedure... can the player ignore people?
		if ( CyniChat.perms.checkPerm( getPlayer(), "cynichat.basic.ignore") ) {
			
			//Well apparently they can. Can the ignoree be ignored?
			if ( CyniChat.data.getDetails(ignoree.toLowerCase()).canIgnore() ) {
				
				//Yeppers yet again...
				//Now, let's just see if the player is already being
				// ignored...
				if ( !getIgnoring().contains( ignoree.toLowerCase() ) ) {
					
					//Nope, they weren't...
					//Let's fix that for them
					getIgnoring().add( ignoree.toLowerCase() );
					getPlayer().sendMessage("You have muted the player");
					return true;
					
				} else {
					
					//Huh... they were already being ignored
					// I suppose that saves us a job.
					getPlayer().sendMessage("You are already ignoring this player");
					return true;
					
				}
			}
			
			//Unignorable player
			getPlayer().sendMessage("You cannot ignore this player.");
			return true;
		}
		
		//A distinct lack of permissions
		getPlayer().sendMessage("You lack the permissions necessary to ignore a person. Poor you...");
		return false;
		
	}
	
	/**
	 * Remove a player which is being ignored
	 * @param ignoree : this is the player we're going to try and listen to again
	 * @return true when complete or false with insufficient perms
	 */
	public boolean remIgnore( String ignoree ) {
		
		//Right... let's get this out of the way quickly.
		//Does the player have the permissions to unignore someone?
		if ( CyniChat.perms.checkPerm( getPlayer(), "cynichat.basic.ignore") ) {
			
			//Yes they do. Are they ignoring this person already?
			if ( getIgnoring().contains( ignoree.toLowerCase() ) ) {
				
				//Yes they are... fix that
				getIgnoring().remove( ignoree.toLowerCase() );
				getPlayer().sendMessage("You have unmuted the player");
				return true;
				
			} else {
				
				//Nope, tell them so and get the heck out of there
				getPlayer().sendMessage("You are not ignoring this player");
				return true;
				
			}
		}
		
		//Permissions insecurity
		return false;
	}
	
	/**
	 * Toggle the afk status
	 */
	public void changeAfk() {
	    this.afk = ( this.afk != true );
	}
	
	/**
	 * Change the latest sender of a msg
	 * @param newPerson : This is the person who can now be /r'd
	 * @return true when complete
	 */
	public boolean changeLatest( String newPerson ) {
		this.LastMessage = newPerson;
		return true;
	}
	
	/**
	 * Send a new /msg and alter the internals accordingly
	 * @param receiver : This is the person receiving the message
	 * @return true when complete
	 */
	public boolean newMsg( UserDetails receiver ) {
		
		//Okay... so change the latest details on both of our suspects
		changeLatest( receiver.getName().toLowerCase() );
		receiver.changeLatest( this.getName() );
		return true;
		
	}
	
	/**
	 * Send a new /r and alter the other players Latest, just in case
	 * @param receiver : The person to receive the message
	 * @return true when complete
	 */
	public boolean newR( Player receiver ) {
		if ( this.LastMessage != null )
			CyniChat.data.getOnlineDetails( receiver )
				.changeLatest( this.getName() );
		return true;
	}
	
	/**
	 * Add a muted channel to a player
	 * @param muter : This is the person trying to mute the player
	 * @param channel : This is the channel the player is muted in
	 * @return true when complete or false if they're already muted
	 */
	public boolean addMute( String muter, Channel channel ) {
		
		//Let us first make sure that the person is not already muted in
		// the asking channel
		if ( !getMutedChannels().contains( channel.getName().toLowerCase() ) ) {
			
			//Okay, they're not. Mute them in the channel
			getMutedChannels().add( channel.getName().toLowerCase() );
			
			//If they're around, it's only polite to tell them
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been muted in " + channel.getName() );
			
			//Print a message to console so we can keep track of everything
			CyniChat.printDebug( muter + " muted " + getName() + " in " + channel.getName() );
			
			return true;
		}
		
		//Person is already muted
		return false;
		
	}
	
	/**
	 * Removes a muted channel from a player
	 * @param unmuter : this is the player that is unmuting someone
	 * @param channel : This is the channel the player can now talk in again
	 * @return true when complete or false if they're not muted anyway
	 */
	public boolean remMute( String unmuter, Channel channel ) {
		
		//Speedrun!
		//Has the player been muted in this channel before?
		if ( getMutedChannels().contains( channel.getName().toLowerCase() ) ) {
			
			//Yes they have. Unmute them
			getMutedChannels().remove( channel.getName().toLowerCase() );
			
			//If they're online, tell them so
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been unmuted in "+channel.getName() );
			
			//Print out a debug message to the console
			CyniChat.printDebug( unmuter + " has unmuted " + getName() + " in " + channel.getName() );
			
			return true;
		}
		
		//Player wasn't muted in the channel
		return false;
	}
	
	/**
	 * Add a new ban to the player
	 * @param banner : This is the person trying to enforce the ban
	 * @param channel : This is the channel the player is being banned from
	 * @return true when completed or false if they're already banned
	 */
	public boolean newBan( String banner, Channel channel ) {
		
		//Let's check whether or not the player is already banned in this channel
		if ( !getBannedChannels().contains( channel.getName().toLowerCase() ) ) {
			
			//Apparently they're not... are they even in this channel for that matter?
			if ( getAllChannels().contains( channel.getName().toLowerCase() ) ) {
				
				//Yep... so they are. Take them out of the channel
				getAllChannels().remove( channel.getName().toLowerCase() );
				
				//And set their current channel to something which isn't
				// the banned channel... if it is the banned channel in
				// any case.
				if ( getCurrentChannel().equals(channel.getName().toLowerCase() ) ) {
					
					//If that was the only channel they were in, set the current
					// channel to nothing
					if ( getAllChannels().isEmpty() ) {
					    
						setCurrentChannel( "" );
						
					} else {
					    
						//Otherwise... it's the first available channel
						setCurrentChannel( getAllChannels().get(0) );
						
					}
					
				}
				
			}
			
			//Add the channel to the banned list
			getBannedChannels().add( channel.getName().toLowerCase() );
			
			//And drop the player a note out of kindness
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been banned in "+channel.getName() );
			
			//Then send a message to the console
			CyniChat.printDebug( banner + " has banned " + getName() + " in " + channel.getName() );
			
			return true;
		}
		
		//Player was already banned in this channel
		return false;
		
	}
	
	/**
	 * Remove a ban from a player
	 * @param unbanner : This is the player trying to unban someone
	 * @param channel : This is the channel that they're being unbanned in
	 * @return true when complete or false when they're not even banned
	 */
	public boolean remBan( String unbanner, Channel channel ) {
		
		//Is the player banned in this channel?
		if ( getBannedChannels().contains( channel.getName().toLowerCase() ) ) {
			
			//Let's fix that by removing the channel from their banned
			// list
			getBannedChannels().remove( channel.getName().toLowerCase() );
			
			//If the player is online, drop them a message to tell them
			// that they've been unbanned
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been unbanned from "+channel.getName() );
			
			//And write a note to the debug
			CyniChat.printDebug( unbanner + " has unbanned " + getName() + " in " + channel.getName() );
			
			return true;
			
		}
		
		//Player wasn't banned in the first place
		return false;
		
	}
	
	/**
	 * Completely remove all instances of the channel from the player
	 * @param name : The channel we're wiping out
	 * @return true if any instances were cleared
	 */
	public boolean clearChannel( String name ) {
		
		//Assume that there were no instances of the channel about
		// the player's person
		boolean ending = false;
		
		//Is there an instance in the joined channels?
		if ( getAllChannels().contains(name) ) {
			//Yes there was!
			//Leave the channel and mark the instance found as
			// true
			leaveChannel(name);
			ending = true;
		}
		
		//Was the player banned in this channel?
		if ( getBannedChannels().contains(name) ) {
			//Yes they were!
			//Remove the ban and mark the instance found as
			// true
			getBannedChannels().remove(name);
			ending = true;
		}
		
		//Was the player muted in this channel?
		if ( this.getMutedChannels().contains(name) ) {
			//Apparently so!
			//Remove the mute and mark the instance found as
			// true
			getMutedChannels().remove(name);
			ending = true;
		}
		
		//Return whether or not there was such an instance
		return ending;
		
	}
	
	/**
	 * This kicks the player from the channel
	 * @param kicker : The kicker of the player
	 * @param channel : The channel the player is being kicked from
	 * @return true when kicked, false if the player was never in the channel in the first place
	 */
	public boolean Kick( String kicker, Channel channel ) {
		
		//Tell the nice console what is going on
		CyniChat.printDebug( kicker +" attempted to kick "+ getName() + " from " + channel.getName() );
		
		//Is the player even joined to the channel?
		if ( getAllChannels().contains( channel.getName().toLowerCase() ) ) {
			
			//Apparently so. Let us rectify that situation
			getAllChannels().remove( channel.getName().toLowerCase() );
			
			//Is their current channel that of which they have just been
			// kicked from?
			if ( getCurrentChannel().equals(channel.getName().toLowerCase() ) ) {
				
				//Right again! Was it the only channel they were inside?
				if ( getAllChannels().isEmpty() ) {
					//Yes... They are now in no channels
					setCurrentChannel( "" );
				} else { 
					//Nope. Set their current channel to the next one along
					setCurrentChannel( getAllChannels().get(0) );
				}
				
			}
			
			//If they're online, drop them a note
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been kicked from "+channel.getName() );
			
			//Tell the console what happened
			CyniChat.printDebug( kicker + " has kicked " + getName() + " from " + channel.getName() );
			
			return true;
		}
		
		//The person was not in the channel
		return false;
	}
	
	/**
	 * Globally silences a player
	 * @param silencer : this is the person silencing the player
	 * @return true when complete
	 */
	public boolean Silence( CommandSender silencer ) {
		
		//Is the player already silenced?
		if ( getSilenced() == false ) {
			
			//Apparently not... fix that situation
			setSilenced( true );
			
			//Tell the nice player that they have been completely
			// silenced if they're online
			if ( getPlayer() != null )
				getPlayer().sendMessage( "You have been muted." );
			
			//And tell the bastard that muted them that his dirty
			// work has been done
			silencer.sendMessage("Player has been muted");
			
			//Drop the debug a message
			CyniChat.printDebug( silencer.getName() + " silenced " + getName() );
			
			return true;
			
		}
		
		//Someone got there first.
		silencer.sendMessage("Player is already muted");
		return true;
	}
	
	/**
	 * Globally unmutes a player
	 * @param listener : This is the person who unmuted the player
	 * @return true when complete
	 */
	public boolean Listen( CommandSender listener ) {
		
		//Is the player already muted?
		if ( getSilenced() == true ) {
			
			//Yep... let them speaketh!
			setSilenced( false );
			
			//If they're around, tell them that they can speak again
			if ( getPlayer() != null ) 
				getPlayer().sendMessage( "You have been unmuted" );
			
			//Let the person who unmuted them know
			listener.sendMessage("Player has been unmuted");
			
			//Drop the console a line
			CyniChat.printDebug( listener.getName() + " unmuted " + getName() );
			
			return true;
			
		}
		
		//Player was already unmuted
		listener.sendMessage("Player is already unmuted");
		return true;
		
	}
	
	/**
	 * bind a player object to the details (used when a player joins)
	 * @param player
	 */
	public void bindPlayer( Player player ) {
		this.player = player;
	}
	
	/**
	 * Unbinds the player;
	 */
	public void unbindPlayer(){
		this.player = null;
	}
		
	/**
	 * Iterate through all the values we store in here and return them as debug
	 * @return ALL THE DEBUG
	 */
	public boolean printAll() {
		
		//Their name
		CyniChat.printDebug("Name: "+ getName() );
		
		//Whether or not they can speak at all
		CyniChat.printDebug("Silenced: "+ getSilenced().toString() );
		
		//Whether they are ignorable or not
		CyniChat.printDebug("Can Ignore: "+ canIgnore().toString() );
		
		//What their current channel is
		CyniChat.printDebug("Current Channel: "+ getCurrentChannel() );
		
		//All the channels that they are currently in
		CyniChat.printDebug("All Channels: "+ getAllVerboseChannels() );
		
		//All the channels that they are banned in
		CyniChat.printDebug("Banned Channels: "+ getBannedVerboseChannels() );
		
		//All the channels that they are muted in
		CyniChat.printDebug("Muted Channels: "+ getMutedVerboseChannels() );
		
		//All the people that they are ignoring
		CyniChat.printDebug("Muted Players: "+ getVerboseIgnoring() );
		
		return true;
	}
	
	/**
	 * When a player joins a channel, this is being used
	 * @param newChan : This is the new channel that we're joining
	 * @param pass : This is either "" or the password sent by the user, to be compared to the channel object.
	 * @return false if the channel is protected and the user doesn't have the perm... actually, just read the code, it's simple enough.
	 */
	public boolean joinChannel( Channel newChan, String pass ) {
		
		//Is the new Channel private?
		if ( newChan.isProtected() ) {
			
			//Apparently so... does the player have the permission to join every channel?
			if ( CyniChat.perms.checkPerm( getPlayer(), "cynichat.basic.join.all") || 
				
					//Or... this specific channel and the password to join it too?
					( CyniChat.perms.checkPerm( getPlayer(), 
							"cynichat.basic.join."+newChan.getName().toLowerCase() ) 
							&& newChan.equalsPass( pass ) ) ) {
				
				//Is the player already inside this channel?
				if ( !getAllChannels().contains( newChan.getName().toLowerCase() ) )
					//Nope... add it to the list
					getAllChannels().add( newChan.getName().toLowerCase() );
				
				//Set the current channel to this one regardless
				setCurrentChannel( newChan.getName().toLowerCase() );
				
				//And let the player know that they are now in the right channel if
				// they are online.
				if ( getPlayer() != null )
					getPlayer().sendMessage("You are now talking in "+ newChan.getName() );
				
				//Drop the console a line
				CyniChat.printDebug( getName() + " joined " + newChan.getName() );
				
				return true;
			}
			
			//Either the permission wasn't there or the password was wrong
			// don't have any specifics here yet
			getPlayer().sendMessage("You could not join this channel for some reason or another... unlucky.");
			return true;
			
		}
		
		//The channel wasn't protected... does the password match?
		if ( newChan.equalsPass( pass ) ) {
			
			//Are they already in the channel?
			if ( !getAllChannels().contains( newChan.getName().toLowerCase() ) )
				//EeNoooooo... Let's fix that.
				getAllChannels().add( newChan.getName().toLowerCase() );
			
			//Set their current channel to this regardless
			setCurrentChannel( newChan.getName().toLowerCase() );
			
			//Tell the player that they are now in the channel
			// if they're around
			if ( getPlayer() != null )
				player.sendMessage("You are now talking in "+ newChan.getName() );
			
			//Tell the console the good news
			CyniChat.printDebug( getName() + " joined " + newChan.getName() );
			
			return true;
			
		}
		
		//The password was incorrect amazingly enough
		getPlayer().sendMessage("This channel requires the 'correct' password. Not the wrong one.");
		return true;
		
	}
	
	/**
	 * When a player leaves a channel, strike the channel from all the lists
	 * @param chan : This is the channel they're leaving
	 * @return true for when all the actions have been carried out.
	 */
	public boolean leaveChannel( String chan ) {
		
		//Is the channel contained in our joined channels?
		if ( getAllChannels().contains( chan.toLowerCase() )) {
			
			//Aye-aye! Now... are we allowed to leave it?
			if ( CyniChat.perms.checkPerm( getPlayer(), "cynichat.basic.leave."+chan.toLowerCase()) ) {
				
				//Also, yes. Let us leave.
				getAllChannels().remove( chan.toLowerCase() );
				
				//Is the current channel the one that we're leaving?
				if ( getCurrentChannel().equals(chan.toLowerCase()) ) {
					
					//Yep, now are we joined to any other channels?
					if ( getAllChannels().isEmpty() ) { 
						
						//Nope. We are in no channels
						setCurrentChannel( "" );
						
						//Tell the player that they are all alone
						if ( getPlayer() != null )
							getPlayer().sendMessage( "You have left all channels. Join one to talk." );
						
					} else {
						
						//Set the channel to the next onoe available
						setCurrentChannel( getAllChannels().get(0) );
						
						//Tell the player that tehy are in a strange and
						// unfamiliar world... or a new channel. That works too
						if ( getPlayer() != null ) 
							getPlayer().sendMessage("You are now in " + getAllChannels().get(0));
						
					}
				}
				
				//Let them know that they HAVE actually left the channel
				if ( getPlayer() != null )
					getPlayer().sendMessage("You have left "+ chan);
				
				//Drop the console a note
				CyniChat.printDebug( getName() + " has left " + chan );
				
				return true;
				
			} else {
				
				//Stay a while...
				//Don't worry... your soul will be safe...
				if ( getPlayer() != null )
					getPlayer().sendMessage("You cannot leave "+ chan);
				
				return true;
				
			}
			
		}
		
		//They weren't even in the channel in the first place!
		//... Idiot...
		if ( getPlayer() != null ) 
			getPlayer().sendMessage("You are not in "+ chan);
		
		return true;
	}
	
	/**
	 * Return the current channel the player is in
	 * @return CurrentChannel
	 */
	public String getCurrentChannel() {
		return this.CurrentChannel;
	}
	
	/**
	 * Set the current channel to something else
	 * @param newChannel : What is the new current channel?
	 */
	public void setCurrentChannel( String newChannel ) {
		this.CurrentChannel = newChannel;
	}
	
	/**
	 * Return the name of the player
	 * @return player name
	 */
	public String getName() {
		return player.getName().toLowerCase();
	}
	
	/**
	 * Return the player object
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Return whether they are silenced or not
	 * @return Silenced
	 */
	public Boolean getSilenced() {
		return Silenced;
	}
	
	/**
	 * Set whether the player is silenced or not
	 * @param state : Their new speaking state
	 */
	public void setSilenced( boolean state ) {
		this.Silenced = state;
	}
	
	/**
	 * Return whether the player is afk or not
	 * @return afk
	 */
	public Boolean getAfk() {
		return afk;
	}
	
	/**
	 * Return all the channels that the player is in
	 * @return JoinedChannels
	 */
	public List<String> getAllChannels() {
		return JoinedChannels;
	}
	
	/**
	 * Set all the joined channels
	 * @param channels : The list of joined channels
	 */
	public void setAllChannels( List<String> channels ) {
		this.JoinedChannels = channels;
	}
	
	/**
	 * Turn any list of channels into a csv list
	 * @param channels : The List of channels
	 * @return the string representation of all the channels
	 */
	public String getVerbose( List<String> channels ) {
		
		//Initialise the basic returns
		String AllChan = "";
		String c = "";
		
		//If there are no channels that we are joined to...
		if ( channels.isEmpty() )
			//then return bugger all
			return "-";
		
		//For the list, return a csv list of joined channels.
		for ( int i=0; i<channels.size(); i++ ) {
			AllChan += c + channels.get(i);
			c = ", ";
		}
		
		//Return the new string representation
		return AllChan;
		
	}
	
	/**
	 * Turn the JoinedChannels array into a string
	 * @return JoinedChannels Verbose
	 */
	public String getAllVerboseChannels() {
		return getVerbose( getAllChannels() );
	}
	
	/**
	 * Return all the channels a player is banned from
	 * @return BannedFrom
	 */
	public List<String> getBannedChannels() {
		return BannedFrom;
	}
	
	/**
	 * Set all the channels the player is banned in
	 * @param channels : A list of all those channels
	 */
	public void setBannedChannels( List<String> channels ) {
		this.BannedFrom = channels;
	}
	
	/**
	 * Return the string of all the channels that a player is banned from
	 * @return BannedFrom Verbose
	 */
	public String getBannedVerboseChannels() {
		return getVerbose( getBannedChannels() );
	}
	
	/**
	 * Return the channels that a player is muted in
	 * @return MutedIn
	 */
	public List<String> getMutedChannels() {
		return MutedIn;
	}
	
	/**
	 * Set all the channels the player is muted in
	 * @param channels : The channels the player is muted in
	 */
	public void setMutedChannels( List<String> channels ) {
		this.MutedIn = channels;
	}
	
	/**
	 * Return the strings of the MutedIn array
	 * @return MutedIn Verbose
	 */
	public String getMutedVerboseChannels() {
		return getVerbose( getMutedChannels() );
	}
	
	/**
	 * Can the player be ignored?
	 * @return whether they can be or not
	 */
	public Boolean canIgnore() {
		return CanIgnore;
	}
	
	public void setIgnoreStatus( boolean state ) {
		this.CanIgnore = state;
	}
	
	/**
	 * Return the players that are being ignored
	 * @return Ignoring
	 */
	public List<String> getIgnoring() {
		return Ignoring;
	}
	
	/**
	 * Set all the people we are ignoring
	 * @param people : Those guys
	 */
	public void setIgnoring( List<String> people ) {
		this.Ignoring = people;
	}
	
	/**
	 * Return the strings of the Ignoring array
	 * @return Ignoring Verbose
	 */
	public String getVerboseIgnoring() {
		return getVerbose( getIgnoring() );
	}

	/**
	 * Get the last person we sent a message to
	 * @return the last person we did a /msg or /r to
	 */
	public String getLatest() {
		return this.LastMessage;
	}

	/**
	 * Get the ID of the player
	 * @return the ID of the player
	 * What else did you think I was going to give?
	 */
	public int getID() {
		return this.ID;
	}

	/**
	 * Set the new ID of the player
	 * @param newId : The ID that will replace the old one
	 */
	public void setId( int newId ) {
		this.ID = newId;
	}

	/**
	 * Load in a player to the class and all their details
	 * @param id : The ID of this player
	 * @param active : The active channel of this player
	 * @param silence : Whether or not this player is globally muted
	 * @param canIgnore : Can we ignore this player or not?
	 * @param joined : All the channels that this player has joined
	 * @param muted : All the channels this player has been muted in
	 * @param banned : All the channels this player has been banned in
	 * @param ignoring : All the players this player is ignoring
	 * @return true when complete
	 */
	public Boolean loadData( int id, String active, Boolean silence, Boolean canIgnore, 
			List<String> joined, List<String> muted, List<String> banned, List<String> ignoring ) {
		
		//Set the ID
		setId( id );
		
		//Bind the player to null
		unbindPlayer();
		
		//Set the current channel as directed
		setCurrentChannel( active.toLowerCase() );
		
		//Set whether they can talk
		setSilenced( silence );
		
		//Are they ignorable?
		setIgnoreStatus( canIgnore );
		
		//Set all their joined channels
		setAllChannels( joined );
		
		//Set all channels that the player is muted in
		setMutedChannels( muted );
		
		//Set all channels that the player is banned in
		setBannedChannels( banned );
		
		//Set all the people that the player is ignoring
		setIgnoring( ignoring );
		
		return true;
	}

}
