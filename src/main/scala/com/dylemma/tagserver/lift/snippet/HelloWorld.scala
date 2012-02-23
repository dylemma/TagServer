package com.dylemma.tagserver.lift.snippet

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.common.Full

class HelloWorld {
	def howdy: NodeSeq => NodeSeq = "#time *" #> Full(new java.util.Date().toString)
}