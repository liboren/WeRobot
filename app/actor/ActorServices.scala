//package actor
//
//import akka.actor.{Actor, ActorRef, ActorSystem}
//import akka.actor.Actor.Receive
//import com.google.inject.{Inject, Singleton}
//import com.neo.sk.hermes._
//import common.AppSettings
//import play.api.Logger
//
///**
//  * User: Taoz
//  * Date: 11/7/2015
//  * Time: 9:14 PM
//  */
//
//
//@Singleton
//class ActorServices @Inject()(settings: AppSettings) extends Actor {
//
//
//  private val log = Logger(this.getClass)
//
//  val system = ActorSystem
//
//  val postServiceBridge =
//  context.actorOf(
//    Props[Master] (),
//    "postServiceBridge"
//  )
//
//  override def receive: Receive = {
//
//    case x => log.error(s"Unknown msg: $x")
//  }
//
//
//}
