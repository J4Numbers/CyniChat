package uk.co.CyniCode.CyniChat.Channel;

import java.util.HashMap;

import org.bukkit.ChatColor;

import uk.co.CyniCode.CyniChat.CyniChat;

public class Channel {
	
	private int ID;
	private String name;
	private String nick;
	private String desc;
	private String pass = null;
	private ChatColor colour;
	private Boolean protect = false;
	
	/**
	 * Print data about the selected object.
	 * @return ALL THE DEBUG
	 */
	public boolean printAll() {
		CyniChat.printDebug("ID: " + ID );
		CyniChat.printDebug("Name: " + name);
		CyniChat.printDebug("Nick: " + nick);
		CyniChat.printDebug("Desc: " + desc);
		CyniChat.printDebug("Pass: " + pass);
		CyniChat.printDebug("Color: " + colour.toString() );
		CyniChat.printDebug("Protected: " + protect );
		return true;
	}
	
	/**
	 * Small hack to check whether the string in the config means that a boolean is true or not.
	 * @param bool : This is the object that we're running the check off
	 * @return the boolean value of the object
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
	 * Load the details from the json file into the class.
	 * @param channel : This is the channel data, stored as a HashMap
	 * @return true for when we have loaded it all in.
	 */
	public boolean load( HashMap<String, Object> channel ) {
		this.ID = CyniChat.counter;
		this.name = channel.get("Name").toString();
		this.nick = channel.get("Nickname").toString();
		this.desc = channel.get("Description").toString();
		this.pass = channel.get("Password").toString();
		this.protect = loadCheck( channel.get("Protected") );
		this.colour = ChatColor.getByChar( channel.get("Colour").toString().charAt(0) );
		return true;
	}
	
	/**
	 * This is for when we're starting the whole thing up and there are no channels at all. Create the default channel of 'global'
	 * @return true for when we've created this channel.
	 */
	public boolean init() {
		this.ID = 1;
		this.name = "global";
		this.nick = "g";
		this.desc = "The global channel for everyone!";
		this.pass = "";
		this.protect = false;
		this.colour = ChatColor.GRAY;
		return true;
	}
	
	/**
	 * Unimplemented method for the time being, but the framework is here to add a new channel into the object
	 * @param newName : This is the new verbose name of the channel
	 * @param newNick : This is the new shortcut name of the channel
	 * @param newDesc : This is the description of the new channel
	 * @param newPass : This is any new password of the channel
	 * @param newColour : This is the char value of the colour ( 1 - f )
	 * @param newProtect : Is this channel protected or not? This is applicable for any channel you need a permission node to enter
	 * @return true for when we've created the new object
	 */
	public boolean addChannel( String newName, String newNick, String newDesc, String newPass, String newColour, boolean newProtect ) {
		this.name = newName;
		this.nick = newNick;
		this.desc = newDesc;
		this.pass = newPass;
		this.colour = ChatColor.valueOf(newColour);
		this.protect = newProtect;
		return true;
	}
	
	/**
	 * Return the ID number of the channel
	 * @return ID
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Return the verbose name of the channel
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the nickname of the channel
	 * @return nick
	 */
	public String getNick() {
		return nick;
	}
	
	/**
	 * Return the description of the channel
	 * @return desc
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Return the Password of the channel
	 * @return pass
	 */
	public String getPass() {
		return pass;
	}
	
	/**
	 * Return the colour object of the channel
	 * @return colour
	 */
	public ChatColor getColour() {
		return colour;
	}
	
	/**
	 * Check whether two passwords match
	 * @param checkPass : This is the password to be checked
	 * @return A boolean value of whether it matches or not
	 */
	public boolean equalsPass( String checkPass ) {
		if ( this.pass != null ) {
			if ( this.pass.equals( checkPass ) ) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Return whether the channel is protected or not
	 * @return protect
	 */
	public Boolean isProtected() {
		return protect;
	}

}
