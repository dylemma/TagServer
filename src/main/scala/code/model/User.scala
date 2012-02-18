package code
package model

import net.liftweb.http._
import net.liftweb.openid._
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import org.openid4java.discovery.DiscoveryInformation
import org.openid4java.message.AuthRequest
import net.liftweb.http.js.jquery.JqJE._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js.JE.JsRaw

object MyVendor extends SimpleOpenIDVendor {
	def ext(di: DiscoveryInformation, authReq: AuthRequest): Unit = {
		import WellKnownAttributes._
		WellKnownEndpoints.findEndpoint(di) map { ep =>
			ep.makeAttributeExtension(List(Email, FullName, FirstName, LastName)) foreach { ex =>
				authReq.addExtension(ex)
			}
		}
	}

	override def createAConsumer = new OpenIDConsumer[UserType] {
		beforeAuth = Full(ext _)
	}
}

object User extends User with MetaOpenIDProtoUser[User] with LongKeyedMetaMapper[User] {
	def openIDVendor = MyVendor
	override def screenWrap = Full(<lift:surround with="default" at="content"><lift:bind/></lift:surround>)
	override def dbTableName = "users"
	override def homePage = if (loggedIn_?) "/dashboard" else "/"

	//	def makePredefOpenID(provider:String, )

	val providersMap = Map(
		"Google" -> "https://www.google.com/accounts/o8/id",
		"Yahoo" -> "http://yahoo.com/",
		"MyOpenID" -> "http://myopenid.com/",
		"Stack_Exchange" -> "https://openid.stackexchange.com/")

	private def fillInUrl(url: String) = {
		JsRaw("$('#logintext').val('" + url + "')").cmd.toJsCmd
	}

	override def loginXhtml = <form method="post" action={ S.uri }>
		<div id="providers">{
			for ((label, url) <- providersMap) yield {
				<div class={ "provider " + label } onclick={ fillInUrl(url) }>{ label }</div>
			}
		}</div>
		<input type="text" id="logintext" name={ openIDVendor.PostParamName }/>
		<user:submit/>
	</form>
}

class User extends LongKeyedMapper[User] with OpenIDProtoUser[User] {
	def getSingleton = User
}

