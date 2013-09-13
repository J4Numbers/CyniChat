package uk.co.CyniCode.CyniChat.Channel;

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
	private int ID;//Is this really needed
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
		
	
	public Channel( String newName, String newNick, String newDesc, String newPass, String newColour, boolean newProtect ) {
		this.name = newName;
		this.nick = newNick;
		this.desc = newDesc;
		this.pass = newPass;
		this.colour = ChatColor.valueOf(newColour);
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

	public boolean setId( int newID ) {
		this.ID = newID;
		return true;
	}

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
