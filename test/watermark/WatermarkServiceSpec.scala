package watermark

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import watermark.model.{Watermark, ProcessId, Journal}

import scala.concurrent.Await
import scala.concurrent.duration._

class WatermarkServiceSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  val watermarkGeneratorResult = Watermark("123")

  trait WithMocks extends Scope {
    val watermarkGenerator = mock[WatermarkGenerator]
    watermarkGenerator.generate() returns watermarkGeneratorResult

    val underTest = new WatermarkService(watermarkGenerator)
  }


  "createWatermark" should {
    "return a process ID for the watermarking" in new WithMocks {
      val publication = Journal("Fancy Journal", "Author", None)

      val result = underTest.createWatermark(publication)

      result must beAnInstanceOf[ProcessId].await
    }
  }
  "checkWatermarkStatus" should {
    "return None when the watermark has not been processed yet" in new WithMocks {
      val publication = Journal("Fancy Journal", "Author", None)
      val processId = Await.result(underTest.createWatermark(publication), 1 second)

      val result = underTest.checkWatermarkStatus(processId)

      result must beNone.await
    }
    "return the watermarked publication when the watermark has been processed already" in new WithMocks {
      val publication = Journal("Fancy Journal", "Author", None)
      val processId = Await.result(underTest.createWatermark(publication), 1 second)
      underTest.processNext()

      val result = underTest.checkWatermarkStatus(processId)

      result must be_==(Some(Journal("Fancy Journal", "Author", Some(watermarkGeneratorResult)))).await
    }
  }

}
