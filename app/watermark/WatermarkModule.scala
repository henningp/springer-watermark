package watermark

import com.google.inject.AbstractModule

/*
  A lot of trouble just to make sure the instance initializes, but Global is going away.
 */
class WatermarkModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[WatermarkingScheduler]).to(classOf[AkkaWatermarkingScheduler]).asEagerSingleton()
  }
}
