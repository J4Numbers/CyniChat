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

function chooseIcon( accepted, loc ) {

	return ( accepted ) ?
		'<img src=\''+loc+'/images/green_tick.png\' style=\'height:16px;\' />' :
		'<img src=\''+loc+'/images/red_cross.png\' style=\'height:16px;\' />';

}

function getHash(loc,d) {

	var hashed;

	if (d)
		$.post(loc+"/phpscripts/hashing.php",
			{hash: document.getElementById("user_log_pass").value,
				name: document.getElementById("user_log_user").value}).done( function(data){
				hashed = data;
			});
	else
		$.post(loc+"/phpscripts/hashing.php",
			{hash: document.getElementById("user_reg_pass").value}).done( function(data){
				hashed = data;
			});

	return hashed;

}

function submitConnData(loc) {

	var conn = testConn(loc);

	console.log( "connection: " + conn );

	if (conn) console.log("First Success");if ( filledIn(loc)) {

		$.post(loc+"/phpscripts/makePropsFile.php",
			{
				host: document.getElementById("user_reg_db_host").value,
				port: document.getElementById("user_reg_db_port").value,
				user: document.getElementById("user_reg_db_user").value,
				pass: document.getElementById("user_reg_db_pass").value,
				name: document.getElementById("user_reg_db_name").value,
				prefix: document.getElementById("user_reg_db_prefix").value,
				domain: document.getElementById("user_reg_domain").value,
				instLoc: document.getElementById("user_reg_inst_loc").value,
				constant: document.getElementById("user_reg_num_const").value
			}).done(function(data){

				if ( data=="successful" ) {
					alert( "Woot!" );
					//location.replace = "generation.php";
				} else if ( data=="perm error" ) {
					alert( "Damn... hoped this wouldn't happen" );
				} else if ( data=="data error" ) {
					alert( "Some important data was not sent" );
				} else if ( data=="file exists error" ) {
					alert( "The properties file is already there" );
				}

			});

	} else {
		alert("Please fill in all the fields");
	}

}

function checkDbHost(loc) {

	var host = document.getElementById("user_reg_db_host").value;

	var acc = ( host.length != 0 );

	document.getElementById("reg_db_host_res").innerHTML = chooseIcon( acc, loc );

	console.log( "Host: " + acc );

	return acc;

}

function checkDbPort(loc) {

	var port = document.getElementById("user_reg_db_port").value;

	var acc = ( port.length != 0 && !isNaN(port) );

	document.getElementById("reg_db_port_res").innerHTML = chooseIcon( acc, loc );

	console.log( "Port: " + acc );

	return acc;

}

function checkDbUser(loc) {

	var user = document.getElementById("user_reg_db_user").value;

	var acc = ( user.length != 0 );

	document.getElementById("reg_db_user_res").innerHTML = chooseIcon( acc, loc );

	console.log( "DB User: " + acc );

	return acc;

}

function checkDbPass(loc) {

	var pass = document.getElementById("user_reg_db_pass").value;

	document.getElementById("reg_db_pass_res").innerHTML = chooseIcon( true, loc );

	console.log( "DB Pass: " + true );

	return true;

}

function checkDbName(loc) {

	var name = document.getElementById("user_reg_db_name").value;
	var reg = new RegExp("[a-zA-Z0-9$_]+");

	var acc = ( reg.test(name) );

	document.getElementById("reg_db_name_res").innerHTML = chooseIcon( acc, loc );

	console.log( "DB Name: " + acc);

	return acc;

}

function checkDbPref(loc) {

	var prefix = document.getElementById("user_reg_db_prefix").value;
	var reg = new RegExp("[a-zA-Z0-9$_]*");

	var acc = ( reg.test(prefix) );

	document.getElementById("reg_db_prefix_res").innerHTML = chooseIcon( acc, loc );

	console.log( "DB prefix: " + acc);

	return acc;

}

function checkDomain(loc) {

	var domain = document.getElementById("user_reg_domain").value;
	var reg = new RegExp("https?:\/\/[[a-zA-Z0-9\.\/_]+[.]{1}[a-z]{2,5}\/?]|localhost\/?");

	var acc = ( reg.test(domain) );

	document.getElementById("reg_domain_res").innerHTML = chooseIcon( acc, loc );

	console.log( "Domain: " + acc);

	return acc;

}

function checkInstLoc(loc) {

	var install = document.getElementById("user_reg_inst_loc").value;
	var reg = new RegExp("https?:\/\/[[a-zA-Z0-9\.\/_]+[.]{1}[a-z]{2,5}]|localhost(\/\w*)*");

	var acc = ( reg.test(install) );

	document.getElementById("reg_inst_loc_res").innerHTML = chooseIcon( acc, loc );

	console.log( "Install Loc: " + acc);

	return acc;

}

function checkConstant(loc) {

	var constant = document.getElementById("user_reg_num_const").value;

	var acc = ( constant.length != 0 && !isNaN(constant) );

	document.getElementById("reg_num_const_res").innerHTML = chooseIcon( acc, loc );

	console.log( "Num Const: " + acc);

	return acc;

}

function filledIn(loc) {

	console.log( "checking fillings" );

	return ( checkDbHost(loc) && checkDbUser(loc) && checkDbPass(loc) &&
			checkDbUser(loc) && checkDbPort(loc) && checkDbPref(loc) &&
			checkDomain(loc) && checkInstLoc(loc) && checkConstant(loc) &&
			checkDbName(loc) );

}

function testConn(loc) {

	$.post(loc+"/phpscripts/testConn.php",
			{host: document.getElementById("user_reg_db_host").value,
			port: document.getElementById("user_reg_db_port").value,
			user: document.getElementById("user_reg_db_user").value,
			pass: document.getElementById("user_reg_db_pass").value,
			name: document.getElementById("user_reg_db_name").value}).done(function(data){

		var acc = (data=="true");
		document.getElementById("conn_result").innerHTML = chooseIcon(acc, loc);

		console.log( "Test Connection: " + acc );

		return acc;

	});

}