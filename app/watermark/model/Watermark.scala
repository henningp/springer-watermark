package watermark.model

import play.api.libs.json._

case class Watermark(value: String)

object Watermark {

  implicit val format = new Format[Watermark] {

    override def writes(o: Watermark): JsValue = JsString(o.value)

    override def reads(json: JsValue): JsResult[Watermark] = {
      json match {
        case jsString: JsString => JsSuccess(Watermark(jsString.as[String]))
        case _ => JsError()
      }
    }
  }

}
