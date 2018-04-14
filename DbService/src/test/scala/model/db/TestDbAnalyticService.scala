package model.db

import com.typesafe.config.ConfigFactory
import model.{Event, UserAnalytic}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfter}
import service.{DatabaseService, UserAnalyticService}
import scala.concurrent.{Await, Future}


class TestDbAnalyticService extends AsyncFlatSpec with BeforeAndAfter {

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

    for{
      _ <- multipleCreateUserAnalytic( createUserAnalytic(234l, 1l, nbrClick, nbrImpression):_*  )
      result <- service.getLastHourAnalyticFrom(234l)
    }yield{
      assert(result.numberUniqueUsers === 1)
      assert(result.nbrClicks === nbrClick)
      assert(result.nbrImpressions === nbrImpression)
    }
  }
}