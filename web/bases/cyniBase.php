<?php

class cyniBase {

private $mysqli;
private $prefix;

public function __construct() {
	
	require_once '/var/www/chatter/config/config.php';
	
	$mysqli = new mysqli( DATA_HOST, DATA_USER, DATA_PASS, DATA_BASE );
	
	if ($mysqli->connect_errno) {
		error_log( "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error . "\n" );
		exit;
	}
	$this->prefix = DATA_PREFIX;
	$this->mysqli = $mysqli;
	
}

public function __destruct() {
	$this->mysqli->close();
}


}