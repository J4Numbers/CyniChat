<?php

$sql = file_get_contents( "../sql/startup.sql" );

$database = new database();

try {

	$database->executeSimpleStatement($sql);

} catch (PDOException $ex) {

	echo $ex->getMessage();

}

?>