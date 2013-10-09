/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.CyniCode.CyniChat.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acts as a central hub for routing messages to various IChatRouters
 *
 * @author James
 */
public class ChatRouter {
    
    public enum EndpointType{
        PLAYER,
        BUNGEE,
        IRC
    }

    private static Map<EndpointType,IChatEndpoint> routers = new HashMap<EndpointType,IChatEndpoint>(10);

    public static void addRouter(EndpointType type,IChatEndpoint router) {
        routers.put(type, router);
    }

    /**
     * Routes a message to all routers under it.
     *
     * @param from
     * @param player
     * @param channel
     * @param message
     */
    public static void routeMessage(EndpointType type, String player, String channel, String message) {
        for (Map.Entry<EndpointType, IChatEndpoint> router : routers.entrySet()) {
            if (router.getKey() != type) {
                router.getValue().giveMessage(type, player, channel, message);
            }
        }
    }
}
