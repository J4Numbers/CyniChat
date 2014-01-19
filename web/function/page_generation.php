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

/**
 * Class pageTemplate
 * A class made for taking in a template and altering it dependant on the page
 * that the user is viewing at the time. Some may in fact draw similarities
 * between this page template class and one made by @tehbeard; I assure you that
 * these comparisons are completely intentional... his works for crying out loud,
 * I'm not going to change that.
 */
class pageTemplate {

	private $raw_page = "";
	private $end_page = "";
	private $tags = array();

	/**
	 * Let's get things rolling. Taking the name of the template file in
	 * the teplate file dir (so template.htm instead of templates/template.htm)
	 * we will do stuff with it!
	 *
	 * @param String $page_name : Get the contents of this file to build
	 *  the page from.
	 * @param String $dir : The root directory of files
	 * @throws RuntimeException if the file does not exist
	 */
	public function __construct( $page_name, $dir ) {
		$page = file_get_contents("$dir/templates/$page_name");
		if ( $page === false )
			throw new RuntimeException("This file does not exist");
		$this->raw_page = $page;
	}

	/**
	 * Apparently the user is picky and likes to change the page to
	 * something else... possibly to a failure page, but who are we
	 * to gamble on such frivolous things.
	 *
	 * @param String $newPage : The page that we're now searching for
	 *  instead of the previous failure
	 * @throws RuntimeException : If the new page doesn't exist either
	 */
	public function changePage( $newPage ) {
		$page = file_get_contents("../templates/$newPage");
		if ($page === false )
			throw new RuntimeException("this file does not exist");
		$this->raw_page = $page;
	}

	/**
	 * When we have a tag that we'd like to replace with a value,
	 * we call on this function to register that interest in the
	 * tags variable that we keep on hand for this exact reason.
	 *
	 * @param String $tag : The tag we're going to replace
	 * @param mixed $value : The value we're going to replace it
	 *  with
	 */
	public function setTag($tag, $value) {
		$this->tags[$tag] = $value;
	}

	/**
	 * Sometimes, the programmer likes not having too much of a
	 * block of text on one single line and they like to spread
	 * it out across multiple lines, using such methods as this
	 * one here.
	 *
	 * @param String $tag : The tag we're going to change
	 * @param mixed $value : The value we're going to append onto
	 *  the end of it.
	 */
	public function appendTag($tag, $value) {
		if ( !isset($this->tags[$tag]) )
			$this->tags[$tag] = "";

		$this->tags[$tag] .= $value;
	}

	/**
	 * A function to simply echo the page onto the screen in
	 * front of whoever is sitting before the screen.
	 */
	public function showPage() {
		echo $this->generatePage();
	}

	/**
	 * Now we have been asked to generate the page based on
	 * the collection of tags, values and raw_html that we've
	 * been provided with previously. Switch out all the tags
	 * and put the result into a new page variable, then let's
	 * return that to the user and act nice about it.
	 *
	 * @return string : Of all the tag changes that we've implemented
	 *  over the past few seconds
	 */
	public function generatePage() {
		$page = $this->raw_page;
		foreach ( $this->tags as $k => $v )
			$page = str_replace( "[[$k]]", $v, $page);
		$this->end_page = $page;
		return $this->end_page;
	}

}