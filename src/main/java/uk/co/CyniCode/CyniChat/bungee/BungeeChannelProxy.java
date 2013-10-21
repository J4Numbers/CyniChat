package uk.co.CyniCode.CyniChat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
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

public class BungeeChannelProxy implements PluginMessageListener, IChatEndpoint {

	public CyniChat plugin;

	public BungeeChannelProxy(CyniChat plugin) {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
		this.plugin = plugin;
	}
	
	public void giveAMessage(EndpointType type, String player, String channel, String message) {
		try {
			//Create message
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeInt(type.ordinal());// typeId
			//Fancy name transmission
			if (type == EndpointType.PLAYER) {
				
				try {
					CyniChat.printDebug(player);
					CyniChat.printDebug(channel);
					CyniChat.printDebug(message);
					Player thisOne = Bukkit.getPlayerExact(player);

					if (thisOne != null) {
						CyniChat.printDebug("Player recognised, name of : " + thisOne.getDisplayName());
					} else {
						CyniChat.printDebug("Null found");
					}

					out.writeUTF(PermissionManager.getPlayerFull(thisOne));
				} catch (NullPointerException e) {
					CyniChat.printDebug("Null error...");
					e.printStackTrace();
					return;
				}

			} else {
				out.writeUTF("");
			}
			out.writeUTF(player);
			out.writeUTF(channel);
			out.writeUTF(message);

			ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
			DataOutputStream msg = new DataOutputStream(msgBytes);
			msg.writeUTF("Forward");
			msg.writeUTF("ALL");
			msg.writeUTF("CyniChat");
			//Push message content
			msg.writeShort(b.toByteArray().length);
			msg.write(b.toByteArray());

			Player p = Bukkit.getOnlinePlayers()[0];
			
			CyniChat.printDebug( msgBytes.toString() );
			
			Thread.sleep( 5000 );
			
			p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
			CyniChat.printDebug("Message sent!");
		} catch (IOException ex) {
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();
		} catch ( InterruptedException exc ) {
			
		}
	}
	
	public void sendInformationToBungee() {
		
		CyniChat.printDebug( "Sending information to bungee..." );
		
		try {
			//Create message
			ByteArrayOutputStream bit = new ByteArrayOutputStream();
			DataOutputStream outward = new DataOutputStream(bit);
			
			Set<String> keys = DataManager.returnAllChannels().keySet();
			Iterator<String> iterKeys = keys.iterator();
			
			while ( iterKeys.hasNext() ) {
				
				String thisChanName = iterKeys.next();
				
				CyniChat.printDebug( "Checking " + thisChanName+ "..." );
				
				Channel thisChan = DataManager.returnAllChannels().get( thisChanName );
				CyniChat.printDebug( "Checking " + thisChan.getName() + " for IRC channels..." );
				if ( !thisChan.getIRC().equals("") ) {
					
					CyniChat.printDebug( "Found channel... " + thisChan.getIRC() );
					outward.writeUTF( thisChanName + "~|~" + thisChan.getIRC() + "~|~" + thisChan.getIRCPass() );
					
				}
			}
			
			outward.writeUTF( "END" );
			
			CyniChat.printDebug( "Found the end..." );
			
			ByteArrayOutputStream msgByter = new ByteArrayOutputStream();
			DataOutputStream msgd = new DataOutputStream(msgByter);
			msgd.writeUTF("Forward");
			msgd.writeUTF("ALL");
			msgd.writeUTF("CyniCord");
			//Push message content
			msgd.writeShort(bit.toByteArray().length);
			msgd.write(bit.toByteArray());
			
			Thread.sleep( 5000 );
			
			Player p = Bukkit.getOnlinePlayers()[0];
			p.sendPluginMessage(plugin, "BungeeCord", msgByter.toByteArray());
			
			CyniChat.printDebug( msgByter.toString() );
			CyniChat.printDebug("Message sent!");
			CyniChat.connected = true;
		} catch (IOException ex) {
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();
		} catch ( InterruptedException exc ) {
			CyniChat.printSevere("Thread was interrupted...");
			exc.printStackTrace();
		}
		
	}

	public void onPluginMessageReceived(String pluginChnl, Player plr, byte[] data) {
		try {
			
			CyniChat.printDebug("Channel recieved : " + pluginChnl);
			
			if (!pluginChnl.equals("BungeeCord")) {
				CyniChat.printWarning("BungeeChannelProxy was given message for channel " + pluginChnl);
				return;
			}
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			String subChannel = in.readUTF();
			if(!subChannel.equals("CyniChat")){return;/*Not our problem*/}
			
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			CyniChat.printDebug("CyniChat message recieved!");

			
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			EndpointType type = EndpointType.values()[dis.readInt()];
			String fancyPlayerName = dis.readUTF();
			String playerName = dis.readUTF();
			String chatChannel = dis.readUTF();
			String message = dis.readUTF();

			CyniChat.printDebug( "Player name : " + playerName );
			CyniChat.printDebug( "Channel name : " + chatChannel );
			CyniChat.printDebug( "Message : " + message );
			
			//if this is a player type message, use the fancy name, else use the regular name
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

	public void giveMessage(EndpointType type, String player, String channel, String message) {
		try {
			//Create message
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeInt(type.ordinal());// typeId
			//Fancy name transmission
			if (type == EndpointType.PLAYER) {
				
				try {
					CyniChat.printDebug(player);
					CyniChat.printDebug(channel);
					CyniChat.printDebug(message);
					Player thisOne = Bukkit.getPlayerExact(player);

					if (thisOne != null) {
						CyniChat.printDebug("Player recognised, name of : " + thisOne.getDisplayName());
					} else {
						CyniChat.printDebug("Null found");
					}

					out.writeUTF(PermissionManager.getPlayerFull(thisOne));
				} catch (NullPointerException e) {
					CyniChat.printDebug("Null error...");
					e.printStackTrace();
					return;
				}

			} else {
				out.writeUTF("");
			}
			out.writeUTF(player);
			out.writeUTF(channel);
			out.writeUTF(message);

			ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
			DataOutputStream msg = new DataOutputStream(msgBytes);
			msg.writeUTF("Forward");
			msg.writeUTF("ALL");
			msg.writeUTF("CyniChat");
			//Push message content
			msg.writeShort(b.toByteArray().length);
			msg.write(b.toByteArray());

			Player p = Bukkit.getOnlinePlayers()[0];
			
			CyniChat.printDebug( msgBytes.toString() );
			
			p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
			CyniChat.printDebug("Message sent!");
		} catch (IOException ex) {
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();
		}
	}
}