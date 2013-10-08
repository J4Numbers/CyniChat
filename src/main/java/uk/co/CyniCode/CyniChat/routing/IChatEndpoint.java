/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.CyniCode.CyniChat.routing;

/**
 * Represents a destination for chat messages to go to.
 * This can be to players on a server (filtered by channel), an IRC bot, BungeeCord proxy, or logging utilities
 * Each endpoint is responsible for messages to itself from itself, these will not be sent to it by ChatRouter
 * @author James
 */
public interface IChatEndpoint {
    
    /**
     * Called when a message is given to this router
     * @param from IChatEndpoint this message comes from
     * @param player name of player for this message. Note player may not be registered in CyniChat user files.
     * @param channel channel this message comes from, this is a CyniChat channel name, not IRC etc channel
     * @param message Actual content of message
     */
    public void giveMessage(IChatEndpoint from,String player,String channel,String message);
}
