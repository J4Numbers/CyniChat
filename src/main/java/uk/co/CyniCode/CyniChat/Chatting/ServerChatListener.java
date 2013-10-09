package uk.co.CyniCode.CyniChat.Chatting;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.DataManager;
import uk.co.CyniCode.CyniChat.PermissionManager;
import uk.co.CyniCode.CyniChat.Command.GeneralCommand;
import uk.co.CyniCode.CyniChat.bungee.BungeeChannelProxy;
import uk.co.CyniCode.CyniChat.events.ChannelChatEvent;
import uk.co.CyniCode.CyniChat.irc.IRCChatListener;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;
import uk.co.CyniCode.CyniChat.routing.IChatEndpoint;

/**
 * A listener class for everything Three parts: Log in, Log out, and speak
 *
 * @author Matthew Ball
 *
 */
public class ServerChatListener implements Listener, IChatEndpoint {

    public static CyniChat plugin;

    /**
     * Listen for any people joining the server so we can load in their
     * configurations or generate a new one
     *
     * @param event : This is what we're listening for
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void joinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CyniChat.printDebug("Player joined");
        DataManager.bindPlayer(player);//Load player details into online users.
    }

    /**
     * Listen for anyone leaving the server so that we can dump their
     * UserDetails into the config and have shot of them
     *
     * @param event : This is what we're listening for
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void leaveEvent(PlayerQuitEvent event) {
        CyniChat.printDebug("Player Left");
        DataManager.unbindPlayer(event.getPlayer());
    }

    /**
     * A command has been fed into the server... wat do?
     *
     * @param event : The Command event that we're checking
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void commandEvent(PlayerCommandPreprocessEvent event) {
        String comm = event.getMessage().replaceFirst("/", "");
        String[] bits = comm.split(" ");
        String mess = comm.substring(bits[0].length() + 1, comm.length());
        CyniChat.printDebug(comm);
        CyniChat.printDebug(bits[0]);
        CyniChat.printDebug(mess);
        if (CyniChat.ifCommandExists(bits[0]) == false) {
            if (DataManager.getChannel(bits[0]) != null) {
                GeneralCommand.quickMessage((CommandSender) event.getPlayer(), bits[0], mess);
                event.setCancelled(true);
            } else {
                CyniChat.printDebug("No channel was found for this name");
            }
        } else {
            CyniChat.printDebug("A command existed with this prefix of \"" + bits[0] + "\"");
        }
        return;
    }

    /**
     * Listen for any chatter on the server so that I can print debug of it for
     * the moment
     *
     * @param event : This is what we're listening for
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void chatEvent(AsyncPlayerChatEvent event) {
        CyniChat.printDebug("Format ::== " + event.getFormat());
        //CyniChat.printDebug( "Recipients ::== " + looper( event.getRecipients() ) );
        Player player = event.getPlayer();
        UserDetails user = DataManager.getOnlineDetails(player);

        if (user.getCurrentChannel() == "") {
            user.printAll();
            player.sendMessage("You are in no channels. Join one to talk.");
            event.setCancelled(true);
            return;
        }

        Channel current = DataManager.getChannel(user.getCurrentChannel().toLowerCase());

        if (user.getSilenced()) {
            user.printAll();
            player.sendMessage("You have been globally muted, you cannot talk.");
            event.setCancelled(true);
            return;
        }

        if (user.getMutedChannels().contains(current.getName().toLowerCase())) {
            player.sendMessage("You have been muted in this channel, move to another channel to talk.");
            event.setCancelled(true);
            return;
        }

        if (current.isProtected()) {
            if (PermissionManager.checkPerm(player, "cynichat.basic.talk." + current.getName().toLowerCase()) == false) {
                player.sendMessage("You do not have the permission to talk here.");
                event.setCancelled(true);
                return;
            }
        }

        String format = "<CurrentChannel> <Player> : " + current.getColour() + "%2$s";
        format = format.replace("<CurrentChannel>", current.getColour() + "[" + current.getNick() + "]");
        format = format.replace("<Player>", PermissionManager.getPlayerFull(player));
        event.setFormat(format);
        CyniChat.printDebug("Format ::== " + event.getFormat());
        Iterator<Player> receivers = event.getRecipients().iterator();
        Player[] all = new Player[event.getRecipients().size()];
        int Count = 0;

        while (receivers.hasNext()) {
            Player currentPlayer = receivers.next();
            UserDetails users = DataManager.getOnlineDetails(currentPlayer);
            CyniChat.printDebug(currentPlayer.getName() + " : " + users.getAllVerboseChannels());
            if (!users.getAllChannels().contains(current.getName().toLowerCase())) {
                all[Count] = currentPlayer;
                Count++;
            } else if ((users.getIgnoring().contains(user.getName())) && (users.getAllChannels().contains(current.getName().toLowerCase()))) {
                all[Count] = currentPlayer;
                Count++;
            }
        }
        if (Count > 0) {
            for (int i = 0; i < Count; i++) {
                CyniChat.printDebug("Iteration = " + i);
                CyniChat.printDebug("Removed " + all[i].getDisplayName());
                event.getRecipients().remove(all[i]);
            }
        }


        ChatRouter.routeMessage(ChatRouter.EndpointType.PLAYER, current.getName(), player.getName(), event.getMessage());
        /*if (CyniChat.IRC == true) {
         CyniChat.PBot.sendMessage(current.getIRC(), player.getDisplayName(), event.getMessage());
         }*/

