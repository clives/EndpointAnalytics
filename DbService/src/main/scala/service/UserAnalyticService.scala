package service

import java.sql.Timestamp
import java.time.{Instant, LocalDateTime, ZoneOffset}
import model.Event.Event
import model.db.DAO
import model.{Analytic, Event, UserAnalytic}

import scala.concurrent.{ExecutionContext, Future}



class UserAnalyticService(val databaseService: DatabaseService)(implicit executionContext: ExecutionContext) {
  import databaseService._
  import databaseService.driver.api._


  implicit def eventMapper=  DAO(databaseService.driver).eventMapper


  val usersAnalytics = TableQuery[daoType.UserAnalytics]


  def getLastHourAnalyticFrom(timestamp: Long): Future[Analytic] = {


    val from = Instant.ofEpochMilli(timestamp)
      .atZone(ZoneOffset.UTC).minusHours(1).toInstant().toEpochMilli()
    val to =timestamp



    val requestDistinctUserCount = usersAnalytics.filter{
      line => line.timestamp >= new Timestamp(from) && line.timestamp <= new Timestamp(to)}.map(_.user_id).distinct.length.result

    val requestCountEvent=(event: Event) => usersAnalytics.filter{
      line => line.timestamp >= new Timestamp(from) && line.timestamp <= new Timestamp(to) && line.Event === event}.length.result


    for{
      nbrdistinctUser <- db.run( requestDistinctUserCount)
      nbrClick <- db.run( requestCountEvent(Event.click))
      nbrImpression <- db.run( requestCountEvent(Event.impression))
    }yield(Analytic(nbrdistinctUser, nbrClick, nbrImpression) )

  }


  def getUsersAnalytics(from: Long, to: Long ): Future[Seq[UserAnalytic]] = {
    db.run(usersAnalytics.filter{ line => line.timestamp >= new Timestamp(from) && line.timestamp <= new Timestamp(to)}.result)
  }

  def getUsersAnalyticsAt(timestamp: Long): Future[Seq[UserAnalytic]] = {
    db.run(usersAnalytics.filter(_.timestamp === new Timestamp(timestamp)).result)
  }


  def createUserAnalytic(userAnalytic: UserAnalytic): Future[Int] =
    db.run( usersAnalytics += userAnalytic)

}