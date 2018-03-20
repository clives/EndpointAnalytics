package model

import java.sql.Timestamp

object Event extends Enumeration {
  type Event = Value
  val click, impression, NotIdentified = Value
}
import Event._



case class UserAnalytic(timestamp: Long, userid: Long, event: Event)

object UserAnalytic{
  def applyTS(timestamp: Timestamp, userid: Long, event: Event):UserAnalytic =
    UserAnalytic( timestamp.getTime, userid, event)

  def unapply(arg: UserAnalytic): Option[(Timestamp, Long, Event)] =
    Some( new Timestamp(arg.timestamp),arg.userid, arg.event )
}

case class Analytic(numberUniqueUsers: Int, nbrClicks: Int, nbrImpressions: Int){
  override def toString()=
    s"""unique_users,${numberUniqueUsers}
       |clicks,${nbrClicks}
       |impressions,${nbrImpressions}""".stripMargin
}