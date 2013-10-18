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
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.ChatRouter.EndpointType;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

public class BungeeChannelProxy implements PluginMessageListener, IChatEndpoint {

	public CyniChat plugin;

	public BungeeChannelProxy(CyniChat plugin) {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		this.plugin = plugin;
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
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
			p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
			CyniChat.printDebug("Message sent!");
		} catch (IOException ex) {
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();
		}
	}
}