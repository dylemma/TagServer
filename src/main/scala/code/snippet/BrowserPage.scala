//package code.snippet
//import net.liftweb.common.Box
//import net.liftweb.sitemap.Menu
//import code.model.db._
//import code.model.File
//
//import scala.xml.{ NodeSeq, Text }
//import net.liftweb.util._
////import net.liftweb.common._
//import net.liftweb.sitemap._
////import code.lib._
//import Helpers._
////import net.liftweb._
////import net.liftweb.http._
//
//object BrowserPage {
//	//TODO: apparently this gets called more than seems necessary... caching of successful results might be in order.
//	private def verifyId(idString: String) = idString match {
//		case AsInt(id) => database.withSession { TimeHelpers.logTime("Get file by id", Files.find(id)) }
//		case _ => None
//	}
//
//	// a menu that accepts urls in the form of /b/{fileId}
//	val menu = Menu.param[File]("Browse", "Browse", path => Box(verifyId(path)), _.id.toString) / "b"
//}
//
//class BrowserPage(root: File) {
//	def render(in: NodeSeq): NodeSeq = {
//		<div>Displaying file { root.id }: { root.name }</div>
//	}
//
//	def children(in: NodeSeq): NodeSeq = database.withTransaction {
//		for { child <- root.children } yield {
//			val link = BrowserPage.menu.calcHref(child)
//			<li><a href={ link }>{ child.name }</a></li>
//		}
//	}
//}