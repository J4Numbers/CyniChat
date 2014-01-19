<?php

/**
 * Copyright 2014 Matthew Ball (CyniCode/M477h3w1012)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

if ( isset($_POST['host']) && isset($_POST['port']) && isset($_POST['user']) &&
	isset($_POST['pass']) && isset($_POST['name']) && isset($_POST['prefix']) &&
	isset($_POST['domain']) && isset($_POST['instLoc']) && isset($_POST['constant']) ) {

	$file = "../config/props.php";

	if ( file_get_contents($file)===true ) die("file exists error");

	$filer = fopen( $file, "w" );

	if ( $filer === false )
		die("perm error");

	fwrite( $filer, sprintf('define( "DATAHOST", "%s" );\n', $_POST['host']) );
	fwrite( $filer, sprintf('define( "DATAPORT", "%d" );\n', $_POST['port']) );
	fwrite( $filer, sprintf('define( "DATAUSER", "%s" );\n', $_POST['user']) );
	fwrite( $filer, sprintf('define( "DATAPASS", "%s" );\n', $_POST['pass']) );
	fwrite( $filer, sprintf('define( "DATABASE", "%s" );\n', $_POST['name']) );
	fwrite( $filer, sprintf('define( "DATAPFIX", "%s" );\n', $_POST['prefix']) );
	fwrite( $filer, sprintf('define( "DATACONST", "%d" );\n\n', $_POST['constant']) );
	fwrite( $filer, sprintf('$home_dir = "$s";\n', $_POST['instLoc']) );
	fwrite( $filer, sprintf('$domain = "$s";\n', $_POST['domain']) );

	fclose( $filer );

	die("successful");

}

die("data error");