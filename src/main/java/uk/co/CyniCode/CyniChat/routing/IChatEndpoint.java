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

import uk.co.CyniCode.CyniChat.routing.ChatRouter.EndpointType;

/**
 * Represents a destination for chat messages to go to.
 * This can be to players on a server (filtered by channel),
 * an IRC bot, BungeeCord proxy, or logging utilities. Each 
 * endpoint is responsible for messages to itself from itself,
 * these will not be sent to it by ChatRouter
 * 
 * @author Tehbeard
 * @author CyniCode
 */
public interface IChatEndpoint {
	
	/**
	 * Called when a message is given to this router
	 * @param type IChatEndpoint this message comes from
	 * @param player name of player for this message. Note player may not be registered in CyniChat user files.
	 * @param channel channel this message comes from, this is a CyniChat channel name, not IRC etc channel
	 * @param message Actual content of message
	 */
	public void giveMessage(EndpointType type,String player,String channel,String message);
	
}