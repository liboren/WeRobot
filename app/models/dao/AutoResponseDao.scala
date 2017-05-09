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
class AutoResponseDao @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider
                                  ) extends HasDatabaseConfigProvider[JdbcProfile] {

  val log = LoggerFactory.getLogger(this.getClass)
  import slick.driver.MySQLDriver.api._
  private [this] val tAutoresponse = SlickTables.tAutoresponse


  /**
    * 增加新的自动回复
    * @param restype 回复类型，1-文本 2-图片
    * @param response 回复内容
    * @param userid 用户id
    * @param groupnickname 群组昵称
    * @param state 状态，0-关闭，1-开启
    * @return 新增的自增id
    * */
  def createrScheduleResponse(restype:Int,response:String,userid:Long,groupnickname:String,state:Int) ={
    db.run(tAutoresponse.map(i => (i.restype,i.response,i.userid,i.groupnickname,i.state)).returning(tAutoresponse.map(_.id)) +=
      (restype,response,userid,groupnickname,state)
    ).mapTo[Long]
  }

  /**
    * 根据群昵称获取开启状态的自动回复消息
    * @param userid 用户id
    * @param groupnickname 群昵称
    * @return 该用户的关键词回复列表
    * */
  def getAutoresponseByGroupNickName(userid:Long,groupnickname:String) = db.run(
    tAutoresponse.filter(m => m.userid === userid && m.groupnickname === groupnickname && m.state === STATE_OPEN).result.headOption
  )

  /**
    * 修改自动回复信息
    * @param restype 回复类型，1-文本 2-图片
    * @param response 回复内容
    * @param userid 用户id
    * @param groupnickname 群组昵称
    * @param state 状态，0-关闭，1-开启
    * @return 更新结果
    * */
  def changeScheduleResponseList(id:Long,restype:Int,response:String,userid:Long,groupnickname:String,state:Int) = {
    db.run(tAutoresponse.filter( m => m.userid === userid && m.id === id).map(m => (m.restype,m.response,m.groupnickname,m.state))
      .update(restype,response,groupnickname,state))
  }

  /**
    * 删除自动回复信息
    * @param id 自增id
    * @param userid 用户id
    * @return 删除结果
    * */
  def deleteScheduleResponse(id:Long,userid:Long) = db.run(
    tAutoresponse.filter( m => m.id === id && m.userid === userid).delete
  )

}

