package watermark

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import watermark.model.{ProcessId, Publication, Watermark}

import scala.collection.mutable
import scala.concurrent.Future

/*
  The Future return types are just here to show how things would look if we had some kind of asynchronous backend,
  like a database. It also complicates the unit tests.
 */
@Singleton
class WatermarkService @Inject() (watermarkGenerator: WatermarkGenerator)  {

  private val logger = Logger

  private val processingQueue = new mutable.Queue[(ProcessId, Publication)]
  private val finishedJobs: mutable.Map[ProcessId, Publication] = mutable.Map()

  def createWatermark(publication: Publication): Future[ProcessId] = {
    val processId = ProcessId(UUID.randomUUID().toString)
    processingQueue += processId -> publication
    logger.info(s"Added ${processId.id}.")
    Future.successful(processId)
  }

  def checkWatermarkStatus(processId: ProcessId): Future[Option[Publication]] = {
    Future.successful(finishedJobs.get(processId))
  }

  def processNext(): Unit = {
    if (processingQueue.isEmpty) return

    val (processId, publication) = processingQueue.dequeue()
    finishedJobs += processId -> addWatermark(publication)
    logger.info(s"Added watermark for process ID ${processId.id}.")
  }

  private def addWatermark(publication: Publication): Publication = {
    //This service creatively uses a random UUID as the watermark.
    publication.withWatermark(watermarkGenerator.generate())
  }

}
