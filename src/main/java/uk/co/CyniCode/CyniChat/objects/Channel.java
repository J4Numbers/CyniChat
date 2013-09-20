package uk.co.CyniCode.CyniChat.objects;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * The class which defines all the things for any channel created.
 * @author Matthew Ball
 *
 */
public class Channel {
	
	/**
	 * Expose all data to json for saving
	 */
	@Expose
	private int ID;
	@Expose
	private String name;
	@Expose
	private String nick;
	@Expose
	private String desc;
	@Expose
	private String pass = null;
	@Expose
	private ChatColor colour;
	@Expose
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
	 * Load a channel into the system
	 * @param id : The ID of the channel
	 * @param newName : The name of the channel
	 * @param newNick : The nickname of the channel
	 * @param newDesc : The description of the channel
	 * @param newPass : The password of the channel
	 * @param newColour : The channel's colour
	 * @param newProtect : Whether or not the channel is protected
	 * @return true when complete
	 */
	public Boolean loadChannel( int id, String newName, String newNick, String newDesc, String newPass, String newColour, boolean newProtect ) {
		this.ID = id;
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
	
	/**
	 * Public constructor for gson
	 * Doubles as default constructor for global channel
	 */
	public Channel(){
		this.ID = 1;
		this.name = "Global";
		this.nick = "g";
		this.desc = "The global channel for everyone!";
		this.pass = "";
		this.protect = false;
		this.colour = ChatColor.GRAY;
	}
	
	/**
	 * Set the colour of the channel to something else
	 * @param newColor : This must be in the list of bukkit colours (RED, DARK_RED etc.)
	 * @return true when complete
	 */
	public boolean setColor( String newColor ) {
		this.colour = ChatColor.valueOf( newColor.toUpperCase() );
		return true;
	}

	/**
	 * Set the ID of the channel
	 * @param newID : The ID we're changing to
	 * @return true when complete
	 */
	public boolean setId( int newID ) {
		this.ID = newID;
		return true;
	}

	/**
	 * Set the password of the channel to something else
	 * @param newPass : The password which is being set
	 * @return true when complete
	 */
	public boolean setPassword( String newPass ) {
		this.pass = newPass;
		return true;
	}

	/**
	 * Set the description of the channel to something else
	 * @param newDesc : What we're changing it to
	 * @return true when complete
	 */
	public boolean setDesc( String newDesc ) {
		this.desc = newDesc;
		return true;
	}

	/**
	 * Create a completely new channel with an impossible ID
	 * @param name : The namme of the new channel
	 * @param nick : The nickname of the new channel
	 * @param protect : Whether or not this channel is protected
	 * @return true when complete
	 */
	public boolean create(String name, String nick, Boolean protect ) {
		this.ID = 0;
		this.name = name;
		this.nick = nick;
		this.protect = protect;
		this.desc = "";
		this.pass = "";
		
		return true;
	}

}
