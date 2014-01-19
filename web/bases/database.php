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
 * Interface database
 *
 * This is a little thing that allows us to define just what each
 * database class is allowed to do; it also allows us to select
 * one of potentially many database usages and to keep things almost
 * the same, bar for the actual workings of course.
 */
interface database {

	/**
	 * A function to return an array of every single player there
	 * is in this damn place. Might take a fair few while loops to
	 * get to the data inside though...
	 *
	 * @return array of all the players mapped onto their details
	 */
	public function getAllPlayers();

	/**
	 * A function to return an array of every channel there is in
	 * the database and lets us skim through all of them at our
	 * leisure.
	 *
	 * @return array of all the channels mapped onto their details
	 */
	public function getAllChannels();

	/**
	 * Given the name of the player in question, get all the data
	 * about them and give it to the user in array form.
	 *
	 * @param String $name the name of the person we're going to
	 *  dig up
	 * @return array of the player's details
	 */
	public function getOnePlayer( $name );

	/**
	 * Given the name of the channel, get the information regarding
	 * that selfsame channel and do all the nice stuff with it...
	 * such as returning it to the asker; aren't we nice?
	 *
	 * @param String $name the name of the channel we're going to
	 *  dig up information on
	 * @return array of the channel's details
	 */
	public function getOneChannel( $name );

	/**
	 * Set a player's options to something else when we give the
	 * function their name and a JSON string of their altered
	 * options.
	 *
	 * @param String $name The name of the person who is going to
	 *  have a brand new lease on life
	 * @param json $options A JSON string of the options that we're
	 *  changing
	 */
	public function setOnePlayer( $name, $options );

	/**
	 * Set a channel's options to something that we are now defining
	 * and most-probably breaking.
	 *
	 * @param String $name The name of the channel which shall be
	 *  remastered momentarily
	 * @param json $options The JSON string that represents the new
	 *  face of the channel
	 */
	public function setOneChannel( $name, $options );

} 