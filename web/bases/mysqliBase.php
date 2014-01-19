<?php

class mysqli_database {

private $mysqli;
private $prefix;
private $players;
private $channels;

public function __construct() {
	
	require_once '/var/www/chatter/config/config.php';
	
	$mysqli = new mysqli( DATA_HOST, DATA_USER, DATA_PASS, DATA_BASE );
	
	if ($mysqli->connect_errno) {
		error_log( "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error . "\n" );
		exit;
	}
	$this->prefix = DATA_PREFIX;
	$this->mysqli = $mysqli;
	
	$getPlayer = $mysqli->prepare( "SELECT * FROM `".DATA_PREFIX."players` ORDER BY `player_name_clean`" );
	
	if ( !$getPlayer->execute() ) {
		error_log( "Failed to find all players : (".$getPlayer->errno.") ".$getPlayer->error );
		exit;
	}
	
	$getPlayer->store_result();
	$getPlayer->bind_result( $plId, $plUser, $plClean, $plChan, $plSil, $plIgn );
	
	$count = 0;
	$players = Array();
	
	while ( $getPlayer->fetch() ) {
		
		$banned = $mysqli->prepare( "SELECT `channel_name` FROM `".DATA_PREFIX."banned` all 
										INNER JOIN `".DATA_PREFIX."channels` chan ON all.`channel_id`=chan.`channel_id` 
										WHERE all.`player_id`=?" );
		$banned->bind_param( "i", $plId );
		
		if ( !$banned->execute() ) {
			error_log( "Failed to get banned channels ($plId) : (".$banned->errno.") ".$banned->error );
			exit;
		}
		
		$banned->store_result();
		$banned->bind_result( $chan );
		
		$c = 0;
		
		while ( $banned->fetch() ) {
			$bannedChans[$c] = $chan;
			$c++;
		}
		
		$joined = $mysqli->prepare( "SELECT `channel_name` FROM `".DATA_PREFIX."current_channel` all 
										INNER JOIN `".DATA_PREFIX."channels` chan ON all.`channel_id`=chan.`channel_id` 
										WHERE all.`player_id`=?" );
		$joined->bind_param( "i", $plId );
		
		if ( !$joined->execute() ) {
			error_log( "Failed to get joined channels ($plId) : (".$joined->errno.") ".$joined->error );
			exit;
		}
		
		$joined->store_result();
		$joined->bind_result( $chan );
		
		$c = 0;
		
		while ( $joined->fetch() ) {
			$joinedChans[$c] = $chan;
			$c++;
		}
		
		$muted = $mysqli->prepare( "SELECT `channel_name` FROM `".DATA_PREFIX."muted` all 
										INNER JOIN `".DATA_PREFIX."channels` chan ON all.`channel_id`=chan.`channel_id` 
										WHERE all.`player_id`=?" );
		$muted->bind_param( "i", $plId );
		
		if ( !$muted->execute() ) {
			error_log( "Failed to get joined channels ($plId) : (".$muted->errno.") ".$muted->error );
			exit;
		}
		
		$muted->store_result();
		$muted->bind_result( $chan );
		
		$c = 0;
		
		while ( $muted->fetch() ) {
			$mutedChans[$c] = $chan;
			$c++;
		}
		
		$ignoring = $mysqli->prepare( "SELECT `player_name` FROM `".DATA_PREFIX."ignoring` all 
										INNER JOIN `".DATA_PREFIX."players` single ON single.`player_id`=all.`ignoree_id` 
										WHERE all.`ignorer_id`=?" );
		$ignoring->bind_param( "i", $plId );
		
		if ( !$ignoring->execute() ) {
			error_log( "Failed to get joined channels ($plId) : (".$joined->errno.") ".$joined->error );
			exit;
		}
		
		$ignoring->store_result();
		$ignoring->bind_result( $chan );
		
		$c = 0;
		
		while ( $ignoring->fetch() ) {
			$ignoringPeople[$c] = $chan;
			$c++;
		}
		
		$thisPl["id"] = $plId;
		$thisPl["username"] = $plUser;
		$thisPl["CurrentChannel"] = $plChan;
		$thisPl["CanIgnore"] = $plIgnore;
		$thisPl["Silenced"] = $plSil;
		$thisPl["BannedFrom"] = $bannedChans;
		$thisPl["JoinedChannels"] = $joinedChans;
		$thisPl["MutedIn"] = $mutedChans;
		$thisPl["Ignoring"] = $ignoringPeople;
		
		$players[$count] = $thisPl;
		$count++;
	}
	
	$this->players = $players;
}

public function getAllPlayers() {
	$mysqli = $this->mysqli;
	
	$get = $mysqli->prepare( "SELECT `player_name` FROM `".DATA_PREFIX."players` ORDER BY `player_name_clean`" );
	
	if ( !$get->execute() ) {
		error_log( "Failed to get all players : (".$get->errno.") ".$get->error );
		exit;
	}
	
	$get->store_result();
	$get->bind_result( $name );
	
	$players = Array();
	$count = 0;
	
	while ( $get->fetch() ) {
		
		$players[$count] = $name;
		$count++;
		
	}
	
	return $players;
}

public function getAllChannels() {
	$mysqli = $this->mysqli;
	
	$get = $mysqli->prepare( "SELECT `channel_name` FROM `".DATA_PREFIX."channels` ORDER BY `channel_name_clean`" );
	
	if ( !$get->execute() ) {
		error_log( "Failed to get all channels : (".$get->errno.") ".$get->error );
		exit;
	}
	
	$get->store_result();
	$get->bind_result( $name );
	
	$channels = Array();
	$count = 0;
	
	while ( $get->fetch() ) {
		
		$channels[$count] = $name;
		$count++;
		
	}
	
	return $channels;
}

public function getPlayer( $player ) {
	return $this->players[$player];
}

public function getChannel( $channel ) {
}

public function __destruct() {
	$this->mysqli->close();
}


}