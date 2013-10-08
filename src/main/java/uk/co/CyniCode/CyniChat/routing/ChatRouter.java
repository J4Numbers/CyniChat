/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.CyniCode.CyniChat.routing;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a central hub for routing messages to various IChatRouters
 * @author James
 */
public class ChatRouter {
    
    private static List<IChatEndpoint> routers = new ArrayList<IChatEndpoint>(10);
    
    public static void addRouter(IChatEndpoint router){
        routers.add(router);
    }
    
    /**
     * Routes a message to all routers under it.
     * @param from
     * @param player
     * @param channel
     * @param message 
     */
    public static void routeMessage(IChatEndpoint from,String player,String channel,String message){
        for(IChatEndpoint router : routers){
            router.giveMessage(from, player, channel, message);
        }
    }
    
}
