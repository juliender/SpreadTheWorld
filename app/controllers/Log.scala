package controllers

import securesocial.core._
import play.api.mvc.{Action, Controller}
import scala.Some
import securesocial.core.LogoutEvent

/**
 * Created by julien on 18/04/14.
 */
object Log extends Controller {

  def logout = Action { implicit request =>

      val user = for (
        authenticator <- SecureSocial.authenticatorFromRequest;
        user <- UserService.find(authenticator.identityId)
      ) yield {
        Authenticator.delete(authenticator.id)
        user
      }

      val result = Redirect(session.get("app-url").get).discardingCookies(Authenticator.discardingCookie)
      user match {
        case Some(u) => result.withSession(Events.fire(new LogoutEvent(u)).getOrElse(session))
        case None => result
      }

  }
}
