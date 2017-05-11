package models.dao

import javax.inject.Inject

import com.google.inject.Singleton
import models.tables.SlickTables
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import util.SecureUtil

/**
 * User: Liboren's. 
 * Date: 2016/3/9.
 * Time: 18:14.
 */
@Singleton
class AdminDao @Inject()(

                         protected val dbConfigProvider: DatabaseConfigProvider
                         ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  private[this] val tSystemuser = SlickTables.tSystemuser
  private val log = Logger(this.getClass)


  def getUserByAccount(account:String) = {
    db.run(tSystemuser.filter(_.account === account).result.headOption)
  }

  def getUserByName(username: String) = {
    db.run(tSystemuser.filter(_.username === username).result.headOption)
  }

  def findById(uid: Long) = {
    db.run(tSystemuser.filter(_.userid === uid).result.headOption)
  }

  def checkPassword(user: SlickTables.rSystemuser, pwd: String) = {
    user.password == SecureUtil.getSecurePassword(pwd,"127.0.0.1",user.createtime)
  }


  def createUser(userunionid:String,username:String,createtime:Long,account:String, password:String) = {
    db.run(tSystemuser.map(t=>(t.userunionid,t.username,t.createtime,t.account,t.password)).returning(
      tSystemuser.map(_.userid))+=(userunionid,username,createtime,account,password)).mapTo[Long]
  }

  def deleteUser(uid: Long) = db.run(
    tSystemuser.filter(_.userid === uid).delete
  )

}
