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
import scala.io.Source
import scala.language.postfixOps

class RouteSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {
  val system: ActorSystem = ActorSystem.create("test-actor-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))(system)

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .loadConfig(Configuration(ConfigFactory.load("application.conf")))
    .build()

  "data list posted to the /api/minimal endpoint" should {
    "have its minimum value calculated, even if invalid values included" in {
      val data = Seq("12","23","5","20","abc","77","125","9","50","1","13","98")
      val request = FakeRequest(POST, "/api/minimal")
        .withHeaders(FakeHeaders(Map(
          "Host" -> "localhost",
          "Content-Type" -> "text/plain",
          "Accept" -> "application/json").toSeq))
        .withBody(data.mkString(","))

      route(app, request) foreach { future =>
        val response = Await.result(future, 10 seconds)
        response.header.status mustBe OK
        response.body.contentType mustBe Some("text/plain; charset=utf-8")
        response.body.isKnownEmpty mustBe false
        val result = Await.result(response.body.consumeData, 2 seconds)
        result.utf8String mustBe "1"
      }
    }
  }

  "when an empty list is posted to the /api/minimal endpoint" should {
    "it should respond with a client error" in {
      val request = FakeRequest(POST, "/api/minimal")
        .withHeaders(FakeHeaders(Map(
          "Host" -> "localhost",
          "Content-Type" -> "text/plain",
          "Accept" -> "application/json").toSeq))
        .withBody(Seq.empty.mkString(","))

      route(app, request) foreach { future =>
        val response = Await.result(future, 10 seconds)
        response.header.status mustBe BAD_REQUEST
        val result = Await.result(response.body.consumeData, 2 seconds)
        result.utf8String mustBe "no data provided"
      }
    }
  }

  "a large data list posted to the /api/minimal endpoint" should {
    "have its minimum value calculated" in {
      val dataSource = Source.fromURL(getClass.getResource("/bigfile.dat"))
      val request = FakeRequest(POST, "/api/minimal")
        .withHeaders(FakeHeaders(Map(
          "Host" -> "localhost",
          "Content-Type" -> "multipart/form-data",
          "Accept" -> "application/json").toSeq))
        .withBody(dataSource.mkString)
      dataSource.close()

      route(app, request) foreach { future =>
        val response = Await.result(future, 10 seconds)
        response.header.status mustBe OK
        response.body.contentType mustBe Some("text/plain; charset=utf-8")
        response.body.isKnownEmpty mustBe false
        val result = Await.result(response.body.consumeData, 2 seconds)
        result.utf8String mustBe "-999968087"
      }
    }
  }

}
