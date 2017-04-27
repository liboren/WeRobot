package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import common.Constants.Response._
/**
  * User: liboren.
  * Date: 2017/4/14.
  * Time: 16:27.
  */
@Singleton
class UserCookieDao @Inject()(
                                 protected val dbConfigProvider: DatabaseConfigProvider
                               ) extends HasDatabaseConfigProvider[JdbcProfile] {

  val log = LoggerFactory.getLogger(this.getClass)
  import slick.driver.MySQLDriver.api._
  private [this] val tUsercookie = SlickTables.tUsercookie


  /**
    * 增加新增cookie
    * @param userid 用户id
    * @param cookie 该用户对应的cookie
    * @return 插入结果
    * */
  def createCookie(userid:Long,cookie:String,uin:String,createtime:Long) ={
    db.run(tUsercookie.map(i => (i.userid,i.cookie,i.uin,i.createtime)).insertOrUpdate(userid,cookie,uin,createtime))
  }

  /**
    * 增加新增cookie
    * @param userid 用户id
    * @return 插入结果
    * */
  def getCookieByUserid(userid:Long) ={
    db.run(tUsercookie.filter(_.userid === userid).result.headOption)
  }


}

