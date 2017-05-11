import java.net.URLEncoder
import javax.inject.{Inject, Singleton}

import models.tables.SlickTables
import akka.actor.ActorSystem
import common.Constants.SessionKey
import models.dao.AdminDao
import play.api.Logger
import play.api.mvc._

import scala.collection.concurrent.TrieMap
import scala.concurrent.{Await, Future}


/**
  * User: Taoz
  * Date: 11/30/2015
  * Time: 9:14 PM
  */
package object controllers {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  @Singleton
  class LoggingAction @Inject() extends ActionBuilder[Request] {
    val log = Logger(this.getClass)

    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      log.info(s"access log: ${request.uri}")
      block(request)
    }
  }

  class AdminRequest[A](val admin: SlickTables.rSystemuser, request: Request[A]) extends WrappedRequest[A](request)

  @Singleton
  class AdminAction @Inject()(adminDao: AdminDao) extends ActionRefiner[Request, AdminRequest] {
    private val log = Logger(this.getClass)
    val SessionTimeOut = 1 * 60 * 60 * 1000 //ms

    protected def authAdmin(request: RequestHeader): Future[Option[SlickTables.rSystemuser]] = {

      val session = request.session
      try {
        val timestamp = session.get(SessionKey.timestamp).get.toLong
        val userId = session.get(SessionKey.userId).get.toLong

        if (System.currentTimeMillis() - timestamp > SessionTimeOut) {
          log.info("Login failed for timeout")
          Future.successful(None)
        } else {
          adminDao.findById(userId)
        }
      } catch {
        case ex: Throwable =>
          log.info("Not Login Yet.")
          Future.successful(None)
      }
    }

    protected def onUnauthorized(request: RequestHeader) =
      Results.Redirect("/terra/admin/login").withNewSession

    override protected def refine[A](request: Request[A]): Future[Either[Result, AdminRequest[A]]] = {
      authAdmin(request).map {
        case Some(admin) =>
          Right(new AdminRequest(admin, request))
        case None =>
          Left(onUnauthorized(request))
      }
    }
  }


  @Singleton
  case class ActionUtils @Inject()(
    LoggingAction: LoggingAction,
    AdminAction: AdminAction,
//    SecureDao:SecureDao,
    system:ActorSystem
  ) {


  }




}
