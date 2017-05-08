package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.collection.parallel.ParSeq

/**
  * Created by Macbook on 2017/4/16.
  */
@Singleton
class MemberDao @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider
                                  ) extends HasDatabaseConfigProvider[JdbcProfile] {

  val log = LoggerFactory.getLogger(this.getClass)
  import slick.driver.MySQLDriver.api._
  private [this] val tGroupuser = SlickTables.tGroupuser

  /**
    * 增加新的关键词回复
    * @param userUnionId @开头群用户唯一id
    * @param userNickName 群成员原昵称
    * @param userDisplayName 群成员群昵称
    * @param groupId 群id
    * @return 新增的自增id
    * */
  def createrMember(userUnionId:String,userNickName:String,userDisplayName:String,groupId:Long) ={
    db.run(tGroupuser.map(i => (i.userunionid,i.usernickname,i.userdisplayname,i.groupid)).returning(tGroupuser.map(_.id)) +=
      (userUnionId,userNickName,userDisplayName,groupId)
    ).mapTo[Long]
  }
  def batchCreaterMember(infoSeq:Seq[(String,String,String,Long)]) ={
    db.run(tGroupuser.map(i => (i.userunionid,i.usernickname,i.userdisplayname,i.groupid)).forceInsertAll(infoSeq).transactionally)
  }

  /**
    * 获取某个群成员信息
    * @param memberUnionId @@开头的群唯一id
    * @param groupid 群id
    * @return Option
    * */
  def getMemberByUnionId(memberUnionId:String,groupid:Long) = db.run(
    tGroupuser.filter(m => m.userunionid === memberUnionId && m.groupid === groupid).result.headOption
  )

  def getMemberByNickName(memNickName:String,groupid:Long) = db.run(
    tGroupuser.filter(m => m.groupid === groupid && ( m.usernickname === memNickName || m.userdisplayname === memNickName)).result.headOption
  )

}
