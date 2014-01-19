/**
 * Copyright 2013 CyniCode (numbers@cynicode.co.uk).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.CyniCode.CyniChat.objects;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * The class which defines all the things for any channel created.
 * 
 * @author CyniCode
 */
public class Channel {
	
	/**
	 * What is the ID of the channel (used only in MySQL data storage)?
	 */
	@Expose
	private int ID;
	
	/**
	 * What is the name of the channel?
	 */
	@Expose
	private String name;
	
	/**
	 * What is the nickname of the channel?
	 */
	@Expose
	private String nick;
	
	/**
	 * What is the description of the channel?
	 */
	@Expose
	private String desc;
	
	/**
	 * Does the channel have a password?
	 */
	@Expose
	private String pass = "";
	
	/**
	 * What is the colour of the chatter that happens in here?
	 */
	@Expose
	private ChatColor colour;
	
	/**
	 * Is the channel connected to an IRC channel?
	 */
	@Expose
	private String ircChannel = "";
	
	/**
	 * Does the IRC channel have a password?
	 */
	@Expose
	private String ircPassword = "";
	
	/**
	 * Is the channel protected?
	 */
	@Expose
	private Boolean protect = false;
	
	/**
	 * Public constructor for gson
	 * Doubles as default constructor for global channel
	 */
	public Channel() {
		this.ID = 1;
		this.name = "global";
		this.nick = "g";
		this.desc = "The global channel for everyone!";
		this.pass = "";
		this.protect = false;
		this.colour = ChatColor.GRAY;
	}
	
	/**
	 * Create a completely new channel with an impossible ID
	 * @param name : The name of the new channel
	 * @param nick : The nickname of the new channel
	 * @param protect : Whether or not this channel is protected
	 */
	public Channel(String name, String nick, Boolean protect ) {
		
		this.ID = 0;
		this.name = name;
		this.nick = nick;
		this.protect = protect;
		this.desc = "";
		this.pass = "";
		this.colour = ChatColor.GRAY;
		
	}
	
	/**
	 * Print data about the selected object.
	 */
	public void printAll() {
		
		//Debug everything
		CyniChat.printDebug("ID: " + getID() );
		CyniChat.printDebug("Name: " + getName() );
		CyniChat.printDebug("Nick: " + getNick() );
		CyniChat.printDebug("IRC: " + getIRC() );
		CyniChat.printDebug("IRCP: " + getIRCPass() );
		CyniChat.printDebug("Desc: " + getDesc() );
		CyniChat.printDebug("Pass: " + getPass() );
		CyniChat.printDebug("Color: " + getColour().toString() );
		CyniChat.printDebug("Protected: " + isProtected() );
		
	}
	
	/**
	 * Load a channel into the system
	 * @param id : The ID of the channel
	 * @param newName : The name of the channel
	 * @param newNick : The nickname of the channel
	 * @param ircChan : The IRC channel for this channel if it is there
	 * @param ircPass : The password for the IRC channel if it exists
	 * @param newDesc : The description of the channel
	 * @param newPass : The password of the channel
	 * @param newColour : The channel's colour
	 * @param newProtect : Whether or not the channel is protected
	 */
	public Channel( int id, String newName, String newNick,
			String ircChan, String ircPass, String newDesc, String newPass, 
			String newColour, boolean newProtect ) {
		
		//Set all the information about the channel we're looking at
		this.ID = id;
		this.name = newName;
		this.nick = newNick;
		this.desc = newDesc;
		this.pass = newPass;
		this.colour = ChatColor.valueOf( newColour );
		this.ircChannel = ircChan.toLowerCase();
		this.ircPassword = ircPass;
		this.protect = newProtect;
		
	}
	
	/**
	 * Return the ID number of the channel
	 * @return ID
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Set the ID of the channel
	 * @param newID : The ID we're changing to
	 */
	public void setId( int newID ) {
		this.ID = newID;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the verbose name of the channel
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the channel to something else
	 * @param name : Which is defined here
	 */
	public void setName( String name ) {
		this.name = name;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the nickname of the channel
	 * @return nick
	 */
	public String getNick() {
		return nick;
	}
	
	/**
	 * Set the nickname of the channel to something else
	 * @param nickname : which is defined here
	 */
	public void setNick( String nickname ) {
		this.nick = nickname;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the description of the channel
	 * @return description of the channel
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Set the description of the channel
	 * @param desc : to whatever is contained in this
	 */
	public void setDesc( String desc ) {
		this.desc = desc;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the Password of the channel
	 * @return pass
	 */
	public String getPass() {
		return pass;
	}
	
	/**
	 * Set the password of the channel to something else
	 * @param newPass : The password which is being set
	 */
	public void setPassword( String newPass ) {
		this.pass = newPass;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the colour object of the channel
	 * @return colour
	 */
	public ChatColor getColour() {
		return colour;
	}
	
	/**
	 * Set the colour of the channel to something else
	 * @param newColor : This must be in the list of bukkit colours (RED, DARK_RED etc.)
	 */
	public void setColor( String newColor ) {
		this.colour = ChatColor.valueOf( newColor.toUpperCase() );
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the IRC channel that may or may not be connected to this
	 * @return ircChannel
	 */
	public String getIRC() {
		return ircChannel;
	}
	
	/**
	 * Set a new channel name for IRC
	 * @param newName : The name we're changing it to
	 */
	public void setIrcName( String newName ) {
		this.ircChannel = newName;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Return the IRC password that might be the right one for the previous method
	 * @return ircPassword
	 */
	public String getIRCPass() {
		return ircPassword;
	}
	
	/**
	 * Set a new password for the IRC channel
	 * @param newPass : The new password for the channel
	 */
	public void setIrcPass( String newPass ) {
		this.ircPassword = newPass;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
	/**
	 * Check whether two passwords match
	 * @param checkPass : This is the password to be checked
	 * @return A boolean value of whether it matches or not
	 */
	public boolean equalsPass( String checkPass ) {
		
		//If the password has been initialised
		if ( getPass() != null )
			
			//Return the check of whether it is equal to the 
			// password we're checking off
			return getPass().equals( checkPass );
		
		//Otherwise, we can join the channel regardless
		return true;
		
	}
	
	/**
	 * Return whether the channel is protected or not
	 * @return protect
	 */
	public Boolean isProtected() {
		return protect;
	}
	
	/**
	 * Set whether the channel is protected or not
	 * @param state : The state that it is now in
	 */
	public void setProtected( Boolean state ) {
		this.protect = state;
		CyniChat.data.getConnection().saveSingleChannel( this );
	}
	
}