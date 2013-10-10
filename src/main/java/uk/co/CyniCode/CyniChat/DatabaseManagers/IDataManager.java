package uk.co.CyniCode.CyniChat.DatabaseManagers;

import java.util.Map;

import uk.co.CyniCode.CyniChat.CyniChat;
import uk.co.CyniCode.CyniChat.objects.Channel;
import uk.co.CyniCode.CyniChat.objects.UserDetails;

public interface IDataManager {
	
	public boolean startConnection( CyniChat plugin );

	public void saveChannels( Map<String, Channel> channels );
	
	public void saveUsers( Map<String, UserDetails> loadedPlayers );
	
	public Map<String, UserDetails> returnPlayers();
	
	public Map<String, Channel> returnChannels();
	
}
