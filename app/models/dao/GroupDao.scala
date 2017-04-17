package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Created by Macbook on 2017/4/16.
  */
@Singleton
class GroupDao @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider
                                  ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.driver.MySQLDriver.api._
  private [this] val tGroup = SlickTables.tGroup

  /**
    * 增加新的群信息
    * @param groupUnionId @@开头群唯一id
    * @param groupNickName 群昵称
    * @param headImgUrl 群头像地址
    * @param state 当前状态，0-未监控，1-监控中
    * @param ownerid 所属用户id
    * @param memberCount 群成员数量
    * @return 群的自增id
    * */
  def createrGroup(groupUnionId:String,groupNickName:String,headImgUrl:String,state:Int,ownerid:Long,memberCount:Int) ={
    groupIsExist(groupNickName,ownerid).flatMap{ exist =>
      if(exist.isDefined){
        deleteGroup(exist.get.groupid,exist.get.ownerid).flatMap{ res =>
          if(res > 0){
            db.run(tGroup.map(i => (i.groupunionid,i.groupnickname,i.headimgurl,i.state,i.ownerid,i.membercount)).returning(tGroup.map(_.groupid)) +=
              (groupUnionId,groupNickName,headImgUrl,state,ownerid,memberCount)
            ).mapTo[Long]
          }
          else{
            Future.successful(-1L)
          }
        }
      }
      else{
        db.run(tGroup.map(i => (i.groupunionid,i.groupnickname,i.headimgurl,i.state,i.ownerid,i.membercount)).returning(tGroup.map(_.groupid)) +=
          (groupUnionId,groupNickName,headImgUrl,state,ownerid,memberCount)
        ).mapTo[Long]
      }
    }

  }

  /**
    * 获取该用户的所有群信息
    * @param userid 所属用户id
    * @return 群列表
    * */
  def getGroupList(userid:Long) = db.run(
    tGroup.filter(_.ownerid === userid).result
  )


  /**
    * 删除群信息
    * @param groupid 群id
    * @param userid 所属用户id
    * @return 删除结果
    * */
  def deleteGroup(groupid:Long,userid:Long) = db.run(
    tGroup.filter( m => m.groupid === groupid && m.ownerid === userid).delete
  )

  /**
    * 判断群是否存在
    * @param groupNickName 群i昵称
    * @param userid 所属用户id
    * @return true or false
    * */
  def groupIsExist(groupNickName:String,userid:Long) = db.run(
    tGroup.filter(m => m.groupnickname === groupNickName && m.ownerid === userid).result.headOption
  )

  /**
    * 获取某个群信息
    * @param groupUnionId @@开头的群唯一id
    * @return Option
    * */
  def getGroupByUnionId(groupUnionId:String) = db.run(
    tGroup.filter(m => m.groupunionid === groupUnionId).result.headOption
  )
}
