package watermark

import com.google.inject.Inject
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc.{Action, _}
import watermark.model.{ProcessId, Publication}

import scala.concurrent.{ExecutionContext, Future}


class WatermarkController @Inject() (watermarkService: WatermarkService)(implicit executionContext: ExecutionContext) {

  def createWatermark() = Action.async(BodyParsers.parse.json) { request =>
    val bookResult = request.body.validate[Publication]
    bookResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"Failure", "message" -> JsError.toJson(errors))))
      },
      publication => {
        val processId = watermarkService.createWatermark(publication)
        processId.map(pId => Accepted(Json.obj("status" -> "OK", "processId" -> Json.toJson(pId))))
      }
    )
  }

  def pollWatermarkStatus(processId: ProcessId) = Action.async {
    watermarkService.checkWatermarkStatus(processId).map {
      case Some(publication) => Ok(Json.toJson(publication))
      case None => NoContent
    }
  }

}
