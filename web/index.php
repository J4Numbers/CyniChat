<?php

require_once 'config/config.php';
require_once HOME_DIR."bases/cyniBase.php";
require_once HOME_DIR."funcs/template.php";

if ( file_exists( "install/" ) ) {
	header( "Location: install/create.php" );
}

$base = new cyniBase();
$pg = new pageTemplate( "index.html" );

?>