<?php

require_once '../config/config.php';
require_once /*HOME_DIR.*/"../bases/cyniBase.php";
require_once /*HOME_DIR.*/"../bases/cyniFile.php";

$base = ( DATA_STORAGE === "mysql" ) ? new cyniBase() : new cyniFile();

$player = $base->getPlayer( $_POST['player'] );

$out = "<table>";

foreach ( $player as $key => $val ) {
	
	$outer = "";
	
	if ( is_array( $val ) ) {
		foreach ( $val as $vel )
			$outer .= $vel . ", ";
	} else $outer = $val;
	
	$out .= "<tr><td>$key</td><td>$outer</td></tr>";
}

$out .= "</table>";
echo $out;

?>