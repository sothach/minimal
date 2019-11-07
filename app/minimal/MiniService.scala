package minimal

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.util.{ByteString, Timeout}
import javax.inject.{Inject, Singleton}
import play.api.libs.streams.Accumulator
import play.api.{Configuration, Environment, Logger}

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContextExecutor, Future}

@Singleton
class MiniService @Inject()(configuration: Configuration,
                            environment: Environment)
                           (implicit system: ActorSystem) {

  private implicit val ec: ExecutionContextExecutor = system.dispatcher
  val logger = Logger(this.getClass)

  val decider: Supervision.Decider = {
    case e: NumberFormatException =>
      logger.warn(s"Assertion failure: ${e.getMessage}")
      Supervision.Resume
  }
  private val materializerSettings = ActorMaterializerSettings(system).withSupervisionStrategy(decider)
  implicit val materializer: ActorMaterializer = ActorMaterializer(materializerSettings)(system)
  implicit val timeout: Timeout = Timeout(1, TimeUnit.SECONDS)

  private val maxFrameSize = configuration.getOptional[Int]("input.frame.size").getOrElse(1000)

  def dataSource(charset: String, contentType: String) = {
    val splitter = Flow[ByteString]
      .via(Framing.delimiter(ByteString(","), maxFrameSize, allowTruncation = true))
      .map(_.decodeString(charset).trim.toLong)
    Accumulator.source[ByteString].map(_.via(splitter)).map(Right.apply)
  }

  def seekMinimalValue(source: Source[Long,_]): Future[Long] =
    source.runReduce((item: Long, other: Long) => item.min(other))

}
