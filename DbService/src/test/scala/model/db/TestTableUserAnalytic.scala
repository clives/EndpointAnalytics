package model.db

import model.{Event, UserAnalytic}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfter}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._


class UserAnalyticTableTest extends AsyncFlatSpec with BeforeAndAfter {

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
    for{
      _ <- createSchema()
      tables <- db.run(MTable.getTables)
      _ <- db.run(usersanalytic += UserAnalytic(timestamp = 1l, userid = 1l, event = Event.click))
      _ <- db.run(usersanalytic += UserAnalytic(timestamp = 1l, userid = 1l, event = Event.click))
      useranalyticResult <- db.run(usersanalytic.result)
    }yield{
      assert(tables.size == 1)
      assert( useranalyticResult.size == 2)
    }
  }

  def createSchema() = {
    db.run((usersanalytic.schema).create)
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