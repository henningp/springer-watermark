package watermark

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.{FakeRequest, PlaySpecification}
import testutil.WithMockControllers
import watermark.model.{Watermark, Journal, ProcessId}

import scala.concurrent.Future

class WatermarkControllerSpec(implicit ee: ExecutionEnv) extends PlaySpecification with Mockito with Results {

  trait WithMocks extends Scope {
    val watermarkService = mock[WatermarkService]
    val underTest = new WatermarkController(watermarkService)
  }

  "POST /watermark" should {
    "return HTTP 400 when the JSON cannot be parsed" in new WithMockControllers with WithMocks {
      watermarkService.createWatermark(any) returns Future.successful(ProcessId("123"))
      val request = FakeRequest(POST, "/watermark").withJsonBody(Json.parse("{}"))

      val response = route(request).get

      status(response) must be_==(400)
    }
    "return HTTP 202 and a JSON status including the process ID" in new WithMockControllers with WithMocks {
      watermarkService.createWatermark(any) returns Future.successful(ProcessId("123456"))
      val request = FakeRequest(POST, "/watermark").withJsonBody(Json.parse("""{
                            "title": "Boo",
                            "author": "Dr. Yada",
                            "topic": "Banana banana",
                            "content": "book"
                        }"""))

      val response = route(request).get

      status(response) must be_==(202)
      contentAsJson(response) must be_==(Json.obj("status"->"OK", "processId"->"123456"))
    }
  }
  "GET /watermark/$processId" should {
    "return HTTP 204 when the watermark has not been processed yet" in new WithMockControllers with WithMocks {
      watermarkService.checkWatermarkStatus(any) returns Future.successful(None)
      val request = FakeRequest(GET, "/watermark/123456")

      val response = route(request).get

      status(response) must be_==(204)
      there was one(watermarkService).checkWatermarkStatus(ProcessId("123456"))
    }
    "return HTTP 200 and the processed publication as a JSON" in new WithMockControllers with WithMocks {
      watermarkService.checkWatermarkStatus(any) returns Future.successful(Some(
        Journal("Some Title", "Some Author", Some(Watermark("123")))))
      val request = FakeRequest(GET, "/watermark/123456")

      val response = route(request).get

      status(response) must be_==(200)
      contentAsJson(response) must be_==(Json.parse(
        """{
          |"content":"journal",
          |"title":"Some Title",
          |"author":"Some Author",
          |"watermark":"123"
          |}""".stripMargin))
      there was one(watermarkService).checkWatermarkStatus(ProcessId("123456"))
    }
  }

}