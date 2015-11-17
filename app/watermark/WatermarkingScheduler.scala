package watermark

import akka.actor.ActorSystem
import com.google.inject.{Singleton, Inject}
import play.api.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait WatermarkingScheduler

/*
  Dummy scheduler, processes one watermark per second.
 */
@Singleton
class AkkaWatermarkingScheduler @Inject()(val system: ActorSystem, watermarkService: WatermarkService)
                                         (implicit ec: ExecutionContext) extends WatermarkingScheduler{

  private val logger = Logger

  logger.info("Starting scheduler.")
  system.scheduler.schedule(1 second, 1 second) {
    watermarkService.processNext()
  }

}
