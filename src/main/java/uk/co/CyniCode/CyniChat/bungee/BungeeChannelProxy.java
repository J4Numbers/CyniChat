package uk.co.CyniCode.CyniChat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.ChatRouter.EndpointType;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

/**
 * Class for handling all the bungee nonsense
 * @author Cynical
 */
public class BungeeChannelProxy implements PluginMessageListener, IChatEndpoint {

	/**
	 * The instance of the plugin
	 */
	public CyniChat plugin;

	/**
	 * Instantiate bungeecord proxies with an iteration of CyniChat
	 * @param plugin : The instance of the plugin
	 */
	public BungeeChannelProxy(CyniChat plugin) {
		
		//Register that we're using the channel 'BungeeCord'
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		
		//And register that we're listening on the same channel
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
		
		this.plugin = plugin;
		
	}
	
	/**
	 * Whenever we get a message that is passed on from anywhere...
	 *  flag it here.
	 * @param pluginChnl : What channel did the message come from? (BungeeCord)
	 * @param plr : The player information associated with the message
	 * @param data : The bit we're interested in... the data package
	 */
	public void onPluginMessageReceived(String pluginChnl, Player plr, byte[] data) {
		try {
			
			//Tell the nice ladies and gentlemen what we've listened
			// in to.
			CyniChat.printDebug("Channel recieved : " + pluginChnl);
			
			//Then check whether we're actually allowed to listen in
			// on it.
			if (!pluginChnl.equals("BungeeCord")) {
				CyniChat.printWarning("BungeeChannelProxy was given message for channel " + pluginChnl);
				return;
			}
			
			//Apparently no-one's saying no... yet.
			CyniChat.printDebug( "Applicable to us" );
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			
			//Split it open and have a look at what sub-channel it
			// was intended for
			String subChannel = in.readUTF();
			CyniChat.printDebug( "Sub Channel : " + subChannel );
			
			//Then check it off against those we're allowed to read
			CyniChat.printDebug( "Is it REALLY applicable to us?" );
			if ( !subChannel.equals("CyniChat") && !subChannel.equals( "CyniCord" ) ) {
				//Not our problem
				CyniChat.printDebug( "Rejected subchannel: " + subChannel );
				return;
			}
			
			//Molto berne... ignore that since I can't be arsed
			// to copy and paste inflections.
			CyniChat.printDebug( "Apparently so." );
			
			//Get the meta information out of it first.
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			
			CyniChat.printDebug("CyniChat message recieved!");
			
			//And read the actual package now
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			CyniChat.printDebug( "Let's go through all the values" );
			
			//Let us assume that it is a CyniCord message. So we have
			// to instantiate everything to a default state where
			// it will be altered if it isn't the case
			EndpointType type = EndpointType.BUNGEE;
			String chatChannel;
			String IRCPassword;
			
			//Set the type ( if applicable )
			if ( subChannel.equals( "CyniChat" ) )
				type = EndpointType.values()[dis.readInt()];
			
			//Set the fancy name of the player
			CyniChat.printDebug( "Type: " + type.name() );
			String fancyPlayerName = dis.readUTF();
			
			//Set the normal name of the player
			CyniChat.printDebug( "Name: " + fancyPlayerName );
			String playerName = dis.readUTF();
			
			//Set the CyniChat chat channel... if applicable
			CyniChat.printDebug( "Name2: " + playerName );
			if ( subChannel.equals( "CyniChat" ) )
				chatChannel = dis.readUTF();
			else chatChannel = "N/A";
			
			//Set the IRC channel that belongs to the chat channel
			CyniChat.printDebug( "Channel: " + chatChannel );
			String IRCChannel = dis.readUTF();
			
			//Set the IRC password of the channel ( if applicable )
			CyniChat.printDebug( "IRC Channel: " + IRCChannel );
			if ( subChannel.equals( "CyniChat" ) )
				IRCPassword = dis.readUTF();
			else IRCPassword = "N/A";
			
			//Get the message contents
			CyniChat.printDebug( "IRC Password: " + IRCPassword );
			String message = dis.readUTF();
			
			//Now let's set the actual chat channel if it came from
			// IRC.
			if ( chatChannel.equals("N/A") )
				chatChannel = ( DataManager.getLinkedChannels().containsKey( IRCChannel ) ) ? 
					DataManager.getLinkedChannels().get( IRCChannel ) :
					"N/A";
			
			//Print out concise debug
			CyniChat.printDebug( "Player name : " + playerName );
			CyniChat.printDebug( "Channel name : " + chatChannel );
			CyniChat.printDebug( "Message : " + message );
			
			//If the channel cannot flag a match, skip over everything
			// and if this is a player type message, use the fancy 
			// name, else use the regular name
			if (DataManager.getChannel(chatChannel) != null) {
				if (type == EndpointType.PLAYER) {
					ChatRouter.routeMessage(EndpointType.BUNGEE, fancyPlayerName, chatChannel, message);
				} else {
					ChatRouter.routeMessage(type, playerName, chatChannel, message);
				}
			} else {
				CyniChat.printDebug( "The getChannel returned null for " + chatChannel );
			}
		} catch (IOException ex) {
			CyniChat.printSevere("Error parsing message in BungeeChannelProxy");
			ex.printStackTrace();
		}
	}

