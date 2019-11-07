package routing

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HttpVerbs.POST
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import play.api.{Application, Configuration}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class RouteErrorSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {
  val system: ActorSystem = ActorSystem.create("test-actor-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))(system)

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .loadConfig(Configuration(ConfigFactory.load("test-application.conf")))
    .build()

  "An incorrectly configured frame-size" should {
    "be reported as an internal server error" in {
      val request = FakeRequest(POST, "/api/minimal")
        .withHeaders(FakeHeaders(Map(
          "Host" -> "localhost",
          "Content-Type" -> "multipart/form-data",
          "Accept" -> "application/json").toSeq))
        .withBody(Seq("1").mkString)

      route(app, request) foreach { future =>
        val response = Await.result(future, 10 seconds)
        response.header.status mustBe INTERNAL_SERVER_ERROR
        val result = Await.result(response.body.consumeData, 2 seconds)
        result.utf8String mustBe "Read 1 bytes which is more than 0 without seeing a line terminator"
      }
    }
  }

}
