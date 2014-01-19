/**
 * Copyright 2013 CyniCode (numbers@cynicode.co.uk).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.CyniCode.CyniChat.routing;

import java.util.HashMap;
import java.util.Map;

import uk.co.CyniCode.CyniChat.CyniChat;

/**
 * Acts as a central hub for routing messages to various IChatRouters
 *
 * @author Tehbeard
 * @author CyniCode
 */
public class ChatRouter {
	
	/**
	 * These are all the possible end-types
	 * PLAYER is for a specific player... I think
	 * BUNGEE is flagged for transfer across the bungee servers
	 * IRC is flagged for transfer to the IRC listeners
	 */
	public enum EndpointType{
		PLAYER,
		BUNGEE,
		IRC
	}
	
	/**
	 * These are all the possible routers of the chat that we could have
	 */
	private static Map<EndpointType,IChatEndpoint> routers = new HashMap<EndpointType,IChatEndpoint>(10);
	
	/**
	 * Add a router into the equation 
	 * @param type
	 * @param router
	 */
	public static void addRouter(EndpointType type,IChatEndpoint router) {
		routers.put(type, router);
	}
	
	/**
	 * Routes a message to all routers under it.
	 *
	 * @param type
	 * @param player
	 * @param channel
	 * @param message
	 */
	public static void routeMessage(EndpointType type, String player, String channel, String message) {
		
		//What endpoint have we found here?
		CyniChat.printDebug( "Routing message for type: " + type.name() );
		
		//Go through all our routers
		for (Map.Entry<EndpointType, IChatEndpoint> router : routers.entrySet()) {
			
			//And check what endpoint they have been assigned
			CyniChat.printDebug( "Checking " + router.getKey().name() + " against " + type.name() );
			if (router.getKey() != type) {
				//The endpoint doesn't match, therefore we should pass it on
				CyniChat.printDebug( "Giving the message on..." );
				try {
					router.getValue().giveMessage(type, player, channel, message);
				} catch ( NullPointerException npe ) {}
				}
		}
	}
}