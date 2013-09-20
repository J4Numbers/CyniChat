Instructions for Use
==============================

A Note From the Author
-----------------------

Hello.

The name's Cynical (or CyniCode; dependant on who you listen to) and thank you for choosing CyniChat. This is a chat channel plugin with a likeness to the popular option, HeroChat. In fact, the majority of the plguin is extremely similar to it, containing a similar command and permission structure.

So why choose this plugin?

Well something must have grabbed your attention if you're looking a this text. So what is actually different from HeroChat?

- This plugin has a much more verbose command system
- This plugin can use either SQL or JSON storage
- This plugin creates ONE file for all the players, and ONE file for all the channels (JSON storage)

Some people are probably wondering what HeroChat is by this point... if you haven't heard of it already that is. To paraphrase, HeroChat is a chat channel plugin which gives server owners the ability to create multiple chat channels on a server and to moderate them as they see fit. Yet it has a number of glaring flaws in its design which I, and a few others, have found to be undesirable.

Such flaws include:
- Giving one file to EVERY player and one file to EVERY channel created
- Some of the commands are somewhat counter-intuitive (banning someone twice to unban them)
- A lack of storage choices
- The plugin itself being closed source

Admittedly the last point contributes to the others. Since it is closed source, it doesn't allow for any changes to be made for any prospective server owners. This is where CyniChat is different. Not only does it give everything that HeroChat does, along with the points detailed above, it allows for people to customise it as they see fit.

But you won't believe me, why not try it and see what you think.
~Cynical

Installation
----------------

To install CyniChat, download the jar and unzip the file (oh look, you've already done that :) ). Copy the file into your bukkit plugins folder.

**Please Note:** You will also need Vault for this plugin to run correctly along with a permissions plugin of your choice.

Start up the server. A lot of errors will initially flash up on your screen, telling you that an SQL connection could not be made. Shut down the server and open up the bukkit plugins folder. Look inside the CyniChat folder and open up the config where you can edit everything to your specification. The data option currently has two options, MySQL or JSON.

Once you have changed the config to suit your needs, start up the server again.

If any errors which are not down to your own server issues occur, please send a message to me at admin(at)cynicode.co.uk

Commands
-----------------

- /ch <channel> [password] -> Changes the current channel or joins a new one
- /ch join <channel> [password] -> alias of /ch
- /ch leave [channel] -> Leaves the current channel, or the defined channel
- /ch qm <channel> <message> -> Sends one message to the defined channel
- /ch ignore <player> -> Ignores a named player
- /ch hear <player> -> Unignores a named player
- /msg <player> <message> -> Sends a message to one player
- /r [message] -> Sends a reply to one player if one message has been sent before
- /ch list [page#] -> Lists all the channels
- /ch who [channel] -> Shows all the players inside a channel
- /afk [message] -> Sets a user as afk (cannot receive private messages)
- /me [message] -> Transmits an action in the context of the user
- /ch create <name> [nick] -> Creates a channel with the defined name and optional nickname
- /ch remove <channel> -> Removes a named channel
- /ch info [channel] -> Prints the info about one channel
- /ch gmute <player> -> Globally mutes one player across all channels
- /ch gunmute <player> -> Globally unmutes one player across all channels
- /ch mute <player> [channel] -> Mutes one player in either the current channel, or the defined channel
- /ch unmute <player> [channel] -> Unmutes one player in either the current, or the defined channel
- /ch kick <player> [channel] -> Kicks the player in either the current or the defined channel
- /ch ban <player> [channel] -> Bans the player in the current, or the defined channel
- /ch unban <player> [channel] -> Unbans the player in the current, or the defined channel
- /ch promote <player> [channel] -> Promotes the player to mod in the current, or the defined channel
- /ch demote <player> [channel] -> Demotes the Player from mod in the current, or the defined channel
- /ch set <channel> <node> <value> -> Changes a node (color, password or description) to a value you specify.
- /ch save -> Saves the information about all the players and channels
- /ch reload -> Saves the information about all the players and channels, then reloads them
- /ch help [page#] -> Lists help pages [1 - #]