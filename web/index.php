<?php

require_once 'config/config.php';
require_once /*HOME_DIR.*/"bases/cyniBase.php";
require_once /*HOME_DIR.*/"bases/cyniFile.php";
require_once /*HOME_DIR.*/"funcs/template.php";

/*if ( file_exists( "install/" ) ) {
	header( "Location: install/create.php" );
}*/

$base = ( DATA_STORAGE === "mysql" ) ? new cyniBase() : new cyniFile();
$pg = new pageTemplate( "index.html" );

$output = "<select class='player'><option selected></option>";

foreach ( $base->getAllPlayers() as $player )
	$output .= "<option value='$player'>$player</output>";

$output .= "</select><button onClick='getPlayer()'>Get</button><span class='out'></span>";

$pg->setContent( "HEAD", "" );
$pg->setContent( "CONTENT", $output );

$pg->sendContent();

?>