import java.net.URLEncoder
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem

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

//  class CustomerRequest[A](
//    val wxUser: WxUser,
//    val customer: rUCustomer,
//    request: Request[A]) extends WrappedRequest[A](request)
//
//
//  @Singleton
//  class CustomerAction @Inject()(userDao: UserDao) extends ActionRefiner[Request, CustomerRequest] {
//    val logger = LoggerFactory.getLogger(getClass)
//    val SessionTimeOut = 24 * 60 * 60 * 1000 //ms
//
//    protected def auth[A](request: Request[A]): Future[Option[(WxUser, rUCustomer)]] = {
//      val session = request.session
//      try {
//        val openId = session.get(SessionKey.openId).get
//        val nickname = session.get(SessionKey.nickName).get
//        val headImg = session.get(SessionKey.headImg).get
//        val timestamp = session.get(SessionKey.timestamp).get.toLong
//        val userId = session.get(SessionKey.userId).get.toLong
//        val uType = session.get(SessionKey.uType).get.toLong
//        val state = session.get(SessionKey.state).get.toInt
//
//        if (System.currentTimeMillis() - timestamp > SessionTimeOut) {
//          logger.info("Login failed for timeout.")
//          Future.successful(None)
//        } else {
//          if (uType == Constants.userType.user) {
//            userDao.getUserById(userId).map{
//              case Some(customer) => Some((WxUser(openId, nickname, headImg), customer))
//              case None => None
//            }
//          } else {
//            Future.successful(None)
//          }
//        }
//      } catch {
//        case ex: Throwable =>
//          logger.info("Not Login Yet." + ex.getMessage)
//          Future.successful(None)
//      }
//    }
//
//    protected def onUnauthorized[A](request: Request[A]) = {
//      val redirectUrl = URLEncoder.encode(s"${Constants.host}${Constants.accountUrl}", "utf-8")
//      Results.Redirect(s"/terra/customer/auth/login?redirectUrl=$redirectUrl").withNewSession
//    }
//
//    override protected def refine[A](request: Request[A]): Future[Either[Result, CustomerRequest[A]]] = {
//      auth(request).map {
//        case Some((wxUser, customer)) =>
//          Right(new CustomerRequest(wxUser, customer, request))
//        case None =>
//          Left(onUnauthorized(request))
//      }
//    }
//  }
//
//
//
//
//  class AdminRequest[A](val admin: rUAdmin, request: Request[A]) extends WrappedRequest[A](request)
//
//  @Singleton
//  class StoreAdminAction @Inject()(adminDao: AdminDao) extends ActionRefiner[Request, AdminRequest] {
//    val logger = LoggerFactory.getLogger(getClass)
//    val SessionTimeOut = 24 * 60 * 60 * 1000 //ms
//
//    protected def authAdmin(request: RequestHeader): Future[Option[rUAdmin]] = {
//
//      val session = request.session
//      try {
//        val timestamp = session.get(SessionKey.timestamp).get.toLong
//        val userId = session.get(SessionKey.userId).get.toLong
//        val uType = session.get(SessionKey.uType).get.toInt
//
//        if (System.currentTimeMillis() - timestamp > SessionTimeOut) {
//          logger.info("Login failed for timeout")
//          Future.successful(None)
//        } else {
//          adminDao.findById(userId)
//        }
//      } catch {
//        case ex: Throwable =>
//          logger.info("Not Login Yet.")
//          Future.successful(None)
//      }
//    }
//
//    protected def onUnauthorized(request: RequestHeader) =
//      Results.Redirect("/terra/admin/login").withNewSession
//
//    override protected def refine[A](request: Request[A]): Future[Either[Result, AdminRequest[A]]] = {
//      authAdmin(request).map {
//        case Some(admin) =>
//          Right(new AdminRequest(admin, request))
//        case None =>
//          Left(onUnauthorized(request))
//      }
//    }
//  }
//
//
//  @Singleton
//  class SystemAdminAction() extends ActionRefiner[AdminRequest, AdminRequest] {
//    /**
//     * 系统管理员认证
//     */
//    val logger = LoggerFactory.getLogger(getClass)
//    override protected def refine[A](request: AdminRequest[A]): Future[Either[Result, AdminRequest[A]]] = {
//      Future.successful {
//        if (request.admin.userType == Constants.userType.admin) {
//          Right(request)
//        } else {
//          Left(Results.Forbidden("Only System Admin can do."))
//        }
//      }
//    }
//  }

  @Singleton
  case class ActionUtils @Inject()(
    LoggingAction: LoggingAction,
//    CustomerAction: CustomerAction,
//    StoreAdminAction: StoreAdminAction,
//    SystemAdminAction: SystemAdminAction,
//    SecureDao:SecureDao,
    system:ActorSystem
  ) {
    import scala.concurrent.duration._


    val log = Logger(this.getClass)

    private[this] val snRecords = new TrieMap[String, Long]()
//    val smsCodeRecords = new TrieMap[String,(String,String,Long, Long)]() //token -> (code,mobile,createTime,expiredTime)

    val smsCodeRecords = new TrieMap[String,(String,String,Long, Long)]() //(mobile,(code,token,createTime,expiredTime)

    val emailCodeRecords = new TrieMap[String,(String,Long,Long)]()   //  (email,(code,createTime,endTime))

    private def snLiveTimeInMillis = 3 * 60 * 1000

    private def smsCodeLiveTimeM = 1  * 60 * 60 * 1000

    private def emailCodeLiveTime = 2 * 60 * 60 * 1000

    def snCacheKey(appId: String, sn: String) = appId + "\u0001" + sn

    //lazy val 初始化在 Application 文件中
    lazy val cacheReducer = { //sn 1分钟刷新一次
      import concurrent.duration._
      system.scheduler.schedule(1 second, snLiveTimeInMillis / 3 millis) {
        val t = System.currentTimeMillis() - snLiveTimeInMillis
        val oldKeys = snRecords.filter(_._2 > t).keys
        snRecords --= oldKeys
      }

      system.scheduler.schedule(1 second, smsCodeLiveTimeM millis){ //手机验证码一小时清理
        val cur = System.currentTimeMillis()
        val oldKeys = smsCodeRecords.filter(_._2._4 < cur).keys
        smsCodeRecords --= oldKeys
      }

      system.scheduler.schedule(1 second, emailCodeLiveTime millis){
        val cur = System.currentTimeMillis()
        val oldKeys = emailCodeRecords.filter(_._2._3 < cur).keys
        emailCodeRecords --= oldKeys
      }
    }


//
//    def checkSignature(
//      appid:String,
//      sn:String,
//      timestamp:Long,
//      params: List[String],
//      signature: String
//    )(action: => EssentialAction): EssentialAction =
//      EssentialAction { requestHeader =>
//        val key = Await.result(SecureDao.getSecureKey(appid), 5 seconds)
//        if(key.isEmpty){ // appid不存在
//          log.debug(s"can not find appid=$appid and its secureKey")
//          val result = Results.Ok(ApiErrorCode.IllegalAppid)
//          Accumulator.done(result)
//        }
//        else{
//          val snKey = snCacheKey(appid, sn)
//          if (snRecords.contains(snKey)) {
//            snRecords(snKey) = System.currentTimeMillis()
//            log.debug(s"the sn=$sn for appid=$appid is expired")
//            val result = Results.Ok(ApiErrorCode.SnExisted)
//            Accumulator.done(result)
//          }
//          else {
//            val curTime = System.currentTimeMillis()
//            if(curTime - 1 * 60 * 1000 < timestamp) {
//              val secureKey = key.get
//              val expected = SecureUtil.generateSignature(params, secureKey)
//              val success = expected.equals(signature)
//              if (success) {
//                snRecords(snKey) = System.currentTimeMillis()
//                action(requestHeader)
//              }
//              else {
//                val result = Results.Ok(ApiErrorCode.AuthenticateFail)
//                Accumulator.done(result) // 'Done' means the Iteratee has completed its computations
//              }
//            }
//            else{
//              val result = Results.Ok(ApiErrorCode.InvalidTimestamp)
//              Accumulator.done(result)
//            }
//          }
//        }
//      }
  }




}
