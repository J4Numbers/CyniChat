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

$home_dir = getcwd()."/..";

require_once "$home_dir/function/page_generation.php";
require_once "$home_dir/function/function.php";

$pg = new pageTemplate( "installer.htm", $home_dir );

$menu = array();

$menu["Index"] = "index.php";

//if (isInstalled())
//	header("Location: index.php");

$menu["Install"] = "#";

foreach ( $menu as $name => $link )
	$pg->appendTag("MENU",
		"<a href='./$link' class='menuItem menuLink' >$name</a>");

$body1  = "<div class='newsarticle_header' ><h1>Installation</h1></div>";
$body1 .= "<div class='newsarticle_text'>Welcome to the installation page. We're going to need a bit of information about you before we get started; please make sure you have it all to hand.</div>";

$table = "<table class='table_of_things' >
			<tr>
				<td class='field_name' >Database Host</td>
				<td><input type='text' class='input' onblur='checkDbHost(\"..\")' id='user_reg_db_host'
					 placeholder='localhost' /></td>
				<td id='reg_db_host_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Database Port</td>
				<td><input type='text' class='input' onblur='checkDbPort(\"..\")' id='user_reg_db_port'
					 placeholder='3306'
				 /></td>
				<td id='reg_db_port_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Database User</td>
				<td><input type='text' class='input' onblur='checkDbUser(\"..\")' id='user_reg_db_user'
					 placeholder='root' /></td>
				<td id='reg_db_user_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Database Password</td>
				<td><input type='password' class='input' onblur='checkDbPass(\"..\")' id='user_reg_db_pass'
					 placeholder='********' /></td>
				<td id='reg_db_pass_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Database Name</td>
				<td><input type='text' class='input' onblur='checkDbName(\"..\")' id='user_reg_db_name'
					 placeholder='CyniForum' /></td>
				<td id='reg_db_name_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Database Prefix</td>
				<td><input type='text' class='input' onblur='checkDbPref(\"..\")' id='user_reg_db_prefix'
					 placeholder='forum_' /></td>
				<td id='reg_db_prefix_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Domain Name</td>
				<td><input type='text' class='input' onblur='checkDomain(\"..\")' id='user_reg_domain'
					 placeholder='http://www.example.com' /></td>
				<td id='reg_domain_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Install Location</td>
				<td><input type='text' class='input' onblur='checkInstLoc(\"..\")' id='user_reg_inst_loc'
					 placeholder='http://www.example.com/forum/' /></td>
				<td id='reg_inst_loc_res' ></td>
			</tr>
			<tr>
				<td class='field_name' >Numeric Constant</td>
				<td><input type='text' class='input' onblur='checkConstant(\"..\")' id='user_reg_num_const'
					 placeholder='52' /></td>
				<td id='reg_num_const_res' ></td>
			</tr>
			<tr>
				<td class='buttons' id='conn_result' ></td>
				<td class='buttons' >
					<button onclick='testConn(\"..\")' class='button' >Test Connection</button>
					<button onclick='submitConnData(\"..\")' class='button' >Submit</button>
				</td>
			</tr>
		</table>";

$body2  = "<div class='newsarticle_header' ><h1>Information</h1></div>";
$body2 .= "<div class='newsarticle_text'>$table</div>";


//TODO: Get the initial information required to generate a configuration file
// Database ( name, password, host, username ), Numerical constant for hashing,
// Local URL, Global URL, their username & pass

//TODO: Store that information in a props file that we make AFTER testing the SQL connection information they provide

//TODO: Generate the database based on that information and hope it all goes to plan

$pg->setTag("HEAD", "<img src='../images/forum_logo.png' class='logo' />");
$pg->setTag("LOCATION", "..");
$pg->setTag("TITLE", "Installation");
$pg->setTag("BODY", "<div class='newsarticle'>$body1</div><div class='newsarticle'>$body2</div>");
$pg->setTag("FOOT", "");

$pg->showPage();