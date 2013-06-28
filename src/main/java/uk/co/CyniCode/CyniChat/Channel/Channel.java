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
	private boolean protect = false;
	
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
	
	private boolean loadCheck( Object bool ) {
		CyniChat.printDebug((String) bool);
		if ( bool.equals("true") ) {
			return true;
		} else {
			return false;
		}
	}
	
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
	
	public boolean addChannel( String newName, String newNick, String newDesc, String newPass, String newColour, boolean newProtect ) {
		this.name = newName;
		this.nick = newNick;
		this.desc = newDesc;
		this.pass = newPass;
		this.colour = ChatColor.valueOf(newColour);
		this.protect = newProtect;
		return true;
	}
	
	public int getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getPass() {
		return pass;
	}
	
	public ChatColor getColour() {
		return colour;
	}
	
	public boolean equalsPass( String checkPass ) {
		if ( this.pass != null ) {
			if ( this.pass.equals( checkPass ) ) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	public boolean isProtected() {
		return protect;
	}

}
