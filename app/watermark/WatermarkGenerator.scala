package watermark

import java.util.UUID

import watermark.model.{Watermark, Publication}

class WatermarkGenerator {

  def generate(): Watermark = {
    //This service creatively uses a random UUID as the watermark.
    Watermark(UUID.randomUUID().toString)
  }

}
