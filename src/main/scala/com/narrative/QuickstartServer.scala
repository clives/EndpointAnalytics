package com.narrative

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration.Duration
import akka.actor.{ ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import service._


object QuickstartServer extends App with UserAnalyticRoutes {

  implicit val system: ActorSystem = ActorSystem("AnalyticAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executor: ExecutionContext = system.dispatcher


  lazy val routes: Route = userRoutes

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val databaseService = new DatabaseServiceMysql(config.getConfig("analyticdb"))
  val userAnalyticService = new UserAnalyticService(databaseService)

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}
