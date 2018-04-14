package com.narrative

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import scala.concurrent.{ExecutionContext, Future}
import model.{Event, UserAnalytic}
import service.UserAnalyticService
import akka.http.scaladsl.server.Directives._
import model.Event.Event
import scala.util.{Failure, Success}


trait UserAnalyticRoutes {

  def userAnalyticService: UserAnalyticService


  def userRoutes(implicit executor: ExecutionContext): Route =
    pathPrefix("analytics") {
      concat(

        pathEnd {
          concat(
            get {
              parameters('timestamp.as[Long]) { (timestamp) =>


                val futureResult = userAnalyticService.getLastHourAnalyticFrom(timestamp).map {
                  result =>
                    HttpEntity(ContentTypes.`text/html(UTF-8)`, result.toString)
                }
                complete(futureResult)


              }

            },
            post {

              parameters(('timestamp.as[Long], 'user.as[Long], 'event.as[String])) { (timestamp, user, event) =>


                val f = Event.values.filter(_ != Event.NotIdentified).find(_.toString == event).map {
                  event: Event =>
                    userAnalyticService.createUserAnalytic(UserAnalytic(timestamp, user, event))
                }.getOrElse(Future.failed(new Exception(s"wrong event -$event")))


                onComplete(f) {
                  case Success(value) => complete(StatusCodes.NoContent)
                  case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
                }
              }
            }
          )
        }
      )
    }
}
