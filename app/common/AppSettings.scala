package common

import javax.inject.Inject

import org.slf4j.LoggerFactory
import play.api.{Logger, Configuration, Environment}

/**
  * User: Liboren's.
  * Date: 2016/3/9.
  * Time: 18:53.
  */

class AppSettings @Inject()(
                             environment: Environment,
                             configuration: Configuration
                           ) {
  private val log = Logger(this.getClass)
  private[this] val allConfig = configuration



//  val appConfig = allConfig.getConfig("app").get
//
//
//  val loginUrl = appConfig.getConfig("loginUrl").get
//  val wxAppid = appConfig.getConfig("wxAppid").get

}
