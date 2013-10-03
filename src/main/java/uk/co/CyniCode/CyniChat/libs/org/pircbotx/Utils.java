/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.CyniCode.CyniChat.libs.org.pircbotx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.ActionEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.FileTransferFinishedEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.FingerEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.IncomingChatRequestEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.IncomingFileTransferEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.InviteEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.JoinEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.KickEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.MessageEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.ModeEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.NickChangeEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.NoticeEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.OpEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.PartEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.PingEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.PrivateMessageEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.QuitEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveChannelBanEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveModeratedEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemovePrivateEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveSecretEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetChannelBanEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetChannelKeyEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetChannelLimitEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetInviteOnlyEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetModeratedEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetPrivateEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetSecretEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.SetTopicProtectionEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.TimeEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.TopicEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.UserModeEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.VersionEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.events.VoiceEvent;
import uk.co.CyniCode.CyniChat.libs.org.pircbotx.hooks.Event;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Utils {
	/**
	 * Extract the source user from any Event. <b>Warning:</b> This will not work
	 * on events that return the user as a string like {@link InviteEvent}
	 * @param event An event to get information from
	 * @return The object of the user or null if the event doesn't have a source
	 */
	public static User getUser(Event event) {
		if (event == null)
			return null;
		else if (event instanceof ActionEvent)
			return ((ActionEvent) event).getUser();
		else if (event instanceof FileTransferFinishedEvent)
			return ((FileTransferFinishedEvent) event).getTransfer().getUser();
		else if (event instanceof FingerEvent)
			return ((FingerEvent) event).getUser();
		else if (event instanceof IncomingChatRequestEvent)
			return ((IncomingChatRequestEvent) event).getChat().getUser();
		else if (event instanceof IncomingFileTransferEvent)
			return ((IncomingFileTransferEvent) event).getTransfer().getUser();
		else if (event instanceof JoinEvent)
			return ((JoinEvent) event).getUser();
		else if (event instanceof KickEvent)
			return ((KickEvent) event).getSource();
		else if (event instanceof MessageEvent)
			return ((MessageEvent) event).getUser();
		else if (event instanceof ModeEvent)
			return ((ModeEvent) event).getUser();
		else if (event instanceof NickChangeEvent)
			return ((NickChangeEvent) event).getUser();
		else if (event instanceof NoticeEvent)
			return ((NoticeEvent) event).getUser();
		else if (event instanceof OpEvent)
			return ((OpEvent) event).getSource();
		else if (event instanceof PartEvent)
			return ((PartEvent) event).getUser();
		else if (event instanceof PingEvent)
			return ((PingEvent) event).getUser();
		else if (event instanceof PrivateMessageEvent)
			return ((PrivateMessageEvent) event).getUser();
		else if (event instanceof QuitEvent)
			return ((QuitEvent) event).getUser();
		else if (event instanceof RemoveChannelBanEvent)
			return ((RemoveChannelBanEvent) event).getUser();
		else if (event instanceof RemoveChannelKeyEvent)
			return ((RemoveChannelKeyEvent) event).getUser();
		else if (event instanceof RemoveChannelLimitEvent)
			return ((RemoveChannelLimitEvent) event).getUser();
		else if (event instanceof RemoveInviteOnlyEvent)
			return ((RemoveInviteOnlyEvent) event).getUser();
		else if (event instanceof RemoveModeratedEvent)
			return ((RemoveModeratedEvent) event).getUser();
		else if (event instanceof RemoveNoExternalMessagesEvent)
			return ((RemoveNoExternalMessagesEvent) event).getUser();
		else if (event instanceof RemovePrivateEvent)
			return ((RemovePrivateEvent) event).getUser();
		else if (event instanceof RemoveSecretEvent)
			return ((RemoveSecretEvent) event).getUser();
		else if (event instanceof RemoveTopicProtectionEvent)
			return ((RemoveTopicProtectionEvent) event).getUser();
		else if (event instanceof SetChannelBanEvent)
			return ((SetChannelBanEvent) event).getUser();
		else if (event instanceof SetChannelKeyEvent)
			return ((SetChannelKeyEvent) event).getUser();
		else if (event instanceof SetChannelLimitEvent)
			return ((SetChannelLimitEvent) event).getUser();
		else if (event instanceof SetInviteOnlyEvent)
			return ((SetInviteOnlyEvent) event).getUser();
		else if (event instanceof SetModeratedEvent)
			return ((SetModeratedEvent) event).getUser();
		else if (event instanceof SetNoExternalMessagesEvent)
			return ((SetNoExternalMessagesEvent) event).getUser();
		else if (event instanceof SetPrivateEvent)
			return ((SetPrivateEvent) event).getUser();
		else if (event instanceof SetSecretEvent)
			return ((SetSecretEvent) event).getUser();
		else if (event instanceof SetTopicProtectionEvent)
			return ((SetTopicProtectionEvent) event).getUser();
		else if (event instanceof TimeEvent)
			return ((TimeEvent) event).getUser();
		else if (event instanceof TopicEvent)
			return ((TopicEvent) event).getUser();
		else if (event instanceof UserModeEvent)
			return ((UserModeEvent) event).getSource();
		else if (event instanceof VersionEvent)
			return ((VersionEvent) event).getUser();
		else if (event instanceof VoiceEvent)
			return ((VoiceEvent) event).getSource();
		return null;
	}

	public static String join(Collection<String> strings, String sep) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> itr = strings.iterator();
		while (itr.hasNext()) {
			builder.append(itr.next());
			if (itr.hasNext())
				builder.append(sep);
		}
		return builder.toString();
	}

	/**
	 * Tokenize IRC raw input into it's components, keeping the
	 * 'sender' and 'message' fields intact.
	 * @param input A string in the format [:]item [item] ... [:item [item] ...]
	 * @return List of strings.
	 */
	public static List<String> tokenizeLine(String input) {
		List<String> retn = new ArrayList<String>();

		if (input == null || input.length() == 0)
			return retn;

		String temp = input;

		while (true) {
			if (temp.startsWith(":") && retn.size() > 0) {
				retn.add(temp.substring(1));

				return retn;
			}

			String[] split = temp.split(" ", 2);
			retn.add(split[0]);

			if (split.length > 1)
				temp = split[1];
			else
				break;
		}

		return retn;
	}
}
