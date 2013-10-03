<?php

require_once '/var/www/chatter/config/config.php';
require_once HOME_DIR."bases/cyniBase.php";
require_once HOME_DIR."funcs/template.php";

$base = new cyniBase();
$pg = new pageTemplate( "index.html" );

?>