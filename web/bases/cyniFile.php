<?php

require_once "$home_dir/config/config.php";

class cyniFile implements database {

	private $players;
	private $channels;

	public function objectToArray($d) {
		if (is_object($d)) {
			// Gets the properties of the given object
			// with get_object_vars function
			$d = get_object_vars($d);
		}

		// Return array
		return $d;
	}

	public function __construct() {

		$players = json_decode( file_get_contents( DATA_PATH . "players.json" , true ) );
		$channels = json_decode( file_get_contents( DATA_PATH . "channels.json" , true ) );

		$this->players = $this->objectToArray( $players );
		$this->channels = $this->objectToArray( $channels );

		//var_dump( $this->players );
		//var_dump( $this->channels );

	}

	public function getAllPlayers() {
		return array_keys( $this->players );
	}

	public function getAllChannels() {
		return array_keys( $this->channels );
	}

	public function getOnePlayer( $player ) {
		return $this->objectToArray( $this->players[$player] );
	}

	public function getOneChannel( $channel ) {
		return $this->objectToArray( $this->channels[$channel] );
	}

	public function setOnePlayer( $player, $options ) {

	}

	public function setOneChannel( $channel, $options ) {

	}

}

?>