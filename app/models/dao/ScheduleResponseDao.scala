package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
/**
  * User: liboren.
  * Date: 2017/4/14.
  * Time: 16:27.
  */
@Singleton
class ScheduleResponseDao @Inject()(
                                 protected val dbConfigProvider: DatabaseConfigProvider
                               ) extends HasDatabaseConfigProvider[JdbcProfile] {

  val log = LoggerFactory.getLogger(this.getClass)
  import slick.driver.MySQLDriver.api._
  private [this] val tScheduleresponse = SlickTables.tScheduleresponse

  /**
    * 增加新的定时回复
    * @param groupid 群组id
    * @param groupname 群组昵称
    * @param userid 用户id
    * @param response 自动回复内容
    * @param responsetype 内容类型，1-文本，2-图片
    * @param state 状态，0-关闭，1-开启
    * @param triggertime 触发时间，0-47，半小时递增
    * @param triggerday 二进制表示，1111100表示周一至周五触发，周末不处罚
    * @return 新增的自增id
    * */
  def createrScheduleResponse(groupid:Long,groupname:String,userid:Long,response:String,responsetype:Int,state:Int,triggertime:Int,triggerday:Int) ={
    db.run(tScheduleresponse.map(i => (i.groupid,i.groupname,i.userid,i.response,i.responsetype,i.state,i.triggertime,i.triggerday)).returning(tScheduleresponse.map(_.id)) +=
      (groupid,groupname,userid,response,responsetype,state,triggertime,triggerday)
    ).mapTo[Long]
  }

  /**
    * 获取该用户的定时回复列表
    * @param userid 用户id
    * @param groupid 群组id
    * @return 该用户的关键词回复列表
    * */
  def getScheduleResponseList(userid:Long,groupid:Long) = db.run(
    tScheduleresponse.filter(m => m.userid === userid && m.groupid === groupid).result
  )

  /**
    * 获取特定时间的开启中的定时回复任务
    * @param triggertime 触发时间，0-47，半小时递增
    * @return 该用户的关键词回复列表
    * */
  def getScheduleResponseByTriggerTime(triggertime:Int) = db.run(
    tScheduleresponse.filter(m => m.triggertime === triggertime && m.state === 1).result
  )

  /**
    * 修改定时回复信息
    * @param id 自增id
    * @param groupid 群组id
    * @param groupname 群组昵称
    * @param userid 用户id
    * @param response 回复内容
    * @param responsetype 回复类型，1-定时回复
    * @param state 状态 ，0-关闭，1-开启
    * @param triggertime 触发时间，0-47，半小时递增
    * @param triggerday 二进制表示，1111100表示周一至周五触发，周末不处罚
    * @return 更新结果
    * */
  def changeScheduleResponseList(id:Long,groupid:Long,groupname:String,userid:Long,response:String,responsetype:Int,state:Int,triggertime:Int,triggerday:Int) = {
    db.run(tScheduleresponse.filter( m => m.userid === userid && m.id === id).map(m => (m.groupid,m.groupname,m.response,m.responsetype,m.state,m.triggertime,m.triggerday))
      .update(groupid,groupname,response,responsetype,state,triggertime,triggerday))
  }

  /**
    * 删除定时回复信息
    * @param id 自增id
    * @param userid 用户id
    * @return 删除结果
    * */
  def deleteScheduleResponse(id:Long,userid:Long) = db.run(
    tScheduleresponse.filter( m => m.id === id && m.userid === userid).delete
  )

}

