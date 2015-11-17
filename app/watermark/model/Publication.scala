package watermark.model

import play.api.libs.json.Format
import play.api.libs.json._
import play.api.libs.functional.syntax._
import Watermark._

trait Publication {

  def title: String
  def author: String
  def watermark: Option[Watermark]

  def withWatermark(watermark: Watermark): Publication

}

case class Book(title: String, author: String, topic: String, watermark: Option[Watermark]) extends Publication {
  override def withWatermark(watermark: Watermark): Publication = Book(title, author, topic, Some(watermark))
}

case class Journal(title: String, author: String, watermark: Option[Watermark]) extends Publication {
  override def withWatermark(watermark: Watermark): Publication = Journal(title, author, Some(watermark))
}


object Publication {

  implicit val publicationFormat: Format[Publication] = new Format[Publication] {
    override def writes(publication: Publication): JsValue = {
      publication match {
        case book: Book => bookFormat.writes(book).as[JsObject] + ("content" -> JsString("book"))
        case journal: Journal => journalFormat.writes(journal).as[JsObject] + ("content" -> JsString("journal"))
      }
    }

    override def reads(json: JsValue): JsResult[Publication] = {
      (json \ "content").toOption.map(_.as[String] match {
        case "book" => bookFormat.reads(json)
        case "journal" => journalFormat.reads(json)
      })
    }.getOrElse(JsError("Unknown publication type."))
  }

  implicit val bookFormat: Format[Book] = (
    (JsPath \ "title").format[String] and
    (JsPath \ "author").format[String] and
    (JsPath \ "topic").format[String] and
    (JsPath \ "watermark").formatNullable[Watermark]
    )(Book.apply, unlift(Book.unapply))

  implicit val journalFormat: Format[Journal] = (
    (JsPath \ "title").format[String] and
      (JsPath \ "author").format[String] and
      (JsPath \ "watermark").formatNullable[Watermark]
    )(Journal.apply, unlift(Journal.unapply))
}
