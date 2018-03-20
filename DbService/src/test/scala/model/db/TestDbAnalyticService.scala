package model.db

import com.typesafe.config.ConfigFactory
import model.{Event, UserAnalytic}
import org.scalatest.{BeforeAndAfter, FlatSpec}
import service.{DatabaseService, UserAnalyticService}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global


class TestDbAnalyticService extends FlatSpec with BeforeAndAfter {

  val config = ConfigFactory.load()
  val db: DatabaseService = new DatabaseService(config.getConfig("testdb"),  slick.jdbc.H2Profile)
  val service = new UserAnalyticService(db)



  private def multipleCreateUserAnalytic( usersAnalytics : UserAnalytic*)={
    Future.sequence(usersAnalytics.map( service.createUserAnalytic _))
  }


  private def createUserAnalytic( timestamp: Long, userid: Long, nbrClick: Int, nbrImpression: Int)={
    (1 to nbrClick).map( _ => UserAnalytic(timestamp, userid, Event.click) ) ++
      (1 to nbrImpression).map( _ => UserAnalytic(timestamp, userid, Event.impression) )
  }


  "Operations using UserAnalytic Service" should "Work" in {

    val nbrImpression=7
    val nbrClick=5

    Await.result(  multipleCreateUserAnalytic( createUserAnalytic(234l, 1l, nbrClick, nbrImpression):_*  ),  2 seconds)
    val result= Await.result(service.getLastHourAnalyticFrom(234l), 2 seconds)


    assert(result.numberUniqueUsers === 1)
    assert(result.nbrClicks === nbrClick)
    assert(result.nbrImpressions === nbrImpression)
  }
}