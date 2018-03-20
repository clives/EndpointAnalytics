package model.db


import model.{Event, UserAnalytic}
import org.scalatest.{BeforeAndAfter, FlatSpec}
import service.FlywayService
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._
import slick.sql

import scala.concurrent.duration._
import scala.concurrent.Await

class UserAnalyticTableTest extends FlatSpec with BeforeAndAfter {

  var db: Database = _
  implicit var session: Session = _
  val daoType = DAO(H2Profile)
  val usersanalytic = TableQuery[daoType.UserAnalytics]

  before {
    db = Database.forConfig("testdb")
    session = db.createSession()
    cleanDb(session)
  }

  "Operations using UserAnalytic entity" should "Work" in {
    createSchema()
    val tables = Await.result(db.run(MTable.getTables), 2 seconds)
    assert(tables.size == 1)

    Await.result(db.run(usersanalytic += UserAnalytic(timestamp = 1l, userid = 1l, event = Event.click)), 2 seconds)
    Await.result(db.run(usersanalytic += UserAnalytic(timestamp = 1l, userid = 2l, event = Event.impression)), 2 seconds)

    val q = for {
      h <- usersanalytic
    } yield (h)

    val useranalyticResult = Await.result(db.run(usersanalytic.result), 2 seconds)

    assert( useranalyticResult.size == 2)
  }

  def createSchema() = {
    Await.result(db.run((usersanalytic.schema).create), 2 seconds)
  }


  def cleanDb(session: Session) = {
    val stmt = session.createStatement()
    stmt.executeUpdate("DROP ALL OBJECTS")
  }

  after {
    cleanDb(session)
    db.close

  }

}