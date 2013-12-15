package uk.co.CyniCode.CyniChat.DatabaseManagers;

import java.util.Map;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

/**
 * An interface for all the potential types of data that we're going
 * to handle at some point or another.
 * 
 * @author CyniCode
 */
public interface IDataManager {
	/**
	 * We have to start each instance of the data methods
	 * @param plugin : The instance of the plugin from which we can grab the configs
	 * @return true when complete
	 */
	public boolean startConnection( CyniChat plugin );
	
	/**
	 * We're going to have to have a method to save the channels somewhere
	 * @param channels : The channel object that we're going to save into the data
	 */
	public void saveChannels( Map<String, Channel> channels );
	
	/**
	 * All the users are also going to have to be saved
	 * @param loadedPlayers : Save these people. Save them from the monsters.
	 */
	public void saveUsers( Map<String, UserDetails> loadedPlayers );
	
	/**
	 * And conversely, we're going to have to load the data from storage
	 * @return UserDetails of everyone that is available in storage
	 */
	public Map<String, UserDetails> returnPlayers();
	
	/**
	 * Load all the data about the channels from storage
	 * @return Channel objects of every single channel
	 */
	public Map<String, Channel> returnChannels();
	
	/**
	 * At some point, we're going to want to boost the connections
	 * so that they don't get tired out and cancel on us.
	 * @return the runnable object that we want to give to bukkit
	 *  for it to schedule
	 */
	public Runnable getBooster();
	
}