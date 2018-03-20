package model.db

import java.sql.Timestamp

import model.Event.Event
import model.{Event, UserAnalytic}
import slick.jdbc.JdbcProfile



case class DAO(val driver: JdbcProfile) {
  import driver.api._

  implicit def eventMapper = MappedColumnType.base[Event, String](
    { event => event.toString },
    { i => Event.values.find(_==i).getOrElse(Event.NotIdentified) }
  )

  class UserAnalytics(tag: Tag) extends Table[UserAnalytic](tag, "UserAnalytic") {
    def user_id = column[Long]("user_id")

    def timestamp = column[Timestamp]("timestamp")

    def Event = column[Event]("event")

    override def * = (timestamp, user_id, Event) <> ((UserAnalytic.applyTS _).tupled, UserAnalytic.unapply)
  }

}