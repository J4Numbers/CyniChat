/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.CyniCode.CyniChat.routing;

/**
 * Represents a destination for chat messages to go to.
 * This can be to players on a server (filtered by channel), an IRC bot, BungeeCord proxy, or logging utilities
 * @author James
 */
public interface IChatEndpoint {
    
    /**
     * Called when a message is given to this router
     * @param from IChatEndpoint this message comes from
     * @param player name of player for this message
     * @param channel channel this message comes from
     * @param message Actual content of message
     */
    public void giveMessage(IChatEndpoint from,String player,String channel,String message);
}
