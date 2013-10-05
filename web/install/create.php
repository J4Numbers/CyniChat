<?php

require_once '../config/config.php';

if ( DATA_STORAGE == "mysql" ) {

$mysqli = new mysqli( DATA_HOST, DATA_USER, DATA_PASS, DATA_BASE );
if ($mysqli->connect_errno) {
	error_log( "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error . "\n" );
	exit;
}
$prefix = DATA_PREFIX;

$install = $mylsqi->prepare( "IF NOT EXISTS (SELECT * FROM `".$prefix."users`)
								CREATE TABLE `".$prefix."users` ( 
									`userID` int not null,
									`username` varchar(32) not null,
									`userPass` varchar(64) not null,
									`userPTime` int not null,
									`userMail` varchar(128) not null,
									`userPrivs` int not null,
									PRIMARY KEY(`userID`)
								)" );
if ( !$install->execute() ) {
	error_log( "Failed to generate new table... : (".$install->errno.") ".$install->error );
	exit;
}

$time = time();

} else {
	try {
		$channels = file_get_contents( DATA_PATH . "/plugins/CyniChat/channels.json" );
	} catch ( Exception $e ) {
		error_log( "Failed to get items... : " . $e->getMessage() );
		exit;
	}
}

?>