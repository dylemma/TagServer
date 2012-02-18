package code.snippet

import net.liftweb.util._

object Includes {

	case class Resource(basePath: List[String], devFile: String, minFile: String) {
		def path = "/classpath/" + basePath.mkString("/") + "/" + (Props.mode match {
			case Props.RunModes.Development => devFile
			case _ => minFile
		})
	}

	private val d3path = List("d3")

	val resources: PartialFunction[List[String], Boolean] = {
		case "d3" :: _ => true
	}

	def script(resource: Resource) = <script src={ resource.path }></script>

	def d3 = script(Resource(d3path, "d3.js", "d3.min.js"))

}