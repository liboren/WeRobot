package models

import com.google.inject.{Inject, Singleton}



//import models.tables.SlickTables

import play.api.Logger

import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}

import slick.driver.JdbcProfile
/**
  * User: liboren.
  * Date: 2016/12/19.
  * Time: 16:02.
  */

@Singleton
class ConnectTest@Inject()(
                             protected val dbConfigProvider: DatabaseConfigProvider
                           ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.driver.PostgresDriver.api._

//  private val log = Logger(this.getClass)
//
//  private[this] val advertise = SlickTables.tAdvertise
//
//
//  def createAdvertise(r:SlickTables.rAdvertise)={
//
//    db.run(advertise returning advertise.map(_.id)+=r).mapTo[Long]
//
//  }
//
//
//  def updateAdvertise(id:Long,title:String,subTitle:String,pic:String,logo:String,desc:String,
//
//                      url:String,updateTime:Long,bigTitle:String,adBgPic:String)={
//
//    db.run(advertise.filter(_.id===id).map(t=>
//
//      (t.title,t.subTitle,t.pic,t.logo,t.description,t.url,t.updateTime,t.bigTitle,t.bgPic)).update(
//
//      (title,subTitle,pic,logo,desc,url,updateTime,bigTitle,adBgPic)))
//
//  }
//
//
//  def getAdById(adId:Long)={
//
//    db.run(advertise.filter(_.id===adId).result.headOption)
//
//  }



}