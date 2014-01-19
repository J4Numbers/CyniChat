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

//if ( file_exists("../config/props.php") )
//	header("Location: ../");

$home_dir = getcwd()."/..";

require_once "$home_dir/function/page_generation.php";
require_once "$home_dir/function/function.php";

$pg = new pageTemplate( "installer.htm", $home_dir );

$menu = array();

$menu["Index"] = "#";

if (!isInstalled())
	$menu["Install/Repair"] = "install.php";
else
	$menu["Re-install"] = "reinstall.php";

foreach ( $menu as $name => $link )
	$pg->appendTag("MENU",
		"<a href='./$link' class='menuItem menuLink' >$name</a>");

$body = "<div class='newsarticle_text'>Welcome to the CyniForum install/repair panel. Please select your relevant option in the headers above. Please note: If you are attempting to re-install the whole forums, you will need to provide your administrator username and password. For initial set-up, you will be providing one for yourself.</div>";

$pg->setTag( "LOCATION", ".." );
$pg->setTag( "TITLE", "Cyni Forums Installation" );
$pg->setTag( "BODY", "<div class='newsarticle'>$body</div>" );
$pg->setTag( "HEAD", "<img src='../images/forum_logo.png' class='logo' />" );
$pg->setTag( "FOOT", "" );

$pg->showPage();

?>