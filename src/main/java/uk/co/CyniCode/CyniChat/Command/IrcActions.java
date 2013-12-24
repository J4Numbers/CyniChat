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

package uk.co.CyniCode.CyniChat.Command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;
import uk.co.CyniCode.CyniChat.routing.ChatRouter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A class to do all the relevant things in accordance with
 * commands that have been handed down from CyniCord.
 *
 * @author Cynical
 */
public class IrcActions {

	public static void extCommands( String instruct, String player,
									String channel, String retChannel ) {

		String result;
		boolean successful = false;

		if ( instruct.equals( "list" ) ) {

			CyniChat.printDebug( "List key used" );

			if ( channel.equals( "ALL" ) ) {

				CyniChat.printDebug( "All key used" );

				//Otherwise, it's all the players that are on the
				// server full stop.
				Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();

				//Get the set of their names
				Set<String> players = users.keySet();

				//And, once again, make a template of the output
				result = "Players online : ";

				//Then, for everyone who is online,
				for ( String thisOne : players )

					//Add them to that template's output
					result += thisOne + ", ";

				successful = true;

			} else {

				CyniChat.printDebug( "Individual channel used" );

				if ( CyniChat.data.getLinkedChans().containsKey( channel ) ) {

					//Let's get the channel that we're listing out
					Channel listChan = CyniChat.data.getChannel(
							CyniChat.data.getLinkedChans().get( channel ) );

					//And all the users online to be in said channel
					Map<String, UserDetails> users = CyniChat.data.getOnlineUsers();

					//Make a nice template for the bot's output
					result = "Players online in "+listChan.getName()+" : ";

					//Then for everyone who is online...
					for ( Map.Entry< String, UserDetails > thisOne
							: users.entrySet() ) {

						//Tell the console that they are online
						CyniChat.printDebug( thisOne.getKey()
								+ " is online... checking channel : "
								+ listChan.getName() );

						//And what channels they are in
						CyniChat.printDebug( thisOne.getValue().getAllVerboseChannels() );

						//Then, if they are in this channel...
						if ( thisOne.getValue().getAllChannels().contains( listChan.getName() ) ) {

							//Debug that fact
							CyniChat.printDebug( "They're in the channel... adding to string" );

							//And add them to the final message
							result += thisOne.getValue() + ", ";

						}

					}

					successful = true;

				} else {

					result = "The channel was not on the server";

				}

			}

		} else if ( instruct.equals( "kick" ) ) {

			CyniChat.printDebug( "Kick key used" );

			//And get the details of the person we're kicking
			UserDetails kicking = CyniChat.data.getDetails( player );

			//If they're even there that is...
			if ( kicking == null ) {

				result = "player not found";

			} else {

				//Then get the channel that we're going to try and kick them from
				Channel kickChan = CyniChat.data.getChannel(
						CyniChat.data.getLinkedChans().get( channel ) );

				//If that doesn't exist...
				if ( kickChan == null ) {

					//Then something is kinda fecked
					result = "Channel is not on this server";

				} else {

					//Is the player online?
					if ( kicking.getPlayer() != null ) {

						//Apparently so.
						CyniChat.printDebug( "Kicking part three..." );

						//Actually kick them from the channel
						if ( kicking.kick( "IRC moderator", kickChan ) ) {

							//Then celebrate by saying that they've been kicked
							result = "The player has been kicked...";
							successful = true;

						} else {

							//The player wasn't in the channel... crafty bugger
							result = "The player was not in the channel...";
							successful = true;

						}

					} else {

						//The player wasn't even online
						result = "This player is not online...";

					}

				}

			}

		} else if ( instruct.equals( "mute" ) ) {

			CyniChat.printDebug( "Mute key used" );

			//Get the person that we're meant to be un/muting
			UserDetails muting = CyniChat.data.getDetails( player );

			//Then ask if they even exist
			if ( muting == null ) {

				//If they don't, tell the bot that they don't
				result = "There is no such player...";

			} else {

				//Then get the channel that they are being freed from
				Channel muteChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );

				//If the channel doesn't exist...
				if ( muteChan == null ) {

					result = "This channel does not exist on this server";

				} else {

					//Okay, then we ask if the player was online
					if ( muting.getPlayer() != null ) {

						//And try to mute them
						if ( muting.addMute( "IRC moderator", muteChan ) ) {

							//Succeed, and celebrate.
							result = "The player has been muted in this channel...";
							successful = true;

						} else {

							//Or fail slightly
							result = "The player was already muted in this channel...";
							successful = true;

						}

					} else {

						result = "The player was not online";

					}

				}

			}

		} else if ( instruct.equals( "unmute" ) ) {

			CyniChat.printDebug( "Unmute key used" );

			//Get the person that we're meant to be un/muting
			UserDetails muting = CyniChat.data.getDetails( player );

			//Then ask if they even exist
			if ( muting == null ) {

				//If they don't, tell the bot that they don't
				result = "There is no such player";

			} else {

				//Then get the channel that they are being freed from
				Channel muteChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );

				//If the channel doesn't exist...
				if ( muteChan == null )

					result = "This channel is not on this server";

				else {

					//Okay, then we ask if the player was online
					if ( muting.getPlayer() != null ) {

						//Since we're unmuting, try to remove the mute
						if ( muting.remMute( "IRC moderator", muteChan ) ) {

							//And be successful
							result = "The player has been unmuted in the channel...";
							successful = true;

						} else {

							result = "The player was not muted in the first place...";
							successful = true;

						}

					} else {

						result = "This player was not online";

					}

				}

			}

		} else if ( instruct.equals( "ban" ) ) {

			CyniChat.printDebug( "Ban key used" );

			//Get the person that this is happening to
			UserDetails banning = CyniChat.data.getDetails( player );

			//And if they even exist
			if ( banning == null ) {

				//Since they don't say so
				result = "There is no such player...";

			} else {

				//Then get the channel we're banning in
				Channel banChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );

				//And ask if that is there
				if ( banChan == null )

					result = "The channel doesn't exist on this server";

				else {

					//Then we ask if they are online
					if ( banning.getPlayer() != null ) {

						//Well... sorry mate, you're being banned
						if ( banning.newBan( "IRC moderator", banChan ) ) {

							//Yeah... sorry.
							result = "The player has been banned in the channel...";
							successful = true;

						} else {

							//Or not... apparently you get to speak another day
							result = "The player was already banned in the channel...";
							successful = true;

						}

					} else {

						result = "The player was not online";

					}

				}

			}

		//UNBAN
		} else {

			CyniChat.printDebug( "Unban key used" );

			//Get the person that this is happening to
			UserDetails banning = CyniChat.data.getDetails( player );

			//And if they even exist
			if ( banning == null ) {

				//Since they don't say so
				result = "There is no such person...";

			} else {

				//Then get the channel we're banning in
				Channel banChan = CyniChat.data.getChannel( CyniChat.data.getLinkedChans().get( channel ) );

				//And ask if that is there
				if ( banChan == null )

					//If not... cry
					result = "The channel doesn't exist on this server";

				else {

					//Then we ask if they are online
					if ( banning.getPlayer() != null ) {

						//Since we're unbanning, remove the ban
						// in this channel
						if ( banning.remBan( "IRC moderator", banChan ) ) {

							//And let the player rejoice
							result = "The player has been unbanned from the channel...";
							successful = true;

						} else {

							result = "The player was already unbanned in the channel...";
							successful = true;

						}

					} else {

						result = "The player was not online";

					}

				}

			}

		}

		CyniChat.printDebug( "Result = " + result );

		CyniChat.printDebug( "Success = " + successful );

		if ( successful )

			sendReply( result, retChannel );

	}

	public static void sendReply( String reply, String returnChannel ) {

		try {

			ChatRouter.EndpointType type = ChatRouter.EndpointType.IRC;

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeInt(type.ordinal());// typeId

			//Add the basic details to what we're transmitting
			out.writeUTF( reply );
			out.writeUTF( returnChannel );

			//Make a data stream of the byte stream
			ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
			DataOutputStream msg = new DataOutputStream(msgBytes);

			//And put in the details of where it's going
			msg.writeUTF("Forward");
			msg.writeUTF("ALL");
			msg.writeUTF("CyniCordReply");

			//Push message content in
			msg.writeShort(b.toByteArray().length);
			msg.write(b.toByteArray());

			//And get a random player to send on as
			Player p = Bukkit.getOnlinePlayers()[0];
			CyniChat.printDebug( msgBytes.toString() );

			//Then send the message through the random player
			p.sendPluginMessage( CyniChat.self, "BungeeCord", msgBytes.toByteArray());

			//This bit might lie...
			CyniChat.printDebug("Message sent!");

		} catch (IOException ex) {

			//Error...
			CyniChat.printSevere("Error sending message to BungeeChannelProxy");
			ex.printStackTrace();

		}

	}

}
