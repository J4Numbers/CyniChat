<?php
/**
*
* pageTemplate
* Defines a page object that containing [[tags]] that can be replaced
* Author: James Holt
* Date  : febuary 2011
**/
define("TMPL_DIR", HOME_DIR."template/");

class pageTemplate{
	private $raw_html="";
	private $html = "";
	private $tags = array();
	
	public function __construct($file){
	$this->raw_html = file_get_contents(TMPL_DIR . $file);
	}
	
	public function setContent($tag,$content){
	$this->tags[$tag]=$content;
	}
	
	public function setContentFile($tag,$filename){
	$this->tags[$tag]=file_get_contents($filename);
	}
	
	public function appendContent($tag,$content){
	$this->tags[$tag] .= $content;
	}	
	
	public function sendContent(){
	echo $this->getContent();
	}
	
	public function getContent(){
	$this->html = $this->raw_html;
	foreach($this->tags as $k => $v){
	$this->html = str_replace('[[' . $k . ']]',$v,$this->html);
	}
	return $this->html;
	}
}

?>