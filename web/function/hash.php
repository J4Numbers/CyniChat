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

require_once "../config/props.php";

function hashPhrase( $pass, $d = false ) {

	$t = (!$d) ? time() : $d;

	$string = str_split($pass);

	$i = 0;
	foreach ($string as $gnirtr ) {
		$orded = ord($gnirtr);
		$asci[$i] = (int) $orded;
		$i++;
	}

	$shah = "";
	$c = 1;

	for ( $l=1;$l<41;$l++ ) {
		$char = intval( ( ( $t / (int) $asci[$c] ) * $l ) * ( $t % DATACONST ) );
		$newchar = $char % 127;
		if ( $newchar < 32 )
			$newchar = $newchar + 32;
		$shah .= chr( $newchar );
		$c = ( $c * 2 ) % $l;
	}

	return $shah;
}

?>