package uk.co.CyniCode.CyniChat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;

public class Bungee implements PluginMessageListener {

public String serverName; // Example: using the GetServer subchannel
public List<String> servers;
public CyniChat plugin;
	
	public Bungee( CyniChat plugin ) {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		serverName = plugin.getName();
		this.plugin = plugin;
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
	}
	
	@Override
	public void onBungeeMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		String subchannel = in.readUTF();
		if (subchannel.equals("CyniChat")) {
			String subChannel = in.readUTF();
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			 
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			String somedata = msgin.readUTF(); // Read the data in the same way you wrote it
			
			String[] setOfData = somedata.split( "|*|" );
		} else if (subchannel.equals("GetServer")) {
			serverName = in.readUTF();
		}
    }
	
	public boolean transmit( Player player, Channel chan, String message ) {
		ByteArrayOutputStream sending = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(sending);
		
		try {
			out.writeUTF("subchannel");
			out.writeUTF("AnArgument");
			
			// OR, if you don't need to send it to a specific player
			Player p = Bukkit.getOnlinePlayers()[0];
			
			out.writeUTF("Forward"); // So bungeecord knows to forward it
			out.writeUTF("ALL");
			out.writeUTF("CyniChat"); // The channel name to check if this your data
			
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF( player.getDisplayName() + "|*|" + chan.getName() + "|*|" + message );
			
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			
			p.sendPluginMessage( plugin, "BungeeCord", sending.toByteArray());
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