        /*if (CyniChat.bungee == true) {
         CyniChat.bungeeInstance.transmit(player, current, event.getMessage());
         }*/

        ChannelChatEvent newChatter = new ChannelChatEvent(player.getDisplayName(), current, event.getMessage(), event.getRecipients());
        Bukkit.getServer().getPluginManager().callEvent(newChatter);
    }

    /**
     * Currently used as a testing thing for the event that is registered here
     *
     * @param event : The event we're listening to (Only visible if debug is on)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void testingRegister(ChannelChatEvent event) {
        CyniChat.printDebug(event.getSenderName() + " said " + event.getMessage() + " in " + event.getChannel().getName());
        CyniChat.printDebug(event.printVerboseRecip());
    }

    /**
     * Loop through a set
     *
     * @param item : This is what we're iterating over
     * @return the strings within the set
     */
    public static String looper(Set<Player> item) {
        String recip = null;
        int j = item.size();
        Object[] arrItem = null;
        arrItem = item.toArray();
        for (int i = 0; i < j; i++) {
            recip += arrItem[i] + ", ";
        }
        return recip;
    }

    public void giveMessage(ChatRouter.EndpointType type, String player, String channel, String message) {
        if (type == ChatRouter.EndpointType.IRC) {
            _handleIRCMessage(player, channel, message);
        }
        if (type == ChatRouter.EndpointType.BUNGEE) {
            _handleBungeeMessage(player, channel, message);
        }
    }

    private void _handleIRCMessage(String player, String channel, String message) {
        Player[] online = Bukkit.getServer().getOnlinePlayers();
        Channel chatChannel = DataManager.getChannel(channel);
        for (int i = 0; i < online.length; i++) {

            UserDetails curPl = DataManager.getOnlineDetails(online[i]);

            if (curPl.getAllChannels().contains(channel)) {
                CyniChat.printDebug("Sending message to " + online[i].getDisplayName());
                String outing = chatChannel.getColour() + "[IRC] [" + chatChannel.getNick() + "] ";
                outing += player + " : " + message;

                online[i].sendMessage(outing);
            }

        }
    }

    private void _handleBungeeMessage(String player, String channel, String message) {

        Channel chatChannel = DataManager.getChannel(channel);
        if(chatChannel == null){
            CyniChat.printDebug("Dropped bungee message from unknown channel " + channel + ":: " + player + " said " + message);
            return;}
        
        String formattedMsg = chatChannel.getColour() + "[" + chatChannel.getNick() + "] " + player + " : " + chatChannel.getColour() + message;
        

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (DataManager.getOnlineDetails(p).getAllChannels().contains(channel)) {
                p.sendMessage(formattedMsg);
            }
        }
    }
}