package controllers

import javax.inject.{Inject, Singleton}
import minimal.MiniService
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(miniService: MiniService,
                              components: ControllerComponents)
                                extends AbstractController(components) {
  implicit val ec: ExecutionContext = components.executionContext

  def minimal = Action.async(streamSource) { request =>
    miniService.seekMinimalValue(request.body)
      .map(result => Ok(result.toString))
      .recover {
        case _: NoSuchElementException =>
          BadRequest("no data provided")
        case e =>
          InternalServerError(e.getMessage)
      }
  }

  def streamSource = BodyParser { header =>
    miniService.dataSource(
      header.charset.getOrElse("UTF-8"),
      header.contentType.getOrElse("text/plain"))
  }

}
