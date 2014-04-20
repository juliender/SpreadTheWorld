package service

import play.api.Application
import securesocial.core.{Identity, UserServicePlugin, Registry, SocialUser}

import models._
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import scala.Some
import play.Logger


class PostgresUserService(application: Application) extends UserServicePlugin(application) {
  /**
   * Finds a user that maches the specified id
   *
   * @param id the user id
   * @return an optional user
   */
  def find(id: IdentityId):Option[Identity] = {
    Registry.providers.get(id.providerId) match {
      case Some(p) => {
        val app_user = User.findByIdentityId(id)
        if (app_user != null) {
          val socialUser = new SocialUser(id,
            "",
            "",
            "",
            None,
            None,
            p.authMethod
          )

          return Some(socialUser)
        } else{
          return None
        }
      }
      case _ => return None
    }
  }

  /**
   * Finds a user by email and provider id.
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation.
   *
   * @param email - the user email
   * @param providerId - the provider id
   * @return
   */
  def findByEmailAndProvider(email: String, providerId: String):Option[Identity] =
  {
    return None
  }

  /**
   * Saves the user.  This method gets called when a user logs in.
   * This is your chance to save the user information in your backing store.
   * @param user
   */
  def save(user: Identity) :Identity = {

    var app_user = models.User.findByIdentityId(user.identityId)
    var identityId = new models.IdentityId()

    //User not id DB : filling profile
    if(app_user == null) {
      app_user = new models.User()
      app_user.save()

      identityId = new models.IdentityId()
      identityId.appUser = app_user
      identityId.userId = user.identityId.userId
      identityId.providerId = user.identityId.providerId
    }else{
      identityId = models.IdentityId.findByUserIdAndProviderId(user.identityId.userId,user.identityId.providerId)
    }

    identityId.fullname = user.fullName
    identityId.lastname = user.lastName
    identityId.firstname = user.firstName
    identityId.accessToken = user.oAuth2Info.get.accessToken
    identityId.pictureUrl = user.avatarUrl.getOrElse(null)

    user.email match {
      case Some(mail) =>       identityId.email = mail
      case None => {}
    }

    identityId.save()

    return user
  }

  def link(current: Identity, to: Identity) {
    // implement me
  }

  /**
   * Saves a token.  This is needed for users that
   * are creating an account in the system instead of using one in a 3rd party system.
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param token The token to save
   */
  def save(token: Token) = {
    // implement me
  }


  /**
   * Finds a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param token the token id
   * @return
   */
  def findToken(token: String): Option[Token] = {
    return None
  }

  /**
   * Deletes a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param uuid the token id
   */
  def deleteToken(uuid: String) {
    // implement me
  }

  /**
   * Deletes all expired tokens
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   */
  def deleteExpiredTokens() {
    // implement me
  }
}