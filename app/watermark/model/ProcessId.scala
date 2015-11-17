package watermark.model

import play.api.libs.json._
import play.api.mvc.PathBindable

case class ProcessId(id: String)

object ProcessId {

  implicit def pathBindable(implicit stringBinder: PathBindable[String]) = new PathBindable[ProcessId] {

    def bind(key: String, value: String): Either[String, ProcessId] =
      stringBinder.bind(key, value).right.map(id => ProcessId(id))

    def unbind(key: String, processId: ProcessId): String =
      stringBinder.unbind(key, processId.id)

  }

  implicit def format = new Format[ProcessId] {

    override def writes(o: ProcessId): JsValue = JsString(o.id)

    override def reads(json: JsValue): JsResult[ProcessId] = {
      json match {
        case jsString: JsString => JsSuccess(ProcessId(jsString.as[String]))
        case _ => JsError()
      }
    }
  }
}