package com.narrative


import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import model.{Analytic, Event, UserAnalytic}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import service.UserAnalyticService
import org.scalamock.scalatest.MockFactory
import scala.concurrent.Future

//#set-up
class UserAnalyticRoutesSpec extends WordSpec with MockFactory with Matchers with ScalaFutures with ScalatestRouteTest
    with UserAnalyticRoutes {

  val userAnalyticService = mock[UserAnalyticService]

  lazy val routes = userRoutes

  import java.time.LocalDateTime

  val REFERENCE_DATE_TIME = LocalDateTime.of(2016, 4, 1, 10, 0) //2016-04-01 at 10:00am


  //#set-up

  //#actual-test
  "UserAnalyticsRoutes" should {
    "return no users if no present (GET /users)" in {

      val ourResponse = Analytic(0, 0, 0)
      (userAnalyticService.getLastHourAnalyticFrom _).expects(1l).returning(Future.successful(ourResponse))

      Get("/analytics?timestamp=1") ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`text/html(UTF-8)`)

        // and no entries should be in the list:
        entityAs[String] should ===(ourResponse.toString)
      }
    }
    //#actual-test
  }



    "be able to add useranalytic (POST /users)" in {
      val userAnalytic = UserAnalytic(100, 1, Event.click)


      (userAnalyticService.createUserAnalytic _).expects(UserAnalytic(100l,1l, Event.click)).returning(Future.successful(1))

      // using the RequestBuilding DSL:
      val request = Post(s"/analytics?timestamp=${userAnalytic.timestamp}&user=${userAnalytic.userid}&event=${userAnalytic.event}")

      request ~> routes ~> check {
        status should ===(StatusCodes.NoContent)

        // and we know what message we're expecting back:
        entityAs[String] should ===("")
      }
    }
    //#testing-post



}
//#set-up
//#user-routes-spec