	/**
	 * So we want to send a message to BungeeCord central do we?
	 * Well it's fiddly as hell so I don't know why we want to do so.
	 * @param type : We need an EndpointType for it (Thank tehbeard)
	 * @param player : We also need to associate a player with the whole
	 *  thing (thank bungeecord)
	 * @param channel : And we need to add a payload into everything
	 * @param message : along with the actual message that we want to give
	 * @throws NullPointerException : If the player can't be raised
	 */
	public void giveMessage(EndpointType type, String player, String channel, String message)
			throws NullPointerException {
		try {
			//Create message containers
			Channel thisChan;
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeInt(type.ordinal());// typeId
			
			//Fancy name transmission
			if (type == EndpointType.PLAYER) {
				
				try {
					//Print out some basic debug
					CyniChat.printDebug("Player: " + player);
					CyniChat.printDebug("Channel: " + channel);
					CyniChat.printDebug("Message: " + message);
					
					//Get the player object from Bukkit when given the name
					Player thisOne = Bukkit.getPlayerExact(player);
					
					//If it's a null instance, then debug it through
					// and cry to our mother... which i'm not sure
					// who it is...
					if (thisOne != null) {
						CyniChat.printDebug("Player recognised, name of : " + thisOne.getDisplayName());
					} else {
						CyniChat.printDebug("Null found");
						throw new NullPointerException( "Player \"" + player + "\" does not exist" );
					}
					
					//Write out the name of the player into the message
					out.writeUTF( CyniChat.perms.getPlayerFull(thisOne));
				} catch (NullPointerException e) {
					CyniChat.printDebug("Null error...");
					e.printStackTrace();
					return;
				}
			} else {
				//No specific player defined.
				out.writeUTF("");
			}
			
			//Get the actual channel that we are sending on
			thisChan = DataManager.getChannel( channel );
			
			//Add the basic details to what we're transmitting
			try {
				out.writeUTF(player);
				out.writeUTF(channel);
				out.writeUTF(thisChan.getIRC() );
				out.writeUTF(thisChan.getIRCPass());
				out.writeUTF(message);
			} catch (NullPointerException e) {
				//That went well... cry again
				throw new NullPointerException( "Channel \"" + channel + "\" does not exist" );
			}
			
			//Make a data stream of the byte stream
			ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
			DataOutputStream msg = new DataOutputStream(msgBytes);
			
			//And put in the details of where it's going
			msg.writeUTF("Forward");
			msg.writeUTF("ALL");
			msg.writeUTF("CyniChat");
			
			//Push message content in
			msg.writeShort(b.toByteArray().length);
			msg.write(b.toByteArray());
			
			//And get a random player to send on as
			Player p = Bukkit.getOnlinePlayers()[0];
			CyniChat.printDebug( msgBytes.toString() );
			
			//Then send the message through the random player
			p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
			
			//This bit might lie...
			CyniChat.printDebug("Message sent!");
			
		} catch (IOException ex) {
			//Error...
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();
		}
	}
}